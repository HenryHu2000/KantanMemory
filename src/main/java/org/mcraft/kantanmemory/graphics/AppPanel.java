package org.mcraft.kantanmemory.graphics;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.mcraft.kantanmemory.core.data.Word;

/**
 * 
 * @author Henry Hu
 *
 */
public class AppPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JLabel wordLabel;
	private JLabel translationLabel;

	private JButton knownButton;
	private JButton unknownButton;
	private JButton continueButton;

	private PanelState state;

	private boolean isKnown;

	public AppPanel() {

		wordLabel = new JLabel("", JLabel.CENTER);
		translationLabel = new JLabel("", JLabel.CENTER);

		Font labelFont = new Font(Font.SANS_SERIF, Font.BOLD, 48);
		wordLabel.setFont(labelFont);
		translationLabel.setFont(labelFont);

		String knownButtonText = "I know this word";
		String unknownButtonText = "I don't know";
		String continueButtonText = "Continue";

		knownButton = new JButton(knownButtonText);
		unknownButton = new JButton(unknownButtonText);
		continueButton = new JButton(continueButtonText);

		Font buttonFont = new Font(Font.SANS_SERIF, Font.BOLD, 48);
		knownButton.setFont(buttonFont);
		unknownButton.setFont(buttonFont);
		continueButton.setFont(buttonFont);

		this.setBorder(new EmptyBorder(20, 30, 20, 30));

		this.add(wordLabel);
		this.add(translationLabel);

		this.add(knownButton);
		this.add(unknownButton);
		this.add(continueButton);
	}

	public void refreshPanel(Word word) {
		switch (state) {
		case KANA_QUESTION:
			initializePanel();
			wordLabel.setText(word.getKana());
			knownButton.setEnabled(true);
			unknownButton.setEnabled(true);
			break;
		case WORD_QUESTION:
			initializePanel();
			wordLabel.setText(word.getName() + (word.getAccent() != -1 ? "(" + word.getAccent() + ")" : ""));
			knownButton.setEnabled(true);
			unknownButton.setEnabled(true);
			break;
		case ANSWER:
			initializePanel();
			wordLabel.setText(
					"<html><center>" + word.getKana() + (word.getAccent() != -1 ? "(" + word.getAccent() + ")" : "")
							+ "<br>" + word.getName() + "</center></html>");
			translationLabel.setText("<html><center>" + word.getTranslation() + "</center></html>");
			continueButton.setEnabled(true);
			break;
		default:
			break;
		}
	}

	public void initializePanel() {
		wordLabel.setText("");
		translationLabel.setText("");

		knownButton.setEnabled(false);
		unknownButton.setEnabled(false);
		continueButton.setEnabled(false);
	}

	public JLabel getWordLabel() {
		return wordLabel;
	}

	public void setWordLabel(JLabel wordLabel) {
		this.wordLabel = wordLabel;
	}

	public JLabel getTranslationLabel() {
		return translationLabel;
	}

	public void setTranslationLabel(JLabel translationLabel) {
		this.translationLabel = translationLabel;
	}

	public JButton getContinueButton() {
		return continueButton;
	}

	public void setContinueButton(JButton continueButton) {
		this.continueButton = continueButton;
	}

	public PanelState getState() {
		return state;
	}

	public void setState(PanelState state) {
		this.state = state;
	}

	public JButton getKnownButton() {
		return knownButton;
	}

	public void setKnownButton(JButton knownButton) {
		this.knownButton = knownButton;
	}

	public JButton getUnknownButton() {
		return unknownButton;
	}

	public void setUnknownButton(JButton unknownButton) {
		this.unknownButton = unknownButton;
	}

	public boolean isKnown() {
		return isKnown;
	}

	public void setKnown(boolean isKnown) {
		this.isKnown = isKnown;
	}

}
