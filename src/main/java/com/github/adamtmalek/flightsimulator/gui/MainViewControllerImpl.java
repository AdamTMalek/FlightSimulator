package com.github.adamtmalek.flightsimulator.gui;

import com.github.adamtmalek.flightsimulator.FlightDataHandler;
import com.github.adamtmalek.flightsimulator.FlightDataHandlerImpl;
import com.github.adamtmalek.flightsimulator.io.FlightDataFileHandlerException;
import com.github.adamtmalek.flightsimulator.models.Flight;
import com.github.adamtmalek.flightsimulator.validators.FlightPlanValidator;
import com.github.adamtmalek.flightsimulator.validators.FlightValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import javax.swing.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

public class MainViewControllerImpl implements MainViewController {
	private @Nullable MainView view; // TODO: Nullable :(
	private final @NotNull FlightDataHandler flightDataHandler;
	private static final Path flightDataDirectory = Path.of("flight-data/");
	private static final Path flightsReportDirectory = Path.of("reports/");

	public MainViewControllerImpl(@Nullable MainView view, @NotNull FlightDataHandler flightDataHandler) {
		this.view = view;
		this.flightDataHandler = flightDataHandler;
	}

	public MainViewControllerImpl(@Nullable MainView view) {
		this(view, new FlightDataHandlerImpl());
	}

	public MainViewControllerImpl() {
		this(null, new FlightDataHandlerImpl());
	}

	@Override
	public void showView() {
		SwingUtilities.invokeLater(() -> {
			if (view == null) view = new Screen(this);
			view.setVisible(true);
			loadAndShowDefaultFlightData();
		});
	}

	private void loadAndShowDefaultFlightData() {
		assert view != null;

		final var aeroplanesPath = getPathFromResourcesFlightData("aeroplanes.csv");
		final var airlinesPath = getPathFromResourcesFlightData("airlines.csv");
		final var airportsPath = getPathFromResourcesFlightData("airports.csv");
		final var flightsPath = getPathFromResourcesFlightData("flights.csv");

		try {
			final var flightData = flightDataHandler.readFlightData(airportsPath, aeroplanesPath, airlinesPath, flightsPath);
			view.displayData(flightData);
		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e);
		}
	}

	private @NotNull Path getPathFromResourcesFlightData(@NotNull String name) {
		try {
			final var resourceName = String.format("cw-spec-data/%s", name);
			final var resource = Objects.requireNonNull(getClass().getClassLoader().getResource(resourceName));
			return Path.of(resource.toURI());
		} catch (URISyntaxException ex) {
			// If we're using toURI() function of URL from getResource(), then how can URISyntaxException be possibly thrown?
			// But we have to do something here - so let's throw a RuntimeException just to "handle" that possibility.
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void onAddFlightFormEdited() {
		assert view != null;
		SwingUtilities.invokeLater(() -> view.setAddButtonEnabled(shouldAddButtonBeEnabled()));
	}

	private boolean shouldAddButtonBeEnabled() {
		assert view != null;
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
		assert view != null;

		// TODO: We run all of this on EDT, it'd be better if we can just fetch the selected items
		//   but run the rest on the calling thread.
		SwingUtilities.invokeLater(() -> {
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
		});
	}

	private void showNotUniqueFlightIdError() {
		JOptionPane.showMessageDialog(new JFrame(), "Flight ID must be unique", "Error", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void onOpenFileClicked() {
		assert view != null;

		final OpenFileViewController openFileController = new OpenFileViewControllerImpl();
		openFileController.openDialog(view.getComponent())
				.ifPresent(this::readAndDisplayFlightData);
	}

	private void readAndDisplayFlightData(@NotNull FlightFilesPaths paths) {
		assert view != null;

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
		flightDataHandler.writeAirlineReports(flightsReportDirectory);
	}

	private void saveFlightData() {
		try {
			flightDataHandler.writeFlightData(flightDataDirectory);
		} catch (FlightDataFileHandlerException e) {
			JOptionPane.showMessageDialog(new JFrame(), e.getMessage(), "Failed to write flight data", JOptionPane.ERROR_MESSAGE);
		}
	}

	@TestOnly
	public static @NotNull Path getFlightDataDirectory() {
		return flightDataDirectory;
	}

	@TestOnly
	public static @NotNull Path getFlightsReportDirectory() {
		return flightsReportDirectory;
	}
}
