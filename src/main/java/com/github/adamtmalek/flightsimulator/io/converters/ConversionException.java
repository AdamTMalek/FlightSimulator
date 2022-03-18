package com.github.adamtmalek.flightsimulator.io.converters;

import org.jetbrains.annotations.NotNull;

public class ConversionException extends RuntimeException {
	public ConversionException(@NotNull String message) {
		super(message);
	}

	public ConversionException(@NotNull Class<?> unexpectedType) {
		super(String.format("Type %s cannot be converted without using a custom converter", unexpectedType.getSimpleName()));
	}
}
