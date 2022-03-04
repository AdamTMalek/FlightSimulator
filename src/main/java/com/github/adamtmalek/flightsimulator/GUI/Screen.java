package com.github.adamtmalek.flightsimulator.GUI;

import com.github.adamtmalek.flightsimulator.GUI.models.BoundComboBoxModel;
import com.github.adamtmalek.flightsimulator.GUI.models.BoundListModel;
import com.github.adamtmalek.flightsimulator.interfaces.Controller;
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
import org.jetbrains.annotations.TestOnly;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
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
	private JTextField flightNumberTextField;
	private JLabel flightNumberLabel;
	private final @NotNull Controller flightTrackerController;

	private static final int MAX_CONTROL_TOWERS = 10;
	private static final int MAX_FLIGHT_ID_CHAR_LENGTH = 4;
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

	public Screen(@NotNull Controller controller) {
		super("Flight Tracking System");
		this.flightTrackerController = controller;
		this.setContentPane(this.panelMain);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();

		initializeComponents(readFlightData());

		addListenersForUpdatingAddButtonState();
		addButton.addActionListener(e -> addNewFlight());

		addFlightSelectionListener();
		updateAddButtonState();
		addOnExitEventHandler();
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

	private @NotNull FlightData readFlightData() {
		try {
			Path fileDirectory = Path.of("src/test/resources/flight-data");
			this.flightTrackerController.readFlightData(fileDirectory);
			return this.flightTrackerController.getFlightData();
		} catch (FlightDataFileHandlerException | IOException ex) {
			JOptionPane.showMessageDialog(new JFrame(), ex.getMessage(), "Failed to read flight data", JOptionPane.ERROR_MESSAGE);
			return new FlightData();
		}
	}

	private void initializeComponents(@NotNull FlightData flightData) {
		flightList.setModel(new BoundListModel<>(flightData.flights()));
		flightList.setCellRenderer(new FlightListCellRenderer());

		airlineBox.setModel(new BoundComboBoxModel<>(flightData.airlines()));
		airlineBox.setRenderer(new AirlineListCellRenderer());

		aeroplaneBox.setModel(new BoundComboBoxModel<>(flightData.aeroplanes()));
		aeroplaneBox.setRenderer(new AeroplaneListCellRenderer());

		departureBox.setModel(new BoundComboBoxModel<>(flightData.airports()));
		departureBox.setRenderer(new AirportListCellRenderer());

		destinationBox.setModel(new BoundComboBoxModel<>(flightData.airports()));
		destinationBox.setRenderer(new AirportListCellRenderer());

		if (!flightData.airlines().isEmpty()) airlineBox.setSelectedIndex(0);
		if (!flightData.aeroplanes().isEmpty()) aeroplaneBox.setSelectedIndex(0);
		if (!flightData.airports().isEmpty()) {
			departureBox.setSelectedIndex(0);
			destinationBox.setSelectedIndex(0);
		}

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
		Stream.of(airlineBox, aeroplaneBox, departureBox,
						destinationBox, flightPlanTable, dateTimeField,
						flightNumberTextField)
				.forEach(e -> e.addPropertyChangeListener(evt -> updateAddButtonState()));
	}

	private void updateAddButtonState() {
		final var enabled = getDateTime() != null
				&& airlineBox.getSelectedItem() != null
				&& aeroplaneBox.getSelectedItem() != null
				&& departureBox.getSelectedItem() != null
				&& destinationBox.getSelectedItem() != null
				&& getFlightPlan().size() >= 2
				&& !flightNumberTextField.getText().isEmpty();
		addButton.setEnabled(enabled);
	}

	public void addNewFlight() {
		final var airline = (Airline) airlineBox.getSelectedItem();
		final var aeroplane = (Aeroplane) aeroplaneBox.getSelectedItem();
		final var departureAirport = (Airport) departureBox.getSelectedItem();
		final var destinationAirport = (Airport) destinationBox.getSelectedItem();
		final var flightPlan = getFlightPlan();
		final var departureDateTime = getDateTime();
		final var flightNumber = flightNumberTextField.getText();

		assert airline != null;
		assert aeroplane != null;
		assert departureAirport != null;
		assert destinationAirport != null;
		assert departureDateTime != null;
		assert !flightNumber.isEmpty();

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

		flightTrackerController.addFlight(flight);
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

		flightNumberTextField = new JTextField();
		flightNumberTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (!Character.isDigit(e.getKeyChar()))
					e.consume();

				if (((JTextComponent) e.getComponent()).getText().length() >= MAX_FLIGHT_ID_CHAR_LENGTH)
					e.consume();
			}
		});
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

	private void addOnExitEventHandler() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				saveFlightsReport();
				super.windowClosing(e);
			}
		});
	}

	private void saveFlightsReport() {
		try {
			flightTrackerController.writeAirlineReports(Path.of("reports/"));
		} catch (FlightDataFileHandlerException e) {
			JOptionPane.showMessageDialog(new JFrame(), e.getMessage(), "Failed to read flight data", JOptionPane.ERROR_MESSAGE);
		}
	}

	@TestOnly
	@NotNull
	public JButton getAddButton() {
		return addButton;
	}

	@TestOnly
	@NotNull
	public JComboBox<Airport> getDepartureBox() {
		return departureBox;
	}

	@TestOnly
	@NotNull
	public JComboBox<Airline> getAirlineBox() {
		return airlineBox;
	}

	@TestOnly
	@NotNull
	public JTextField getFlightNumberTextField() {
		return flightNumberTextField;
	}

	@TestOnly
	@NotNull
	public JTable getFlightPlanTable() {
		return flightPlanTable;
	}
}
