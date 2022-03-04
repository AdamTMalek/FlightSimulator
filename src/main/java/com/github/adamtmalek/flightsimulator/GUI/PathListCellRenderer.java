package com.github.adamtmalek.flightsimulator.GUI;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class PathListCellRenderer extends CustomListCellRenderer<Path> {
	@Override
	protected @NotNull String getText(@NotNull Path value) {
		return value.getFileName().toString();
	}
}
