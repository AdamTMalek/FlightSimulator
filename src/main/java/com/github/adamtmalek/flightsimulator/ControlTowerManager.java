package com.github.adamtmalek.flightsimulator;

import java.util.Queue;
import java.util.Iterator;

import com.github.adamtmalek.flightsimulator.interfaces.Publisher;
import com.github.adamtmalek.flightsimulator.models.Airport;
import com.github.adamtmalek.flightsimulator.models.Flight;

public class ControlTowerManager extends Publisher<Flight> implements Runnable {

	Airport.ControlTower controlTower;

	ControlTowerManager(Airport.ControlTower controlTower) {
		this.controlTower = controlTower;

	}

	public void run(){

		Queue<Flight> flightQueue = controlTower.flightQueue;

		Iterator<Flight> iterator = flightQueue.iterator();

		while (iterator.hasNext()) {

			publishTo(flightQueue.remove(), gui);

		}

	}

}
