package com.github.adamtmalek.flightsimulator.models.io;

import com.github.adamtmalek.flightsimulator.TestSuite;
import com.github.adamtmalek.flightsimulator.io.CsvFileHandler;
import com.github.adamtmalek.flightsimulator.io.SerializableField;
import com.github.adamtmalek.flightsimulator.io.SerializationException;
import com.github.adamtmalek.flightsimulator.models.Aeroplane;
import com.github.adamtmalek.flightsimulator.models.Airline;
import com.github.adamtmalek.flightsimulator.models.Airport;
import com.github.adamtmalek.flightsimulator.models.GeodeticCoordinate;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class CsvFileHandlerTest extends TestSuite {
	private static final @NotNull CsvFileHandler fileHandler = new CsvFileHandler("; ");

	@Test
	public void testAeroplanesAreReadCorrectly() {
		final List<Aeroplane> actual;
		try {
			actual = fileHandler.readFile(
					getPathFromResources("flight-data/aeroplanes.csv"),
					Aeroplane.class
			);
		} catch (SerializationException e) {
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
	public void testAeroplanesAreSavedCorrectly() {
		final var planes = Arrays.asList(
				new Aeroplane("B777", "Boeing", 875.0, 952.78815),
				new Aeroplane("A330", "Airbus", 800.0, 768.439),
				new Aeroplane("A350", "Airbus", 900.0, 747.24)
		);

		final var expected = getPathFromResources("flight-data/aeroplanes.csv");
		final Path actual;
		try {
			actual = fileHandler.saveToFile(Files.createTempFile("csvAeroplanes", ".csv"), planes);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		final var expectedFile = new File(expected.toUri());
		final var actualFile = new File(actual.toUri());

		Assertions.assertThat(actualFile)
				.hasSameTextualContentAs(expectedFile);
	}

	@Test
	public void testAirlinesAreReadCorrectly() {
		final List<Airline> actual;
		try {
			actual = fileHandler.readFile(
					getPathFromResources("flight-data/airlines.csv"),
					Airline.class
			);
		} catch (SerializationException e) {
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
	public void testAirlinesAreSavedCorrectly() {
		final var airlines = Arrays.asList(
				new Airline("AA", "American Airlines"),
				new Airline("OK", "Czech Airlines"),
				new Airline("BA", "British Airways")
		);

		final var expected = getPathFromResources("flight-data/airlines.csv");
		final Path actual;
		try {
			actual = fileHandler.saveToFile(Files.createTempFile("csvAirlines", ".csv"), airlines);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		final var expectedFile = new File(expected.toUri());
		final var actualFile = new File(actual.toUri());

		Assertions.assertThat(actualFile)
				.hasSameTextualContentAs(expectedFile);
	}

	@Test
	public void testAirportsAreReadCorrectly() {
		final List<Airport> actual;
		try {
			actual = fileHandler.readFile(
					getPathFromResources("flight-data/airports.csv"),
					Airport.class
			);
		} catch (SerializationException e) {
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

	@Test
	public void testAirportsAreSavedCorrectly() {
		final var airports = Arrays.asList(
				new Airport("CDG", "Paris Charles de Gaulle",
						new GeodeticCoordinate(49 + (0 / 60.0) + (35.0064 / 3600.0), 2 + (32 / 60.0) + (52.0008 / 3600))),
				new Airport("EDI", "Edinburgh",
						new GeodeticCoordinate(55 + (56 / 60.0) + (59.99 / 3600.0), -(3 + (22 / 60.0) + (12.59 / 3600.0)))),
				new Airport("LHR", "Heathrow",
						new GeodeticCoordinate(51 + (28 / 60.0) + (12.0720 / 3600.0), -(0 + (27 / 60.0) + (15.4620 / 3600.0))))
		);

		final var expected = getPathFromResources("flight-data/airports.csv");
		final Path actual;
		try {
			actual = fileHandler.saveToFile(Files.createTempFile("csvAirports", ".csv"), airports);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		final var expectedFile = new File(expected.toUri());
		final var actualFile = new File(actual.toUri());

		Assertions.assertThat(actualFile)
				.hasSameTextualContentAs(expectedFile);
	}

	@Test
	public void testReadingEmptyFileResultsInEmptyList() {
		final Path emptyFilePath;
		try {
			emptyFilePath = Files.createTempFile("emptyFile", ".csv");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		Assertions.assertThat(fileHandler.readFile(emptyFilePath, Aeroplane.class))
				.isEmpty();
	}

	@Test
	public void testSerializationExceptionIsThrownWhenNoSuitableConstructorIsFound() {
		Assertions.assertThatThrownBy(() -> {
			try {
				fileHandler.readFile(
						getPathFromResources("flight-data/aeroplanes.csv"),
						TestClass.class
				);
			} catch (SerializationException e) {
				Assertions.fail(String.format("Exception thrown: %s", e));
			}
		});
	}

	public static class TestClass {
		@SerializableField
		public String foo;
		@SerializableField
		public String bar;


		public TestClass() {
			this.foo = null;
			this.bar = null;
		}
	}
}
