package com.github.adamtmalek.flightsimulator.gui;

import com.github.adamtmalek.flightsimulator.FlightDataHandler;
import com.github.adamtmalek.flightsimulator.FlightDataHandlerImpl;
import com.github.adamtmalek.flightsimulator.io.FlightData;
import com.github.adamtmalek.flightsimulator.io.FlightDataFileHandlerException;
import com.github.adamtmalek.flightsimulator.models.Aeroplane;
import com.github.adamtmalek.flightsimulator.models.Airline;
import com.github.adamtmalek.flightsimulator.models.Airport;
import com.github.adamtmalek.flightsimulator.models.Flight;
import com.github.adamtmalek.flightsimulator.validators.FlightPlanValidator;
import com.github.adamtmalek.flightsimulator.validators.FlightValidator;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class MainViewControllerImpl implements MainViewController {
	private MainView view;
	private final @NotNull FlightDataHandler flightDataHandler;
	private static final Path flightDataDirectory = Path.of("flight-data/");
	private static final Path flightsReportDirectory = Path.of("reports/");

	public MainViewControllerImpl(@NotNull MainView view, @NotNull FlightDataHandler flightDataHandler) {
		this.view = view;
		this.flightDataHandler = flightDataHandler;
	}

	public MainViewControllerImpl(@NotNull MainView view) {
		this(view, new FlightDataHandlerImpl());
	}

	public MainViewControllerImpl() {
		this.flightDataHandler = new FlightDataHandlerImpl();
		SwingUtilities.invokeLater(() -> view = new Screen(this));
	}

	@Override
	public void showView() {
		final var flightData = loadFlightData();
		SwingUtilities.invokeLater(() -> {
			view.setVisible(true);
			view.displayData(flightData);
		});
	}

	@Contract(pure = true)
	private @NotNull FlightData loadFlightData() {
		final var aeroplanesPath = getPathFromResourcesFlightData("aeroplanes.csv");
		final var airlinesPath = getPathFromResourcesFlightData("airlines.csv");
		final var airportsPath = getPathFromResourcesFlightData("airports.csv");
		final var flightsPath = getPathFromResourcesFlightData("flights.csv");

		try {
			return flightDataHandler.readFlightData(airportsPath, aeroplanesPath, airlinesPath, flightsPath);
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

		flightDataHandler.getFlightData()
				.flights()
				.stream()
				.filter(f -> f.flightID().equals(flight.flightID()))
				.findAny()
				.ifPresentOrElse(
						(f) -> showNotUniqueFlightIdError(),
						() -> {
							flightDataHandler.addFlight(flight);
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
				.ifPresent(this::readAndDisplayFlightData));
	}

	private void readAndDisplayFlightData(@NotNull FlightFilesPaths paths) {
		try {
			final var data = flightDataHandler.readFlightData(
					paths.airports(),
					paths.aeroplanes(),
					paths.airlines(),
					paths.flights()
			);
			SwingUtilities.invokeLater(() -> view.displayData(data));
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
