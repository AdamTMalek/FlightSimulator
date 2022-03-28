package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.io.*;
import com.github.adamtmalek.flightsimulator.models.Airline;
import com.github.adamtmalek.flightsimulator.models.Flight;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ReportWriter {
	public void writeAirlineReports(@NotNull Path destinationPath,
																	@NotNull Collection<Airline> airlines,
																	@NotNull Collection<Flight> flights) {
		for (var airline : airlines) {
			final var flightsForAirline = filterFlightsByAirline(flights, airline);
			final var escapedName = airline.name().replace("\\", "-").replace("/", "-");
			final var airlineReportPath = destinationPath.resolve(escapedName + ".csv");

			var csvWriter = new CsvFileHandler(",");

			csvWriter.saveToFile(airlineReportPath, new ArrayList<>() {{
				add(new AirlineReport(flightsForAirline));
			}});
		}
	}

	private @NotNull List<Flight> filterFlightsByAirline(@NotNull Collection<Flight> flights,
																											 @NotNull Airline airline) {
		return flights
				.stream()
				.filter(flight -> flight.flightID().contains(airline.code()))
				.toList();
	}
}
