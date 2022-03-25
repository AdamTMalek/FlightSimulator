package com.github.adamtmalek.flightsimulator.models;

import com.github.adamtmalek.flightsimulator.io.SerializableField;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.security.InvalidParameterException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;


public final class Flight {
	@SerializableField
	private final @NotNull String flightID;
	private final @NotNull Airline airline;
	@SerializableField
	private final @NotNull Aeroplane aeroplane;
	@SerializableField
	private final @NotNull Airport departureAirport;
	@SerializableField
	private final @NotNull Airport destinationAirport;
	@SerializableField
	private final @NotNull ZonedDateTime departureDate;
	private final @NotNull List<Airport.ControlTower> controlTowersToCross;
	private final double estimatedTotalDistanceToTravel;
	private final double estimatedFuelConsumption;
	private final double estimatedCO2Produced;
	private final GeodeticCoordinate estimatedPosition;

	@Contract(pure = true)
	public @NotNull String flightID() {
		return flightID;
	}

	@Contract(pure = true)
	public @NotNull Airline airline() {
		return airline;
	}

	@Contract(pure = true)
	public @NotNull Aeroplane aeroplane() {
		return aeroplane;
	}

	@Contract(pure = true)
	public @NotNull Airport departureAirport() {
		return departureAirport;
	}

	@Contract(pure = true)
	public @NotNull Airport destinationAirport() {
		return destinationAirport;
	}

	@Contract(pure = true)
	public @NotNull ZonedDateTime departureDate() {
		return departureDate;
	}

	@Contract(pure = true)
	public @NotNull List<Airport.ControlTower> controlTowersToCross() {
		return controlTowersToCross;
	}

	@Contract(pure = true)
	public double estimatedTotalDistanceToTravel() {
		return estimatedTotalDistanceToTravel;
	}

	@Contract(pure = true)
	public double estimatedFuelConsumption() {
		return estimatedFuelConsumption;
	}

	@Contract(pure = true)
	public double estimatedCO2Produced() {
		return estimatedCO2Produced;
	}

	@Contract(pure = true)
	public GeodeticCoordinate estimatedPosition() {
		return estimatedPosition;
	}

	public Flight(
			@NotNull String flightID,
			@NotNull Airline airline,
			@NotNull Aeroplane aeroplane,
			@NotNull Airport departureAirport,
			@NotNull Airport destinationAirport,
			@NotNull ZonedDateTime departureDate,
			@NotNull List<Airport.ControlTower> controlTowersToCross,
			@NotNull GeodeticCoordinate currentPosition
	) {
		this.flightID = flightID;
		this.airline = airline;
		this.aeroplane = aeroplane;
		this.departureAirport = departureAirport;
		this.destinationAirport = destinationAirport;
		this.departureDate = departureDate;
		this.controlTowersToCross = controlTowersToCross;

		this.estimatedTotalDistanceToTravel = calculateEstimatedTotalDistanceToTravel(controlTowersToCross);
		this.estimatedFuelConsumption = calculateEstimatedFuelConsumption(aeroplane.fuelConsumptionRate(),
				estimatedTotalDistanceToTravel);
		this.estimatedCO2Produced = calculateEstimatedCO2Produced(this.estimatedFuelConsumption);
		this.estimatedPosition = currentPosition;
	}

	public Flight(
			@NotNull String flightID,
			@NotNull Airline airline,
			@NotNull Aeroplane aeroplane,
			@NotNull Airport departureAirport,
			@NotNull Airport destinationAirport,
			@NotNull ZonedDateTime departureDate,
			@NotNull List<Airport.ControlTower> controlTowersToCross) {
		this(flightID, airline, aeroplane,
				departureAirport, destinationAirport, departureDate,
				controlTowersToCross,	departureAirport.position);
	}

	@Contract("_, _, _, _, _, _, _ -> new")
	public static @NotNull Flight buildWithSerialNumber(@NotNull String serialNumber,
																											@NotNull Airline airline,
																											@NotNull Aeroplane aeroplane,
																											@NotNull Airport departureAirport,
																											@NotNull Airport destinationAirport,
																											@NotNull ZonedDateTime departureDate,
																											@NotNull List<Airport.ControlTower> controlTowersToCross) {
		final var flightID = constructFlightId(serialNumber, airline);

		return new Flight(flightID,
				airline,
				aeroplane,
				departureAirport,
				destinationAirport,
				departureDate,
				controlTowersToCross
		);
	}

	private static @NotNull String constructFlightId(String serialNumber, Airline airline) throws InvalidParameterException {
		try {
			Integer.parseInt(serialNumber);
		} catch (NumberFormatException e) {
			throw new InvalidParameterException("Flight serial number must be a series of integer values.");
		}
		return airline.code() + serialNumber;
	}

	private static double calculateEstimatedTotalDistanceToTravel(@NotNull List<Airport.ControlTower> controlTowersToCross) {
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

	@Contract(pure = true)
	private static double calculateEstimatedFuelConsumption(double fuelConsumption, double distance) {
		return fuelConsumption * (distance / 100);
	}

	@Contract(pure = true)
	private static double calculateEstimatedCO2Produced(double estimatedFuelConsumption) {
		return Aeroplane.AVG_RATE_OF_CO2_EMISSION * estimatedFuelConsumption;
	}

	@Contract("_ -> new")
	public @NotNull Flight buildWithNewPosition(GeodeticCoordinate position) {
		return new Flight(
				this.flightID,
				this.airline,
				this.aeroplane,
				this.departureAirport,
				this.destinationAirport,
				this.departureDate,
				this.controlTowersToCross,
				position
		);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (Flight) obj;
		return Objects.equals(this.flightID, that.flightID) &&
				Objects.equals(this.airline, that.airline) &&
				Objects.equals(this.aeroplane, that.aeroplane) &&
				Objects.equals(this.departureAirport, that.departureAirport) &&
				Objects.equals(this.destinationAirport, that.destinationAirport) &&
				Objects.equals(this.departureDate, that.departureDate) &&
				Objects.equals(this.controlTowersToCross, that.controlTowersToCross) &&
				Double.doubleToLongBits(this.estimatedTotalDistanceToTravel) == Double.doubleToLongBits(that.estimatedTotalDistanceToTravel) &&
				Double.doubleToLongBits(this.estimatedFuelConsumption) == Double.doubleToLongBits(that.estimatedFuelConsumption) &&
				Double.doubleToLongBits(this.estimatedCO2Produced) == Double.doubleToLongBits(that.estimatedCO2Produced) &&
				Objects.equals(this.estimatedPosition, that.estimatedPosition);
	}

	@Override
	public int hashCode() {
		return Objects.hash(flightID, airline, aeroplane, departureAirport, destinationAirport, departureDate, controlTowersToCross, estimatedTotalDistanceToTravel, estimatedFuelConsumption, estimatedCO2Produced, estimatedPosition);
	}

	@Override
	public String toString() {
		return "Flight[" +
				"flightID=" + flightID + ", " +
				"airline=" + airline + ", " +
				"aeroplane=" + aeroplane + ", " +
				"departureAirport=" + departureAirport + ", " +
				"destinationAirport=" + destinationAirport + ", " +
				"departureDate=" + departureDate + ", " +
				"controlTowersToCross=" + controlTowersToCross + ", " +
				"estimatedTotalDistanceToTravel=" + estimatedTotalDistanceToTravel + ", " +
				"estimatedFuelConsumption=" + estimatedFuelConsumption + ", " +
				"estimatedCO2Produced=" + estimatedCO2Produced + ", " +
				"estimatedPosition=" + estimatedPosition + ']';
	}
}