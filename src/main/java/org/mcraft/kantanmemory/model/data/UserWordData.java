package org.mcraft.kantanmemory.model.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data for each word user learns.
 * 
 * @author Henry Hu
 *
 */
public class UserWordData implements Comparable<UserWordData> {
	protected Word word;

	protected int familiarity;

	@JsonProperty("last_seen_time")
	protected String lastSeenTime;
	@JsonIgnore
	protected static final int MIN_FAMILIARITY = 0;
	@JsonIgnore
	protected static final int MAX_FAMILIARITY = 5;
	@JsonIgnore
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	// For generating YAML file
	protected UserWordData() {

	}

	public UserWordData(Word word) {
		setWord(word);
		setFamiliarity(0);
		setLastSeenDate(new Date());
	}

	public UserWordData(Word word, int familiarity, Date lastSeenDate) {
		setWord(word);
		setFamiliarity(familiarity);
		setLastSeenDate(lastSeenDate);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof UserWordData) {
			return this.getWord().equals(((UserWordData) o).getWord());
		}
		return false;
	}

	@Override
	public int compareTo(UserWordData anotherUserWordData) {
		if (this.equals(anotherUserWordData)) {
			return 0;
		} else if (this.familiarity > anotherUserWordData.getFamiliarity()) {
			return 1;
		} else if (this.familiarity < anotherUserWordData.getFamiliarity()) {
			return -1;
		} else if (this.getLastSeenDate().compareTo(anotherUserWordData.getLastSeenDate()) != 0) {
			return this.getLastSeenDate().compareTo(anotherUserWordData.getLastSeenDate());
		}

		return this.word.compareTo(anotherUserWordData.getWord());
	}

	@Override
	public String toString() {
		return word.getName() + " " + familiarity + " " + lastSeenTime;
	}

	public Word getWord() {
		return word;
	}

	public void setWord(Word word) {
		this.word = word;
	}

	@JsonIgnore
	public Date getLastSeenDate() {
		Date date = null;
		try {
			date = new SimpleDateFormat(DATE_FORMAT).parse(this.lastSeenTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	@JsonIgnore
	public void setLastSeenDate(Date lastSeenDate) {
		this.lastSeenTime = new SimpleDateFormat(DATE_FORMAT).format(lastSeenDate);
	}

	/**
	 * Set last seen time to current time
	 */
	public void updateLastSeenTime() {
		setLastSeenDate(new Date());
	}

	public String getLastSeenTime() {
		return lastSeenTime;
	}

	public void setLastSeenTime(String lastSeenTime) {
		this.lastSeenTime = lastSeenTime;
	}

	public int getFamiliarity() {
		return familiarity;
	}

	public void setFamiliarity(int familiarity) {
		if (familiarity < MIN_FAMILIARITY) {
			this.familiarity = MIN_FAMILIARITY;
		} else if (familiarity > MAX_FAMILIARITY) {
			this.familiarity = MAX_FAMILIARITY;
		} else {
			this.familiarity = familiarity;
		}
	}

	/**
	 * Increase the familiarity by one. Maximum familiarity is 5.
	 */
	public void upgradeFamiliarity() {
		if (familiarity < MAX_FAMILIARITY) {
			familiarity++;
		}
	}

	/**
	 * Decrease the familiarity by one. Minimum familiarity is 0;
	 */
	public void downgradeFamiliarity() {
		if (familiarity > MIN_FAMILIARITY) {
			familiarity--;
		}
	}

}
