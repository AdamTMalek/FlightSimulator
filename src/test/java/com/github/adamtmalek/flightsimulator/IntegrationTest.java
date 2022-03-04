package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.GUI.Screen;
import com.github.adamtmalek.flightsimulator.models.*;
import com.github.adamtmalek.flightsimulator.models.io.FlightData;
import com.github.adamtmalek.flightsimulator.models.io.FlightDataFileHandlerException;
import com.github.adamtmalek.flightsimulator.models.io.TestSuite;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Scanner;

class IntegrationTest extends TestSuite {

	static Path genTmpDir() {
		try {
			return Files.createTempDirectory("integration-test");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	void openAndCloseAppCheckCorrectReadTest() {
		FlightTrackerController mainController = new FlightTrackerController();
		Screen screen = new Screen(mainController);
		screen.setVisible(true);
		Assertions.assertEquals("OK420", mainController.getFlightData().flights().get(0).flightID());
	}

	@Test
	void loadFlightsAddWriteReadTest() {
		// Temporary directory for this test
		Path tempDir = genTmpDir();

		// Initialise controller and load flight data
		FlightTrackerController mainController = new FlightTrackerController();

		FlightData flightData;
		try {
			flightData = mainController.readFlightData(getPathFromResources("flight-data")).getFlightData();
		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}

		// should be the first flight in the test file
		Assertions.assertEquals("OK420", flightData.flights().get(0).flightID());

		// Make new flight and try adding to controller
		Flight testFlight = Flight.buildWithFlightId("AA123",
			new Airline("AA", "American Airlines"),
			new Aeroplane("A330", "manufacturer", 2000, 15),
			new Airport("CDG", "Paris Charles de Gaulle",
				new GeodeticCoordinate(1, 2)),
			new Airport("LHR", "Heathrow",
				new GeodeticCoordinate(200, 100)),
			ZonedDateTime.of(2022, 3, 1, 16, 0, 0, 0, ZoneId.of("UTC+0")),
			new ArrayList<Airport.ControlTower>());
		mainController.addFlight(testFlight);

		// now we check if the last flight matches the one we just added
		Assertions.assertEquals("AA123", flightData.flights().get(flightData.flights().size() - 1).flightID());

		// write this to a file
		try {
			mainController.writeFlightData(tempDir);
		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}

		// load the file on a new controller/dataset and check if it matches
		FlightTrackerController readTestController = new FlightTrackerController();
		FlightData readTestFD;
		try {
			readTestFD = readTestController.readFlightData(tempDir).getFlightData(); // Is this how the tempDir works?
		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}
		Assertions.assertEquals("OK420", readTestFD.flights().get(0).flightID());
		Assertions.assertEquals("AA123", readTestFD.flights().get(readTestFD.flights().size() - 1).flightID());
	}

	@Test
	void loadFlightsDeleteWriteReadTest() {
		// Temporary directory for this test
		Path tempDir = genTmpDir();

		// Initialise controller and load flight data
		FlightTrackerController mainController = new FlightTrackerController();

		FlightData flightData;
		try {
			flightData = mainController.readFlightData(getPathFromResources("flight-data")).getFlightData();
		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}

		// should be the first flight in the test file
		Assertions.assertEquals("OK420", flightData.flights().get(0).flightID());


		// remove the testFlight/last flight from the controller assert if it worked
		mainController.removeFlight(flightData.flights().size() - 1);
		// last flight in flightData should be BA605 now
		Assertions.assertEquals("OK420", flightData.flights().get(flightData.flights().size() - 1).flightID());

		// write the flights down again and check if it still matches
		try {
			mainController.writeFlightData(tempDir);
		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e);
		}

		// load the file on a new controller/dataset and check if it matches
		FlightTrackerController readTestController2 = new FlightTrackerController();
		FlightData readTestFD2;
		try {
			readTestFD2 = readTestController2.readFlightData(tempDir).getFlightData(); // Is this how the tempDir works?
		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}
		Assertions.assertEquals("OK420", readTestFD2.flights().get(0).flightID());
		Assertions.assertEquals("OK420", readTestFD2.flights().get(readTestFD2.flights().size() - 1).flightID());
	}

	@Test
	void loadFlightsAddWriteReadDeleteWriteReadTest() {
		// Temporary directory for this test
		Path tempDir = genTmpDir();

		// Initialise controller and load flight data
		FlightTrackerController mainController = new FlightTrackerController();

		FlightData flightData;
		try {
			flightData = mainController.readFlightData(getPathFromResources("flight-data")).getFlightData();
		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}

		// should be the first flight in the test file
		Assertions.assertEquals("OK420", flightData.flights().get(0).flightID());

		// Make new flight and try adding to controller
		Flight testFlight = Flight.buildWithFlightId("AA123",
				new Airline("AA", "American Airlines"),
				new Aeroplane("A330", "manufacturer", 2000, 15),
				new Airport("CDG", "Paris Charles de Gaulle",
						new GeodeticCoordinate(1, 2)),
				new Airport("LHR", "Heathrow",
						new GeodeticCoordinate(200, 100)),
				ZonedDateTime.of(2022, 3, 1, 16, 0, 0, 0, ZoneId.of("UTC+0")),
				new ArrayList<Airport.ControlTower>());
		mainController.addFlight(testFlight);

		// now we check if the last flight matches the one we just added
		Assertions.assertEquals("AA123", flightData.flights().get(flightData.flights().size() - 1).flightID());

		// write this to a file
		try {
			mainController.writeFlightData(tempDir);
		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}

		// load the file on a new controller/dataset and check if it matches
		FlightTrackerController readTestController = new FlightTrackerController();
		FlightData readTestFD;
		try {
			readTestFD = readTestController.readFlightData(tempDir).getFlightData(); // Is this how the tempDir works?
		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}
		Assertions.assertEquals("OK420", readTestFD.flights().get(0).flightID());
		Assertions.assertEquals("AA123", readTestFD.flights().get(readTestFD.flights().size() - 1).flightID());

		// remove the testFlight/last flight from the controller assert if it worked
		mainController.removeFlight(flightData.flights().size() - 1);
		// last flight in flightData should be BA605 now
		Assertions.assertEquals("BA605", flightData.flights().get(flightData.flights().size() - 1).flightID());

		// write the flights down again and check if it still matches
		try {
			mainController.writeFlightData(tempDir);
		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e);
		}

		// load the file on a new controller/dataset and check if it matches
		FlightTrackerController readTestController2 = new FlightTrackerController();
		FlightData readTestFD2;
		try {
			readTestFD2 = readTestController2.readFlightData(tempDir).getFlightData(); // Is this how the tempDir works?
		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}
		Assertions.assertEquals("OK420", readTestFD2.flights().get(0).flightID());
		Assertions.assertEquals("BA605", readTestFD2.flights().get(readTestFD2.flights().size() - 1).flightID());
	}

