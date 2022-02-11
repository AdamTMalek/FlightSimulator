package com.github.adamtmalek.flightsimulator.models;


import org.jetbrains.annotations.NotNull;

public record Aeroplane(@NotNull String model,
                        @NotNull String manufacturer,
                        @NotNull String speed,
                        @NotNull String fuelConsumption) {
  // TODO: Change this to secondary constructor. Primary should have the final classes as data types.
}
