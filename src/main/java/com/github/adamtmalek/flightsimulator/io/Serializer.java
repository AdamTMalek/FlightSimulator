package com.github.adamtmalek.flightsimulator.io;

import com.github.adamtmalek.flightsimulator.io.converters.ConversionException;
import com.github.adamtmalek.flightsimulator.io.converters.Converter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class Serializer {
	public abstract <T> @NotNull Path saveToFile(@NotNull Path path,
																							 @NotNull Collection<T> objectCollection) throws SerializationException;

	public abstract <T> List<T> readFile(@NotNull Path path, @NotNull Class<T> klass) throws SerializationException;

	protected @NotNull <T> Constructor<T> getConstructorFor(@NotNull Class<T> klass) throws SerializationException {
		final var paramTypes = getSerializableFields(klass)
				.map(Field::getType)
				.toArray(Class<?>[]::new);
		try {
			return klass.getConstructor(paramTypes);
		} catch (NoSuchMethodException e) {
			final var expectedConstructorFormRepresentation = String.format("(%s)",
					String.join(",", getSerializableFields(klass)
							.map(Field::getType)
							.map(Class::getName)
							.toList()
					)
			);
			final var message = String.format("Expected class %s to have a constructor which accepts %s",
					klass.getName(),
					expectedConstructorFormRepresentation);
			throw new SerializationException(message, e);
		}
	}

	protected <T> @NotNull T createInstance(@NotNull Class<T> klass, @NotNull String[] values)
			throws SerializationException {
		final var constructor = getConstructorFor(klass);
		final List<String> valuesList = new ArrayList<>(Arrays.asList(values));
		final var convertedValues = new ArrayList<>();
		for (var param : constructor.getParameters()) {
			final Object convertedValue;
			final var customConverter = param.isAnnotationPresent(SerializableField.class) ? getCustomConverter(param) : null;
			if (customConverter == null && isBuiltInType(param)) {
				convertedValue = convertValueOfBuiltInType(valuesList, param);
			} else if (customConverter == null) {
				convertedValue = tryConvertValueWithoutDefinedCustomConverter(klass, param, valuesList);
			} else {
				convertedValue = convertValueWithCustomConverter(valuesList, customConverter);
			}

			convertedValues.add(convertedValue);
		}

		try {
			return constructor.newInstance(convertedValues.toArray());
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new SerializationException(e);
		}
	}

	private Object convertValueOfBuiltInType(@NotNull List<String> valuesList, @NotNull Parameter param) {
		final Object convertedValue;
		final var type = param.getType();
		final var value = valuesList.remove(0);

		if (type == double.class) convertedValue = Double.parseDouble(value);
		else if (type == float.class) convertedValue = Float.parseFloat(value);
		else if (type == int.class) convertedValue = Integer.parseInt(value);
		else if (type == boolean.class) convertedValue = Boolean.parseBoolean(value);
		else if (type == String.class) convertedValue = value;
		else throw new ConversionException(type);

		return convertedValue;
	}

	private @NotNull Object convertValueWithCustomConverter(@NotNull List<String> valuesList,
																													@NotNull Converter<?> customConverter) {
		final Object convertedValue;
		final var valuesToTake = valuesList.subList(0, customConverter.getNumberOfStringsToConsume())
				.toArray(new String[0]);
		IntStream.range(0, customConverter.getNumberOfStringsToConsume()).forEach(e -> valuesList.remove(0));

		convertedValue = customConverter.convertFromString(valuesToTake);
		return convertedValue;
	}

	/**
	 * This method will try to obtain a converter based on any field in the class
	 * which is marked as serializable {@link SerializableField}.
	 * This is so that the same annotation does not need to be duplicated, once in the field declaration and once
	 * in the constructor.
	 *
	 * @param klass      The class which is being constructed.
	 * @param parameter  The parameter for which the values need to be converter.
	 * @param valuesList List of values to convert.
	 * @return Converted object based on the values list.
	 */
	private @NotNull Object tryConvertValueWithoutDefinedCustomConverter(@NotNull Class<?> klass,
																																			 @NotNull Parameter parameter,
																																			 @NotNull List<String> valuesList) {
		return Arrays.stream(klass.getDeclaredFields())
				.filter(f -> f.isAnnotationPresent(SerializableField.class))
				.filter(f -> f.getAnnotation(SerializableField.class).converter() != SerializableField.DefaultConverter.class)
				.filter(f -> f.getType() == parameter.getType())
				.findAny()
				.map(f -> convertValueWithCustomConverter(valuesList, Objects.requireNonNull(getCustomConverter(f))))
				.orElseThrow(() -> {
					final var msg = String.format("Cannot find a converter for parameter %s of class %s",
							parameter.getName(), klass.getName());
					return new SerializationException(msg);
				});
	}

	protected boolean hasSerializableFields(@NotNull Class<?> klass) {
		return getSerializableFields(klass).findAny().isPresent();
	}

	@SuppressWarnings("unchecked")
	protected <T> @NotNull Stream<List<String>> getSerializableValues(@NotNull Collection<T> objectsCollection) {
		final var klass = objectsCollection.stream()
				.findAny()
				.map(o -> o.getClass())
				.orElseThrow(() -> new IllegalStateException("Unexpected empty collection"));
		final var serializableFields = getSerializableFields(klass).toList();
		if (serializableFields.isEmpty()) {
			throw new SerializationException(String.format("Class %s has no fields marked with @SerializableField - " +
					"it cannot be serialized", klass));
		}
		return objectsCollection.stream()
				.map(o -> serializableFields.stream().map(field -> {
					try {
						final Object originalValue = field.canAccess(o) ? field.get(o) : o.getClass().getMethod(field.getName()).invoke(o);
						final var converter = ((Converter<T>) getConverter(field));
						return converter.convertToString((T) originalValue);
					} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
						throw new SerializationException(e);
					}
				}).toList());
	}

	protected @NotNull Stream<Field> getSerializableFields(@NotNull Class<?> klass) {
		return Arrays.stream(klass.getDeclaredFields())
				.filter(f -> f.isAnnotationPresent(SerializableField.class));
	}

	protected @NotNull Stream<String> getFieldNames(@NotNull Stream<Field> fields) {
		return fields.map(field -> {
			final String customName = field.getAnnotation(SerializableField.class).name();
			return !customName.isBlank() ? customName : field.getName();
		});
	}

	protected @NotNull Converter<?> getConverter(@NotNull AnnotatedElement element) throws SerializationException {
		try {
			return element.getAnnotation(SerializableField.class)
					.converter()
					.getConstructor()
					.newInstance();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new SerializationException(e);
		}
	}

	protected @Nullable Converter<?> getCustomConverter(@NotNull AnnotatedElement element) throws SerializationException {
		final var converter = getConverter(element);
		return converter.getClass() == SerializableField.DefaultConverter.class ? null : converter;
	}

	private boolean isBuiltInType(@NotNull Parameter parameter) {
		final var klass = parameter.getType();
		return (klass == String.class)
				|| (klass == double.class)
				|| (klass == float.class)
				|| (klass == int.class)
				|| (klass == boolean.class);
	}
}
