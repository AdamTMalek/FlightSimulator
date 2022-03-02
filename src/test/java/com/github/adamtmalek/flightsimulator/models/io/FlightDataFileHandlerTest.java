package com.github.adamtmalek.flightsimulator.models.io;

import com.github.adamtmalek.flightsimulator.models.Flight;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Objects;

class FlightDataFileHandlerTest {
  private static FlightData flightData;
  private final ZonedDateTime dateTime = ZonedDateTime.of(2022, 1, 31, 12, 0, 0, 0, ZoneId.of("Europe/London"));
  
  @BeforeAll
  @Test
  public static void setupTest() {
    try {
      final var dataDir = Objects.requireNonNull(
        FlightDataFileHandlerTest.class.getClassLoader().getResource("flight-data")
      );
      final var fileHandler = FlightDataFileHandler.withDefaultFileNames(Path.of(dataDir.toURI()));
      flightData = fileHandler.readFlightData();
    } catch (URISyntaxException | FileHandlerException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void testFlightsAreReadCorrectly() {
    final var actual = flightData.flights();


    final var expected = new Flight[]{
      Flight.build("OK420",
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
      Flight.build("BA605",
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

    org.assertj.core.api.Assertions.assertThat(actual)
      .containsExactlyInAnyOrder(expected);
  }
}