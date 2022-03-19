package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.io.FlightDataFileHandlerException;
import com.github.adamtmalek.flightsimulator.models.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class FlightDataHandlerTest extends TestSuite {
	@Test
	void testReadFilesFromSingleDirectory() {
		var controller = new FlightDataHandlerImpl();
		try {
			final var flightData = controller.readFlightData(getPathFromResources("flight-data"));

			// Check each array has been populated. More robust checking of file-reading is outside of this tests code.
			Assertions.assertTrue(flightData.flights().stream().anyMatch(v -> v.flightID().equals("OK420")));
			Assertions.assertTrue(flightData.aeroplanes().stream().anyMatch(v -> v.model().equals("A330")));
			Assertions.assertTrue(flightData.airports().stream().anyMatch(v -> v.code.equals("CDG")));
			Assertions.assertTrue(flightData.airlines().stream().anyMatch(v -> v.code().equals("AA")));

		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	@Test
	void testReadFilesFromSpecificPaths() {
		var controller = new FlightDataHandlerImpl();
		try {
			final var flightData = controller.readFlightData(
							getPathFromResources("flight-data/airports.csv"),
							getPathFromResources("flight-data/aeroplanes.csv"),
							getPathFromResources("flight-data/airlines.csv"),
							getPathFromResources("flight-data/flights.csv"));
			// Check each array has been populated. More robust checking of file-reading is outside of this tests code.
			Assertions.assertTrue(flightData.flights().stream().anyMatch(v -> v.flightID().equals("OK420")));
			Assertions.assertTrue(flightData.aeroplanes().stream().anyMatch(v -> v.model().equals("A330")));
			Assertions.assertTrue(flightData.airports().stream().anyMatch(v -> v.code.equals("CDG")));
			Assertions.assertTrue(flightData.airlines().stream().anyMatch(v -> v.code().equals("AA")));

		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Test
	void testWriteAirlineReports() {

		var controller = new FlightDataHandlerImpl();

		// Must give controller airlines and flights in order to filter, requiring file-reading.
		try {
			controller.readFlightData(getPathFromResources("flight-data"));
		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}

		try {
			var generatedDirectory = Files.createTempDirectory("generated-airline-reports");

			controller.writeAirlineReports(generatedDirectory);

			// Check American Airlines
			final var expectedAAFile = new File(getPathFromResources("airline-reports/American Airlines.csv").toUri());
			final var actualAAFile = new File(generatedDirectory.resolve("American Airlines.csv").toUri());
			org.assertj.core.api.Assertions.assertThat(actualAAFile)
					.hasSameTextualContentAs(expectedAAFile);

			// Check Czech Airlines
			final var expectedOKFile = new File(getPathFromResources("airline-reports/Czech Airlines.csv").toUri());
			final var actualOKFile = new File(generatedDirectory.resolve("Czech Airlines.csv").toUri());
			org.assertj.core.api.Assertions.assertThat(actualOKFile)
					.hasSameTextualContentAs(expectedOKFile);

			// Check British Airways
			final var expectedBAFile = new File(getPathFromResources("airline-reports/British Airways.csv").toUri());
			final var actualBAFile = new File(generatedDirectory.resolve("British Airways.csv").toUri());
			org.assertj.core.api.Assertions.assertThat(actualBAFile)
					.hasSameTextualContentAs(expectedBAFile);


		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	void testWriteFlightData() {
		var controller = new FlightDataHandlerImpl();

		// Must give controller airlines and flights in order to filter, requiring file-reading.
		try {
			controller.readFlightData(getPathFromResources("flight-data"));
		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}

		try {
			var generatedDirectory = Files.createTempDirectory("saved-flight-data");
			controller.writeFlightData(generatedDirectory);

			// Check Flights
			final var expectedFlightsFile = new File(getPathFromResources("flight-data/flights.csv").toUri()).toPath();
			final var actualFlightsFile = new File(generatedDirectory.resolve("flights.csv").toUri()).toPath();
			CsvFileAssert.assertThat(actualFlightsFile, ";")
					.hasTheSameContentAs(expectedFlightsFile);

			// Check Airlines
			final var expectedAirlinesFile = new File(getPathFromResources("flight-data/airlines.csv").toUri()).toPath();
			final var actualAirlinesFile = new File(generatedDirectory.resolve("airlines.csv").toUri()).toPath();
			CsvFileAssert.assertThat(expectedAirlinesFile, ";")
					.hasTheSameContentAs(actualAirlinesFile);

			// Check Airports
			final var expectedAirportsFile = new File(getPathFromResources("flight-data/airports.csv").toUri()).toPath();
			final var actualAirportsFile = new File(generatedDirectory.resolve("airports.csv").toUri()).toPath();
			CsvFileAssert.assertThat(expectedAirportsFile, ";")
					.hasTheSameContentAs(actualAirportsFile);

			// Check Aeroplanes
			final var expectedAeroplanesFile = new File(getPathFromResources("flight-data/aeroplanes.csv").toUri()).toPath();
			final var actualAeroplanesFile = new File(generatedDirectory.resolve("aeroplanes.csv").toUri()).toPath();
			CsvFileAssert.assertThat(expectedAeroplanesFile, ";")
					.hasTheSameContentAs(actualAeroplanesFile);

		} catch (IOException | FlightDataFileHandlerException e) {
			throw new RuntimeException(e);
		}

	}

	@Test
	void testAddFlight() {
		final var flight = Flight.buildWithFlightId("newFlightID",
				new Airline("a", "a"),
				new Aeroplane("a", "a", 1, 50),
				new Airport("G", "Glasgow Airport", new GeodeticCoordinate(55.87, -4.43)),
				new Airport("NY", "New York Airport", new GeodeticCoordinate(40.71, -74.01)),
				ZonedDateTime.of(2022, 2, 18, 16, 0, 0, 0, ZoneId.of("UTC+0")),
				new ArrayList<>());

		var handler = new FlightDataHandlerImpl();
		handler.addFlight(flight);

		//noinspection OptionalGetWithoutIsPresent
		Assertions.assertEquals(flight, handler.getFlightData().flights().stream().findFirst().get());
	}

	@Test
	void testRemoveFlight() {
		final var flight = Flight.buildWithFlightId("newFlightID",
				new Airline("a", "a"),
				new Aeroplane("a", "a", 1, 50),
				new Airport("G", "Glasgow Airport", new GeodeticCoordinate(55.87, -4.43)),
				new Airport("NY", "New York Airport", new GeodeticCoordinate(40.71, -74.01)),
				ZonedDateTime.of(2022, 2, 18, 16, 0, 0, 0, ZoneId.of("UTC+0")),
				new ArrayList<>());

		var handler = new FlightDataHandlerImpl();
		handler.addFlight(flight);

		Assertions.assertEquals(1, handler.getFlightData().flights().size());
		handler.removeFlight(flight);
		Assertions.assertEquals(0, handler.getFlightData().flights().size());

	}

	@Test
	void testEditFlight() {
		final var originalFlight = Flight.buildWithFlightId("001",
				new Airline("a", "a"),
				new Aeroplane("a", "a", 1, 50),
				new Airport("G", "Glasgow Airport", new GeodeticCoordinate(55.87, -4.43)),
				new Airport("NY", "New York Airport", new GeodeticCoordinate(40.71, -74.01)),
				ZonedDateTime.of(2022, 2, 18, 16, 0, 0, 0, ZoneId.of("UTC+0")),
				new ArrayList<>());
		final var changedFlight = Flight.buildWithFlightId("002",
				new Airline("a", "a"),
				new Aeroplane("a", "a", 1, 50),
				new Airport("G", "Glasgow Airport", new GeodeticCoordinate(55.87, -4.43)),
				new Airport("NY", "New York Airport", new GeodeticCoordinate(40.71, -74.01)),
				ZonedDateTime.of(2022, 2, 18, 16, 0, 0, 0, ZoneId.of("UTC+0")),
				new ArrayList<>());

		final var handler = new FlightDataHandlerImpl();
		handler.addFlight(originalFlight);
		handler.editFlight(originalFlight, changedFlight);

		//noinspection OptionalGetWithoutIsPresent
		Assertions.assertEquals(changedFlight, handler.getFlightData().flights().stream().findFirst().get());
	}
}
