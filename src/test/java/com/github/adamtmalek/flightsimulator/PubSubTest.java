package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.interfaces.Publisher;
import com.github.adamtmalek.flightsimulator.interfaces.Subscriber;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PubSubTest {


	@Test
	void testPubSubConncetion() {

		class PublisherTest extends Publisher<String> { }

		class SubscriberTest implements Subscriber<String>{
			public String receivedString = new String();

			public void callback(String data){
				receivedString = data;
			}
		}

		PublisherTest publisher = new PublisherTest();
		SubscriberTest subscriber = new SubscriberTest();

		publisher.registerSubscriber(subscriber);
		String dataToSend = "Hello World!";
		publisher.publish(dataToSend);

		Assertions.assertEquals(dataToSend,subscriber.receivedString);

	}

}
