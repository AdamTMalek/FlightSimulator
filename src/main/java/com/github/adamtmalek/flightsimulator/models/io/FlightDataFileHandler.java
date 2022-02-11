package com.github.adamtmalek.flightsimulator.models.io;

import com.github.adamtmalek.flightsimulator.models.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FlightDataFileHandler {
  private static final String DEFAULT_AIRPORTS_FILENAME = "airports.csv";
  private static final String DEFAULT_AIRLINES_FILENAME = "airlines.csv";
  private static final String DEFAULT_AEROPLANES_FILENAME = "aeroplanes.csv";
  private static final String DEFAULT_FLIGHTS_FILENAME = "flights.csv";

  private final @NotNull Path airportsCsv;
  private final @NotNull Path airlinesCsv;
  private final @NotNull Path aeroplanesCsv;
  private final @NotNull Path flightsCsv;

  private final HashMap<String, Airport> airportMap = new HashMap<>();
  private final HashMap<String, ControlTower> controlTowerMap = new HashMap<>();
  private final HashMap<String, Airline> airlineMap = new HashMap<>();
  private final HashMap<String, Aeroplane> aeroplaneMap = new HashMap<>();
  private final HashMap<String, Flight> flightMap = new HashMap<>();

  private FlightDataFileHandler(@NotNull Path airportsCsv,
                                @NotNull Path airlinesCsv,
                                @NotNull Path aeroplanesCsv,
                                @NotNull Path flightsCsv) {

    this.airportsCsv = airportsCsv;
    this.airlinesCsv = airlinesCsv;
    this.aeroplanesCsv = aeroplanesCsv;
    this.flightsCsv = flightsCsv;
  }

  public static @NotNull Builder getBuilder() {
    return new Builder();
  }

  public static @NotNull FlightDataFileHandler withDefaultFileNames(@NotNull Path dataDirectory) {
    return new FlightDataFileHandler(
        dataDirectory.resolve(DEFAULT_AIRPORTS_FILENAME),
        dataDirectory.resolve(DEFAULT_AIRLINES_FILENAME),
        dataDirectory.resolve(DEFAULT_AEROPLANES_FILENAME),
        dataDirectory.resolve(DEFAULT_FLIGHTS_FILENAME)
    );
  }

  public @NotNull Collection<Airport> readAirports() throws IOException {
    return parseToMap(readCsv(airportsCsv), Airport.class).values();
  }

  public @NotNull Collection<Airline> readAirlines() throws IOException {
    return parseToMap(readCsv(airlinesCsv), Airline.class).values();
  }

  public @NotNull Collection<Aeroplane> readAeroplanes() throws IOException {
    return parseToMap(readCsv(aeroplanesCsv), Aeroplane.class).values();
  }

  private @NotNull Stream<List<String>> readCsv(@NotNull Path path) throws IOException {
    return Files.readAllLines(path)
        .stream()
        .map(line ->
            Arrays.stream(line.split(";"))
                .map(String::strip)
                .collect(Collectors.toList()));
  }

  private <T> @NotNull Map<String, T> parseToMap(@NotNull Stream<List<String>> data,
                                                 @NotNull Class<T> recordClass) {
    return data.map(values -> {
      final var valueClasses = values.stream().map(Object::getClass).toArray(Class<?>[]::new);
      final Object[] valueArray = values.toArray(new String[0]);
      try {
        final var code = values.get(0);
        final T instance = recordClass.getConstructor(valueClasses).newInstance(valueArray);
        return new AbstractMap.SimpleImmutableEntry<>(code, instance);
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
        throw new RuntimeException(e);
      }
    }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

//  private @NotNull Map<String, Flight> parseFlightsToMap(@NotNull Stream<List<String>> flightsCsv) {
//    return flightsCsv.map(values -> {
//      final var code = values.get(0);
//      final var aeroplane = aeroplaneMap.get(values.get(1));
//      final var departureAirport = airportMap.get(values.get(2));
//      final var arrivalAirport = airportMap.get(values.get(3));
//      final var dateOfDeparture = values.get(4);
//      final var timeOfDeparture = values.get(5);
//      final var flightPlan = values.subList(6, values.size())
//          .stream()
//          .map(airportMap::get)  // TODO: This needs to be changed, so that it's list of control towers
//          .toList();
//
//      final var flight = new Flight(code, aeroplane,
//          departureAirport, arrivalAirport,
//          dateOfDeparture, timeOfDeparture, flightPlan);
//
//      return new AbstractMap.SimpleImmutableEntry<>(code, flight);
//    }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//  }

  public static class Builder {
    private @Nullable Path directoryPath = null;
    private @Nullable Path airportsPath = null;
    private @Nullable Path airlinesPath = null;
    private @Nullable Path aeroplanesPath = null;
    private @Nullable Path flightsPath = null;

    public @NotNull Builder withDirectory(@NotNull Path directoryPath) {
      this.directoryPath = directoryPath;
      return this;
    }

    public @NotNull Builder withAirportsPath(@NotNull Path path) {
      airportsPath = path;
      return this;
    }

    public @NotNull Builder withAirportsFilename(@NotNull String filename) {
      airportsPath = getDirectoryPath().resolve(filename);
      return this;
    }

    public @NotNull Builder withDefaultAirportsFilename() {
      return withAirportsFilename(DEFAULT_AIRPORTS_FILENAME);
    }

    public @NotNull Builder withAirlinesPath(@NotNull Path path) {
      airlinesPath = path;
      return this;
    }

    public @NotNull Builder withAirlinesFilename(@NotNull String filename) {
      airlinesPath = getDirectoryPath().resolve(filename);
      return this;
    }

    public @NotNull Builder withDefaultAirlinesFilename() {
      return withAirlinesFilename(DEFAULT_AIRLINES_FILENAME);
    }

    public @NotNull Builder withAeroplanesPath(@NotNull Path path) {
      aeroplanesPath = path;
      return this;
    }

    public @NotNull Builder withAeroplanesFilename(@NotNull String filename) {
      aeroplanesPath = getDirectoryPath().resolve(filename);
      return this;
    }

    public @NotNull Builder withDefaultAeroplanesFilename() {
      return withAeroplanesFilename(DEFAULT_AEROPLANES_FILENAME);
    }

    public @NotNull Builder withFlightsPath(@NotNull Path path) {
      flightsPath = path;
      return this;
    }

    public @NotNull Builder withFlightsFilename(@NotNull String filename) {
      flightsPath = getDirectoryPath().resolve(filename);
      return this;
    }

    public @NotNull Builder withDefaultFlightsFilename() {
      return withFlightsFilename(DEFAULT_FLIGHTS_FILENAME);
    }

    public @NotNull FlightDataFileHandler build() {
      final Function<String, String> messageGenerator = (n) -> String.format("Path to %s CSV must be set", n);
      final var airports = Objects.requireNonNull(airportsPath, messageGenerator.apply("airports"));
      final var airlines = Objects.requireNonNull(airlinesPath, messageGenerator.apply("airlines"));
      final var planes = Objects.requireNonNull(aeroplanesPath, messageGenerator.apply("aeroplanes"));
      final var flights = Objects.requireNonNull(flightsPath, messageGenerator.apply("flights"));

      return new FlightDataFileHandler(airports, airlines, planes, flights);
    }

    private @NotNull Path getDirectoryPath() {
      return Objects.requireNonNull(directoryPath, "Path to the data directory must be set (use withDirectory)");
    }
  }
}