	@Test
	void loadThreeNewFlightsAndWriteReportTest() {
		// Temporary directory for this test
		Path tmpDir = genTmpDir();

		// Initialise controller and load flight data
		FlightTrackerController mainController = new FlightTrackerController();
		FlightData flightData;
		try {
			flightData = mainController.readFlightData(getPathFromResources("flight-data")).getFlightData();
		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}

		// Generate some test flights
		ArrayList<Airport.ControlTower> towers = new ArrayList<Airport.ControlTower>();
		towers.add(new Airport.ControlTower("EDI", "Edinburgh",
			new GeodeticCoordinate(55.949997222222215, -3.370163888888889)));
		towers.add(new Airport.ControlTower("LHR", "Heathrow",
			new GeodeticCoordinate(300, 200)));
		Flight testF1 = Flight.buildWithFlightId("AA12",
			new Airline("AA", "American Airlines"),
			new Aeroplane("A330", "Airbus", 800, 768.439),
			new Airport("CDG", "Paris Charles de Gaulle",
				new GeodeticCoordinate(1, 2)),
			new Airport("LHR", "Heathrow",
				new GeodeticCoordinate(200, 100)),
			ZonedDateTime.of(2022, 3, 1, 16, 0, 0, 0, ZoneId.of("UTC+0")),
			towers);
		Flight testF2 = Flight.buildWithFlightId("AA123",
			new Airline("AA", "American Airlines"),
			new Aeroplane("A330", "Airbus", 800, 768.439),
			new Airport("CDG", "Paris Charles de Gaulle",
				new GeodeticCoordinate(1, 2)),
			new Airport("LHR", "Heathrow",
				new GeodeticCoordinate(300, 150)),
			ZonedDateTime.of(2022, 4, 2, 16, 0, 0, 0, ZoneId.of("UTC+0")),
			towers);
		Flight testF3 = Flight.buildWithFlightId("BA1234",
			new Airline("BA", "British Airways"),
			new Aeroplane("A330", "Airbus", 800, 768.439),
			new Airport("LHR", "Heathrow",
				new GeodeticCoordinate(300, 2)),
			new Airport("EDI", "Edinburgh",
				new GeodeticCoordinate(200, -300.2)),
			ZonedDateTime.of(2022, 4, 2, 16, 0, 0, 0, ZoneId.of("UTC+0")),
			towers);

		mainController.addFlight(testF1);
		mainController.addFlight(testF2);
		mainController.addFlight(testF3);

		mainController.writeAirlineReports(tmpDir);

//		String[] airlines = {"American Airlines", "British Airways", "Czech Airlines"};
//		for (int i = 0; i < 3; i++) {
//			System.out.println(airlines[i] + ":");
//			try {
//				File f = new File(tmpDir.resolve(airlines[i] + ".csv").toString());
//				Scanner reader = new Scanner(f);
//				while (reader.hasNextLine()) {
//					System.out.println(reader.nextLine());
//				}
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			}
//		}

