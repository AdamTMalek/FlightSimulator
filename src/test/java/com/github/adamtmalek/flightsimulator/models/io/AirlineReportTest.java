package com.github.adamtmalek.flightsimulator.models.io;

import com.github.adamtmalek.flightsimulator.models.Flight;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class AirlineReportTest {

//    @Test
//    void givenPopulatedListThenMembersAreSummed() {
//        var flights = new ArrayList<Flight>();
//        flights.add(new Flight("",
//                new Aeroplane("", "", "", ""),
//                new Airport("", "", "", ""),
//                new Airport("", "", "", ""),
//                ZonedDateTime.now(),
//                new ArrayList<Airport.ControlTower>(),
//                new Kilometre(1),
//                Duration.ofHours(2),
//                new GramsPerKilometre(3)
//        ));
//
//        flights.add(new Flight("",
//                new Aeroplane("", "", "", ""),
//                new Airport("", "", "", ""),
//                new Airport("", "", "", ""),
//                ZonedDateTime.now(),
//                new ArrayList<Airport.ControlTower>(),
//                new Kilometre(1),
//                Duration.ofHours(2),
//                new GramsPerKilometre(3)
//        ));
//
//        flights.add(new Flight("",
//                new Aeroplane("", "", "", ""),
//                new Airport("", "", "", ""),
//                new Airport("", "", "", ""),
//                ZonedDateTime.now(),
//                new ArrayList<ControlTower>(),
//                new Kilometre(1),
//                Duration.ofHours(2),
//                new GramsPerKilometre(3)
//        ));
//
//        final var airlineReport = new AirlineReport(flights);
//        Assertions.assertEquals(3, airlineReport.totalFlights());
//        Assertions.assertEquals(9, airlineReport.estimatedCO2Emissions().gramsPerKilometre());
//        Assertions.assertEquals(3, airlineReport.totalDistanceTravelled().kilometre());
//        //TODO Test Fuel Consumption
//
//    }

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
