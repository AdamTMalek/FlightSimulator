package com.github.adamtmalek.flightsimulator.logger;

public abstract class Logger {
	abstract void log(LogLevel level, String msg);

	LogLevel loggerLevel;

	public void trace(String msg) {
		this.log(LogLevel.TRACE, msg);
	}

	public void debug(String msg) {
		this.log(LogLevel.DEBUG, msg);
	}

	public void info(String msg) {
		this.log(LogLevel.INFO, msg);
	}

	public void warn(String msg) {
		this.log(LogLevel.WARN, msg);
	}

	public void error(String msg) {
		this.log(LogLevel.ERROR, msg);
	}

	public void fatal(String msg) {
		this.log(LogLevel.FATAL, msg);
	}
}
