package com.xebia.xup.utils;

import com.xebia.xup.models.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Log4j2
@RequiredArgsConstructor
@Component
public class URLPingUtility {

	private final RestTemplate restTemplate;

	public Status pingURL(String url) {
		// help with certificate errors
		if (url.startsWith("https")) {
			url = url.replaceFirst("https", "http");
		}
		try {
			final ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
			log.info("url {} => [{}]", url, response.getStatusCode());
			// if we are redirected then we will get 3xx so need to check for that also
			// since we replaced https with http
			if (response.getStatusCode().is2xxSuccessful() || response.getStatusCode().is3xxRedirection()) {
				return Status.UP;
			}
		} catch (RestClientException e) {
			log.error("RestClientException", e);
			return Status.DOWN;
		}

		return Status.DOWN;
	}

}
