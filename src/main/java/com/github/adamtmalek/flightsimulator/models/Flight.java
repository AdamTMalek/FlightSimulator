package com.github.adamtmalek.flightsimulator.models;

import com.github.adamtmalek.flightsimulator.io.SerializableField;
import org.jetbrains.annotations.NotNull;

import java.security.InvalidParameterException;
import java.time.ZonedDateTime;
import java.util.List;


public record Flight(
		@SerializableField
		@NotNull String flightID,
		@NotNull Airline airline,
		@SerializableField
		@NotNull Aeroplane aeroplane,
		@SerializableField
		@NotNull Airport departureAirport,
		@SerializableField
		@NotNull Airport destinationAirport,
		@SerializableField
		@NotNull ZonedDateTime departureDate,
		@NotNull List<Airport.ControlTower> controlTowersToCross,
		double estimatedTotalDistancetoTravel,
		double estimatedFuelConsumption,
		double estimatedCO2Produced,
		GeodeticCoordinate estimatedPosition) {


	public static Flight buildWithSerialNumber(@NotNull String serialNumber,
																						 @NotNull Airline airline,
																						 @NotNull Aeroplane aeroplane,
																						 @NotNull Airport departureAirport,
																						 @NotNull Airport destinationAirport,
																						 @NotNull ZonedDateTime departureDate,
																						 @NotNull List<Airport.ControlTower> controlTowersToCross) {
		final var distanceTravelled = calculateEstimatedTotalDistanceToTravel(controlTowersToCross);
		final var estimatedFuelConsumption = calculateEstimatedFuelConsumption(aeroplane.fuelConsumptionRate(), distanceTravelled);
		final var estimatedCO2Produced = calculateEstimatedCO2Produced(estimatedFuelConsumption);
		final var flightID = constructFlightId(serialNumber, airline);

		return new Flight(flightID,
				airline,
				aeroplane,
				departureAirport,
				destinationAirport,
				departureDate,
				controlTowersToCross,
				distanceTravelled,
				estimatedFuelConsumption,
				estimatedCO2Produced,
				new GeodeticCoordinate(0, 0)
		);
	}

	public static Flight buildWithFlightId(@NotNull String fullFlightId,
																				 @NotNull Airline airline,
																				 @NotNull Aeroplane aeroplane,
																				 @NotNull Airport departureAirport,
																				 @NotNull Airport destinationAirport,
																				 @NotNull ZonedDateTime departureDate,
																				 @NotNull List<Airport.ControlTower> controlTowersToCross) {
		final var estimatedTotalDistance = calculateEstimatedTotalDistanceToTravel(controlTowersToCross);
		final var estimatedFuelConsumption = calculateEstimatedFuelConsumption(aeroplane.fuelConsumptionRate(), estimatedTotalDistance);
		final var estimatedCO2Produced = calculateEstimatedCO2Produced(estimatedFuelConsumption);

		return new Flight(fullFlightId,
				airline,
				aeroplane,
				departureAirport,
				destinationAirport,
				departureDate,
				controlTowersToCross,
				estimatedTotalDistance,
				estimatedFuelConsumption,
				estimatedCO2Produced,
				new GeodeticCoordinate(0, 0)
		);
	}

	private static String constructFlightId(String serialNumber, Airline airline) throws InvalidParameterException {
		try {
			Integer.parseInt(serialNumber);
		} catch (NumberFormatException e) {
			throw new InvalidParameterException("Flight serial number must be a series of integer values.");
		}
		return airline.code() + serialNumber;
	}

	private static double calculateEstimatedTotalDistanceToTravel(List<Airport.ControlTower> controlTowersToCross) {
		var distanceTravelled = 0.0;

		if (controlTowersToCross.size() > 1) {

			// Calculate the distance between pairs of control tower pairs, and sum into distance travelled.
			for (int i = 1; i < controlTowersToCross.size(); i++) {
				final var firstControlTowerPosition = controlTowersToCross.get(i - 1).position;
				final var secondControlTowerPosition = controlTowersToCross.get(i).position;
				distanceTravelled += Math.abs(firstControlTowerPosition.calculateDistance(secondControlTowerPosition));
			}
		}
		return distanceTravelled;

	}

	private static double calculateEstimatedFuelConsumption(double fuelConsumption, double distance) {
		return fuelConsumption * (distance / 100);
	}

	private static double calculateEstimatedCO2Produced(double estimatedFuelConsumption) {
		return Aeroplane.AVG_RATE_OF_CO2_EMISSION * estimatedFuelConsumption;
	}

	public Flight buildWithNewPosition(GeodeticCoordinate position) {
		return new Flight(this.flightID,
				this.airline,
				this.aeroplane,
				this.departureAirport,
				this.destinationAirport,
				this.departureDate,
				this.controlTowersToCross,
				this.estimatedTotalDistancetoTravel,
				this.estimatedFuelConsumption,
				this.estimatedCO2Produced,
				position);
	}


}