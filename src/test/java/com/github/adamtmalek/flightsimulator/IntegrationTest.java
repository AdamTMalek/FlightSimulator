package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.models.*;
import com.github.adamtmalek.flightsimulator.models.io.FlightData;
import com.github.adamtmalek.flightsimulator.models.io.FlightDataFileHandlerException;
import com.github.adamtmalek.flightsimulator.models.io.TestSuite;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Scanner;

class IntegrationTest extends TestSuite {

	static Path genTmpDir() {
		try {
			return Files.createTempDirectory("integration-test");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	void loadFlightsAddWriteReadDeleteWriteRead() {
		// Temporary directory for this test
		Path tempDir = genTmpDir();

		// Initialise controller and load flight data
		FlightTrackerController mainController = new FlightTrackerController();

		FlightData flightData;
		try {
			flightData = mainController.readFlightData(getPathFromResources("flight-data")).getFlightData();
		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}

		// should be the first flight in the test file
		Assertions.assertEquals("OK420", flightData.flights().get(0).flightID());

		// Make new flight and try adding to controller
		Flight testFlight = Flight.buildWithFlightId("123ID",
				new Airline("AA", "name"),
				new Aeroplane("A330", "manufacturer", 2000, 15),
				new Airport("CDG", "start",
						new GeodeticCoordinate(1, 2)),
				new Airport("LHR", "end",
						new GeodeticCoordinate(200, 100)),
				ZonedDateTime.of(2022, 3, 1, 16, 0, 0, 0, ZoneId.of("UTC+0")),
				new ArrayList<Airport.ControlTower>());
		mainController.addFlight(testFlight);

		// now we check if the last flight matches the one we just added
		Assertions.assertEquals("123ID", flightData.flights().get(flightData.flights().size() - 1).flightID());

		// write this to a file
		try {
			mainController.writeFlightData(tempDir);
		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}

		// load the file on a new controller/dataset and check if it matches
		FlightTrackerController readTestController = new FlightTrackerController();
		FlightData readTestFD;
		try {
			readTestFD = readTestController.readFlightData(tempDir).getFlightData(); // Is this how the tempDir works?
		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}
		Assertions.assertEquals("OK420", readTestFD.flights().get(0).flightID());
		Assertions.assertEquals("123ID", readTestFD.flights().get(readTestFD.flights().size() - 1).flightID());

		// remove the testFlight/last flight from the controller assert if it worked
		mainController.removeFlight(flightData.flights().size() - 1);
		// last flight in flightData should be BA605 now
		Assertions.assertEquals("BA605", flightData.flights().get(flightData.flights().size() - 1).flightID());

		// write the flights down again and check if it still matches
		try {
			mainController.writeFlightData(tempDir);
		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e);
		}

		// load the file on a new controller/dataset and check if it matches
		FlightTrackerController readTestController2 = new FlightTrackerController();
		FlightData readTestFD2;
		try {
			readTestFD2 = readTestController2.readFlightData(tempDir).getFlightData(); // Is this how the tempDir works?
		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}
		Assertions.assertEquals("OK420", readTestFD2.flights().get(0).flightID());
		Assertions.assertEquals("BA605", readTestFD2.flights().get(readTestFD2.flights().size() - 1).flightID());
	}

	@Test
	void loadThreeNewFlightsAndWriteReportTest() {
		// Temporary directory for this test
		Path tmpDir = genTmpDir();

		// Initialise controller and load flight data
		FlightTrackerController mainController = new FlightTrackerController();
		FlightData flightData;
		try {
			flightData = mainController.readFlightData(getPathFromResources("flight-data")).getFlightData();
		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}

		// Generate some test flights
		Flight testF1 = Flight.buildWithFlightId("123ID",
			new Airline("AA", "name"),
			new Aeroplane("A330", "manufacturer", 2000, 15),
			new Airport("CDG", "start",
				new GeodeticCoordinate(1, 2)),
			new Airport("LHR", "end",
				new GeodeticCoordinate(200, 100)),
			ZonedDateTime.of(2022, 3, 1, 16, 0, 0, 0, ZoneId.of("UTC+0")),
			new ArrayList<Airport.ControlTower>());
		Flight testF2 = Flight.buildWithFlightId("1234ID",
			new Airline("AA", "name"),
			new Aeroplane("A330", "manufacturer", 50, 15),
			new Airport("CDG", "start",
				new GeodeticCoordinate(1, 2)),
			new Airport("LHR", "end",
				new GeodeticCoordinate(300, 150)),
			ZonedDateTime.of(2022, 4, 2, 16, 0, 0, 0, ZoneId.of("UTC+0")),
			new ArrayList<Airport.ControlTower>());
		Flight testF3 = Flight.buildWithFlightId("1235ID",
			new Airline("BA", "name"),
			new Aeroplane("A330", "manufacturer", 200, 15),
			new Airport("LHR", "start",
				new GeodeticCoordinate(300, 2)),
			new Airport("EDI", "end",
				new GeodeticCoordinate(200, -300.2)),
			ZonedDateTime.of(2022, 4, 2, 16, 0, 0, 0, ZoneId.of("UTC+0")),
			new ArrayList<Airport.ControlTower>());

		mainController.addFlight(testF1);
		mainController.addFlight(testF2);
		mainController.addFlight(testF3);

		mainController.writeAirlineReports(tmpDir);

		System.out.println("American Airlines:");
		try {
			File f = new File(tmpDir.resolve("American Airlines.csv").toString());
			Scanner reader = new Scanner(f);
			while (reader.hasNextLine()) {
				System.out.println(reader.nextLine());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("British Airways:");
		try {
			File f = new File(tmpDir.resolve("British Airways.csv").toString());
			Scanner reader = new Scanner(f);
			while (reader.hasNextLine()) {
				System.out.println(reader.nextLine());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("Czech Airlines:");
		try {
			File f = new File(tmpDir.resolve("Czech Airlines.csv").toString());
			Scanner reader = new Scanner(f);
			while (reader.hasNextLine()) {
				System.out.println(reader.nextLine());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
}
