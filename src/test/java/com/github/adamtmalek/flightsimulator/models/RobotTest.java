package com.github.adamtmalek.flightsimulator.models;

import com.github.adamtmalek.flightsimulator.GUI.Screen;
import com.github.adamtmalek.flightsimulator.models.io.FlightDataFileHandlerException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class RobotTest {
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
		slave = new Screen();
		slave.setVisible(true);
		JComboBox components = slave.getAirlineBox();
			Point location = components.getLocationOnScreen();
			System.out.println(location.x + ", " + location.y + "");
			mouseMoveAndClick(location.x, location.y);
			robot.keyPress(KeyEvent.VK_DOWN);
			robot.keyPress(KeyEvent.VK_DOWN);
			robot.keyPress(KeyEvent.VK_ENTER);
		JComboBox components1 = slave.getDepatureBox();
		Point location1 = components1.getLocationOnScreen();
		System.out.println(location1.x + ", " + location1.y + "");
		mouseMoveAndClick(location1.x, location1.y);
		robot.keyPress(KeyEvent.VK_DOWN);
		robot.keyPress(KeyEvent.VK_DOWN);
		robot.keyPress(KeyEvent.VK_ENTER);
		JButton components2 = slave.getAddButton();
		Point location2 = components2.getLocationOnScreen();
		System.out.println(location2.x +", "+location2.y+"");
		mouseMoveAndClick(location2.x, location2.y);


		}



		//creates an object that stimulates a mouse click
		public void mouseMoveAndClick ( int xLoc, int yLoc){
			robot.mouseMove(xLoc, yLoc);
			robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
			robot.delay(1000);

		}


		public static void main(String[] args) throws FlightDataFileHandlerException {
			System.out.println("Start test");
			new RobotTest();
			System.out.println("end the sesh");
		}
	}
