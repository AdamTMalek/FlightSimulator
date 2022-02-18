package com.github.adamtmalek.flightsimulator.models;

public record Kilometre(double kilometre) {

    public Kilometre(String kilometre) {
        //double kilometre = Double.parseDouble("kilometre");
        //return kilometre; combination of syntax/logical errors here. Commented out so code can be ran.
        this(0.0);
    }
}


