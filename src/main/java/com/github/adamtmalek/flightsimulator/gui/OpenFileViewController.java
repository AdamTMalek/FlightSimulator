package com.github.adamtmalek.flightsimulator.gui;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface OpenFileViewController {
	@NotNull Optional<FlightFilesPaths> openDialog();
}
