package com.github.adamtmalek.flightsimulator.models.io;

import com.github.adamtmalek.flightsimulator.models.Aeroplane;
import com.github.adamtmalek.flightsimulator.models.Airline;
import com.github.adamtmalek.flightsimulator.models.Airport;
import com.github.adamtmalek.flightsimulator.models.Flight;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

class FlightDataFileHandlerTest {
  private static FlightData flightData;

  @BeforeAll
  @Test
  public static void setupTest() {
    try {
      final var dataDir = Objects.requireNonNull(
          FlightDataFileHandlerTest.class.getClassLoader().getResource("FlightDataFileHandlerTest")
      );
      final var fileHandler = FlightDataFileHandler.withDefaultFileNames(Path.of(dataDir.toURI()));
      flightData = fileHandler.readFlightData();
    } catch (URISyntaxException | IOException | FileHandlerException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void testAeroplanesAreReadCorrectly() {
    final var actual = flightData.aeroplanes();
    final var expected = new Aeroplane[]{
        new Aeroplane("B777", "Boeing", "875", "952.78815"),
        new Aeroplane("A330", "Airbus", "800", "768.439"),
        new Aeroplane("A350", "Airbus", "900", "747.24")
    };

    Assertions.assertThat(actual)
        .containsExactlyInAnyOrder(expected);
  }

  @Test
  public void testAirlinesAreReadCorrectly() {
    final var actual = flightData.airlines();
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
    final var actual = flightData.airports();
    final var expected = new Airport[]{
        new Airport("CDG", "Paris Charles de Gaulle", "49°0'35.0064\"N", "2°32'52.0008\"E"),
        new Airport("EDI", "Edinburgh", "55°56'59.99\"N", "-3°22'12.59\"W"),
        new Airport("LHR", "Heathrow", "51°28'12.0720\"N", "0°27'15.4620\"W")
    };

    Assertions.assertThat(actual)
        .containsExactlyInAnyOrder(expected);
  }

  @Test
  public void testFlightsAreReadCorrectly() {
    final var actual = flightData.flights();

    final var expected = new Flight[]{
        new Flight("OK420",
            flightData.aeroplanes().stream().filter(v -> v.model().equals("B777")).findFirst().orElseThrow(),
            flightData.airports().stream().filter(v -> v.code.equals("CDG")).findFirst().orElseThrow(),
            flightData.airports().stream().filter(v -> v.code.equals("EDI")).findFirst().orElseThrow(),
            "14:02:2022",
            "23:00",
            Arrays.asList(
                flightData.airports().stream().filter(v -> v.code.equals("CDG")).findFirst().orElseThrow().controlTower,
                flightData.airports().stream().filter(v -> v.code.equals("EDI")).findFirst().orElseThrow().controlTower
            )
        ),
        // BA605; A330; EDI; LHR; 15:01:2022; 06:00; EDI; CDG; LHR
        new Flight("BA605",
            flightData.aeroplanes().stream().filter(v -> v.model().equals("A330")).findFirst().orElseThrow(),
            flightData.airports().stream().filter(v -> v.code.equals("EDI")).findFirst().orElseThrow(),
            flightData.airports().stream().filter(v -> v.code.equals("LHR")).findFirst().orElseThrow(),
            "15:01:2022",
            "06:00",
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
}