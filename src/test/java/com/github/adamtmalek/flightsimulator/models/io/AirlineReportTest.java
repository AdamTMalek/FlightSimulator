package com.github.adamtmalek.flightsimulator.models.io;

import com.github.adamtmalek.flightsimulator.models.Aeroplane;
import com.github.adamtmalek.flightsimulator.models.Airport;
import com.github.adamtmalek.flightsimulator.models.Flight;
import com.github.adamtmalek.flightsimulator.models.GeodeticCoordinate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.ArrayList;

public class AirlineReportTest {

	@Test
	void givenPopulatedListThenMembersAreSummed() {

		var glasgowAirport = new Airport("G", "Glasgow Airport", new GeodeticCoordinate(55.87, -4.43));
		var edinburghAirport = new Airport("E", "Edinburgh Airport", new GeodeticCoordinate(55.95, -3.19));
		var londonAirport = new Airport("L", "London Airport", new GeodeticCoordinate(51.47, -0.46));
		var newYorkAirport = new Airport("NY", "New York Airport", new GeodeticCoordinate(40.71, -74.01));

		var flights = new ArrayList<Flight>();

		// Glasgow to London Flight
		flights.add(new Flight("",
				new Aeroplane("", "", 10, 15),
				glasgowAirport,
				londonAirport,
				ZonedDateTime.now(),
				new ArrayList<Airport.ControlTower>() {{
					add(glasgowAirport.controlTower);
					add(londonAirport.controlTower);
				}}
		));

		// Edinburgh to New York Flight
		flights.add(new Flight("",
				new Aeroplane("", "", 10, 15),
				edinburghAirport,
				newYorkAirport,
				ZonedDateTime.now(),
				new ArrayList<Airport.ControlTower>() {{
					add(edinburghAirport.controlTower);
					add(newYorkAirport.controlTower);
				}}
		));


		final var airlineReport = new AirlineReport(flights);
		Assertions.assertEquals(2, airlineReport.totalFlights());
		Assertions.assertEquals(0, airlineReport.estimatedCO2Emissions()); //TODO

		//GLA-LONDON = ~554KM, EDI-NY=~5241KM
		// Add slight delta for differences between distance calculators.
		Assertions.assertEquals(5795, airlineReport.totalDistanceTravelled(), 5);
	}

	@Test
	void givenEmptyAirlinesListThenMembersAreSetTo0() {
		var flights = new ArrayList<Flight>();
		final var airlineReport = new AirlineReport(flights);
		Assertions.assertEquals(0, airlineReport.totalFlights());
		Assertions.assertEquals(0, airlineReport.estimatedCO2Emissions());
		Assertions.assertEquals(0, airlineReport.totalDistanceTravelled());
		//TODO Test Fuel Consumption

	}
}
