package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.models.GeodeticCoordinate;
import com.github.adamtmalek.flightsimulator.models.Kilometre;
import com.github.adamtmalek.flightsimulator.models.Radians;

public class Utils {

    public static Kilometre calculateDistanceBetweenCoordinates(GeodeticCoordinate coordA, GeodeticCoordinate coordB) {
        var radianLatitudeA = new Radians(Math.toRadians(coordA.latitude().degrees()));
        var radianLongitudeA = new Radians(Math.toRadians(coordA.longitude().degrees()));
        var radianLatitudeB = new Radians(Math.toRadians(coordA.latitude().degrees()));
        var radianLongitudeB = new Radians(Math.toRadians(coordB.longitude().degrees()));

        var deltaLatitude = new Radians(radianLatitudeB.radians() - radianLatitudeA.radians());
        var deltaLongitude = new Radians(radianLongitudeB.radians() - radianLongitudeA.radians());

        return new Kilometre(2.0 * EARTH_RADIUS.kilometre()
                * Math.asin(Math.sqrt(Math.sin(deltaLatitude.radians() / 2)
                + Math.cos(coordA.latitude().degrees())
                * Math.cos(coordB.latitude().degrees())
                * Math.sin(deltaLongitude.radians() / 2))));
    }

    private static final Kilometre EARTH_RADIUS = new Kilometre(6371);

}
