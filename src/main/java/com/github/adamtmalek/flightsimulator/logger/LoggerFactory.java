package com.github.adamtmalek.flightsimulator.logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

/*
	To add more loggers:
	1. Create a new logger class which extends Logger (check ConsoleLogger.java for simplest example)
	2. Add unique logger name to getLoggerTypes() and corresponding switch case in getLogger()
	3. Optionally add properties to the logger.properties file
 */

public class LoggerFactory {
	private final String propertiesPath = "src/main/java/com/github/adamtmalek/flightsimulator/logger/logger.properties";
	private final Properties props = new Properties();

	public LoggerFactory() {
		try {
			props.load(new FileInputStream(propertiesPath));
		} catch (FileNotFoundException e) {
			System.err.println("Properties file was not found in " + propertiesPath);
		} catch (IOException e) {
			System.err.println("IOException when reading properties file. " + e.getMessage());
		}
	}

	public String[] getLoggerTypes() {
		return new String[]{
				"CONSOLE", "FILE"
		};
	}

	public Logger getLogger(String loggerType) throws IOException {
		return switch (loggerType) {
			case "CONSOLE" -> new ConsoleLogger(
					getLogLevelFromProperties("ConsoleLogger", "WARN"));
			case "FILE" -> new FileLogger(
					getLogLevelFromProperties("FileLogger", "ALL"),
					props.getProperty("FileLogger.path",
					"src/main/java/com/github/adamtmalek/flightsimulator/logger/logs/"));
			default -> null;
		};
	}

	public LogLevel getLogLevelFromProperties(String loggerType, String defaultLevel) {
		String level = props.getProperty(
				String.format("%s.logLevel", loggerType),
				defaultLevel.toUpperCase(Locale.ROOT));
		return LogLevel.valueOf(level);
	}
}
