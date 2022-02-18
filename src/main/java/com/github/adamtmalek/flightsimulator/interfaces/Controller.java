package com.github.adamtmalek.flightsimulator.interfaces;

import com.github.adamtmalek.flightsimulator.models.Flight;
import com.github.adamtmalek.flightsimulator.models.io.FileHandlerException;
import com.github.adamtmalek.flightsimulator.models.io.FlightData;

import java.io.IOException;
import java.nio.file.Path;

public interface Controller {

    // Read single directory for required files, using default naming scheme
    // defined in FlightDataFileHandler.
    public FlightData readFlightData(Path fileDirectoryPath) throws
            IOException,
            FileHandlerException;

    // Read unique paths for required files.
    public FlightData readFlightData(Path airportSourcePath,
                                     Path aeroplaneSourcePath,
                                     Path airlineSourcePath,
                                     Path flightSourcePath) throws
            IOException,
            FileHandlerException;

    public void writeFlightData(Path destinationPath);

    public void writeAirlineReports(Path destinationPath);

    public void addFlight(Flight flight) throws
            UnsupportedOperationException,
            ClassCastException,
            NullPointerException,
            IllegalArgumentException;

    public void removeFlight(int index) throws
            IndexOutOfBoundsException,
            UnsupportedOperationException;

    public void editFlight(int index, Flight flight) throws
            NullPointerException,
            IndexOutOfBoundsException;

    public FlightData getFlightData();

}
