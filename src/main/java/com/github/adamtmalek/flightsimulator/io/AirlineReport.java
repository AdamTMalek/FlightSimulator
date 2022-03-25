package com.github.adamtmalek.flightsimulator.io;

import com.github.adamtmalek.flightsimulator.models.Flight;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

/**
 * @param totalDistanceTravelled   Units: Kilometre
 * @param estimatedFuelConsumption Units: Litres per kilometre
 * @param estimatedCO2Emissions    Units: Grams per kilometre
 */
public record AirlineReport(
		@SerializableField
		int totalFlights,
		@SerializableField
		double totalDistanceTravelled,
		@SerializableField
		double estimatedFuelConsumption,
		@SerializableField
		double estimatedCO2Emissions) {

	public AirlineReport(@NotNull List<Flight> flights) {
		this(flights.size(),
				calculateTotalDistanceTravelled(flights),
				calculateEstimatedFuelConsumption(flights),
				calculateEstimatedCO2Emissions(flights)
		);
	}

	private static double calculateTotalDistanceTravelled(@NotNull Collection<Flight> flights) {
		return flights.stream().mapToDouble(Flight::estimatedTotalDistanceToTravel).sum();
	}

	private static double calculateEstimatedFuelConsumption(@NotNull Collection<Flight> flights) {
		return flights.stream().mapToDouble(Flight::estimatedFuelConsumption).sum();
	}

	private static double calculateEstimatedCO2Emissions(@NotNull Collection<Flight> flights) {
		return flights.stream().mapToDouble(Flight::estimatedCO2Produced).sum();
	}
}
