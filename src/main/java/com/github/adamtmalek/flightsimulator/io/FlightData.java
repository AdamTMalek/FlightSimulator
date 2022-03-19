package com.github.adamtmalek.flightsimulator.io;


import com.github.adamtmalek.flightsimulator.models.Aeroplane;
import com.github.adamtmalek.flightsimulator.models.Airline;
import com.github.adamtmalek.flightsimulator.models.Airport;
import com.github.adamtmalek.flightsimulator.models.Flight;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public record FlightData(
		@NotNull Set<Airport> airports,
		@NotNull Set<Airline> airlines,
		@NotNull Set<Aeroplane> aeroplanes,
		@NotNull Set<Flight> flights) {

	public FlightData() {
		this(new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
	}
}