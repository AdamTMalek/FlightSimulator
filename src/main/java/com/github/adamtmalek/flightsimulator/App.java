package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.gui.MainViewController;
import com.github.adamtmalek.flightsimulator.gui.MainViewControllerImpl;

public class App {

	public static void main(String[] args) {
		final var simulator = new Simulator();
		final var controller = new MainViewControllerImpl(simulator);
		controller.showView();
	}
}
