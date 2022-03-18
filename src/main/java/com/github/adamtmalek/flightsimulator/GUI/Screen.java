package com.github.adamtmalek.flightsimulator.GUI;

import com.github.adamtmalek.flightsimulator.GUI.models.BoundComboBoxModel;
import com.github.adamtmalek.flightsimulator.GUI.models.BoundListModel;
import com.github.adamtmalek.flightsimulator.interfaces.Controller;
import com.github.adamtmalek.flightsimulator.models.*;
import com.github.adamtmalek.flightsimulator.io.FlightData;
import com.github.adamtmalek.flightsimulator.io.FlightDataFileHandlerException;
import com.github.adamtmalek.flightsimulator.validators.FlightPlanValidator;
import com.github.adamtmalek.flightsimulator.validators.FlightValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.text.MaskFormatter;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class Screen extends JFrame {
	private JPanel panelMain;
	private JLabel textDistance;
	private JLabel textTime;
	private JLabel textFuelConsumption;
	private JLabel textCo2Emission;
	private JList<Flight> flightList;
	private JTextArea flightPlan;
	private JComboBox<Airline> airlineBox;
	private JComboBox<Aeroplane> aeroplaneBox;
	private JComboBox<Airport> departureBox;
	private JComboBox<Airport> destinationBox;
	private JButton addButton;
	private JButton exitButton;
	private JFormattedTextField dateTimeField;
	private JTable flightPlanTable;
	private JTextField flightNumberTextField;

	private final @NotNull Controller flightTrackerController;
	private final @NotNull Airport.ControlTower emptyControlTower = new Airport.ControlTower("(not selected)",
			"(not selected)", new GeodeticCoordinate(0, 0));

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
		exitButton.addActionListener(e -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));

		addFlightSelectionListener();
		updateAddButtonState();
		addOnExitEventHandler();
	}

	private void addFlightSelectionListener() {
		flightList.addListSelectionListener(e -> {
			final var flight = flightList.getSelectedValue();
			if (flight == null) return;

			textDistance.setText(Double.toString(flight.estimatedTotalDistancetoTravel()));
			textFuelConsumption.setText(Double.toString(flight.estimatedFuelConsumption()));
			textCo2Emission.setText(Double.toString(flight.estimatedCO2Produced()));
			textTime.setText(dateTimeFormatter.format(flight.departureDate()));

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

		IntStream.range(0, MAX_CONTROL_TOWERS).forEach(i -> {
			flightPlanTable.getColumnModel()
					.getColumn(i)
					.setCellEditor(new ComboBoxCellEditor(Stream.concat(
							flightData.airports().stream().map(e -> e.controlTower),
							Stream.of(emptyControlTower)
					).toList()));

			flightPlanTable.getColumnModel()
					.getColumn(i)
					.setCellRenderer(new ControlTowerTableCellRenderer());
		});

		resetComponents();

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

		flightTrackerController.getFlightData()
				.flights()
				.stream()
				.filter(f -> f.flightID().equals(flight.flightID()))
				.findAny()
				.ifPresentOrElse((f) -> {
					JOptionPane.showMessageDialog(new JFrame(), "Flight ID must be unique", "Error", JOptionPane.ERROR_MESSAGE);
				}, () -> {
					flightTrackerController.addFlight(flight);
					flightList.updateUI();
					resetComponents();
				});
	}

	@SuppressWarnings("unchecked")
	private void resetComponents() {
		IntStream.range(0, MAX_CONTROL_TOWERS)
				.forEach(i -> {
					flightPlanTable.setValueAt(emptyControlTower, 0, i);
				});
		flightPlanTable.updateUI();

		flightNumberTextField.setText("");
		if (airlineBox.getModel().getSize() > 0) airlineBox.setSelectedIndex(0);
		if (aeroplaneBox.getModel().getSize() > 0) aeroplaneBox.setSelectedIndex(0);
		if (departureBox.getModel().getSize() > 0) {  // If departure box is not empty, then destination box is not either
			departureBox.setSelectedIndex(0);
			destinationBox.setSelectedIndex(0);
		}
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
				.filter(e -> !e.equals(emptyControlTower))
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

		setJMenuBar(createMenuBar());
	}

	private @NotNull JMenuBar createMenuBar() {
		final var menuBar = new JMenuBar();
		menuBar.add(createFileMenu());
		return menuBar;
	}

	private @NotNull JMenu createFileMenu() {
		final var fileMenu = new JMenu("File");
		final var openItem = new JMenuItem(new AbstractAction("Open") {
			@Override
			public void actionPerformed(ActionEvent e) {
				onOpenClick();
			}
		});
		fileMenu.add(openItem);
		return fileMenu;
	}

	private void onOpenClick() {
		final var fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

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
			this.flightTrackerController.readFlightData(
					selectedPaths.airports(),
					selectedPaths.aeroplanes(),
					selectedPaths.airlines(),
					selectedPaths.flights()
			);
			initializeComponents(this.flightTrackerController.getFlightData());
		} catch (FlightDataFileHandlerException | IOException ex) {
			JOptionPane.showMessageDialog(new JFrame(), ex.getMessage(), "Failed to read flight data", JOptionPane.ERROR_MESSAGE);
		}
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
				saveFlightData();
				super.windowClosing(e);
			}
		});
	}

	private void saveFlightsReport() {
		try {
			flightTrackerController.writeAirlineReports(Path.of("reports/"));
		} catch (FlightDataFileHandlerException e) {
			JOptionPane.showMessageDialog(new JFrame(), e.getMessage(), "Failed to write flight report", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void saveFlightData() {
		try {
			flightTrackerController.writeFlightData(Path.of("flight-data/"));
		} catch (FlightDataFileHandlerException e) {
			JOptionPane.showMessageDialog(new JFrame(), e.getMessage(), "Failed to write flight data", JOptionPane.ERROR_MESSAGE);
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

	public JButton getExitButton() {
		return exitButton;
	}


}
