package org.mcraft.kantanmemory;

import org.mcraft.kantanmemory.controller.Controller;
import org.mcraft.kantanmemory.file.DataInitializer;
import org.mcraft.kantanmemory.file.DataReader;
import org.mcraft.kantanmemory.file.data.UserConfig;
import org.mcraft.kantanmemory.model.Model;
import org.mcraft.kantanmemory.view.View;

/**
 * @author Henry Hu
 */
public class MainClass {

  public static void main(String[] args) {
    Controller controller = new Controller();
    View view = new View(controller);

    new DataInitializer().initializeAll();
    UserConfig config = new DataReader().getConfig();

    int[] wordNums = view.promptStart(config);
    int newWordNum = wordNums[0];
    int revisionWordNum = wordNums[1];

    Model model = new Model(view, newWordNum, revisionWordNum);
    controller.setModel(model);
  }
}
