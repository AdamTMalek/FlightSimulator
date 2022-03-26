package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class FlightSimulationThreadManagementTest {
	@Test
	void testThreadsStartStopCorrectly() {

		// Configure thread timing for test case.
		FlightSimulationThreadManagement.setFlightSimulationFrequency(0.005); // Flight travelling between G-E-L within test duration.
		FlightSimulationThreadManagement.setThreadFrequency(2);
		FlightSimulationThreadManagement.setGuiUpdateFrequency(2);

		ObservableSet<Flight> observableFlights = FXCollections.observableSet();
		observableFlights
				.addListener((SetChangeListener<Flight>) changeListener -> {
					final var newFlight = changeListener.getElementAdded();
					final var message = "Listener received data from `%s`: %s at `%s, %s`"
							.formatted(newFlight.flightStatus().getCurrentControlTower(),
									newFlight.flightID(),
									newFlight.flightStatus().getCurrentPosition().latitude(),
									newFlight.flightStatus().getCurrentPosition().longitude());
					System.out.println(message);
				});

		var glasgowAirport = new Airport("G", "Glasgow Airport", new GeodeticCoordinate(55.87, -4.43));
		var edinburghAirport = new Airport("E", "Edinburgh Airport", new GeodeticCoordinate(55.95, -3.19));
		var londonAirport = new Airport("L", "London Airport", new GeodeticCoordinate(51.47, -0.46));
		var newYorkAirport = new Airport("NY", "New York Airport", new GeodeticCoordinate(40.71, -74.01));

		var flightJoiner = new FlightJoiner(observableFlights);

		glasgowAirport.controlTower.registerSubscriber(flightJoiner);
		edinburghAirport.controlTower.registerSubscriber(flightJoiner);
		londonAirport.controlTower.registerSubscriber(flightJoiner);
		newYorkAirport.controlTower.registerSubscriber(flightJoiner);

		var flightA = new Flight("FA",
				new Airline("", ""),
				new Aeroplane("a", "a", 1, 50),
				glasgowAirport,
				newYorkAirport,
				ZonedDateTime.of(2022, 2, 18, 16, 0, 0, 0, ZoneId.of("UTC+0")),
				List.of(glasgowAirport.controlTower, edinburghAirport.controlTower,
						londonAirport.controlTower, newYorkAirport.controlTower));

		var flightB = new Flight("FB",
				new Airline("", ""),
				new Aeroplane("a", "a", 1, 50),
				glasgowAirport,
				newYorkAirport,
				ZonedDateTime.of(2022, 2, 18, 16, 0, 0, 0, ZoneId.of("UTC+0")),
				List.of(glasgowAirport.controlTower, edinburghAirport.controlTower,
						londonAirport.controlTower, newYorkAirport.controlTower));


		var flightSimManager = new FlightSimulationThreadManagement(
				List.of(flightA, flightB),
				List.of(glasgowAirport.controlTower, edinburghAirport.controlTower,
						londonAirport.controlTower, newYorkAirport.controlTower),
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
		Assertions.assertEquals(2, observableFlights.stream().toList().size());
	}

	@Test
	void testThreadsPauseResumeCorrectly() {

		// Configure thread timing for test case.
		FlightSimulationThreadManagement.setFlightSimulationFrequency(0.005); // Flight travelling between G-E-L within test duration.
		FlightSimulationThreadManagement.setThreadFrequency(2);
		FlightSimulationThreadManagement.setGuiUpdateFrequency(20);

		ObservableSet<Flight> observableFlights = FXCollections.observableSet();
		observableFlights
				.addListener((SetChangeListener<Flight>) changeListener -> {
					final var newFlight = changeListener.getElementAdded();
					final var message = "Listener received data from `%s`: %s at `%s, %s`"
							.formatted(newFlight.flightStatus().getCurrentControlTower(),
									newFlight.flightID(),
									newFlight.flightStatus().getCurrentPosition().latitude(),
									newFlight.flightStatus().getCurrentPosition().longitude());
					System.out.println(message);
				});

		var glasgowAirport = new Airport("G", "Glasgow Airport", new GeodeticCoordinate(55.87, -4.43));
		var edinburghAirport = new Airport("E", "Edinburgh Airport", new GeodeticCoordinate(55.95, -3.19));
		var londonAirport = new Airport("L", "London Airport", new GeodeticCoordinate(51.47, -0.46));
		var newYorkAirport = new Airport("NY", "New York Airport", new GeodeticCoordinate(40.71, -74.01));

		var flightJoiner = new FlightJoiner(observableFlights);

		glasgowAirport.controlTower.registerSubscriber(flightJoiner);
		edinburghAirport.controlTower.registerSubscriber(flightJoiner);
		londonAirport.controlTower.registerSubscriber(flightJoiner);
		newYorkAirport.controlTower.registerSubscriber(flightJoiner);

		var flightA = new Flight("FA",
				new Airline("", ""),
				new Aeroplane("a", "a", 1, 50),
				glasgowAirport,
				newYorkAirport,
				ZonedDateTime.of(2022, 2, 18, 16, 0, 0, 0, ZoneId.of("UTC+0")),
				List.of(glasgowAirport.controlTower, edinburghAirport.controlTower,
						londonAirport.controlTower, newYorkAirport.controlTower));

		var flightB = new Flight("FB",
				new Airline("", ""),
				new Aeroplane("a", "a", 1, 50),
				glasgowAirport,
				newYorkAirport,
				ZonedDateTime.of(2022, 2, 18, 16, 0, 0, 0, ZoneId.of("UTC+0")),
				List.of(glasgowAirport.controlTower, edinburghAirport.controlTower,
						londonAirport.controlTower, newYorkAirport.controlTower));

		var flightSimManager = new FlightSimulationThreadManagement(
				List.of(flightA, flightB),
				List.of(glasgowAirport.controlTower, edinburghAirport.controlTower,
						londonAirport.controlTower, newYorkAirport.controlTower),
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
		Assertions.assertEquals(14, observableFlights.stream().toList().size());
	}


	@Test
	void testNoThreadsCreatedAutonomously() {

		ObservableSet<Flight> observableFlights = FXCollections.observableSet();
		observableFlights
				.addListener((SetChangeListener<Flight>) changeListener -> {
					final var newFlight = changeListener.getElementAdded();
					final var message = "Listener received data from `%s`: %s at `%s, %s`"
							.formatted(newFlight.flightStatus().getCurrentControlTower(),
									newFlight.flightID(),
									newFlight.flightStatus().getCurrentPosition().latitude(),
									newFlight.flightStatus().getCurrentPosition().longitude());
					System.out.println(message);
				});

		// Configure thread timing for test case.
		FlightSimulationThreadManagement.setFlightSimulationFrequency(0.005); // Flight travelling between G-E-L within test duration.
		FlightSimulationThreadManagement.setThreadFrequency(2);
		FlightSimulationThreadManagement.setGuiUpdateFrequency(2);


		var glasgowAirport = new Airport("G", "Glasgow Airport", new GeodeticCoordinate(55.87, -4.43));
		var edinburghAirport = new Airport("E", "Edinburgh Airport", new GeodeticCoordinate(55.95, -3.19));
		var londonAirport = new Airport("L", "London Airport", new GeodeticCoordinate(51.47, -0.46));
		var newYorkAirport = new Airport("NY", "New York Airport", new GeodeticCoordinate(40.71, -74.01));

		var flightJoiner = new FlightJoiner(observableFlights);

		glasgowAirport.controlTower.registerSubscriber(flightJoiner);
		edinburghAirport.controlTower.registerSubscriber(flightJoiner);
		londonAirport.controlTower.registerSubscriber(flightJoiner);
		newYorkAirport.controlTower.registerSubscriber(flightJoiner);

		var flightA = new Flight("FA",
				new Airline("", ""),
				new Aeroplane("a", "a", 1, 50),
				glasgowAirport,
				newYorkAirport,
				ZonedDateTime.of(2022, 2, 18, 16, 0, 0, 0, ZoneId.of("UTC+0")),
				List.of(glasgowAirport.controlTower, edinburghAirport.controlTower,
						londonAirport.controlTower, newYorkAirport.controlTower));

		var flightB = new Flight("FB",
				new Airline("", ""),
				new Aeroplane("a", "a", 1, 50),
				glasgowAirport,
				newYorkAirport,
				ZonedDateTime.of(2022, 2, 18, 16, 0, 0, 0, ZoneId.of("UTC+0")),
				List.of(glasgowAirport.controlTower, edinburghAirport.controlTower,
						londonAirport.controlTower, newYorkAirport.controlTower));

		var flightSimManager = new FlightSimulationThreadManagement(
				List.of(flightA, flightB),
				List.of(glasgowAirport.controlTower, edinburghAirport.controlTower,
						londonAirport.controlTower, newYorkAirport.controlTower),
				flightJoiner);


		try {
			Thread.sleep(2000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Assertions.assertEquals(0, observableFlights.stream().toList().size());

	}

	@Test
	void testFlightThreadAdded() {
		// Configure thread timing for test case.
		FlightSimulationThreadManagement.setFlightSimulationFrequency(0.005); // Flight travelling between G-E-L within test duration.
		FlightSimulationThreadManagement.setThreadFrequency(2);
		FlightSimulationThreadManagement.setGuiUpdateFrequency(2);

		ObservableSet<Flight> observableFlights = FXCollections.observableSet();
		observableFlights
				.addListener((SetChangeListener<Flight>) changeListener -> {
					final var newFlight = changeListener.getElementAdded();
					final var message = "Listener received data from `%s`: %s at `%s, %s`"
							.formatted(newFlight.flightStatus().getCurrentControlTower(),
									newFlight.flightID(),
									newFlight.flightStatus().getCurrentPosition().latitude(),
									newFlight.flightStatus().getCurrentPosition().longitude());
					System.out.println(message);
				});

		var glasgowAirport = new Airport("G", "Glasgow Airport", new GeodeticCoordinate(55.87, -4.43));
		var edinburghAirport = new Airport("E", "Edinburgh Airport", new GeodeticCoordinate(55.95, -3.19));
		var londonAirport = new Airport("L", "London Airport", new GeodeticCoordinate(51.47, -0.46));
		var newYorkAirport = new Airport("NY", "New York Airport", new GeodeticCoordinate(40.71, -74.01));

		var flightJoiner = new FlightJoiner(observableFlights);

		glasgowAirport.controlTower.registerSubscriber(flightJoiner);
		edinburghAirport.controlTower.registerSubscriber(flightJoiner);
		londonAirport.controlTower.registerSubscriber(flightJoiner);
		newYorkAirport.controlTower.registerSubscriber(flightJoiner);

		var flightA = new Flight("FA",
				new Airline("", ""),
				new Aeroplane("a", "a", 1, 50),
				glasgowAirport,
				newYorkAirport,
				ZonedDateTime.of(2022, 2, 18, 16, 0, 0, 0, ZoneId.of("UTC+0")),
				List.of(glasgowAirport.controlTower, edinburghAirport.controlTower,
						londonAirport.controlTower, newYorkAirport.controlTower));

		var flightB = new Flight("FB",
				new Airline("", ""),
				new Aeroplane("a", "a", 1, 50),
				glasgowAirport,
				newYorkAirport,
				ZonedDateTime.of(2022, 2, 18, 16, 0, 0, 0, ZoneId.of("UTC+0")),
				List.of(glasgowAirport.controlTower, edinburghAirport.controlTower,
						londonAirport.controlTower, newYorkAirport.controlTower));


		var flightSimManager = new FlightSimulationThreadManagement(
				List.of(flightA),
				List.of(glasgowAirport.controlTower, edinburghAirport.controlTower,
						londonAirport.controlTower, newYorkAirport.controlTower),
				flightJoiner);


		flightSimManager.startThreads();

		flightSimManager.startTrackingNewFlight(flightB);


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


		flightSimManager.stopThreads();

		// With test frequency configuration, should have 2 outputs after initial 2000ms delay between starting and stopping.
		// If there is more, the threads must not have stopped.
		Assertions.assertEquals(2, observableFlights.stream().toList().size());

	}

}
