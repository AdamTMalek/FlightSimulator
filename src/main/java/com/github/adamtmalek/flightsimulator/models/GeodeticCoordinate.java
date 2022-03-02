package com.github.adamtmalek.flightsimulator.models;

/**
 * @param latitude  Units: Degrees
 * @param longitude Units: Degrees
 */
public record GeodeticCoordinate(double latitude, double longitude) {
	/**
	 * @return Units: Kilometres
	 */
	public double calculateDistance(GeodeticCoordinate coord) {

		double theta = coord.longitude - this.longitude;

		return Math.toDegrees(Math.acos(Math.sin(Math.toRadians(coord.latitude))
				* Math.sin(Math.toRadians(this.latitude))
				+ Math.cos(Math.toRadians(coord.latitude))
				* Math.cos(Math.toRadians(this.latitude))
				* Math.cos(Math.toRadians(theta)))) * 60 * 1.1515 * 1.609344;
	}
}
