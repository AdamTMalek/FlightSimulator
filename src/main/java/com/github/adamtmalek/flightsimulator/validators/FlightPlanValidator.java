package com.github.adamtmalek.flightsimulator.validators;

import com.github.adamtmalek.flightsimulator.models.Airport;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FlightPlanValidator implements Validator<List<Airport.ControlTower>> {
	private final Airport departureAirport;
	private final Airport destinationAirport;

	public FlightPlanValidator(@NotNull Airport departureAirport,
														 @NotNull Airport destinationAirport) {
		this.departureAirport = departureAirport;
		this.destinationAirport = destinationAirport;
	}

	@Override
	public @NotNull ValidationResult validate(List<Airport.ControlTower> value) {
		if (value.isEmpty())
			return new ValidationResult(false, "Flight plan is empty");
		if (value.size() < 2)
			return new ValidationResult(false, "Flight plan contains only one point");

		if (value.get(0) != departureAirport.controlTower)
			return new ValidationResult(false, "The first point of the flight plan is not the departure airport");
		if (value.get(value.size() - 1) != destinationAirport.controlTower)
			return new ValidationResult(false, "The last point of the flight plan is not the destination airport");

		return ValidationResult.VALID;
	}
}
