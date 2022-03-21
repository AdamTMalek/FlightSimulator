package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.interfaces.Publisher;
import com.github.adamtmalek.flightsimulator.models.Airport;
import com.github.adamtmalek.flightsimulator.models.Flight;
import com.github.adamtmalek.flightsimulator.models.GeodeticCoordinate;

public class FlightTracker extends Publisher<Flight> implements Runnable {


	private final Flight flight;
	private double duration;
	private volatile boolean isRunning;

	FlightTracker(Flight flight) {
		this.flight = flight;
		this.duration = 0;
		this.isRunning = true;
	}

	public void run() {

		while (isRunning) {
			OrientatedGeodeticCoordinate intermittentCoordinate = calculateCurrentPosition(calculateCurrentDistanceTravelled());
			final var currentPosition = intermittentCoordinate.position;
			final var nextControlTower = intermittentCoordinate.nextControlTower;

			final var updatedFlight = flight.buildWithNewPosition(currentPosition);

			publishTo(updatedFlight, nextControlTower);
			duration += FlightSimulationThreadManagement.getApproxFlightSimulationPeriodMs();
			try {
				final var sleepFor = FlightSimulationThreadManagement.getApproxThreadPeriodMs();
				Thread.sleep(FlightSimulationThreadManagement.getApproxThreadPeriodMs());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void stop() {
		isRunning = false;
	}

	private OrientatedGeodeticCoordinate calculateCurrentPosition(double distanceTravelled) {
		var iterator = flight.controlTowersToCross().listIterator();
		Airport.ControlTower firstCoord = flight.controlTowersToCross().get(0);
		Airport.ControlTower secondCoord = flight.controlTowersToCross().get(1);
		double distanceBetweenPreviousControlTowers = 0.0;
		while (iterator.hasNext() && distanceBetweenPreviousControlTowers < distanceTravelled) { //iterator.nextIndex() + 1 <= flight.controlTowersToCross().size() - 1
			firstCoord = iterator.next();
			secondCoord = flight.controlTowersToCross().get(iterator.nextIndex());
			distanceBetweenPreviousControlTowers += firstCoord.position.calculateDistance(secondCoord.position);

		}
		distanceBetweenPreviousControlTowers -= firstCoord.position.calculateDistance(secondCoord.position);

		final var azimuthBetweenControlTowers = firstCoord.position.calculateAzimuth(secondCoord.position);
		final var intermittentCoordinate = firstCoord.position.extendCoordinate(azimuthBetweenControlTowers, distanceTravelled - distanceBetweenPreviousControlTowers);
		return new OrientatedGeodeticCoordinate(secondCoord, intermittentCoordinate);
	}

	private double calculateCurrentDistanceTravelled() {
		return flight.aeroplane().speed() * duration;
	}


	private record OrientatedGeodeticCoordinate(Airport.ControlTower nextControlTower, GeodeticCoordinate position) {
	}

}
