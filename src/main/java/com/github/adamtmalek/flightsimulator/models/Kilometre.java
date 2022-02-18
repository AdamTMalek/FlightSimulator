package com.github.adamtmalek.flightsimulator.models;

public record Kilometre(double kilometre) {
    public Kilometre(double kilometre) {
        if (kilometre <= 0) {
            throw new java.lang.IllegalArgumentException(
                    String.format("Invalid distance must be greater than 0: %f", kilometre));
        }
        this.kilometre = kilometre;
    }


    public Kilometre(String kilometre) {
        //double kilometre = Double.parseDouble("kilometre");
        //return kilometre; combination of syntax/logical errors here. Commented out so code can be ran.
        this(0.0);
    }
}


