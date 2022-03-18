package com.github.adamtmalek.flightsimulator.GUI.renderers;

import com.github.adamtmalek.flightsimulator.models.Airport;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class ComboBoxCellEditor extends DefaultCellEditor {
	@SuppressWarnings("unchecked")
	public ComboBoxCellEditor(@NotNull Collection<Airport.ControlTower> elements) {
		super(new JComboBox<Airport.ControlTower>());

		final var component = (JComboBox<Airport.ControlTower>) getComponent();
		component.setRenderer(new ControlTowerListCellRenderer());
		DefaultComboBoxModel<Airport.ControlTower> model = (DefaultComboBoxModel<Airport.ControlTower>) component.getModel();

		elements.forEach(model::addElement);
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		return super.getTableCellEditorComponent(table, value, isSelected, row, column);
	}
}
