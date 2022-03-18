package com.github.adamtmalek.flightsimulator.models;


import com.github.adamtmalek.flightsimulator.io.SerializableField;
import org.jetbrains.annotations.NotNull;

/**
 * @param speed               Units: Kilometres per hour
 * @param fuelConsumptionRate Units: Litres per 100 kilometre
 */
public record Aeroplane(
    @SerializableField
    @NotNull String model,
    @SerializableField
    @NotNull String manufacturer,
    @SerializableField
    double speed,
    @SerializableField
    double fuelConsumptionRate) {

  static final double AVG_RATE_OF_CO2_EMISSION = 3.16; // kilogram per litre
}
