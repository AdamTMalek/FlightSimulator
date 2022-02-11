package com.github.adamtmalek.flightsimulator.models;

import org.jetbrains.annotations.NotNull;

public record Airport(
		@NotNull String code,
		@NotNull String name,
		@NotNull GeodeticCoordinate position) {
	public Airport(@NotNull String code, @NotNull String name, @NotNull String latitude, @NotNull String longitude) {
		this(code, name, null);
		// TODO: Convert latitude and longitude to GeodeticCoordinate
		throw new RuntimeException("Not implemented");
	}
}
