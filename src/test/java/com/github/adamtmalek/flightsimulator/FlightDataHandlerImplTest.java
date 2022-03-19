package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.io.FlightData;
import com.github.adamtmalek.flightsimulator.io.FlightDataFileHandlerException;
import com.github.adamtmalek.flightsimulator.models.*;
import com.github.adamtmalek.flightsimulator.models.io.TestSuite;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

/**
 * TODO: This test does not do anything sensible, due to MVC restructuring.
 *  Currently the code here is left as most of it can be reused,
 *  but the test case probably needs to be split in two:
 *    - FileDataHandlerTest - GUI-less tests testing just the FlightDataHandler (unit tests)
 *    - integration test that includes the GUI
 *  Since that involves a significant amount of refactoring work, this test is disabled.
 *  Enabling it in this state will result in failed tests or exceptions.
 */
@Disabled
public class FlightDataHandlerImplTest extends TestSuite {
	private static final @NotNull Path tempDirectory = createTempDir();
	
	private static @NotNull Path createTempDir() {
		try {
			return Files.createTempDirectory("integration-test");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Test
	void loadFlightsAddWriteReadTest() {
		// Initialise controller and load flight data
		FlightDataHandler mainController = new FlightDataHandlerImpl();

		FlightData flightData;
		try {
			flightData = mainController.readFlightData(getPathFromResources("flight-data"));
		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}

		// should be the first flight in the test file
		Assertions.assertEquals("OK420", flightData.flights().get(0).flightID());

		// Make new flight and try adding to controller
		Flight testFlight = Flight.buildWithFlightId("AA123",
				new Airline("AA", "American Airlines"),
				new Aeroplane("A330", "manufacturer", 2000, 15),
				new Airport("CDG", "Paris Charles de Gaulle",
						new GeodeticCoordinate(1, 2)),
				new Airport("LHR", "Heathrow",
						new GeodeticCoordinate(200, 100)),
				ZonedDateTime.of(2022, 3, 1, 16, 0, 0, 0, ZoneId.of("UTC+0")),
				new ArrayList<Airport.ControlTower>());
		mainController.addFlight(testFlight);

		// now we check if the last flight matches the one we just added
		Assertions.assertEquals("AA123", flightData.flights().get(flightData.flights().size() - 1).flightID());

		// write this to a file
		try {
			mainController.writeFlightData(tempDirectory);
		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}

		// load the file on a new controller/dataset and check if it matches
		FlightDataHandler readTestController = new FlightDataHandlerImpl();
		FlightData readTestFD;
		try {
			readTestFD = readTestController.readFlightData(tempDirectory); // Is this how the tempDir works?
		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}
		Assertions.assertEquals("OK420", readTestFD.flights().get(0).flightID());
		Assertions.assertEquals("AA123", readTestFD.flights().get(readTestFD.flights().size() - 1).flightID());
	}

	@Test
	void loadFlightsDeleteWriteReadTest() {
		// Initialise controller and load flight data
		FlightDataHandler mainController = new FlightDataHandlerImpl();

		FlightData flightData;
		try {
			flightData = mainController.readFlightData(getPathFromResources("flight-data"));
		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}

		// should be the first flight in the test file
		Assertions.assertEquals("OK420", flightData.flights().get(0).flightID());


		// remove the testFlight/last flight from the controller assert if it worked
		mainController.removeFlight(flightData.flights().size() - 1);
		// last flight in flightData should be BA605 now
		Assertions.assertEquals("OK420", flightData.flights().get(flightData.flights().size() - 1).flightID());

		// write the flights down again and check if it still matches
		try {
			mainController.writeFlightData(tempDirectory);
		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e);
		}

		// load the file on a new controller/dataset and check if it matches
		FlightDataHandler readTestController2 = new FlightDataHandlerImpl();
		FlightData readTestFD2;
		try {
			readTestFD2 = readTestController2.readFlightData(tempDirectory); // Is this how the tempDir works?
		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}
		Assertions.assertEquals("OK420", readTestFD2.flights().get(0).flightID());
		Assertions.assertEquals("OK420", readTestFD2.flights().get(readTestFD2.flights().size() - 1).flightID());
	}

	@Test
	void loadFlightsAddWriteReadDeleteWriteReadTest() {
		// Initialise controller and load flight data
		FlightDataHandler mainController = new FlightDataHandlerImpl();

		FlightData flightData;
		try {
			flightData = mainController.readFlightData(getPathFromResources("flight-data"));
		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}

		// should be the first flight in the test file
		Assertions.assertEquals("OK420", flightData.flights().get(0).flightID());

		// Make new flight and try adding to controller
		Flight testFlight = Flight.buildWithFlightId("AA123",
				new Airline("AA", "American Airlines"),
				new Aeroplane("A330", "manufacturer", 2000, 15),
				new Airport("CDG", "Paris Charles de Gaulle",
						new GeodeticCoordinate(1, 2)),
				new Airport("LHR", "Heathrow",
						new GeodeticCoordinate(200, 100)),
				ZonedDateTime.of(2022, 3, 1, 16, 0, 0, 0, ZoneId.of("UTC+0")),
				new ArrayList<Airport.ControlTower>());
		mainController.addFlight(testFlight);

		// now we check if the last flight matches the one we just added
		Assertions.assertEquals("AA123", flightData.flights().get(flightData.flights().size() - 1).flightID());

		// write this to a file
		try {
			mainController.writeFlightData(tempDirectory);
		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}

		// load the file on a new controller/dataset and check if it matches
		FlightDataHandler readTestController = new FlightDataHandlerImpl();
		FlightData readTestFD;
		try {
			readTestFD = readTestController.readFlightData(tempDirectory); // Is this how the tempDir works?
		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}
		Assertions.assertEquals("OK420", readTestFD.flights().get(0).flightID());
		Assertions.assertEquals("AA123", readTestFD.flights().get(readTestFD.flights().size() - 1).flightID());

		// remove the testFlight/last flight from the controller assert if it worked
		mainController.removeFlight(flightData.flights().size() - 1);
		// last flight in flightData should be BA605 now
		Assertions.assertEquals("BA605", flightData.flights().get(flightData.flights().size() - 1).flightID());

		// write the flights down again and check if it still matches
		try {
			mainController.writeFlightData(tempDirectory);
		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e);
		}

		// load the file on a new controller/dataset and check if it matches
		FlightDataHandler readTestController2 = new FlightDataHandlerImpl();
		FlightData readTestFD2;
		try {
			readTestFD2 = readTestController2.readFlightData(tempDirectory); // Is this how the tempDir works?
		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}
		Assertions.assertEquals("OK420", readTestFD2.flights().get(0).flightID());
		Assertions.assertEquals("BA605", readTestFD2.flights().get(readTestFD2.flights().size() - 1).flightID());
	}

