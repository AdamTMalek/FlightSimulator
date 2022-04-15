package com.github.adamtmalek.flightsimulator.logger;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JacksonXmlRootElement(localName = "loggerConfig")
public class LoggerConfigurations {
	@JacksonXmlProperty(localName = "configurations")
	public LoggerConfiguration[] loggerConfigurations;

	public static class LoggerConfiguration {
		@JacksonXmlProperty(localName = "class")
		private String loggerClass;
		@JacksonXmlProperty(localName = "level")
		private LogLevel logLevel;
		private @Nullable String output;

		public @NotNull String getLoggerClass() {
			return loggerClass;
		}

		public @NotNull LogLevel getLogLevel() {
			return logLevel;
		}

		public @Nullable String getOutput() {
			return output;
		}
	}
}