		// Check against saved files
		File actualFile1 = new File(tmpDir.resolve("American Airlines.csv").toString());
		File expectedFile1 = new File(getPathFromResources("integration-tests/American Airlines.csv").toString());
		org.assertj.core.api.Assertions.assertThat(actualFile1).hasSameTextualContentAs(expectedFile1);

		File actualFile2 = new File(tmpDir.resolve("British Airways.csv").toString());
		File expectedFile2 = new File(getPathFromResources("integration-tests/British Airways.csv").toString());
		org.assertj.core.api.Assertions.assertThat(actualFile2).hasSameTextualContentAs(expectedFile2);

		File actualFile3 = new File(tmpDir.resolve("Czech Airlines.csv").toString());
		File expectedFile3 = new File(getPathFromResources("integration-tests/Czech Airlines.csv").toString());
		org.assertj.core.api.Assertions.assertThat(actualFile3).hasSameTextualContentAs(expectedFile3);
	}

	@Test
	void addNewFlightsAndWriteReportWithRobotTest() {
		// Temporary directory for this test
		Path tmpDir = genTmpDir();

		// Initialise controller and load flight data
		FlightTrackerController mainController = new FlightTrackerController();
		FlightData flightData;
		try {
			flightData = mainController.readFlightData(getPathFromResources("flight-data")).getFlightData();
		} catch (FlightDataFileHandlerException e) {
			throw new RuntimeException(e.getMessage());
		}

		// Launch Robot
		Robot robot;
		try {
			robot = new Robot();
		} catch (AWTException e) {
			throw new RuntimeException(e);
		}

		// Start GUI
		Screen screen = new Screen(mainController);
		screen.setVisible(true);

		// Add a flight using robot
		addDummyFlight(robot, screen);

		exit(robot, screen);

		String[] airlines = {"American Airlines", "British Airways", "Czech Airlines"};
		for (int i = 0; i < 3; i++) {
			System.out.println(airlines[i] + ":");
			try {
				File f = new File(tmpDir.resolve(airlines[i] + ".csv").toString());
				Scanner reader = new Scanner(f);
				while (reader.hasNextLine()) {
					System.out.println(reader.nextLine());
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		// Check against saved files
		File actualFile1 = new File(tmpDir.resolve("American Airlines.csv").toString());
		File expectedFile1 = new File(getPathFromResources("integration-tests/American Airlines.csv").toString());
		org.assertj.core.api.Assertions.assertThat(actualFile1).hasSameTextualContentAs(expectedFile1);

		File actualFile2 = new File(tmpDir.resolve("British Airways.csv").toString());
		File expectedFile2 = new File(getPathFromResources("integration-tests/British Airways.csv").toString());
		org.assertj.core.api.Assertions.assertThat(actualFile2).hasSameTextualContentAs(expectedFile2);

		File actualFile3 = new File(tmpDir.resolve("Czech Airlines.csv").toString());
		File expectedFile3 = new File(getPathFromResources("integration-tests/Czech Airlines.csv").toString());
		org.assertj.core.api.Assertions.assertThat(actualFile3).hasSameTextualContentAs(expectedFile3);
	}


	@Test
	void addFlight() {

		// Init robot
		try {
			var robot = new Robot();
			// Init controller
			var controller = new FlightTrackerController();

			var slave = new Screen(controller);
			slave.setVisible(true);

			addDummyFlight(robot,slave);

			final var addedFlight = controller.getFlightData().flights().get(2);
			Assertions.assertEquals("AA777", addedFlight.flightID());
			Assertions.assertEquals("A330", addedFlight.aeroplane().model());

			Assertions.assertEquals("Airbus", addedFlight.aeroplane().manufacturer());
			Assertions.assertEquals("AA", addedFlight.airline().code());
			Assertions.assertEquals("American Airlines", addedFlight.airline().name());

			Assertions.assertEquals("EDI",addedFlight.controlTowersToCross().get(0).code);
			Assertions.assertEquals("LHR",addedFlight.controlTowersToCross().get(1).code);
			Assertions.assertEquals("CDG",addedFlight.controlTowersToCross().get(2).code);

			Assertions.assertEquals("EDI",addedFlight.departureAirport().code);
			Assertions.assertEquals("CDG",addedFlight.destinationAirport().code);


		}catch(AWTException e){
			throw new RuntimeException(e);
		}

	}


	private void addDummyFlight(Robot robot, Screen slave){
		selectDepartureAircraft(robot,slave,2);
		enterFlightSerialNumber(robot,slave,"777");
		enterValidFlightPlan(robot,slave);
		add(robot,slave);
	}

	private void add(Robot robot, Screen slave){
		var addButton = slave.getAddButton();
		var location = addButton.getLocationOnScreen();
		mouseMoveAndClick(robot,location.x, location.y);
		robot.delay(500);
	}

	private  void exit(Robot robot, Screen slave){
		var exitButton =slave.getExitButton();
		var location = exitButton.getLocationOnScreen();
		mouseMoveAndClick(robot,location.x, location.y);
		robot.delay(200);


	}
	private void enterValidFlightPlan(Robot robot, Screen slave){
		var flightPlanTable = slave.getFlightPlanTable();
		var location = flightPlanTable.getLocationOnScreen();

		mouseMoveAndClick(robot,location.x +5, location.y);
		robot.delay(200);
		robot.keyPress(KeyEvent.VK_UP);
		robot.delay(500);
		robot.keyPress(KeyEvent.VK_ENTER);

		mouseMoveAndClick(robot,location.x +105, location.y);
		robot.delay(200);
		for(int i=0;i<2;i++) {
			robot.keyPress(KeyEvent.VK_UP);
			robot.delay(500);
		}
		robot.keyPress(KeyEvent.VK_ENTER);

		mouseMoveAndClick(robot,location.x +220, location.y);
		robot.delay(200);
		for(int i=0;i<3;i++) {
			robot.keyPress(KeyEvent.VK_UP);
			robot.delay(500);
		}
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.delay(500);

	}
	private void enterFlightSerialNumber(Robot robot, Screen slave, String serialNumber){
		var flightSerialNumberTextField = slave.getFlightNumberTextField();
		Point location = flightSerialNumberTextField.getLocationOnScreen();
		mouseMoveAndClick(robot,location.x, location.y);
		robot.delay(200);
		type(robot,serialNumber);


	}
	private void selectDepartureAircraft(Robot robot,Screen slave, int row){
		var departureBox = slave.getDepartureBox();
		Point location = departureBox.getLocationOnScreen();
		mouseMoveAndClick(robot,location.x, location.y);
		for(int i =0; i<row;i++) {
			robot.keyPress(KeyEvent.VK_DOWN);
			robot.keyPress(KeyEvent.VK_DOWN);
		}
		robot.keyPress(KeyEvent.VK_ENTER);
	}

	private void type(Robot robot, String string){
		for(char c: string.toCharArray()){
			int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
			if(keyCode==KeyEvent.CHAR_UNDEFINED){
				throw new RuntimeException("Key code not found for character " + c);
			}else{
				robot.keyPress(keyCode);
				robot.delay(200);
				robot.keyRelease(keyCode);
				robot.delay(200);
			}
		}
	}
	//creates an object that stimulates a mouse click
	private void mouseMoveAndClick (Robot robot, int xLoc, int yLoc){
		robot.mouseMove(xLoc, yLoc);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		robot.delay(500);
	}
}
