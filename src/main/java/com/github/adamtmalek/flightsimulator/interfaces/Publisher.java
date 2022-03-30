package com.github.adamtmalek.flightsimulator.interfaces;

import com.github.adamtmalek.flightsimulator.logger.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class Publisher<T> {
	private final List<Subscriber<T>> subscribers = new ArrayList<>();
	private final @NotNull Logger logger = Logger.getInstance();

	public void registerSubscriber(@NotNull Subscriber<T> sub) {
		subscribers.add(sub);
	}

	public void deregisterSubscriber(@NotNull Subscriber<T> sub) {
		subscribers.remove(sub);
	}

	public void publish(@NotNull T data) {
		if (subscribers.isEmpty()) {
			logger.warn("Warning, no subscribers attached!");
		}

		for (Subscriber<T> sub : subscribers) {
			sub.callback(data);
		}
	}

	public void publishTo(T data, @NotNull Subscriber<T> subscriber) {
		subscriber.callback(data);
	}
}
