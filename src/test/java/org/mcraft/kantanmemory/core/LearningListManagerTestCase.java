package org.mcraft.kantanmemory.core;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mcraft.kantanmemory.core.data.LearningList;
import org.mcraft.kantanmemory.core.data.UserWordData;
import org.mcraft.kantanmemory.core.data.Word;
import org.mcraft.kantanmemory.file.DataInitializer;
import org.mcraft.kantanmemory.file.DataReader;
import org.mcraft.kantanmemory.file.DataWriter;
import org.mcraft.kantanmemory.file.data.UserConfig;

public class LearningListManagerTestCase {
	private LearningListManager learningListManager;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		learningListManager = new LearningListManager();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testGenerateLearningList() {
		DataInitializer dataInitializer = new DataInitializer();

		// Make sure word list files are there
		try {
			File wordlistDirFile = new File(dataInitializer.getWordlistDir());
			if (wordlistDirFile.exists()) {
				for (File file : new File(dataInitializer.getWordlistDir()).listFiles()) {
					file.delete();
				}
				Files.deleteIfExists(Paths.get(dataInitializer.getWordlistDir()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		dataInitializer.initializeWordlists();

		// Make sure user word data file is there
		String userWordDataFilePath = dataInitializer.getDataDir() + File.separator + "user-word-data.yml";
		try {
			Files.deleteIfExists(Paths.get(userWordDataFilePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		dataInitializer.initializeUserWordDataFile();

		// Test
		Word[] wordlist = new DataReader().getWordlist("Japanese-wordlist-1.csv");
		int learnedWordNum = 100;
		UserWordData[] userWordDataArr = new UserWordData[learnedWordNum];
		for (int i = 0; i < learnedWordNum; i++) {
			userWordDataArr[i] = new UserWordData(wordlist[i]);
			userWordDataArr[i].setFamiliarity(i % 5);

			Calendar c = Calendar.getInstance();
			c.setTime(userWordDataArr[i].getLastSeenDate());
			c.add(Calendar.DATE, -(i % 4)); // Time in the past
			userWordDataArr[i].setLastSeenDate(c.getTime());
		}
		new DataWriter().saveUserWordDataList(userWordDataArr);

		new DataInitializer().initializeConfig();
		UserConfig config = new DataReader().getConfig();
		config.setCurrentWordlist("Japanese-wordlist-1.csv");
		config.setWordlistProgress("Japanese-wordlist-1.csv", learnedWordNum);
		new DataWriter().saveConfig(config);
		LearningList learningList = learningListManager.generateLearningList(10, 50);

		assertEquals("Not all 50 words obtained", 50, learningList.getRevisionList().size());

		Set<UserWordData> newWords = new TreeSet<UserWordData>(learningList.getNewWordList());
		Set<UserWordData> revisionWords = new TreeSet<UserWordData>(learningList.getRevisionList());
		Set<UserWordData> knownWords = new TreeSet<UserWordData>(Arrays.asList(new DataReader().getUserWordDataList()));

		knownWords.removeAll(newWords);
		assertEquals("New word list are not all new", 100, knownWords.size());

		knownWords.removeAll(revisionWords);
		assertEquals("Not all revision words from know words", 50, knownWords.size());

		revisionWords.removeAll(newWords);
		assertEquals("some new words are in revision words", 50, revisionWords.size());

	}

	@Test
	public final void testSaveLearningList() {
		DataInitializer dataInitializer = new DataInitializer();

		// Make sure word list file is there
		try {
			File wordlistDirFile = new File(dataInitializer.getWordlistDir());
			if (wordlistDirFile.exists()) {
				for (File file : new File(dataInitializer.getWordlistDir()).listFiles()) {
					file.delete();
				}
				Files.deleteIfExists(Paths.get(dataInitializer.getWordlistDir()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		dataInitializer.initializeWordlists();

		// Make sure user word data file is there
		String userWordDataFilePath = dataInitializer.getDataDir() + File.separator + "user-word-data.yml";
		try {
			Files.deleteIfExists(Paths.get(userWordDataFilePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		dataInitializer.initializeUserWordDataFile();

		// Test
		Word[] wordlist = new DataReader().getWordlist("Japanese-wordlist-1.csv");
		int learnedWordNum = 100;
		UserWordData[] userWordDataArr = new UserWordData[learnedWordNum];
		for (int i = 0; i < learnedWordNum; i++) {
			userWordDataArr[i] = new UserWordData(wordlist[i]);

			Calendar c = Calendar.getInstance();
			c.setTime(userWordDataArr[i].getLastSeenDate());
			c.add(Calendar.DATE, -1); // 1 day ahead
			userWordDataArr[i].setLastSeenDate(c.getTime());
		}
		new DataWriter().saveUserWordDataList(userWordDataArr);

		// Test updating an old seen word
		UserWordData seenWordData = userWordDataArr[0];
		Word word = seenWordData.getWord();
		seenWordData.upgradeFamiliarity();
		int newFamiliarity = seenWordData.getFamiliarity();
		seenWordData.setFamiliarity(newFamiliarity);
		Date currentTime = new Date();
		seenWordData.setLastSeenDate(currentTime);

		// Test adding a new unseen word
		UserWordData unseenWordData = new UserWordData(wordlist[learnedWordNum]);

		learningListManager.saveLearningList(Arrays.asList(new UserWordData[] { seenWordData, unseenWordData }));

		UserWordData[] newUserWordDataArr = new DataReader().getUserWordDataList();

		UserWordData updatedData = null;
		for (int i = 0; i < newUserWordDataArr.length; i++) {
			if (newUserWordDataArr[i].getWord().equals(word)) {
				updatedData = newUserWordDataArr[i];
				break;
			}
		}

		assertNotNull("Updated word not exist", updatedData);

		assertEquals("Wrong total word number", learnedWordNum + 1, newUserWordDataArr.length);

		assertEquals("Familiarity not updated", newFamiliarity, updatedData.getFamiliarity());
		assertEquals("Last seen time not updated", new SimpleDateFormat(UserWordData.DATE_FORMAT).format(currentTime),
				updatedData.getLastSeenTime());

	}

	@Test
	public final void testSaveNewWordList() {
		DataInitializer dataInitializer = new DataInitializer();

		// Make sure word list file is there
		try {
			File wordlistDirFile = new File(dataInitializer.getWordlistDir());
			if (wordlistDirFile.exists()) {
				for (File file : new File(dataInitializer.getWordlistDir()).listFiles()) {
					file.delete();
				}
				Files.deleteIfExists(Paths.get(dataInitializer.getWordlistDir()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		dataInitializer.initializeWordlists();

		// Make sure user word data file is there
		String userWordDataFilePath = dataInitializer.getDataDir() + File.separator + "user-word-data.yml";
		try {
			Files.deleteIfExists(Paths.get(userWordDataFilePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		dataInitializer.initializeUserWordDataFile();

		// Test
		Word[] wordlist = new DataReader().getWordlist("Japanese-wordlist-1.csv");
		int learnedWordNum = 100;
		UserWordData[] userWordDataArr = new UserWordData[learnedWordNum];
		for (int i = 0; i < learnedWordNum; i++) {
			userWordDataArr[i] = new UserWordData(wordlist[i]);
		}
		new DataWriter().saveUserWordDataList(userWordDataArr);

		List<UserWordData> newWordList = Arrays.asList(userWordDataArr).subList(0, 50);
		learningListManager.saveNewWordList(newWordList);
		assertEquals("Duplicates added into data", 100, new DataReader().getUserWordDataList().length);

		int newWordNum = 50;
		UserWordData[] newUserWordDataArr = new UserWordData[newWordNum];
		for (int i = 0; i < newWordNum; i++) {
			newUserWordDataArr[i] = new UserWordData(wordlist[learnedWordNum + i]);
		}

		learningListManager.saveNewWordList(Arrays.asList(newUserWordDataArr));
		assertEquals("Duplicates exist", 150, new DataReader().getUserWordDataList().length);

	}

	@Test
	public final void testSaveRevisionList() {
		DataInitializer dataInitializer = new DataInitializer();

		// Make sure word list file is there
		try {
			File wordlistDirFile = new File(dataInitializer.getWordlistDir());
			if (wordlistDirFile.exists()) {
				for (File file : new File(dataInitializer.getWordlistDir()).listFiles()) {
					file.delete();
				}
				Files.deleteIfExists(Paths.get(dataInitializer.getWordlistDir()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		dataInitializer.initializeWordlists();

		// Make sure user word data file is there
		String userWordDataFilePath = dataInitializer.getDataDir() + File.separator + "user-word-data.yml";
		try {
			Files.deleteIfExists(Paths.get(userWordDataFilePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		dataInitializer.initializeUserWordDataFile();

		// Test
		Word[] wordlist = new DataReader().getWordlist("Japanese-wordlist-1.csv");
		int learnedWordNum = 100;
		UserWordData[] userWordDataArr = new UserWordData[learnedWordNum];
		for (int i = 0; i < learnedWordNum; i++) {
			userWordDataArr[i] = new UserWordData(wordlist[i]);

			Calendar c = Calendar.getInstance();
			c.setTime(userWordDataArr[i].getLastSeenDate());
			c.add(Calendar.DATE, -1); // 1 day ahead
			userWordDataArr[i].setLastSeenDate(c.getTime());
		}
		new DataWriter().saveUserWordDataList(userWordDataArr);

		Word word = userWordDataArr[0].getWord();

		userWordDataArr[0].upgradeFamiliarity();
		int newFamiliarity = userWordDataArr[0].getFamiliarity();
		userWordDataArr[0].setFamiliarity(newFamiliarity);
		Date currentTime = new Date();
		userWordDataArr[0].setLastSeenDate(currentTime);
		learningListManager.saveRevisionList(Arrays.asList(new UserWordData[] { userWordDataArr[0] }));

		UserWordData[] newUserWordDataArr = new DataReader().getUserWordDataList();

		UserWordData updatedData = null;
		for (int i = 0; i < newUserWordDataArr.length; i++) {
			if (newUserWordDataArr[i].getWord().equals(word)) {
				updatedData = newUserWordDataArr[i];
				break;
			}
		}

		assertNotNull("Updated word not exist", updatedData);

		assertEquals("Total word number changed", learnedWordNum, newUserWordDataArr.length);

		assertEquals("Familiarity not updated", newFamiliarity, updatedData.getFamiliarity());
		assertEquals("Last seen time not updated", new SimpleDateFormat(UserWordData.DATE_FORMAT).format(currentTime),
				updatedData.getLastSeenTime());

	}

}
