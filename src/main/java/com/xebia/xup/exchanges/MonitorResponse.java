package com.xebia.xup.exchanges;

import com.xebia.xup.models.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class MonitorResponse {

	@Schema(description = "A Unique name for the Monitor Request")
	private String monitorName;

	private String uri;

	@Schema(description = "Average response time of the website")
	private final Double averageResponseTime;

	private final Status status;

	@Schema(description = "(UP/DOWN)TIME according to the status")
	private final long time;

}
