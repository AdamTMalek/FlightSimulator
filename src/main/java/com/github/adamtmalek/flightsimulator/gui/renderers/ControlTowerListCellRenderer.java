package com.github.adamtmalek.flightsimulator.gui.renderers;

import com.github.adamtmalek.flightsimulator.models.Airport;
import org.jetbrains.annotations.NotNull;

public class ControlTowerListCellRenderer extends CustomListCellRenderer<Airport.ControlTower> {
	@Override
	protected @NotNull String getText(Airport.@NotNull ControlTower value) {
		return value.name;
	}
}
