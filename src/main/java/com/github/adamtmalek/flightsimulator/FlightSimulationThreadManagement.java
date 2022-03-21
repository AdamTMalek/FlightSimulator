package com.github.adamtmalek.flightsimulator;

/**
 * Class containing configurable values thread frequency and period.
 * <p>
 * NB: Period is approximate. It will be used to determine how long a thread should sleep for each run.
 * As such, it does not consider how long each run shall take to run. The actual period of each thread
 * may vary between runs (data-dependent).
 */
public class FlightSimulationThreadManagement {
	private static double THREAD_FREQUENCY = 5; //Hz
	private static double FLIGHT_SIMULATION_FREQUENCY = 5; //Hz

	public static double getApproxThreadPeriod() {
		return (1.0 / THREAD_FREQUENCY);
	}

	public static void setThreadFrequency(double frequency) {
		THREAD_FREQUENCY = frequency;
	}

	public static double getApproxFlightSimulationPeriod() {
		return (1.0 / FLIGHT_SIMULATION_FREQUENCY);
	}

	public static void setFlightSimulationFrequency(double frequency) {
		FLIGHT_SIMULATION_FREQUENCY = frequency;
	}


}
