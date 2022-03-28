package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.interfaces.Publisher;
import com.github.adamtmalek.flightsimulator.models.Airport;
import com.github.adamtmalek.flightsimulator.models.Flight;
import com.github.adamtmalek.flightsimulator.models.GeodeticCoordinate;

import java.time.ZonedDateTime;

public class FlightTracker extends Publisher<Flight> implements Runnable {


	private final Flight flight;
	private long flightDuration;
	private ZonedDateTime simulationRelativeTime;
	private volatile boolean isRunning;


	FlightTracker(Flight flight, ZonedDateTime simulationStartTime) {
		this.flight = flight;
		this.isRunning = true;
		this.flightDuration = 0;
		this.simulationRelativeTime = simulationStartTime;
	}

	public void run() {

		while (isRunning) {
			System.out.println(this.flight.flightID() + " is running at " + simulationRelativeTime);

			if (hasDepartureDatePassed()) {
				System.out.println("Flight is being tracked.");

				final var updatedFlight = trackFlight();
				publishTo(updatedFlight, updatedFlight.flightStatus().getCurrentControlTower());

				this.flightDuration += (FlightSimulationThreadManagement.getApproxFlightSimulationPeriodMs() / 1000);

				if (updatedFlight.flightStatus().getStatus() == Flight.FlightStatus.Status.TERMINATED) {
					System.out.println("Flight has terminated. Stopping track.");
					stop();
				}


			} else {
				System.out.println("Flight has yet to take off.");
				publishTo(flight, flight.departureAirport().controlTower);
			}
			simulationRelativeTime = simulationRelativeTime.plusSeconds(FlightSimulationThreadManagement.getApproxFlightSimulationPeriodMs() / 1000);
			try {
				Thread.sleep(FlightSimulationThreadManagement.getApproxThreadPeriodMs());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean hasDepartureDatePassed() {
		return simulationRelativeTime.toEpochSecond() >= flight.departureDate().toEpochSecond();
	}

	public void stop() {
		isRunning = false;
	}

	private Flight trackFlight() {
		if (flightDuration == 0.0) {
			// Distance is 0.0, therefore the flight shall be travelling to the 2nd control tower (1 index)
			// , and is currently at the position of the departure airport control tower (0 index).
			return flight.withNewFlightStatus(flight.controlTowersToCross().get(1), flight.controlTowersToCross().get(0).position, Flight.FlightStatus.Status.IN_PROGRESS);
		} else {      // Distance is >0.0, therefore we can track the target.
			final var distanceTravelled = calculateCurrentDistanceTravelled();
			if (distanceTravelled >= flight.estimatedTotalDistanceToTravel()) { // Then we can say the flight has reached its destination.
				System.out.println("Flight has terminated! Pos is " + flight.destinationAirport().position.latitude() + "," + flight.destinationAirport().position.longitude());
				return flight.withNewFlightStatus(flight.destinationAirport().controlTower, flight.destinationAirport().position, Flight.FlightStatus.Status.TERMINATED);
			} else { // Flight hasn't reached its destination yet, therefore we should calculate a tracked position.
				return createFlightWithTrackedPosition();
			}
		}
	}

	private Flight createFlightWithTrackedPosition() {
		OrientatedGeodeticCoordinate intermittentCoordinate = calculateCurrentPosition(calculateCurrentDistanceTravelled());
		final var currentPosition = intermittentCoordinate.position;
		final var nextControlTower = intermittentCoordinate.nextControlTower;

		return flight.withNewFlightStatus(nextControlTower, currentPosition,
				Flight.FlightStatus.Status.IN_PROGRESS);

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
		return flight.aeroplane().speed() * flightDuration;
	}


	private record OrientatedGeodeticCoordinate(Airport.ControlTower nextControlTower, GeodeticCoordinate position) {
	}

}
