package com.github.adamtmalek.flightsimulator;

import javafx.beans.value.ObservableValueBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * This is an implementation of ObservableValueBase which adds setValue methods
 * that when used, fire the value changed event informing other observers
 * about the change.
 * @param <T> Type of value
 */
public class OurObservableValue<T> extends ObservableValueBase<T> {
	private @Nullable T currentValue;

	public OurObservableValue(@Nullable T initialValue) {
		currentValue = initialValue;
	}

	public OurObservableValue() {
		this(null);
	}

	public void setValue(@Nullable T value) {
		currentValue = value;
		fireValueChangedEvent();
	}

	/**
	 * Using this method a function can be passed to this method to update the value based on the old value
	 * @param valueSetter Value setter, the argument of this function will be the current value.
	 */
	public void setValue(@NotNull Function<T, T> valueSetter) {
		setValue(valueSetter.apply(currentValue));
	}

	@Override
	public T getValue() {
		return currentValue;
	}
}
