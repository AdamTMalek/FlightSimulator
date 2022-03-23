package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.models.Flight;

import java.util.LinkedList;
import java.util.Queue;

public class SynchronizedQueue {

	public final Queue<Flight> queue;
	int maxQueueSize = 20;

	public SynchronizedQueue() {
		queue = new LinkedList<>();
	}

	public synchronized void push(Flight flight) throws InterruptedException {
		while (queue.size() == maxQueueSize) {
			wait();
		}
		queue.offer(flight);
		notifyAll();
	}

	// Removes head of queue and returns it.
	public synchronized Flight poll() throws InterruptedException {
		while (queue.isEmpty()) {
			wait();
		}
		Flight flight = queue.poll();
		notifyAll();
		return flight;
	}

	public synchronized boolean isEmpty() {

		return queue.isEmpty();
	}

}
