package com.github.adamtmalek.flightsimulator.gui.mapView;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.WaypointRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.net.URL;

public class FlightWaypointRenderer implements WaypointRenderer<FlightWaypoint> {
	private BufferedImage flightIcon;

	public FlightWaypointRenderer(){
		URL res = getClass().getResource("/plane.png");

		try{
			final var temp = ImageIO.read(res).getScaledInstance(25,25,Image.SCALE_DEFAULT);
			flightIcon = new BufferedImage(25,25,BufferedImage.TYPE_INT_ARGB);
			flightIcon.getGraphics().drawImage(temp,0,0,null);

		}catch(Exception e){
			e.printStackTrace();
			return;
		}
	}


	@Override
	public void paintWaypoint(Graphics2D g, JXMapViewer map, FlightWaypoint waypoint) {
		g = (Graphics2D)g.create();

		if (flightIcon == null)
			return;


		Point2D point = map.getTileFactory().geoToPixel(waypoint.getPosition(), map.getZoom());

		int x = (int)point.getX();
		int y = (int)point.getY();

		g.drawImage(flightIcon, x -flightIcon.getWidth() / 2, y -flightIcon.getHeight(), null);

		String label = waypoint.getFlightId();

		FontMetrics metrics = g.getFontMetrics();
		int tw = metrics.stringWidth(label);
		int th =1+metrics.getAscent();

		g.drawString(label, x - tw / 2, y  + flightIcon.getHeight());

		g.dispose();

	}
}
