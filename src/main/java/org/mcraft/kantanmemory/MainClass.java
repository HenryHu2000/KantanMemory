package org.mcraft.kantanmemory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;

import org.mcraft.kantanmemory.core.LearningListManager;
import org.mcraft.kantanmemory.core.LearningProcess;
import org.mcraft.kantanmemory.core.data.FamiliarType;
import org.mcraft.kantanmemory.core.data.LearningList;
import org.mcraft.kantanmemory.core.data.LearningWordData;
import org.mcraft.kantanmemory.file.DataInitializer;
import org.mcraft.kantanmemory.file.DataReader;
import org.mcraft.kantanmemory.file.DataWriter;
import org.mcraft.kantanmemory.file.data.UserConfig;
import org.mcraft.kantanmemory.graphics.AppFrame;
import org.mcraft.kantanmemory.graphics.AppPanel;
import org.mcraft.kantanmemory.graphics.PanelState;

/**
 * 
 * @author Henry Hu
 *
 */
public class MainClass {

	public static void main(String[] args) {
		// commandLineUI();
		graphicalUI();
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
		new DataWriter().saveConfig(config);

		// Handle end of word list case
		if (new DataReader().getWordlist(wordlist).length <= config.getWordlistProgress(wordlist)) {
			// End of word list
			System.out.println("Word list already finished!");
		}

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

	public static void graphicalUI() {
		// Initialize GUI
		final AppFrame frame = new AppFrame();

		new DataInitializer().initializeAll();
		UserConfig config = new DataReader().getConfig();

		int newWordNum = 0;
		int revisionWordNum = 0;
		while (true) {
			try {
				String[] learningModes = new String[] { "New word mode", "Review mode" };
				String learningMode = (String) JOptionPane.showInputDialog(frame,
						"<html><font size=+2>" + "New word mode or review mode?" + "</font></html>",
						AppFrame.FRAME_TITLE, JOptionPane.QUESTION_MESSAGE, null, learningModes, learningModes[0]);

				if (learningMode == null) {
					System.exit(0);
				} else if (learningMode.equals(learningModes[0])) {
					// New word mode

					// Choose the word list to learn new words from
					String wordlist = "";
					String[] wordlists = config.getWordlists().toArray(new String[config.getWordlists().size()]);
					wordlist = (String) JOptionPane.showInputDialog(frame,
							"<html><font size=+2>" + "Which word list do you want to learn? (enter file name)"
									+ "</font></html>",
							AppFrame.FRAME_TITLE, JOptionPane.QUESTION_MESSAGE, null, wordlists,
							config.getCurrentWordlist() != null ? config.getCurrentWordlist() : wordlists[0]);
					if (wordlist == null) {
						System.exit(0);
					}
					config.setCurrentWordlist(wordlist);
					new DataWriter().saveConfig(config);

					// Handle end of word list case
					if (new DataReader().getWordlist(wordlist).length <= config.getWordlistProgress(wordlist)) {
						// End of word list
						JOptionPane.showMessageDialog(frame,
								"<html><font size=+2>" + "Word list already finished!" + "</font></html>");
						continue;
					}

					// Prompt to ask for number of new words
					final int defaultNewWordNum = 10;
					Object newWordNumObj = JOptionPane.showInputDialog(frame,
							"<html><font size=+2>" + "How many new words do you want to learn?" + "</font></html>",
							AppFrame.FRAME_TITLE, JOptionPane.QUESTION_MESSAGE, null,
							new Integer[] { 5, 10, 15, 20, 25, 30, 40, 50, 60, 70, 80, 90, 100 }, defaultNewWordNum);
					if (newWordNumObj == null) {
						System.exit(0);
					}
					newWordNum = (int) newWordNumObj;
					revisionWordNum = newWordNum * 5;

				} else if (learningMode.equals(learningModes[1])) {
					// Review mode

					// Prompt to ask for number of revision words
					final int defaultRevisionWordNum = 50;
					Object revisionWordNumObj = JOptionPane.showInputDialog(frame,
							"<html><font size=+2>" + "How many words do you want to review?" + "</font></html>",
							AppFrame.FRAME_TITLE, JOptionPane.QUESTION_MESSAGE, null,
							new Integer[] { 25, 50, 75, 100, 125, 150, 200, 250, 300, 350, 400, 450, 500 },
							defaultRevisionWordNum);
					if (revisionWordNumObj == null) {
						System.exit(0);
					}
					revisionWordNum = (int) revisionWordNumObj;
					newWordNum = 0;
				}
				break;
			} catch (Exception e) {
			}
		}
		final LearningListManager learningListManager = new LearningListManager();
		LearningList learningList = learningListManager.generateLearningList(newWordNum, revisionWordNum);
		final LearningProcess learningProcess = new LearningProcess(learningList);

		// Set listeners for GUI buttons
		frame.getAppPanel().getFamiliarButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				AppPanel appPanel = frame.getAppPanel();
				switch (appPanel.getState()) {
				case KANA_QUESTION:
					appPanel.setFamiliar(true);
					break;
				case WORD_QUESTION:
					if (learningProcess.getCurrentWordData().getFamiliarType() != FamiliarType.UNFAMILIAR) {
						// Familiar and half-familiar words are expected to be familiar at the first try
						appPanel.setFamiliar(false);
					} else {
						appPanel.setFamiliar(true);
					}
					break;
				default:
					break;
				}
				appPanel.setState(PanelState.ANSWER); // Continue to answer state
				appPanel.refreshPanel(learningProcess.getCurrentWordData().getWord());

			}
		});
		frame.getAppPanel().getUnfamiliarButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				AppPanel appPanel = frame.getAppPanel();
				switch (appPanel.getState()) {
				case KANA_QUESTION:
					appPanel.setFamiliar(false);
					appPanel.setState(PanelState.WORD_QUESTION);
					break;
				case WORD_QUESTION:
					appPanel.setFamiliar(false);
					appPanel.setState(PanelState.ANSWER);
					break;
				default:
					break;
				}

				appPanel.refreshPanel(learningProcess.getCurrentWordData().getWord());

			}
		});
		frame.getAppPanel().getContinueButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				AppPanel appPanel = frame.getAppPanel();
				if (appPanel.getState() == PanelState.ANSWER) {
					learningProcess.proceed(appPanel.isFamiliar());
					if (learningProcess.isTerminated()) {
						appPanel.initializePanel();
						appPanel.getWordLabel().setText("All words finished!");
						learningListManager.saveLearningList(learningProcess.getFinishedWordList());
						return;
					}
					if (learningProcess.getCurrentWordData().getFamiliarType() != FamiliarType.UNFAMILIAR) {
						appPanel.setState(PanelState.KANA_QUESTION);
					} else {
						appPanel.setState(PanelState.WORD_QUESTION);
					}
				}
				appPanel.refreshPanel(learningProcess.getCurrentWordData().getWord());
			}
		});

		// Put the first word into GUI
		AppPanel appPanel = frame.getAppPanel();
		appPanel.setState(PanelState.KANA_QUESTION);
		if (!learningProcess.isTerminated()) {
			appPanel.refreshPanel(learningProcess.getCurrentWordData().getWord());
		} else {
			appPanel.initializePanel();
			if (!learningProcess.getAllWords().isEmpty()) {
				appPanel.getWordLabel().setText("All words finished!");
			} else {
				appPanel.getWordLabel().setText("No word exists!");
			}
		}

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				learningListManager.saveLearningList(learningProcess.getAllWords());
				System.exit(0);
			}
		});

	}

}
