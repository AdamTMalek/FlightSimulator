package com.github.adamtmalek.flightsimulator.interfaces;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ControllerTest {

	@BeforeAll
	static void setUpBeforeClass() throws IOException, FileHandlerException {
		// Initialise Controller objects
		// Read all flights from the samples (the controller does this already?)
		public Controller testController = new Controller(); // TODO missing filepath
		public Flight testFlight = Flight(); // TODO make a proper flight
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void AddFlight() {
		fail("Not yet implemented");
		// Make a new flight
		// Controller.addFlight()
		testController.addFlight();
		// Check against Controller.getFlightData() ?
	}

	@Test
	void EditFlight() {
		fail("Not yet implemented");
		// Edit the flight that was just added
		// Controller.editFlight()
		// Check against Controller.getFlightData() ?
	}
	
	@Test
	void WriteToFlights() {
		fail("Not yet implemented");
		// write the new flights to disk
		// read them and make sure they all match what was just tested
	}
	
	@Test
	void RemoveFlight() {
		fail("Not yet implemented");
		// Controller.removeFlight()
	}

}
