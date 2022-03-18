package com.github.adamtmalek.flightsimulator.GUI.renderers;

import com.github.adamtmalek.flightsimulator.models.Flight;
import org.jetbrains.annotations.NotNull;

public class FlightListCellRenderer extends CustomListCellRenderer<Flight> {
	@Override
	protected @NotNull String getText(@NotNull Flight value) {
		return value.flightID();
	}
}
