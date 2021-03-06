package com.github.adamtmalek.flightsimulator.gui.renderers;

import com.github.adamtmalek.flightsimulator.models.Airline;
import org.jetbrains.annotations.NotNull;

public class AirlineListCellRenderer extends CustomListCellRenderer<Airline> {
	@Override
	protected @NotNull String getText(@NotNull Airline value) {
		return value.name();
	}
}
