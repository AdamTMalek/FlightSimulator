package com.github.adamtmalek.flightsimulator.models;

import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * @param distanceTravelled         Units: Kilometre
 * @param estimatedFuelConsumption: Units: Litres
 * @param estimatedCO2Produced      Units: Kilograms
 */
public record Flight(@NotNull String flightID,
                     @NotNull Aeroplane aeroplane,
                     @NotNull Airport departureAirport,
                     @NotNull Airport destinationAirport,
                     @NotNull ZonedDateTime departureDate,
                     @NotNull List<Airport.ControlTower> controlTowersToCross,
                     double distanceTravelled,
                     double estimatedFuelConsumption,
                     double estimatedCO2Produced) {
  

    public static Flight build(@NotNull String flightID,
                               @NotNull Aeroplane aeroplane,
                               @NotNull Airport departureAirport,
                               @NotNull Airport destinationAirport,
                               @NotNull ZonedDateTime departureDate,
                               @NotNull List<Airport.ControlTower> controlTowersToCross) {
        final var distanceTravelled = calculateDistanceTravelled(controlTowersToCross);
        final var estimatedFuelConsumption = calculateEstimatedFuelConsumption(aeroplane.fuelConsumptionRate(), distanceTravelled);
        final var estimatedCO2Produced = calculateEstimatedCO2Produced(estimatedFuelConsumption);

        return new Flight(flightID,
                aeroplane,
                departureAirport,
                destinationAirport,
                departureDate,
                controlTowersToCross,
                distanceTravelled,
                estimatedFuelConsumption,
                estimatedCO2Produced);


    }

    private static double calculateDistanceTravelled(List<Airport.ControlTower> controlTowersToCross) {
        var distanceTravelled = 0.0;

        if (controlTowersToCross.size() > 1) {

            // Calculate the distance between pairs of control tower pairs, and sum into distance travelled.
            for (int i = 1; i < controlTowersToCross.size(); i++) {
                final var firstControlTowerPosition = controlTowersToCross.get(i - 1).position;
                final var secondControlTowerPosition = controlTowersToCross.get(i).position;
                distanceTravelled += Math.abs(firstControlTowerPosition.calculateDistance(secondControlTowerPosition));
            }
        }
        return distanceTravelled;

    }

    private static double calculateEstimatedFuelConsumption(double fuelConsumption, double distance) {
        return fuelConsumption * (distance / 100);
    }

    private static double calculateEstimatedCO2Produced(double estimatedFuelConsumption) {
        return Aeroplane.AVG_RATE_OF_CO2_EMISSION * estimatedFuelConsumption;
    }


}
