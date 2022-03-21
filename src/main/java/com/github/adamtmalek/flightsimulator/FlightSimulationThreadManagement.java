package com.github.adamtmalek.flightsimulator;

/**
 * Class containing configurable values thread frequency and period.
 * <p>
 * NB: Period is approximate. It will be used to determine how long a thread should sleep for each run.
 * As such, it does not consider how long each run shall take to run. The actual period of each thread
 * may vary between runs (data-dependent).
 */
public class FlightSimulationThreadManagement {
	private static long THREAD_FREQUENCY = 2; //Hz
	private static long FLIGHT_SIMULATION_FREQUENCY = 5; //Hz

	public static long getApproxThreadPeriodMs() {
		double periodS = (1.0 / THREAD_FREQUENCY);
		return (long) (periodS * 1000.0);
	}

	public static void setThreadFrequency(long frequency) {
		THREAD_FREQUENCY = frequency;
	}

	public static long getApproxFlightSimulationPeriodMs() {
		double periodS = (1.0 / FLIGHT_SIMULATION_FREQUENCY);
		return (long) (periodS * 1000.0);
	}

	public static void setFlightSimulationFrequency(long frequency) {
		FLIGHT_SIMULATION_FREQUENCY = frequency;
	}


}
