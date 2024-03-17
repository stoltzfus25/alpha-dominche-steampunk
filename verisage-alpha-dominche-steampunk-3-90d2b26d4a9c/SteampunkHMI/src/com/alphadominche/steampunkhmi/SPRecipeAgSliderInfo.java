package com.alphadominche.steampunkhmi;

public class SPRecipeAgSliderInfo {
	private SPRecipe mRecipe;
	private int mStack;
	private int mAgitation;
	
	public SPRecipeAgSliderInfo(SPRecipe recipe, int stack, int agitation) {
		mRecipe = recipe;
		mStack = stack;
		mAgitation = agitation;
	}
	
	public boolean hasRoomAfterToAddAg() {
		return mRecipe.getRoomAfterAg(mStack, mAgitation) > 0;
	}
	
	private int getStartTimeAfterPrevious() {
		if (mAgitation <= 0) return 0;
		return mRecipe.getStartTimeAfter(mStack, mAgitation - 1);
	}
	
	public int getNextStartTime() {
		return mRecipe.getStartTimeAfter(mStack, mAgitation);
	}
	
	private int getNextAgStartTime() {
		if (mAgitation == mRecipe.getAgitationCount(mStack) - 1) return mRecipe.getTotalTime(mStack);
		return mRecipe.getAgitationStartTime(mStack, mAgitation + 1);
	}
	
	private int getTopIndex() {
		return mRecipe.getAgitationCount(mStack) - 1;
	}
	
	public int getStartTimeMin() {
		if (mAgitation == 0) {
			return 0;
		} else {
			return getStartTimeAfterPrevious();
		}
	}
	
	public int getStartTimeMax() {
		if (mAgitation == 0) {
			return 0;
		} else if (mAgitation == getTopIndex()) {
			return mRecipe.getTotalTime(mStack) - mRecipe.getDurationAsWholeSeconds(mStack, mAgitation);
		} else {
			return mRecipe.getAgitationStartTime(mStack, mAgitation + 1) - mRecipe.getDurationAsWholeSeconds(mStack, mAgitation);
		}
	}
	
	public int getStartTimeProgressMax() {
		int agTopIndex = getTopIndex();
		if (mAgitation == 0) {
			return 0;
		} else if (mAgitation == agTopIndex) {
			return mRecipe.getTotalTime(mStack) - getStartTimeAfterPrevious() - mRecipe.getDurationAsWholeSeconds(mStack, mAgitation);
		} else {
			return mRecipe.getAgitationStartTime(mStack, mAgitation + 1) - getStartTimeAfterPrevious() - mRecipe.getDurationAsWholeSeconds(mStack, mAgitation);
		}
	}
	
	public int getStartTimeProgress() {
		return mRecipe.getAgitationStartTime(mStack, mAgitation) - getStartTimeMin();
	}
	
	public int getStartTime() {
		return mRecipe.getAgitationStartTime(mStack, mAgitation);
	}
	
	public double getDurationMin() {
		return SPModel.MIN_AGITATION_LENGTH;
	}
	
	public double getDurationMax() {
		return Math.min(SPModel.MAX_AGITATION_LENGTH, getNextAgStartTime() - getStartTime());
	}
	
	public int getDurationProgressMax() {
		return (int)(getDurationMax() * 10);
	}
	
	public int getDurationProgress() {
		return (int)(mRecipe.getAgitationLength(mStack, mAgitation) * 10);
	}
	
	public double getDuration() {
		return mRecipe.getAgitationLength(mStack, mAgitation);
	}
	
	public double getPulseWidthMin() {
		return SPModel.MIN_AGITATION_PULSE_WIDTH;
	}
	
	public double getPulseWidthMax() {
		return SPModel.MAX_AGITATION_PULSE_WIDTH;
	}
	
	public int getPulseWidthProgressMax() {
		return 10;
	}
	
	public int getPulseWidthProgress() {
		return (int)(mRecipe.getAgitationPulseWidth(mStack, mAgitation) * 10);
	}
	
	public double getPulseWidth() {
		return mRecipe.getAgitationPulseWidth(mStack, mAgitation);
	}
}
