package com.alphadominche.steampunkhmi.restclient.presistenceservice;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;

import com.alphadominche.steampunkhmi.SPTempUnitType;
import com.alphadominche.steampunkhmi.SPVolumeUnitType;
import com.alphadominche.steampunkhmi.database.tables.RecipeTable;
import com.alphadominche.steampunkhmi.model.Recipe;
import com.alphadominche.steampunkhmi.utils.AccountSettings;
import com.alphadominche.steampunkhmi.utils.Constants;
import com.alphadominche.steampunkhmi.utils.MachineSettings;
import com.alphadominche.steampunkhmi.utils.SteampunkUtils;

public class IntentParser {
	// TODO pull the intent parsing out of the persistence service and put it
	// here so we have better decoupling and so we can test it easier

	private Context mContext;

	public IntentParser(Context context) {
		this.mContext = context;
	}

//	@SuppressWarnings("unchecked")
	public Recipe parseSaveRecipeIntent(Intent saveRecipeIntent) {
		Recipe recipe = makeRecipe(saveRecipeIntent);
//		recipe.put("recipe", makeRecipe(saveRecipeIntent));
//		recipe.put("stacks", makeStacks(saveRecipeIntent)); //ArrayList<Stack>
//		recipe.put("agitations", makeAgitationCycles(saveRecipeIntent, (ArrayList<Stack>)recipe.get("stacks"))); //HashMap<Stack, ArrayList<AgitationCycle>>

		return recipe;
	}

//	public Recipe parseCreateRecipeIntent(Intent createRecipeIntent) {
//
//		return makeRecipe(createRecipeIntent);
//	}
//
//	public Recipe parseUpdateRecipeIntent(Intent updateRecipeIntent) {
//
//		return makeRecipe(updateRecipeIntent);
//	}
//	
//	public Stack parseUpdateStackIntent(Intent updateStackIntent) {
//
//		return makeStack(updateStackIntent);
//	}
//	
//	public Stack parseCreateStackIntent(Intent createStackIntent) {
//		return makeStack(createStackIntent);
//	}
//	
//	public AgitationCycle parseCreateStackAgitationCycleIntent(Intent intent) {
//		return makeAgitationCycle(intent);
//	}
//	
//	public AgitationCycle parseUpdateAgitationCycleIntent(Intent updateAgitationCycleIntent) {
//
//		return makeAgitationCycle(updateAgitationCycleIntent);
//	}
	

