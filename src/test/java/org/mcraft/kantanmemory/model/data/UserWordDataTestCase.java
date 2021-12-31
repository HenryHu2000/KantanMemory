package org.mcraft.kantanmemory.model.data;

import static org.junit.Assert.*;

import java.util.Date;

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
public class UserWordDataTestCase {
	private UserWordData userWordData;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		userWordData = new UserWordData(new Word(null, null, null, 0));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testLastSeenDate() {
		Date date = new Date(0);
		userWordData.setLastSeenDate(date);
		assertEquals("Not same time", date.toString(), userWordData.getLastSeenDate().toString());

	}

}
