package com.github.adamtmalek.flightsimulator.gui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public interface OpenFileView {
	@Nullable Path getSelectedAeroplanesFile();

	@Nullable Path getSelectedAirlinesFile();

	@Nullable Path getSelectedAirportsFile();

	@Nullable Path getSelectedFlightsFile();

	void setAeroplanesFileSelectionTo(@NotNull Path value);

	void setAirlinesFileSelectionTo(@NotNull Path value);

	void setAirportsFileSelectionTo(@NotNull Path value);

	void setFlightsFileSelectionTo(@NotNull Path value);
}
