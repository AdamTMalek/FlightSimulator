package com.github.adamtmalek.flightsimulator.gui;

import com.github.adamtmalek.flightsimulator.Simulator;
import com.github.adamtmalek.flightsimulator.io.FlightDataFileHandlerException;
import com.github.adamtmalek.flightsimulator.models.Aeroplane;
import com.github.adamtmalek.flightsimulator.models.Airline;
import com.github.adamtmalek.flightsimulator.models.Airport;
import com.github.adamtmalek.flightsimulator.models.Flight;
import com.github.adamtmalek.flightsimulator.validators.FlightPlanValidator;
import com.github.adamtmalek.flightsimulator.validators.FlightValidator;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
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
		final AtomicReference<Airline> airline = new AtomicReference<>();
		final AtomicReference<Aeroplane> aeroplane = new AtomicReference<>();
		final AtomicReference<Airport> departureAirport = new AtomicReference<>();
		final AtomicReference<Airport> destinationAirport = new AtomicReference<>();
		final List<Airport.ControlTower> flightPlan = new ArrayList<>();
		final AtomicReference<ZonedDateTime> departureDateTime = new AtomicReference<>();
		final AtomicReference<String> flightNumber = new AtomicReference<>();

		SwingUtilities.invokeAndWait(() -> {
			airline.set(view.getSelectedAirline());
			aeroplane.set(view.getSelectedAeroplane());
			departureAirport.set(view.getSelectedDepartureAirport());
			destinationAirport.set(view.getSelectedArrivalAirport());
			flightPlan.addAll(view.getFlightPlan());
			departureDateTime.set(view.getDateTimeOfDeparture());
			flightNumber.set(view.getFlightNumber());
		});

		assert airline.get() != null;
		assert aeroplane.get() != null;
		assert departureAirport.get() != null;
		assert destinationAirport.get() != null;
		assert departureDateTime.get() != null;
		assert flightNumber.get() != null && !flightNumber.get().isEmpty();

		final var invalidResults = Stream.of(
						new FlightValidator(departureAirport.get()).validate(destinationAirport.get()),
						new FlightPlanValidator(departureAirport.get(), destinationAirport.get()).validate(flightPlan)
				).filter(e -> !e.isValid())
				.toList();

		if (!invalidResults.isEmpty()) {
			SwingUtilities.invokeLater(() -> invalidResults.forEach(result ->
					JOptionPane.showMessageDialog(new JFrame(), result.reason(), "Error", JOptionPane.ERROR_MESSAGE)));
			return;
		}
		final var flight = Flight.buildWithSerialNumber(flightNumber.get(), airline.get(), aeroplane.get(),
				departureAirport.get(), destinationAirport.get(), departureDateTime.get(), flightPlan);

		simulator.getFlights()
				.stream()
				.filter(f -> f.flightID().equals(flight.flightID()))
				.findAny()
				.ifPresentOrElse(
						(f) -> showNotUniqueFlightIdError(),
						() -> {
							simulator.addFlight(flight);
							SwingUtilities.invokeLater(() -> view.updateFlightList());
						}
				);
	}

	private void showNotUniqueFlightIdError() {
		SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(new JFrame(), "Flight ID must be unique", "Error", JOptionPane.ERROR_MESSAGE));

	}

	@Override
	public void onOpenFileClicked() {
		final OpenFileViewController openFileController = new OpenFileViewControllerImpl();
		SwingUtilities.invokeLater(() -> openFileController.openDialog(view.getComponent())
				.ifPresent(this::readFlightData));
	}

	private void readFlightData(@NotNull FlightFilesPaths paths) {
		try {
			simulator.readFlightData(
					paths.airports(),
					paths.aeroplanes(),
					paths.airlines(),
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
		simulator.writeFlightData();
	}
}
