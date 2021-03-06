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

// TODO: Assertions in this test that rely on the position change are pretty much guaranteed to fail,
//   as they are extremely time dependent. We need to have a better way of checking if we're traveling
//   in the right direction without relying on hardcoded numbers.
public class FlightTrackingTest {
	/**
	 * Tests the system's capability to track flights concurrently. Every time a flight calculates its new position,
	 * it is published to the next control tower. The next control tower then publishes the most up-to-date tracked position for
	 * each received flight.
	 * <p>
	 * eg. If a flight is travelling from controlTowerA to controlTowerB, it will publish coordinates [0,0], [1,1], [2,2] and so on.
	 * The control tower will receive this data and store it. However, when publishing, we only care about the most recently received
	 * data.
	 * <p>
	 * The FlightJoiner subscribes to these positions, which is responsible for joining flights that have travelled between control towers.
	 * <p>
	 * eg. if a flight travels from controlTowerA, to controlTowerB, and then to controlTowerC, the flight shall publish it's tracked
	 * position to controlTowerB, and then to controlTowerC once it passes over controlTowerB. As such, controlTowerB and controlTowerC
	 * will publish the same flight. Therefore, the FlightJoiner joins these 2 Flights together by removing controlTowerA's flight. This
	 * is so that view components are provided with a single, most up-to-date instance per flight.
	 */

	@Test
	void testJoinFlightBetweenControlTowers() {
		/**
		 * Tests what happens when a flight travels between control towers, before crossing any.
		 */
		// Configure thread timing for test case.
		FlightSimulationThreadManagement.setFlightSimulationFrequency(0.05); // Flight travelling between G-E within test duration.
		FlightSimulationThreadManagement.setThreadFrequency(1.9);
		FlightSimulationThreadManagement.setGuiUpdateFrequency(0.508);

		final var simulationStartTime = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC+0"));

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

		var joiner = new FlightJoiner(observableFlights);

		final var airports = List.of(glasgowAirport, edinburghAirport, londonAirport, newYorkAirport);
		final var controlTowers = airports.stream().map(airport -> airport.controlTower).toList();
		controlTowers.forEach(tower -> tower.registerSubscriber(joiner));

		final var controlTowerThreads = airports.stream().map(airport -> new Thread(airport.controlTower)).toList();
		controlTowerThreads.forEach(Thread::start);

		var tracker = new Thread(new FlightTracker(Flight.buildWithSerialNumber("001",
				new Airline("", ""),
				new Aeroplane("a", "a", 1, 50),
				glasgowAirport,
				newYorkAirport,
				simulationStartTime,
				controlTowers),
				simulationStartTime));


		var joinerThread = new Thread(joiner);

		tracker.start();
		joinerThread.start();
		try {
			Thread.sleep(2000);
			tracker.stop();
			joinerThread.stop();
			controlTowerThreads.forEach(Thread::stop);

		} catch (Exception e) {
			e.printStackTrace();
		}

		Assertions.assertEquals(1, observableFlights.stream().toList().size());
		Assertions.assertEquals("E", observableFlights.stream().toList().get(0).flightStatus().getCurrentControlTower().code);
	}

	@Test
	void testJoinFlightAfterPassingControlTower() {
		/**
		 * Tests what happens when a flight crosses over a control tower, upon which it should communicate with the next control
		 * tower on its path.
		 */
		// Configure thread timing for test case.
		FlightSimulationThreadManagement.setFlightSimulationFrequency(0.005); // Flight travelling between G-E-L within test duration.
		FlightSimulationThreadManagement.setThreadFrequency(1.9);
		FlightSimulationThreadManagement.setGuiUpdateFrequency(0.508);

		final var simulationStartTime = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC+0"));

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

		var joiner = new FlightJoiner(observableFlights);

		final var airports = List.of(glasgowAirport, edinburghAirport, londonAirport, newYorkAirport);
		final var controlTowers = airports.stream().map(airport -> airport.controlTower).toList();
		controlTowers.forEach(tower -> tower.registerSubscriber(joiner));

		final var controlTowerThreads = airports.stream().map(airport -> new Thread(airport.controlTower)).toList();
		controlTowerThreads.forEach(Thread::start);


		var tracker = new Thread(new FlightTracker(Flight.buildWithSerialNumber("001",
				new Airline("", ""),
				new Aeroplane("a", "a", 1, 50),
				glasgowAirport,
				newYorkAirport,
				simulationStartTime,
				controlTowers),
				simulationStartTime));


		var joinerThread = new Thread(joiner);

		tracker.start();
		joinerThread.start();
		try {
			Thread.sleep(2000);
			tracker.stop();
			joinerThread.stop();
			controlTowerThreads.forEach(Thread::stop);

		} catch (Exception e) {
			e.printStackTrace();
		}

		Assertions.assertEquals(1, observableFlights.stream().toList().size());
		Assertions.assertEquals("L", observableFlights.stream().toList().get(0).flightStatus().getCurrentControlTower().code);
		Assertions.assertEquals(53.233916335001446, observableFlights.stream().toList().get(0).flightStatus().getCurrentPosition().latitude());
		Assertions.assertEquals(-1.4620081301856966, observableFlights.stream().toList().get(0).flightStatus().getCurrentPosition().longitude());
	}

