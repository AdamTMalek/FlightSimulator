package com.github.adamtmalek.flightsimulator.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.security.InvalidParameterException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class FlightTest {

	@Test
	void testFlightBuiltWithSerialNumber() {
		var flight = Flight.buildWithSerialNumber("001",
				new Airline("TEST", ""),
				new Aeroplane("a", "a", 1, 50),
				new Airport("G", "Glasgow Airport", new GeodeticCoordinate(55.87, -4.43)),
				new Airport("E", "Edinburgh Airport", new GeodeticCoordinate(55.95, -3.19)),
				ZonedDateTime.of(2022, 2, 18, 16, 0, 0, 0, ZoneId.of("UTC+0")),
				new ArrayList<>());

		Assertions.assertEquals("TEST001", flight.flightID());
	}

	@Test
	void testFlightBuiltWithFullId() {
		var flight = new Flight("FULL-FLIGHT-ID",
				new Airline("TEST", ""),
				new Aeroplane("a", "a", 1, 50),
				new Airport("G", "Glasgow Airport", new GeodeticCoordinate(55.87, -4.43)),
				new Airport("E", "Edinburgh Airport", new GeodeticCoordinate(55.95, -3.19)),
				ZonedDateTime.of(2022, 2, 18, 16, 0, 0, 0, ZoneId.of("UTC+0")),
				new ArrayList<>());

		Assertions.assertEquals("FULL-FLIGHT-ID", flight.flightID());
	}

	@Test
	void testExceptionThrownIfInvalidSerialNumber() {
		Assertions.assertThrows(InvalidParameterException.class, () -> {
			Flight.buildWithSerialNumber("Serial numbers containing non-integers is invalid.",
					new Airline("TEST", ""),
					new Aeroplane("a", "a", 1, 50),
					new Airport("G", "Glasgow Airport", new GeodeticCoordinate(55.87, -4.43)),
					new Airport("E", "Edinburgh Airport", new GeodeticCoordinate(55.95, -3.19)),
					ZonedDateTime.of(2022, 2, 18, 16, 0, 0, 0, ZoneId.of("UTC+0")),
					new ArrayList<>());
		});
	}

	@Test
	void testFlightInformationCalculations() {
		var glasgowAirport = new Airport("G", "Glasgow Airport", new GeodeticCoordinate(55.87, -4.43));
		var edinburghAirport = new Airport("E", "Edinburgh Airport", new GeodeticCoordinate(55.95, -3.19));
		var londonAirport = new Airport("L", "London Airport", new GeodeticCoordinate(51.47, -0.46));
		var newYorkAirport = new Airport("NY", "New York Airport", new GeodeticCoordinate(40.71, -74.01));

		var flight = Flight.buildWithSerialNumber("001",
				new Airline("", ""),
				new Aeroplane("a", "a", 1, 50),
				glasgowAirport,
				newYorkAirport,
				ZonedDateTime.of(2022, 2, 18, 16, 0, 0, 0, ZoneId.of("UTC+0")),
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
		Assertions.assertEquals(6157.39, flight.estimatedTotalDistanceToTravel(), 0.5);

		// The aircraft consumes an x litres per 100 kilometre, specified as fuelConsumptionRatio.
		// As the flight has a total distance of ~6157.39, the estimated fuel consumption is as follows:
		// 50* (6157.39/100) = 3078.70 Litres
		Assertions.assertEquals(3078.70, flight.estimatedFuelConsumption(), 0.5);

		// Estimated Fuel Consumed * Average CO2 Emissions per Kilometre = 3078.70*3.16 = ~9728.69
		Assertions.assertEquals(9728.69, flight.estimatedCO2Produced(), 1);
	}
}
