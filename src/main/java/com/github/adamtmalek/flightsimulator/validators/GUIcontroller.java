package com.github.adamtmalek.flightsimulator.validators;

import com.github.adamtmalek.flightsimulator.gui.models.BoundComboBoxModel;
import com.github.adamtmalek.flightsimulator.gui.models.BoundListModel;
import com.github.adamtmalek.flightsimulator.interfaces.Controller;
import com.github.adamtmalek.flightsimulator.models.*;
import com.github.adamtmalek.flightsimulator.gui.FilePicker;
import com.github.adamtmalek.flightsimulator.io.*;
import com.github.adamtmalek.flightsimulator.gui.*;
import com.github.adamtmalek.flightsimulator.GUI.Screen;

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
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class GUIcontroller  {

	private static Screen view;
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	private static final @NotNull Controller flightTrackerController;
	private final @NotNull Airport.ControlTower emptyControlTower = new Airport.ControlTower("(not selected)",
			"(not selected)", new GeodeticCoordinate(0, 0));

	private static final int MAX_CONTROL_TOWERS = 10;

	public GUIcontroller(Screen view,Controller controller){
		this.view = view;
		this.flightTrackerController = controller;
	}
	private List<Airport.ControlTower> getFlightPlanList() {
		return IntStream.range(0, MAX_CONTROL_TOWERS)
				.mapToObj(i -> view.getFlightPlanTable().getModel().getValueAt(0, i))
				.filter(e -> !e.equals(emptyControlTower))
				.map(e -> (Airport.ControlTower) e)
				.toList();
	}
	private @Nullable ZonedDateTime getDateTime() {
		try {
			return LocalDateTime.parse((String) view.getDateTimeField().getValue(), dateTimeFormatter).atZone(ZoneId.systemDefault());
		} catch (DateTimeParseException ex) {
			return null;
		}
	}
	public static void addNewFlight() {
		final var airline = (Airline) view.getAirlineBox().getSelectedItem();
		final var aeroplane = (Aeroplane) view.getAeroplaneBox().getSelectedItem();
		final var departureAirport = (Airport) view.getDepartureBox().getSelectedItem();
		final var destinationAirport = (Airport) view.getDestinationBox().getSelectedItem();
		final var flightPlan = getFlightPlanList();
		final var departureDateTime = getDateTime();
		final var flightNumber = view.getFlightNumberTextField().getText();

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
		view.getFlightList().updateUI();
		resetComponents();
	}
	@SuppressWarnings("unchecked")
	private void resetComponents() {
		IntStream.range(0, MAX_CONTROL_TOWERS)
				.forEach(i -> {
					view.getFlightPlanTable().setValueAt(emptyControlTower, 0, i);
				});
		view.getFlightPlanTable().updateUI();

		view.getFlightNumberTextField().setText("");
		if (view.getAirlineBox().getModel().getSize() > 0) view.getAirlineBox().setSelectedIndex(0);
		if (view.getAeroplaneBox().getModel().getSize() > 0) view.getAeroplaneBox().setSelectedIndex(0);
		if (view.getDepartureBox().getModel().getSize() > 0) {  // If departure box is not empty, then destination box is not either
			view.getDepartureBox().setSelectedIndex(0);
			view.getDestinationBox().setSelectedIndex(0);
		}
	}
	public static void FlightSelectionListener() {
		view.getFlightList().addListSelectionListener(e -> {
			final var flight = (Flight) view.getFlightList().getSelectedValue();
			if (flight == null) return;

			view.getTextDistance().setText(Double.toString(flight.estimatedTotalDistancetoTravel()));
			view.getTextFuelConsumption().setText(Double.toString(flight.estimatedFuelConsumption()));
			view.getTextCo2Emission().setText(Double.toString(flight.estimatedCO2Produced()));
			view.getTextTime().setText(dateTimeFormatter.format(flight.departureDate()));

			final var flightPlanText = flight.controlTowersToCross()
					.stream()
					.map(t -> t.code)
					.collect(Collectors.joining("\n"));
			view.getFlightPlan().setText(flightPlanText);
		});
	}


}
