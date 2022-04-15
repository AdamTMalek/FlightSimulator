package com.github.adamtmalek.flightsimulator.logger;

import org.jetbrains.annotations.Nullable;

/**
 * This logger, even though it has a public constructor should not be created by itself.
 * The public constructor is there to get reflection working, to be able to create it by the factory.
 */
public class ConsoleLogger extends Logger {
	public ConsoleLogger(LogLevel level, @Nullable String output) {
		super(level, output);
	}

	public void log(LogLevel level, String msg) {
		System.out.println(level.colourCode + msg + "\033[0m");
		// Arrays.toString(Thread.currentThread().getStackTrace());
	}
}
