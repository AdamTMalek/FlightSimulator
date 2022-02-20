package com.github.adamtmalek.flightsimulator.models.io.converters;

public interface Converter<T> {
	T convertFromString(String... values);

	String convertToString(T object);

	default int getNumberOfStringsToConsume() {
		return 1;
	}
}
