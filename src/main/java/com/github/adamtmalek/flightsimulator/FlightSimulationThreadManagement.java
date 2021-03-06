package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.logger.Logger;
import com.github.adamtmalek.flightsimulator.models.Airport;
import com.github.adamtmalek.flightsimulator.models.Flight;
import javafx.beans.value.ChangeListener;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class containing configurable values thread frequency and period.
 * <p>
 * NB: Period is approximate. It will be used to determine how long a thread should sleep for each run.
 * As such, it does not consider how long each run shall take to run. The actual period of each thread
 * may vary between runs (data-dependent).
 */
public class FlightSimulationThreadManagement {
	private static final double THREAD_FREQUENCY_SPEED_DIFFERENCE = 1;
	public static final double DEFAULT_THREAD_FREQUENCY = 2;
	public static final double MINIMUM_THREAD_FREQUENCY = DEFAULT_THREAD_FREQUENCY - THREAD_FREQUENCY_SPEED_DIFFERENCE;
	public static final double MAXIMUM_THREAD_FREQUENCY = DEFAULT_THREAD_FREQUENCY + THREAD_FREQUENCY_SPEED_DIFFERENCE;

	private static final double GUI_THREAD_FREQUENCY_SPEED_DIFFERENCE = 1;
	public static final double DEFAULT_GUI_UPDATE_FREQUENCY = DEFAULT_THREAD_FREQUENCY;
	public static final double MINIMUM_GUI_UPDATE_FREQUENCY = DEFAULT_GUI_UPDATE_FREQUENCY - GUI_THREAD_FREQUENCY_SPEED_DIFFERENCE;
	public static final double MAXIMUM_GUI_UPDATE_FREQUENCY = DEFAULT_GUI_UPDATE_FREQUENCY + GUI_THREAD_FREQUENCY_SPEED_DIFFERENCE;

	public static final double FLIGHT_SIM_FREQ_5_MINUTES = 0.003333333;
	public static final double FLIGHT_SIM_FREQ_15_MINUTES = FLIGHT_SIM_FREQ_5_MINUTES / 3;
	public static final double FLIGHT_SIM_FREQ_30_MINUTES = FLIGHT_SIM_FREQ_15_MINUTES / 2;
	public static final double FLIGHT_SIM_FREQ_45_MINUTES = FLIGHT_SIM_FREQ_15_MINUTES / 3;
	public static final double FLIGHT_SIM_FREQ_60_MINUTES = FLIGHT_SIM_FREQ_30_MINUTES / 2;

	private static double THREAD_FREQUENCY = 2; //Hz
	private static double FLIGHT_SIMULATION_FREQUENCY = 5; //Hz
	private static double GUI_UPDATE_FREQUENCY = 2; //Hz

	private final @NotNull Logger logger = Logger.getInstance();
	private final Collection<Thread> threads;
	private ZonedDateTime simulationStartTime;
	private Stopwatch stopwatch;

	public FlightSimulationThreadManagement(@NotNull Collection<Flight> flights,
																					@NotNull Collection<Airport.ControlTower> controlTowers,
																					@NotNull FlightJoiner flightJoiner,
																					@NotNull ZonedDateTime simulationStartTime) {

		this.simulationStartTime = simulationStartTime;
		stopwatch = new Stopwatch(simulationStartTime);

		final var flightTrackerThreads = flights
				.stream()
				.map(f -> new FlightTracker(f, simulationStartTime))
				.map(Thread::new)
				.toList();

		final var controlTowerThreads = controlTowers
				.stream()
				.map(Thread::new)
				.toList();
		final var flightJoinerThread = new Thread(flightJoiner);

		this.threads = Stream.of(flightTrackerThreads, controlTowerThreads, List.of(flightJoinerThread), List.of(new Thread(stopwatch)))
				.flatMap(Collection::stream)
				.collect(Collectors.toSet());
	}

	public static long getApproxThreadPeriodMs() {
		return getPeriodMs(THREAD_FREQUENCY);
	}

	public static long getApproxFlightSimulationPeriodMs() {
		return getPeriodMs(FLIGHT_SIMULATION_FREQUENCY);
	}

	public static long getApproxGuiUpdateThreadPeriodMs() {
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

	public void startTrackingNewFlight(@NotNull Flight flight) {
		var newFlightThread = new Thread(new FlightTracker(flight, stopwatch.getCurrentRelativeTime()));
		newFlightThread.start();
		threads.add(newFlightThread);
	}

	public void registerTimeObserver(ChangeListener<? super ZonedDateTime> listener) {
		stopwatch.registerTimeObserver(listener);
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
