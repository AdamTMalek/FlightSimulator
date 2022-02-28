package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.interfaces.Controller;
import com.github.adamtmalek.flightsimulator.models.Airline;
import com.github.adamtmalek.flightsimulator.models.Flight;
import com.github.adamtmalek.flightsimulator.models.io.*;

import java.nio.file.Path;
import java.util.ArrayList;

public class FlightTrackerController implements Controller {


    public FlightTrackerController readFlightData(Path fileDirectoryPath) throws FileHandlerException {

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
                                                  Path flightSourcePath) throws FileHandlerException {

        final var handler = FlightDataFileHandler.getBuilder()
                .withAirportsPath(airportSourcePath)
                .withAeroplanesPath(aeroplaneSourcePath)
                .withAirlinesPath(airlineSourcePath)
                .withFlightsPath(flightSourcePath)
                .build();

        flightData = handler.readFlightData();
        return this;
    }


    public void writeFlightData(Path destinationPath) {
        throw new RuntimeException("FlightDataFileHandler not implemented, file is not written to."); //TODO FlightDataFileHandler.writeFlightData(flights, destinationPath);
    }

    public void writeAirlineReports(Path destinationPath) {
        for (var airline : flightData.airlines()) {
            final var flightsForAirline = filterFlightsByAirline(airline);
            final var airlineReportPath = destinationPath.resolve(airline.name() + ".csv"); //concatenate destinationPath with the name of the airline.

            var csvWriter = new CsvFileHandler(",");

            csvWriter.saveToFile(airlineReportPath, new ArrayList<>() {{
                add(new AirlineReport(flightsForAirline));
            }}); //new AirlineReport
        }
    }

    public void addFlight(Flight flight) throws
            UnsupportedOperationException,
            ClassCastException,
            NullPointerException,
            IllegalArgumentException {
        flightData.flights().add(flight);
    }

    public void removeFlight(int index) throws
            IndexOutOfBoundsException,
            UnsupportedOperationException {
        flightData.flights().remove(index);
    }

    public void editFlight(int index, Flight flight) throws
            NullPointerException,
            IndexOutOfBoundsException {
        flightData.flights().set(index, flight);
    }

    public FlightData getFlightData() {
        return flightData;
    }

    private FlightData flightData;

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
