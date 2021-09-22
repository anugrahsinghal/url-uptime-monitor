package com.xebia.xup.service;

import com.xebia.xup.models.Poll;
import com.xebia.xup.models.Status;
import com.xebia.xup.models.XupURLMonitor;
import com.xebia.xup.repository.PollRepository;
import com.xebia.xup.utils.URLPingUtility;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
public class URLPollingServiceImpl implements URLPollingService {

	private final URLPingUtility pingUtility;
	private final PollRepository pollRepository;

	@Override
	public void pollForMonitor(XupURLMonitor urlMonitor) {
		log.info("Polling for task {}", urlMonitor.getMonitorName());

		Instant start = Instant.now();
		Status status = pingUtility.pingURL(urlMonitor.getUri());
		Instant finish = Instant.now();
		long timeElapsed = ChronoUnit.MILLIS.between(start, finish);

		pollRepository.save(
				new Poll(status, LocalDateTime.now(Clock.systemDefaultZone()),
						timeElapsed, urlMonitor)
		);
	}

}
