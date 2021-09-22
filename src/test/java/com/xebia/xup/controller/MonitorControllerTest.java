package com.xebia.xup.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import com.xebia.xup.exchanges.CreateMonitorRequest;
import com.xebia.xup.service.MonitorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(MonitorController.class)
class MonitorControllerTest {

	private static final String MONITOR_API = "/api/monitors";

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MonitorService monitorService;

	@Test
	void createMonitor_does_not_accept_ill_formed_uri() throws Exception {
		String input = "{\n"
		               + "\"name\": \"localhost-get\",\n"
		               + "\"uri\": \"INVALID_URI\",\n"
		               + "\"frequency\": 1\n"
		               + "}";

		makePostRequest(input)
				.andExpect(status().is4xxClientError());
	}

	@Test
	void createMonitor_accept_well_formed_uri() throws Exception {

		doNothing()
				.when(monitorService).createNewMonitor(any(CreateMonitorRequest.class));

		String input = "{\n"
		               + "\"name\": \"localhost-get\",\n"
		               + "\"uri\": \"http://localhost:8080/api/monitors/\",\n"
		               + "\"frequency\": 1\n"
		               + "}";

		makePostRequest(input)
				.andExpect(status().is2xxSuccessful());

	}

	private ResultActions makePostRequest(String content) throws Exception {
		return mockMvc.perform(post(MONITOR_API)
				.contentType(MediaType.APPLICATION_JSON)
				.content(content));
	}


}