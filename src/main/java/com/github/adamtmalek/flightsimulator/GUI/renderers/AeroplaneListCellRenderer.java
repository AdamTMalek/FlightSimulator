package com.github.adamtmalek.flightsimulator.GUI.renderers;

import com.github.adamtmalek.flightsimulator.models.Aeroplane;
import org.jetbrains.annotations.NotNull;

public class AeroplaneListCellRenderer extends CustomListCellRenderer<Aeroplane> {
	@Override
	protected @NotNull String getText(@NotNull Aeroplane value) {
		return value.model();
	}
}
