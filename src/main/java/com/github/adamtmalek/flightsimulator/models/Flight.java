package com.github.adamtmalek.flightsimulator.models;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record Flight(@NotNull String flightID,
                     @NotNull Aeroplane aeroplane,
                     @NotNull Airport departureAirport,
                     @NotNull Airport destinationAirport,
                     @NotNull String departureDate,
                     @NotNull String departureTime,
                     @NotNull List<Airport.ControlTower> controlTowersToCross) {
}
