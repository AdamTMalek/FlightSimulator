package com.github.adamtmalek.flightsimulator.models.io;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CsvFileHandler extends Serializer {
	public static final @NotNull String SEPARATOR_COMMA = ",";

	private final @NotNull String separator;

	public CsvFileHandler(@NotNull String separator) {
		this.separator = separator;
	}

	public <T> List<T> readFile(@NotNull Path filePath, @NotNull Class<T> klass) throws SerializationException {
		try {
			return Files.readAllLines(filePath)
					.stream()
					.map(line -> createInstanceFromLine(line, klass))
					.toList();
		} catch (IOException e) {
			throw new SerializationException(e);
		}
	}

	private <T> @NotNull T createInstanceFromLine(@NotNull String line,
																								@NotNull Class<T> klass) throws SerializationException {
		final var values = Arrays.stream(line.split(separator))
				.map(String::strip)
				.toArray(String[]::new);

		return createInstance(klass, values);
	}

	@Override
	public <T> @NotNull Path saveToFile(@NotNull Path path,
																			@NotNull Collection<T> objectCollection) throws SerializationException {
		try {
			if (objectCollection.isEmpty()) return Files.createFile(path);

			final var content = getSerializableValues(objectCollection)
					.map(values -> String.join(separator, values))
					.collect(Collectors.joining("\n"));

			if (path.getParent() != null) Files.createDirectories(path.getParent());
			return Files.writeString(path, content);
		} catch (IOException e) {
			throw new SerializationException(e);
		}
	}
}
