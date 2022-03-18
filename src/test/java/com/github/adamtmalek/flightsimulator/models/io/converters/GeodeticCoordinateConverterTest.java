package com.github.adamtmalek.flightsimulator.models.io.converters;

import com.github.adamtmalek.flightsimulator.io.converters.GeodeticCoordinateConverter;
import com.github.adamtmalek.flightsimulator.models.GeodeticCoordinate;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GeodeticCoordinateConverterTest {
	private final @NotNull GeodeticCoordinateConverter converter = new GeodeticCoordinateConverter();
	private final double expectedPositiveDegrees = 51 + 28 / 60.0 + 12.0720 / 3600.0;

	@Test
	public void testLatitudeFromStringConversionIsPositiveWhenDirectionIsNorth() {
		final var testParameters = new String[]{"51°28'12.0720\"N", "0°0'0.0000\"E"};
		final var expected = new GeodeticCoordinate(expectedPositiveDegrees, 0);
		final var actual = converter.convertFromString(testParameters);

		Assertions.assertEquals(expected, actual);
	}

	@Test
	public void testLatitudeFromStringConversionIsNegativeWhenDirectionIsSouth() {
		final var testParameters = new String[]{"51°28'12.0720\"S", "0°0'0.0000\"E"};
		final var expected = new GeodeticCoordinate(-expectedPositiveDegrees, 0);
		final var actual = converter.convertFromString(testParameters);

		Assertions.assertEquals(expected, actual);
	}

	@Test
	public void testLatitudeFromStringConversionIsNegativeWhenDirectionIsSouthAndValueNegative() {
		final var testParameters = new String[]{"-51°28'12.0720\"S", "0°0'0.0000\"E"};
		final var expected = new GeodeticCoordinate(-expectedPositiveDegrees, 0);
		final var actual = converter.convertFromString(testParameters);

		Assertions.assertEquals(expected, actual);
	}

	@Test
	public void testLongitudeFromStringConversionIsPositiveWhenDirectionIsEast() {
		final var testParameters = new String[]{"0°0'0.0000\"N", "51°28'12.0720\"E"};
		final var expected = new GeodeticCoordinate(0, expectedPositiveDegrees);
		final var actual = converter.convertFromString(testParameters);

		Assertions.assertEquals(expected, actual);
	}

	@Test
	public void testLongitudeFromStringConversionIsNegativeWhenDirectionIsWest() {
		final var testParameters = new String[]{"0°0'0.0000\"N", "51°28'12.0720\"W"};
		final var expected = new GeodeticCoordinate(0, -expectedPositiveDegrees);
		final var actual = converter.convertFromString(testParameters);

		Assertions.assertEquals(expected, actual);
	}

	@Test
	public void testLongitudeFromStringConversionIsNegativeWhenDirectionIsWestAndValueNegative() {
		final var testParameters = new String[]{"0°0'0.0000\"N", "-51°28'12.0720\"W"};
		final var expected = new GeodeticCoordinate(0, -expectedPositiveDegrees);
		final var actual = converter.convertFromString(testParameters);

		Assertions.assertEquals(expected, actual);
	}

	@Test
	public void testLongLatArePositiveForZeroSouthWest() {
		final var testParameters = new String[]{"0°0'0.0000\"S", "-0°0'0.0000\"W"};
		final var expected = new GeodeticCoordinate(0, 0);
		final var actual = converter.convertFromString(testParameters);

		Assertions.assertEquals(expected, actual);
	}

	@Test
	public void testConvertToStringNorthEast() {
		final var expected = "25°15'9.9972\"N; 55°21'52.0020\"E";
		final var actual = converter.convertToString(converter.convertFromString(expected.split(";")));

		Assertions.assertEquals(expected, actual);
	}

	@Test
	public void testConvertToStringSouthWest() {
		final var expected = "33°56'50.4456\"S; 151°10'45.9408\"W";
		final var actual = converter.convertToString(converter.convertFromString(expected.split(";")));

		Assertions.assertEquals(expected, actual);
	}

	@Test
	public void testConvertZeroes() {
		final var expected = "0°0'0.0000\"N; 0°0'0.0000\"E";
		final var actual = converter.convertToString(converter.convertFromString(expected.split(";")));

		Assertions.assertEquals(expected, actual);
	}
}
