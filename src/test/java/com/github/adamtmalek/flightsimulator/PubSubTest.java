package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.interfaces.Publisher;
import com.github.adamtmalek.flightsimulator.interfaces.Subscriber;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PubSubTest {

	private class PublisherTest extends Publisher<String> { }

	private class SubscriberTest implements Subscriber<String>{
		public String receivedString = new String();

		public void callback(String data){
			receivedString = data;
		}
	}

	@Test
	void testPubSubConnection() {


		PublisherTest publisher = new PublisherTest();
		SubscriberTest subscriber = new SubscriberTest();

		publisher.registerSubscriber(subscriber);
		String dataToSend = "Hello World!";
		publisher.publish(dataToSend);

		Assertions.assertEquals(dataToSend,subscriber.receivedString);

	}

	@Test
	void testPubToConnection(){
		PublisherTest publisher = new PublisherTest();
		SubscriberTest subscriber = new SubscriberTest();

		String dataToSend = "Hello World!";

		publisher.publishTo(dataToSend,subscriber);

		Assertions.assertEquals(dataToSend,subscriber.receivedString);

	}

}
