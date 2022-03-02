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

	private final @NotNull String delimiter = "; ";

	private final @NotNull CsvFileHandler csvFileHandler = new CsvFileHandler(delimiter);

	private final @NotNull String dateFormat = "dd:MM:yyyy";
	private final @NotNull String timeFormat = "HH:mm";
	private final @NotNull DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(dateFormat);
	private final @NotNull DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(timeFormat);
	private final @NotNull DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat + " " + timeFormat);

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

	public @NotNull FlightData readFlightData() throws FlightDataFileHandlerException {
		try {
			final var airportMap = csvFileHandler.readFile(airportsCsv, Airport.class)
					.stream().collect(Collectors.toMap(e -> e.code, e -> e));
			final var airlineMap = csvFileHandler.readFile(airlinesCsv, Airline.class)
					.stream().collect(Collectors.toMap(Airline::code, e -> e));
			final var aeroplaneMap = csvFileHandler.readFile(aeroplanesCsv, Aeroplane.class)
					.stream().collect(Collectors.toMap(Aeroplane::model, e -> e));

			return new FlightData(
					new ArrayList<>(airportMap.values()),
					new ArrayList<>(airlineMap.values()),
					new ArrayList<>(aeroplaneMap.values()),
					readFlights(aeroplaneMap, airportMap, airlineMap)
			);
		} catch (SerializationException | IOException e) {
			throw new FlightDataFileHandlerException(e);
		}
	}

	private @NotNull Stream<List<String>> readCsv(@NotNull Path path) throws IOException {
		return Files.readAllLines(path)
				.stream()
				.map(line ->
						Arrays.stream(line.split(delimiter))
								.map(String::strip)
								.collect(Collectors.toList()));
	}

	private @NotNull List<Flight> readFlights(Map<String, Aeroplane> aeroplaneMap,
																						Map<String, Airport> airportMap,
																						Map<String, Airline> airlineMap) throws IOException {
		return readFlights(readCsv(flightsCsv), aeroplaneMap, airportMap, airlineMap);
	}

	private @NotNull List<Flight> readFlights(@NotNull Stream<List<String>> flightsCsv,
																						@NotNull Map<String, Aeroplane> aeroplaneMap,
																						@NotNull Map<String, Airport> airportMap,
																						@NotNull Map<String, Airline> airlineMap) {
		return flightsCsv.map(values -> {
			final var code = values.get(0);
			final var airline = airlineMap.get(values.get(0).substring(0, 2));
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

			return Flight.buildWithFlightId(code, airline, aeroplane, departureAirport, arrivalAirport, dateTime, flightPlan);
		}).collect(Collectors.toCollection(ArrayList<Flight>::new));
	}

	public void saveFlights(@NotNull FlightData data) throws FlightDataFileHandlerException {
		csvFileHandler.saveToFile(airlinesCsv, data.airlines());
		csvFileHandler.saveToFile(aeroplanesCsv, data.aeroplanes());
		csvFileHandler.saveToFile(airportsCsv, data.airports());

		final var flightsContent = data.flights()
				.stream()
				.map(flight -> {
					final var code = flight.flightID();
					final var aeroplane = flight.aeroplane().model();
					final var departureAirport = flight.departureAirport().code;
					final var arrivalAirport = flight.destinationAirport().code;
					final var date = flight.departureDate().toLocalDate().format(dateFormatter);
					final var time = flight.departureDate().toLocalTime().format(timeFormatter);
					final var flightPlan = flight.controlTowersToCross().stream().map(e -> e.code).toList();

					return String.join(delimiter, code, aeroplane, departureAirport,
							arrivalAirport, date, time,
							String.join(delimiter, flightPlan));
				})
				.collect(Collectors.joining("\n"));

		try {
			Files.writeString(flightsCsv, flightsContent);
		} catch (IOException e) {
			throw new FlightDataFileHandlerException(e);
		}
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
