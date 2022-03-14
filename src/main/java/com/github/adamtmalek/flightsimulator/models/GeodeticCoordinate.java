package com.github.adamtmalek.flightsimulator.models;

/**
 * @param latitude  Units: Degrees
 * @param longitude Units: Degrees
 */
public record GeodeticCoordinate(double latitude, double longitude) {
	private static final double EARTH_RADIUS = 6378.1;

	public GeodeticCoordinate extendCoordinate(double azimuth,
																						 double distance) {

		var latitudeRadians = Math.toRadians(this.latitude);
		var longitudeRadians = Math.toRadians(this.longitude);


		double extendedLatitude = Math.asin(Math.sin(latitudeRadians) * Math.cos(distance / EARTH_RADIUS)
				+ Math.cos(latitudeRadians) * Math.sin(distance / EARTH_RADIUS) * Math.cos(azimuth));

		double extendedLongitude = longitudeRadians
				+ Math.atan2(Math.sin(azimuth) * Math.sin(distance / EARTH_RADIUS) * Math.cos(latitudeRadians),
				Math.cos(distance / EARTH_RADIUS) - Math.sin(latitudeRadians) * Math.sin(extendedLatitude));
		return new GeodeticCoordinate(Math.toDegrees(extendedLatitude), Math.toDegrees(extendedLongitude));
	}

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

	/**
	 * @return Units: Radians
	 */
	public double calculateAzimuth(GeodeticCoordinate coord) {
		var theta1 = Math.toRadians(this.latitude);
		var theta2 = Math.toRadians(coord.latitude);
		var delta = Math.toRadians(coord.longitude - this.longitude);
		var y = Math.sin(delta) * Math.cos(theta2);
		var x = Math.cos(theta1) * Math.sin(theta2) -
				Math.sin(theta1) * Math.cos(theta2) * Math.cos(delta);
		var theta = Math.atan2(y, x);
		var ret = (Math.toDegrees(theta) + 360) % 360;
		return Math.toRadians(ret);
	}

}
