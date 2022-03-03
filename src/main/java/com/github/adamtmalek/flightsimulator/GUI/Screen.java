package com.github.adamtmalek.flightsimulator.GUI;

import com.github.adamtmalek.flightsimulator.FlightTrackerController;
import com.github.adamtmalek.flightsimulator.models.Aeroplane;
import com.github.adamtmalek.flightsimulator.models.Airline;
import com.github.adamtmalek.flightsimulator.models.Airport;
import com.github.adamtmalek.flightsimulator.models.Flight;
import com.github.adamtmalek.flightsimulator.models.io.FlightData;
import com.github.adamtmalek.flightsimulator.models.io.FlightDataFileHandlerException;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


public class Screen extends JFrame implements ActionListener {
	private JPanel panelMain;
	private JPanel panelTop;
	protected JPanel panelBottom;
	protected JPanel panelTopLeft;
	protected JPanel panelTopRight;
	protected JTextField textDistance;
	protected JTextField textTime;
	protected JTextField textFuelConsumption;
	protected JTextField textCo2Emission;
	protected JList<Flight> flightList;
	protected JTextArea flightPlan;
	protected JPanel addFlightPanel;
	protected JPanel addFlightPlanPanel;
	private JComboBox airlineBox;
	private JComboBox aeroplaneBox;
	private JComboBox depatureBox;
	protected JComboBox destinationBox;
	protected JComboBox flightPlanBox1;
	protected JComboBox flightPlanBox2;
	protected JComboBox flightPlanBox3;
	protected JComboBox comboBox8;
	protected JComboBox comboBox9;
	protected JComboBox comboBox10;
	protected JComboBox comboBox11;
	protected JComboBox comboBox12;
	protected JComboBox comboBox13;
	protected JButton addButton;
	protected JButton exitButton;
	protected JTextField ddMmYyyyTextField;
	protected DefaultListModel flightListModel;
	protected DefaultComboBoxModel airlineListModel;
	protected DefaultComboBoxModel aeroplaneListModel;
	protected DefaultComboBoxModel depatureListModel;
	protected DefaultComboBoxModel destinationListModel;
	protected DefaultComboBoxModel flightPlan1ListModel;
	protected DefaultComboBoxModel flightPlan2ListModel;
	protected DefaultComboBoxModel flightPlan3ListModel;
	protected List<Flight> flights;
	protected List<Airline> airlines;
	protected List<Aeroplane> aeroplanes;
	protected List<Airport> airports;
	protected Aeroplane selectedAeroplane;
	protected Airport selectedDepature;
	protected Airport selectedDestination;
	public FlightTrackerController flightTrackerController;
	public List<Airport.ControlTower> selectedFlightPlan;

