package org.mcraft.kantanmemory.kernel.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class LearningWordData extends UserWordData {
	@JsonIgnore
	private FamiliarType familiarType;

	public LearningWordData(UserWordData userWordData) {
		super(userWordData.getWord(), userWordData.getFamiliarity(), userWordData.getLastSeenDate());
		setFamiliarType(FamiliarType.HALF_FAMILIAR);
	}

	public LearningWordData(UserWordData userWordData, FamiliarType familiarType) {
		super(userWordData.getWord(), userWordData.getFamiliarity(), userWordData.getLastSeenDate());
		this.setFamiliarType(familiarType);
	}

	@JsonIgnore
	public FamiliarType getFamiliarType() {
		return familiarType;
	}

	@JsonIgnore
	public void setFamiliarType(FamiliarType familiarType) {
		this.familiarType = familiarType;
	}

}
