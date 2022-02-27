package com.github.adamtmalek.flightsimulator.models;

import com.github.adamtmalek.flightsimulator.models.io.SerializableField;
import com.github.adamtmalek.flightsimulator.models.io.converters.GeodeticCoordinateConverter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Airport {
	@SerializableField
	public final @NotNull String code;
	@SerializableField
	public final @NotNull String name;
	@NotNull
	@SerializableField(converter = GeodeticCoordinateConverter.class)
	public final GeodeticCoordinate position;
	public final ControlTower controlTower;

	public Airport(@NotNull String code,
								 @NotNull String name,
								 @NotNull GeodeticCoordinate position) {
		this.code = code;
		this.name = name;
		this.position = position;
		this.controlTower = new ControlTower(code, name, position);
	}

	@Override
	public String toString() {
		return String.format("Airport(code=%s name=%s latitude=%f longitude=%f)",
			code, name, position.latitude(), position.longitude());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Airport airport = (Airport) o;
		return code.equals(airport.code)
			&& name.equals(airport.name)
			&& position.latitude() == airport.position.latitude()
			&& position.longitude() == airport.position.longitude();
	}

	@Override
	public int hashCode() {
		return Objects.hash(code, name, position.latitude(), position.longitude());
	}

	public static class ControlTower {
		public final @NotNull String code;
		public final @NotNull String name;
		public final @NotNull GeodeticCoordinate position;

		public ControlTower(@NotNull String codeIn, @NotNull String nameIn, @NotNull GeodeticCoordinate positionIn) {
			code = codeIn;
			name = nameIn;
			position = positionIn;
		}

		@Override
		public String toString() {
			return "ControlTower(" +
				"code='" + code + '\'' +
				", name='" + name + '\'' +
				", position=" + position +
				')';
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			ControlTower that = (ControlTower) o;
			return code.equals(that.code) && name.equals(that.name) && position.equals(that.position);
		}

		@Override
		public int hashCode() {
			return Objects.hash(code, name, position);
		}
	}
}
