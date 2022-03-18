package com.github.adamtmalek.flightsimulator.gui;

public interface MainViewController {
	void showView();

	void onOpenFileClicked();

	void onAddFlightFormEdited();

	void onAddFlightClicked();

	void onWindowClosing();
}
