package com.github.adamtmalek.flightsimulator.models;


public record GramsPerKilometre(double gramsPerKilometre) {


    public GramsPerKilometre(double gramsPerKilometre) {
        if (gramsPerKilometre <= 0) {
            throw new java.lang.IllegalArgumentException(
                    String.format("Invalid distance must be greater than 0: %f", gramsPerKilometre()));
        }
        this.gramsPerKilometre = gramsPerKilometre;
    }

    public GramsPerKilometre(String gramsPerKilometre) {
        //double gramsPerKilometre = Double.parseDouble("gramsPerkilometre");
        //return gramsPerKilometre; combination of syntax/logical errors here. Commented out so code can be ran.
        this(0.0);
    }
}
