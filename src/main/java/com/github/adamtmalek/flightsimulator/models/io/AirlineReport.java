package com.github.adamtmalek.flightsimulator.models.io;

import com.github.adamtmalek.flightsimulator.models.Flight;
import com.github.adamtmalek.flightsimulator.models.GramsPerKilometre;
import com.github.adamtmalek.flightsimulator.models.Kilometre;

import java.util.ArrayList;

public record AirlineReport(int totalFlights, Kilometre totalDistanceTravelled,
                            GramsPerKilometre estimatedCO2Emissions) {
    public AirlineReport(ArrayList<Flight> flights) {
        this(flights.size(), calculateTotalDistanceTravelled(flights), calculateEstimatedCO2Emissions(flights));
    }

    private static Kilometre calculateTotalDistanceTravelled(ArrayList<Flight> flights) {
        return new Kilometre(0); //TODO implement proper calculation
    }

    private static GramsPerKilometre calculateEstimatedCO2Emissions(ArrayList<Flight> flights) {
        return new GramsPerKilometre(0); //TODO implement proper calculation
    }
}
