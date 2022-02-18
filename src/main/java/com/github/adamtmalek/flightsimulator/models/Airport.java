package com.github.adamtmalek.flightsimulator.models;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Airport {
    public final @NotNull String code;
    public final @NotNull String name;
    public final @NotNull GeodeticCoordinate position;

    public Airport(@NotNull String code, @NotNull String name, @NotNull String latitude, @NotNull String longitude) {
        this.code = code;
        this.name = name;
        this.position = new GeodeticCoordinate(new Degrees(Double.parseDouble(latitude)), new Degrees(Double.parseDouble(longitude)));
    }

    public final ControlTower controlTower = new ControlTower();

    public class ControlTower {
        public final @NotNull String code = Airport.this.code;
        public final @NotNull String name = Airport.this.name;
        public final @NotNull GeodeticCoordinate position = Airport.this.position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Airport airport = (Airport) o;
        return code.equals(airport.code)
                && name.equals(airport.name)
                && Double.compare(position.latitude().degrees(), airport.position.latitude().degrees()) == 0
                && Double.compare(position.longitude().degrees(), airport.position.longitude().degrees()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, name, position.latitude().degrees(), position.longitude().degrees());
    }
}
