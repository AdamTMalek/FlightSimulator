package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.gui.MainViewController;
import com.github.adamtmalek.flightsimulator.gui.MainViewControllerImpl;

public class App {

	public static void main(String[] args) {
		final MainViewController controller = new MainViewControllerImpl();
		controller.showView();
	}
}
