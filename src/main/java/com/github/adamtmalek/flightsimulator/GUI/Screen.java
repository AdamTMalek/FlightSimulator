package com.github.adamtmalek.flightsimulator.GUI;

import com.github.adamtmalek.flightsimulator.FlightTrackerController;
import com.github.adamtmalek.flightsimulator.models.Aeroplane;
import com.github.adamtmalek.flightsimulator.models.Airline;
import com.github.adamtmalek.flightsimulator.models.Airport;
import com.github.adamtmalek.flightsimulator.models.Flight;
import com.github.adamtmalek.flightsimulator.models.io.FlightData;
import com.github.adamtmalek.flightsimulator.models.io.FlightDataFileHandlerException;
import com.github.adamtmalek.flightsimulator.validators.FlightPlanValidator;
import com.github.adamtmalek.flightsimulator.validators.FlightValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.nio.file.Path;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class Screen extends JFrame {
	private JPanel panelMain;
	private JPanel panelTop;
	private JPanel panelBottom;
	private JPanel panelTopLeft;
	private JPanel panelTopRight;
	private JTextField textDistance;
	private JTextField textTime;
	private JTextField textFuelConsumption;
	private JTextField textCo2Emission;
	private JList<Flight> flightList;
	private JTextArea flightPlan;
	private JPanel addFlightPanel;
	private JPanel addFlightPlanPanel;
	private JComboBox<Airline> airlineBox;
	private JComboBox<Aeroplane> aeroplaneBox;
	private JComboBox<Airport> departureBox;
	private JComboBox<Airport> destinationBox;
	private JButton addButton;
	private JButton exitButton;
	private JFormattedTextField dateTimeField;
	private JTable flightPlanTable;
	private final FlightTrackerController flightTrackerController = new FlightTrackerController();

	private static final int MAX_CONTROL_TOWERS = 10;
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

	public Screen() throws FlightDataFileHandlerException {
		super("Flight Tracking System");
		this.setContentPane(this.panelMain);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();

		initializeComponents(readFlightData());

		addListenersForUpdatingAddButtonState();
		addButton.addActionListener(e -> addNewFlight());

		addFlightSelectionListener();
	}

	private void addFlightSelectionListener() {
		flightList.addListSelectionListener(e -> {
			final var flight = flightList.getSelectedValue();
			if (flight == null) return;

			textDistance.setText(Double.toString(flight.distanceTravelled()));
			textFuelConsumption.setText(Double.toString(flight.estimatedFuelConsumption()));
			textCo2Emission.setText(Double.toString(flight.estimatedCO2Produced()));

			final var flightPlanText = flight.controlTowersToCross()
					.stream()
					.map(t -> t.code)
					.collect(Collectors.joining("\n"));
			flightPlan.setText(flightPlanText);
		});
	}

	private @NotNull FlightData readFlightData() throws FlightDataFileHandlerException {
		Path fileDirectory = Path.of("src/test/resources/flight-data");
		this.flightTrackerController.readFlightData(fileDirectory);
		return this.flightTrackerController.getFlightData();
	}

	private void initializeComponents(@NotNull FlightData flightData) {
		final var flightListModel = new DefaultListModel<Flight>();
		flightListModel.addAll(flightData.flights());
		flightList.setModel(flightListModel);
		flightList.setCellRenderer(new FlightListCellRenderer());

		final var airlineListModel = new DefaultComboBoxModel<Airline>();
		airlineListModel.addAll(flightData.airlines());
		airlineBox.setModel(airlineListModel);
		airlineBox.setRenderer(new AirlineListCellRenderer());
		airlineBox.setSelectedIndex(0);

		final var aeroplaneListModel = new DefaultComboBoxModel<Aeroplane>();
		aeroplaneListModel.addAll(flightData.aeroplanes());
		aeroplaneBox.setModel(aeroplaneListModel);
		aeroplaneBox.setRenderer(new AeroplaneListCellRenderer());
		aeroplaneBox.setSelectedIndex(0);

		final var departureListModel = new DefaultComboBoxModel<Airport>();
		departureListModel.addAll(flightData.airports());
		departureBox.setModel(departureListModel);
		departureBox.setRenderer(new AirportListCellRenderer());
		departureBox.setSelectedIndex(0);

		final var destinationListModel = new DefaultComboBoxModel<Airport>();
		destinationListModel.addAll(flightData.airports());
		destinationBox.setModel(destinationListModel);
		destinationBox.setRenderer(new AirportListCellRenderer());
		destinationBox.setSelectedIndex(0);

		IntStream.range(0, MAX_CONTROL_TOWERS).forEach(i -> {
			flightPlanTable.getColumnModel()
					.getColumn(i)
					.setCellEditor(new ComboBoxCellEditor(flightData.airports().stream().map(e -> e.controlTower).toList()));

			flightPlanTable.getColumnModel()
					.getColumn(i)
					.setCellRenderer(new ControlTowerTableCellRenderer());
		});

		this.flightList.updateUI();
	}

	private void addListenersForUpdatingAddButtonState() {
		flightPlanTable.addPropertyChangeListener(evt -> updateAddButtonState());
		dateTimeField.addPropertyChangeListener(evt -> updateAddButtonState());
	}

	private void updateAddButtonState() {
		final var enabled = getDateTime() != null
				&& airlineBox.getSelectedItem() != null
				&& aeroplaneBox.getSelectedItem() != null
				&& departureBox.getSelectedItem() != null
				&& destinationBox.getSelectedItem() != null
				&& getFlightPlan().size() >= 2;
		addButton.setEnabled(enabled);
	}

	public void addNewFlight() {
		final var airline = (Airline) airlineBox.getSelectedItem();
		final var aeroplane = (Aeroplane) aeroplaneBox.getSelectedItem();
		final var departureAirport = (Airport) departureBox.getSelectedItem();
		final var destinationAirport = (Airport) destinationBox.getSelectedItem();
		final var flightPlan = getFlightPlan();
		final var departureDateTime = getDateTime();

		assert airline != null;
		assert aeroplane != null;
		assert departureAirport != null;
		assert destinationAirport != null;
		assert departureDateTime != null;

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

		// TODO: Get the serial number from input
		final var flight = Flight.buildWithSerialNumber("501", airline, aeroplane,
				departureAirport, destinationAirport, departureDateTime, flightPlan);

		((DefaultListModel<Flight>) this.flightList.getModel()).add(0, flight);
		flightList.updateUI();
	}

	private @Nullable ZonedDateTime getDateTime() {
		try {
			return LocalDateTime.parse((String) dateTimeField.getValue(), dateTimeFormatter).atZone(ZoneId.systemDefault());
		} catch (DateTimeParseException ex) {
			return null;
		}
	}

	private List<Airport.ControlTower> getFlightPlan() {
		return IntStream.range(0, MAX_CONTROL_TOWERS)
				.mapToObj(i -> flightPlanTable.getModel().getValueAt(0, i))
				.filter(Objects::nonNull)
				.map(e -> (Airport.ControlTower) e)
				.toList();
	}

	private void createUIComponents() {
		flightPlanTable = new JTable(1, MAX_CONTROL_TOWERS);
		flightPlanTable.setRowHeight(30);

		dateTimeField = new JFormattedTextField(createDateTimeFormatter());
		dateTimeField.setValue("03/03/2022 15:51");
		dateTimeField.updateUI();
	}

	private @NotNull MaskFormatter createDateTimeFormatter() {
		try {
			final var formatter = new MaskFormatter("##/##/#### ##:##");
			formatter.setCommitsOnValidEdit(true);
			return formatter;
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
}
