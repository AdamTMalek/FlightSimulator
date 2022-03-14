package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.interfaces.Publisher;
import com.github.adamtmalek.flightsimulator.models.Airport;
import com.github.adamtmalek.flightsimulator.models.Flight;
import com.github.adamtmalek.flightsimulator.models.GeodeticCoordinate;

import java.util.ListIterator;

public class FlightManager extends Publisher<Flight> {

	Flight flight;
	ListIterator<Airport.ControlTower> controlTowerIterator;

	FlightManager(Flight flight) {
		this.flight = flight;
		this.controlTowerIterator = flight.controlTowersToCross()
				.subList(1, flight.controlTowersToCross().size()).listIterator();

	}

	public void tick() {

		final var currentCoordinate = calculateCurrentPosition();

		final var nextControlTower = controlTowerIterator.hasNext()
				? controlTowerIterator.next() : flight.controlTowersToCross().get(flight.controlTowersToCross().size() - 1);

		publishTo(flight, nextControlTower);
	}

	private double calculateCurrentDistanceTravelled() {
		return flight.aeroplane().speed() * getCurrentElapsedDuration();
	}

	private GeodeticCoordinate calculateCurrentPosition() {
		return new GeodeticCoordinate(0.0, 0.0);
	}

	private double getCurrentElapsedDuration() {
		return 0.0;
	}
}
