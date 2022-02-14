package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.models.Aeroplane;
import com.github.adamtmalek.flightsimulator.models.Airline;
import com.github.adamtmalek.flightsimulator.models.Airport;
import com.github.adamtmalek.flightsimulator.models.Flight;
import com.github.adamtmalek.flightsimulator.models.io.FlightDataFileHandler;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;

public class App {

    private static ArrayList<Airport> airports;
    private static ArrayList<Airline> airlines;
    private static ArrayList<Aeroplane> aeroplanes;
    private static ArrayList<Flight> flights;


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

        try {
            airports = new ArrayList<Airport>(handler.readAirports());
            airlines = new ArrayList<Airline>();
            aeroplanes = new ArrayList<Aeroplane>();
            flights = new ArrayList<Flight>();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
