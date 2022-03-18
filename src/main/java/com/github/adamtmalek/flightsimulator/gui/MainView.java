package com.github.adamtmalek.flightsimulator.gui;

import com.github.adamtmalek.flightsimulator.io.FlightData;
import com.github.adamtmalek.flightsimulator.models.Aeroplane;
import com.github.adamtmalek.flightsimulator.models.Airline;
import com.github.adamtmalek.flightsimulator.models.Airport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.time.ZonedDateTime;
import java.util.List;

public interface MainView {
	@NotNull Component getComponent();

	@Nullable String getFlightNumber();

	@Nullable Airline getSelectedAirline();

	@Nullable Aeroplane getSelectedAeroplane();

	@Nullable Airport getSelectedDepartureAirport();

	@Nullable Airport getSelectedArrivalAirport();

	@Nullable ZonedDateTime getDateTimeOfDeparture();

	@NotNull List<Airport.ControlTower> getFlightPlan();

	void setAddButtonEnabled(boolean enabled);

	void displayData(@NotNull FlightData flightData);

	void updateFlightList();
}
