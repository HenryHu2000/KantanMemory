package org.mcraft.kantanmemory.controller;

import org.mcraft.kantanmemory.model.Model;

public class Controller {

  private Model model;

  public void onClickKnown() {
    if (model != null) {
      model.proceedKnown();
    }
  }

  public void onClickUnknown() {
    if (model != null) {
      model.proceedUnknown();
    }
  }

  public void onClickContinue() {
    if (model != null) {
      model.proceedContinue();
    }
  }

  public void onClose() {
    if (model != null) {
      model.exit();
    }
  }

  public void setModel(Model model) {
    this.model = model;
  }
}
