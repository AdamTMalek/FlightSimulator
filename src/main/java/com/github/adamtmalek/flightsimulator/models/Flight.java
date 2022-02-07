package com.github.adamtmalek.flightsimulator.models;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

public record Flight(
		String flightID,
		Aeroplane aeroplane,
		Airport departureAirport,
		Airport destinationAirport,
		ZonedDateTime departureDateTime,
		List<ControlTower> controlTowersToCross,
		Kilometre totalDistance,
		Duration flightDuration,
		GramsPerKilometre estimatedCO2Produced) {
	public Flight(
			String flightID,
			Aeroplane aeroplane,
			Airport departureAirport,
			Airport destinationAirport,
			ZonedDateTime departureDateTime,
			List<ControlTower> controlTowersToCross,
			Kilometre totalDistance,
			Duration flightDuration,
			GramsPerKilometre estimatedCO2Produced) {
		this.flightID = flightID;
		this.aeroplane = aeroplane;
		this.departureAirport = departureAirport;
		this.destinationAirport = destinationAirport;
		this.departureDateTime = departureDateTime;
		this.controlTowersToCross = controlTowersToCross;
		this.totalDistance = totalDistance;
		this.flightDuration = flightDuration;
		this.estimatedCO2Produced = estimatedCO2Produced;
	}
}
