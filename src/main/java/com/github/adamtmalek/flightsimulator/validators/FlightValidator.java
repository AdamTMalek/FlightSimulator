package com.github.adamtmalek.flightsimulator.validators;

import com.github.adamtmalek.flightsimulator.models.Airport;
import org.jetbrains.annotations.NotNull;

public class FlightValidator implements Validator<Airport> {
	private final Airport airportA;

	public FlightValidator(@NotNull Airport airportA) {
		this.airportA = airportA;
	}

	@Override
	public @NotNull ValidationResult validate(Airport value) {
		if (airportA == value)
			return new ValidationResult(false, "Departure and destination airports cannot be the same");
		else
			return ValidationResult.VALID;
	}
}
