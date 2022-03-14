package com.github.adamtmalek.flightsimulator.interfaces;

import java.util.ArrayList;
import java.util.List;

public abstract class Publisher<T> {
	private List<Subscriber<T>> subscribers = new ArrayList<Subscriber<T>>();

	public void registerSubscriber(Subscriber sub){
		subscribers.add(sub);
	}

	public void deregisterSubscriber(Subscriber sub){
		subscribers.remove(sub);
	}

	public void publish(T data){
		if(subscribers.isEmpty()){
			System.out.println("Warning, no subscribers attached!");
		}
		for(Subscriber sub: subscribers){
			sub.callback(data);
		}
	}

	public void publishTo(T data, Subscriber subscriber){
		subscriber.callback(data);
	}
}
