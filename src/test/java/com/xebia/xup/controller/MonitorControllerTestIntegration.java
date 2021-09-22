package com.xebia.xup.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class MonitorControllerTestIntegration {

	private static final String MONITOR_API = "/api/monitors";
	private static final String input = "{\n"
	                                    + "\"name\": \"localhost-get\",\n"
	                                    + "\"uri\": \"http://localhost:8080/api/monitors/\",\n"
	                                    + "\"frequency\": 1\n"
	                                    + "}";

	@Autowired
	private MockMvc mockMvc;


	@Test
	void cannot_create_monitor_with_same_name() throws Exception {
		makePostRequest(input)
				.andExpect(status().is2xxSuccessful());

		makePostRequest(input)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$", hasKey("message")));


	}

	@Test
	void monitor_is_created() throws Exception {
		makePostRequest(input)
				.andExpect(status().is2xxSuccessful());
	}

	@Test
	void monitor_is_created_and_returned() throws Exception {
		makePostRequest(input)
				.andExpect(status().is2xxSuccessful());

		mockMvc.perform(get(MONITOR_API))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].monitorName", equalTo("localhost-get")));

		mockMvc.perform(get(MONITOR_API + "/localhost-get"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.monitorName", equalTo("localhost-get")));
	}

	@Test
	void monitor_is_not_created_for_frequencies_equal_zero() throws Exception {
		final int zeroInterval = 0;
		String zeroIntervalFreq = "{\n"
		                          + "\"name\": \"localhost-get\",\n"
		                          + "\"uri\": \"http://localhost:8080/api/monitors/\",\n"
		                          + "\"frequency\": " + zeroInterval + "\n"
		                          + "}";

		makePostRequest(zeroIntervalFreq)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$", hasKey("message")));
	}

	@Test
	void monitor_is_not_created_for_frequencies_less_than_zero() throws Exception {
		final int negFreq = -1;
		String negFreqStr = "{\n"
		                    + "\"name\": \"localhost-get\",\n"
		                    + "\"uri\": \"http://localhost:8080/api/monitors/\",\n"
		                    + "\"frequency\": " + negFreq + "\n"
		                    + "}";

		makePostRequest(negFreqStr)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$", hasKey("message")));
	}

	@Test
	void monitor_is_not_created_for_frequencies_more_than_24_hr() throws Exception {
		final int moreThan24Hr = (24 * 60) + 1;
		String moreThan24HrInput = "{\n"
		                           + "\"name\": \"localhost-get\",\n"
		                           + "\"uri\": \"http://localhost:8080/api/monitors/\",\n"
		                           + "\"frequency\": " + moreThan24Hr + "\n"
		                           + "}";

		makePostRequest(moreThan24HrInput)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$", hasKey("message")));
	}

	@Test
	void monitor_is_not_created_for_frequencies_not_multiple_of_60_when_trying_for_hour() throws Exception {
		final int non60Multiple = (2 * 60) + 1;
		String non60MultipleInput = "{\n"
		                            + "\"name\": \"localhost-get\",\n"
		                            + "\"uri\": \"http://localhost:8080/api/monitors/\",\n"
		                            + "\"frequency\": " + non60Multiple + "\n"
		                            + "}";

		makePostRequest(non60MultipleInput)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$", hasKey("message")));
	}

	private ResultActions makePostRequest(String content) throws Exception {
		return mockMvc.perform(post(MONITOR_API)
				.contentType(MediaType.APPLICATION_JSON)
				.content(content));
	}


}