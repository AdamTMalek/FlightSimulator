package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.GUI.Screen;
import com.github.adamtmalek.flightsimulator.models.io.FlightDataFileHandlerException;

import java.nio.file.Path;

public class App {

	public static void main(String[] args) {
		var flightTrackerController = new FlightTrackerController();
		Screen screen = new Screen(flightTrackerController);
		screen.setVisible(true);
	}
}
