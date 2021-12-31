package org.mcraft.kantanmemory.view;

import java.awt.GridLayout;
import java.awt.HeadlessException;
import javax.swing.JFrame;

/**
 * @author Henry Hu
 */
public class AppFrame extends JFrame {

  private static final long serialVersionUID = 1L;

  public static final String FRAME_TITLE = "KantanMemory";

  private AppPanel appPanel = new AppPanel();

  public AppFrame() throws HeadlessException {
    super(FRAME_TITLE);

    int width = 1200;
    int height = 1600;
    this.setSize(width, height);

    this.setLocationRelativeTo(null);

    this.setContentPane(appPanel);
    this.setLayout(new GridLayout(0, 1));
    this.setVisible(true);
  }

  public AppPanel getAppPanel() {
    return appPanel;
  }

}
