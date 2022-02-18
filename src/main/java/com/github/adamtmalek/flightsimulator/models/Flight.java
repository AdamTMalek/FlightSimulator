package com.github.adamtmalek.flightsimulator.models;

import com.github.adamtmalek.flightsimulator.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record Flight(@NotNull String flightID,
                     @NotNull Aeroplane aeroplane,
                     @NotNull Airport departureAirport,
                     @NotNull Airport destinationAirport,
                     @NotNull String departureDate,
                     @NotNull String departureTime,
                     @NotNull List<Airport.ControlTower> controlTowersToCross,
                     @NotNull Kilometre distanceTravelled,
                     @NotNull LitresPerKilometre fuelConsumption,
                     @NotNull GramsPerKilometre estimatedCO2Produced) {

    public Flight(@NotNull String flightID,
                  @NotNull Aeroplane aeroplane,
                  @NotNull Airport departureAirport,
                  @NotNull Airport destinationAirport,
                  @NotNull String departureDate,
                  @NotNull String departureTime,
                  @NotNull List<Airport.ControlTower> controlTowersToCross) {

        this(flightID,
                aeroplane,
                departureAirport,
                destinationAirport,
                departureDate,
                departureTime,
                controlTowersToCross,
                calculateDistanceTravelled(controlTowersToCross),
                calculateFuelConsumption(controlTowersToCross),
                calculateEstimatedCO2Produced(controlTowersToCross)
        );
    }

    private static Kilometre calculateDistanceTravelled(List<Airport.ControlTower> controlTowersToCross) {
        var distanceTravelled = new Kilometre(0);

        if (controlTowersToCross.size() > 1) {

            // Calculate the distance between pairs of control tower pairs, and sum into distance travelled.
            for (int i = 1; i < controlTowersToCross.size(); i++) {
                final var firstControlTowerPosition = controlTowersToCross.get(i--).position;
                final var secondControlTowerPosition = controlTowersToCross.get(i).position;

                distanceTravelled = new Kilometre(distanceTravelled.kilometre()
                        + Utils.calculateDistanceBetweenCoordinates(firstControlTowerPosition, secondControlTowerPosition).kilometre());
            }
        }
        return distanceTravelled;

    }

    private static LitresPerKilometre calculateFuelConsumption(List<Airport.ControlTower> controlTowersToCross) {
        throw new RuntimeException("Not implemented");
    }

    private static GramsPerKilometre calculateEstimatedCO2Produced(List<Airport.ControlTower> controlTowersToCross) {
        throw new RuntimeException("Not implemented");
    }


}
