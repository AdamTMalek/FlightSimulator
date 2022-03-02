package com.github.adamtmalek.flightsimulator.models.io;


import com.github.adamtmalek.flightsimulator.models.Aeroplane;
import com.github.adamtmalek.flightsimulator.models.Airline;
import com.github.adamtmalek.flightsimulator.models.Airport;
import com.github.adamtmalek.flightsimulator.models.Flight;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record FlightData(

		@NotNull List<Airport> airports,
		@NotNull List<Airline> airlines,
		@NotNull List<Aeroplane> aeroplanes,
		@NotNull List<Flight> flights) {

	public FlightData() {
		this(new ArrayList<Airport>(), new ArrayList<Airline>(), new ArrayList<Aeroplane>(), new ArrayList<Flight>());
	}

}