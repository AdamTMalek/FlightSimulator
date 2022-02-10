package com.github.adamtmalek.flightsimulator.models;

public record Airport(
		String code, 
		String name,
		GeodeticCoordinate position) {
	private boolean isStringAlphabetic(String s) {
		for (int i = 0; i<s.length(); i++) {
			if (!Character.isAlphabetic(s.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	// TODO validate name/position?
	public Airport(
			String code, 
			String name,
			GeodeticCoordinate position) {
		if (code.length() != 3 || !isStringAlphabetic(code)) {
			throw new java.lang.IllegalArgumentException(
					String.format("Invalid airport code must be 3 letters: %f", code));
		}
		this.code = code.toUpperCase(); // TODO should maybe test
		this.name = name;
		this.position = position;
		
	}
}
