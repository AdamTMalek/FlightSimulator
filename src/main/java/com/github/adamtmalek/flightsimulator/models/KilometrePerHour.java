package com.github.adamtmalek.flightsimulator.models;


public record KilometrePerHour(double kilometrePerHour) {
    public KilometrePerHour(double kilometrePerHour) {
        if (kilometrePerHour <= 0) {
            throw new java.lang.IllegalArgumentException(
                    String.format("Invalid distance must be greater than 0: %f", kilometrePerHour));
        }
        this.kilometrePerHour = kilometrePerHour;
    }

    public KilometrePerHour(String kilometrePerHour) {
        //double kilometrePerHour = Double.parseDouble("perHour");
        //return kilometrePerHour; combination of syntax/logical errors here. Commented out so code can be ran.
        this(0.0);
    }
}