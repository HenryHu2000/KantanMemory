package org.mcraft.kantanmemory.model.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author Henry Hu
 *
 */
public class LearningWordData extends UserWordData {
	@JsonIgnore
	private KnownType knownType;

	public LearningWordData(UserWordData userWordData) {
		super(userWordData.getWord(), userWordData.getFamiliarity(), userWordData.getLastSeenDate());
		setKnownType(KnownType.HALF_KNOWN);
	}

	public LearningWordData(UserWordData userWordData, KnownType knownType) {
		super(userWordData.getWord(), userWordData.getFamiliarity(), userWordData.getLastSeenDate());
		this.setKnownType(knownType);
	}

	@JsonIgnore
	public KnownType getKnownType() {
		return knownType;
	}

	@JsonIgnore
	public void setKnownType(KnownType knownType) {
		this.knownType = knownType;
	}

}
