package com.github.adamtmalek.flightsimulator.models.io;

import com.github.adamtmalek.flightsimulator.io.FlightData;
import com.github.adamtmalek.flightsimulator.io.FlightDataFileHandler;
import com.github.adamtmalek.flightsimulator.io.FlightDataFileHandlerException;
import com.github.adamtmalek.flightsimulator.models.Flight;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;


class FlightDataFileHandlerTest extends TestSuite {
	private final Path flightDataPath = getPathFromResources("flight-data");
	private final ZonedDateTime dateTime = ZonedDateTime.of(2022, 1, 31, 12, 0, 0, 0, ZoneId.of("Europe/London"));

	@Test
	public void testFlightsAreReadCorrectly() {
		final FlightData flightData;
		try {
			final FlightDataFileHandler fileHandler = FlightDataFileHandler.withDefaultFileNames(flightDataPath);
			flightData = fileHandler.readFlightData();
		} catch (FlightDataFileHandlerException e) {
			Assertions.fail("Exception thrown" + e);
			return;
		}
		final var actual = flightData.flights();

		final var expected = new Flight[]{
				Flight.buildWithFlightId("OK420",
						flightData.airlines().stream().filter(v -> v.code().equals("OK")).findFirst().orElseThrow(),
						flightData.aeroplanes().stream().filter(v -> v.model().equals("B777")).findFirst().orElseThrow(),
						flightData.airports().stream().filter(v -> v.code.equals("CDG")).findFirst().orElseThrow(),
						flightData.airports().stream().filter(v -> v.code.equals("EDI")).findFirst().orElseThrow(),
						dateTime,
						Arrays.asList(
								flightData.airports().stream().filter(v -> v.code.equals("CDG")).findFirst().orElseThrow().controlTower,
								flightData.airports().stream().filter(v -> v.code.equals("EDI")).findFirst().orElseThrow().controlTower
						)
				),
				// BA605; A330; EDI; LHR; 15:01:2022; 06:00; EDI; CDG; LHR
				Flight.buildWithFlightId("BA605",
						flightData.airlines().stream().filter(v -> v.code().equals("BA")).findFirst().orElseThrow(),
						flightData.aeroplanes().stream().filter(v -> v.model().equals("A330")).findFirst().orElseThrow(),
						flightData.airports().stream().filter(v -> v.code.equals("EDI")).findFirst().orElseThrow(),
						flightData.airports().stream().filter(v -> v.code.equals("LHR")).findFirst().orElseThrow(),
						dateTime,
						Arrays.asList(
								flightData.airports().stream().filter(v -> v.code.equals("EDI")).findFirst().orElseThrow().controlTower,
								flightData.airports().stream().filter(v -> v.code.equals("CDG")).findFirst().orElseThrow().controlTower,
								flightData.airports().stream().filter(v -> v.code.equals("LHR")).findFirst().orElseThrow().controlTower
						)
				)
		};

		Assertions.assertThat(actual)
				.containsExactlyInAnyOrder(expected);
	}

	@Test
	public void testFlightsAreSavedCorrectly() {
		final FlightData flightData;
		try {
			final FlightDataFileHandler fileHandler = FlightDataFileHandler.withDefaultFileNames(flightDataPath);
			flightData = fileHandler.readFlightData();
		} catch (FlightDataFileHandlerException e) {
			Assertions.fail("Exception thrown" + e);
			return;
		}

		final var expectedFile = flightDataPath.resolve("flights.csv");
		final Path actual;
		try {
			final var tempOutputDir = Files.createTempDirectory("flightDataFileHandlerTest");
			final var fileHandler = FlightDataFileHandler.withDefaultFileNames(tempOutputDir);
			fileHandler.saveFlights(flightData);
			actual = tempOutputDir.resolve("flights.csv");
		} catch (IOException | FlightDataFileHandlerException e) {
			Assertions.fail("Exception thrown " + e);
			return;
		}

		Assertions.assertThat(actual)
				.hasSameTextualContentAs(expectedFile);
	}
}