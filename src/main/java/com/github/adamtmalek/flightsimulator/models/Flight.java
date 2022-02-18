package com.github.adamtmalek.flightsimulator.models;

import com.github.adamtmalek.flightsimulator.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @param distanceTravelled    Units: Kilometre
 * @param fuelConsumption:     Units: Litres per kilometre
 * @param estimatedCO2Produced Units Grams per kilometre:
 */
public record Flight(@NotNull String flightID,
                     @NotNull Aeroplane aeroplane,
                     @NotNull Airport departureAirport,
                     @NotNull Airport destinationAirport,
                     @NotNull String departureDate,
                     @NotNull String departureTime,
                     @NotNull List<Airport.ControlTower> controlTowersToCross,
                     @NotNull double distanceTravelled,
                     @NotNull double fuelConsumption,
                     @NotNull double estimatedCO2Produced) {

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

    private static double calculateDistanceTravelled(List<Airport.ControlTower> controlTowersToCross) {
        var distanceTravelled = 0.0;

        if (controlTowersToCross.size() > 1) {

            // Calculate the distance between pairs of control tower pairs, and sum into distance travelled.
            for (int i = 1; i < controlTowersToCross.size(); i++) {
                final var firstControlTowerPosition = controlTowersToCross.get(i - 1).position;
                final var secondControlTowerPosition = controlTowersToCross.get(i).position;

                distanceTravelled += Utils.calculateDistanceBetweenCoordinates(firstControlTowerPosition, secondControlTowerPosition);
            }
        }
        return distanceTravelled;

    }

    private static double calculateFuelConsumption(List<Airport.ControlTower> controlTowersToCross) {
        return 0; //TODO
    }

    private static double calculateEstimatedCO2Produced(List<Airport.ControlTower> controlTowersToCross) {
        return 0; //TODO
    }


}
