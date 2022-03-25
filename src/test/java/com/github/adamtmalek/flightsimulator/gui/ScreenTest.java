package com.github.adamtmalek.flightsimulator.gui;

import com.github.adamtmalek.flightsimulator.Simulator;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.swing.*;

@RunWith(MockitoJUnitRunner.class)
public class ScreenTest extends AssertJSwingJUnitTestCase {
	private final @NotNull MainViewController mockedController = Mockito.mock(MainViewController.class);
	private final @NotNull Simulator mockedSimulator = Mockito.mock(Simulator.class);
	private FrameFixture frame = null;

	@Override
	protected void onSetUp() {
		final var view = GuiActionRunner.execute(() -> new Screen(mockedController, mockedSimulator));
		view.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);  // Otherwise, closing the frame will stop all threads
		frame = new FrameFixture(robot(), view);
		frame.show();
	}

	@Test
	public void testOnOpenFileClickedIsCalled() {
		frame.menuItemWithPath("File", "Open").click();
		Mockito.verify(mockedController).onOpenFileClicked();
	}

	@Test
	public void testOnWindowClosingIsCalledWhenWindowIsClosed() {
		frame.close();
		Mockito.verify(mockedController).onWindowClosing();
	}
}