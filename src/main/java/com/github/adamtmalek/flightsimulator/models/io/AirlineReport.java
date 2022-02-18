package com.github.adamtmalek.flightsimulator.models.io;

import com.github.adamtmalek.flightsimulator.models.Flight;

import java.util.ArrayList;

/**
 * @param totalDistanceTravelled Units: Kilometre
 * @param averageFuelConsumption Units: Litres per kilometre
 * @param estimatedCO2Emissions  Units: Grams per kilometre
 */
public record AirlineReport(int totalFlights,
                            double totalDistanceTravelled,
                            double averageFuelConsumption,
                            double estimatedCO2Emissions) {

    public AirlineReport(ArrayList<Flight> flights) {
        this(flights.size(), calculateTotalDistanceTravelled(flights), calculateAverageFuelConsumption(flights), calculateEstimatedCO2Emissions(flights));
    }

    private static double calculateTotalDistanceTravelled(ArrayList<Flight> flights) {
        throw new RuntimeException("Not implemented");
//            return new Kilometre(flights.stream().mapToDouble(o -> o.totalDistance().kilometre()).sum());

    }

    private static double calculateAverageFuelConsumption(ArrayList<Flight> flights) {
        throw new RuntimeException("Not implemented");
    }

    private static double calculateEstimatedCO2Emissions(ArrayList<Flight> flights) {
        throw new RuntimeException("Not implemented");
//            return new GramsPerKilometre(flights.stream().mapToDouble(o -> o.estimatedCO2Produced().gramsPerKilometre()).sum());

    }
}
