package com.github.adamtmalek.flightsimulator.models;


public record LitresPerKilometre(double litresPerKilometre) {

    public LitresPerKilometre(String litresPerKilometre) {
        //double litresPerKilometre = Double.parseDouble("litresPerKilometre");
        //return litresPerKilometre; combination of syntax/logical errors here. Commented out so code can be ran.
        this(0.0);
    }
}
