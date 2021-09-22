package com.xebia.xup.models;

import com.xebia.xup.service.URLPollingService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class URLPollTask implements Runnable {

	private final XupURLMonitor monitor;
	private final URLPollingService urlPollingService;

	@Override
	public void run() {
		urlPollingService.pollForMonitor(monitor);
	}

}
