package org.mcraft.kantanmemory.file.data;

import java.util.Set;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserConfig {
	@JsonProperty("progresses_of_wordlists")
	private TreeMap<String, Integer> progressesOfWordlists; // Index of the last word learned

	@JsonProperty("current_wordlist")
	private String currentWordlist;

	public UserConfig() {
		progressesOfWordlists = new TreeMap<String, Integer>();
	}

	@JsonIgnore
	public Set<String> getWordlists() {
		return progressesOfWordlists.keySet();
	}

	public int getWordlistProgress(String wordlist) {
		return progressesOfWordlists.get(wordlist);
	}

	public void setWordlistProgress(String wordlist, int progress) {
		progressesOfWordlists.put(wordlist, progress);
	}

	public String getCurrentWordlist() {
		return currentWordlist;
	}

	public void setCurrentWordlist(String currentWordlist) {
		this.currentWordlist = currentWordlist;
	}

}
