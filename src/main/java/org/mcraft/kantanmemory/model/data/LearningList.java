package org.mcraft.kantanmemory.model.data;

import java.util.ArrayList;

/**
 * 
 * @author Henry Hu
 *
 */
public class LearningList {
	private ArrayList<UserWordData> newWordList;
	private ArrayList<UserWordData> revisionList;

	public LearningList(ArrayList<UserWordData> newWordList, ArrayList<UserWordData> revisionList) {
		this.newWordList = newWordList;
		this.revisionList = revisionList;
	}

	public ArrayList<UserWordData> getNewWordList() {
		return newWordList;
	}

	public void setNewWordList(ArrayList<UserWordData> newWordList) {
		this.newWordList = newWordList;
	}

	public ArrayList<UserWordData> getRevisionList() {
		return revisionList;
	}

	public void setRevisionList(ArrayList<UserWordData> revisionList) {
		this.revisionList = revisionList;
	}
}
