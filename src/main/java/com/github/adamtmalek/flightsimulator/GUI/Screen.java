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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.time.ZonedDateTime;
import java.time.ZoneId;


public class Screen extends JFrame implements ActionListener {
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
    private JComboBox flightPlanBox1;
    private JComboBox flightPlanBox2;
    private JComboBox flightPlanBox3;
    private JComboBox comboBox8;
    private JComboBox comboBox9;
    private JComboBox comboBox10;
    private JComboBox comboBox11;
    private JComboBox comboBox12;
    private JComboBox comboBox13;
    private JButton addButton;
    private JButton exitButton;
    private JTextField ddMmYyyyTextField;
    private DefaultListModel flightListModel;
    private DefaultComboBoxModel airlineListModel;
    private DefaultComboBoxModel aeroplaneListModel;
    private DefaultComboBoxModel depatureListModel;
    private DefaultComboBoxModel destinationListModel;
    private DefaultComboBoxModel flightPlan1ListModel;
    private DefaultComboBoxModel flightPlan2ListModel;
    private DefaultComboBoxModel flightPlan3ListModel;
    private List<Flight> flights;
    private List<Airline> airlines;
    private List<Aeroplane> aeroplanes;
    private List<Airport> airports;
    private Aeroplane selectedAeroplane;
    private Airport selectedDepature;
    private Airport selectedDestination;
    public FlightTrackerController flightTrackerController;
    public List<Airport.ControlTower> selectedFlightPlan;

    public Screen() throws FileHandlerException {
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

    public void actionPerformed(ActionEvent e)
    {
        this.selectedFlightPlan = new ArrayList<>();
        double distanceTravelled;
        double estimatedFuelConsumption;
        double estimatedCO2Produced;
        int newIndex;

        if(e.getSource() == airlineBox) {
            int airlineIndex = airlineBox.getSelectedIndex();
            Airline airline = airlines.get(airlineIndex);
        }
        if(e.getSource() == aeroplaneBox) {
            int aeroplaneIndex = aeroplaneBox.getSelectedIndex();
            this.selectedAeroplane = aeroplanes.get(aeroplaneIndex);
        }
        if(e.getSource() == depatureBox) {
            int depatureIndex = depatureBox.getSelectedIndex();
            this.selectedDepature = airports.get(depatureIndex);
        }
        if(e.getSource() == destinationBox) {
            int destinationIndex = destinationBox.getSelectedIndex();
            this.selectedDestination = airports.get(destinationIndex);
        }
        if(e.getSource() == flightPlanBox1) {
            int flightPlanIndex = flightPlanBox1.getSelectedIndex();
            this.selectedFlightPlan.add(airports.get(flightPlanIndex).controlTower);
            System.out.println(this.selectedFlightPlan);
        }
        if(e.getSource() == flightPlanBox2) {
            int flightPlanIndex = flightPlanBox2.getSelectedIndex();
            this.selectedFlightPlan.add(airports.get(flightPlanIndex).controlTower);
            System.out.println(this.selectedFlightPlan);
        }
        if(e.getSource() == flightPlanBox3) {
            int flightPlanIndex = flightPlanBox3.getSelectedIndex();
            this.selectedFlightPlan.add(airports.get(flightPlanIndex).controlTower);
            System.out.println(this.selectedFlightPlan);
        }

        ZonedDateTime zonedDateTimeNow = ZonedDateTime.now(ZoneId.of("UTC"));

        if(e.getSource() == addButton) {
            Flight newFlight = new Flight("501", this.selectedAeroplane, this.selectedDepature,
                    this.selectedDestination, zonedDateTimeNow, this.selectedFlightPlan, distanceTravelled = 0.0,
                    estimatedFuelConsumption = 0.0, estimatedCO2Produced = 0.0
            );

            this.addNewFlight(newFlight);
        }
    }

    public void refreshFlightList() {
        flightListModel.removeAllElements();
        for (Flight flight : flights) {
            flightListModel.addElement(flight.flightID());
        }
    }

    public void addAirlinesList() {
        for (Airline airline: airlines) {
            airlineListModel.addElement(airline.name());
        }
    }

    public void addAeroplanesList() {
        for (Aeroplane aeroplane: aeroplanes) {
            aeroplaneListModel.addElement(aeroplane.model());
        }
    }

    public void addAirportsList() {
        for (Airport airport: airports) {
            depatureListModel.addElement(airport.name);
            destinationListModel.addElement(airport.name);
            flightPlan1ListModel.addElement(airport.controlTower.name);
            flightPlan2ListModel.addElement(airport.controlTower.name);
            flightPlan3ListModel.addElement(airport.controlTower.name);
        }
    }

    public void addNewFlight( Flight flight) {
        this.flightTrackerController.addFlight(flight);
        this.refreshFlightList();
    }
}

