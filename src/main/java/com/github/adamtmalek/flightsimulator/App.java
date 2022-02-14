package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.models.Aeroplane;
import com.github.adamtmalek.flightsimulator.models.Airline;
import com.github.adamtmalek.flightsimulator.models.Airport;
import com.github.adamtmalek.flightsimulator.models.Flight;
import com.github.adamtmalek.flightsimulator.models.io.FileHandlerException;
import com.github.adamtmalek.flightsimulator.models.io.FlightData;
import com.github.adamtmalek.flightsimulator.models.io.FlightDataFileHandler;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class App {

    private static List<Airport> airports;
    private static List<Airline> airlines;
    private static List<Aeroplane> aeroplanes;
    private static List<Flight> flights;

    public static void mainAppLoop() {
        boolean isRunning = true;
        while (isRunning) {
            //TODO GUI to App relation, how to determine what operation to perform, and then perform it.
        }
    }

    public static void writeFlightData(Path destinationPath) {
        //TODO FlightDataFileHandler.writeFlightData(flights, destinationPath);
    }

    public static void writeAirlineReports(Path destinationPath) {
        for (var airline : airlines) {
            final var flightsForAirline = filterFlightsByAirline(airline);
            //TODO Get path.
            //TODO ReportFileHandler.writeReport(new AirlineReport(flightsForAirline),path);
        }
    }

    private static ArrayList<Flight> filterFlightsByAirline(Airline airline) {

        ArrayList<Flight> filteredFlights = new ArrayList<Flight>();
        for (var flight : flights) {
            if (flight.flightID().equals(airline.code())) {
                filteredFlights.add(flight);
            }
        }
        return filteredFlights;
    }

    public static void addFlight(Flight flight) throws
            UnsupportedOperationException,
            ClassCastException,
            NullPointerException,
            IllegalArgumentException {
        flights.add(flight);
    }

    public static void removeFlight(int index) throws
            IndexOutOfBoundsException,
            UnsupportedOperationException {
        flights.remove(index);
    }

    public static void editFlight(int index, Flight flight) {
        flights.set(index, flight);
    }


    public static void main(String[] args) {
        final var path = Path.of(URI.create("file:///C:/Users/chris/IdeaProjects/FlightSimulator/src/main/resources"));  // TODO: Change this to the right path
        final var handler = FlightDataFileHandler.getBuilder()
                .withDirectory(path)
                .withAirportsFilename("SampleAirports.csv")
                .withDefaultAirlinesFilename()
                .withDefaultFlightsFilename()
                .withDefaultAeroplanesFilename()
                .build();

        final FlightData data;
        try {
            data = handler.readFlightData();
        } catch (IOException | FileHandlerException e) {
            // TODO: This is a bad solution, we'll need to catch it and create some error dialog
            throw new RuntimeException(e);
        }

        airports = data.airports();
        airlines = data.airlines();
        aeroplanes = data.aeroplanes();
        flights = data.flights();

        mainAppLoop();

        Path airlineReportFolderPath = Path.of(""); //TODO Get airlineReportFolderPath.
        writeAirlineReports(airlineReportFolderPath);
        Path flightDataPath = Path.of(""); //TODO Get flightDataPath.
        writeFlightData(flightDataPath);


    }
}
