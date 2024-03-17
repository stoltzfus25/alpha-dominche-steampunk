package com.alphadominche.steampunkhmi;

public class SPRecipeDefaults {
	public final static double TEMPERATURE = (190.0 - 32.0) * 5.0 / 9.0 + 273.15; //190 deg F
	public final static double VOLUME = 12.0 * 29.5735296; //12 ounces
	public final static int NUMBER_OF_AGITATIONS = 3;
	public final static double FIRST_AGITATION = 3.0; //seconds
	public final static double SECOND_AGITATION = 2.0; //seconds
	public final static double THIRD_AGITATION = 2.0; //seconds
	public final static double EXTRA_AGITATION = 1.0; //seconds
	public final static double PULSE_WIDTH = 0.5;
	public final static int TOTAL_TIME = 75; //seconds
	public final static double VACUUM_BREAK = 1.0; //seconds
	public final static int EXTRACTION_TIME = 45; //seconds
	public final static double GRIND = 3.75;
	public final static int GRAMS = 23;
	public final static String NAME = "Recipe";
	public final static String FILTER = SPRecipe.filters[1];
	
	public final static double COMPARISON_TOLERANCE = 0.00001;
	
	public final static long NEW_RECIPE_ID = -1L;
	
	public static SPRecipe getNewCoffeeRecipe(SPUser user) {
		SPRecipe newRecipe = new SPRecipe(SPRecipeType.COFFEE, NEW_RECIPE_ID, user);
		newRecipe.setName(NAME);
		newRecipe.setFilter(FILTER);
		newRecipe.setGrams(GRAMS);
		newRecipe.setGrind(GRIND);
		newRecipe.setTemp(0, TEMPERATURE);
		newRecipe.setVolume(0, VOLUME);
		newRecipe.setVacuumBreak(0, VACUUM_BREAK);
		newRecipe.setExtractionSeconds(0, EXTRACTION_TIME);
		newRecipe.setTotalTime(0, TOTAL_TIME);
		newRecipe.setAgitation(0, 0, FIRST_AGITATION, 0, PULSE_WIDTH);
		newRecipe.setAgitation(0, 1, SECOND_AGITATION, 25, PULSE_WIDTH);
		newRecipe.setAgitation(0, 2, THIRD_AGITATION, 50, PULSE_WIDTH);
		newRecipe.setPublished(false);
		newRecipe.setNewRecipe(true);
		return newRecipe;
	}
	
	public static SPRecipe getNewTeaRecipe(SPUser user) {
		SPRecipe newRecipe = new SPRecipe(SPRecipeType.TEA, NEW_RECIPE_ID, user);
		newRecipe.setName(NAME);
		newRecipe.setFilter(FILTER);
		newRecipe.setGrams(GRAMS);
		newRecipe.setGrind(GRIND);
		newRecipe.setTemp(0, TEMPERATURE);
		newRecipe.setVolume(0, VOLUME);
		newRecipe.setVacuumBreak(0, VACUUM_BREAK);
		newRecipe.setExtractionSeconds(0, EXTRACTION_TIME);
		newRecipe.setTotalTime(0, TOTAL_TIME);
		newRecipe.setAgitation(0, 0, FIRST_AGITATION, 0, PULSE_WIDTH);
		newRecipe.setAgitation(0, 1, SECOND_AGITATION, 25, PULSE_WIDTH);
		newRecipe.setAgitation(0, 2, THIRD_AGITATION, 50, PULSE_WIDTH);
		newRecipe.setPublished(false);
		newRecipe.setNewRecipe(true);
		return newRecipe;
	}
	
	public static SPRecipeStack getNewStackForRecipe(SPRecipe recipe) {
		SPRecipeStack newStack = new SPRecipeStack();
		
		newStack.setTemperature(TEMPERATURE);
		newStack.setVolume(VOLUME);
		newStack.setTotalTime(TOTAL_TIME);
		newStack.setVacuumBreak(VACUUM_BREAK);
		newStack.setExtractionTime(EXTRACTION_TIME);
		newStack.setAgitation(0, FIRST_AGITATION, 0, PULSE_WIDTH);
		newStack.setAgitation(1, SECOND_AGITATION, 25, PULSE_WIDTH);
		newStack.setAgitation(2, THIRD_AGITATION, 50, PULSE_WIDTH);
		
		return newStack;
	}
}
