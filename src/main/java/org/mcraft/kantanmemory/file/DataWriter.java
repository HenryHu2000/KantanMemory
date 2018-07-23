package org.mcraft.kantanmemory.file;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.mcraft.kantanmemory.core.data.UserWordData;
import org.mcraft.kantanmemory.file.data.UserConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class DataWriter {
	private String dataDir;

	public DataWriter() {
		try {
			String jarDir = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI())
					.getParent();
			String dataFolderName = "data";
			setDataDir(jarDir + File.separator + dataFolderName);

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

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
	 * 
	 * @param config
	 * @return true if and only if saving is successful
	 */
	public boolean saveConfig(UserConfig config) {
		if (!isDataDirExist()) {
			new File(dataDir).mkdirs();
		}

		if (isConfigFileExist()) {
			ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

			String configFileName = "user-config.yml";

			File configFile = new File(dataDir + File.separator + configFileName);
			try {
				configFile.createNewFile();
				mapper.writeValue(configFile, config);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

		} else {
			new DataInitializer().initializeConfig();
		}

		return true;
	}

	public boolean isUserWordDataFileExist() {
		String userWordDataFileName = "user-word-data.yml";
		File userWordDataFile = new File(dataDir + File.separator + userWordDataFileName);

		return userWordDataFile.exists();
	}

	/**
	 * 
	 * @param dataList
	 * @return true if and only if saving is successful
	 */
	public boolean saveUserWordDataList(UserWordData[] userWordDataList) {
		Arrays.sort(userWordDataList);

		if (!isDataDirExist()) {
			new File(dataDir).mkdirs();
		}

		if (isUserWordDataFileExist()) {
			ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

			String userWordDataFileName = "user-word-data.yml";

			File userWordDataFile = new File(dataDir + File.separator + userWordDataFileName);

			try {
				userWordDataFile.createNewFile();
				mapper.writeValue(userWordDataFile, userWordDataList);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			new DataInitializer().initializeUserWordDataFile();
		}

		return true;
	}

	public String getDataDir() {
		return dataDir;
	}

	public void setDataDir(String dataDir) {
		this.dataDir = dataDir;
	}

}
