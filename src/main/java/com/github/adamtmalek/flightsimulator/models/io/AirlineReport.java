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
        throw new RuntimeException("Not implemented");
//            return new Kilometre(flights.stream().mapToDouble(o -> o.totalDistance().kilometre()).sum());

    }

    private static LitresPerKilometre calculateAverageFuelConsumption(ArrayList<Flight> flights) {
        throw new RuntimeException("Not implemented");
    }

    private static GramsPerKilometre calculateEstimatedCO2Emissions(ArrayList<Flight> flights) {
        throw new RuntimeException("Not implemented");
//            return new GramsPerKilometre(flights.stream().mapToDouble(o -> o.estimatedCO2Produced().gramsPerKilometre()).sum());

    }
}