	private Recipe makeRecipe(Intent recipeIntent) {
		Long recipeId = recipeIntent.hasExtra(RecipeTable.ID) ? recipeIntent
				.getLongExtra(RecipeTable.ID, 0) : null;
		long localId = recipeIntent.getLongExtra(RecipeTable.LOCAL_ID, -1);
		String name = recipeIntent.getStringExtra(RecipeTable.NAME);
		int type = recipeIntent.getIntExtra(RecipeTable.TYPE, -1);
		boolean published = recipeIntent.getBooleanExtra(RecipeTable.PUBLISHED,
				false);
		double grams = recipeIntent.getDoubleExtra(RecipeTable.GRAMS, -1);
		double teaspoons = recipeIntent.getDoubleExtra(RecipeTable.TEASPOONS,
				-1);
		int filter = recipeIntent.getIntExtra(RecipeTable.FILTER, -1);
		double grind = recipeIntent.getDoubleExtra(RecipeTable.GRIND, -1);
		String stacks = recipeIntent.getStringExtra(RecipeTable.STACKS);

		long steampunkUserId = SteampunkUtils
				.getCurrentSteampunkUserId(mContext);

		Recipe newRecipe = new Recipe();
		newRecipe.setLocal_id(localId);
		newRecipe.setId(recipeId);
		newRecipe.setName(name);
		newRecipe.setType(type);
		newRecipe.setSteampunk_user_id(steampunkUserId);
		newRecipe.setPublished(published);
		newRecipe.setGrams(grams);
		newRecipe.setTeaspoons(teaspoons);
		newRecipe.setGrind(grind);
		newRecipe.setFilter(filter);
		newRecipe.setStacks(stacks);

		return newRecipe;
	}

//	private ArrayList<Stack> makeStacks(Intent intent) {
//		@SuppressWarnings("unchecked")
//		ArrayList<HashMap<String, Serializable>> stackDescList = (ArrayList<HashMap<String, Serializable>>)intent.getExtras().get(Constants.STACKS_INTENT_KEY);
//		ArrayList<Stack> stacks = new ArrayList<Stack>();
//		for(int i = 0;i < stackDescList.size();i++) { SPLog.debug("currStack: " + stackDescList);
//			stacks.add(makeStack(stackDescList.get(i)));
//		}
//		
//		return stacks;
//	}
//	@SuppressWarnings("unchecked")
//	private HashMap<Stack, ArrayList<AgitationCycle>> makeAgitationCycles(Intent intent, ArrayList<Stack> stacks) {
//		ArrayList<HashMap<String, Serializable>> stackDescList = (ArrayList<HashMap<String, Serializable>>)intent.getExtras().get(Constants.STACKS_INTENT_KEY);
//		HashMap<Stack, ArrayList<AgitationCycle>> agMap = new HashMap<Stack, ArrayList<AgitationCycle>>();
//		for(int i = 0;i < stackDescList.size();i++) {
//			ArrayList<AgitationCycle> stackAgitations = new ArrayList<AgitationCycle>();
//			HashMap<String, Serializable> agDescList = stackDescList.get(i);
//			ArrayList<Serializable> ags = (ArrayList<Serializable>)agDescList.get(Constants.AGITATION_INTENT_KEY);
//			for(int j = 0;j < ags.size();j++) {
//				stackAgitations.add(makeAgitationCycle((HashMap<String, Serializable>)ags.get(j)));
//			}
//			agMap.put(stacks.get(i), stackAgitations);
//		}
//		
//		return agMap;
//	}
//	private Stack makeStack(HashMap<String, Serializable> stackMap) {
//		Long stackId = (Long)(stackMap.get(StackTable.ID));
//
//		long localId = (Long)stackMap.get(StackTable.ID);
//		long recipeId = (Long)stackMap.get(StackTable.RECIPE_ID);
//		int order = (Integer)stackMap.get(StackTable.STACK_ORDER);
//		double volume = (Double)stackMap.get(StackTable.VOLUME);
//		int duration = (Integer)stackMap.get(StackTable.DURATION);
//		double temperature = (Double)stackMap.get(StackTable.TEMPERATURE);
//		double vacuum_break = (Double)stackMap.get(StackTable.VACUUM_BREAK);
//		int pull_down_time = (Integer)stackMap.get(StackTable.PULL_DOWN_TIME);
//
//		
//
//		Stack newStack = new Stack();
////		newStack.setLocal_id(localId);
//		newStack.setId(stackId);
//		newStack.setRecipe_id(recipeId);
//		newStack.setStack_order(order);
//		newStack.setDuration(duration);
//		newStack.setVacuum_break(vacuum_break);
//		newStack.setVolume(volume);
////		newStack.setStart_time(start_time);
//		newStack.setTemperature(temperature);
//		newStack.setPull_down_time(pull_down_time);
//
//		return newStack;
//	}
//	private AgitationCycle makeAgitationCycle(HashMap<String, Serializable> agMap) {
//		Long agitationCycleId = (Long)agMap.get(AgitationCycleTable.ID);
////		long localId = (Long)agMap.get(AgitationCycleTable.LOCAL_ID);
//		long stackId = (Long)agMap.get(AgitationCycleTable.STACK_ID);
//		int startTime = (Integer)agMap.get(AgitationCycleTable.START_TIME);
//		int duration = (Integer)agMap.get(AgitationCycleTable.DURATION);
//		
//		AgitationCycle newAgitation = new AgitationCycle();
////		newAgitation.setLocal_id(localId);
//		newAgitation.setId(agitationCycleId);
//		newAgitation.setStack_id(stackId);
//		newAgitation.setStart_time(startTime);
//		newAgitation.setDuration(duration);
//		
//		return newAgitation;
//	}

