package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.io.*;
import com.github.adamtmalek.flightsimulator.models.Airline;
import com.github.adamtmalek.flightsimulator.models.Flight;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FlightDataHandlerImpl implements FlightDataHandler {
	private @NotNull FlightData flightData = new FlightData();

	@Override
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

	@Override
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

	@Override
	public void writeFlightData(@NotNull Path destinationPath) throws FlightDataFileHandlerException {
		FlightDataFileHandler.getBuilder()
				.withFlightsPath(destinationPath.resolve("flights.csv"))
				.withAeroplanesPath(destinationPath.resolve("aeroplanes.csv"))
				.withAirlinesPath(destinationPath.resolve("airlines.csv"))
				.withAirportsPath(destinationPath.resolve("airports.csv")).build().saveFlights(flightData);
	}

	@Override
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

	@Override
	public @NotNull FlightData getFlightData() {
		return flightData;
	}

	@Override
	public void addFlight(@NotNull Flight flight) {
		flightData.flights().add(flight);
	}

	@Override
	public void removeFlight(int index) {
		flightData.flights().remove(index);
	}

	@Override
	public void editFlight(int index, @NotNull Flight flight) {
		flightData.flights().set(index, flight);
	}

	private @NotNull List<Flight> filterFlightsByAirline(@NotNull Airline airline) {
		return flightData.flights()
				.stream()
				.filter(flight -> flight.flightID().contains(airline.code()))
				.toList();
	}
}
