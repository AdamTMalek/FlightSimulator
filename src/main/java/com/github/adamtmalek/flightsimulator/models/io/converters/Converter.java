package com.github.adamtmalek.flightsimulator.models.io.converters;


/**
 * The converter is used for converting a value of type T from- and to string.
 * A converter is mostly used alongside SerializableField annotation,
 * @see com.github.adamtmalek.flightsimulator.models.io.SerializableField
 * where a custom converter is used for conversion performed by the Serializer.
 *
 * @param <T> Type which is handled by the converter
 */
public interface Converter<T> {
	T convertFromString(String... values);

	String convertToString(T object);

	/**
	 * @return Number of string values that the converter needs to consume to perform conversion.
	 */
	default int getNumberOfStringsToConsume() {
		return 1;
	}
}
