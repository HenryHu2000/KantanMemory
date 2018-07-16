package org.mcraft.kantanmemory;

import org.mcraft.kantanmemory.file.DataInitializer;
import org.mcraft.kantanmemory.file.DataReader;
import org.mcraft.kantanmemory.file.DataWriter;
import org.mcraft.kantanmemory.file.data.UserConfig;
import org.mcraft.kantanmemory.kernel.LearningListManager;
import org.mcraft.kantanmemory.kernel.LearningProcess;
import org.mcraft.kantanmemory.kernel.data.FamiliarType;
import org.mcraft.kantanmemory.kernel.data.LearningList;
import org.mcraft.kantanmemory.kernel.data.LearningWordData;

public class MainClass {

	public static void main(String[] args) {
		commandLineUI();
	}

	public static void commandLineUI() {

		new DataInitializer().initializeAll();
		UserConfig config = new DataReader().getConfig();
		String wordlist = System.console()
				.readLine("Please enter the name of the word list file you want to learn (blank for default): ");
		if (wordlist.isEmpty()) {
			wordlist = config.getCurrentWordlist() == null ? "Japanese-wordlist-1.csv" : config.getCurrentWordlist();
		} else if (!config.getWordlists().contains(wordlist)) {
			System.out.println("Word list not exist. Use default word list instead.");
			wordlist = config.getCurrentWordlist() == null ? "Japanese-wordlist-1.csv" : config.getCurrentWordlist();
		}
		config.setCurrentWordlist(wordlist);

		// Handle end of word list case
		if (new DataReader().getWordlist(wordlist).length <= config.getWordlistProgress(wordlist)) {
			// End of word list
			System.out.println("Word list already finished!");
		}

		new DataWriter().saveConfig(config);

		LearningListManager learningListManager = new LearningListManager();

		int newWordNum = 0;
		while (true) {
			try {
				newWordNum = Integer.parseInt(
						System.console().readLine("Please enter the number of new words you want to learn: "));
				break;
			} catch (Exception e) {
			}
		}
		LearningList learningList = learningListManager.generateLearningList(newWordNum, newWordNum * 5);
		learningListManager.saveLearningList(learningList);
		LearningProcess learningProcess = new LearningProcess(learningList);
		System.out.println();

		while (!learningProcess.isTerminated()) {
			LearningWordData wordData = learningProcess.getCurrentWordData();
			boolean isFamiliar = false;
			switch (wordData.getFamiliarType()) {
			case FAMILIAR:
			case HALF_FAMILIAR:
				System.out.println(wordData.getWord().getKana());
				System.out.println("Do you know this word?");
				isFamiliar = readYesOrNo();
				if (!isFamiliar) {
					System.out.println(wordData.getWord().getName());
					System.out.println("Do you know this word now?");
					readYesOrNo();
				}
				break;
			case UNFAMILIAR:
				System.out.println(wordData.getWord().getName());
				System.out.println("Do you know this word?");
				isFamiliar = readYesOrNo();
				break;
			}
			System.out.println();

			System.out.println(wordData.getWord().getKana()
					+ (wordData.getWord().getAccent() != -1 ? "(" + wordData.getWord().getAccent() + ")" : ""));
			System.out.println(wordData.getWord().getName());
			System.out.println(wordData.getWord().getTranslation());

			learningProcess.proceed(isFamiliar);

			// Handle program closed when unfinished case
			if (wordData.getFamiliarType() == FamiliarType.FAMILIAR) {
				learningListManager.saveLearningList(learningProcess.getFinishedWordList());
				learningProcess.getFinishedWordList().clear();
			}

			System.console().readLine("(Press enter to continue)");
			System.out.println("\n----------------\n");
		}

		System.out.println("All words finished!");
		learningListManager.saveLearningList(learningProcess.getFinishedWordList());

	}

	public static boolean readYesOrNo() {
		while (true) {
			String readLine = System.console().readLine("(Y/N): ");
			if (!readLine.isEmpty()) {
				switch (readLine.toLowerCase().toCharArray()[0]) {
				case 'y':
					return true;
				case 'n':
					return false;
				}
			}
		}
	}
}
