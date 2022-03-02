package com.github.adamtmalek.flightsimulator.models.io.converters;

import com.github.adamtmalek.flightsimulator.models.GeodeticCoordinate;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class GeodeticCoordinateConverter implements Converter<GeodeticCoordinate> {
	private static final int numberOfStringValuesRequired = 2;

	@Override
	public GeodeticCoordinate convertFromString(String... values) {
		if (values.length != numberOfStringValuesRequired) {
			throw new ConversionException("Required 2 string parameters to be passed for conversion");
		}

		final var latitude = DMSRepresentation.fromString(values[0]);
		final var longitude = DMSRepresentation.fromString(values[1]);

		return new GeodeticCoordinate(convertDmsToDouble(latitude), convertDmsToDouble(longitude));
	}

	@Override
	public String convertToString(GeodeticCoordinate object) {
		final var latitude = convertDoubleToDms(object.latitude());
		final var longitude = convertDoubleToDms(object.longitude());

		return String.format("%.0f°%.0f'%.4f\"%s; %.0f°%.0f'%.4f\"%s",
				latitude.degrees, latitude.minutes, latitude.seconds, latitude.isPositive ? "N" : "S",
				longitude.degrees, longitude.minutes, longitude.seconds, longitude.isPositive ? "E" : "W");
	}

	private double convertDmsToDouble(@NotNull DMSRepresentation dms) {
		final var doubleRepresentation = dms.degrees + (dms.minutes / 60.0) + (dms.seconds / 3600.0);
		return dms.isPositive ? doubleRepresentation : -doubleRepresentation;
	}

	private @NotNull DMSRepresentation convertDoubleToDms(double decDegrees) {
		final double absDegrees = Math.abs(decDegrees);
		final double floorAbsDegrees = Math.floor(absDegrees);

		final var degrees = Math.abs(Math.signum(decDegrees) * floorAbsDegrees);
		final var minutes = Math.floor(60 * (absDegrees - floorAbsDegrees));
		final var seconds = 3600 * (absDegrees - floorAbsDegrees) - 60 * minutes;
		final var isPositive = decDegrees >= 0.0;

		return new DMSRepresentation(degrees, minutes, seconds, isPositive);
	}

	@Override
	public int getNumberOfStringsToConsume() {
		return numberOfStringValuesRequired;
	}

	private record DMSRepresentation(double degrees,
																	 double minutes,
																	 double seconds,
																	 boolean isPositive) {
		private static final Pattern pattern = Pattern.compile("(\\d+)°(\\d+)'([\\d.]+)\"(\\w)");

		public static @NotNull DMSRepresentation fromString(@NotNull String string) {
			final var matcher = pattern.matcher(string);
			if (matcher.find()) {
				double degrees = Double.parseDouble(matcher.group(1));
				double minutes = Double.parseDouble(matcher.group(2));
				double seconds = Double.parseDouble(matcher.group(3));
				char direction = matcher.group(4).charAt(0);
				boolean isPositive = (degrees == 0.0 && minutes == 0.0 && seconds == 0.0) || isPositiveDirection(direction);

				return new DMSRepresentation(degrees, minutes, seconds, isPositive);
			} else {
				throw new ConversionException(String.format("Cannot convert string %s - no match found", string));
			}
		}

		private static boolean isPositiveDirection(char direction) {
			return switch (direction) {
				case 'N', 'E' -> true;
				case 'S', 'W' -> false;
				default -> throw new ConversionException(String.format("Unrecognised direction character: %s", direction));
			};
		}
	}
}
