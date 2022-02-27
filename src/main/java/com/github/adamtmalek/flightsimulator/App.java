package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.models.io.FileHandlerException;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

public class App {

	public static void main(String[] args) {
		var flightTrackerController = new FlightTrackerController();

		final var path = Path.of(URI.create("file:///C:/Users/chris/IdeaProjects/FlightSimulator/src/test/resources/FlightDataFileHandlerTest"));  // TODO: Change this to the right path

		try {
			final var flightData = flightTrackerController.readFlightData(path).getFlightData();

			for (final var flight : flightData.flights()) {
				System.out.println(flight.flightID());
			}

		} catch (FileHandlerException e) {
			throw new RuntimeException(e);
		}


	}
}
