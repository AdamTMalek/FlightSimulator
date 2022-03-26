package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.interfaces.Publisher;
import com.github.adamtmalek.flightsimulator.logger.Logger;
import com.github.adamtmalek.flightsimulator.logger.LoggerFront;
import com.github.adamtmalek.flightsimulator.models.Airport;
import com.github.adamtmalek.flightsimulator.models.Flight;
import com.github.adamtmalek.flightsimulator.models.GeodeticCoordinate;
import org.jetbrains.annotations.NotNull;

public class FlightTracker extends Publisher<Flight> implements Runnable {
	private final Flight flight;
	private double duration;
	private volatile boolean isRunning;
	private final @NotNull Logger logger = LoggerFront.getInstance();

	FlightTracker(Flight flight) {
		this.flight = flight;
		this.duration = 0;
		this.isRunning = true;
	}

	public void run() {

		while (isRunning) {
			logger.debug(this.flight.flightID() + " is running!");
			OrientatedGeodeticCoordinate intermittentCoordinate = calculateCurrentPosition(calculateCurrentDistanceTravelled());
			final var currentPosition = intermittentCoordinate.position;
			final var nextControlTower = intermittentCoordinate.nextControlTower;

			final var updatedFlight = flight.withNewFlightStatus(nextControlTower, currentPosition,
					Flight.FlightStatus.Status.IN_PROGRESS);

			publishTo(updatedFlight, nextControlTower);
			duration += (FlightSimulationThreadManagement.getApproxFlightSimulationPeriodMs() / 1000.0);

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
		if (distanceTravelled > 0.0) {
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
		} else {
			// Distance is 0.0, therefore the flight shall be travelling to the 2nd control tower (1 index)
			// , and is currently at the position of the departure airport control tower (0 index).
			return new OrientatedGeodeticCoordinate(flight.controlTowersToCross().get(1), flight.controlTowersToCross().get(0).position);
		}
	}

	private double calculateCurrentDistanceTravelled() {
		return flight.aeroplane().speed() * duration;
	}


	private record OrientatedGeodeticCoordinate(Airport.ControlTower nextControlTower, GeodeticCoordinate position) {
	}

}
