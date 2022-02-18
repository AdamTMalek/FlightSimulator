package com.github.adamtmalek.flightsimulator.models;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Airport {
    public final @NotNull String code;
    public final @NotNull String name;
    public final @NotNull GeodeticCoordinate position;
    public final ControlTower controlTower;

    public Airport(@NotNull String code, @NotNull String name, @NotNull GeodeticCoordinate position) {
        this.code = code;
        this.name = name;
        this.position = position;
        this.controlTower = new ControlTower(code, name, position);
    }


    public class ControlTower {
        public final @NotNull String code;
        public final @NotNull String name;
        public final @NotNull GeodeticCoordinate position;

        public ControlTower(@NotNull String codeIn, @NotNull String nameIn, @NotNull GeodeticCoordinate positionIn) {
            code = codeIn;
            name = nameIn;
            position = positionIn;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Airport airport = (Airport) o;
        return code.equals(airport.code)
                && name.equals(airport.name)
                && Double.compare(position.latitude(), airport.position.latitude()) == 0
                && Double.compare(position.longitude(), airport.position.longitude()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, name, position.latitude(), position.longitude());
    }
}
