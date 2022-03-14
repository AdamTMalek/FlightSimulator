package com.github.adamtmalek.flightsimulator.logger;

public enum LogLevel {
	ALL		(Integer.MAX_VALUE, ""),
	TRACE	(600, ""),
	DEBUG	(500, ""),
	INFO	(400, ""),
	WARN	(300, "\033[0;33m"),
	ERROR	(200, "\033[0;31m"),
	FATAL	(100, "\033[1;31m"),
	OFF		(0, "");

	public final int value;
	public final String colourCode;

	LogLevel(int v, String c) {
		this.value = v;
		this.colourCode = c;
	}
}
