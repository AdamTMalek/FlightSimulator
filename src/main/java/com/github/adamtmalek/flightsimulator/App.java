package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.models.io.FileHandlerException;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

public class App {

    public static void main(String[] args) {
        var flightTrackerController = new FlightTrackerController();

        final var path = Path.of(URI.create("file:///C:/Users/chris/IdeaProjects/FlightSimulator/src/main/resources"));  // TODO: Change this to the right path

        try {
            flightTrackerController.readFlightData(path);
        } catch (IOException | FileHandlerException e) {
            throw new RuntimeException(e); //only have single Airports CSV, so an exception here is expected.
        }


    }
}
