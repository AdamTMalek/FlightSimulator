package com.github.adamtmalek.flightsimulator.collections;

import java.util.LinkedList;
import java.util.Queue;

public class SynchronizedQueue<T> {

	public final Queue<T> queue;
	int maxQueueSize = 20;

	public SynchronizedQueue() {
		queue = new LinkedList<>();
	}

	public synchronized void push(T object) throws InterruptedException {
		while (queue.size() == maxQueueSize) {
			wait();
		}
		queue.offer(object);
		notifyAll();
	}

	/**
	 * Retries and removes the head from the queue.
	 * @return Head of the queue
	 * @throws InterruptedException May throw interrupted exception, as wait() is used underneath
	 */
	public synchronized T poll() throws InterruptedException {
		while (queue.isEmpty()) {
			wait();
		}
		T object = queue.poll();
		notifyAll();
		return object;
	}

	public synchronized boolean isEmpty() {

		return queue.isEmpty();
	}

}
