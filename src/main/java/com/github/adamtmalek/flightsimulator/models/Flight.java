package com.github.adamtmalek.flightsimulator.models;

import com.github.adamtmalek.flightsimulator.interfaces.Publisher;
import com.github.adamtmalek.flightsimulator.models.io.SerializableField;
import org.jetbrains.annotations.NotNull;

import java.security.InvalidParameterException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.ListIterator;


public class Flight  extends Publisher<GeodeticCoordinate>{

	@SerializableField
	public final @NotNull String flightID;
	public final @NotNull Airline airline;
	@SerializableField
	public final @NotNull Aeroplane aeroplane;
	@SerializableField
	public final @NotNull Airport departureAirport;
	@SerializableField
	public final @NotNull Airport destinationAirport;
	@SerializableField
	public final @NotNull ZonedDateTime departureDate;
	public final @NotNull List<Airport.ControlTower> controlTowersToCross;
	public final double distanceTravelled; // Units: Kilometre
	public final double estimatedFuelConsumption; // Units: Litres
	public final double estimatedCO2Produced; // Units: Kilograms

	private ListIterator<Airport.ControlTower> controlTowerIterator;

	public Flight(
			@NotNull String flightID,
			@NotNull Airline airline,
			@NotNull Aeroplane aeroplane,
			@NotNull Airport departureAirport,
			@NotNull Airport destinationAirport,
			@NotNull ZonedDateTime departureDate,
			@NotNull List<Airport.ControlTower> controlTowersToCross,
			double distanceTravelled,
			double estimatedFuelConsumption,
			double estimatedCO2Produced) {

		this.flightID = flightID;
		this.airline = airline;
		this.aeroplane = aeroplane;
		this.departureAirport = departureAirport;
		this.destinationAirport = destinationAirport;
		this.departureDate = departureDate;
		this.controlTowersToCross = controlTowersToCross;
		this.distanceTravelled = distanceTravelled;
		this.estimatedFuelConsumption = estimatedFuelConsumption;
		this.estimatedCO2Produced = estimatedCO2Produced;

		this.controlTowerIterator = controlTowersToCross.subList(1,controlTowersToCross.size()).listIterator();
	}

	public static Flight buildWithSerialNumber(@NotNull String serialNumber,
																						 @NotNull Airline airline,
																						 @NotNull Aeroplane aeroplane,
																						 @NotNull Airport departureAirport,
																						 @NotNull Airport destinationAirport,
																						 @NotNull ZonedDateTime departureDate,
																						 @NotNull List<Airport.ControlTower> controlTowersToCross) {
		final var distanceTravelled = calculateDistanceTravelled(controlTowersToCross);
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
				estimatedCO2Produced);
	}

	public static Flight buildWithFlightId(@NotNull String fullFlightId,
																				 @NotNull Airline airline,
																				 @NotNull Aeroplane aeroplane,
																				 @NotNull Airport departureAirport,
																				 @NotNull Airport destinationAirport,
																				 @NotNull ZonedDateTime departureDate,
																				 @NotNull List<Airport.ControlTower> controlTowersToCross) {
		final var estimatedTotalDistanceTravelled = calculateDistanceTravelled(controlTowersToCross);
		final var estimatedFuelConsumption = calculateEstimatedFuelConsumption(aeroplane.fuelConsumptionRate(), estimatedTotalDistanceTravelled);
		final var estimatedCO2Produced = calculateEstimatedCO2Produced(estimatedFuelConsumption);

		return new Flight(fullFlightId,
				airline,
				aeroplane,
				departureAirport,
				destinationAirport,
				departureDate,
				controlTowersToCross,
				estimatedTotalDistanceTravelled,
				estimatedFuelConsumption,
				estimatedCO2Produced);
	}

	public void tick(){

		final var currentCoordinate = calculateCurrentPosition();

			final var nextControlTower = controlTowerIterator.hasNext()
					? controlTowerIterator.next() : controlTowersToCross.get(controlTowersToCross.size()-1);

		publishTo(currentCoordinate,nextControlTower);
	}

	private double calculateCurrentDistanceTravelled(){
		return aeroplane.speed() * getCurrentElapsedDuration();
	}
	private GeodeticCoordinate calculateCurrentPosition(){
		return new GeodeticCoordinate(0.0,0.0);
	}

	private double getCurrentElapsedDuration(){
		return 0.0;
	}


	private static String constructFlightId(String serialNumber, Airline airline) throws InvalidParameterException {
		try {
			Integer.parseInt(serialNumber);
		} catch (NumberFormatException e) {
			throw new InvalidParameterException("Flight serial number must be a series of integer values.");
		}
		return airline.code() + serialNumber;
	}

	private static double calculateDistanceTravelled(List<Airport.ControlTower> controlTowersToCross) {
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

	public String flightID() {
		return flightID;
	}

	public Airline airline() {
		return airline;
	}

	public Aeroplane aeroplane() {
		return aeroplane;
	}

	public Airport departureAirport() {
		return departureAirport;
	}

	public Airport destinationAirport() {
		return destinationAirport;
	}

	public ZonedDateTime departureDate() {
		return departureDate;
	}

	public List<Airport.ControlTower> controlTowersToCross() {
		return controlTowersToCross;
	}

	public double distanceTravelled() {
		return distanceTravelled;
	}

	public double estimatedFuelConsumption() {
		return estimatedFuelConsumption;
	}

	public double estimatedCO2Produced() {
		return estimatedCO2Produced;
	}
}
