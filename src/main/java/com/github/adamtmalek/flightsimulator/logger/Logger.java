package com.github.adamtmalek.flightsimulator.logger;

import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
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

			// load config
			String propertiesPath = "src/main/java/com/github/adamtmalek/flightsimulator/logger/logger.properties";
			Properties properties = new Properties();
			try {
				properties.load(new FileInputStream(propertiesPath));
			} catch (FileNotFoundException e) {
				System.err.println("Properties file was not found in " + propertiesPath);
			} catch (IOException e) {
				System.err.println("IOException when reading properties file. " + e.getMessage());
			}

//			List<String> loggersToMake = Arrays.asList(properties.getProperty("loggers").split(","));

//			for (String loggerClass: loggersToMake) {
//				Logger logger = makeLoggerFromConfig(loggerClass.trim(), properties);
//				if (logger != null) {
//					loggers.add(logger);
//				}
//			}

			// Hard coded here
			System.out.println("src/main/java/com/github/adamtmalek/flightsimulator/logger/logs/".equals(properties.getProperty("FileLogger.path")));
//			loggers = Arrays.asList(
//					new ConsoleLogger(LogLevel.WARN),
//					new FileLogger(LogLevel.ALL,
//							properties.getProperty("FileLogger.path")
//					)
//			);

			// Factory version
			LoggerFactory logFact = new LoggerFactory();
			// List.stream().forEach() version
//			List<String> loggersToMake = Arrays.asList(logFact.getLoggerTypes());
//			loggersToMake.stream()
//					.forEach(logType -> loggers.add(logFact.getLogger(logType)));
			// For loop version
			String[] loggersToMake = logFact.getLoggerTypes();
			for (String loggerType: loggersToMake) {
				loggers.add(logFact.getLogger(loggerType));
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

		private Logger makeLoggerFromConfig(String loggerClass, Properties properties) {
			Class c;
			try {
				c = Class.forName("com.github.adamtmalek.flightsimulator.logger." + loggerClass);
			} catch (ClassNotFoundException e) {
				System.err.println(String.format("Logger class %s not found.", loggerClass));
				return null;
			}

			//properties.propertyNames().asIterator().forEachRemaining(s -> System.out.println(s));
			List<String> props = properties.stringPropertyNames()
					.stream()
					.filter(s -> s.startsWith(loggerClass))
					.toList();
			//System.out.println(props);
			List<Object> params = new ArrayList<>();
			for (String prop: props) {
				if (prop.endsWith("logLevel")) {
					params.add(LogLevel.valueOf(properties.getProperty(prop)));
				} else {
					params.add(properties.getProperty(prop));
				}
			}

//			Parameter[] params = cons.getParameters();
//			for (Parameter param: params) {
//
//				paramsToSend.add()
//			}

			// Constructor for the logger class
			Constructor cons = c.getDeclaredConstructors()[0];
			try {
				return (Logger) cons.newInstance(params);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}


			System.out.println(Arrays.toString(c.getDeclaredConstructors()[0].getParameters()));
			return null;
		}
	}
}
