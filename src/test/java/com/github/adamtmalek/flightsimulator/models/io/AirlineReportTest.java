package com.github.adamtmalek.flightsimulator.models.io;

import com.github.adamtmalek.flightsimulator.models.*;
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
    flights.add(Flight.build("0",
      new Airline("", ""),
      new Aeroplane("", "", 10, 5),
      glasgowAirport,
      londonAirport,
      ZonedDateTime.now(),
      new ArrayList<Airport.ControlTower>() {{
        add(glasgowAirport.controlTower);
        add(londonAirport.controlTower);
      }}
    ));

    // Edinburgh to New York Flight
    flights.add(Flight.build("0",
      new Airline("a", "a"),
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


    //GLA-LONDON = ~554KM, EDI-NY=~5241KM
    // Add slight delta for differences between distance calculators.
    Assertions.assertEquals(5795, airlineReport.totalDistanceTravelled(), 5);

    // Each flight aircraft consumes an x litres per 100 kilometre, specified as fuelConsumptionRatio.
    // GLA-LONDON will consume 5*(554/100)=27.7 litres of fuel, ED-NY will consume 15*5241=786.15 litres of fuel,
    // giving a total of ~813.85
    Assertions.assertEquals(813.85, airlineReport.estimatedFuelConsumption(), 5);

    // Total Fuel Consumed * Average CO2 Emissions per Kilometre = 813.85*3.16 = ~2571.77
    // Add  delta for differences between distance calculation.
    Assertions.assertEquals(2571.77, airlineReport.estimatedCO2Emissions(), 10);

  }

  @Test
  void givenEmptyAirlinesListThenMembersAreSetTo0() {
    var flights = new ArrayList<Flight>();
    final var airlineReport = new AirlineReport(flights);
    Assertions.assertEquals(0, airlineReport.totalFlights());
    Assertions.assertEquals(0, airlineReport.estimatedCO2Emissions());
    Assertions.assertEquals(0, airlineReport.totalDistanceTravelled());
    Assertions.assertEquals(0, airlineReport.estimatedFuelConsumption());

  }
}
