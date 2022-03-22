package com.github.adamtmalek.flightsimulator.logger;

public class LoggerFront extends Logger {
	private static final Logger singleton = new LoggerFront();

	private final Logger[] loggers = {
		//new ConsoleLogger(LogLevel.INFO),
		ConsoleLogger.getInstance(),
		new FileLogger(LogLevel.ALL, "src/main/java/com/github/adamtmalek/flightsimulator/logger/logs/")
	};

	private LoggerFront() {}

	public static Logger getInstance() {
		return singleton;
	}

	public void log(LogLevel level, String msg) {
		String formattedMsg = String.format(
			"[%s]: %s",
			level.name(),
			msg
		);

		for (Logger logger: loggers) {
			logger.log(level, formattedMsg);
		}
	}
}
