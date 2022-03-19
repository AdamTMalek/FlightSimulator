package com.github.adamtmalek.flightsimulator.gui.dialog;

import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class Dialog extends JDialog {
	private @NotNull DialogResult dialogResult = DialogResult.OK;

	@NotNull
	public DialogResult showDialog() {
		setVisible(true);
		return dialogResult;
	}

	@MustBeInvokedByOverriders
	protected void onOK() {
		dialogResult = DialogResult.OK;
		closeDialog();
	}

	@MustBeInvokedByOverriders
	protected void onCancel() {
		dialogResult = DialogResult.Cancelled;
		closeDialog();
	}

	private void closeDialog() {
		setVisible(false);
		dispose();
	}
}
