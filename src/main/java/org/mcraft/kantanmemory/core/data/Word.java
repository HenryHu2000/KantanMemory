package org.mcraft.kantanmemory.core.data;

public class Word implements Comparable<Word> {
	private String name;
	private String kana;
	private String translation;
	private int accent;

	// For generating YAML file
	protected Word() {

	}

	public Word(String name, String kana, String translation) {
		this.name = name;
		this.kana = kana;
		this.translation = translation;
		this.accent = -1;
	}

	public Word(String name, String kana, String translation, int accent) {
		this.name = name;
		this.kana = kana;
		this.translation = translation;
		this.accent = accent;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Word) {
			return (this.name.equals(((Word) o).getName())) && (this.kana.equals(((Word) o).getKana()))
					&& (this.translation.equals(((Word) o).getTranslation()));
		}
		return false;
	}

	@Override
	public int compareTo(Word anotherWord) {
		// TODO Auto-generated method stub
		if (this.equals(anotherWord)) {
			return 0;
		} else if (this.kana.compareTo(anotherWord.getKana()) != 0) {
			return this.kana.compareTo(anotherWord.getKana());
		} else if (this.name.compareTo(anotherWord.getName()) != 0) {
			return this.name.compareTo(anotherWord.getName());
		} else if (this.translation.compareTo(anotherWord.getTranslation()) != 0) {
			return this.translation.compareTo(anotherWord.getTranslation());
		}
		return 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKana() {
		return kana;
	}

	public void setKana(String kana) {
		this.kana = kana;
	}

	public String getTranslation() {
		return translation;
	}

	public void setTranslation(String translation) {
		this.translation = translation;
	}

	public int getAccent() {
		return accent;
	}

	public void setAccent(int accent) {
		this.accent = accent;
	}

}
