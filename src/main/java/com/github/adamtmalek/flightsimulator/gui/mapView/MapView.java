package com.github.adamtmalek.flightsimulator.gui.mapView;

import javax.swing.event.MouseInputListener;

import com.github.adamtmalek.flightsimulator.models.Flight;
import javafx.collections.SetChangeListener;
import org.jetbrains.annotations.NotNull;
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
		waypointPainter.setRenderer(new FlightWaypointRenderer());
		mapViewer.setFont(new Font("Default", Font.PLAIN, 20));

		TileFactoryInfo info = new OSMTileFactoryInfo();
		DefaultTileFactory tileFactory = new DefaultTileFactory(info);
		mapViewer.setTileFactory(tileFactory);

		// Use 8 threads in parallel to load the tiles
		tileFactory.setThreadPoolSize(8);

		// Set the focus
		GeoPosition edinburgh = new GeoPosition(55.95, -3.19);
		mapViewer.setZoom(15);
		mapViewer.setAddressLocation(edinburgh);

		mapViewer.setOverlayPainter(waypointPainter);
		//Set up mouse interaction.
		MouseInputListener mia = new PanMouseInputListener(mapViewer);
		mapViewer.addMouseListener(mia);
		mapViewer.addMouseMotionListener(mia);
		mapViewer.addMouseListener(new CenterMapListener(mapViewer));
		mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));
		//mapViewer.addKeyListener(new PanKeyListener(mapViewer));
		// Display the viewer in a JFrame
		this.getContentPane().add(mapViewer);
		this.setSize(800, 600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	public void handleChange(DefaultListModel<Flight> model,
														@NotNull SetChangeListener.Change<? extends Flight> change) {
		if (change.wasAdded()) {
			addMarker(model, change.getElementAdded());
		}/* else {
			System.out.println("removing marker");
			removeMarker(change.getElementRemoved());
		}*/

	}

	private void addMarker(DefaultListModel<Flight> model, Flight flight){

		if(!model.isEmpty()) {
			final var flights = Arrays.stream(model.toArray()).map(Flight.class::cast).toList();

			final var flightWaypoints = flights.stream().map(f-> new FlightWaypoint(f.flightID(),
					new GeoPosition(f.flightStatus().getCurrentPosition().latitude(),f.flightStatus().getCurrentPosition().longitude()))).collect(Collectors.toSet());

			waypointPainter.setWaypoints(flightWaypoints);
			this.repaint();
		}
	}

	//private void removeMarker(Flight flight){
	//}
	}
