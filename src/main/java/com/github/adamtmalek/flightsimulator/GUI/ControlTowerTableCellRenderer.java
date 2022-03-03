package com.github.adamtmalek.flightsimulator.GUI;

import com.github.adamtmalek.flightsimulator.models.Airport;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class ControlTowerTableCellRenderer extends JLabel implements TableCellRenderer {
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (value == null || value.getClass() != Airport.ControlTower.class)
			return this;

		final var tower = (Airport.ControlTower)value;
		setText(tower.name);
		return this;
	}
}
