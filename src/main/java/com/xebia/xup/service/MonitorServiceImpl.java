package com.xebia.xup.service;

import com.xebia.xup.exchanges.CreateMonitorRequest;
import com.xebia.xup.exchanges.MonitorResponse;
import com.xebia.xup.models.Poll;
import com.xebia.xup.models.Status;
import com.xebia.xup.models.URLPollTask;
import com.xebia.xup.models.XupURLMonitor;
import com.xebia.xup.repository.PollRepository;
import com.xebia.xup.repository.URLMonitorRepository;
import com.xebia.xup.strategies.FrequencyParseStrategy;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Log4j2
@Service
public class MonitorServiceImpl implements MonitorService {

	private static final String TIME_COLUMN = "time";
	private static final PageRequest SINGLE_RECORD_TIME_ASC_SORT = PageRequest.of(0, 1, Sort.Direction.ASC, TIME_COLUMN);
	private static final PageRequest SINGLE_RECORD_TIME_DESC_SORT = PageRequest.of(0, 1, Sort.Direction.DESC, TIME_COLUMN);

	private final TaskScheduler scheduler;
	private final PollRepository pollRepository;
	private final URLPollingService urlPollingService;
	private final URLMonitorRepository urlMonitorRepository;
	private final FrequencyParseStrategy frequencyParseStrategy;

	@Override
	public void createNewMonitor(@NonNull CreateMonitorRequest monitorRequest) {

		Optional<XupURLMonitor> optMonitor = urlMonitorRepository.findById(monitorRequest.getName());

		if (optMonitor.isPresent()) {
			throw new IllegalArgumentException("Task With Same Name already exists");
		}

		final XupURLMonitor xupURLMonitor = new XupURLMonitor(
				monitorRequest.getName(),
				monitorRequest.getUri(),
				frequencyParseStrategy.getDuration(monitorRequest.getFrequency())
		);

		urlMonitorRepository.save(xupURLMonitor);

		startMonitor(xupURLMonitor);
	}

	private void startMonitor(@NonNull XupURLMonitor xupURLMonitor) {

		final Runnable externalServiceURLPollTask = new URLPollTask(xupURLMonitor, urlPollingService);
		// TODO save the scheduledFuture so that it can be cancelled
		//noinspection unused
		final ScheduledFuture<?> scheduledFuture = scheduler.scheduleAtFixedRate(externalServiceURLPollTask, xupURLMonitor.getDelay());
	}

	@Override
	public List<MonitorResponse> getAllMonitors() {
		final Iterable<XupURLMonitor> allMonitors = urlMonitorRepository.findAll();

		return StreamSupport.stream(allMonitors.spliterator(), false)
				.map(this::convertToMonitorResponse)
				.collect(Collectors.toList());
	}

	@Override
	public MonitorResponse getMonitor(@NonNull String monitorName) {
		Optional<XupURLMonitor> monitorOpt = urlMonitorRepository.findById(monitorName);

		return monitorOpt
				.map(this::convertToMonitorResponse)
				.orElseThrow(() -> new IllegalArgumentException(monitorName + " does not exist"));

	}

	private MonitorResponse convertToMonitorResponse(XupURLMonitor monitor) {
		return new MonitorResponse(
				monitor.getMonitorName(),
				monitor.getUri(),
				pollRepository.findAverageLatency(monitor.getMonitorName()),
				getLatestStatus(monitor.getMonitorName()),
				statusTimeCalculator(monitor.getMonitorName())
		);
	}

	private Status getLatestStatus(String monitorName) {
		final List<Poll> lastPoll = pollRepository.findAllByMonitor_MonitorName(monitorName, SINGLE_RECORD_TIME_DESC_SORT);
		if (lastPoll.isEmpty()) {
			// no polls done
			return Status.DOWN;
		}
		return lastPoll.get(0).getStatus();
	}

	public long statusTimeCalculator(String monitorName) {
		final List<Poll> lastPoll = pollRepository.findAllByMonitor_MonitorName(monitorName, SINGLE_RECORD_TIME_DESC_SORT);
		if (lastPoll.isEmpty()) {
			// no polls done
			return 0;
		}
		final Poll latestPoll = lastPoll.get(0);
		log.debug("latestPoll [{}]", latestPoll);

		return timeCalculation(monitorName, latestPoll);
	}

	/**
	 * @param monitorName name of the monitor
	 * @param latestPoll  latest poll for the monitor
	 * @return the time that the monitor has been in the status of the latest poll
	 */
	private long timeCalculation(String monitorName, Poll latestPoll) {
		// get last time with reverse status
		final List<Poll> lastReversedStatusTime = pollRepository.findMonitorByNameAndStatus(
				monitorName, reverseStatus(latestPoll.getStatus()), SINGLE_RECORD_TIME_DESC_SORT);
		// monitor was never down
		if (lastReversedStatusTime.isEmpty()) {
			return monitorInSameStatus(monitorName, latestPoll);
		} else {
			return monitorDirectionReversed(monitorName, latestPoll, lastReversedStatusTime);
		}
	}

	private Status reverseStatus(Status given) {
		if (given == Status.UP) {
			return Status.DOWN;
		} else {
			return Status.UP;
		}
	}

	private long monitorDirectionReversed(String monitorName, Poll latestPoll, List<Poll> lastReversedStatusTime) {
		log.debug("lastReversedStatusTime [{}]", lastReversedStatusTime);

		final Poll lastReversedStatusTimePoll = lastReversedStatusTime.get(0);

		final List<Poll> firstPollAfterReverseStatus = pollRepository.findMonitorByNameAndTimeAfterAndStatus(
				monitorName, lastReversedStatusTimePoll.getTime(), latestPoll.getStatus(), SINGLE_RECORD_TIME_ASC_SORT);

		final Poll theFirstPollAfterReverseStatus = firstPollAfterReverseStatus.get(0);
		log.debug("latestPoll [{}]", latestPoll.getTime());
		log.debug("firstTimeWithCurrentStatus [{}]", theFirstPollAfterReverseStatus.getTime());
		final long timeDifference = ChronoUnit.SECONDS.between(
				theFirstPollAfterReverseStatus.getTime(),
				latestPoll.getTime()
		);

		log.debug("timeDifference [{}]", timeDifference);

		return timeDifference;
	}

	private long monitorInSameStatus(String monitorName, Poll latestPoll) {
		// find first ever uptime
		final List<Poll> firstTimeWithCurrentStatus = pollRepository.findMonitorByNameAndStatus(
				monitorName, latestPoll.getStatus(), SINGLE_RECORD_TIME_ASC_SORT);

		log.debug("latestPoll [{}]", latestPoll.getTime());
		log.debug("firstTimeWithCurrentStatus [{}]", firstTimeWithCurrentStatus.get(0).getTime());
		long timeDifference = ChronoUnit.SECONDS.between(
				firstTimeWithCurrentStatus.get(0).getTime(),
				latestPoll.getTime()
		);
		log.debug("timeDifference [{}]", timeDifference);
		return timeDifference;
	}

}
