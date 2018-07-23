package org.mcraft.kantanmemory.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;

import org.mcraft.kantanmemory.core.data.UserWordData;
import org.mcraft.kantanmemory.file.data.UserConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class DataInitializer {
	private String wordlistDir;
	private String dataDir;

	public DataInitializer() {
		try {
			String jarDir = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI())
					.getParent();
			String wordlistFolderName = "wordlists";
			String dataFolderName = "data";

			setWordlistDir(jarDir + File.separator + wordlistFolderName);
			setDataDir(jarDir + File.separator + dataFolderName);

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

	}

	public void initializeAll() {
		initializeWordlists();
		initializeConfig();
		initializeUserWordDataFile();
	}

	/**
	 * 
	 * @return true if the data directory exist
	 */
	public boolean isDataDirExist() {
		return new File(dataDir).exists();
	}

	public boolean isConfigFileExist() {
		String configFileName = "user-config.yml";
		File configFile = new File(dataDir + File.separator + configFileName);

		return configFile.exists();
	}

	/**
	 * Create new config file in data directory.
	 */
	public void initializeConfig() {
		if (!isDataDirExist()) {
			new File(dataDir).mkdirs();
		}

		String configFileName = "user-config.yml";
		File configFile = new File(dataDir + File.separator + configFileName);
		UserConfig config = null;

		if (!isConfigFileExist()) {
			ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

			config = new UserConfig();
			try {
				configFile.createNewFile();
				mapper.writeValue(configFile, config);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		// Refresh the list of word list progresses
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		try {
			config = mapper.readValue(configFile, UserConfig.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		File wordlistFolder = new File(wordlistDir);
		if (wordlistFolder.exists() && wordlistFolder.isDirectory()) {
			for (File wordlist : wordlistFolder.listFiles()) {
				if (!config.getWordlists().contains(wordlist.getName())) {
					config.setWordlistProgress(wordlist.getName(), 0);
				}
			}
			try {
				mapper.writeValue(configFile, config);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public boolean isUserWordDataFileExist() {
		String userWordDataFileName = "user-word-data.yml";
		File userWordDataFile = new File(dataDir + File.separator + userWordDataFileName);

		return userWordDataFile.exists();
	}

	public void initializeUserWordDataFile() {
		if (!isDataDirExist()) {
			new File(dataDir).mkdirs();
		}

		if (!isUserWordDataFileExist()) {
			ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

			UserWordData[] userWordDataList = new UserWordData[] { null };
			String userWordDataFileName = "user-word-data.yml";

			File userWordDataFile = new File(dataDir + File.separator + userWordDataFileName);

			try {
				userWordDataFile.createNewFile();
				mapper.writeValue(userWordDataFile, userWordDataList);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @return true if the word list directory exist
	 */
	public boolean isWordlistDirExist() {
		return new File(wordlistDir).exists();
	}

	/**
	 * Create directory and copy all word lists from jar to the folder if directory
	 * doesn't exist.
	 */
	public void initializeWordlists() {
		if (!isWordlistDirExist()) {
			copyWordlists();
		}
	}

	// Copy all word lists from jar to the folder where jar locates
	private void copyWordlists() {
		copyWordlistFromJar("Japanese-wordlist-1.csv");
		copyWordlistFromJar("Japanese-wordlist-2.csv");

		// TODO change to read all word lists
	}

	private void copyWordlistFromJar(String jarWordlistName) {
		String jarWordlistDir = "/wordlists";
		InputStream input = this.getClass().getResourceAsStream(jarWordlistDir + "/" + jarWordlistName);
		OutputStream output;

		File file = new File(wordlistDir + File.separator + jarWordlistName);
		file.getParentFile().mkdirs();
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			output = new FileOutputStream(file);

			final int EOF = -1;
			int c;
			try {
				while ((c = input.read()) != EOF) {
					output.write(c);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					input.close();
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// TODO test

	}

	public String getWordlistDir() {
		return wordlistDir;
	}

	public void setWordlistDir(String path) {
		this.wordlistDir = path;
	}

	public String getDataDir() {
		return dataDir;
	}

	public void setDataDir(String dataDir) {
		this.dataDir = dataDir;
	}
}
