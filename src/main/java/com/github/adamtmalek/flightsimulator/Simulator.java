//package com.github.adamtmalek.flightsimulator;
//
//import com.github.adamtmalek.flightsimulator.interfaces.Subscriber;
//import com.github.adamtmalek.flightsimulator.models.*;
//import javafx.collections.*;
//import javafx.collections.ObservableSet;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
//public class Simulator {
//	private final @NotNull ObservableSet<Aeroplane> aeroplanes = FXCollections.observableSet();
//	private final @NotNull ObservableSet<Airline> airlines = FXCollections.observableSet();
//	private final @NotNull ObservableSet<Airport> airports = FXCollections.observableSet();
//	private final @NotNull ObservableSet<Flight> flights = FXCollections.observableSet();
//
//	private final @NotNull FlightJoiner flightJoiner = new FlightJoiner();
//	private final @NotNull FlightSimulationThreadManagement threadManager;
//
//	public Simulator() {
//		final var flightJoiner = new FlightJoiner();
//		final var controlTowers = airports.stream().map(o -> o.controlTower).toList();
//		threadManager = new FlightSimulationThreadManagement(flights, controlTowers, flightJoiner);
//	}
//
//	private void initFlightJoiner(@NotNull FlightJoiner joiner) {
//		joiner.registerSubscriber(data -> data.stream().map(DirectedFlight::flight)
//				.forEach(flight -> {
//					flights.removeIf(f -> f.flightID().equals(flight.flightID()));
//					flights.add(flight);
//				})
//		);
//	}
//
//	private void init() {
//		airports.stream()
//				.map(o -> o.controlTower)
//				.forEach(o -> o.registerSubscriber(flightJoiner));
//	}
//
//	public void addAeroplanes(@NotNull Collection<Aeroplane> aeroplanes) {
//		this.
//	}
//
//	public void addFlights(@NotNull Collection<Flight> flights) {
//		this.flights.addAll(flights);
//	}
//
//	public void addControlTowers(@NotNull Collection<Airport.ControlTower> controlTowers) {
//
//	}
//
//	public void addFlightsListListener(ListChangeListener<? super Flight> listener) {
//		this.flights.addListener(listener);
//	}
//}
