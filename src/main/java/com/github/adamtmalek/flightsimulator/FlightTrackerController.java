package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.interfaces.Controller;
import com.github.adamtmalek.flightsimulator.models.Airline;
import com.github.adamtmalek.flightsimulator.models.Flight;
import com.github.adamtmalek.flightsimulator.models.io.*;

import java.nio.file.Path;
import java.util.ArrayList;

public class FlightTrackerController implements Controller {
	private FlightData flightData = new FlightData();

	public FlightTrackerController readFlightData(Path fileDirectoryPath) throws FlightDataFileHandlerException {

		final var handler = FlightDataFileHandler.getBuilder()
				.withDirectory(fileDirectoryPath)
				.withDefaultFlightsFilename()
				.withDefaultAeroplanesFilename()
				.withDefaultAirlinesFilename()
				.withDefaultAirportsFilename()
				.build();

		flightData = handler.readFlightData();
		return this;
	}

	public FlightTrackerController readFlightData(Path airportSourcePath,
																								Path aeroplaneSourcePath,
																								Path airlineSourcePath,
																								Path flightSourcePath) throws FlightDataFileHandlerException {

		final var handler = FlightDataFileHandler.getBuilder()
				.withAirportsPath(airportSourcePath)
				.withAeroplanesPath(aeroplaneSourcePath)
				.withAirlinesPath(airlineSourcePath)
				.withFlightsPath(flightSourcePath)
				.build();

		flightData = handler.readFlightData();
		return this;
	}


	public void writeFlightData(Path destinationPath) throws FlightDataFileHandlerException {
		FlightDataFileHandler.getBuilder()
				.withFlightsPath(destinationPath.resolve("flights.csv"))
				.withAeroplanesPath(destinationPath.resolve("aeroplanes.csv"))
				.withAirlinesPath(destinationPath.resolve("airlines.csv"))
				.withAirportsPath(destinationPath.resolve("airports.csv")).build().saveFlights(flightData);
	}

	public void writeAirlineReports(Path destinationPath) {
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

	public void addFlight(Flight flight) {
		flightData.flights().add(flight);
	}

	public void removeFlight(int index) {
		flightData.flights().remove(index);
	}

	public void editFlight(int index, Flight flight) {
		flightData.flights().set(index, flight);
	}

	public FlightData getFlightData() {
		return flightData;
	}

	private ArrayList<Flight> filterFlightsByAirline(Airline airline) {

		ArrayList<Flight> filteredFlights = new ArrayList<Flight>();
		for (var flight : flightData.flights()) {
			if (flight.flightID().contains(airline.code())) {
				filteredFlights.add(flight);
			}
		}
		return filteredFlights;
	}
}
