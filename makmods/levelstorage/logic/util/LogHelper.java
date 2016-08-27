package makmods.levelstorage.logic.util;

import org.apache.logging.log4j.Level;

import makmods.levelstorage.LevelStorage;

/**
 * Just a util helper for easier logging
 */
public class LogHelper {

	public static void severe(String message) { //Used to be Level.SEVERE
		LevelStorage.logger.log(Level.TRACE, message);
	}

	public static void fine(String message) { //Used to be Level.FINE
		LevelStorage.logger.log(Level.TRACE, message);
	}

	public static void finer(String message) { //Used to be Level.FINER
		LevelStorage.logger.log(Level.TRACE, message);
	}

	public static void finest(String message) { //Used to be Level.FINEST
		LevelStorage.logger.log(Level.TRACE, message);
	}

	public static void info(String message) {
		LevelStorage.logger.log(Level.INFO, message);
	}

	public static void warning(String message) {
		LevelStorage.logger.log(Level.WARN, message);
	}

}
