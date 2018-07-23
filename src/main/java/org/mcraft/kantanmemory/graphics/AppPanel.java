package org.mcraft.kantanmemory.graphics;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.mcraft.kantanmemory.core.data.Word;

public class AppPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JLabel wordLabel;
	private JLabel translationLabel;

	private JButton familiarButton;
	private JButton unfamiliarButton;
	private JButton continueButton;

	private PanelState state;

	private boolean isFamiliar;

	public AppPanel() {

		wordLabel = new JLabel("", JLabel.CENTER);
		translationLabel = new JLabel("", JLabel.CENTER);

		Font labelFont = new Font(Font.SANS_SERIF, Font.BOLD, 48);
		wordLabel.setFont(labelFont);
		translationLabel.setFont(labelFont);

		String familiarButtonText = "I know this word";
		String unfamiliarButtonText = "I don't know";
		String continueButtonText = "Continue";

		familiarButton = new JButton(familiarButtonText);
		unfamiliarButton = new JButton(unfamiliarButtonText);
		continueButton = new JButton(continueButtonText);

		Font buttonFont = new Font(Font.SANS_SERIF, Font.BOLD, 48);
		familiarButton.setFont(buttonFont);
		unfamiliarButton.setFont(buttonFont);
		continueButton.setFont(buttonFont);

		this.setBorder(new EmptyBorder(20, 30, 20, 30));

		this.add(wordLabel);
		this.add(translationLabel);

		this.add(familiarButton);
		this.add(unfamiliarButton);
		this.add(continueButton);
	}

	public void refreshPanel(Word word) {
		switch (state) {
		case KANA_QUESTION:
			initializePanel();
			wordLabel.setText(word.getKana());
			familiarButton.setEnabled(true);
			unfamiliarButton.setEnabled(true);
			break;
		case WORD_QUESTION:
			initializePanel();
			wordLabel.setText(word.getName() + (word.getAccent() != -1 ? "(" + word.getAccent() + ")" : ""));
			familiarButton.setEnabled(true);
			unfamiliarButton.setEnabled(true);
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

		familiarButton.setEnabled(false);
		unfamiliarButton.setEnabled(false);
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

	public JButton getFamiliarButton() {
		return familiarButton;
	}

	public void setFamiliarButton(JButton familiarButton) {
		this.familiarButton = familiarButton;
	}

	public JButton getUnfamiliarButton() {
		return unfamiliarButton;
	}

	public void setUnfamiliarButton(JButton unfamiliarButton) {
		this.unfamiliarButton = unfamiliarButton;
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

	public boolean isFamiliar() {
		return isFamiliar;
	}

	public void setFamiliar(boolean isFamiliar) {
		this.isFamiliar = isFamiliar;
	}

}
