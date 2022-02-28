package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.models.io.FileHandlerException;
import com.github.adamtmalek.flightsimulator.models.io.TestSuite;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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

        } catch (FileHandlerException e) {
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

        } catch (FileHandlerException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Test
    void writeAirlineReports() {

        var controller = new FlightTrackerController();

        // Must give controller airlines and flights in order to filter, requiring file-reading.
        try {
            final var flightData = controller.readFlightData(getPathFromResources("flight-data")).getFlightData();
        } catch (FileHandlerException e) {
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


}
