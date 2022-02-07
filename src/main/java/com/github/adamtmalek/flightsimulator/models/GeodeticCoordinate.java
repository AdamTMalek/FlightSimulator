package com.github.adamtmalek.flightsimulator.models;

public record GeodeticCoordinate(double latitude, double longitude) {
	private boolean isDegree(double deg) {
		// TODO
		return true;
	}
	
	public GeodeticCoordinate(double latitude, double longitude) {
		if (!isDegree(latitude) || !isDegree(longitude)) {
			throw new java.lang.IllegalArgumentException(
					String.format("Invalid coordinates must be degrees: %f, %f", latitude, longitude));
		}
		this.latitude = latitude;
		this.longitude = longitude;
	}
}
