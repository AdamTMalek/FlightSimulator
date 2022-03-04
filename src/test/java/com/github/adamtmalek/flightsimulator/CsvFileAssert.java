package com.github.adamtmalek.flightsimulator;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class CsvFileAssert extends AbstractAssert<CsvFileAssert, Path> {
	private static final @NotNull String DEFAULT_DELIMITER = ",";
	private final @NotNull String delimiter;

	public CsvFileAssert(@NotNull Path actual, @NotNull String delimiter) {
		super(actual, CsvFileAssert.class);
		this.delimiter = delimiter;
	}

	public CsvFileAssert(@NotNull Path actual) {
		this(actual, DEFAULT_DELIMITER);
	}

	@Contract("_, _ -> new")
	public static @NotNull CsvFileAssert assertThat(@NotNull Path actual, @NotNull String delimiter) {
		return new CsvFileAssert(actual, delimiter);
	}

	@Contract("_ -> new")
	public static @NotNull CsvFileAssert assertThat(@NotNull Path actual) {
		return new CsvFileAssert(actual);
	}

	public void hasTheSameContentAs(@NotNull Path other) {
			Assertions.assertThat(getContent(actual))
					.containsExactlyInAnyOrderElementsOf(getContent(other));
	}

	private @NotNull List<List<String>> getContent(@NotNull Path path) {
		try {
			return Files.readAllLines(path, StandardCharsets.UTF_8)
					.stream()
					.map(line -> Arrays.stream(line.split(delimiter)))
					.map(line -> line.map(String::strip).toList())
					.toList();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
