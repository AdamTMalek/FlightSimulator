package com.github.adamtmalek.flightsimulator.GUI.models;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

/**
 * The BoundListModel is an implemention of AbstractListModel
 * @see javax.swing.AbstractListModel
 * which data source is bounded to the data source passed in the constructor.
 *
 * This means, that no local copy of the list exists, and any change to the original
 * list be reflected in the data stored in this class as well.
 *
 * @param <E> Type of elements stored in the list.
 */
public class BoundListModel<E> extends AbstractListModel<E> {
	private final @NotNull List<E> data;

	public BoundListModel(@NotNull List<E> data) {
		this.data = data;
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
