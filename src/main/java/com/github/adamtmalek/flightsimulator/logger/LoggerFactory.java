package com.github.adamtmalek.flightsimulator.logger;

public class LoggerFactory {
	public String[] getLoggerTypes() {
		String[] availableTypes = {
			"CONSOLE", "FILE"
		};
		return availableTypes;
	}

	public Logger getLogger(String loggerType) {
		return switch (loggerType) {
			case "CONSOLE" -> new ConsoleLogger(LogLevel.WARN);
			case "FILE" -> new FileLogger(LogLevel.ALL,
					"src/main/java/com/github/adamtmalek/flightsimulator/logger/logs/");
			default -> null;
		};
	}
}
