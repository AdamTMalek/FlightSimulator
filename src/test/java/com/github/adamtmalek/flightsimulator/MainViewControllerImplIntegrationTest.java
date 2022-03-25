package com.github.adamtmalek.flightsimulator;

import com.github.adamtmalek.flightsimulator.gui.MainView;
import com.github.adamtmalek.flightsimulator.gui.MainViewControllerImpl;
import com.github.adamtmalek.flightsimulator.io.FlightData;
import com.github.adamtmalek.flightsimulator.models.Aeroplane;
import com.github.adamtmalek.flightsimulator.models.Airline;
import com.github.adamtmalek.flightsimulator.models.Airport;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

class MainViewControllerImplIntegrationTest extends TestSuite {

	@AfterEach
	void removeReportAndFlightDataDirectories() {
		try {
			FileUtils.deleteDirectory(Simulator.FLIGHT_DATA_DIRECTORY.toFile());
			FileUtils.deleteDirectory(Simulator.FLIGHTS_REPORT_DIRECTORY.toFile());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	void testAirlineReportIsSavedOnClose() {
		final var controller = new MainViewControllerImpl(new MainViewStub(), new Simulator());
		controller.showView();
		controller.onWindowClosing();

		File actualFile = Simulator.FLIGHTS_REPORT_DIRECTORY.resolve("American Airlines.csv").toFile();
		File expectedFile = getPathFromResources("airline-reports/American Airlines.csv").toFile();
		org.assertj.core.api.Assertions.assertThat(actualFile).hasSameTextualContentAs(expectedFile);
	}

	private static class MainViewStub implements MainView {
		@Override
		public void setVisible(boolean visible) {
		}

		@Contract(value = " -> fail", pure = true)
		@Override
		public @NotNull Component getComponent() {
			throw new RuntimeException("Not implemented - this method should not be called on a MainViewStub");
		}

		@Override
		public @Nullable String getFlightNumber() {
			return null;
		}

		@Override
		public @Nullable Airline getSelectedAirline() {
			return null;
		}

		@Override
		public @Nullable Aeroplane getSelectedAeroplane() {
			return null;
		}

		@Override
		public @Nullable Airport getSelectedDepartureAirport() {
			return null;
		}

		@Override
		public @Nullable Airport getSelectedArrivalAirport() {
			return null;
		}

		@Override
		public @Nullable ZonedDateTime getDateTimeOfDeparture() {
			return null;
		}

		@Override
		public @NotNull List<Airport.ControlTower> getFlightPlan() {
			return Collections.emptyList();
		}

		@Override
		public void setAddButtonEnabled(boolean enabled) {

		}

		@Override
		public void displayData(@NotNull FlightData flightData) {

		}

		@Override
		public void updateFlightList() {

		}
	}
}