	public Screen() throws FlightDataFileHandlerException {
		super("Flight Tracking System");
		this.setContentPane(this.panelMain);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();

		this.flightTrackerController = new FlightTrackerController();
		Path fileDirectory = Path.of("src/test/resources/flight-data");
		this.flightTrackerController.readFlightData(fileDirectory);
		FlightData flightData = this.flightTrackerController.getFlightData();

		this.flights = flightData.flights();
		this.airlines = flightData.airlines();
		this.aeroplanes = flightData.aeroplanes();
		this.airports = flightData.airports();


		flightListModel = new DefaultListModel<>();
		flightList.setModel(flightListModel);

		airlineListModel = new DefaultComboBoxModel<>();
		airlineBox.setModel(airlineListModel);

		aeroplaneListModel = new DefaultComboBoxModel<>();
		aeroplaneBox.setModel(aeroplaneListModel);

		depatureListModel = new DefaultComboBoxModel<>();
		depatureBox.setModel(depatureListModel);

		destinationListModel = new DefaultComboBoxModel<>();
		destinationBox.setModel(destinationListModel);

		flightPlan1ListModel = new DefaultComboBoxModel<>();
		flightPlanBox1.setModel(flightPlan1ListModel);

		flightPlan2ListModel = new DefaultComboBoxModel<>();
		flightPlanBox2.setModel(flightPlan2ListModel);

		flightPlan3ListModel = new DefaultComboBoxModel<>();
		flightPlanBox3.setModel(flightPlan3ListModel);

		this.refreshFlightList();
		this.addAirlinesList();
		this.addAeroplanesList();
		this.addAirportsList();

		airlineBox.addActionListener(this);
		aeroplaneBox.addActionListener(this);
		depatureBox.addActionListener(this);
		destinationBox.addActionListener(this);
		flightPlanBox1.addActionListener(this);
		flightPlanBox2.addActionListener(this);
		flightPlanBox3.addActionListener(this);
		addButton.addActionListener(this);

		flightList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int flightIndex = flightList.getSelectedIndex();
				if (flightIndex >= 0) {
					Flight flight = flights.get(flightIndex);
					textDistance.setText(Double.toString(flight.distanceTravelled()));
					textFuelConsumption.setText(Double.toString(flight.estimatedFuelConsumption()));
					textCo2Emission.setText(Double.toString(flight.estimatedCO2Produced()));
					List<Airport.ControlTower> flightControlTowers = flight.controlTowersToCross();
					StringBuffer allTowers = new StringBuffer();
					for (var tower : flightControlTowers) {
						allTowers.append(tower.code);
						allTowers.append('\n');
					}
					flightPlan.setText(allTowers.toString());
				}
			}
		});

	}

	public void actionPerformed(ActionEvent e) {
		this.selectedFlightPlan = new ArrayList<>();
		double distanceTravelled;
		double estimatedFuelConsumption;
		double estimatedCO2Produced;
		int newIndex;

		if (e.getSource() == airlineBox) {
			int airlineIndex = airlineBox.getSelectedIndex();
			Airline airline = airlines.get(airlineIndex);
		}
		if (e.getSource() == aeroplaneBox) {
			int aeroplaneIndex = aeroplaneBox.getSelectedIndex();
			this.selectedAeroplane = aeroplanes.get(aeroplaneIndex);
		}
		if (e.getSource() == depatureBox) {
			int depatureIndex = depatureBox.getSelectedIndex();
			this.selectedDepature = airports.get(depatureIndex);
		}
		if (e.getSource() == destinationBox) {
			int destinationIndex = destinationBox.getSelectedIndex();
			this.selectedDestination = airports.get(destinationIndex);
		}
		if (e.getSource() == flightPlanBox1) {
			int flightPlanIndex = flightPlanBox1.getSelectedIndex();
			this.selectedFlightPlan.add(airports.get(flightPlanIndex).controlTower);
			System.out.println(this.selectedFlightPlan);
		}
		if (e.getSource() == flightPlanBox2) {
			int flightPlanIndex = flightPlanBox2.getSelectedIndex();
			this.selectedFlightPlan.add(airports.get(flightPlanIndex).controlTower);
			System.out.println(this.selectedFlightPlan);
		}
		if (e.getSource() == flightPlanBox3) {
			int flightPlanIndex = flightPlanBox3.getSelectedIndex();
			this.selectedFlightPlan.add(airports.get(flightPlanIndex).controlTower);
			System.out.println(this.selectedFlightPlan);
		}

		ZonedDateTime zonedDateTimeNow = ZonedDateTime.now(ZoneId.of("UTC"));


	}

	public void refreshFlightList() {
		flightListModel.removeAllElements();
		for (Flight flight : flights) {
			flightListModel.addElement(flight.flightID());
		}
	}

	public void addAirlinesList() {
		for (Airline airline : airlines) {
			airlineListModel.addElement(airline.name());
		}
	}

	public void addAeroplanesList() {
		for (Aeroplane aeroplane : aeroplanes) {
			aeroplaneListModel.addElement(aeroplane.model());
		}
	}

	public void addAirportsList() {
		for (Airport airport : airports) {
			depatureListModel.addElement(airport.name);
			destinationListModel.addElement(airport.name);
			flightPlan1ListModel.addElement(airport.controlTower.name);
			flightPlan2ListModel.addElement(airport.controlTower.name);
			flightPlan3ListModel.addElement(airport.controlTower.name);
		}
	}

	public void addNewFlight(Flight flight) {
		this.flightTrackerController.addFlight(flight);
		this.refreshFlightList();
	}

	public JComboBox getAirlineBox() {
		return airlineBox;
	}

	public JComboBox getDepatureBox() {
		return depatureBox;
	}

	public JButton getAddButton() {
		return addButton;
	}

	//for testing
	public static void main(String[] args) throws FlightDataFileHandlerException {
		var flightTrackerController = new FlightTrackerController();

		final var path = Path.of("src/test/resources/flight-data");  // TODO: Change this to the right path

		try {
			final var flightData = flightTrackerController.readFlightData(path).getFlightData();

			for (final var flight : flightData.flights()) {
				System.out.println(flight.flightID());
			}

		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e);
		}

		Screen screen = new Screen();
		screen.setVisible(true);
	}
}





