package com.github.adamtmalek.flightsimulator.logger;

import com.fasterxml.jackson.xml.XmlMapper;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

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
		private List<Logger> loggers;

		protected LoggerImplementation(LogLevel logLevel) {
			// instantiate loggers here from config
			super(logLevel);

			loggers = new ArrayList<>();
			// Factory version
			LoggerFactory logFact = new LoggerFactory();
			// List.stream().forEach() version
//			List<String> loggersToMake = Arrays.asList(logFact.getLoggerTypes());
//			loggersToMake.stream()
//					.forEach(logType -> loggers.add(logFact.getLogger(logType)));

			// For loop version
			String[] loggersToMake = logFact.getLoggerTypes();
			for (String loggerType: loggersToMake) {
				try {
					this.loggers.add(logFact.getLogger(loggerType));
				} catch (IOException e) {
					System.out.println("IOException when trying to create logger");
					System.out.println(e.getMessage());
				}
			}
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
