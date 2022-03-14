package com.github.adamtmalek.flightsimulator.logger;

public enum LogLevel {
	OFF		(Integer.MAX_VALUE),
	SEVERE	(1000),
	WARNING	(900),
	INFO	(800),
	CONFIG	(700),
	FINE	(500),
	FINER	(400),
	FINEST	(300),
	ALL		(0);

	public final int value;

	LogLevel(int v) {
		this.value = v;
	}
}
