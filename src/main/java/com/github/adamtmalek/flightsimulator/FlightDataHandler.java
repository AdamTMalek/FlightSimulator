package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.io.*;
import com.github.adamtmalek.flightsimulator.models.Airline;
import com.github.adamtmalek.flightsimulator.models.Flight;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FlightDataHandler {
	private @NotNull FlightData flightData = new FlightData();

	public @NotNull FlightData readFlightData(Path fileDirectoryPath) throws FlightDataFileHandlerException {

		final var handler = FlightDataFileHandler.getBuilder()
				.withDirectory(fileDirectoryPath)
				.withDefaultFlightsFilename()
				.withDefaultAeroplanesFilename()
				.withDefaultAirlinesFilename()
				.withDefaultAirportsFilename()
				.build();

		flightData = handler.readFlightData();
		return flightData;
	}

	public @NotNull FlightData readFlightData(@NotNull Path airportSourcePath,
																						@NotNull Path aeroplaneSourcePath,
																						@NotNull Path airlineSourcePath,
																						@NotNull Path flightSourcePath) throws FlightDataFileHandlerException {

		final var handler = FlightDataFileHandler.getBuilder()
				.withAirportsPath(airportSourcePath)
				.withAeroplanesPath(aeroplaneSourcePath)
				.withAirlinesPath(airlineSourcePath)
				.withFlightsPath(flightSourcePath)
				.build();

		flightData = handler.readFlightData();
		return flightData;
	}

	public void writeFlightData(@NotNull Path destinationPath) throws FlightDataFileHandlerException {
		FlightDataFileHandler.getBuilder()
				.withFlightsPath(destinationPath.resolve("flights.csv"))
				.withAeroplanesPath(destinationPath.resolve("aeroplanes.csv"))
				.withAirlinesPath(destinationPath.resolve("airlines.csv"))
				.withAirportsPath(destinationPath.resolve("airports.csv")).build().saveFlights(flightData);
	}

	public void writeAirlineReports(@NotNull Path destinationPath) {
		for (var airline : flightData.airlines()) {
			final var flightsForAirline = filterFlightsByAirline(airline);
			final var escapedName = airline.name().replace("\\", "-").replace("/", "-");
			final var airlineReportPath = destinationPath.resolve(escapedName + ".csv");

			var csvWriter = new CsvFileHandler(",");

			csvWriter.saveToFile(airlineReportPath, new ArrayList<>() {{
				add(new AirlineReport(flightsForAirline));
			}}); //new AirlineReport
		}
	}

	public @NotNull FlightData getFlightData() {
		return flightData;
	}

	public void addFlight(@NotNull Flight flight) {
		flightData.flights().add(flight);
	}

	public void removeFlight(int index) {
		flightData.flights().remove(index);
	}

	public void editFlight(int index, @NotNull Flight flight) {
		flightData.flights().set(index, flight);
	}

	public FlightData getFlightData() {
		return flightData;
	}

	private @NotNull ArrayList<Flight> filterFlightsByAirline(Airline airline) {

		ArrayList<Flight> filteredFlights = new ArrayList<Flight>();
		for (var flight : flightData.flights()) {
			if (flight.flightID().contains(airline.code())) {
				filteredFlights.add(flight);
			}
		}
		return filteredFlights;
	}
}
