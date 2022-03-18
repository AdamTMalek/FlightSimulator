package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.gui.Screen;

public class App {

	public static void main(String[] args) {
		var flightTrackerController = new FlightTrackerController();
		Screen screen = new Screen(flightTrackerController);
		screen.setVisible(true);
	}
}
