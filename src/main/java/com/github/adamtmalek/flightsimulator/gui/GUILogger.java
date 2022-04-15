package com.github.adamtmalek.flightsimulator.gui;

import com.github.adamtmalek.flightsimulator.logger.LogLevel;
import com.github.adamtmalek.flightsimulator.logger.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GUILogger extends Logger {
	private static @Nullable GUILogger instance = null;

	private @Nullable LogReceivedListener listener = null;

	public static @Nullable GUILogger getInstance() {
		return instance;
	}

	public GUILogger(@NotNull LogLevel logLevel, @Nullable String output) {
		super(logLevel, output);
		instance = this;
	}

	public void log(@NotNull LogLevel level, @NotNull String message) {
		if (listener == null) return;
		listener.onReceived(level, message);
	}

	public void setListener(@NotNull LogReceivedListener listener) {
		this.listener = listener;
	}

	@FunctionalInterface
	public interface LogReceivedListener {
		void onReceived(@NotNull LogLevel level, @NotNull String message);
	}
}