	@Test
	void addFlight() {
		// Init controller
		var controller = new FlightDataHandlerImpl();

		try {
			controller.readFlightData(getPathFromResources("flight-data"));
		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}

		final var addedFlight = controller.getFlightData().flights().get(2);
		Assertions.assertEquals("AA777", addedFlight.flightID());
		Assertions.assertEquals("A330", addedFlight.aeroplane().model());

		Assertions.assertEquals("Airbus", addedFlight.aeroplane().manufacturer());
		Assertions.assertEquals("AA", addedFlight.airline().code());
		Assertions.assertEquals("American Airlines", addedFlight.airline().name());

		Assertions.assertEquals("EDI", addedFlight.controlTowersToCross().get(0).code);
		Assertions.assertEquals("LHR", addedFlight.controlTowersToCross().get(1).code);
		Assertions.assertEquals("CDG", addedFlight.controlTowersToCross().get(2).code);

		Assertions.assertEquals("EDI", addedFlight.departureAirport().code);
		Assertions.assertEquals("CDG", addedFlight.destinationAirport().code);
	}

	@Test
	void addDuplicateFlight() {
		// Init controller
		var controller = new FlightDataHandlerImpl();

		final var addedFlight = controller.getFlightData().flights().get(2);
		Assertions.assertEquals("AA777", addedFlight.flightID());
		Assertions.assertEquals("A330", addedFlight.aeroplane().model());

		Assertions.assertEquals("Airbus", addedFlight.aeroplane().manufacturer());
		Assertions.assertEquals("AA", addedFlight.airline().code());
		Assertions.assertEquals("American Airlines", addedFlight.airline().name());

		Assertions.assertEquals("EDI", addedFlight.controlTowersToCross().get(0).code);
		Assertions.assertEquals("LHR", addedFlight.controlTowersToCross().get(1).code);
		Assertions.assertEquals("CDG", addedFlight.controlTowersToCross().get(2).code);

		Assertions.assertEquals("EDI", addedFlight.departureAirport().code);
		Assertions.assertEquals("CDG", addedFlight.destinationAirport().code);
	}
}
