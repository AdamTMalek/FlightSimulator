package com.github.adamtmalek.flightsimulator;

import javafx.beans.value.ChangeListener;

import java.time.ZonedDateTime;

public class Stopwatch implements Runnable {
	private final OurObservableValue<ZonedDateTime> currentTime;

	private volatile boolean isRunning = true;

	public Stopwatch(ZonedDateTime stopwatchRelativeStartTime) {
		currentTime = new OurObservableValue<>(stopwatchRelativeStartTime);
	}

	public void run() {
		while (isRunning) {
			System.out.println("Stopwatch running: " + currentTime.getValue());
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
		System.out.println("Stopwatch current rel time: " + currentTime);
		return currentTime.getValue();
	}

	public void registerTimeObserver(ChangeListener<? super ZonedDateTime> listener) {
		currentTime.addListener(listener);
	}
}
