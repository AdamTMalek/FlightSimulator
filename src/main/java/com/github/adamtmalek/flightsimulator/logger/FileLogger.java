package com.github.adamtmalek.flightsimulator.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class FileLogger extends Logger {
	private String loggingDir;
	private final Charset charset = Charset.defaultCharset();
	FileLogger(LogLevel level, String path) {
		loggerLevel = level;
		loggingDir = path;

		String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
		try {
			String filePath = loggingDir + "/" + date + ".log";
			File file = new File(filePath);
			if (!file.createNewFile()) {
				System.out.println("Log file with the same timestamp already exists.");
			}
			loggingDir = filePath;
//			try (BufferedWriter writer = Files.newBufferedWriter(loggingDir, charset)) {
//				String msg = "Launched.";
//				writer.write(msg, 0, msg.length());
//			} catch (IOException e) {
//				//System.err.format("IOException: ")
//			}
		} catch (IOException e) {
			System.err.println("IOException while creating new log file: " + e.getMessage());
		}
	}
	public void log(LogLevel level, String msg) {
		try {
			FileWriter writer = new FileWriter(loggingDir, true);
			writer.write(msg + "\n");
			writer.close();
		} catch (IOException e) {
			System.err.println("IOException while logging to: " + loggingDir);
			System.out.println(e.getMessage());
		}

	}
}
