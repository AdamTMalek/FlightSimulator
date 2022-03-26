package com.github.adamtmalek.flightsimulator.gui;

import com.github.adamtmalek.flightsimulator.Simulator;
import com.github.adamtmalek.flightsimulator.io.FlightDataFileHandlerException;
import com.github.adamtmalek.flightsimulator.models.Airport;
import com.github.adamtmalek.flightsimulator.models.Flight;
import com.github.adamtmalek.flightsimulator.validators.FlightPlanValidator;
import com.github.adamtmalek.flightsimulator.validators.FlightValidator;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;

public class MainViewControllerImpl implements MainViewController {
	private MainView view;
	private final @NotNull Simulator simulator;

	public MainViewControllerImpl(@NotNull MainView view, @NotNull Simulator simulator) {
		this.view = view;
		this.simulator = simulator;
	}

	public MainViewControllerImpl(@NotNull Simulator simulator) {
		this.simulator = simulator;
		SwingUtilities.invokeLater(() -> view = new Screen(this, simulator));
	}

	@Override
	public void showView() {
		SwingUtilities.invokeLater(() -> {
			view.setVisible(true);
			try {
				simulator.readFlightData();
			} catch (FlightDataFileHandlerException e) {
				JOptionPane.showMessageDialog(new JFrame(), e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
	}

	@Override
	public void onAddFlightFormEdited() {
		SwingUtilities.invokeLater(() -> view.setAddButtonEnabled(shouldAddButtonBeEnabled()));
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
		try {
			addNewFlight();
		} catch (InterruptedException ignored) {
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	private void addNewFlight() throws InterruptedException, InvocationTargetException {
		final var airline = view.getSelectedAirline();
		final var aeroplane = view.getSelectedAeroplane();
		final Airport departureAirport = view.getSelectedDepartureAirport();
		final Airport destinationAirport = view.getSelectedArrivalAirport();
		final List<Airport.ControlTower> flightPlan = view.getFlightPlan();
		final ZonedDateTime departureDateTime = view.getDateTimeOfDeparture();
		final String flightNumber = view.getFlightNumber();

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

		simulator.getFlights()
				.stream()
				.filter(f -> f.flightID().equals(flight.flightID()))
				.findAny()
				.ifPresentOrElse(
						(f) -> showNotUniqueFlightIdError(),
						() -> {
							simulator.addFlight(flight);
							view.updateFlightList();
						}
				);
	}

	private void showNotUniqueFlightIdError() {
		JOptionPane.showMessageDialog(new JFrame(), "Flight ID must be unique", "Error", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void onOpenFileClicked() {
		final OpenFileViewController openFileController = new OpenFileViewControllerImpl();
		openFileController.openDialog(view.getComponent())
				.ifPresent(this::readFlightData);
	}

	private void readFlightData(@NotNull FlightFilesPaths paths) {
		try {
			simulator.readFlightData(
					paths.aeroplanes(),
					paths.airlines(),
					paths.airports(),
					paths.flights()
			);
		} catch (FlightDataFileHandlerException ex) {
			SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
					new JFrame(), ex.getMessage(), "Failed to read flight data", JOptionPane.ERROR_MESSAGE)
			);
		}
	}

	@Override
	public void onWindowClosing() {
		saveFlightData();
		saveFlightsReport();
	}

	private void saveFlightsReport() {
		simulator.writeAirlineReports();
	}

	private void saveFlightData() {
		try {
			simulator.writeFlightData();
		} catch (FlightDataFileHandlerException e) {
			JOptionPane.showMessageDialog(new JFrame(), e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
