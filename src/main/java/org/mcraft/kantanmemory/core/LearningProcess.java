package org.mcraft.kantanmemory.core;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.mcraft.kantanmemory.core.data.KnownType;
import org.mcraft.kantanmemory.core.data.LearningList;
import org.mcraft.kantanmemory.core.data.LearningWordData;
import org.mcraft.kantanmemory.core.data.UserWordData;

/**
 * 
 * @author Henry Hu
 *
 */
public class LearningProcess {
	private LinkedList<UserWordData> newWordList = new LinkedList<UserWordData>();
	private LinkedList<UserWordData> finishedWordList = new LinkedList<UserWordData>();

	private Deque<LearningWordData> learningWordQueue = new ArrayDeque<LearningWordData>();
	private int maximumWordQueueLength = 20;

	private boolean isTerminated;

	public LearningProcess(LearningList learningList) {

		newWordList.addAll(learningList.getRevisionList());
		newWordList.addAll(learningList.getNewWordList()); // new words at the back

		if (!getAllWords().isEmpty()) {
			isTerminated = false;
			addNewWordToQueue(); // Add the first word to queue
		} else {
			isTerminated = true; // Handle the empty new word list case
		}
	}

	public void proceed(boolean isKnown) {
		if (isTerminated) {
			return;
		}

		boolean isCurrentExist = handleCurrent(isKnown);
		if (!isCurrentExist) {
			addNewWordToQueue();
		}

		boolean isNextExist = handleNext();
		if (!isNextExist) {
			this.isTerminated = true;
		}
	}

	/**
	 * 
	 * @param isFamiliar
	 * @return false if there is no current word
	 */
	private boolean handleCurrent(boolean isFamiliar) {

		LearningWordData currentWordData = getCurrentWordData();

		if (currentWordData == null) {
			return false;
		}

		currentWordData.updateLastSeenTime();

		if (isFamiliar) {
			switch (currentWordData.getKnownType()) {
			case KNOWN:
			case HALF_KNOWN:
				currentWordData.setKnownType(KnownType.KNOWN);
				currentWordData.upgradeFamiliarity();
				moveWordToFinished();
				break;
			case UNKNOWN:
				currentWordData.setKnownType(KnownType.HALF_KNOWN);
				moveWordToQueueBack();
				break;
			default:
				break;

			}
		} else {
			currentWordData.setKnownType(KnownType.UNKNOWN);
			currentWordData.downgradeFamiliarity();
			moveWordToQueueBack();
		}
		return true;
	}

	private void moveWordToFinished() {
		finishedWordList.add(learningWordQueue.remove());
	}

	private void moveWordToQueueBack() {
		learningWordQueue.add(learningWordQueue.remove());
	}

	/**
	 * 
	 * @return false if there is no next word
	 */
	private boolean handleNext() {
		if (newWordList.isEmpty() && learningWordQueue.isEmpty()) {
			return false;
		}

		if ((!newWordList.isEmpty()) && learningWordQueue.size() < maximumWordQueueLength) {
			addNewWordToQueue();
		}

		return true;
	}

	/**
	 * Add word from new word list to front of the queue.
	 */
	private boolean addNewWordToQueue() {
		if (!newWordList.isEmpty()) {
			learningWordQueue.addFirst(new LearningWordData(newWordList.pop()));
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Add word to front of the queue.
	 */
	private void addNewWordToQueue(LearningWordData wordData) {
		learningWordQueue.addFirst(wordData);
	}

	public EnumMap<KnownType, Integer> getNumbersOfTypes() {
		Map<KnownType, Integer> numOfTypesMap = new HashMap<KnownType, Integer>();
		numOfTypesMap.put(KnownType.UNKNOWN, 0);
		numOfTypesMap.put(KnownType.HALF_KNOWN, 0);
		numOfTypesMap.put(KnownType.KNOWN, 0);

		for (LearningWordData wordData : learningWordQueue) {
			switch (wordData.getKnownType()) {
			case KNOWN:
				numOfTypesMap.put(KnownType.KNOWN, numOfTypesMap.get(KnownType.KNOWN) + 1);
				break;
			case HALF_KNOWN:
				numOfTypesMap.put(KnownType.HALF_KNOWN, numOfTypesMap.get(KnownType.HALF_KNOWN) + 1);
				break;
			case UNKNOWN:
				numOfTypesMap.put(KnownType.UNKNOWN, numOfTypesMap.get(KnownType.UNKNOWN) + 1);
				break;
			default:
				break;
			}
		}
		return new EnumMap<KnownType, Integer>(numOfTypesMap);
	}

	public LinkedList<UserWordData> getNewWordList() {
		return newWordList;
	}

	public void setNewWordList(LinkedList<UserWordData> newWordList) {
		this.newWordList = newWordList;
	}

	public LinkedList<UserWordData> getFinishedWordList() {
		return finishedWordList;
	}

	public void setFinishedWordList(LinkedList<UserWordData> finishedWordList) {
		this.finishedWordList = finishedWordList;
	}

	public Deque<LearningWordData> getLearningWordQueue() {
		return learningWordQueue;
	}

	public void setLearningWordQueue(Deque<LearningWordData> learningWordQueue) {
		this.learningWordQueue = learningWordQueue;
	}

	public List<UserWordData> getAllWords() {
		List<UserWordData> list = new ArrayList<UserWordData>();
		list.addAll(newWordList);
		list.addAll(learningWordQueue);
		list.addAll(finishedWordList);
		return list;
	}

	public int getMaximumWordQueueLength() {
		return maximumWordQueueLength;
	}

	public void setMaximumWordQueueLength(int maximumWordQueueLength) {
		this.maximumWordQueueLength = maximumWordQueueLength;
	}

	public LearningWordData getCurrentWordData() {
		return learningWordQueue.peek();
	}

	public void setCurrentWordData(LearningWordData currentWordData) {
		addNewWordToQueue(currentWordData);
	}

	public boolean isTerminated() {
		return isTerminated;
	}

	public void setTerminated(boolean isTerminated) {
		this.isTerminated = isTerminated;
	}

}
