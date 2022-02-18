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
//        final double EARTH_RADIUS = 6371.0;
//
//        var radianLatitudeA = Math.toRadians(coord.latitude());
//        var radianLongitudeA = Math.toRadians(coord.longitude());
//        var radianLatitudeB = Math.toRadians(this.latitude());
//        var radianLongitudeB = Math.toRadians(this.longitude());
//
//        var deltaLatitude = Math.abs(radianLatitudeB - radianLatitudeA);
//        var deltaLongitude = Math.abs(radianLongitudeB - radianLongitudeA);
//
//        var distance = (2.0 * EARTH_RADIUS
//                * Math.asin(Math.sqrt(Math.pow(Math.sin(deltaLatitude / 2), 2)
//                + Math.cos(coord.latitude())
//                + Math.cos(this.latitude())
//                * Math.pow(Math.sin(deltaLongitude / 2), 2))));
//        return distance;
    }


}
