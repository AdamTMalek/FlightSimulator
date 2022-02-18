package com.github.adamtmalek.flightsimulator.models.io;

import com.github.adamtmalek.flightsimulator.models.Aeroplane;
import com.github.adamtmalek.flightsimulator.models.Airport;
import com.github.adamtmalek.flightsimulator.models.Flight;
import com.github.adamtmalek.flightsimulator.models.GeodeticCoordinate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class FlightTest {

    @Test
    void givenMultipleControlTowersThenCalculateExtraRecordFields() {

        var glasgowAirport = new Airport("G", "Glasgow Airport", new GeodeticCoordinate(55.87, -4.43));
        var edinburghAirport = new Airport("E", "Edinburgh Airport", new GeodeticCoordinate(55.95, -3.19));
        var londonAirport = new Airport("L", "London Airport", new GeodeticCoordinate(51.47, -0.46));
        var newYorkAirport = new Airport("NY", "New York Airport", new GeodeticCoordinate(40.71, -74.01));

        var flight = new Flight("a",
                new Aeroplane("a", "a", 1, 1),
                glasgowAirport,
                newYorkAirport,
                ZonedDateTime.of(2022, 02, 18, 16, 00, 00, 0, ZoneId.of("UTC+0")),
                new ArrayList<Airport.ControlTower>() {{
                    add(glasgowAirport.controlTower);
                    add(edinburghAirport.controlTower);
                    add(londonAirport.controlTower);
                    add(newYorkAirport.controlTower);
                }});

        // Distance between Glasgow and Edinburgh: 77.79km
        // Distance between Edinburgh and London: 529.46km
        // Distance between London and New York 5550.14km:
        // Total Distance 6157.39:
        Assertions.assertEquals(6157.39, flight.distanceTravelled(), 0.5);
        //TODO: Check other fields are calculated correctly.
    }
}
