package org.mcraft.kantanmemory.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.mcraft.kantanmemory.core.data.LearningList;
import org.mcraft.kantanmemory.core.data.UserWordData;
import org.mcraft.kantanmemory.core.data.Word;
import org.mcraft.kantanmemory.file.DataReader;
import org.mcraft.kantanmemory.file.DataWriter;
import org.mcraft.kantanmemory.file.data.UserConfig;

public class LearningListManager {
	public LearningList generateLearningList(int newWordListSize, int revisionListSize) {
		LearningList learningList = new LearningList(generateNewWordList(newWordListSize),
				generateRevisionList(revisionListSize));

		return learningList;
	}

	public ArrayList<UserWordData> generateNewWordList(int newWordListSize) {
		// generate new word list
		ArrayList<UserWordData> newWordList = new ArrayList<UserWordData>();

		// Handle zero new word case
		if (newWordListSize == 0) {
			return newWordList;
		}

		UserConfig config = new DataReader().getConfig();
		String currentWordlistName = config.getCurrentWordlist();
		Word[] currentWordlist = new DataReader().getWordlist(currentWordlistName);

		int wordlistProgress = config.getWordlistProgress(currentWordlistName);

		if (currentWordlist.length < wordlistProgress + newWordListSize) {
			// End of word list
			newWordListSize = currentWordlist.length - wordlistProgress;
		}

		for (int i = 0; i < newWordListSize; i++) {
			newWordList.add(new UserWordData(currentWordlist[wordlistProgress + i]));
		}
		// Update word list progress
		config.setWordlistProgress(currentWordlistName, wordlistProgress + newWordListSize);
		new DataWriter().saveConfig(config);
		return newWordList;
	}

	public ArrayList<UserWordData> generateRevisionList(int revisionListSize) {

		// Handle zero revision word case
		if (revisionListSize == 0) {
			return new ArrayList<UserWordData>();
		}

		UserWordData[] wordDataArr = new DataReader().getUserWordDataList();
		Arrays.sort(wordDataArr);

		ArrayList<UserWordData> revisionList;
		if (wordDataArr.length <= revisionListSize) {
			// Review all words if there are fewer than wanted
			revisionList = new ArrayList<UserWordData>(Arrays.asList(wordDataArr));
			Collections.reverse(revisionList); // Most seen words at the front
			return revisionList;
		} else {
			revisionList = new ArrayList<UserWordData>(Arrays.asList(wordDataArr).subList(0, revisionListSize));
			Collections.reverse(revisionList); // Most seen words at the front
			return revisionList;
		}

	}

	/**
	 * 
	 * @param learningList array of both new and revision words
	 * @return whether successful
	 */
	public boolean saveLearningList(List<UserWordData> learningList) {
		Set<UserWordData> userWordDataSet = new TreeSet<UserWordData>();

		// Replace old words with newly reviewed words
		userWordDataSet.addAll(learningList);

		UserWordData[] wordDataArr = new DataReader().getUserWordDataList();

		List<UserWordData> learnedList = Arrays.asList(wordDataArr);
		for (UserWordData learnedUserWordData : learnedList) {
			boolean isDuplicate = false;
			for (UserWordData learningUserWordData : learningList) {
				if (learnedUserWordData.getWord().equals(learningUserWordData.getWord())) {
					isDuplicate = true;
				}
			}
			if (!isDuplicate) {
				userWordDataSet.add(learnedUserWordData);
			}

		}

		return new DataWriter().saveUserWordDataList(userWordDataSet.toArray(new UserWordData[userWordDataSet.size()]));
	}

	public boolean saveLearningList(LearningList learningList) {
		boolean isNewWordSaved = saveNewWordList(learningList.getNewWordList());
		boolean isRevisionSaved = saveRevisionList(learningList.getRevisionList());
		return isNewWordSaved && isRevisionSaved;
	}

	public boolean saveNewWordList(List<UserWordData> newWordList) {
		Set<UserWordData> learnedWords = new TreeSet<UserWordData>(
				Arrays.asList(new DataReader().getUserWordDataList()));
		learnedWords.addAll(newWordList);
		return new DataWriter().saveUserWordDataList(learnedWords.toArray(new UserWordData[learnedWords.size()]));

	}

	/**
	 * Update the old words in user word data file.
	 * 
	 * @param revisionList
	 * @return whether successful
	 */
	public boolean saveRevisionList(List<UserWordData> revisionList) {

		Set<UserWordData> userWordDataSet = new TreeSet<UserWordData>();
		// Replace old words with newly reviewed words
		userWordDataSet.addAll(revisionList);
		List<UserWordData> learnedWords = Arrays.asList(new DataReader().getUserWordDataList());
		userWordDataSet.addAll(learnedWords);

		return new DataWriter().saveUserWordDataList(userWordDataSet.toArray(new UserWordData[userWordDataSet.size()]));
	}

}
