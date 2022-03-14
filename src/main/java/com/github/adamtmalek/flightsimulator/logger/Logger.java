package com.github.adamtmalek.flightsimulator.logger;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

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
		if (level.value > this.loggerLevel.value) {return;}
		String logmsg = String.format(
			"%s[%s]: %s%s",
			level.colourCode,
			level.name(),
			msg,
			"\033[0m"
			);
		// DO STUFF
		System.out.println(logmsg);
		// Arrays.toString(Thread.currentThread().getStackTrace());
	}
	public void trace(String msg) {
		logger.log(LogLevel.TRACE, msg);
	}

	public void debug(String msg) {
		logger.log(LogLevel.DEBUG, msg);
	}

	public void info(String msg) {
		logger.log(LogLevel.INFO, msg);
	}

	public void warn(String msg) {
		logger.log(LogLevel.WARN, msg);
	}


	public void error(String msg) {
		logger.log(LogLevel.ERROR, msg);
	}

	public void fatal(String msg) {
		logger.log(LogLevel.FATAL, msg);
	}
}
