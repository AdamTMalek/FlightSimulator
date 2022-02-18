package com.github.adamtmalek.flightsimulator.models;


public record GramsPerKilometre(double gramsPerKilometre) {
    
    public GramsPerKilometre(String gramsPerKilometre) {
        //double gramsPerKilometre = Double.parseDouble("gramsPerkilometre");
        //return gramsPerKilometre; combination of syntax/logical errors here. Commented out so code can be ran.
        this(0.0);
    }
}
