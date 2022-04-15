package com.github.adamtmalek.flightsimulator.gui;

import com.github.adamtmalek.flightsimulator.FlightSimulationThreadManagement;
import com.github.adamtmalek.flightsimulator.Simulator;
import com.github.adamtmalek.flightsimulator.gui.mapView.MapView;
import com.github.adamtmalek.flightsimulator.gui.renderers.*;
import com.github.adamtmalek.flightsimulator.models.*;
import javafx.collections.SetChangeListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class Screen extends JFrame implements MainView {
	private static final @NotNull String windowTitle = "Flight Tracking System";
	private static final @NotNull String simulationToggleNotSelectedText = "Start Simulation";
	private static final @NotNull String simulationToggleSelectedText = "Pause Simulation";

	private JPanel panelMain;
	private JLabel textDistance;
	private JLabel textTime;
	private JLabel textFuelConsumption;
	private JLabel textCo2Emission;
	private JList<Flight> flightList;
//	private JTextArea flightPlan;
	private JComboBox<Airline> airlineBox;
	private JComboBox<Aeroplane> aeroplaneBox;
	private JComboBox<Airport> departureBox;
	private JComboBox<Airport> destinationBox;
	private JButton addButton;
	private JButton exitButton;
	private JFormattedTextField dateTimeField;
	private JTable flightPlanTable;
	private JTextField flightNumberTextField;
	private JToggleButton simulationToggle;
	private JTable flightPlanStatusTable;
	private MapView mapView = new MapView();
	private JSlider simulationSpeedSlider;
	private JLabel currentSimulationTimeLabel;
	private JTextArea logArea;
	private JSlider simulationTickSlider;
	private JSlider guiRefreshSlider;

	private final Hashtable<Integer, JLabel> sliderLabelMap = new Hashtable<Integer, JLabel>() {{
		put(slowSimulationSpeedIndex, new JLabel("Slow"));
		put(normalSimulationSpeedIndex, new JLabel("Standard"));
		put(fastSimulationSpeedIndex, new JLabel("Fast"));
	}};

	private final int slowSimulationSpeedIndex = 0;
	private final int normalSimulationSpeedIndex = 1;
	private final int fastSimulationSpeedIndex = 2;

	private final @NotNull DefaultListModel<Flight> flightsModel = new DefaultListModel<>();
	private final @NotNull DefaultTableModel flightPlanStatusTableModel = new DefaultTableModel();
	private final @NotNull MutableComboBoxModel<Airline> airlinesModel = new DefaultComboBoxModel<>();
	private final @NotNull MutableComboBoxModel<Aeroplane> aeroplanesModel = new DefaultComboBoxModel<>();
	private final @NotNull MutableComboBoxModel<Airport> departureAirportsModel = new DefaultComboBoxModel<>();
	private final @NotNull MutableComboBoxModel<Airport> destinationAirportModel = new DefaultComboBoxModel<>();

	private final @NotNull MainViewController controller;
	private final @NotNull Airport.ControlTower emptyControlTower = new Airport.ControlTower("(not selected)",
			"(not selected)", new GeodeticCoordinate(0, 0));

	private static final int MAX_CONTROL_TOWERS = 10;
	private static final int MAX_FLIGHT_ID_CHAR_LENGTH = 4;
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

	private final @NotNull Simulator simulator;

	public Screen(@NotNull MainViewController controller, @NotNull Simulator simulator) {
		super(windowTitle);
		this.controller = controller;
		this.simulator = simulator;
		this.setContentPane(this.panelMain);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();

		initializeComponents();

		addListenersForUpdatingAddButtonState();
		addButton.addActionListener(e -> controller.onAddFlightClicked());
		simulationToggle.addActionListener(e -> {
			final boolean selected = simulationToggle.isSelected();
			simulationToggle.setText(selected ? simulationToggleSelectedText : simulationToggleNotSelectedText);
			controller.onSimulationControlClicked(selected);
		});
		exitButton.addActionListener(e -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
		flightList.addListSelectionListener(e -> setSelectedFlight());
		addChangeListenerToSimulationControls();

		simulator.registerSimulationTimeObserver((observable, oldValue, newValue) -> {
			SwingUtilities.invokeLater(() -> currentSimulationTimeLabel.setText(dateTimeFormatter.format(newValue)));
		});

		final var guiLogger = GUILogger.getInstance();
		if (guiLogger != null) {
			guiLogger.setListener((level, message) -> SwingUtilities.invokeLater(() ->
				logArea.append("[%s]: %s\n".formatted(level, message))
			));
		}

		addOnExitEventHandler();
		addListenersToSimulatorCollections();
	}

	private void initializeComponents() {
		flightList.setCellRenderer(new FlightListCellRenderer());
		flightList.setModel(flightsModel);
		airlineBox.setRenderer(new AirlineListCellRenderer());
		airlineBox.setModel(airlinesModel);
		aeroplaneBox.setRenderer(new AeroplaneListCellRenderer());
		aeroplaneBox.setModel(aeroplanesModel);
		departureBox.setRenderer(new AirportListCellRenderer());
		departureBox.setModel(departureAirportsModel);
		destinationBox.setRenderer(new AirportListCellRenderer());
		destinationBox.setModel(destinationAirportModel);
		flightPlanStatusTable.setModel(flightPlanStatusTableModel);
		flightPlanStatusTableModel.addColumn("ControlTower");
		flightPlanStatusTableModel.addColumn("Status");
		simulationSpeedSlider.setLabelTable(sliderLabelMap);
		guiRefreshSlider.setLabelTable(sliderLabelMap);
		initializeTickPeriodSlider();
	}

	private void initializeTickPeriodSlider() {
		final var labelMap = new Hashtable<Integer, JLabel>() {{
			put(0, new JLabel("5 minutes"));
			put(1, new JLabel("15 minutes"));
			put(2, new JLabel("30 minutes"));
			put(3, new JLabel("45 minutes"));
			put(4, new JLabel("1 hour"));
		}};

		simulationTickSlider.setLabelTable(labelMap);
	}

	private void addChangeListenerToSimulationControls() {
		simulationSpeedSlider.addChangeListener(e -> {
			double selectedThreadFrequency = switch (simulationSpeedSlider.getValue()) {
				case slowSimulationSpeedIndex -> FlightSimulationThreadManagement.MINIMUM_THREAD_FREQUENCY;
				case normalSimulationSpeedIndex -> FlightSimulationThreadManagement.DEFAULT_THREAD_FREQUENCY;
				case fastSimulationSpeedIndex -> FlightSimulationThreadManagement.MAXIMUM_THREAD_FREQUENCY;
				default -> throw new RuntimeException("Unexpected simulation speed slider value: %d".formatted(
						simulationSpeedSlider.getValue()
				));
			};

			controller.onSimulationSpeedChange(selectedThreadFrequency);
		});

		guiRefreshSlider.addChangeListener(e -> {
			double selectedThreadFrequency = switch (guiRefreshSlider.getValue()) {
				case slowSimulationSpeedIndex -> FlightSimulationThreadManagement.MINIMUM_GUI_UPDATE_FREQUENCY;
				case normalSimulationSpeedIndex -> FlightSimulationThreadManagement.DEFAULT_GUI_UPDATE_FREQUENCY;
				case fastSimulationSpeedIndex -> FlightSimulationThreadManagement.MAXIMUM_GUI_UPDATE_FREQUENCY;
				default -> throw new RuntimeException("Unexpected simulation speed slider value: %d".formatted(
						guiRefreshSlider.getValue()
				));
			};

			controller.onGuiRefreshSpeedChange(selectedThreadFrequency);
		});

		simulationTickSlider.addChangeListener(e -> {
			double selectedThreadFrequency = switch (simulationTickSlider.getValue()) {
				case 0 -> FlightSimulationThreadManagement.FLIGHT_SIM_FREQ_5_MINUTES;
				case 1 -> FlightSimulationThreadManagement.FLIGHT_SIM_FREQ_15_MINUTES;
				case 2 -> FlightSimulationThreadManagement.FLIGHT_SIM_FREQ_30_MINUTES;
				case 3 -> FlightSimulationThreadManagement.FLIGHT_SIM_FREQ_45_MINUTES;
				case 4 -> FlightSimulationThreadManagement.FLIGHT_SIM_FREQ_60_MINUTES;
				default -> throw new RuntimeException("Unexpected simulation speed slider value: %d".formatted(
						simulationTickSlider.getValue()
				));
			};

			controller.onSimulationTickPeriodChange(selectedThreadFrequency);
		});
	}

	private void addListenersForUpdatingAddButtonState() {
		Stream.of(airlineBox, aeroplaneBox, departureBox,
						destinationBox, flightPlanTable, dateTimeField,
						flightNumberTextField)
				.forEach(e -> e.addPropertyChangeListener(evt -> controller.onAddFlightFormEdited()));
	}

	private void setSelectedFlight() {
		final var flight = flightList.getSelectedValue();
		if (flight == null) return;

		textDistance.setText(Double.toString(flight.estimatedTotalDistanceToTravel()));
		textFuelConsumption.setText(Double.toString(flight.estimatedFuelConsumption()));
		textCo2Emission.setText(Double.toString(flight.estimatedCO2Produced()));
		textTime.setText(dateTimeFormatter.format(flight.departureDate()));

		JLabel doneImage = getImage("DONE");
		JLabel currentImage = getImage("CURRENT");

		Airport.ControlTower ct;
		int idxLastControlTower = flight.controlTowersToCross().size() - 1;
		int idxCurrentControlTower = flight.flightStatus().getControlTowersPassed().size() - 1;
		List<Airport.ControlTower> controlTower = flight.controlTowersToCross();

		for(int idx = 0; idx <= idxCurrentControlTower; idx++){
			ct = controlTower.get(idx);
			if(idx == idxCurrentControlTower && idx != idxLastControlTower){
				flightPlanStatusTableModel.addRow(
						new Object[]{ct.code, currentImage}
				);
			} else {
				flightPlanStatusTableModel.addRow(
						new Object[]{ct.code, doneImage}
				);
			}
		}
	}

	private void addListenersToSimulatorCollections() {
		simulator.addFlightCollectionListener(change -> handleChange(flightList, flightsModel, change));
		simulator.addFlightCollectionListener(change -> mapView.handleChange(flightsModel));
		simulator.addAirlineCollectionListener(change -> handleChange(airlineBox, airlinesModel, change));
		simulator.addAeroplaneCollectionListener(change -> handleChange(aeroplaneBox, aeroplanesModel, change));
		simulator.addAirportCollectionListener(change -> {
			handleChange(departureBox, departureAirportsModel, change);
			handleChange(destinationBox, destinationAirportModel, change);
			SwingUtilities.invokeLater(this::populateFlightPlanTable);
		});
	}

	private <T> void handleChange(@NotNull JComponent component,
																@NotNull DefaultListModel<T> model,
																@NotNull SetChangeListener.Change<? extends T> change) {
		SwingUtilities.invokeLater(() -> {
			if (change.wasAdded()) {
				model.addElement(change.getElementAdded());
			} else {
				model.removeElement(change.getElementRemoved());
			}

			component.updateUI();
		});
	}

	private <T> void handleChange(@NotNull JComponent component,
																@NotNull MutableComboBoxModel<T> model,
																@NotNull SetChangeListener.Change<? extends T> change) {
		SwingUtilities.invokeLater(() -> {
			if (change.wasAdded()) {
				model.addElement(change.getElementAdded());
			} else {
				model.removeElement(change.getElementRemoved());
			}

			component.updateUI();
		});
	}

	@Override
	public @NotNull Component getComponent() {
		return this;
	}

	@Override
	public @Nullable String getFlightNumber() {
		return flightNumberTextField.getText();
	}

	@Override
	public @Nullable Airline getSelectedAirline() {
		return (Airline) airlineBox.getSelectedItem();
	}

	@Override
	public @Nullable Aeroplane getSelectedAeroplane() {
		return (Aeroplane) aeroplaneBox.getSelectedItem();
	}

	@Override
	public @Nullable Airport getSelectedDepartureAirport() {
		return (Airport) departureBox.getSelectedItem();
	}

	@Override
	public @Nullable Airport getSelectedArrivalAirport() {
		return (Airport) destinationBox.getSelectedItem();
	}

	@Override
	public ZonedDateTime getDateTimeOfDeparture() {
		try {
			return LocalDateTime.parse((String) dateTimeField.getValue(), dateTimeFormatter).atZone(ZoneId.systemDefault());
		} catch (DateTimeParseException ex) {
			return null;
		}
	}

	@Override
	public @NotNull List<Airport.ControlTower> getFlightPlan() {
		return IntStream.range(0, MAX_CONTROL_TOWERS)
				.mapToObj(i -> flightPlanTable.getModel().getValueAt(0, i))
				.filter(Objects::nonNull)
				.filter(e -> !e.equals(emptyControlTower))
				.map(e -> (Airport.ControlTower) e)
				.toList();
	}

	private void populateFlightPlanTable() {
		final var controlTowers = IntStream.range(0, departureAirportsModel.getSize())
				.mapToObj(departureAirportsModel::getElementAt)
				.map(airport -> airport.controlTower);

		final var controlTowerChoices = Stream.concat(controlTowers, Stream.of(emptyControlTower)).toList();

		IntStream.range(0, MAX_CONTROL_TOWERS).forEach(i -> {
			flightPlanTable.getColumnModel()
					.getColumn(i)
					.setCellEditor(new ComboBoxCellEditor(controlTowerChoices));

			flightPlanTable.getColumnModel()
					.getColumn(i)
					.setCellRenderer(new ControlTowerTableCellRenderer());
		});
	}

	@Override
	public void updateFlightList() {
		flightList.updateUI();
	}

	@Override
	public void setAddButtonEnabled(boolean enabled) {
		addButton.setEnabled(enabled);
	}

	@Override
	public void resetSelections() {
		IntStream.range(0, MAX_CONTROL_TOWERS)
				.forEach(i -> flightPlanTable.setValueAt(emptyControlTower, 0, i));
		flightPlanTable.updateUI();

		flightNumberTextField.setText("");
		if (airlineBox.getModel().getSize() > 0) airlineBox.setSelectedIndex(0);
		if (aeroplaneBox.getModel().getSize() > 0) aeroplaneBox.setSelectedIndex(0);
		if (departureBox.getModel().getSize() > 0) {  // If departure box is not empty, then destination box is not either
			departureBox.setSelectedIndex(0);
			destinationBox.setSelectedIndex(0);
		}
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
				controller.onOpenFileClicked();
			}
		});
		fileMenu.add(openItem);
		return fileMenu;
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
				controller.onWindowClosing();
				super.windowClosing(e);
			}
		});
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected ImageIcon createImageIcon(String path,
																			String description) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	private JLabel getImage(String imgType){
		ImageIcon img;
		if (Objects.equals(imgType, "DONE")){
			img = createImageIcon("resources/done.png",
					"Image for passed control tower");
		} else {
			img = createImageIcon("resources/plane.png",
					"Image for current control tower");
		}
		return new JLabel(img);
	}
}
