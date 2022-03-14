package com.github.adamtmalek.flightsimulator.logger;

public enum LogLevel {
	ALL		(Integer.MAX_VALUE),
	TRACE	(600),
	DEBUG	(500),
	INFO	(400),
	WARN	(300),
	ERROR	(200),
	FATAL	(100),
	OFF		(0);

	public final int value;

	LogLevel(int v) {
		this.value = v;
	}
}