	public String parseGetMachineSettingsIntent(Intent intent) {
//		MachineSettings currentMachineSettings = MachineSettings
//				.getMachineSettingsFromSharedPreferences(mContext);
		String serialNumber = intent
				.getStringExtra(Constants.MACHINE_SETTINGS_SERIAL_NUMBER);
		return serialNumber;
	}
	public MachineSettings parseSaveMachineSettingsIntent(
			Intent saveMachineSettingsIntent) {

		MachineSettings currentMachineSettings = MachineSettings
				.getMachineSettingsFromSharedPreferences(mContext);

		Long machineId = currentMachineSettings.getId();
		String serialNumber = saveMachineSettingsIntent
				.getStringExtra(Constants.MACHINE_SETTINGS_SERIAL_NUMBER);
		double boilerTemp = saveMachineSettingsIntent.getDoubleExtra(
				Constants.MACHINE_SETTINGS_BOILER_TEMP, -1.0);
		double rinseTemp = saveMachineSettingsIntent.getDoubleExtra(
				Constants.MACHINE_SETTINGS_RINSE_TEMP, -1.0);
		double rinseVolume = saveMachineSettingsIntent.getDoubleExtra(
				Constants.MACHINE_SETTINGS_RINSE_VOLUME, -1.0);
		double elevation = saveMachineSettingsIntent.getDoubleExtra(
				Constants.MACHINE_SETTINGS_ELEVATION, -1.0);
		Integer crucibleCount = currentMachineSettings.getCrucibleCount();
		// Because of type erasure, I can't do a check on a generic and this is
		// the only way to pass an ArrayList through an intent
		@SuppressWarnings("unchecked")
		ArrayList<Boolean> crucibleStates = (ArrayList<Boolean>) saveMachineSettingsIntent
				.getSerializableExtra(Constants.MACHINE_SETTINGS_CRUCIBLE_STATES);

		SPTempUnitType tempUnitType = (SPTempUnitType) saveMachineSettingsIntent
				.getSerializableExtra(Constants.MACHINE_SETTINGS_TEMP_UNIT_TYPE);
		SPVolumeUnitType volumeUnitType = (SPVolumeUnitType) saveMachineSettingsIntent
				.getSerializableExtra(Constants.MACHINE_SETTINGS_VOLUME_UNIT_TYPE);
		boolean localOnly = saveMachineSettingsIntent.getBooleanExtra(Constants.MACHINE_SETTINGS_LOCAL_ONLY, false);

		return new MachineSettings(machineId, serialNumber, boilerTemp,
				rinseTemp, rinseVolume, elevation, crucibleCount,
				crucibleStates, tempUnitType, volumeUnitType, localOnly);
	}

	public AccountSettings parseSaveAccountIntent(Intent saveAccountIntent) {
		String intentusername = saveAccountIntent
				.getStringExtra(Constants.USER_SETTINGS_USERNAME);
		String intentEmail = saveAccountIntent
				.getStringExtra(Constants.USER_SETTINGS_EMAIL);
		String intentAddress = saveAccountIntent
				.getStringExtra(Constants.USER_SETTINGS_ADDRESS);
		String intentCity = saveAccountIntent
				.getStringExtra(Constants.USER_SETTINGS_CITY);
		String intentState = saveAccountIntent
				.getStringExtra(Constants.USER_SETTINGS_STATE);
		String intentCountry = saveAccountIntent
				.getStringExtra(Constants.USER_SETTINGS_COUNTRY);
		String intentZipCode = saveAccountIntent
				.getStringExtra(Constants.USER_SETTINGS_ZIP_CODE);
		Boolean intentProtectRecipes = saveAccountIntent.getBooleanExtra(
				Constants.USER_SETTINGS_PROTECT_RECIPES, true);

		AccountSettings newAccountSettings = new AccountSettings(
				intentusername, intentEmail, intentAddress, intentCity,
				intentState, intentCountry, intentZipCode, intentProtectRecipes);

		return newAccountSettings;
	}
	
	public String parsePasswordResetIdentifier(Intent pwResetIntent) {
		return pwResetIntent.getStringExtra(Constants.PW_RESET_IDENTIFIER);
	}

	
}
