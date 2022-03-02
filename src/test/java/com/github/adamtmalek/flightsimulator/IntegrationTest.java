package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.models.*;
import com.github.adamtmalek.flightsimulator.models.io.FileHandlerException;
import com.github.adamtmalek.flightsimulator.models.io.FlightData;
import com.github.adamtmalek.flightsimulator.models.io.TestSuite;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

class IntegrationTest extends TestSuite {

	@Test
	void loadFlightsAddWriteReadDeleteWriteRead() {
		// Temporary directory for this test
		Path tempDir;
		try {
			tempDir = Files.createTempDirectory("integration-testing");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		// Initialise controller and load flight data
		FlightTrackerController mainController = new FlightTrackerController();

		FlightData flightData;
		try {
			flightData = mainController.readFlightData(getPathFromResources("flight-data")).getFlightData();
		} catch (FileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}

		// should be the first flight in the test file
		Assertions.assertEquals("OK420", flightData.flights().get(0).flightID());

		// Make new flight and try adding to controller
		Flight testFlight = Flight.buildWithFlightId("123ID",
				new Airline("code","name"),
				new Aeroplane("model", "manufacturer", 2000, 15),
				new Airport("code1", "start",
						new GeodeticCoordinate(1, 2)),
				new Airport("code2", "end",
						new GeodeticCoordinate(200, 100)),
				ZonedDateTime.of(2022, 3, 1, 16, 0, 0, 0, ZoneId.of("UTC+0")),
				new ArrayList<Airport.ControlTower>());
		mainController.addFlight(testFlight);

		// now we check if the last flight matches the one we just added
		Assertions.assertEquals("123ID", flightData.flights().get(flightData.flights().size()-1).flightID());

		// write this to a file
		mainController.writeFlightData(tempDir);

		// load the file on a new controller/dataset and check if it matches
		FlightTrackerController readTestController = new FlightTrackerController();
		FlightData readTestFD;
		try {
			readTestFD = readTestController.readFlightData(tempDir).getFlightData(); // Is this how the tempDir works?
		} catch (FileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}
		Assertions.assertEquals("OK420", readTestFD.flights().get(0).flightID());
		Assertions.assertEquals("123ID", readTestFD.flights().get(readTestFD.flights().size()-1).flightID());

		// remove the testFlight/last flight from the controller assert if it worked
		mainController.removeFlight(flightData.flights().size()-1);
		// last flight in flightData should be BA605 now
		Assertions.assertEquals("BA605", flightData.flights().get(flightData.flights().size()-1).flightID());

		// write the flights down again and check if it still matches
		mainController.writeFlightData(tempDir);

		// load the file on a new controller/dataset and check if it matches
		FlightTrackerController readTestController2 = new FlightTrackerController();
		FlightData readTestFD2;
		try {
			readTestFD2 = readTestController2.readFlightData(tempDir).getFlightData(); // Is this how the tempDir works?
		} catch (FileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}
		Assertions.assertEquals("OK420", readTestFD2.flights().get(0).flightID());
		Assertions.assertEquals("BA605", readTestFD2.flights().get(readTestFD2.flights().size()-1).flightID());
	}


	@Test
	void loadFlightsDeleteReadAddWriteReadWithNewControllers() {
		Assertions.fail("this isnt implemented");
		// Temporary directory for this test

		// Initialise controller and load flight data

		// Find the location of two specific flights and remove only those

		// Write and check if removed successfully

		// Make three flights and add to controller

		// Write this to a file

		// Write and check if they saved properly

		// Load the file on a new controller/dataset and check if it matches
	}

}
