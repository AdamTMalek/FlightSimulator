package com.github.adamtmalek.flightsimulator.models.io;

import com.github.adamtmalek.flightsimulator.models.Aeroplane;
import com.github.adamtmalek.flightsimulator.models.Airline;
import com.github.adamtmalek.flightsimulator.models.Airport;
import com.github.adamtmalek.flightsimulator.models.Flight;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

	private final @NotNull CsvFileHandler csvFileHandler = new CsvFileHandler(";");
	private final @NotNull DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd:MM:yyyy HH:mm");

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
		final var airportMap = csvFileHandler.readFile(airportsCsv, Airport.class)
			.stream().collect(Collectors.toMap(e -> e.code, e -> e));
		final var airlineMap = csvFileHandler.readFile(airlinesCsv, Airline.class)
			.stream().collect(Collectors.toMap(Airline::code, e -> e));
		final var aeroplaneMap = csvFileHandler.readFile(aeroplanesCsv, Aeroplane.class)
			.stream().collect(Collectors.toMap(Aeroplane::model, e -> e));

		final var flights = readFlights(aeroplaneMap, airportMap);

		return new FlightData(
			new ArrayList<>(airportMap.values()),
			new ArrayList<>(airlineMap.values()),
			new ArrayList<>(aeroplaneMap.values()),
			flights
		);
	}

	private @NotNull Stream<List<String>> readCsv(@NotNull Path path) throws IOException {
		return Files.readAllLines(path)
			.stream()
			.map(line ->
				Arrays.stream(line.split(";"))
					.map(String::strip)
					.collect(Collectors.toList()));
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

			String formattedDate = String.format("%s %s", values.get(4), values.get(5));
			final var dateTime = LocalDateTime.parse(formattedDate, dateTimeFormatter)
				.atZone(ZoneId.of("Europe/London"));

			final var flightPlan = values.subList(6, values.size())
				.stream()
				.map(c -> airportMap.get(c).controlTower)
				.toList();

			return new Flight(code, aeroplane, departureAirport, arrivalAirport, dateTime, flightPlan);
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
