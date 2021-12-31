package org.mcraft.kantanmemory.file;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mcraft.kantanmemory.model.data.UserWordData;
import org.mcraft.kantanmemory.model.data.Word;
import org.mcraft.kantanmemory.file.data.UserConfig;

/**
 * 
 * @author Henry Hu
 *
 */
public class DataReaderTestCase {
	private DataReader dataReader;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		dataReader = new DataReader();
	}

	@After
	public void tearDown() throws Exception {
	}

	// @Test
	// public final void testDataReader() {
	//
	// }

	@Test
	public final void testGetWordlist() {
		DataInitializer dataInitializer = new DataInitializer();
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

		Word[] words = dataReader.getWordlist("Japanese-wordlist-1.csv");
		// assertEquals("First word name different", "﻿私", words[0].getName());
		assertEquals("First word kana different", "わたし", words[0].getKana());
		assertEquals("First word translation different", "我", words[0].getTranslation());
		assertEquals("First word accent different", 0, words[0].getAccent());

		assertEquals("Last word name different", "いくら［～ても］", words[words.length - 1].getName());
		assertEquals("Last word kana different", "いくら［～ても］", words[words.length - 1].getKana());
		assertEquals("Last word translation different", "无论～也，怎么～也", words[words.length - 1].getTranslation());
		assertEquals("Last word accent different", -1, words[words.length - 1].getAccent());

	}

	@Test
	public final void testGetWordlists() {
		DataInitializer dataInitializer = new DataInitializer();
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

		Map<String, Word[]> wordlists = dataReader.getWordlists();

		assertTrue("Word list Japanese-wordlist-1.csv not obtained", wordlists.containsKey("Japanese-wordlist-1.csv"));
		assertTrue("Word list Japanese-wordlist-2.csv not obtained", wordlists.containsKey("Japanese-wordlist-2.csv"));
	}

	@Test
	public final void testGetConfig() {
		new DataInitializer().initializeConfig();
		UserConfig config = dataReader.getConfig();

		assertNotNull("Null config obtained", config);
	}

	@Test
	public final void testGetUserWordDataList() {
		new DataInitializer().initializeConfig();
		UserWordData[] dataList = dataReader.getUserWordDataList();

		assertNotNull("Null data list obtained", dataList);
	}

}
