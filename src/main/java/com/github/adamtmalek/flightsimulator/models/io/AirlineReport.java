package com.github.adamtmalek.flightsimulator.models.io;

import com.github.adamtmalek.flightsimulator.models.Flight;

import java.util.ArrayList;

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

	public AirlineReport(ArrayList<Flight> flights) {
		this(flights.size(), calculateTotalDistanceTravelled(flights), calculateEstimatedFuelConsumption(flights), calculateEstimatedCO2Emissions(flights));
	}

	private static double calculateTotalDistanceTravelled(ArrayList<Flight> flights) {
		return flights.stream().mapToDouble(o -> o.distanceTravelled()).sum();
	}

	private static double calculateEstimatedFuelConsumption(ArrayList<Flight> flights) {
		return flights.stream().mapToDouble(o -> o.estimatedFuelConsumption()).sum();
	}

	private static double calculateEstimatedCO2Emissions(ArrayList<Flight> flights) {
		return flights.stream().mapToDouble(o -> o.estimatedCO2Produced()).sum();

	}
}
