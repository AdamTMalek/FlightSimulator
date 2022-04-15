package com.github.adamtmalek.flightsimulator.logger;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public abstract class Logger {
	public abstract void log(LogLevel level, String msg);

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

	private final static Logger singleton = new LoggerFacade(LogLevel.ALL, null);

	protected final @NotNull LogLevel loggerLevel;
	protected final @Nullable String output;

	private boolean shouldLog(@NotNull LogLevel logLevel) {
		return loggerLevel.value >= logLevel.value;
	}

	public static Logger getInstance() {
		return singleton;
	}

	public Logger(@NotNull LogLevel logLevel, @Nullable String output) {
		this.loggerLevel = logLevel;
		this.output = output;
	}

	private static class LoggerFacade extends Logger {
		private static final String configFilename = "logConfig.xml";
		private final List<Logger> loggers;

		protected LoggerFacade(@NotNull LogLevel logLevel, @Nullable String output) {
			super(logLevel, output);
			loggers = getLoggerFactory().createLoggers();
		}

		@Contract("-> new")
		private static LoggerFactory getLoggerFactory() {
			return getConfigurations()
					.map(LoggerFactory::withConfiguration)
					.orElseGet(LoggerFactory::withDefaultConfiguration);
		}

		@Contract(pure = true)
		private static @NotNull Optional<LoggerConfigurations> getConfigurations() {
			final var mapper = new XmlMapper();

			try {
				final var resourceUrl = LoggerFacade.class.getClassLoader().getResource(configFilename);
				if (resourceUrl == null) return Optional.empty();

				return Optional.of(mapper.readValue(resourceUrl, LoggerConfigurations.class));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		public void log(@NotNull LogLevel level, @NotNull String message) {
			loggers.stream()
					.filter(logger -> logger.shouldLog(level))
					.forEach(logger -> logger.log(level, message));
		}
	}
}
