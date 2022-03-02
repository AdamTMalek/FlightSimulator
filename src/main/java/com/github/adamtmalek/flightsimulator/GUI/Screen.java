package com.github.adamtmalek.flightsimulator.GUI;

import com.github.adamtmalek.flightsimulator.FlightTrackerController;
import com.github.adamtmalek.flightsimulator.models.Aeroplane;
import com.github.adamtmalek.flightsimulator.models.Airline;
import com.github.adamtmalek.flightsimulator.models.Airport;
import com.github.adamtmalek.flightsimulator.models.Flight;
import com.github.adamtmalek.flightsimulator.models.io.FileHandlerException;
import com.github.adamtmalek.flightsimulator.models.io.FlightData;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.nio.file.Path;
import java.util.List;


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
    private JComboBox airlineBox;
    private JComboBox aeroplaneBox;
    private JComboBox depatureBox;
    private JComboBox destinationBox;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JComboBox comboBox5;
    private JComboBox comboBox6;
    private JComboBox comboBox7;
    private JComboBox comboBox8;
    private JComboBox comboBox9;
    private JComboBox comboBox10;
    private JComboBox comboBox11;
    private JComboBox comboBox12;
    private JComboBox comboBox13;
    private JButton addButton;
    private JButton cancelButton;
    private JButton exitButton;
    private DefaultListModel flightListModel;
    private List<Flight> flights;
    private List<Airline> airlines;
    private List<Aeroplane> aeroplanes;
    private List<Airport> airports;
    private List<Airport.ControlTower> controlTowers;

    public Screen() throws FileHandlerException {
        super("Flight Tracking System");
        this.setContentPane(this.panelMain);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();

        FlightTrackerController flightTrackerController = new FlightTrackerController();
        Path fileDirectory = Path.of("src/test/resources/flight-data");
        flightTrackerController.readFlightData(fileDirectory);
        FlightData flightData = flightTrackerController.getFlightData();

        this.flights = flightData.flights();
        this.airlines = flightData.airlines();
        this.aeroplanes = flightData.aeroplanes();
        this.airports = flightData.airports();


        flightListModel = new DefaultListModel<>();
        flightList.setModel(flightListModel);

        this.refreshFlightList();
        this.addAirlinesList();
        this.addAeroplanesList();
        this.addAirportsList();

        flightList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int flightIndex = flightList.getSelectedIndex();
                if (flightIndex >= 0) {
                    Flight flight = flights.get(flightIndex);
                    textDistance.setText(Double.toString(flight.distanceTravelled()));
                    textFuelConsumption.setText(Double.toString(flight.estimatedFuelConsumption()));
                    textCo2Emission.setText(Double.toString(flight.estimatedCO2Produced()));
                    controlTowers = flight.controlTowersToCross();
                    StringBuffer allTowers = new StringBuffer();
                    for (var tower : controlTowers) {
                        allTowers.append(tower.code);
                        allTowers.append('\n');
                    }
                    flightPlan.setText(allTowers.toString());
                }
            }
        });

    }

    public void refreshFlightList() {
        flightListModel.removeAllElements();
        for (Flight flight : flights) {
            flightListModel.addElement(flight.flightID());
        }
    }

    public void addAirlinesList() {
        for (Airline airline: airlines) {
            airlineBox.addItem(airline.name());
        }
    }

    public void addAeroplanesList() {
        for (Aeroplane aeroplane: aeroplanes) {
            aeroplaneBox.addItem(aeroplane.model());
        }
    }

    public void addAirportsList() {
        for (Airport airport: airports) {
            depatureBox.addItem(airport.name);
            destinationBox.addItem(airport.name);
        }
    }

}