	@Test
	void testJoinMultipleFlightCrossingSameControlTowers() {
		/**
		 * Tests what happens when multiple flights travels between control towers.
		 */

		// Configure thread timing for test case.
		FlightSimulationThreadManagement.setFlightSimulationFrequency(0.05); // Flight travelling between G-E within test duration.
		FlightSimulationThreadManagement.setThreadFrequency(2);
		FlightSimulationThreadManagement.setGuiUpdateFrequency(1);

		final var simulationStartTime = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC+0"));

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

		var joiner = new FlightJoiner(observableFlights);

		final var airports = List.of(glasgowAirport, edinburghAirport, londonAirport, newYorkAirport);
		final var controlTowers = airports.stream().map(airport -> airport.controlTower).toList();
		controlTowers.forEach(tower -> tower.registerSubscriber(joiner));

		final var controlTowerThreads = airports.stream().map(airport -> new Thread(airport.controlTower)).toList();
		controlTowerThreads.forEach(Thread::start);

		var flightATracker = new Thread(new FlightTracker(new Flight("FA",
				new Airline("", ""),
				new Aeroplane("a", "a", 1, 50),
				glasgowAirport,
				newYorkAirport,
				simulationStartTime,
				controlTowers),
				simulationStartTime));

		var flightBTracker = new Thread(new FlightTracker(new Flight("FB",
				new Airline("", ""),
				new Aeroplane("a", "a", 1, 50),
				glasgowAirport,
				newYorkAirport,
				simulationStartTime,
				controlTowers),
				simulationStartTime));


		var joinerThread = new Thread(joiner);

		flightATracker.start();
		flightBTracker.start();

		joinerThread.start();
		try {
			Thread.sleep(2200);
			flightATracker.stop();
			flightBTracker.stop();
			joinerThread.stop();
			controlTowerThreads.forEach(Thread::stop);

		} catch (Exception e) {
			e.printStackTrace();
		}


		Assertions.assertEquals(4, observableFlights.stream().toList().size());

		final var mostRecentFlightA = observableFlights.stream().filter(f -> f.flightID().equals("FA") && f.flightStatus().getCurrentPosition().latitude() == 55.91264599913692).findFirst();
		Assertions.assertEquals(true, mostRecentFlightA.isPresent());
		Assertions.assertEquals("E", mostRecentFlightA.get().flightStatus().getCurrentControlTower().code);
		Assertions.assertEquals(55.91264599913692, mostRecentFlightA.get().flightStatus().getCurrentPosition().latitude());
		Assertions.assertEquals(-3.7937437059544337, mostRecentFlightA.get().flightStatus().getCurrentPosition().longitude());

		final var mostRecentFlightB = observableFlights.stream().filter(f -> f.flightID().equals("FB") && f.flightStatus().getCurrentPosition().latitude() == 55.91264599913692).findFirst();
		Assertions.assertEquals(true, mostRecentFlightB.isPresent());
		Assertions.assertEquals("E", mostRecentFlightB.get().flightStatus().getCurrentControlTower().code);
		Assertions.assertEquals(55.91264599913692, mostRecentFlightB.get().flightStatus().getCurrentPosition().latitude());
		Assertions.assertEquals(-3.7937437059544337, mostRecentFlightB.get().flightStatus().getCurrentPosition().longitude());

	}

