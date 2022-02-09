package com.github.adamtmalek.flightsimulator.models;

import org.jetbrains.annotations.NotNull;

public record Airline(@NotNull String code,
                      @NotNull String name) {

}
