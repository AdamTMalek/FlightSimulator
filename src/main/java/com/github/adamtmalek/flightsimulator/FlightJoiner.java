package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.interfaces.Publisher;
import com.github.adamtmalek.flightsimulator.interfaces.Subscriber;
import com.github.adamtmalek.flightsimulator.logger.Logger;
import com.github.adamtmalek.flightsimulator.models.Flight;
import javafx.collections.ObservableSet;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FlightJoiner extends Publisher<Collection<Flight>> implements Subscriber<Collection<Flight>>, Runnable {
	private final Map<String, Flight> flightMap;
	private final @NotNull Logger logger = Logger.getInstance();
	private boolean isProcessing;
	private volatile boolean isRunning;
	private ObservableSet<Flight> observableFlights;

	FlightJoiner(ObservableSet<Flight> observableFlights) {
		isRunning = true;
		flightMap = new HashMap<>();
		this.observableFlights = observableFlights;
	}

	public void run() {
		while (isRunning) {
			var uniqueFlights = new ArrayList<>(flightMap.values());
			logger.info("FlightJoiner is running!");

			//TO-DO, register GUI component subscriber
			if (!uniqueFlights.isEmpty()) {
				logger.debug("FlightJoiner is publishing joined flights.");
				publish(uniqueFlights);
				observableFlights.addAll(flightMap.values());
				flightMap.clear();
			}

			try {
				final var sleepFor = FlightSimulationThreadManagement.getApproxGuiUpdateThreadPeriodMs();
				Thread.sleep(sleepFor);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void stop() {
		isRunning = false;
	}

	public void callback(Collection<Flight> flights) {
		joinFlights(flights);
	}

	private synchronized void joinFlights(Collection<Flight> flights) {
		for (var newFlight : flights) {
			final var message = "FlightJoiner received data from `%s`: %s at `%s, %s`"
					.formatted(newFlight.flightStatus().getCurrentControlTower(),
							newFlight.flightID(),
							newFlight.flightStatus().getCurrentPosition().latitude(),
							newFlight.flightStatus().getCurrentPosition().longitude());
			logger.debug(message);

			final var existingFlight = flightMap.get(newFlight.flightID());
			if (existingFlight == null) {
				// Flight doesn't exist in the map yet, so we can simply insert it into the map.
				flightMap.put(newFlight.flightID(), newFlight);
			} else {
				final var flightPlan = existingFlight.controlTowersToCross();
				final var indexOfExistingControlTowerMessage = flightPlan.indexOf(existingFlight.flightStatus().getCurrentControlTower());
				final var indexOfNewControlTowerMessage = flightPlan.indexOf(newFlight.flightStatus().getCurrentControlTower());

				// Determine which flight is travelling to the furthest control tower. This shall be the most recent one,
				// so ensure this is the element stored in the map.
				if (indexOfNewControlTowerMessage >= indexOfExistingControlTowerMessage) {
					flightMap.put(newFlight.flightID(), newFlight);
				}
			}
		}
	}
}
