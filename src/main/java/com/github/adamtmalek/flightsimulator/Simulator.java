package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.io.FlightDataFileHandler;
import com.github.adamtmalek.flightsimulator.io.FlightDataFileHandlerException;
import com.github.adamtmalek.flightsimulator.models.Aeroplane;
import com.github.adamtmalek.flightsimulator.models.Airline;
import com.github.adamtmalek.flightsimulator.models.Airport;
import com.github.adamtmalek.flightsimulator.models.Flight;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;

public final class Simulator {
	public static final Path FLIGHT_DATA_DIRECTORY = Path.of("flight-data/");
	public static final Path FLIGHTS_REPORT_DIRECTORY = Path.of("reports/");

	private final @NotNull ObservableSet<Aeroplane> aeroplanes = FXCollections.observableSet();
	private final @NotNull ObservableSet<Airline> airlines = FXCollections.observableSet();
	private final @NotNull ObservableSet<Airport> airports = FXCollections.observableSet();
	private final @NotNull ObservableSet<Flight> flights = FXCollections.observableSet();

	private final @NotNull FlightJoiner flightJoiner = new FlightJoiner();
	private final @NotNull FlightSimulationThreadManagement threadManager;

	public Simulator() {
		final var flightJoiner = new FlightJoiner();
		final var controlTowers = airports.stream().map(o -> o.controlTower).toList();

		initFlightJoiner(flightJoiner);
		threadManager = new FlightSimulationThreadManagement(flights, controlTowers, flightJoiner);
	}

	private void initFlightJoiner(@NotNull FlightJoiner joiner) {
		airports.stream()
				.map(o -> o.controlTower)
				.forEach(o -> o.registerSubscriber(flightJoiner));

		joiner.registerSubscriber(data -> data.forEach(flight -> {
					flights.removeIf(f -> f.flightID().equals(flight.flightID()));
					flights.add(flight);
				})
		);
	}

	public void addAeroplanes(@NotNull Collection<Aeroplane> aeroplanes) {
		this.aeroplanes.addAll(aeroplanes);
	}

	public void addFlights(@NotNull Collection<Flight> flights) {
		this.flights.addAll(flights);
	}

	public void addFlight(@NotNull Flight flight) {
		this.flights.add(flight);
	}

	public void addAirports(@NotNull Collection<Airport> airports) {
		this.airports.addAll(airports);
	}

	public void addAirlines(@NotNull Collection<Airline> airlines) {
		this.airlines.addAll(airlines);
	}

	public void addFlightCollectionListener(@NotNull SetChangeListener<? super Flight> listener) {
		this.flights.addListener(listener);
	}

	public void addAeroplaneCollectionListener(@NotNull SetChangeListener<? super Aeroplane> listener) {
		this.aeroplanes.addListener(listener);
	}

	public void addAirportCollectionListener(@NotNull SetChangeListener<? super Airport> listener) {
		this.airports.addListener(listener);
	}

	public void addAirlineCollectionListener(@NotNull SetChangeListener<? super Airline> listener) {
		this.airlines.addListener(listener);
	}

	public @NotNull @Unmodifiable Collection<Flight> getFlights() {
		return flights.stream().toList();
	}

	public void writeAirlineReports() {
		final var writer = new ReportWriter();
		writer.writeAirlineReports(FLIGHTS_REPORT_DIRECTORY, airlines, flights);
	}

	public void writeFlightData() {

	}

	public void readFlightData() throws FlightDataFileHandlerException {
		final var aeroplanesPath = getPathFromResourcesFlightData("aeroplanes.csv");
		final var airlinesPath = getPathFromResourcesFlightData("airlines.csv");
		final var airportsPath = getPathFromResourcesFlightData("airports.csv");
		final var flightsPath = getPathFromResourcesFlightData("flights.csv");

		readFlightData(aeroplanesPath, airlinesPath, airportsPath, flightsPath);
	}

	public void readFlightData(@NotNull Path aeroplanesFile,
														 @NotNull Path airlinesFile,
														 @NotNull Path airportsFile,
														 @NotNull Path flightsFile) throws FlightDataFileHandlerException {
		final var handler = FlightDataFileHandler.getBuilder()
				.withAirportsPath(airportsFile)
				.withAeroplanesPath(aeroplanesFile)
				.withAirlinesPath(airlinesFile)
				.withFlightsPath(flightsFile)
				.build();

		final var data = handler.readFlightData();
		replaceCollectionWith(aeroplanes, data.aeroplanes());
		replaceCollectionWith(airlines, data.airlines());
		replaceCollectionWith(airports, data.airports());
		replaceCollectionWith(flights, data.flights());
	}

	private <T> void replaceCollectionWith(@NotNull Collection<T> collection,
																				 @NotNull Collection<T> data) {
		collection.clear();
		collection.addAll(data);
	}

	private @NotNull Path getPathFromResourcesFlightData(@NotNull String name) {
		try {
			final var resourceName = String.format("cw-spec-data/%s", name);
			final var resource = Objects.requireNonNull(getClass().getClassLoader().getResource(resourceName));
			return Path.of(resource.toURI());
		} catch (URISyntaxException ex) {
			// If we're using toURI() function of URL from getResource(), then how can URISyntaxException be possibly thrown?
			// But we have to do something here - so let's throw a RuntimeException just to "handle" that possibility.
			throw new RuntimeException(ex);
		}
	}
}
