package com.github.adamtmalek.flightsimulator.models.io;

import org.jetbrains.annotations.NotNull;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Objects;

public abstract class TestSuite {
	protected static @NotNull Path getPathFromResources(@NotNull Class<? extends TestSuite> klass,
																											@NotNull String path) {
		try {
			return Path.of(
					Objects.requireNonNull(klass
							.getClassLoader()
							.getResource(path)
					).toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	protected @NotNull Path getPathFromResources(@NotNull String path) {
		return getPathFromResources(this.getClass(), path);
	}

	protected static @NotNull Path getPathFromResourcesOfThisTest(@NotNull Class<? extends TestSuite> klass,
																																@NotNull String resourceName) {
		try {
			return Path.of(
					Objects.requireNonNull(klass
							.getClassLoader()
							.getResource(String.format("%s/%s", klass.getSimpleName(), resourceName))
					).toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	protected @NotNull Path getPathFromResourcesOfThisTest(@NotNull String resourceName) {
		return getPathFromResourcesOfThisTest(this.getClass(), resourceName);
	}
}
