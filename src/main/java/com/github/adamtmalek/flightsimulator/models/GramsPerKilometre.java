package com.github.adamtmalek.flightsimulator.models;


public record GramsPerKilometre(double gramsPerKilometre) {
	

	public GramsPerKilometre(double gramsPerKilometre) {
		if (gramsPerKilometre <= 0) {
			throw new java.lang.IllegalArgumentException(
				String.format("Invalid distance must be greater than 0: %f", gramsPerKilometre()));
		}
		this.gramsPerKilometre = gramsPerKilometre;
	}
	public double casting2(){
		double gramsPerKilometre = Double.parseDouble("");
	}
}
