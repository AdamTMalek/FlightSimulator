package com.github.adamtmalek.flightsimulator.models;


public record LitresPerKilometre(double litresPerKilometre) {
	public LitresPerKilometre(double litresPerKilometre) {
		if (litresPerKilometre <= 0) {
			throw new java.lang.IllegalArgumentException(
					String.format("Invalid amount must be greater than 0: %f", litresPerKilometre));
		}
		this.litresPerKilometre = litresPerKilometre;
	}

	@java.lang.Override
	public double LitresPerKilometre(String litresPerKilometre) {
		double litresPerKilometre = Double.parseDouble("litresPerKilometre");
		return litresPerKilometre;
	}
}
