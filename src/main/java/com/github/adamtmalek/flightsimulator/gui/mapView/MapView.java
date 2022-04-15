package com.github.adamtmalek.flightsimulator.gui.mapView;

import javax.swing.event.MouseInputListener;

import com.github.adamtmalek.flightsimulator.models.Flight;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.*;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.stream.Collectors;

public class MapView extends JFrame{
	private JXMapViewer mapViewer = new JXMapViewer();
	private WaypointPainter<FlightWaypoint> waypointPainter = new WaypointPainter<FlightWaypoint>();


	public MapView(){
		JXMapViewer mapViewer = new JXMapViewer();
		mapViewer.setFont(new Font("Default", Font.PLAIN, 20));

		TileFactoryInfo info = new OSMTileFactoryInfo();
		DefaultTileFactory tileFactory = new DefaultTileFactory(info);
		mapViewer.setTileFactory(tileFactory);
		tileFactory.setThreadPoolSize(2);

		// Set  focus
		GeoPosition edinburgh = new GeoPosition(55.95, -3.19);
		mapViewer.setZoom(16);
		mapViewer.setAddressLocation(edinburgh);

		//Set map viewer to use custom flight waypoint renderer
		waypointPainter.setRenderer(new FlightWaypointRenderer());
		mapViewer.setOverlayPainter(waypointPainter);

		//Set up mouse interaction.
		MouseInputListener mia = new PanMouseInputListener(mapViewer);
		mapViewer.addMouseListener(mia);
		mapViewer.addMouseMotionListener(mia);
		mapViewer.addMouseListener(new CenterMapListener(mapViewer));
		mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));

		// Display the viewer in a JFrame
		this.getContentPane().add(mapViewer);
		this.setSize(800, 600);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setTitle("Map Viewer - Active Flights");
		this.setVisible(true);

	}

	public void handleChange(DefaultListModel<Flight> model) {
		SwingUtilities.invokeLater(() -> drawMarkers(model));
	}

	private void drawMarkers(DefaultListModel<Flight> model){

		if(!model.isEmpty()) {
			final var flights = Arrays.stream(model.toArray()).map(Flight.class::cast).toList();

			final var flightWaypoints = flights.stream().filter(f->f.flightStatus().getStatus()== Flight.FlightStatus.Status.IN_PROGRESS)
					.map(f-> new FlightWaypoint(f.flightID(),
						new GeoPosition(f.flightStatus().getCurrentPosition().latitude(),f.flightStatus().getCurrentPosition().longitude()))).collect(Collectors.toSet());

			waypointPainter.setWaypoints(flightWaypoints);
			this.repaint(); //Call to ensure markers are updated on the map, otherwise only repainted on mouse interaction.
		}
	}

	}
