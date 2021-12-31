package org.mcraft.kantanmemory.view;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import org.mcraft.kantanmemory.controller.Controller;
import org.mcraft.kantanmemory.file.DataReader;
import org.mcraft.kantanmemory.file.DataWriter;
import org.mcraft.kantanmemory.file.data.UserConfig;
import org.mcraft.kantanmemory.model.Model;
import org.mcraft.kantanmemory.model.data.Word;

public class View implements Updatable {

  private final AppFrame frame;

  public View(Controller controller) {
    // Initialize GUI
    frame = new AppFrame();
    AppPanel panel = frame.getAppPanel();
    JButton knownButton = panel.getKnownButton();
    JButton unknownButton = panel.getUnknownButton();
    JButton continueButton = panel.getContinueButton();

    knownButton.addActionListener((e) -> controller.onClickKnown());
    unknownButton.addActionListener((e) -> controller.onClickUnknown());
    continueButton.addActionListener((e) -> controller.onClickContinue());
    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        controller.onClose();
      }
    });
  }

  public int[] promptStart(UserConfig config) {
    int newWordNum = 0;
    int revisionWordNum = 0;
    while (true) {
      try {
        String[] learningModes = new String[]{"New word mode", "Review mode"};
        String learningMode = (String) JOptionPane.showInputDialog(frame,
            "<html><font size=+4>" + "New word mode or review mode?" + "</font></html>",
            AppFrame.FRAME_TITLE, JOptionPane.QUESTION_MESSAGE, null, learningModes,
            learningModes[0]);

        if (learningMode == null) {
          System.exit(0);
        } else if (learningMode.equals(learningModes[0])) {
          // New word mode

          // Choose the word list to learn new words from
          String wordlist = "";
          String[] wordlists = config.getWordlists()
              .toArray(new String[config.getWordlists().size()]);
          wordlist = (String) JOptionPane.showInputDialog(frame,
              "<html><font size=+4>" + "Which word list do you want to learn? (enter file name)"
                  + "</font></html>",
              AppFrame.FRAME_TITLE, JOptionPane.QUESTION_MESSAGE, null, wordlists,
              config.getCurrentWordlist() != null ? config.getCurrentWordlist() : wordlists[0]);
          if (wordlist == null) {
            System.exit(0);
          }
          config.setCurrentWordlist(wordlist);
          new DataWriter().saveConfig(config);

          // Handle end of word list case
          if (new DataReader().getWordlist(wordlist).length <= config
              .getWordlistProgress(wordlist)) {
            // End of word list
            JOptionPane.showMessageDialog(frame,
                "<html><font size=+4>" + "Word list already finished!" + "</font></html>");
            continue;
          }

          // Prompt to ask for number of new words
          final int defaultNewWordNum = 10;
          Object newWordNumObj = JOptionPane.showInputDialog(frame,
              "<html><font size=+4>" + "How many new words do you want to learn?"
                  + "</font></html>",
              AppFrame.FRAME_TITLE, JOptionPane.QUESTION_MESSAGE, null,
              new Integer[]{5, 10, 15, 20, 25, 30, 40, 50, 60, 70, 80, 90, 100}, defaultNewWordNum);
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
              "<html><font size=+4>" + "How many words do you want to review?" + "</font></html>",
              AppFrame.FRAME_TITLE, JOptionPane.QUESTION_MESSAGE, null,
              new Integer[]{25, 50, 75, 100, 125, 150, 200, 250, 300, 350, 400, 450, 500},
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
    return new int[]{newWordNum, revisionWordNum};
  }

  @Override
  public void update(Model model) {
    AppPanel panel = frame.getAppPanel();
    JLabel wordLabel = panel.getWordLabel();
    JLabel translationLabel = panel.getTranslationLabel();
    JButton knownButton = panel.getKnownButton();
    JButton unknownButton = panel.getUnknownButton();
    JButton continueButton = panel.getContinueButton();

    if (model.isTerminated()) {
      initializePanel();
      if (!model.isLearningListEmpty()) {
        panel.getWordLabel().setText("All words finished!");
      } else {
        panel.getWordLabel().setText("No word exists!");
      }
      return;
    }

    Word word = model.getWord();
    switch (model.getState()) {
      case KANA_QUESTION:
        initializePanel();
        wordLabel.setText(word.getKana());
        knownButton.setEnabled(true);
        unknownButton.setEnabled(true);
        break;
      case WORD_QUESTION:
        initializePanel();
        wordLabel
            .setText(word.getName() + (word.getAccent() != -1 ? "(" + word.getAccent() + ")" : ""));
        knownButton.setEnabled(true);
        unknownButton.setEnabled(true);
        break;
      case ANSWER:
        initializePanel();
        wordLabel.setText(
            "<html><center>" + word.getKana() + (word.getAccent() != -1 ? "(" + word.getAccent()
                + ")" : "")
                + "<br>" + word.getName() + "</center></html>");
        translationLabel.setText("<html><center>" + word.getTranslation() + "</center></html>");
        continueButton.setEnabled(true);
        break;
      default:
        break;
    }
  }

  private void initializePanel() {
    AppPanel panel = frame.getAppPanel();
    JLabel wordLabel = panel.getWordLabel();
    JLabel translationLabel = panel.getTranslationLabel();
    JButton knownButton = panel.getKnownButton();
    JButton unknownButton = panel.getUnknownButton();
    JButton continueButton = panel.getContinueButton();

    wordLabel.setText("");
    translationLabel.setText("");

    knownButton.setEnabled(false);
    unknownButton.setEnabled(false);
    continueButton.setEnabled(false);
  }

}
