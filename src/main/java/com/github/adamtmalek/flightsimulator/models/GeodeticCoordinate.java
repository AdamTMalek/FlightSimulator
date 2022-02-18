package com.github.adamtmalek.flightsimulator.models;

/**
 * @param latitude  Units: Degrees
 * @param longitude Units: Degrees
 */
public record GeodeticCoordinate(double latitude, double longitude) {


    public double calculateDistance(GeodeticCoordinate coord) {

        final double EARTH_RADIUS = 6371.0;

        var radianLatitudeA = Math.toRadians(coord.latitude());
        var radianLongitudeA = Math.toRadians(coord.longitude());
        var radianLatitudeB = Math.toRadians(this.latitude());
        var radianLongitudeB = Math.toRadians(this.longitude());

        var deltaLatitude = radianLatitudeB - radianLatitudeA;
        var deltaLongitude = radianLongitudeB - radianLongitudeA;

        return (2.0 * EARTH_RADIUS
                * Math.asin(Math.sqrt(Math.sin(deltaLatitude / 2)
                + Math.cos(coord.latitude())
                * Math.cos(this.latitude())
                * Math.sin(deltaLongitude / 2))));
    }


}
