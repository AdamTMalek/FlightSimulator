package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.interfaces.Subscriber;
import com.github.adamtmalek.flightsimulator.models.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class FlightSimulationThreadManagementTest {
	@Test
	void testThreadsStartStopCorrectly() {

		// Configure thread timing for test case.
		FlightSimulationThreadManagement.setFlightSimulationFrequency(0.005); // Flight travelling between G-E-L within test duration.
		FlightSimulationThreadManagement.setThreadFrequency(2);
		FlightSimulationThreadManagement.setGuiUpdateFrequency(2);


		var glasgowAirport = new Airport("G", "Glasgow Airport", new GeodeticCoordinate(55.87, -4.43));
		var edinburghAirport = new Airport("E", "Edinburgh Airport", new GeodeticCoordinate(55.95, -3.19));
		var londonAirport = new Airport("L", "London Airport", new GeodeticCoordinate(51.47, -0.46));
		var newYorkAirport = new Airport("NY", "New York Airport", new GeodeticCoordinate(40.71, -74.01));

		var flightA = Flight.buildWithFlightId("FA",
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

		var flightB = Flight.buildWithFlightId("FB",
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

		var flightJoiner = new FlightJoiner();
		var stubOutputSub = new StubFlightJoinerSubscriber();
		flightJoiner.registerSubscriber(stubOutputSub);

		var flightSimManager = new FlightSimulationThreadManagement(
				new ArrayList<Flight>() {{
					add(flightA);
					add(flightB);
				}},
				new ArrayList<Airport.ControlTower>() {{
					add(glasgowAirport.controlTower);
					add(edinburghAirport.controlTower);
					add(londonAirport.controlTower);
					add(newYorkAirport.controlTower);
				}},
				flightJoiner);

		flightSimManager.startThreads();

		try {
			Thread.sleep(2000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		flightSimManager.stopThreads();

		try {
			Thread.sleep(2000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// With test frequency configuration, should have 2 outputs after initial 2000ms delay between starting and stopping.
		// If there is more, the threads must not have stopped.
		Assertions.assertEquals(2, stubOutputSub.timesReceivedData);
	}

	@Test
	void testThreadsPauseResumeCorrectly() {

		// Configure thread timing for test case.
		FlightSimulationThreadManagement.setFlightSimulationFrequency(0.005); // Flight travelling between G-E-L within test duration.
		FlightSimulationThreadManagement.setThreadFrequency(10);
		FlightSimulationThreadManagement.setGuiUpdateFrequency(10);


		var glasgowAirport = new Airport("G", "Glasgow Airport", new GeodeticCoordinate(55.87, -4.43));
		var edinburghAirport = new Airport("E", "Edinburgh Airport", new GeodeticCoordinate(55.95, -3.19));
		var londonAirport = new Airport("L", "London Airport", new GeodeticCoordinate(51.47, -0.46));
		var newYorkAirport = new Airport("NY", "New York Airport", new GeodeticCoordinate(40.71, -74.01));

		var flightA = Flight.buildWithFlightId("FA",
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

		var flightB = Flight.buildWithFlightId("FB",
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

		var flightJoiner = new FlightJoiner();
		var stubOutputSub = new StubFlightJoinerSubscriber();
		flightJoiner.registerSubscriber(stubOutputSub);

		var flightSimManager = new FlightSimulationThreadManagement(
				new ArrayList<Flight>() {{
					add(flightA);
					add(flightB);
				}},
				new ArrayList<Airport.ControlTower>() {{
					add(glasgowAirport.controlTower);
					add(edinburghAirport.controlTower);
					add(londonAirport.controlTower);
					add(newYorkAirport.controlTower);
				}},
				flightJoiner);

		flightSimManager.startThreads();

		try {
			Thread.sleep(2000);
		} catch (Exception e) {
			e.printStackTrace();
		}


		flightSimManager.pauseThreads();

		try {
			Thread.sleep(2000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		flightSimManager.resumeThreads();

		try {
			Thread.sleep(2000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		flightSimManager.stopThreads();

		try {
			Thread.sleep(2000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// With test frequency configuration, should have 6 outputs after starting, pausing, resuming, and then stopping.
		// If there is more, the threads must not have stopped.
		Assertions.assertEquals(6, stubOutputSub.timesReceivedData);
	}


	@Test
	void testNoThreadsCreatedAutonomously() {

		// Configure thread timing for test case.
		FlightSimulationThreadManagement.setFlightSimulationFrequency(0.005); // Flight travelling between G-E-L within test duration.
		FlightSimulationThreadManagement.setThreadFrequency(2);
		FlightSimulationThreadManagement.setGuiUpdateFrequency(2);


		var glasgowAirport = new Airport("G", "Glasgow Airport", new GeodeticCoordinate(55.87, -4.43));
		var edinburghAirport = new Airport("E", "Edinburgh Airport", new GeodeticCoordinate(55.95, -3.19));
		var londonAirport = new Airport("L", "London Airport", new GeodeticCoordinate(51.47, -0.46));
		var newYorkAirport = new Airport("NY", "New York Airport", new GeodeticCoordinate(40.71, -74.01));

		var flightA = Flight.buildWithFlightId("FA",
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

		var flightB = Flight.buildWithFlightId("FB",
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

		var flightJoiner = new FlightJoiner();
		var stubOutputSub = new StubFlightJoinerSubscriber();
		flightJoiner.registerSubscriber(stubOutputSub);

		var flightSimManager = new FlightSimulationThreadManagement(
				new ArrayList<Flight>() {{
					add(flightA);
					add(flightB);
				}},
				new ArrayList<Airport.ControlTower>() {{
					add(glasgowAirport.controlTower);
					add(edinburghAirport.controlTower);
					add(londonAirport.controlTower);
					add(newYorkAirport.controlTower);
				}},
				flightJoiner);


		try {
			Thread.sleep(2000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Assertions.assertEquals(0, stubOutputSub.timesReceivedData);

	}

	private class StubFlightJoinerSubscriber implements Subscriber<ArrayList<DirectedFlight>> {
		public int timesReceivedData = 0;

		public void callback(ArrayList<DirectedFlight> data) {
			timesReceivedData++;
		}
	}

}
