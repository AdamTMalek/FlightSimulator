package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.interfaces.Subscriber;
import com.github.adamtmalek.flightsimulator.models.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
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


		var glasgowAirport = new Airport("G", "Glasgow Airport", new GeodeticCoordinate(55.87, -4.43));
		var edinburghAirport = new Airport("E", "Edinburgh Airport", new GeodeticCoordinate(55.95, -3.19));
		var londonAirport = new Airport("L", "London Airport", new GeodeticCoordinate(51.47, -0.46));
		var newYorkAirport = new Airport("NY", "New York Airport", new GeodeticCoordinate(40.71, -74.01));

		var joiner = new FlightJoiner();
		final var airports = List.of(glasgowAirport, edinburghAirport, londonAirport, newYorkAirport);
		final var controlTowers = airports.stream().map(airport -> airport.controlTower).toList();
		controlTowers.forEach(tower -> tower.registerSubscriber(joiner));

		final var controlTowerThreads = airports.stream().map(airport -> new Thread(airport.controlTower)).toList();
		controlTowerThreads.forEach(Thread::start);

		var stubJoinerSubscriber = new StubFlightJoinerSubscriber();
		joiner.registerSubscriber(stubJoinerSubscriber);

		var tracker = new Thread(new FlightTracker(Flight.buildWithSerialNumber("001",
				new Airline("", ""),
				new Aeroplane("a", "a", 1, 50),
				glasgowAirport,
				newYorkAirport,
				ZonedDateTime.of(2022, 2, 18, 16, 0, 0, 0, ZoneId.of("UTC+0")),
				controlTowers
		)));


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

		final var stubJoinerSubscriberData = stubJoinerSubscriber.receivedData;
		Assertions.assertEquals(1, stubJoinerSubscriberData.size());
		Assertions.assertEquals("E", stubJoinerSubscriberData.get(0).flightStatus().getCurrentControlTower().code);
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


		var glasgowAirport = new Airport("G", "Glasgow Airport", new GeodeticCoordinate(55.87, -4.43));
		var edinburghAirport = new Airport("E", "Edinburgh Airport", new GeodeticCoordinate(55.95, -3.19));
		var londonAirport = new Airport("L", "London Airport", new GeodeticCoordinate(51.47, -0.46));
		var newYorkAirport = new Airport("NY", "New York Airport", new GeodeticCoordinate(40.71, -74.01));

		var joiner = new FlightJoiner();

		final var airports = List.of(glasgowAirport, edinburghAirport, londonAirport, newYorkAirport);
		final var controlTowers = airports.stream().map(airport -> airport.controlTower).toList();
		controlTowers.forEach(tower -> tower.registerSubscriber(joiner));

		final var controlTowerThreads = airports.stream().map(airport -> new Thread(airport.controlTower)).toList();
		controlTowerThreads.forEach(Thread::start);

		var stubJoinerSubscriber = new StubFlightJoinerSubscriber();
		joiner.registerSubscriber(stubJoinerSubscriber);

		var tracker = new Thread(new FlightTracker(Flight.buildWithSerialNumber("001",
				new Airline("", ""),
				new Aeroplane("a", "a", 1, 50),
				glasgowAirport,
				newYorkAirport,
				ZonedDateTime.of(2022, 2, 18, 16, 0, 0, 0, ZoneId.of("UTC+0")),
				controlTowers
		)));


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

		final var stubJoinerSubscriberData = stubJoinerSubscriber.receivedData;
		Assertions.assertEquals(1, stubJoinerSubscriberData.size());
		Assertions.assertEquals("L", stubJoinerSubscriberData.get(0).flightStatus().getCurrentControlTower().code);
		Assertions.assertEquals(53.233916335001446, stubJoinerSubscriberData.get(0).flightStatus().getCurrentPosition().latitude());
		Assertions.assertEquals(-1.4620081301856966, stubJoinerSubscriberData.get(0).flightStatus().getCurrentPosition().longitude());
	}

	@Test
	void testJoinMultipleFlightCrossingSameControlTowers() {
		/**
		 * Tests what happens when multiple flights travels between control towers.
		 */

		// Configure thread timing for test case.
		FlightSimulationThreadManagement.setFlightSimulationFrequency(0.05); // Flight travelling between G-E within test duration.
		FlightSimulationThreadManagement.setThreadFrequency(1.9);
		FlightSimulationThreadManagement.setGuiUpdateFrequency(0.508);


		var glasgowAirport = new Airport("G", "Glasgow Airport", new GeodeticCoordinate(55.87, -4.43));
		var edinburghAirport = new Airport("E", "Edinburgh Airport", new GeodeticCoordinate(55.95, -3.19));
		var londonAirport = new Airport("L", "London Airport", new GeodeticCoordinate(51.47, -0.46));
		var newYorkAirport = new Airport("NY", "New York Airport", new GeodeticCoordinate(40.71, -74.01));

		var joiner = new FlightJoiner();

		final var airports = List.of(glasgowAirport, edinburghAirport, londonAirport, newYorkAirport);
		final var controlTowers = airports.stream().map(airport -> airport.controlTower).toList();
		controlTowers.forEach(tower -> tower.registerSubscriber(joiner));

		final var controlTowerThreads = airports.stream().map(airport -> new Thread(airport.controlTower)).toList();
		controlTowerThreads.forEach(Thread::start);

		var stubJoinerSubscriber = new StubFlightJoinerSubscriber();
		joiner.registerSubscriber(stubJoinerSubscriber);

		var flightATracker = new Thread(new FlightTracker(new Flight("FA",
				new Airline("", ""),
				new Aeroplane("a", "a", 1, 50),
				glasgowAirport,
				newYorkAirport,
				ZonedDateTime.of(2022, 2, 18, 16, 0, 0, 0, ZoneId.of("UTC+0")),
				controlTowers
		)));

		var flightBTracker = new Thread(new FlightTracker(new Flight("FB",
				new Airline("", ""),
				new Aeroplane("a", "a", 1, 50),
				glasgowAirport,
				newYorkAirport,
				ZonedDateTime.of(2022, 2, 18, 16, 0, 0, 0, ZoneId.of("UTC+0")),
				controlTowers
		)));


		var joinerThread = new Thread(joiner);

		flightATracker.start();
		flightBTracker.start();

		joinerThread.start();
		try {
			Thread.sleep(2000);
			flightATracker.stop();
			flightBTracker.stop();
			joinerThread.stop();
			controlTowerThreads.forEach(Thread::stop);

		} catch (Exception e) {
			e.printStackTrace();
		}

		final var stubJoinerSubscriberData = stubJoinerSubscriber.receivedData;
		Assertions.assertEquals(2, stubJoinerSubscriberData.size());
		Assertions.assertEquals("E", stubJoinerSubscriberData.get(0).flightStatus().getCurrentControlTower().code);
		Assertions.assertEquals(55.91264599913692, stubJoinerSubscriberData.get(0).flightStatus().getCurrentPosition().latitude());
		Assertions.assertEquals(-3.7937437059544337, stubJoinerSubscriberData.get(0).flightStatus().getCurrentPosition().longitude());

		Assertions.assertEquals("E", stubJoinerSubscriberData.get(1).flightStatus().getCurrentControlTower().code);
		Assertions.assertEquals(55.91264599913692, stubJoinerSubscriberData.get(1).flightStatus().getCurrentPosition().latitude());
		Assertions.assertEquals(-3.7937437059544337, stubJoinerSubscriberData.get(1).flightStatus().getCurrentPosition().longitude());

	}

	private static class StubFlightJoinerSubscriber implements Subscriber<Collection<Flight>> {
		public ArrayList<Flight> receivedData = new ArrayList<>();

		public void callback(Collection<Flight> data) {
			receivedData.addAll(data);
		}
	}
}
