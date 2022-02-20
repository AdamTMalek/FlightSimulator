package com.github.adamtmalek.flightsimulator.models;


import org.jetbrains.annotations.NotNull;

/**
 * @param speed               Units: Kilometres per hour
 * @param fuelConsumptionRate Units: Litres per 100 kilometre
 */
public record Aeroplane(@NotNull String model,
                        @NotNull String manufacturer,
                        double speed,
                        double fuelConsumptionRate) {
}
