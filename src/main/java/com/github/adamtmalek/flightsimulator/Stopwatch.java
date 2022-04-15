package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.logger.Logger;
import javafx.beans.value.ChangeListener;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;

public class Stopwatch implements Runnable {
	private final OurObservableValue<ZonedDateTime> currentTime;
	private final @NotNull Logger logger = Logger.getInstance();

	private volatile boolean isRunning = true;

	public Stopwatch(ZonedDateTime stopwatchRelativeStartTime) {
		currentTime = new OurObservableValue<>(stopwatchRelativeStartTime);
	}

	public void run() {
		while (isRunning) {
			logger.debug("Stopwatch running: " + currentTime.getValue());
			currentTime.setValue(value -> value.plusSeconds(FlightSimulationThreadManagement.getApproxFlightSimulationPeriodMs() / 1000));

			try {
				Thread.sleep(FlightSimulationThreadManagement.getApproxThreadPeriodMs());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	public void stop() {
		isRunning = false;
	}

	public ZonedDateTime getCurrentRelativeTime() {
		logger.debug("Stopwatch current rel time: " + currentTime);
		return currentTime.getValue();
	}

	public void registerTimeObserver(ChangeListener<? super ZonedDateTime> listener) {
		currentTime.addListener(listener);
	}
}
