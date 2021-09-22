package com.xebia.xup.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import com.xebia.xup.exchanges.CreateMonitorRequest;
import com.xebia.xup.models.URLPollTask;
import com.xebia.xup.models.XupURLMonitor;
import com.xebia.xup.repository.PollRepository;
import com.xebia.xup.repository.URLMonitorRepository;
import com.xebia.xup.strategies.FrequencyParseStrategy;
import com.xebia.xup.strategies.MinuteBasedFrequencyParseStrategy;
import java.time.Duration;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.TaskScheduler;

@ExtendWith(MockitoExtension.class)
class MonitorServiceTest {
	final String name = "mock-task";
	private final static String URI = "http://www.example.com";
	private final TaskScheduler taskScheduler = mock(TaskScheduler.class);
	private final PollRepository pollRepository = mock(PollRepository.class);
	private final URLPollingService URLPollingService = mock(URLPollingService.class);
	private final URLMonitorRepository urlMonitorRepository = mock(URLMonitorRepository.class);
	private final FrequencyParseStrategy frequencyParseStrategy = new MinuteBasedFrequencyParseStrategy();
	private final MonitorService monitorService = new MonitorServiceImpl(
			taskScheduler, pollRepository, URLPollingService, urlMonitorRepository, frequencyParseStrategy
	);

	@Test
	void createNewMonitor_creates_monitor_and_submits_to_task_scheduler() {
		monitorService.createNewMonitor(new CreateMonitorRequest(name, URI, 59));

		when(urlMonitorRepository.findById(any(String.class)))
				.thenReturn(Optional.empty());

		ArgumentCaptor<Runnable> runnable = ArgumentCaptor.forClass(Runnable.class);
		ArgumentCaptor<Duration> duration = ArgumentCaptor.forClass(Duration.class);

		verify(taskScheduler, times(1))
				.scheduleAtFixedRate(runnable.capture(), duration.capture());

		final URLPollTask target = (URLPollTask) runnable.getValue();

		assertEquals(URLPollingService, target.getUrlPollingService());
		assertEquals(name, target.getMonitor().getMonitorName());
		assertEquals(59, target.getMonitor().getDelay().toMinutes());
	}

	@Test
	void task_scheduler_not_called_when_submitting_duplicate_tasks() {
		when(urlMonitorRepository.findById(any(String.class)))
				.thenReturn(Optional.empty())
				.thenReturn(Optional.of(mock(XupURLMonitor.class)));

		monitorService.createNewMonitor(new CreateMonitorRequest(name, URI, 59));
		assertThrows(IllegalArgumentException.class,
				() -> monitorService.createNewMonitor(new CreateMonitorRequest(name, URI, 59)));

		verify(taskScheduler, Mockito.atMost(1))
				.scheduleAtFixedRate(any(Runnable.class), any(Duration.class));
	}

	@Test
	void throws_exception_when_task_with_same_name_is_present() {
		when(urlMonitorRepository.findById(any(String.class)))
				.thenReturn(Optional.of(mock(XupURLMonitor.class)));

		assertThrows(IllegalArgumentException.class,
				() -> monitorService.createNewMonitor(new CreateMonitorRequest(name, URI, 59)));
	}


	@Test
	void createNewMonitor_creates_monitor() {
		monitorService.createNewMonitor(new CreateMonitorRequest(name, URI, 59));

		when(urlMonitorRepository.findById(any(String.class)))
				.thenReturn(Optional.empty());

		ArgumentCaptor<XupURLMonitor> monitor = ArgumentCaptor.forClass(XupURLMonitor.class);

		verify(urlMonitorRepository).save(monitor.capture());

		final XupURLMonitor value = monitor.getValue();

		assertEquals(name, value.getMonitorName());
		assertEquals(59, value.getDelay().toMinutes());
		assertEquals(URI, value.getUri());
	}

}