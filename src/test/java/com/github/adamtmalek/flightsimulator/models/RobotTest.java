package com.github.adamtmalek.flightsimulator.models;

import com.github.adamtmalek.flightsimulator.FlightTrackerController;
import com.github.adamtmalek.flightsimulator.GUI.Screen;
import com.github.adamtmalek.flightsimulator.io.FlightDataFileHandlerException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class RobotTest {
	//initiating a robot
	Robot robot;
	//connecting robot glass to screen GUI
	Screen slave;

	//constructor also handles any isssues with the file used to generate the GUI
	public RobotTest() throws FlightDataFileHandlerException {

		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
//implements the controller interface
		var flightTrackerController = new FlightTrackerController();
		slave = new Screen(flightTrackerController);
		slave.setVisible(true);
		//simulates a real user accessing and using the GUI
		JComboBox components1 = slave.getDepartureBox();
		Point location1 = components1.getLocationOnScreen();
		System.out.println(location1.x + ", " + location1.y + "");
		mouseMoveAndClick(location1.x, location1.y);
		robot.keyPress(KeyEvent.VK_DOWN);
		robot.keyPress(KeyEvent.VK_DOWN);
		robot.keyPress(KeyEvent.VK_ENTER);
		JTextField components3 = slave.getFlightNumberTextField();
		Point location3 = components3.getLocationOnScreen();
		System.out.println(location3.x +", "+location3.y+"");
		mouseMoveAndClick(location3.x, location3.y);
		for (int i = 0; i < 4; i++) {
			robot.keyPress(KeyEvent.VK_7);
		}
JTable components5 = slave.getFlightPlanTable();
		Point location5 = components5.getLocationOnScreen();
		System.out.println(location5.x + ", " + location5.y + "");
		mouseMoveAndClick(location5.x +5, location5.y);
		robot.keyPress(KeyEvent.VK_DOWN);
		robot.keyPress(KeyEvent.VK_DOWN);
		robot.keyPress(KeyEvent.VK_DOWN);
		robot.keyPress(KeyEvent.VK_ENTER);
		JTable components6 = slave.getFlightPlanTable();
		Point location6 = components6.getLocationOnScreen();
		System.out.println(location6.x + ", " + location6.y + "");
		mouseMoveAndClick(location6.x +105, location6.y);
		robot.keyPress(KeyEvent.VK_DOWN);
		robot.keyPress(KeyEvent.VK_DOWN);
		robot.keyPress(KeyEvent.VK_ENTER);
		JTable components7 = slave.getFlightPlanTable();
		Point location7 = components7.getLocationOnScreen();
		System.out.println(location7.x + ", " + location7.y + "");
		mouseMoveAndClick(location7.x +220, location5.y);
		robot.keyPress(KeyEvent.VK_DOWN);
		robot.keyPress(KeyEvent.VK_ENTER);
		JButton components2 = slave.getAddButton();
		Point location2 = components2.getLocationOnScreen();
		System.out.println(location2.x +", "+location2.y+"");
		mouseMoveAndClick(location2.x, location2.y);
		JButton components4 =slave.getExitButton();
		Point location4 = components4.getLocationOnScreen();
		System.out.println(location4.x + ", "+ location4.y+"");
		mouseMoveAndClick(location4.x, location4.y);



	}

	public void getRobot() {
		//implements the controller interface
		var flightTrackerController = new FlightTrackerController();
		slave = new Screen(flightTrackerController);
		slave.setVisible(true);
		//simulates a real user accessing and using the GUI
		JComboBox components1 = slave.getDepartureBox();
		Point location1 = components1.getLocationOnScreen();
		System.out.println(location1.x + ", " + location1.y + "");
		mouseMoveAndClick(location1.x, location1.y);
		robot.keyPress(KeyEvent.VK_DOWN);
		robot.keyPress(KeyEvent.VK_DOWN);
		robot.keyPress(KeyEvent.VK_ENTER);
		JTextField components3 = slave.getFlightNumberTextField();
		Point location3 = components3.getLocationOnScreen();
		System.out.println(location3.x +", "+location3.y+"");
		mouseMoveAndClick(location3.x, location3.y);
		for (int i = 0; i < 4; i++) {
			robot.keyPress(KeyEvent.VK_7);
		}
		JTable components5 = slave.getFlightPlanTable();
		Point location5 = components5.getLocationOnScreen();
		System.out.println(location5.x + ", " + location5.y + "");
		mouseMoveAndClick(location5.x +5, location5.y);
		robot.keyPress(KeyEvent.VK_DOWN);
		robot.keyPress(KeyEvent.VK_DOWN);
		robot.keyPress(KeyEvent.VK_DOWN);
		robot.keyPress(KeyEvent.VK_ENTER);
		JTable components6 = slave.getFlightPlanTable();
		Point location6 = components6.getLocationOnScreen();
		System.out.println(location6.x + ", " + location6.y + "");
		mouseMoveAndClick(location6.x +105, location6.y);
		robot.keyPress(KeyEvent.VK_DOWN);
		robot.keyPress(KeyEvent.VK_DOWN);
		robot.keyPress(KeyEvent.VK_ENTER);
		JTable components7 = slave.getFlightPlanTable();
		Point location7 = components7.getLocationOnScreen();
		System.out.println(location7.x + ", " + location7.y + "");
		mouseMoveAndClick(location7.x +220, location5.y);
		robot.keyPress(KeyEvent.VK_DOWN);
		robot.keyPress(KeyEvent.VK_ENTER);
		JButton components2 = slave.getAddButton();
		Point location2 = components2.getLocationOnScreen();
		System.out.println(location2.x +", "+location2.y+"");
		mouseMoveAndClick(location2.x, location2.y);
		JButton components4 =slave.getExitButton();
		Point location4 = components4.getLocationOnScreen();
		System.out.println(location4.x + ", "+ location4.y+"");
		mouseMoveAndClick(location4.x, location4.y);

	}

	//creates an object that stimulates a mouse click
		public void mouseMoveAndClick ( int xLoc, int yLoc){
			robot.mouseMove(xLoc, yLoc);
			robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
			robot.delay(1000);

		}

//Opens a new screen so the test can be carrierd out
		public static void main(String[] args) throws FlightDataFileHandlerException {
			System.out.println("Start test");
			new RobotTest();
			System.out.println("end the sesh");
		}
	}
