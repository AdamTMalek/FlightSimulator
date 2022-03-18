package com.github.adamtmalek.flightsimulator.GUI.renderers;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public abstract class CustomListCellRenderer<E> extends JLabel implements ListCellRenderer<E> {
	@Override
	public boolean isOpaque() {
		return true;
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends E> list, E value, int index, boolean isSelected, boolean cellHasFocus) {
		if (value == null) return this;

		if (isSelected || cellHasFocus) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		setText(getText(value));
		return this;
	}

	protected abstract @NotNull String getText(@NotNull E value);
}
