package com.github.adamtmalek.flightsimulator.validators;

import com.github.adamtmalek.flightsimulator.GUI.Screen;
import com.github.adamtmalek.flightsimulator.GUI.models.*;
import com.github.adamtmalek.flightsimulator.interfaces.Controller;
import com.github.adamtmalek.flightsimulator.models.*;
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
import java.awt.event.*;
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

public class GUIcontroller  {

	private static Screen view;
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");


	public GUIcontroller(Screen view){
		this.view = view;
	}


	public static void FlightSelectionListener() {
		view.getFlightList().addListSelectionListener(e -> {
			final var flight = (Flight) view.getFlightList().getSelectedValue();
			if (flight == null) return;

			view.getTextDistance().setText(Double.toString(flight.distanceTravelled()));
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
