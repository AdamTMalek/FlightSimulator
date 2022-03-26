package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.logger.Logger;
import com.github.adamtmalek.flightsimulator.logger.LoggerFront;
import com.github.adamtmalek.flightsimulator.models.Airport;
import com.github.adamtmalek.flightsimulator.models.Flight;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

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

	private final @NotNull Logger logger = LoggerFront.getInstance();
	private final Collection<Thread> threads;

	public FlightSimulationThreadManagement(@NotNull Collection<Flight> flights,
																					@NotNull Collection<Airport.ControlTower> controlTowers,
																					@NotNull FlightJoiner flightJoiner) {

		final var flightTrackerThreads = flights
				.stream()
				.map(FlightTracker::new)
				.map(Thread::new)
				.toList();

		final var controlTowerThreads = controlTowers
				.stream()
				.map(Thread::new)
				.toList();
		final var flightJoinerThread = new Thread(flightJoiner);

		this.threads = Stream.of(flightTrackerThreads, controlTowerThreads, List.of(flightJoinerThread))
				.flatMap(Collection::stream)
				.toList();
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
		logger.info("Starting threads. ================== ");
		this.threads.forEach(Thread::start);
	}

	public void stopThreads() {
		logger.info("Stopping threads. ================== ");
		this.threads.forEach(Thread::stop);
	}

	public void pauseThreads() {
		logger.info("Pausing threads. ================== ");
		this.threads.forEach(Thread::suspend);
	}

	public void resumeThreads() {
		logger.info("Resuming threads. ================== ");
		this.threads.forEach(Thread::resume);
	}


}
