package com.xebia.xup.strategies;

import java.time.Duration;

public interface FrequencyParseStrategy {

	Duration getDuration(long frequencyInMinutes);

}