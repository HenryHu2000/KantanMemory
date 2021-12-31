package org.mcraft.kantanmemory.view;

import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * @author Henry Hu
 */
public class AppPanel extends JPanel {

  private static final long serialVersionUID = 1L;

  private JLabel wordLabel;
  private JLabel translationLabel;

  private JButton knownButton;
  private JButton unknownButton;
  private JButton continueButton;

  public AppPanel() {

    wordLabel = new JLabel("", JLabel.CENTER);
    translationLabel = new JLabel("", JLabel.CENTER);

    Font labelFont = new Font(Font.SANS_SERIF, Font.BOLD, 96);
    wordLabel.setFont(labelFont);
    translationLabel.setFont(labelFont);

    String knownButtonText = "I know this word";
    String unknownButtonText = "I don't know";
    String continueButtonText = "Continue";

    knownButton = new JButton(knownButtonText);
    unknownButton = new JButton(unknownButtonText);
    continueButton = new JButton(continueButtonText);

    Font buttonFont = new Font(Font.SANS_SERIF, Font.BOLD, 96);
    knownButton.setFont(buttonFont);
    unknownButton.setFont(buttonFont);
    continueButton.setFont(buttonFont);

    this.setBorder(new EmptyBorder(40, 60, 40, 60));

    this.add(wordLabel);
    this.add(translationLabel);

    this.add(knownButton);
    this.add(unknownButton);
    this.add(continueButton);
  }

  public JLabel getWordLabel() {
    return wordLabel;
  }

  public JLabel getTranslationLabel() {
    return translationLabel;
  }

  public JButton getKnownButton() {
    return knownButton;
  }

  public JButton getUnknownButton() {
    return unknownButton;
  }

  public JButton getContinueButton() {
    return continueButton;
  }

}
