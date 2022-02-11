package com.github.adamtmalek.flightsimulator.models.io;


import com.github.adamtmalek.flightsimulator.models.Aeroplane;
import com.github.adamtmalek.flightsimulator.models.Airline;
import com.github.adamtmalek.flightsimulator.models.Airport;
import com.github.adamtmalek.flightsimulator.models.Flight;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public record FlightData(@NotNull Collection<Airport> airports,
                         @NotNull Collection<Airline> airlines,
                         @NotNull Collection<Aeroplane> aeroplanes,
                         @NotNull Collection<Flight> flights) {

}
