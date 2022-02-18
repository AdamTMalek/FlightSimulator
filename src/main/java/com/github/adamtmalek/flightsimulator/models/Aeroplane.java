package com.github.adamtmalek.flightsimulator.models;


import org.jetbrains.annotations.NotNull;

/**
 * @param speed           Units: Kilometres per hour
 * @param fuelConsumption Units: Litres
 */
public record Aeroplane(@NotNull String model,
                        @NotNull String manufacturer,
                        @NotNull double speed,
                        @NotNull double fuelConsumption) {
}
