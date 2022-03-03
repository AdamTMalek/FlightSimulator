package com.github.adamtmalek.flightsimulator.validators;

import org.jetbrains.annotations.NotNull;

public interface Validator<T> {
	@NotNull ValidationResult validate(T value);
}
