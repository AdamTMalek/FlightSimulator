package com.github.adamtmalek.flightsimulator.models;

import com.github.adamtmalek.flightsimulator.models.io.SerializableField;
import org.jetbrains.annotations.NotNull;

public record Airline(@SerializableField @NotNull String code,
											@SerializableField @NotNull String name) {

}
