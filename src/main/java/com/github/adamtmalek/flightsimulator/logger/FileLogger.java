package com.github.adamtmalek.flightsimulator.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class FileLogger extends Logger {
	private String loggingDir;
	private final Charset charset = Charset.defaultCharset();
	private boolean hasErrored = false;

	protected FileLogger(LogLevel level, String path) {
		super(level);
		this.loggingDir = path;

		String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
		try {
			File dir = new File(loggingDir);
			this.hasErrored = !dir.mkdirs();
			String filePath = loggingDir + "" + date + ".log";
			File file = new File(filePath);
			if (!file.createNewFile()) {
				System.out.println("Log file with the same timestamp already exists.");
				this.hasErrored = true;
			}
			this.loggingDir = filePath;
//			try (BufferedWriter writer = Files.newBufferedWriter(loggingDir, charset)) {
//				String msg = "Launched.";
//				writer.write(msg, 0, msg.length());
//			} catch (IOException e) {
//				//System.err.format("IOException: ")
//			}
		} catch (SecurityException e) {
			System.err.println("SecurityException while creating logging dir at: " + this.loggingDir + e.getMessage());
		} catch (IOException e) {
			System.err.println("IOException while creating new log file: " + e.getMessage());
		}
	}
	public void log(LogLevel level, String msg) {
		if (this.hasErrored) {return;}
		try {
			FileWriter writer = new FileWriter(loggingDir, true);
			writer.write(msg + "\n");
			writer.close();
		} catch (IOException e) {
			System.err.println("IOException while logging to: " + loggingDir);
			System.out.println(e.getMessage());
			this.hasErrored = true;
		}

	}
}
