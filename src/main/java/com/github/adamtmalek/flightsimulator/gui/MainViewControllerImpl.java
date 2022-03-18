package com.github.adamtmalek.flightsimulator.gui;

import com.github.adamtmalek.flightsimulator.FlightTrackerController;
import com.github.adamtmalek.flightsimulator.io.FlightDataFileHandlerException;
import com.github.adamtmalek.flightsimulator.models.Flight;
import com.github.adamtmalek.flightsimulator.validators.FlightPlanValidator;
import com.github.adamtmalek.flightsimulator.validators.FlightValidator;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;
import java.util.stream.Stream;

public class MainViewControllerImpl implements MainViewController {
	private final @NotNull MainView view = new Screen(this);
	private final @NotNull FlightTrackerController flightTrackerController = new FlightTrackerController();

	@Override
	public void showView() {
		view.getComponent().setVisible(true);
	}

	@Override
	public void onAddFlightFormEdited() {
		view.setAddButtonEnabled(shouldAddButtonBeEnabled());
	}

	private boolean shouldAddButtonBeEnabled() {
		return view.getDateTimeOfDeparture() != null
				&& view.getSelectedAirline() != null
				&& view.getSelectedAeroplane() != null
				&& view.getSelectedDepartureAirport() != null
				&& view.getSelectedArrivalAirport() != null
				&& view.getFlightPlan().size() >= 2
				&& view.getFlightNumber() != null && !view.getFlightNumber().isEmpty();
	}

	@Override
	public void onAddFlightClicked() {
		addNewFlight();
	}

	private void addNewFlight() {
		final var airline = view.getSelectedAirline();
		final var aeroplane = view.getSelectedAeroplane();
		final var departureAirport = view.getSelectedDepartureAirport();
		final var destinationAirport = view.getSelectedArrivalAirport();
		final var flightPlan = view.getFlightPlan();
		final var departureDateTime = view.getDateTimeOfDeparture();
		final var flightNumber = view.getFlightNumber();

		assert airline != null;
		assert aeroplane != null;
		assert departureAirport != null;
		assert destinationAirport != null;
		assert departureDateTime != null;
		assert flightNumber != null && !flightNumber.isEmpty();

		final var invalidResults = Stream.of(
						new FlightValidator(departureAirport).validate(destinationAirport),
						new FlightPlanValidator(departureAirport, destinationAirport).validate(flightPlan)
				).filter(e -> !e.isValid())
				.toList();

		if (!invalidResults.isEmpty()) {
			invalidResults.forEach(result ->
					JOptionPane.showMessageDialog(new JFrame(), result.reason(), "Error", JOptionPane.ERROR_MESSAGE));
			return;
		}
		final var flight = Flight.buildWithSerialNumber(flightNumber, airline, aeroplane,
				departureAirport, destinationAirport, departureDateTime, flightPlan);

		flightTrackerController.getFlightData()
				.flights()
				.stream()
				.filter(f -> f.flightID().equals(flight.flightID()))
				.findAny()
				.ifPresentOrElse(
						(f) -> JOptionPane.showMessageDialog(
								new JFrame(),
								"Flight ID must be unique",
								"Error",
								JOptionPane.ERROR_MESSAGE
						),
						() -> {
							flightTrackerController.addFlight(flight);
							view.updateFlightList();
						}
				);
	}

	@Override
	public void onOpenFileClicked() {
		final var fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (fileChooser.showOpenDialog(view.getComponent()) != JFileChooser.APPROVE_OPTION) return;

		File chosenDirectory = fileChooser.getSelectedFile();
		File[] csvFiles = chosenDirectory.listFiles(pathname -> pathname.getName().endsWith(".csv"));

		if (csvFiles == null || csvFiles.length < 4) {
			JOptionPane.showMessageDialog(new JFrame(), "Directory has to contain at least 4 CSV files", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		final var filePicker = new FilePicker(chosenDirectory.toPath());
		final var selectedPaths = filePicker.showDialog();

		if (selectedPaths == null) {
			return;
		}

		try {
			flightTrackerController.readFlightData(
					selectedPaths.airports(),
					selectedPaths.aeroplanes(),
					selectedPaths.airlines(),
					selectedPaths.flights()
			);
			view.displayData(this.flightTrackerController.getFlightData());
		} catch (FlightDataFileHandlerException ex) {
			JOptionPane.showMessageDialog(new JFrame(), ex.getMessage(), "Failed to read flight data", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void onWindowClosing() {
		saveFlightData();
		saveFlightsReport();
	}

	private void saveFlightsReport() {
		flightTrackerController.writeAirlineReports(Path.of("reports/"));
	}

	private void saveFlightData() {
		try {
			flightTrackerController.writeFlightData(Path.of("flight-data/"));
		} catch (FlightDataFileHandlerException e) {
			JOptionPane.showMessageDialog(new JFrame(), e.getMessage(), "Failed to write flight data", JOptionPane.ERROR_MESSAGE);
		}
	}
}
