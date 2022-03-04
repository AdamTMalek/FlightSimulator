package com.github.adamtmalek.flightsimulator.GUI.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

/**
 * The BoundComboBoxModel is a model for ComboBoxes which data source is bounded
 * to the data source passed in the constructor.
 *
 * This means, that no local copy of the list exists, and any change to the original
 * list be reflected in the data stored in this class as well.
 *
 * @param <E> Type of elements stored in the list.
 */
public class BoundComboBoxModel<E> extends AbstractListModel<E> implements MutableComboBoxModel<E> {
	private final @NotNull List<E> data;
	private @Nullable E selectedObject = null;

	public BoundComboBoxModel(@NotNull List<E> data) {
		this.data = data;
	}

	@Override
	public void addElement(E item) {
		data.add(item);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void removeElement(Object obj) {
		data.remove((E)obj);
	}

	@Override
	public void insertElementAt(E item, int index) {
		data.add(index, item);
	}

	@Override
	public void removeElementAt(int index) {
		data.remove(index);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setSelectedItem(Object anItem) {
		if ((selectedObject != null && !selectedObject.equals(anItem)) ||
				selectedObject == null && anItem != null) {
			selectedObject = (E)anItem;
			fireContentsChanged(this, -1, -1);
		}
	}

	@Override
	public Object getSelectedItem() {
		return selectedObject;
	}

	@Override
	public int getSize() {
		return data.size();
	}

	@Override
	public E getElementAt(int index) {
		return data.get(index);
	}
}
