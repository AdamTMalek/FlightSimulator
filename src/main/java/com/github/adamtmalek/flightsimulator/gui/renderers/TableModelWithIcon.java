package com.github.adamtmalek.flightsimulator.gui.renderers;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class TableModelWithIcon extends DefaultTableModel {
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 1) return ImageIcon.class;
		return super.getColumnClass(columnIndex);
	}
}
