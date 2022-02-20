package com.github.adamtmalek.flightsimulator.models.io;

public class FileHandlerException extends Exception {
	public FileHandlerException(String message) {
		super(message);
	}

	public FileHandlerException(Throwable cause) {
		super(cause);
	}

	public FileHandlerException(String message, Throwable cause) {
		super(message, cause);
	}
}
