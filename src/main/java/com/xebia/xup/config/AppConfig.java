package com.xebia.xup.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

@EnableScheduling
@EnableJpaRepositories("com.xebia.xup.repository")
@Configuration
public class AppConfig {

	@Value("${connect.timeout:5000}")
	private int connectTimeout;

	@Value("${connect.request.timeout:5000}")
	private int connectRequestTimeout;

	@Value("${read.timeout:5000}")
	private int readTimeout;

	@Bean
	public TaskScheduler taskScheduler() {
		final ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setThreadNamePrefix("XUP-URL-MONITOR-");
		return threadPoolTaskScheduler;
	}

	@Bean
	public RestTemplate customRestTemplate() {
		HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		httpRequestFactory.setConnectionRequestTimeout(connectRequestTimeout);
		httpRequestFactory.setConnectTimeout(connectTimeout);
		httpRequestFactory.setReadTimeout(readTimeout);

		return new RestTemplate(httpRequestFactory);
	}

	@Bean
	public Module javaTimeModule() {
		return new JavaTimeModule();
	}

	@Bean
	protected Module hibernateModule() {
		return new Hibernate5Module();
	}

}

