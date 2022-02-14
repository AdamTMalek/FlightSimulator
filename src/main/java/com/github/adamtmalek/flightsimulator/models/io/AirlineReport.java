package com.github.adamtmalek.flightsimulator.models.io;

import com.github.adamtmalek.flightsimulator.models.Flight;
import com.github.adamtmalek.flightsimulator.models.GramsPerKilometre;
import com.github.adamtmalek.flightsimulator.models.Kilometre;
import com.github.adamtmalek.flightsimulator.models.LitresPerKilometre;

import java.util.ArrayList;

public record AirlineReport(int totalFlights,
                            Kilometre totalDistanceTravelled,
                            LitresPerKilometre averageFuelConsumption,
                            GramsPerKilometre estimatedCO2Emissions) {

    public AirlineReport(ArrayList<Flight> flights) {
        this(flights.size(), calculateTotalDistanceTravelled(flights), calculateAverageFuelConsumption(flights), calculateEstimatedCO2Emissions(flights));
    }

    private static Kilometre calculateTotalDistanceTravelled(ArrayList<Flight> flights) {
        if (flights.size() == 0) {
            return new Kilometre(0);
        } else {
            throw new RuntimeException("Not implemented");
//            return new Kilometre(flights.stream().mapToDouble(o -> o.totalDistance().kilometre()).sum());
        }
    }

    private static LitresPerKilometre calculateAverageFuelConsumption(ArrayList<Flight> flights) {
        if (flights.size() == 0) {
            return new LitresPerKilometre(0);
        } else {
            return new LitresPerKilometre(0); // TODO Flight record needs to calculate fuel consumption.
        }

    }

    private static GramsPerKilometre calculateEstimatedCO2Emissions(ArrayList<Flight> flights) {
        if (flights.size() == 0) {
            return new GramsPerKilometre(0);
        } else {
            throw new RuntimeException("Not implemented");
//            return new GramsPerKilometre(flights.stream().mapToDouble(o -> o.estimatedCO2Produced().gramsPerKilometre()).sum());
        }
    }
}
