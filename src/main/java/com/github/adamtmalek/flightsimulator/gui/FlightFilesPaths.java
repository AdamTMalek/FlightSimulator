package com.github.adamtmalek.flightsimulator.gui;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public record FlightFilesPaths(@NotNull Path aeroplanes,
															 @NotNull Path airlines,
															 @NotNull Path airports,
															 @NotNull Path flights) {

}
