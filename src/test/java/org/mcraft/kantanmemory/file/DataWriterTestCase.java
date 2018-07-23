package org.mcraft.kantanmemory.file;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mcraft.kantanmemory.core.data.UserWordData;
import org.mcraft.kantanmemory.core.data.Word;
import org.mcraft.kantanmemory.file.DataInitializer;
import org.mcraft.kantanmemory.file.data.UserConfig;

public class DataWriterTestCase {
	private DataWriter dataWriter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		dataWriter = new DataWriter();
	}

	@After
	public void tearDown() throws Exception {
	}

	// @Test
	// public final void testDataWriter() {
	//
	// }

	@Test
	public final void testSaveUserWordDataList() {
		new DataInitializer().initializeUserWordDataFile();
		UserWordData[] dataListToSave = new UserWordData[] { new UserWordData(new Word("私", "わたし", "我", 0)) };
		dataWriter.saveUserWordDataList(dataListToSave);

		Word word = new DataReader().getUserWordDataList()[0].getWord();
		assertEquals("Not same word", "私", word.getName());
		assertEquals("Not same kana", "わたし", word.getKana());
		assertEquals("Not same translation", "我", word.getTranslation());
		assertEquals("Not same accent", 0, word.getAccent());
	}

	@Test
	public final void testSaveConfig() {
		new DataInitializer().initializeConfig();

		UserConfig configToSave = new UserConfig();
		configToSave.setWordlistProgress("Japanese-wordlist-1.csv", 0);
		dataWriter.saveConfig(configToSave);

		UserConfig config = new DataReader().getConfig();

		assertEquals("Not same word list name", "Japanese-wordlist-1.csv",
				config.getWordlists().toArray(new String[1])[0]);
		assertEquals("Not same progress number", 0, config.getWordlistProgress("Japanese-wordlist-1.csv"));

	}

}
