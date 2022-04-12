package com.github.adamtmalek.flightsimulator.gui.mapView;

import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

public class FlightWaypoint extends DefaultWaypoint {

	private final String flightId;

	public FlightWaypoint(String flightId, GeoPosition position){
		super(position);
		this.flightId = flightId;
	}

	public String getFlightId(){
		return flightId;
	}
}