	@Test
	void testFlightReachDestination() {
		/**
		 * Tests what happens when a flight reaches its destination.
		 */
		// Configure thread timing for test case.
		FlightSimulationThreadManagement.setFlightSimulationFrequency(0.001); // Flight travelling between G-E within test duration.
		FlightSimulationThreadManagement.setThreadFrequency(1.9);
		FlightSimulationThreadManagement.setGuiUpdateFrequency(3);

		final var simulationStartTime = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC+0"));

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
		var londonAirport = new Airport("L", "London Airport", new GeodeticCoordinate(51.47, -0.46));

		var joiner = new FlightJoiner(observableFlights);

		final var airports = List.of(glasgowAirport, londonAirport);
		final var controlTowers = airports.stream().map(airport -> airport.controlTower).toList();
		controlTowers.forEach(tower -> tower.registerSubscriber(joiner));

		final var controlTowerThreads = airports.stream().map(airport -> new Thread(airport.controlTower)).toList();
		controlTowerThreads.forEach(Thread::start);

		var tracker = new Thread(new FlightTracker(Flight.buildWithSerialNumber("001",
				new Airline("", ""),
				new Aeroplane("a", "a", 1, 50),
				glasgowAirport,
				londonAirport,
				simulationStartTime.minusDays(5),
				controlTowers),
				simulationStartTime));


		var joinerThread = new Thread(joiner);

		tracker.start();
		joinerThread.start();
		try {
			Thread.sleep(2000);
			tracker.stop();
			joinerThread.stop();
			controlTowerThreads.forEach(Thread::stop);

		} catch (Exception e) {
			e.printStackTrace();
		}
		final var finalFlight = observableFlights.stream().toList().stream().filter(f -> f.flightStatus().getStatus() == Flight.FlightStatus.Status.TERMINATED).findFirst();
		Assertions.assertEquals(true, finalFlight.isPresent());
		Assertions.assertEquals(Flight.FlightStatus.Status.TERMINATED, finalFlight.get().flightStatus().getStatus());
		Assertions.assertEquals(londonAirport.controlTower.code, finalFlight.get().flightStatus().getCurrentControlTower().code);
		Assertions.assertEquals(londonAirport.position, finalFlight.get().flightStatus().getCurrentPosition());

	}

	@Test
	void testFlightCommunicatesWithDepartureAirportBeforeTakeoff() {
		/**
		 * Test flight communicates with the departure airport correctly before takeoff.
		 */
		// Configure thread timing for test case.
		FlightSimulationThreadManagement.setFlightSimulationFrequency(0.000278); // Adds approximately 1 hour every tick
		FlightSimulationThreadManagement.setThreadFrequency(2);
		FlightSimulationThreadManagement.setGuiUpdateFrequency(3);

		final var simulationStartTime = ZonedDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC+0"));

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
		var londonAirport = new Airport("L", "London Airport", new GeodeticCoordinate(51.47, -0.46));

		var joiner = new FlightJoiner(observableFlights);

		final var airports = List.of(glasgowAirport, londonAirport);
		final var controlTowers = airports.stream().map(airport -> airport.controlTower).toList();
		controlTowers.forEach(tower -> tower.registerSubscriber(joiner));

		final var controlTowerThreads = airports.stream().map(airport -> new Thread(airport.controlTower)).toList();
		controlTowerThreads.forEach(Thread::start);

		var tracker = new Thread(new FlightTracker(Flight.buildWithSerialNumber("001",
				new Airline("", ""),
				new Aeroplane("a", "a", 1, 50),
				glasgowAirport,
				londonAirport,
				simulationStartTime.plusHours(2),
				controlTowers),
				simulationStartTime));


		var joinerThread = new Thread(joiner);

		tracker.start();
		joinerThread.start();
		try {
			Thread.sleep(1000);
			tracker.stop();
			joinerThread.stop();
			controlTowerThreads.forEach(Thread::stop);

		} catch (Exception e) {
			e.printStackTrace();
		}
		Assertions.assertEquals(1, observableFlights.stream().toList().size());
		final var finalFlight = observableFlights.stream().toList().get(0);
		Assertions.assertEquals(Flight.FlightStatus.Status.WAITING_FOR_DEPARTURE, finalFlight.flightStatus().getStatus());
		Assertions.assertEquals(glasgowAirport.controlTower.code, finalFlight.flightStatus().getCurrentControlTower().code);
		Assertions.assertEquals(glasgowAirport.position, finalFlight.flightStatus().getCurrentPosition());
	}
}
