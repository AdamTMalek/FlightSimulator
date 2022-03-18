package com.github.adamtmalek.flightsimulator.gui;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class OpenFileViewControllerImpl implements OpenFileViewController {
	@Override
	public @NotNull Optional<FlightFilesPaths> openDialog() {
		return Optional.empty();
	}
}
