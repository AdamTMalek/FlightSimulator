package com.github.adamtmalek.flightsimulator.logger;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public abstract class Logger {
	abstract void log(LogLevel level, String msg);

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

	protected LogLevel loggerLevel;

	private final static Logger singleton = new LoggerImplementation(LogLevel.ALL);

	public static Logger getInstance() {
		return singleton;
	}

	protected Logger(LogLevel logLevel) {
		this.loggerLevel = logLevel;
	}

	private static class LoggerImplementation extends Logger {
		private List<? extends Logger> loggers;

		protected LoggerImplementation(LogLevel logLevel) {
			// instantiate loggers here from config
			super(logLevel);
			loggers = Arrays.asList(
					new ConsoleLogger(LogLevel.WARN),
					new FileLogger(LogLevel.ALL,
							"src/main/java/com/github/adamtmalek/flightsimulator/logger/logs/"
					)
			);
		}

		protected void log(@NotNull LogLevel level, @NotNull String message) {
			String formattedMsg = String.format(
					"[%s]: %s",
					level.name(),
					message
			);

			loggers.stream()
					.filter(logger -> shouldLog(logger, level))
					.forEach(logger -> logger.log(level, message));
		}

		private static boolean shouldLog(@NotNull Logger logger, @NotNull LogLevel level) {
			return logger.loggerLevel.value >= level.value;
		}
	}
}
