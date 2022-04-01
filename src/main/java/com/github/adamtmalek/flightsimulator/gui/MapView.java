package com.github.adamtmalek.flightsimulator.gui;

import javax.swing.WindowConstants;
import javax.swing.event.MouseInputListener;

import com.github.adamtmalek.flightsimulator.models.Flight;
import javafx.collections.SetChangeListener;
import org.jetbrains.annotations.NotNull;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.cache.FileBasedLocalCache;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.viewer.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MapView extends JFrame{
	private JXMapViewer mapViewer = new JXMapViewer();
	private WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<Waypoint>();


	public MapView(){
		JXMapViewer mapViewer = new JXMapViewer();

		// Create a TileFactoryInfo for OpenStreetMap
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
		mapViewer.addKeyListener(new PanKeyListener(mapViewer));

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
		} else {
			System.out.println("removing marker");
			removeMarker(change.getElementRemoved());
		}

	}

	private void addMarker(DefaultListModel<Flight> model, Flight flight){

		final var currentPos = flight.flightStatus().getCurrentPosition();

		final var flights = Arrays.stream(model.toArray()).map(Flight.class::cast).toList();

		final var geoPosistiions = flights.stream().map(f->new GeoPosition(f.flightStatus().getCurrentPosition().latitude(),
				f.flightStatus().getCurrentPosition().longitude())).collect(Collectors.toSet());

		final var waypoints = geoPosistiions.stream().map(DefaultWaypoint::new).collect(Collectors.toSet());

		waypointPainter.setWaypoints(waypoints);
	}

	private void removeMarker(Flight flight){

	}
	}
