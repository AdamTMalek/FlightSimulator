package com.github.adamtmalek.flightsimulator.models;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Airport {
  public final @NotNull String code;
  public final @NotNull String name;
  public final @NotNull String latitude;
  public final @NotNull String longitude;

  public Airport(@NotNull String code, @NotNull String name, @NotNull String latitude, @NotNull String longitude) {
    this.code = code;
    this.name = name;
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public final ControlTower controlTower = new ControlTower();

  public class ControlTower {
    public final @NotNull String code = Airport.this.code;
    public final @NotNull String name = Airport.this.name;
    public final @NotNull String latitude = Airport.this.latitude;
    public final @NotNull String longitude = Airport.this.longitude;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Airport airport = (Airport) o;
    return code.equals(airport.code)
        && name.equals(airport.name)
        && latitude.equals(airport.latitude)
        && longitude.equals(airport.longitude);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, name, latitude, longitude);
  }
}
