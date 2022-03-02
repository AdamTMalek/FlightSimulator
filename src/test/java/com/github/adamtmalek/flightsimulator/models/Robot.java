package com.github.adamtmalek.flightsimulator.models;
import com.github.adamtmalek.flightsimulator.GUI.Screen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Objects;

public class RobotTest {
    Robot robot;
    //connecting robot glass to screen GUI
    Screen slave = new Screen();

    public RobotTest()
        {
            try {
                robot = new Robot();
            } catch (AWTException e) {
                e.printStackTrace();
            }
            Component[] components = slave.getComponents();
            for (int cnt = 0; cnt < components.length; cnt++) {
                Point location = components[cnt].getLocationOnScreen();
                System.out.println(location.x + ", " + location.y + "");
                if(components[cnt] instanceof JComboBox){
                    robot.keyPress(KeyEvent.VK_DOWN);
                    robot.keyPress(KeyEvent.VK_DOWN);
                    robot.keyPress(KeyEvent.VK_ENTER);
                }
        }
        //creates an object that stimulates a mouse click
    public void mouseMoveAndClick(int xLoc, int yLoc){
        robot.mouseMove(xLoc, yLoc);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

    }



    public static void main(String args[]) {
        System.out.println("Start test");
        new RobotTest();
        System.out.println("end the sesh");
    }
}
