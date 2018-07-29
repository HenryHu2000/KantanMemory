package org.mcraft.kantanmemory.file;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author Henry Hu
 *
 */
public class DataInitializerTestCase {
	private DataInitializer dataInitializer;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		dataInitializer = new DataInitializer();

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testInitializer() {
		assertTrue("jar directory exists", new File(dataInitializer.getWordlistDir()).getParentFile().exists());
	}

	// @Test
	// public final void testInitializeAll() {
	// fail("Not yet implemented"); // TODO
	// }

	@Test
	public final void testInitializeConfig() {

		String configFilePath = dataInitializer.getDataDir() + File.separator + "user-config.yml";
		try {
			Files.deleteIfExists(Paths.get(configFilePath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		dataInitializer.initializeConfig();
		assertTrue("File not created", new File(configFilePath).exists());

	}

	@Test
	public final void testInitializeUserWordDataFile() {
		String userWordDataFilePath = dataInitializer.getDataDir() + File.separator + "user-word-data.yml";
		try {
			Files.deleteIfExists(Paths.get(userWordDataFilePath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		dataInitializer.initializeUserWordDataFile();
		assertTrue("File not created", new File(userWordDataFilePath).exists());
	}

	@Test
	public final void testInitializeWordlists() {
		// Should create directory and copy word lists if directory doesn't exist
		try {
			File wordlistDirFile = new File(dataInitializer.getWordlistDir());
			if (wordlistDirFile.exists()) {
				for (File file : new File(dataInitializer.getWordlistDir()).listFiles()) {
					file.delete();
				}
				Files.deleteIfExists(Paths.get(dataInitializer.getWordlistDir()));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dataInitializer.initializeWordlists();

		assertTrue("directory not created", new File(dataInitializer.getWordlistDir()).exists());
		assertTrue("wordlist 1 not copied",
				new File(dataInitializer.getWordlistDir() + File.separator + "Japanese-wordlist-1.csv").exists());
		assertTrue("wordlist 2 not copied",
				new File(dataInitializer.getWordlistDir() + File.separator + "Japanese-wordlist-2.csv").exists());

		// Shouldn't copy word lists if directory does exist
		try {
			Files.deleteIfExists(
					Paths.get(dataInitializer.getWordlistDir() + File.separator + "Japanese-wordlist-1.csv"));
			Files.deleteIfExists(
					Paths.get(dataInitializer.getWordlistDir() + File.separator + "Japanese-wordlist-2.csv"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assertFalse("wordlist 1 not copied",
				new File(dataInitializer.getWordlistDir() + File.separator + "Japanese-wordlist-1.csv").exists());
		assertFalse("wordlist 2 not copied",
				new File(dataInitializer.getWordlistDir() + File.separator + "Japanese-wordlist-2.csv").exists());

	}

	// @Test
	// public final void testIsWordlistDirExist() {
	//
	// fail("Not yet implemented"); // TODO
	// }

}
