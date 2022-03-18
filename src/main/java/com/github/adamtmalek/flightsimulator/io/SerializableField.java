package com.github.adamtmalek.flightsimulator.io;

import com.github.adamtmalek.flightsimulator.io.converters.Converter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Use this annotation to make field serializable.
 * Fields that are not marked with this annotation will be ignored by the serializer,
 * and will not be saved to or read from a file.
 * <br>
 * Note that only fields of a class need to be marked with the annotation, even if a
 * type requiring a custom converter is defined as a constructor parameter.
 * The serializer will obtain the converter for the declared parameter based on the
 * field.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface SerializableField {
	/**
	 * Allows custom name to be set as a heading.
	 * The default value, empty string, means that the name of the field will be set
	 * from the name of the field.
	 */
	@NotNull String name() default "";

	/**
	 * Fields of types that are non-default (i.e. different than primitives, Strings
	 * or Numbers) need a custom converter to be serializable.
	 */
	@NotNull Class<? extends Converter<?>> converter() default DefaultConverter.class;

	boolean primaryKey() default false;

	/**
	 * This class is only meant as a workaround for the converter default value,
	 * as it cannot accept null, we have to pass some class which meets the criteria
	 * (i.e. it extends converter).
	 * <br>
	 * Ideally, we could have some DefaultConverter which would implement Converter<Number>,
	 * but unfortunately in Java, primitives are primitives, and hence this would not work
	 * for them, as they do not inherit from the Number class.
	 */
	final class DefaultConverter implements Converter<Object> {
		@Override
		@Contract(pure = true)
		public @Nullable Object convertFromString(String... values) {
			return null;
		}

		@Override
		public String convertToString(@NotNull Object object) {
			return object.toString();
		}
	}

	enum SerializationType {
		DIRECT,
		MAPPING,
	}
}
