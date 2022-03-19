package com.github.adamtmalek.flightsimulator;

public class SharedQueue {

	protected int queueSize;
	private Flight[] queue;
	private int startIndex = 0;
	private int endIndex = 0;
	private int count = 0;

	SharedQueue(int queueSize) {

		if (queueSize <= 0 || queueSize > 20){
			throw new IllegalArgumentException("Invalid shared queue size");
		}
		this.queueSize = queueSize;
		buffer = new Flight[queueSize];
	}

	public synchronized void push(Flight flight) throws InterruptedException {
		while (count == queueSize){
			wait();
		}
		queue[startIndex] = flight;
		startIndex = (startIndex + 1) % queueSize;
		count++;
		notifyAll();
	}

	public synchronized Flight pop() throws InterruptedException {
		while (count == 0){
			wait();
		}
		Flight flight = queue[endIndex];
		endIndex = (endIndex + 1) % queueSize;
		count--;
		notifyAll();
		return flight;
	}
	
}
