package com.github.adamtmalek.flightsimulator.logger;

class ConsoleLogger extends Logger {
	static final ConsoleLogger singleton = new ConsoleLogger(LogLevel.INFO);

	ConsoleLogger(LogLevel level) {
		loggerLevel = level;
	}

	static ConsoleLogger getInstance() {
		return singleton;
	}

	void log(LogLevel level, String msg) {
		if (level.value > loggerLevel.value) {return;}
		System.out.println(level.colourCode + msg + "\033[0m");
		// Arrays.toString(Thread.currentThread().getStackTrace());
	}
}
