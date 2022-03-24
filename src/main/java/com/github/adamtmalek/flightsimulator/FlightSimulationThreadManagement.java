package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.models.Airport;
import com.github.adamtmalek.flightsimulator.models.Flight;

import java.util.List;

/**
 * Class containing configurable values thread frequency and period.
 * <p>
 * NB: Period is approximate. It will be used to determine how long a thread should sleep for each run.
 * As such, it does not consider how long each run shall take to run. The actual period of each thread
 * may vary between runs (data-dependent).
 */
public class FlightSimulationThreadManagement {
	private static double THREAD_FREQUENCY = 2; //Hz
	private static double FLIGHT_SIMULATION_FREQUENCY = 5; //Hz
	private static double GUI_UPDATE_FREQUENCY = 2; //Hz

	private final List<Thread> flightTrackerThreads;
	private final List<Thread> controlTowerThreads;
	private final Thread flightJoinerThread;

	public FlightSimulationThreadManagement(List<Flight> flights, List<Airport.ControlTower> controlTowers, FlightJoiner flightJoiner) {

		this.flightTrackerThreads = flights
				.stream()
				.map(flight -> new Thread(new FlightTracker(flight)))
				.toList();

		this.controlTowerThreads = controlTowers
				.stream()
				.map(controlTower -> {
					controlTower.registerSubscriber(flightJoiner);
					return new Thread(controlTower);
				})
				.toList();
		this.flightJoinerThread = new Thread(flightJoiner);
	}

	public static long getApproxThreadPeriodMs() {
		return getPeriodMs(THREAD_FREQUENCY);
	}

	public static long getApproxFlightSimulationPeriodMs() {
		return getPeriodMs(FLIGHT_SIMULATION_FREQUENCY);

	}

	public static long getApproxGuiUpdateFrequency() {
		return getPeriodMs(GUI_UPDATE_FREQUENCY);

	}

	private static long getPeriodMs(double frequency) {
		double periodS = (1.0 / frequency);
		return (long) (periodS * 1000.0);
	}

	public static void setThreadFrequency(double frequency) {
		THREAD_FREQUENCY = frequency;
	}

	public static void setFlightSimulationFrequency(double frequency) {
		FLIGHT_SIMULATION_FREQUENCY = frequency;
	}

	public static void setGuiUpdateFrequency(double frequency) {
		GUI_UPDATE_FREQUENCY = frequency;
	}

	public void startThreads() {
		System.out.println("Starting threads. ================== ");

		this.flightTrackerThreads.forEach(Thread::start);
		this.controlTowerThreads.forEach(Thread::start);
		this.flightJoinerThread.start();
	}

	public void stopThreads() {
		System.out.println("Stopping threads. ================== ");

		this.flightTrackerThreads.forEach(Thread::stop);
		this.controlTowerThreads.forEach(Thread::stop);
		this.flightJoinerThread.stop();
	}

	public void pauseThreads() {
		System.out.println("Pausing threads. ================== ");

		this.flightTrackerThreads.forEach(Thread::suspend);
		this.controlTowerThreads.forEach(Thread::suspend);
		this.flightJoinerThread.suspend();
	}

	public void resumeThreads() {
		System.out.println("Resuming threads. ================== ");

		this.flightTrackerThreads.forEach(Thread::resume);
		this.controlTowerThreads.forEach(Thread::resume);
		this.flightJoinerThread.resume();
	}


}
