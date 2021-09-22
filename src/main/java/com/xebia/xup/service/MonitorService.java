package com.xebia.xup.service;


import com.xebia.xup.exchanges.CreateMonitorRequest;
import com.xebia.xup.exchanges.MonitorResponse;
import java.util.List;

public interface MonitorService {

	void createNewMonitor(CreateMonitorRequest monitorRequest);

	List<MonitorResponse> getAllMonitors();

	MonitorResponse getMonitor(String monitorName);

	default void deleteMonitor(String monitorName) {
		throw new IllegalStateException("This Feature is Yet To be Implemented");
	}

	default void updateMonitor(CreateMonitorRequest monitorRequest) {
		throw new IllegalStateException("This Feature is Yet To be Implemented");
	}

}
