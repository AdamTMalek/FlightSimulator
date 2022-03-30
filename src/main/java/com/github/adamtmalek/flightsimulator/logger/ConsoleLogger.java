package com.github.adamtmalek.flightsimulator.logger;

class ConsoleLogger extends Logger {
	protected ConsoleLogger(LogLevel level) {
		super(level);
	}

	void log(LogLevel level, String msg) {
		System.out.println(level.colourCode + msg + "\033[0m");
		// Arrays.toString(Thread.currentThread().getStackTrace());
	}
}
