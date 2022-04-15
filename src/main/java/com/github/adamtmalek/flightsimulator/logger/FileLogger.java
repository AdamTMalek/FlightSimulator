package com.github.adamtmalek.flightsimulator.logger;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This logger, even though it has a public constructor should not be created by itself.
 * The public constructor is there to get reflection working, to be able to create it by the factory.
 */
public class FileLogger extends Logger {
	private final @NotNull File outputFile;

	public FileLogger(LogLevel level, String path) {
		super(level, path);
		assert this.output != null;
		outputFile = createLogFile();
	}

	private @NotNull File createLogFile() {
		String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
		String filePath = "%s/%s.log".formatted(output, date);

		final var file = new File(filePath);

		try {
			//noinspection ResultOfMethodCallIgnored
			file.getParentFile().mkdirs();

			if (file.createNewFile()) {
				return file;
			} else {
				throw new RuntimeException("Failed to create log file %s. Already exists.".formatted(file.getAbsolutePath()));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void log(LogLevel level, String msg) {
		try {
			FileWriter writer = new FileWriter(outputFile, true);
			writer.write(msg + "\n");
			writer.close();
		} catch (IOException e) {
			System.err.println("IOException while logging to: " + outputFile);
			System.out.println(e.getMessage());
		}
	}
}
