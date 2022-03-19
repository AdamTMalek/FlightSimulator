package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.io.FlightData;
import com.github.adamtmalek.flightsimulator.io.FlightDataFileHandlerException;
import com.github.adamtmalek.flightsimulator.models.Flight;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public interface FlightDataHandler {
	@NotNull FlightData readFlightData(Path fileDirectoryPath) throws FlightDataFileHandlerException;

	@NotNull FlightData readFlightData(@NotNull Path airportSourcePath,
																		 @NotNull Path aeroplaneSourcePath,
																		 @NotNull Path airlineSourcePath,
																		 @NotNull Path flightSourcePath) throws FlightDataFileHandlerException;

	void writeFlightData(@NotNull Path destinationPath) throws FlightDataFileHandlerException;

	void writeAirlineReports(@NotNull Path destinationPath);

	@NotNull FlightData getFlightData();

	void addFlight(@NotNull Flight flight);

	void removeFlight(@NotNull Flight flight);

	void editFlight(@NotNull Flight oldFlight, @NotNull Flight newFlight);
}
