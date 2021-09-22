package com.xebia.xup.controller;

import com.xebia.xup.exchanges.CreateMonitorRequest;
import com.xebia.xup.exchanges.MonitorResponse;
import com.xebia.xup.service.MonitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "XUP", description = "Website Uptime Monitoring Tool")
@RequiredArgsConstructor
@Log4j2
@RestController
@RequestMapping("/api")
public class MonitorController {

	private final MonitorService monitorService;

	@Operation(summary = "Register new checks for a website", description = "Register new checks for a website")
	@ApiResponse(responseCode = "201", description = "Check was created")
	@PostMapping(value = "/monitors")
	@ResponseStatus(HttpStatus.CREATED)
	public void createMonitor(@RequestBody @Valid CreateMonitorRequest monitorRequest) {

		monitorService.createNewMonitor(monitorRequest);
	}

	@Operation(summary = "Get All Checks", description = "Get All Checks")
	@ApiResponse(responseCode = "200", description = "Checks were found")
	@GetMapping(value = "/monitors")
	public List<MonitorResponse> getMonitors() {

		return monitorService.getAllMonitors();
	}

	@Operation(summary = "Get Check by name", description = "Get Check by name")
	@ApiResponse(responseCode = "200", description = "Checks was found")
	@ApiResponse(responseCode = "400", description = "Check was not found")
	@GetMapping(value = "/monitors/{monitorName}")
	public MonitorResponse getMonitor(@PathVariable String monitorName) {

		log.info("Get Details for {}", monitorName);

		return monitorService.getMonitor(monitorName);
	}

}
