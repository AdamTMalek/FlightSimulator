package com.github.adamtmalek.flightsimulator.interfaces;

import com.github.adamtmalek.flightsimulator.models.Flight;
import com.github.adamtmalek.flightsimulator.io.FlightData;
import com.github.adamtmalek.flightsimulator.io.FlightDataFileHandlerException;

import java.io.IOException;
import java.nio.file.Path;

public interface Controller {

	// Read single directory for required files, using default naming scheme
	// defined in FlightDataFileHandler.
	public Controller readFlightData(Path fileDirectoryPath) throws
			IOException,
			FlightDataFileHandlerException;

	// Read unique paths for required files.
	public Controller readFlightData(Path airportSourcePath,
																	 Path aeroplaneSourcePath,
																	 Path airlineSourcePath,
																	 Path flightSourcePath) throws
			IOException,
			FlightDataFileHandlerException;

	public void writeFlightData(Path destinationPath) throws FlightDataFileHandlerException;

	public void writeAirlineReports(Path destinationPath) throws FlightDataFileHandlerException;

	public void addFlight(Flight flight);

	public void removeFlight(int index);

	public void editFlight(int index, Flight flight);

	public FlightData getFlightData();

}
