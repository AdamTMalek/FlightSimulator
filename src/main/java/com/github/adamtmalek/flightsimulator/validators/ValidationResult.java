package com.github.adamtmalek.flightsimulator.validators;

import org.jetbrains.annotations.Nullable;

public record ValidationResult(boolean isValid,
															 @Nullable String reason) {
}
