package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.interfaces.Publisher;
import com.github.adamtmalek.flightsimulator.interfaces.Subscriber;
import com.github.adamtmalek.flightsimulator.models.Airport;
import com.github.adamtmalek.flightsimulator.models.DirectedFlight;
import org.jetbrains.annotations.NotNull;

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
	}

	public void run() {
		while (isRunning) {

			var uniqueFlights = new ArrayList<DirectedFlight>(directedFlightMap.values());

			//TO-DO, register GUI component subscriber
			if (!uniqueFlights.isEmpty()) {
				System.out.println("FlightJoiner is publishing joined flights.");
				publish(uniqueFlights);
				directedFlightMap.clear();
			}

			try {
				final var sleepFor = FlightSimulationThreadManagement.getApproxGuiUpdateFrequency();
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

		for (var newFlight : flights) {
			System.out.println("FlightJoiner received data from `" + newFlight.controlTowerCode() + "`: " + newFlight.flight().flightID() + "` at `" + newFlight.flight().estimatedPosition().latitude() + ", " + newFlight.flight().estimatedPosition().longitude() + "`");

			final var existingFlight = directedFlightMap.get(newFlight.flight().flightID());
			if (existingFlight == null) {
				// Flight doesn't exist in the map yet, so we can simply insert it into the map.
				directedFlightMap.put(newFlight.flight().flightID(), newFlight);
			} else {

				final var flightPlan = existingFlight.flight().controlTowersToCross();
				final var indexOfExistingControlTowerMessage = getIndexOfControlTower(flightPlan, existingFlight.controlTowerCode());
				final var indexOfNewControlTowerMessage = getIndexOfControlTower(flightPlan, newFlight.controlTowerCode());

				// Determine which flight is travelling to the furthest control tower. This shall be the most recent one,
				// so ensure this is the element stored in the map.
				if (indexOfNewControlTowerMessage >= indexOfExistingControlTowerMessage) {
					directedFlightMap.put(newFlight.flight().flightID(), newFlight);
				}

			}
		}
	}

	private int getIndexOfControlTower(@NotNull List<Airport.ControlTower> flightPlan, @NotNull String controlTowerId) {
		for (int i = 0; i < flightPlan.size(); i++) {
			if (flightPlan.get(i).code.equals(controlTowerId)) {
				return i;
			}
		}
		return -1;
	}
}
