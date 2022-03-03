package com.github.adamtmalek.flightsimulator.GUI.models;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

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
