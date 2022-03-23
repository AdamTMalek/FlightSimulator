package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.interfaces.Publisher;
import com.github.adamtmalek.flightsimulator.interfaces.Subscriber;
import com.github.adamtmalek.flightsimulator.models.Airport;
import com.github.adamtmalek.flightsimulator.models.DirectedFlight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlightJoiner extends Publisher<ArrayList<DirectedFlight>> implements Subscriber<ArrayList<DirectedFlight>>, Runnable {

	private final Map<String, DirectedFlight> directedFlightMap;
	private boolean isProcessing;
	private volatile boolean isRunning;

	FlightJoiner() {
		isRunning = true;
		directedFlightMap = new HashMap<>();
		isProcessing = false;
	}

	public void run() {
		while (isRunning) {
			System.out.println("Entering!");

			while (isProcessing) {
				try {
					System.out.println("Flight joiner sleeping");
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
			var uniqueFlights = new ArrayList<DirectedFlight>(directedFlightMap.values());

			//TO-DO, register GUI component subscriber
			if (!uniqueFlights.isEmpty()) {
				System.out.println("Publishing!");

				publish(uniqueFlights);
				directedFlightMap.clear();
			}

			try {
				final var sleepFor = FlightSimulationThreadManagement.getApproxThreadPeriodMs() + 1100;
				Thread.sleep(sleepFor);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public void stop() {
		isRunning = false;
	}


	public void callback(ArrayList<DirectedFlight> flights) {

		joinFlights(flights);

	}

	private synchronized void joinFlights(ArrayList<DirectedFlight> flights) {
		isProcessing = true;
		System.out.println("FlightJoiner received the following data, from `" + flights.get(0).controlTowerCode() + "`: " + flights.get(0).flight().flightID() + "` at `" + flights.get(0).flight().estimatedPosition().latitude() + ", " + flights.get(0).flight().estimatedPosition().longitude() + "`");

		for (var newFlight : flights) {
			final var existingFlight = directedFlightMap.get(newFlight.flight().flightID());
			if (existingFlight == null) {
				// Flight doesn't exist in the map yet, so we can simply insert it into the map.
				directedFlightMap.put(newFlight.flight().flightID(), newFlight);
			} else {
				// determine which flight is most up-to-date, and ensure this is the element stored in the map.
				final var flightPlan = existingFlight.flight().controlTowersToCross();
				final var indexOfExistingControlTowerMessage = getIndexOfControlTower(flightPlan, existingFlight.controlTowerCode());
				final var indexOfNewControlTowerMessage = getIndexOfControlTower(flightPlan, newFlight.controlTowerCode());

				if (indexOfNewControlTowerMessage >= indexOfExistingControlTowerMessage) {
					directedFlightMap.put(newFlight.flight().flightID(), newFlight);
				}

			}
		}
		isProcessing = false;

	}

	private int getIndexOfControlTower(List<Airport.ControlTower> flightPlan, String controlTowerId) {

		int indexOfControlTower = -1;
		for (int i = 0; i < flightPlan.size(); i++) {

			if (flightPlan.get(i).code == controlTowerId) {
				indexOfControlTower = i;
			}
		}
		return indexOfControlTower;
	}

}
