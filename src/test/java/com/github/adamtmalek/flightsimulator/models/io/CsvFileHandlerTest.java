package com.github.adamtmalek.flightsimulator.models.io;

import com.github.adamtmalek.flightsimulator.models.Aeroplane;
import com.github.adamtmalek.flightsimulator.models.Airline;
import com.github.adamtmalek.flightsimulator.models.Airport;
import com.github.adamtmalek.flightsimulator.models.GeodeticCoordinate;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class CsvFileHandlerTest {
	private static final @NotNull CsvFileHandler fileHandler = new CsvFileHandler(";");

	@Test
	public void testAeroplanesAreReadCorrectly() {
		final List<Aeroplane> actual;
		try {
			actual = fileHandler.readFile(
				getPathFromTestResources("aeroplanes.csv"),
				Aeroplane.class
			);
		} catch (IOException | FileHandlerException e) {
			Assertions.fail(String.format("Exception thrown: %s", e));
			return;
		}

		final var expected = new Aeroplane[]{
			new Aeroplane("B777", "Boeing", 875.0, 952.78815),
			new Aeroplane("A330", "Airbus", 800.0, 768.439),
			new Aeroplane("A350", "Airbus", 900.0, 747.24)
		};

		Assertions.assertThat(actual)
			.containsExactlyInAnyOrder(expected);
	}

	@Test
	public void testAirlinesAreReadCorrectly() {
		final List<Airline> actual;
		try {
			actual = fileHandler.readFile(
				getPathFromTestResources("airlines.csv"),
				Airline.class
			);
		} catch (IOException | FileHandlerException e) {
			Assertions.fail(String.format("Exception thrown: %s", e));
			return;
		}

		final var expected = new Airline[]{
			new Airline("AA", "American Airlines"),
			new Airline("OK", "Czech Airlines"),
			new Airline("BA", "British Airways")
		};

		Assertions.assertThat(actual)
			.containsExactlyInAnyOrder(expected);
	}

	@Test
	public void testAirportsAreReadCorrectly() {
		final List<Airport> actual;
		try {
			actual = fileHandler.readFile(
				getPathFromTestResources("airports.csv"),
				Airport.class
			);
		} catch (IOException | FileHandlerException e) {
			Assertions.fail(String.format("Exception thrown: %s", e));
			return;
		}

		final var expected = new Airport[]{
			new Airport("CDG", "Paris Charles de Gaulle",
				new GeodeticCoordinate(49 + (0 / 60.0) + (35.0064 / 3600.0), 2 + (32 / 60.0) + (52.0008 / 3600))),
			new Airport("EDI", "Edinburgh",
				new GeodeticCoordinate(55 + (56 / 60.0) + (59.99 / 3600.0), -(3 + (22 / 60.0) + (12.59 / 3600.0)))),
			new Airport("LHR", "Heathrow",
				new GeodeticCoordinate(51 + (28 / 60.0) + (12.0720 / 3600.0), -(0 + (27 / 60.0) + (15.4620 / 3600.0))))
		};

		Assertions.assertThat(actual)
			.containsExactlyInAnyOrder(expected);
	}

	private @NotNull Path getPathFromTestResources(@NotNull String resourceName) {
		try {
			return Path.of(
				Objects.requireNonNull(this.getClass()
					.getClassLoader()
					.getResource(String.format("FlightDataFileHandlerTest/%s", resourceName))
				).toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}
