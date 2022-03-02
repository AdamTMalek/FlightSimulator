package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.GUI.Screen;
import com.github.adamtmalek.flightsimulator.models.io.FlightDataFileHandlerException;

import java.nio.file.Path;

public class App {

	public static void main(String[] args) throws FlightDataFileHandlerException {
		var flightTrackerController = new FlightTrackerController();

		final var path = Path.of("src/test/resources/flight-data");  // TODO: Change this to the right path

		try {
			final var flightData = flightTrackerController.readFlightData(path).getFlightData();

			for (final var flight : flightData.flights()) {
				System.out.println(flight.flightID());
			}

		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e);
		}

		Screen screen = new Screen();
		screen.setVisible(true);
	}
}
