package com.github.adamtmalek.flightsimulator.gui.dialog;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Optional;

public interface DialogController<T> {
	@NotNull Optional<T> openDialog(@Nullable Component dialogParent);
}
