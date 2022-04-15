package com.github.adamtmalek.flightsimulator.logger;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


class LoggerFactory {
	private final @Nullable LoggerConfigurations configurations;

	private LoggerFactory(@Nullable LoggerConfigurations configurations) {
		this.configurations = configurations;
	}

	@Contract("_ -> new")
	public static @NotNull LoggerFactory withConfiguration(@NotNull LoggerConfigurations configurations) {
		return new LoggerFactory(configurations);
	}

	@Contract("-> new")
	public static @NotNull LoggerFactory withDefaultConfiguration() {
		return new LoggerFactory(null);
	}


	public @NotNull @Unmodifiable List<Logger> createLoggers() {
		if (configurations == null) {
			return new ArrayList<>() {{
				add(new ConsoleLogger(LogLevel.WARN, null));
			}};
		}

		return Arrays.stream(configurations.loggerConfigurations).map(this::createLogger).toList();
	}

	@Contract("_ -> new")
	private @NotNull Logger createLogger(@NotNull LoggerConfigurations.LoggerConfiguration loggerConfiguration) {
		final var fqn = loggerConfiguration.getLoggerClass();
		try {
			final var klass = Class.forName(fqn);
			final var constructor = klass.getConstructor(LogLevel.class, String.class);
			return (Logger) constructor.newInstance(loggerConfiguration.getLogLevel(), loggerConfiguration.getOutput());
		} catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
						 | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}
