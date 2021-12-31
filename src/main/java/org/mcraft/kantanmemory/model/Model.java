package org.mcraft.kantanmemory.model;

import org.mcraft.kantanmemory.model.data.KnownType;
import org.mcraft.kantanmemory.model.data.LearningList;
import org.mcraft.kantanmemory.model.data.LearningState;
import org.mcraft.kantanmemory.model.data.Word;
import org.mcraft.kantanmemory.view.Updatable;

public class Model {

  private final Updatable view;

  private final LearningListManager learningListManager;
  private final LearningProcess learningProcess;

  private LearningState state;
  private boolean isKnown;

  public Model(Updatable view, int newWordNum, int revisionWordNum) {
    this.view = view;

    learningListManager = new LearningListManager();
    LearningList learningList = learningListManager
        .generateLearningList(newWordNum, revisionWordNum);
    learningProcess = new LearningProcess(learningList);

    // Put the first word into GUI
    state = LearningState.KANA_QUESTION;
    view.update(this);
  }

  public void proceedKnown() {
    switch (state) {
      case KANA_QUESTION:
        isKnown = true;
        break;
      case WORD_QUESTION:
        if (learningProcess.getCurrentWordData().getKnownType() != KnownType.UNKNOWN) {
          // Known and half-known words are expected to be known at the first try
          isKnown = false;
        } else {
          isKnown = true;
        }
        break;
      default:
        break;
    }
    state = LearningState.ANSWER; // Continue to answer state

    view.update(this);
  }

  public void proceedUnknown() {
    switch (state) {
      case KANA_QUESTION:
        isKnown = false;
        state = LearningState.WORD_QUESTION;
        break;
      case WORD_QUESTION:
        isKnown = false;
        state = LearningState.ANSWER;
        break;
      default:
        break;
    }

    view.update(this);
  }

  public void proceedContinue() {
    if (state == LearningState.ANSWER) {
      learningProcess.proceed(isKnown);
      if (learningProcess.isTerminated()) {
        learningListManager.saveLearningList(learningProcess.getFinishedWordList());
      } else {
        if (learningProcess.getCurrentWordData().getKnownType() != KnownType.UNKNOWN) {
          state = LearningState.KANA_QUESTION;
        } else {
          state = LearningState.WORD_QUESTION;
        }
      }
    }

    view.update(this);
  }

  public void exit() {
    learningListManager.saveLearningList(learningProcess.getAllWords());
    System.exit(0);
  }

  public Word getWord() {
    return learningProcess.getCurrentWordData().getWord();
  }

  public LearningState getState() {
    return state;
  }

  public boolean isTerminated() {
    return learningProcess.isTerminated();
  }

  public boolean isLearningListEmpty() {
    return learningProcess.getAllWords().isEmpty();
  }
}
