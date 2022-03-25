package com.github.adamtmalek.flightsimulator.collections;

import com.github.adamtmalek.flightsimulator.models.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class SynchronizedQueueTest {

	private SynchronizedQueue<Flight> testSynchronizedQueue;

	@Test
	void testQueuePush() throws InterruptedException {

		testSynchronizedQueue = new SynchronizedQueue<>();

		final var departureAirport = new Airport("G", "Glasgow Airport", new GeodeticCoordinate(55.87, -4.43));
		final var destinationAirport = new Airport("E", "Edinburgh Airport", new GeodeticCoordinate(55.95, -3.19));

		var flight1 = Flight.buildWithSerialNumber("001",
				new Airline("TEST", ""),
				new Aeroplane("a", "a", 1, 50),
				departureAirport,
				destinationAirport,
				ZonedDateTime.of(2022, 2, 18, 16, 0, 0, 0, ZoneId.of("UTC+0")),
				List.of(departureAirport.controlTower, destinationAirport.controlTower));

		var flight2 = Flight.buildWithSerialNumber("002",
				new Airline("TEST", ""),
				new Aeroplane("a", "a", 1, 50),
				departureAirport,
				destinationAirport,
				ZonedDateTime.of(2022, 2, 18, 16, 0, 0, 0, ZoneId.of("UTC+0")),
				List.of(departureAirport.controlTower, destinationAirport.controlTower));

		testSynchronizedQueue.push(flight1);
		testSynchronizedQueue.push(flight2);

		int queueSize = testSynchronizedQueue.queue.size();

		Assertions.assertEquals(2, queueSize);

	}

	@Test
	void testQueuePoll() throws InterruptedException {

		testSynchronizedQueue = new SynchronizedQueue<>();

		final var departureAirport = new Airport("G", "Glasgow Airport", new GeodeticCoordinate(55.87, -4.43));
		final var destinationAirport = new Airport("E", "Edinburgh Airport", new GeodeticCoordinate(55.95, -3.19));

		var flight1 = Flight.buildWithSerialNumber("001",
				new Airline("TEST", ""),
				new Aeroplane("a", "a", 1, 50),
				departureAirport,
				destinationAirport,
				ZonedDateTime.of(2022, 2, 18, 16, 0, 0, 0, ZoneId.of("UTC+0")),
				List.of(departureAirport.controlTower, destinationAirport.controlTower));

		var flight2 = Flight.buildWithSerialNumber("002",
				new Airline("TEST", ""),
				new Aeroplane("a", "a", 1, 50),
				departureAirport,
				destinationAirport,
				ZonedDateTime.of(2022, 2, 18, 16, 0, 0, 0, ZoneId.of("UTC+0")),
				List.of(departureAirport.controlTower, destinationAirport.controlTower));

		testSynchronizedQueue.push(flight1);
		testSynchronizedQueue.push(flight2);
		testSynchronizedQueue.poll();

		int queueSize = testSynchronizedQueue.queue.size();

		Assertions.assertEquals(1, queueSize);
	}

}
