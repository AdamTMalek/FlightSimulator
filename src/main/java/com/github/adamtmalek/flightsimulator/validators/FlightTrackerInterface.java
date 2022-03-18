package com.github.adamtmalek.flightsimulator.validators;

import com.github.adamtmalek.flightsimulator.FlightTrackerController;
import com.github.adamtmalek.flightsimulator.io.FlightData;
import com.github.adamtmalek.flightsimulator.io.FlightDataFileHandlerException;
import com.github.adamtmalek.flightsimulator.models.Airline;
import com.github.adamtmalek.flightsimulator.models.Flight;

import java.nio.file.Path;
import java.util.ArrayList;

public interface FlightTrackerInterface {
	 FlightTrackerController readFlightData(Path fileDirectoryPath) throws FlightDataFileHandlerException;

	 FlightTrackerController readFlightData(Path airportSourcePath,
																								Path aeroplaneSourcePath,
																								Path airlineSourcePath,
																								Path flightSourcePath) throws FlightDataFileHandlerException;

	 void writeFlightData(Path destinationPath) throws FlightDataFileHandlerException;

	 void writeAirlineReports(Path destinationPath);

	 void addFlight(Flight flight);

	 void removeFlight(int index);



	 FlightData getFlightData();

;
}
