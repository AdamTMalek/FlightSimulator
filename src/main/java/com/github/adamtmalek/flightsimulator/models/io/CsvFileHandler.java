package com.github.adamtmalek.flightsimulator.models.io;

import com.github.adamtmalek.flightsimulator.models.io.converters.ConversionException;
import com.github.adamtmalek.flightsimulator.models.io.converters.Converter;
import com.github.adamtmalek.flightsimulator.models.io.converters.CustomConverter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class CsvFileHandler {
	public static final @NotNull String SEPARATOR_COMMA = ",";

	private final @NotNull String separator;

	public CsvFileHandler(@NotNull String separator) {
		this.separator = separator;
	}

	public <T> List<T> readFile(@NotNull Path filePath, @NotNull Class<T> klass) throws IOException, FileHandlerException {
		return readFileStream(filePath)
			.map(line -> parseLine(line, klass))
			.toList();
	}

	private Stream<String> readFileStream(@NotNull Path filePath) throws IOException {
		return Files.readAllLines(filePath)
			.stream();
	}

	@SuppressWarnings("unchecked")
	private <T> @NotNull T parseLine(@NotNull String line, @NotNull Class<T> klass) {
		final var values = Arrays.stream(line.split(separator))
			.map(String::strip)
			.toArray(String[]::new);

		return Arrays.stream(klass.getConstructors())
			.filter(e -> isSuitableConstructor(e, values))
			.findFirst()
			.map(e -> createInstance((Constructor<T>) e, values))
			.orElseThrow(() ->
				new RuntimeException(
					String.format("Cannot instantiate %s - no suitable constructor", klass.getSimpleName())
				)
			);
	}

	private boolean isSuitableConstructor(@NotNull Constructor<?> constructor, @NotNull String[] values) {
		final var parameters = constructor.getParameters();
		final var expectedStringValues = Arrays.stream(parameters)
			.reduce(0, (acc, param) -> acc + getNumberOfStringToConsumeByParameter(param), Integer::sum);
		if (expectedStringValues != values.length) {
			return false;
		}

		return Arrays.stream(parameters)
			.allMatch(param -> {
				if (param.isAnnotationPresent(CustomConverter.class)) {
					return true;
				} else {
					return param.getType() == double.class
						|| param.getType() == float.class
						|| param.getType() == String.class
						|| param.getType() == int.class;
				}
			});
	}

	private int getNumberOfStringToConsumeByParameter(@NotNull Parameter parameter) {
		return parameter.isAnnotationPresent(CustomConverter.class)
			? getCustomConverterInstance(parameter).getNumberOfStringsToConsume()
			: 1;
	}

	private <T> @NotNull T createInstance(@NotNull Constructor<T> constructor, @NotNull String[] values) {
		List<String> valuesList = new ArrayList<>(Arrays.asList(values));
		final var convertedValues = new ArrayList<>();
		for (var param : constructor.getParameters()) {
			final Object convertedValue;
			if (param.isAnnotationPresent(CustomConverter.class)) {
				final var converter = getCustomConverterInstance(param);
				final var valuesToTake = valuesList.subList(0, converter.getNumberOfStringsToConsume())
					.toArray(new String[0]);
				valuesList = valuesList.subList(converter.getNumberOfStringsToConsume(), valuesList.size());

				convertedValue = converter.convertFromString(valuesToTake);
			} else {
				final var type = param.getType();
				final var value = valuesList.remove(0);
				if (type == double.class) convertedValue = Double.parseDouble(value);
				else if (type == float.class) convertedValue = Float.parseFloat(value);
				else if (type == int.class) convertedValue = Integer.parseInt(value);
				else if (type == String.class) convertedValue = value;
				else throw new ConversionException(type);
			}

			convertedValues.add(convertedValue);
		}

		try {
			return constructor.newInstance(convertedValues.toArray());
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	private @NotNull Converter<?> getCustomConverterInstance(@NotNull Parameter parameter) {
		try {
			return parameter.getAnnotation(CustomConverter.class)
				.converter()
				.getConstructor()
				.newInstance();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}
}
