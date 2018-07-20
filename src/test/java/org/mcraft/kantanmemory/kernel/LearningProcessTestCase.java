package org.mcraft.kantanmemory.kernel;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Calendar;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mcraft.kantanmemory.file.DataInitializer;
import org.mcraft.kantanmemory.file.DataReader;
import org.mcraft.kantanmemory.file.DataWriter;
import org.mcraft.kantanmemory.file.data.UserConfig;
import org.mcraft.kantanmemory.kernel.data.FamiliarType;
import org.mcraft.kantanmemory.kernel.data.LearningList;
import org.mcraft.kantanmemory.kernel.data.LearningWordData;
import org.mcraft.kantanmemory.kernel.data.UserWordData;
import org.mcraft.kantanmemory.kernel.data.Word;

public class LearningProcessTestCase {
	private LearningProcess learningProcess;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
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

		// Prepare new words to be added
		new DataInitializer().initializeConfig();
		UserConfig config = new DataReader().getConfig();
		config.setCurrentWordlist("Japanese-wordlist-1.csv");
		new DataWriter().saveConfig(config);

		// Prepare old words to be added
		Word[] wordlist = new DataReader().getWordlist("Japanese-wordlist-1.csv");
		int learnedWordNum = 50;
		UserWordData[] userWordDataArr = new UserWordData[learnedWordNum];
		for (int i = 0; i < learnedWordNum; i++) {
			userWordDataArr[i] = new UserWordData(wordlist[i]);

			userWordDataArr[i].setFamiliarity(i % 5);

			Calendar c = Calendar.getInstance();
			c.setTime(userWordDataArr[i].getLastSeenDate());
			c.add(Calendar.DATE, -1); // 1 day ahead
			userWordDataArr[i].setLastSeenDate(c.getTime());
		}
		new DataWriter().saveUserWordDataList(userWordDataArr);

		LearningList learningList = new LearningListManager().generateLearningList(10, 50);

		learningProcess = new LearningProcess(learningList);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testLearningProcess() {

		assertEquals("Not 1 word in queue", 1, learningProcess.getLearningWordQueue().size());
		assertEquals("Wrong number of words waiting to be added to queue", (10 + 50) - 1,
				learningProcess.getNewWordList().size());

	}

	@Test
	public final void testProceed() {
		Word[] initialWords = new Word[60];
		for (int i = 0; i < (60 - 1); i++) {
			initialWords[i] = learningProcess.getNewWordList().get(i).getWord();
		}
		initialWords[initialWords.length - 1] = learningProcess.getLearningWordQueue().peek().getWord();
		Arrays.sort(initialWords);

		for (int i = 0; i < 1000; i++) {

			if (learningProcess.isTerminated()) {
				break;
			}

			if (i % 2 == 0) {
				learningProcess.proceed(false);
			} else {
				learningProcess.proceed(true);
			}
			assertEquals("Wrong total word number", 60, learningProcess.getAllWords().size());
			// System.out.println(i + ": " + new
			// TreeSet<UserWordData>(learningProcess.getAllWords()).size());
		}
		while (!learningProcess.isTerminated()) {
			learningProcess.proceed(true);
			assertEquals("Wrong total word number", 60, learningProcess.getAllWords().size());
		}

		Word[] finishedWords = new Word[60];
		for (int i = 0; i < learningProcess.getFinishedWordList().size(); i++) {
			finishedWords[i] = learningProcess.getFinishedWordList().get(i).getWord();
		}
		Arrays.sort(finishedWords);
		// TODO

		assertArrayEquals("Initial and finished words not the same", initialWords, finishedWords);

	}

	@Test
	public final void testProceedSingleWord() {
		LearningWordData data = null;

		boolean isHalfFamiliarTested = false;
		boolean isUnfamiliarTested = false;

		for (int i = 0; i < 1000; i++) {
			if (learningProcess.isTerminated()) {
				break;
			}

			if (i == 30) {
				data = learningProcess.getCurrentWordData();
			}

			if (learningProcess.getCurrentWordData().equals(data)) {
				switch (data.getFamiliarType()) {
				case FAMILIAR:
				case HALF_FAMILIAR:
					if (!isHalfFamiliarTested) {
						learningProcess.proceed(false);
						assertEquals("Wrong Familiar type", FamiliarType.UNFAMILIAR, data.getFamiliarType());
						isHalfFamiliarTested = true;
					} else {
						learningProcess.proceed(true);
						assertEquals("Wrong Familiar type", FamiliarType.FAMILIAR, data.getFamiliarType());
					}
					break;
				case UNFAMILIAR:
					if (!isUnfamiliarTested) {
						learningProcess.proceed(false);
						assertEquals("Wrong Familiar type", FamiliarType.UNFAMILIAR, data.getFamiliarType());
						isUnfamiliarTested = true;

					} else {
						learningProcess.proceed(true);
						assertEquals("Wrong Familiar type", FamiliarType.HALF_FAMILIAR, data.getFamiliarType());
					}
					break;
				default:
					break;

				}
			} else {
				if (i % 2 == 0) {
					learningProcess.proceed(false);
				} else {
					learningProcess.proceed(true);
				}

			}

		}
		while (!learningProcess.isTerminated()) {
			learningProcess.proceed(true);
		}
		assertTrue("Half-familiar case not tested", isHalfFamiliarTested);
		assertTrue("Unfamiliar case not tested", isUnfamiliarTested);

	}

	@Test
	public final void testGetNumbersOfTypes() {

		Word[] wordlist = new DataReader().getWordlist("Japanese-wordlist-1.csv");
		int learningWordNum = 30;

		// One unfamiliar word already in learning queue
		for (int i = 0; i < learningWordNum - 1; i++) {
			LearningWordData learningWordData = new LearningWordData(new UserWordData(wordlist[i]));
			if (i < 5) {
				learningWordData.setFamiliarType(FamiliarType.FAMILIAR);
			} else if (i >= 20) {
				learningWordData.setFamiliarType(FamiliarType.HALF_FAMILIAR);
			} else {
				learningWordData.setFamiliarType(FamiliarType.UNFAMILIAR);
			}

			learningProcess.getLearningWordQueue().add(learningWordData);
		}

		assertEquals("Incorrect unfamiliar number", 15,
				learningProcess.getNumbersOfTypes().get(FamiliarType.UNFAMILIAR).intValue());
		assertEquals("Incorrect half-familiar number", 10,
				learningProcess.getNumbersOfTypes().get(FamiliarType.HALF_FAMILIAR).intValue());
		assertEquals("Incorrect familiar number", 5,
				learningProcess.getNumbersOfTypes().get(FamiliarType.FAMILIAR).intValue());

	}

	@Test
	public final void testCurrentWordData() {
		UserWordData wordData = new UserWordData(new Word("私", "わたし", "我", 0));
		learningProcess.setCurrentWordData(new LearningWordData(wordData));
		assertEquals("Current word set not equals to current word get", wordData.getWord(),
				learningProcess.getCurrentWordData().getWord());
	}

}
