package com.github.adamtmalek.flightsimulator.validators;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ValidationResult(boolean isValid,
															 @Nullable String reason) {
	public static @NotNull ValidationResult VALID = new ValidationResult(true, null);
}
