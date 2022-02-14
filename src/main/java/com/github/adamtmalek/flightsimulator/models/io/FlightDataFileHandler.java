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

  public @NotNull FlightData readFlightData() throws IOException, FileHandlerException {
    final var airportMap = read(airportsCsv, Airport.class);
    final var airlineMap = read(airlinesCsv, Airline.class);
    final var aeroplaneMap = read(aeroplanesCsv, Aeroplane.class);
    final var flights = readFlights(aeroplaneMap, airportMap);

    return new FlightData(
        new ArrayList<>(airportMap.values()),
        new ArrayList<>(airlineMap.values()),
        new ArrayList<>(aeroplaneMap.values()),
        flights
    );
  }

  private <T> @NotNull Map<String, T> read(@NotNull Path fileToRead, @NotNull Class<T> klass) throws FileHandlerException {
    try {
      return parseToMap(readCsv(fileToRead), klass);
    } catch (IOException e) {
      throw new FileHandlerException(String.format("Failed to read %s", fileToRead), e);
    } catch (RuntimeException e) {
      final var message = String.format("Failed to parse %s to create instance of %s class",
          fileToRead, klass.getName());
      throw new FileHandlerException(message, e);
    }
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
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
        throw new RuntimeException(e);
      } catch (NoSuchMethodException e) {
        final var exceptionMessage = String.format("Failed to find a constructor with %d String parameters",
            valueClasses.length);
        throw new RuntimeException(exceptionMessage, e);
      }
    }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private @NotNull List<Flight> readFlights(Map<String, Aeroplane> aeroplaneMap,
                                                   Map<String, Airport> airportMap) throws IOException {
    return readFlights(readCsv(flightsCsv), aeroplaneMap, airportMap);
  }

  private @NotNull List<Flight> readFlights(@NotNull Stream<List<String>> flightsCsv,
                                            @NotNull Map<String, Aeroplane> aeroplaneMap,
                                            @NotNull Map<String, Airport> airportMap) {
    return flightsCsv.map(values -> {
      final var code = values.get(0);
      final var aeroplane = aeroplaneMap.get(values.get(1));
      final var departureAirport = airportMap.get(values.get(2));
      final var arrivalAirport = airportMap.get(values.get(3));
      final var dateOfDeparture = values.get(4);
      final var timeOfDeparture = values.get(5);
      final var flightPlan = values.subList(6, values.size())
          .stream()
          .map(c -> airportMap.get(c).controlTower)
          .toList();

      return new Flight(code, aeroplane,
          departureAirport, arrivalAirport,
          dateOfDeparture, timeOfDeparture, flightPlan);
    }).toList();
  }

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
