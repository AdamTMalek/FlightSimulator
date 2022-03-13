package com.github.adamtmalek.flightsimulator.logger;

public class Logger {
	private static Logger logger = new Logger();

	private Logger() {
		this.loggerLevel = Level.ALL;
	}

	public static Logger getInstance() {
		return logger;
	}

	private int loggerLevel;

	public void setLevel(int level) {
		loggerLevel = level;
	}

	public int getLevel() {
		return loggerLevel;
	}

	public void log(int level, String msg) {
		if (level < this.loggerLevel) {return;}
		// DO STUFF
		System.out.println();
	}

	public void severe(String msg) {
		logger.log(Level.SEVERE, msg);
	}

	public void warning(String msg) {
		logger.log(Level.WARNING, msg);
	}

	public void info(String msg) {
		logger.log(Level.INFO, msg);
	}

	public void config(String msg) {
		logger.log(Level.CONFIG, msg);
	}

	public void fine(String msg) {
		logger.log(Level.FINE, msg);
	}

	public void finer(String msg) {
		logger.log(Level.FINER, msg);
	}

	public void finest(String msg) {
		logger.log(Level.FINEST, msg);
	}
}
