package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.interfaces.Subscriber;
import com.github.adamtmalek.flightsimulator.models.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class FlightJoinerTest {


	@Test
	void testJoinFlightAfterPassingControlTower() {

		// Configure thread timing for test case.
		FlightSimulationThreadManagement.setFlightSimulationFrequency(0.005);
		FlightSimulationThreadManagement.setThreadFrequency(2);
		FlightSimulationThreadManagement.setGuiUpdateFrequency(0.508);


		var glasgowAirport = new Airport("G", "Glasgow Airport", new GeodeticCoordinate(55.87, -4.43));
		var edinburghAirport = new Airport("E", "Edinburgh Airport", new GeodeticCoordinate(55.95, -3.19));
		var londonAirport = new Airport("L", "London Airport", new GeodeticCoordinate(51.47, -0.46));
		var newYorkAirport = new Airport("NY", "New York Airport", new GeodeticCoordinate(40.71, -74.01));

		var joiner = new FlightJoiner();

		glasgowAirport.controlTower.registerSubscriber(joiner);
		edinburghAirport.controlTower.registerSubscriber(joiner);
		londonAirport.controlTower.registerSubscriber(joiner);
		newYorkAirport.controlTower.registerSubscriber(joiner);

		var stubJoinerSubscriber = new StubFlightJoinerSubscriber();
		joiner.registerSubscriber(stubJoinerSubscriber);

		var tracker = new Thread(new FlightTracker(Flight.buildWithSerialNumber("001",
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
				}})));


		var joinerThread = new Thread(joiner);

		tracker.start();
		joinerThread.start();
		try {
			Thread.sleep(2000);
			tracker.stop();
			joinerThread.stop();

		} catch (Exception e) {
			e.printStackTrace();
		}

		final var stubJoinerSubscriberData = stubJoinerSubscriber.receivedData;
		Assertions.assertEquals(1, stubJoinerSubscriberData.size());
		Assertions.assertEquals("L", stubJoinerSubscriberData.get(0).controlTowerCode());
		Assertions.assertEquals(53.233916335001446, stubJoinerSubscriberData.get(0).flight().estimatedPosition().latitude());
		Assertions.assertEquals(-1.4620081301856966, stubJoinerSubscriberData.get(0).flight().estimatedPosition().longitude());

	}

	private class StubFlightJoinerSubscriber implements Subscriber<ArrayList<DirectedFlight>> {
		public ArrayList<DirectedFlight> receivedData = new ArrayList<DirectedFlight>();

		public void callback(ArrayList<DirectedFlight> data) {
			receivedData.addAll(data);
		}
	}
}
