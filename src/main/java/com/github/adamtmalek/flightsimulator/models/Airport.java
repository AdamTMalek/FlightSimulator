package com.github.adamtmalek.flightsimulator.models;

import com.github.adamtmalek.flightsimulator.FlightSimulationThreadManagement;
import com.github.adamtmalek.flightsimulator.SynchronizedQueue;
import com.github.adamtmalek.flightsimulator.interfaces.Publisher;
import com.github.adamtmalek.flightsimulator.interfaces.Subscriber;
import com.github.adamtmalek.flightsimulator.io.SerializableField;
import com.github.adamtmalek.flightsimulator.io.converters.GeodeticCoordinateConverter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static java.lang.Thread.sleep;

public class Airport {
	@SerializableField
	public final @NotNull String code;
	@SerializableField
	public final @NotNull String name;
	@NotNull
	@SerializableField(converter = GeodeticCoordinateConverter.class)
	public final GeodeticCoordinate position;
	public final ControlTower controlTower;

	public Airport(@NotNull String code,
								 @NotNull String name,
								 @NotNull GeodeticCoordinate position) {
		this.code = code;
		this.name = name;
		this.position = position;
		this.controlTower = new ControlTower(code, name, position);
	}

	@Override
	public String toString() {
		return String.format("Airport(code=%s name=%s latitude=%f longitude=%f)",
				code, name, position.latitude(), position.longitude());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Airport airport = (Airport) o;
		return code.equals(airport.code)
				&& name.equals(airport.name)
				&& position.latitude() == airport.position.latitude()
				&& position.longitude() == airport.position.longitude();
	}

	@Override
	public int hashCode() {
		return Objects.hash(code, name, position.latitude(), position.longitude());
	}

	public static class ControlTower extends Publisher<Flight> implements Subscriber<Flight>, Runnable {
		public final @NotNull String code;
		public final @NotNull String name;
		public final @NotNull GeodeticCoordinate position;
		private final SynchronizedQueue synchronizedQueue;
		private final HashMap<String, Flight> flightMap;
		private volatile boolean isRunning;

		public ControlTower(@NotNull String codeIn, @NotNull String nameIn, @NotNull GeodeticCoordinate positionIn) {
			code = codeIn;
			name = nameIn;
			position = positionIn;
			synchronizedQueue = new SynchronizedQueue();
			flightMap = new HashMap<String, Flight>();
			isRunning = true;
		}

		public void callback(Flight data) {
			try {
				System.out.println(name + " received `" + data.flightID() + "`");
				synchronizedQueue.push(data);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			try {
				while (isRunning) {

					if (!synchronizedQueue.isEmpty()) {

						// Each element in queue is inserted into map and cleared.
						while (!synchronizedQueue.isEmpty()) {

							Flight flight = synchronizedQueue.poll();

							// If exists FlightID exists in map, it is replaced. Otherwise, a new
							// entry is inserted into the map.
							flightMap.put(flight.flightID(), flight);
						}

						var updatedFlights = new ArrayList<Flight>(flightMap.values());

						// TODO publish to GUI
					}
					long waitTime = FlightSimulationThreadManagement.getApproxThreadPeriodMs();
					sleep(waitTime);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		public void stop() {
			isRunning = false;
		}

		@Override
		public String toString() {
			return "ControlTower(" +
					"code='" + code + '\'' +
					", name='" + name + '\'' +
					", position=" + position +
					')';
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			ControlTower that = (ControlTower) o;
			return code.equals(that.code) && name.equals(that.name) && position.equals(that.position);
		}

		@Override
		public int hashCode() {
			return Objects.hash(code, name, position);
		}
	}
}
