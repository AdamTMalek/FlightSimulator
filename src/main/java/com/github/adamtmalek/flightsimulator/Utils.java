package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.models.GeodeticCoordinate;

public class Utils {

    public static double calculateDistanceBetweenCoordinates(GeodeticCoordinate coordA, GeodeticCoordinate coordB) {
        var radianLatitudeA = Math.toRadians(coordA.latitude());
        var radianLongitudeA = Math.toRadians(coordA.longitude());
        var radianLatitudeB = Math.toRadians(coordA.latitude());
        var radianLongitudeB = Math.toRadians(coordB.longitude());

        var deltaLatitude = radianLatitudeB - radianLatitudeA;
        var deltaLongitude = radianLongitudeB - radianLongitudeA;

        return (2.0 * EARTH_RADIUS
                * Math.asin(Math.sqrt(Math.sin(deltaLatitude / 2)
                + Math.cos(coordA.latitude())
                * Math.cos(coordB.latitude())
                * Math.sin(deltaLongitude / 2))));
    }

    private static final double EARTH_RADIUS = 6371.0;

}
