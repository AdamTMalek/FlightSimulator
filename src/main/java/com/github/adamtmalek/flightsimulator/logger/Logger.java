package com.github.adamtmalek.flightsimulator.logger;

import org.jetbrains.annotations.NotNull;

public class Logger {
	private static final Logger logger = new Logger();

	private Logger() {
		this.loggerLevel = LogLevel.ALL;
	}

	public static Logger getInstance() {
		return logger;
	}

	private LogLevel loggerLevel;

	public void setLevel(LogLevel level) {
		loggerLevel = level;
	}

	public LogLevel getLevel() {
		return loggerLevel;
	}

	public void log(@NotNull LogLevel level, String msg) {
		if (level.value < this.loggerLevel.value) {return;}
		// DO STUFF
		System.out.println(msg);
	}

	public void severe(String msg) {
		logger.log(LogLevel.SEVERE, msg);
	}

	public void warning(String msg) {
		logger.log(LogLevel.WARNING, msg);
	}

	public void info(String msg) {
		logger.log(LogLevel.INFO, msg);
	}

	public void config(String msg) {
		logger.log(LogLevel.CONFIG, msg);
	}

	public void fine(String msg) {
		logger.log(LogLevel.FINE, msg);
	}

	public void finer(String msg) {
		logger.log(LogLevel.FINER, msg);
	}

	public void finest(String msg) {
		logger.log(LogLevel.FINEST, msg);
	}
}
