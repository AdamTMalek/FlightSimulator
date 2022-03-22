package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.models.Flight;

import java.util.LinkedList;
import java.util.Queue;

public class SynchronizedQueue {

	private final Queue<Flight> queue;

	public SynchronizedQueue() {
		queue = new LinkedList<>();
	}

	public synchronized void push(Flight flight) {
		queue.offer(flight);
		notifyAll();
	}

	public synchronized Flight pop() throws InterruptedException {
		while (queue.size() == 0){
			wait();
		}
		return queue.poll();
	}

}
