package com.xebia.xup.exchanges;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateMonitorRequest {

	@Schema(description = "A Unique name for the Monitor Request")
	private String name;

	@Schema(description = "URL to be monitored")
	@URL
	private String uri;

	@Schema(description = "Frequency should be in minutes. "
	                      + "We choose a minute interval if we want checks to be performed at intervals less than an hour. "
	                      + "Any check more than one hour needs to be in multiples of 60. "
	                      + "For example, frequency can have a value between 1 minute to 59 minutes. "
	                      + "Beyond that it will be in hours like 60 minutes, 120 minutes maximum till 1440 minutes."
	                      + "Values like 61 minutes 100 minutes will not be accepted")
	private long frequency;

}
