package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.io.FlightData;
import com.github.adamtmalek.flightsimulator.io.FlightDataFileHandler;
import com.github.adamtmalek.flightsimulator.io.FlightDataFileHandlerException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ReportWriterTest extends TestSuite {
	@Test
	void testWriteAirlineReports() {
		final var writer = new ReportWriter();

		final var handler = FlightDataFileHandler.withDefaultFileNames(this.getPathFromResources("flight-data"));

		final FlightData flightData;
		final Path tempOutputDirectory;
		try {
			flightData = handler.readFlightData();
			tempOutputDirectory = Files.createTempDirectory("generated-airline-reports");
		} catch (FlightDataFileHandlerException | IOException e) {
			throw new RuntimeException(e);
		}

		writer.writeAirlineReports(tempOutputDirectory, flightData.airlines(), flightData.flights());

		// Check American Airlines
		final var expectedAAFile = new File(getPathFromResources("airline-reports/American Airlines.csv").toUri());
		final var actualAAFile = new File(tempOutputDirectory.resolve("American Airlines.csv").toUri());
		org.assertj.core.api.Assertions.assertThat(actualAAFile)
				.hasSameTextualContentAs(expectedAAFile);

		// Check Czech Airlines
		final var expectedOKFile = new File(getPathFromResources("airline-reports/Czech Airlines.csv").toUri());
		final var actualOKFile = new File(tempOutputDirectory.resolve("Czech Airlines.csv").toUri());
		org.assertj.core.api.Assertions.assertThat(actualOKFile)
				.hasSameTextualContentAs(expectedOKFile);

		// Check British Airways
		final var expectedBAFile = new File(getPathFromResources("airline-reports/British Airways.csv").toUri());
		final var actualBAFile = new File(tempOutputDirectory.resolve("British Airways.csv").toUri());
		org.assertj.core.api.Assertions.assertThat(actualBAFile)
				.hasSameTextualContentAs(expectedBAFile);

	}
}
