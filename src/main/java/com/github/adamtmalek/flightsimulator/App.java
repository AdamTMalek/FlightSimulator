package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.models.io.FlightDataFileHandler;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

public class App {
  public static void main(String[] args) {
    final var path = Path.of(URI.create("file:///"));  // TODO: Change this to the right path
    final var handler = FlightDataFileHandler.getBuilder()
        .withDirectory(path)
        .withAirportsFilename("SampleAirports.csv")
        .withDefaultAirlinesFilename()
        .withDefaultFlightsFilename()
        .withDefaultAeroplanesFilename()
        .build();

    try {
      final var airports = handler.readAirports();
      System.out.println(airports.size());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
