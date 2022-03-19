package com.github.adamtmalek.flightsimulator.gui;

import com.github.adamtmalek.flightsimulator.FlightDataHandler;
import com.github.adamtmalek.flightsimulator.io.FlightDataFileHandlerException;
import com.github.adamtmalek.flightsimulator.models.Flight;
import com.github.adamtmalek.flightsimulator.validators.FlightPlanValidator;
import com.github.adamtmalek.flightsimulator.validators.FlightValidator;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.nio.file.Path;
import java.util.stream.Stream;

public class MainViewControllerImpl implements MainViewController {
	private final @NotNull MainView view = new Screen(this);
	private final @NotNull FlightDataHandler flightDataHandler = new FlightDataHandler();

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

		flightDataHandler.getFlightData()
				.flights()
				.stream()
				.filter(f -> f.flightID().equals(flight.flightID()))
				.findAny()
				.ifPresentOrElse(
						(f) -> showNotUniqueFlightIdError(),
						() -> {
							flightDataHandler.addFlight(flight);
							view.updateFlightList();
						}
				);
	}

	private void showNotUniqueFlightIdError() {
		JOptionPane.showMessageDialog(new JFrame(),"Flight ID must be unique","Error", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void onOpenFileClicked() {
		final OpenFileViewController openFileController = new OpenFileViewControllerImpl();
		openFileController.openDialog(view.getComponent())
				.ifPresent(this::readAndDisplayFlightData);
	}

	private void readAndDisplayFlightData(@NotNull FlightFilesPaths paths) {
		try {
			flightDataHandler.readFlightData(
					paths.airports(),
					paths.aeroplanes(),
					paths.airlines(),
					paths.flights()
			);
			view.displayData(this.flightDataHandler.getFlightData());
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
		flightDataHandler.writeAirlineReports(Path.of("reports/"));
	}

	private void saveFlightData() {
		try {
			flightDataHandler.writeFlightData(Path.of("flight-data/"));
		} catch (FlightDataFileHandlerException e) {
			JOptionPane.showMessageDialog(new JFrame(), e.getMessage(), "Failed to write flight data", JOptionPane.ERROR_MESSAGE);
		}
	}
}
