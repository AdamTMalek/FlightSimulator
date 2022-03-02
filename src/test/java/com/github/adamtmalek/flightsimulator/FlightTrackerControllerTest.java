package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.models.*;
import com.github.adamtmalek.flightsimulator.models.io.FlightDataFileHandlerException;
import com.github.adamtmalek.flightsimulator.models.io.TestSuite;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class FlightTrackerControllerTest extends TestSuite {


	@Test
	void readFilesFromSingleDirectory() {
		var controller = new FlightTrackerController();
		try {
			final var flightData = controller.readFlightData(getPathFromResources("flight-data")).getFlightData();

			// Check each array has been populated. More robust checking of file-reading is outside of this tests code.
			Assertions.assertEquals("OK420", flightData.flights().get(0).flightID());
			Assertions.assertEquals("A330", flightData.aeroplanes().get(0).model());
			Assertions.assertEquals("CDG", flightData.airports().get(0).code);
			Assertions.assertEquals("AA", flightData.airlines().get(0).code());

		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	@Test
	void readFilesFromSpecificPaths() {
		var controller = new FlightTrackerController();
		try {
			final var flightData = controller.readFlightData(
					getPathFromResources("flight-data/airports.csv"),
					getPathFromResources("flight-data/aeroplanes.csv"),
					getPathFromResources("flight-data/airlines.csv"),
					getPathFromResources("flight-data/flights.csv"))
				.getFlightData();
			// Check each array has been populated. More robust checking of file-reading is outside of this tests code.
			Assertions.assertEquals("OK420", flightData.flights().get(0).flightID());
			Assertions.assertEquals("A330", flightData.aeroplanes().get(0).model());
			Assertions.assertEquals("CDG", flightData.airports().get(0).code);
			Assertions.assertEquals("AA", flightData.airlines().get(0).code());

		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Test
	void writeAirlineReports() {

		var controller = new FlightTrackerController();

		// Must give controller airlines and flights in order to filter, requiring file-reading.
		try {
			final var flightData = controller.readFlightData(getPathFromResources("flight-data")).getFlightData();
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
	void writeFlightData() {
		var controller = new FlightTrackerController();

		// Must give controller airlines and flights in order to filter, requiring file-reading.
		try {
			controller.readFlightData(getPathFromResources("flight-data")).getFlightData();
		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}

		try {
			var generatedDirectory = Files.createTempDirectory("saved-flight-data");
			controller.writeFlightData(generatedDirectory);

			// Check Flights
			final var expectedFlightsFile = new File(getPathFromResources("flight-data/flights.csv").toUri());
			final var actualFlightsFile = new File(generatedDirectory.resolve("flights.csv").toUri());
			org.assertj.core.api.Assertions.assertThat(actualFlightsFile)
				.hasSameTextualContentAs(expectedFlightsFile);

			// Check Airlines
			final var expectedAirlinesFile = new File(getPathFromResources("flight-data/airlines.csv").toUri());
			final var actualAirlinesFile = new File(generatedDirectory.resolve("airlines.csv").toUri());
			org.assertj.core.api.Assertions.assertThat(actualAirlinesFile)
				.hasSameTextualContentAs(expectedAirlinesFile);

			// Check Airports
			final var expectedAirportsFile = new File(getPathFromResources("flight-data/airports.csv").toUri());
			final var actualAirportsFile = new File(generatedDirectory.resolve("airports.csv").toUri());
			org.assertj.core.api.Assertions.assertThat(actualAirportsFile)
				.hasSameTextualContentAs(expectedAirportsFile);

			// Check Aeroplanes
			final var expectedAiroplanesFile = new File(getPathFromResources("flight-data/aeroplanes.csv").toUri());
			final var actualAiroplanessFile = new File(generatedDirectory.resolve("aeroplanes.csv").toUri());
			org.assertj.core.api.Assertions.assertThat(actualAiroplanessFile)
				.hasSameTextualContentAs(expectedAiroplanesFile);

		} catch (IOException | FlightDataFileHandlerException e) {
			throw new RuntimeException(e);
		}

	}

	@Test
	void addFlight() {
		var controller = new FlightTrackerController();

		controller.addFlight(Flight.buildWithFlightId("newFlightID",
			new Airline("a", "a"),
			new Aeroplane("a", "a", 1, 50),
			new Airport("G", "Glasgow Airport", new GeodeticCoordinate(55.87, -4.43)),
			new Airport("NY", "New York Airport", new GeodeticCoordinate(40.71, -74.01)),
			ZonedDateTime.of(2022, 2, 18, 16, 0, 0, 0, ZoneId.of("UTC+0")),
			new ArrayList<Airport.ControlTower>()));

		final var addedFlight = controller.getFlightData().flights().get(0);
		Assertions.assertEquals("newFlightID", addedFlight.flightID());

	}

	@Test
	void removeFlight() {
		var controller = new FlightTrackerController();

		controller.addFlight(Flight.buildWithFlightId("newFlightID",
			new Airline("a", "a"),
			new Aeroplane("a", "a", 1, 50),
			new Airport("G", "Glasgow Airport", new GeodeticCoordinate(55.87, -4.43)),
			new Airport("NY", "New York Airport", new GeodeticCoordinate(40.71, -74.01)),
			ZonedDateTime.of(2022, 2, 18, 16, 0, 0, 0, ZoneId.of("UTC+0")),
			new ArrayList<Airport.ControlTower>()));

		Assertions.assertEquals(1, controller.getFlightData().flights().size());
		controller.removeFlight(0);
		Assertions.assertEquals(0, controller.getFlightData().flights().size());

	}

	@Test
	void editFlight() {
		var controller = new FlightTrackerController();

		controller.addFlight(Flight.buildWithFlightId("001",
			new Airline("a", "a"),
			new Aeroplane("a", "a", 1, 50),
			new Airport("G", "Glasgow Airport", new GeodeticCoordinate(55.87, -4.43)),
			new Airport("NY", "New York Airport", new GeodeticCoordinate(40.71, -74.01)),
			ZonedDateTime.of(2022, 2, 18, 16, 0, 0, 0, ZoneId.of("UTC+0")),
			new ArrayList<Airport.ControlTower>()));

		var changedFlight = Flight.buildWithFlightId("002",
			new Airline("a", "a"),
			new Aeroplane("a", "a", 1, 50),
			new Airport("G", "Glasgow Airport", new GeodeticCoordinate(55.87, -4.43)),
			new Airport("NY", "New York Airport", new GeodeticCoordinate(40.71, -74.01)),
			ZonedDateTime.of(2022, 2, 18, 16, 0, 0, 0, ZoneId.of("UTC+0")),
			new ArrayList<Airport.ControlTower>());

		final var originalFlight = controller.getFlightData().flights().get(0);

		Assertions.assertEquals("001", originalFlight.flightID());
		controller.editFlight(0, changedFlight);
		final var editedFlight = controller.getFlightData().flights().get(0);
		Assertions.assertEquals("002", editedFlight.flightID());

	}
}
