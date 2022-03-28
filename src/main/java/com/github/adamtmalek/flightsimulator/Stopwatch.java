package com.github.adamtmalek.flightsimulator;

import java.time.ZonedDateTime;

public class Stopwatch implements Runnable {

	private ZonedDateTime currentTime;
	private volatile boolean isRunning = true;

	public Stopwatch(ZonedDateTime stopwatchRelativeStartTime) {
		currentTime = stopwatchRelativeStartTime;
	}

	public void run() {
		while (isRunning) {
			System.out.println("Stopwatch running: " + currentTime);
			currentTime = currentTime.plusSeconds(FlightSimulationThreadManagement.getApproxFlightSimulationPeriodMs() / 1000);

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
		return currentTime;
	}


}
