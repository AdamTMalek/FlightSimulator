package com.github.adamtmalek.flightsimulator.GUI.renderers;

import com.github.adamtmalek.flightsimulator.models.Airport;
import org.jetbrains.annotations.NotNull;

public class AirportListCellRenderer extends CustomListCellRenderer<Airport> {
	@Override
	protected @NotNull String getText(@NotNull Airport value) {
		return value.name;
	}
}
