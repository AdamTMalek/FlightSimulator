package com.github.adamtmalek.flightsimulator.interfaces;

public interface Subscriber<T> {

	public void callback(T data);
}
