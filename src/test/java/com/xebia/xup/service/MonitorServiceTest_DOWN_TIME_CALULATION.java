package com.xebia.xup.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;


import com.xebia.xup.models.Poll;
import com.xebia.xup.models.Status;
import com.xebia.xup.models.XupURLMonitor;
import com.xebia.xup.repository.PollRepository;
import com.xebia.xup.repository.URLMonitorRepository;
import com.xebia.xup.strategies.FrequencyParseStrategy;
import com.xebia.xup.strategies.MinuteBasedFrequencyParseStrategy;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.scheduling.TaskScheduler;

@SuppressWarnings("PointlessArithmeticExpression")
@DataJpaTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase
class MonitorServiceTest_DOWN_TIME_CALULATION {

	public static final String URI = "http://www.example.com";
	private static final int DELTA = 1;
	private static final int SECONDS = 60;
	private static final int interval = 10;
	private static final long LATENCY = 100L;
	private final TaskScheduler taskScheduler = mock(TaskScheduler.class);
	private final URLPollingService URLPollingService = mock(URLPollingService.class);
	private final FrequencyParseStrategy frequencyParseStrategy = new MinuteBasedFrequencyParseStrategy();
	@Autowired
	private PollRepository pollRepository;
	@Autowired
	private URLMonitorRepository urlMonitorRepository;

	private MonitorServiceImpl monitorService;
	private XupURLMonitor urlMonitor;
	private LocalDateTime currentTime;

	@BeforeEach
	void setup() {
		monitorService = new MonitorServiceImpl(
				taskScheduler, pollRepository,
				URLPollingService, urlMonitorRepository, frequencyParseStrategy
		);
		final XupURLMonitor monitor = new XupURLMonitor(("mock-task"), URI, Duration.of(interval, ChronoUnit.MINUTES));
		urlMonitor = urlMonitorRepository.save(monitor);
		currentTime = LocalDateTime.now(Clock.systemDefaultZone());
	}


	@Test
	void status_time_calculator_service_never_up() {
		for (int i = 0; i < 3; i++) {
			final LocalDateTime time = LocalDateTime.now(Clock.systemDefaultZone()).plus(i * interval, ChronoUnit.MINUTES);
			pollRepository.save(new Poll(Status.DOWN, time, LATENCY, urlMonitor));
		}

		assertEquals(2 * interval * SECONDS, monitorService.statusTimeCalculator(urlMonitor.getMonitorName()), DELTA);
	}

	@Test
	void status_time_calculator_service_only_down() {
		pollRepository.save(new Poll(Status.DOWN, currentTime, LATENCY, urlMonitor));

		assertEquals(0 * interval * SECONDS, monitorService.statusTimeCalculator(urlMonitor.getMonitorName()));
	}

	@Test
	void status_time_calculator_service_starts_down_and_reamins_down_for_multiple_time_frames() {
		pollRepository.save(new Poll(Status.DOWN, currentTime, LATENCY, urlMonitor));
		pollRepository.save(new Poll(Status.DOWN, currentTime.plus(interval, ChronoUnit.MINUTES), LATENCY, urlMonitor));

		assertEquals(1 * interval * SECONDS, monitorService.statusTimeCalculator(urlMonitor.getMonitorName()), DELTA);
	}

	@Test
	void status_time_calculator_service_starts_as_down_goes_up_then_comes_down_across_multiple_time_frames() {
		for (int i = 0; i < 3; i++) {
			final LocalDateTime time = currentTime.plus(i * interval, ChronoUnit.MINUTES);
			pollRepository.save(new Poll(Status.DOWN, time, LATENCY, urlMonitor));
		}
		currentTime = currentTime.plus(3 * interval, ChronoUnit.MINUTES);
		for (int i = 0; i < 3; i++) {
			final LocalDateTime time = currentTime.plus(i * interval, ChronoUnit.MINUTES);
			pollRepository.save(new Poll(Status.UP, time, LATENCY, urlMonitor));
		}
		currentTime = currentTime.plus(3 * interval, ChronoUnit.MINUTES);
		for (int i = 0; i < 3; i++) {
			final LocalDateTime time = currentTime.plus(i * interval, ChronoUnit.MINUTES);
			pollRepository.save(new Poll(Status.DOWN, time, LATENCY, urlMonitor));
		}

		assertEquals(2 * interval * SECONDS, monitorService.statusTimeCalculator(urlMonitor.getMonitorName()), DELTA);
	}

	@Test
	void status_time_calculator_service_goes_down_then_up_then_down() {
		pollRepository.save(new Poll(Status.DOWN, currentTime, LATENCY, urlMonitor));
		pollRepository.save(new Poll(Status.UP, currentTime.plus(interval, ChronoUnit.MINUTES), LATENCY, urlMonitor));
		pollRepository.save(new Poll(Status.DOWN, currentTime.plus(2 * interval, ChronoUnit.MINUTES), LATENCY, urlMonitor));

		assertEquals(0 * interval * SECONDS, monitorService.statusTimeCalculator(urlMonitor.getMonitorName()));
	}

	@Test
	void status_time_calculator_service_starts_as_up_but_goes_down_after() {
		pollRepository.save(new Poll(Status.UP, currentTime, LATENCY, urlMonitor));
		pollRepository.save(new Poll(Status.DOWN, currentTime.plus(interval, ChronoUnit.MINUTES), LATENCY, urlMonitor));

		assertEquals(0 * interval * SECONDS, monitorService.statusTimeCalculator(urlMonitor.getMonitorName()));
	}

	@Test
	void status_time_calculator_service_starts_as_up_but_goes_down_after_for_multiple_time_frames() {
		pollRepository.save(new Poll(Status.UP, currentTime, LATENCY, urlMonitor));
		pollRepository.save(new Poll(Status.DOWN, currentTime.plus(interval, ChronoUnit.MINUTES), LATENCY, urlMonitor));
		pollRepository.save(new Poll(Status.DOWN, currentTime.plus(2 * interval, ChronoUnit.MINUTES), LATENCY, urlMonitor));

		assertEquals(1 * interval * SECONDS, monitorService.statusTimeCalculator(urlMonitor.getMonitorName()), DELTA);
	}


}