package org.mcraft.kantanmemory.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.mcraft.kantanmemory.core.data.UserWordData;
import org.mcraft.kantanmemory.core.data.Word;
import org.mcraft.kantanmemory.file.data.UserConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class DataReader {
	private String wordlistDir;
	private String dataDir;

	public DataReader() {
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

	public Word[] getWordlist(String wordlistName) {
		Reader reader = null;
		File wordlistFile = new File(wordlistDir + File.separator + wordlistName);
		if (wordlistFile.exists()) {
			try {
				reader = new FileReader(wordlistFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		} else {
			return new Word[] {};
		}

		ArrayList<Word> wordlist = new ArrayList<Word>();
		try {
			Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader("Word", "Kana", "Translation", "Accent")
					.parse(reader);

			for (CSVRecord record : records) {
				int accent = -1;
				try {
					try {
						accent = Integer.parseInt(record.get("Accent"));
					} catch (java.lang.IllegalArgumentException e) {

					}
				} catch (java.lang.NumberFormatException e) {

				}

				wordlist.add(new Word(record.get("Word"), record.get("Kana"), record.get("Translation"), accent));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return wordlist.toArray(new Word[wordlist.size()]);
	}

	public Map<String, Word[]> getWordlists() {
		File wordlistFolder = new File(wordlistDir);
		if (wordlistFolder.exists() && wordlistFolder.isDirectory()) {
			TreeMap<String, Word[]> output = new TreeMap<String, Word[]>();
			for (File wordlist : wordlistFolder.listFiles()) {
				output.put(wordlist.getName(), this.getWordlist(wordlist.getName()));
			}
			return output;
		} else {
			return new TreeMap<String, Word[]>();
		}
	}

	public UserConfig getConfig() {
		// TODO
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

		String configFileName = "user-config.yml";
		File configFile = new File(dataDir + File.separator + configFileName);

		if (configFile.exists()) {
			UserConfig config = null;
			try {
				config = mapper.readValue(configFile, UserConfig.class);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return config;

		} else {
			return new UserConfig();
		}

	}

	public UserWordData[] getUserWordDataList() {
		// TODO
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

		String userWordDataFileName = "user-word-data.yml";
		File userWordDataFile = new File(dataDir + File.separator + userWordDataFileName);

		if (userWordDataFile.exists()) {
			UserWordData[] userWordDataArr = null;
			try {

				userWordDataArr = mapper.readValue(userWordDataFile, UserWordData[].class);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Handle the initial user word data
			if (userWordDataArr.length == 1 && userWordDataArr[0] == null) {
				userWordDataArr = new UserWordData[] {};
			}

			return userWordDataArr;

		} else {
			return new UserWordData[] {};
		}

	}

	public String getWordlistDir() {
		return wordlistDir;
	}

	public void setWordlistDir(String wordlistDir) {
		this.wordlistDir = wordlistDir;
	}

	public String getDataDir() {
		return dataDir;
	}

	public void setDataDir(String dataDir) {
		this.dataDir = dataDir;
	}

}
