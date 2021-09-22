package com.xebia.xup.strategies;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Component;

@Component
public class MinuteBasedFrequencyParseStrategy implements FrequencyParseStrategy {

	@Override
	public Duration getDuration(long frequencyInMinutes) {
		if (frequencyInMinutes <= 0 || frequencyInMinutes > 24 * 60) {
			throw new IllegalArgumentException("Frequency cannot be less than 0 minutes and cannot be more than 24 hours");
		}
		if (frequencyInMinutes >= 60 && frequencyInMinutes % 60 != 0) {
			throw new IllegalArgumentException("Not A Valid Duration, Duration Should be 0-59 minutes or in multiples of 60");
		}

		return Duration.of(frequencyInMinutes, ChronoUnit.MINUTES);
	}

}
