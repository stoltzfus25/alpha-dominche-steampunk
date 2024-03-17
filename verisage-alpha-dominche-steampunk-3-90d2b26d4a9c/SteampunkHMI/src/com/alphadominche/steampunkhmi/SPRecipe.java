package com.alphadominche.steampunkhmi;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import com.alphadominche.steampunkhmi.contentprovider.Provider;
import com.alphadominche.steampunkhmi.database.tables.RecipeTable;
import com.alphadominche.steampunkhmi.database.tables.RoasterTable;
import com.alphadominche.steampunkhmi.restclient.persistenceservicehelper.DefaultPersistenceServiceHelper;
import com.alphadominche.steampunkhmi.utils.AccountSettings;
import com.alphadominche.steampunkhmi.utils.Constants;
import com.alphadominche.steampunkhmi.utils.SteampunkUtils;

	
public class SPRecipe extends Observable implements Observer {
	public final static String AGITATIONS = "agitations";
	
	public final static double TWO_DECIMAL_PLACES = 100.0;
	public static final String TEA_RRECIPE_TYPE_DB_STR = "0";
	public final static int MIN_TOTAL_TIME = 0;
	public final static int MAX_TOTAL_TIME = 300;
	public final static int TOTAL_TIME_STEP = 1;
	public final static double MIN_VACUUM_BREAK_TIME = 0.0;
	public final static double MAX_VACUUM_BREAK_TIME = 10.0;
	public final static double VACUUM_BREAK_STEP = 0.1;
	public final static double MIN_GRIND = 0.0;
	public final static double MAX_GRIND = 10.0;
	public final static double GRIND_STEP = 0.25; //0.1;
	public final static int MIN_EXTRACT_TIME = 0;
	public final static int MAX_EXTRACT_TIME = 90;
	public final static int EXTRACT_TIME_STEP = 1;
	public final static int MIN_AGITATION_TIME = 0;
	public final static int MAX_AGITATION_TIME = 15;
	public final static int AGITATION_TIME_STEP = 1;
	public final static int MIN_TEMPERATURE_F = 140;
	public final static int MAX_TEMPERATURE_F = 212;
	public final static int MIN_TEMPERATURE_C = 60;
	public final static int MAX_TEMPERATURE_C = 100;
	public final static int TEMPERATURE_STEP = 1;
	public final static int MIN_GRAMS = 0;
	public final static int MAX_GRAMS = 80;
	public final static int GRAMS_STEP = 1;
	public final static double MIN_TEASPOONS = 0.0;
	public final static double MAX_TEASPOONS = 10.0;
	public final static double TEASPOONS_STEP = 0.5;
	public final static double MIN_VOLUME_OZ = 0.0;
	public final static double MAX_VOLUME_OZ = 16.0;
	public final static double VOLUME_OZ_STEP = 1.0;
	public final static double MIN_VOLUME_ML = 0.0;
	public final static double MAX_VOLUME_ML = 475.0;
	public final static double VOLUME_ML_STEP = 1.0;
	public final static int PUSH_UP_TIME = 5;
	public final static int FILL_DRAIN_TIME = 10;
	public final static int RINSE_DRAIN_TIME = 10;
	public final static String[] filters = {"Press", "Mid", "Clear", "Press w/Paper", "Mid w/Paper", "Clear w/Paper"};
	
	private long id;
	private SPRecipeType type;
	private SPUser roaster;
	private String coffee;
	private String tea;
	private double grind;
	private String filter;
	private int filterId;
	private ArrayList<SPRecipeStack> mStacks;
	private boolean newRecipe;
	
//	public ArrayList<SPRecipeStack> getStacks() {
//		return mStacks;
//	}
//
//	public void setStacks(ArrayList<SPRecipeStack> stacks) {
//		this.mStacks = stacks;
//		for(int i = 0;i < mStacks.size();i++) {
//			mStacks.get(i).addObserver(this);
//		}
//	}

	private int mGrams;
	private double mTeaspoons;
	private boolean mPublished;
	
	public SPRecipe(SPRecipeType type, long id, SPUser user) {
		this.type = type;
		this.id = id;
		mStacks = new ArrayList<SPRecipeStack>();
		mStacks.add(new SPRecipeStack());
		mStacks.get(0).addObserver(this);
		mGrams = 0;
		mTeaspoons = 0;
		mPublished = false;
		SPUser currentUser = user;
		setRoaster(currentUser);
//		validate();
	}
	
	SPRecipe(SPRecipe recipe){
		this.id = recipe.getId();
		this.type = recipe.type;
		
		if (recipe.type == SPRecipeType.TEA) {
			this.setTea(recipe.getTea());
		} else {
			this.setCoffee(recipe.getCoffee());
		}
		
		mStacks = new ArrayList<SPRecipeStack>();
		for(int i = 0; i < recipe.mStacks.size(); i++){
			mStacks.add(new SPRecipeStack(recipe.mStacks.get(i)));
		}
		
		filter = recipe.getFilter();
		grind = recipe.getGrind();
		mGrams = recipe.getGrams();
		setPublished(recipe.isPublished());
		setRoaster(recipe.getRoaster());
//		validate();
	}
	
	public SPRecipe(long id, Context context) {
		this.id = id;
		ContentResolver contentResolver = context.getContentResolver();
		String recipeId = String.valueOf(id);
		Cursor recipeCursor = contentResolver.query(
				Provider.RECIPE_CONTENT_URI,
				RecipeTable.ALL_COLUMNS,
				RecipeTable.ID + "=?",
				new String[] {recipeId},
				null);
		recipeCursor.moveToFirst();
		this.id = Long.parseLong(recipeCursor.getString(recipeCursor.getColumnIndex(RecipeTable.ID)));
		int tempType = recipeCursor.getInt(recipeCursor.getColumnIndex(RecipeTable.TYPE));
		String recipeName = recipeCursor.getString(recipeCursor.getColumnIndex(RecipeTable.NAME));
		int published = Integer.parseInt(recipeCursor.getString(recipeCursor.getColumnIndex(RecipeTable.PUBLISHED)));
		double grindValue = recipeCursor.getDouble(recipeCursor.getColumnIndex(RecipeTable.GRIND));
		int filterId = recipeCursor.getInt(recipeCursor.getColumnIndex(RecipeTable.FILTER));
		long roasterId = recipeCursor.getLong(recipeCursor.getColumnIndex(RecipeTable.STEAMPUNK_USER_ID));
		int grams = recipeCursor.getInt(recipeCursor.getColumnIndex(RecipeTable.GRAMS));
		double teaspoons = recipeCursor.getDouble(recipeCursor.getColumnIndex(RecipeTable.TEASPOONS));
		String stacks = recipeCursor.getString(recipeCursor.getColumnIndex(RecipeTable.STACKS));
		mStacks = new ArrayList<SPRecipeStack>();
		try {
			JSONArray stackArray = new JSONArray(stacks);
			for(int i = 0;i < stackArray.length();i++) {
				SPRecipeStack newStack = new SPRecipeStack(stackArray.getJSONObject(i));
				newStack.addObserver(this);
				mStacks.add(newStack);
			}
		} catch (JSONException e) {
			
		}
		if (tempType == 0) {
			this.type = SPRecipeType.TEA;
			this.setTea(recipeName);
		}
		else {
			this.type = SPRecipeType.COFFEE;
			this.setCoffee(recipeName);
		}
		setGrams(grams);
		setTeaspoons(teaspoons);
		setGrind(grindValue);
		setFilter(filters[filterId]);
		if (published == 0) {
			setPublished(false);
		} else {
			setPublished(true);
		}
		long userId = SteampunkUtils.getCurrentSteampunkUserId(context);
		String currUserType = SteampunkUtils.getCurrentSteampunkUserType(context);
		Cursor roasterCursor = context.getContentResolver().query(Provider.ROASTER_CONTENT_URI,
				new String[] {RoasterTable.USERNAME.toString()}, RoasterTable.WHERE_SP_ID_EQUALS,
				new String[] {Long.toString(roasterId)}, null);
		roasterCursor.moveToFirst();
		
		if (roasterCursor.getCount() > 0) {
			int roasterColumn = roasterCursor.getColumnIndex(RoasterTable.USERNAME);
			String currusername=AccountSettings.getAccountSettingsFromSharedPreferences(context).getUsername();
			String username = ((userId == roasterId) ? currusername : roasterCursor.getString(roasterColumn));
			String usertype = ((userId == roasterId) ? SteampunkUtils.getCurrentSteampunkUserType(context) : SPUser.ROASTER);
			setRoaster(new SPUser(roasterId, username, usertype));
		} else {
			setRoaster(new SPUser(roasterId, "", currUserType));
		}
//		validate();
	}

	// Validate that the recipe is born of goodly parents
	private void validate() {
		if (mStacks.size() == 0) {
			mStacks.add(new SPRecipeStack());
			mStacks.get(0).addObserver(this);
		}
		else if (mStacks.size() > 3) {
			while (mStacks.size() != 3) {
				mStacks.remove(mStacks.size() - 1).deleteObserver(this);
			}
		}
		
		for (int i = 0; i < mStacks.size(); i++) {
//			mStacks.get(i).validate();
		}
	}
		
	public SPRecipeType getType() {
		return type;
	}
	
	public void setType(SPRecipeType type) {
		this.type = type;
		setChanged();
		notifyObservers();
	}
	
	public long getId() {
		return id;
	}
	
	public SPUser getRoaster() {
		return roaster;
	}

	public void setRoaster(SPUser roaster) {
		this.roaster = roaster;
		setChanged();
		notifyObservers();
	}

	public String getCoffee() {
		return coffee;
	}

	public void setCoffee(String coffee) {
		this.coffee = coffee;
		setChanged();
		notifyObservers();
	}

	public String getTea() {
		return tea;
	}

	public void setTea(String tea) {
		this.tea = tea;
		setChanged();
		notifyObservers();
	}

	public String getName(){
		if (this.type == SPRecipeType.COFFEE) {
			return this.coffee;
		} else {
			return this.tea;
		}
	}
	
	public void setName(String name) {
		if (this.type == SPRecipeType.COFFEE){
			this.coffee = name;
		} else {
			this.tea = name;
		}
		setChanged();
		notifyObservers();
	}
	
	public int getImage(){
		if(this.type == SPRecipeType.COFFEE){
			return R.drawable.bean_gray;
		}
		else{
			return R.drawable.tea_gray;
		}
	}
	
	public double getGrind() {
		return grind;
	}

	public void setGrind(double grindVal) {
		this.grind = grindVal;
		setChanged();
		notifyObservers();
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
		for(int i = 0;i < filters.length;i++){
			if(filters[i].equals(filter)){
				setFilterId(i);
			}
		}
//		setChanged();
//		notifyObservers();
	}
	
	public int getGrams() {
		return mGrams;
	}
	
	public void setGrams(int grams) {
		mGrams = grams;
		setChanged();
		notifyObservers();
	}
	
	public double getTeaspoons() {
		return mTeaspoons;
	}
	
	public void setTeaspoons(double teaspoons) {
		mTeaspoons = teaspoons;
		setChanged();
		notifyObservers();
	}
	
	public int getTotalTime(int stackIndex) {
		return mStacks.get(stackIndex).getTotalTime();
	}
	
	public void setTotalTime(int stackIndex, int time) {
		mStacks.get(stackIndex).setTotalTime(time);
	}
	
	public double getTemp(int stackIndex) {
		return mStacks.get(stackIndex).getTemperature();
	}
	
	public void setTemp(int stackIndex, double temp) {
		mStacks.get(stackIndex).setTemperature(temp);
	}
	
	public SPTempUnitType getTempType(int stackIndex) {
		return mStacks.get(stackIndex).getTempType();
	}
	
	public void setTempType(int stackIndex, SPTempUnitType units) {
		mStacks.get(stackIndex).setTempType(units);
	}
	
	public double getVolume(int stackIndex) {
		return mStacks.get(stackIndex).getVolume(); // TODO need to make it so that the recipes which are from the build before JSON stacks either get deleted or converted
	}
	
	public void setVolume(int stackIndex, double volume) {
		mStacks.get(stackIndex).setVolume(Math.round(volume * TWO_DECIMAL_PLACES) / TWO_DECIMAL_PLACES);
	}
	
	public double getVacuumBreak(int stackIndex) {
		return mStacks.get(stackIndex).getVacuumBreak();
	}
	
	public SPVolumeUnitType getVolumeType(int stackIndex) {
		return mStacks.get(stackIndex).getVolumeType();
	}
	
	public void setVolumeType(int stackIndex, SPVolumeUnitType units) {
		mStacks.get(stackIndex).setVolumeType(units);
	}
	
	public void setVacuumBreak(int stackIndex, double time) {
		mStacks.get(stackIndex).setVacuumBreak(time);
	}
	
	public int getExtractionSeconds(int stackIndex) {
		return mStacks.get(stackIndex).getExtractionTime();
	}
	
	public void setExtractionSeconds(int stackIndex, int seconds) {
		mStacks.get(stackIndex).setExtractionTime(seconds);
	}
	
	public int getAgitationCount(int stackIndex) {
		return mStacks.get(stackIndex).getAgitationCount();
	}
	
	public double getAgitationLength(int stackIndex, int index) {
		return mStacks.get(stackIndex).getAgitationLength(index);
	}
	
	public int getAgitationStartTime(int stackIndex, int index) {
		return mStacks.get(stackIndex).getAgitationStartTime(index);
	}
	
	public double getAgitationPulseWidth(int stackIndex, int index) {
		return mStacks.get(stackIndex).getAgitationPulseWidth(index);
	}
	
	public void setAgitation(int stackIndex, int index, double seconds, int startTime, double pulseWidth) {
		mStacks.get(stackIndex).setAgitation(index, seconds, startTime, pulseWidth);
	}
	
	public int getRoomAfterAg(int stackIndex, int agIndex) {
		int startAfter = getStartTimeAfter(stackIndex, agIndex);
		if (getTotalTime(stackIndex) > startAfter) {
			if (getAgitationCount(stackIndex) - 1 > agIndex) {
				return getAgitationStartTime(stackIndex, agIndex + 1) - startAfter;
			} else {
				return getTotalTime(stackIndex) - startAfter;
			}
		}
		return 0;
	}
	
	public int getStartTimeAfter(int stackIndex, int agIndex) {
		int prevStart = getAgitationStartTime(stackIndex, agIndex);
		return  prevStart + getDurationAsWholeSeconds(stackIndex, agIndex);
	}
	
	public int getDurationAsWholeSeconds(int stackIndex, int agIndex) {
		double prevDuration = getAgitationLength(stackIndex, agIndex);
		int prevDelta = 0;
		if (Math.abs(prevDuration - Math.floor(prevDuration)) < SPRecipeDefaults.COMPARISON_TOLERANCE) {
			prevDelta = (int)Math.floor(prevDuration + 0.5);
		} else {
			prevDelta = (int)Math.ceil(prevDuration);
		}
		return prevDelta;
	}
	
	public void removeAgitation(int stackIndex, int index) {
		mStacks.get(stackIndex).removeAgitation(index);
	}
	
	public int getStackCount() {
		return mStacks.size();
	}
	
	public void addStack() {
		if (type == SPRecipeType.TEA) {
			mStacks.add(SPRecipeDefaults.getNewStackForRecipe(this));
			mStacks.get(mStacks.size() - 1).addObserver(this);
		}
		setChanged();
		notifyObservers();
	}
	
	public void removeStack(int index) {
		if (type == SPRecipeType.TEA){
			mStacks.remove(index).deleteObserver(this);
		}
		setChanged();
		notifyObservers();
	}

	@Override
	public void update(Observable observable, Object data) { SPLog.debug("got notified!");
		setChanged();
		notifyObservers();
	}
	
	public void save(Context context) {
		SPModel.getInstance(context).setSavingRecipe(getId());
		int recipeType = Constants.RECIPE_TYPE_TEA;
		if (type == SPRecipeType.COFFEE) {
			recipeType = Constants.RECIPE_TYPE_COFFEE;
		} else if (type == SPRecipeType.TEA) {
			recipeType = Constants.RECIPE_TYPE_TEA;
		}
		
		String stacks = "";
		try {
			JSONArray jsonStacks = new JSONArray();
			for(int i = 0;i < mStacks.size();i++) {
				jsonStacks.put(mStacks.get(i).unparse());
			}
			stacks = jsonStacks.toString(); //SPLog.debug("stacks(save): " + stacks);
		} catch (JSONException e) {
			
		}
		
		if (newRecipe) {
			id = -1L;
		}
		DefaultPersistenceServiceHelper.getInstance(context).saveRecipe(
				id,
				getName(),
				recipeType,
				mPublished,
				mGrams,
				mTeaspoons,
				grind,
				filterId,
				stacks);
	}
	
	public void delete(Context context) {
		DefaultPersistenceServiceHelper.getInstance(context).deleteRecipe(id);
	}
	
	public static void delete(Context context, long recipeId) {
		DefaultPersistenceServiceHelper.getInstance(context).deleteRecipe(recipeId);
	}

	public boolean isPublished() {
		return mPublished;
	}

	public void setPublished(boolean mPublished) {
		this.mPublished = mPublished;
		setChanged();
		notifyObservers();
	}


	public int getFilterId() {
		return filterId;
	}

	public void setFilterId(int filterId) {
		this.filterId = filterId;
		setChanged();
		notifyObservers();
	}

	public boolean isNewRecipe() {
		return newRecipe;
	}

	public void setNewRecipe(boolean newRecipe) {
		this.newRecipe = newRecipe;
		setChanged();
		notifyObservers();
	}

	public int getTypeNumber() {
		if (getType() == SPRecipeType.TEA) {
			return 0;
		} else {
			return 1;
		}
	}
	
	public void copyRecipeIntoSelf(SPRecipe recipe) {
		this.id = recipe.getId();
		while (mStacks.size() > recipe.getStackCount()) {
			mStacks.remove(mStacks.size() - 1);
		}
		while (mStacks.size() < recipe.getStackCount()) {
			mStacks.add(new SPRecipeStack());
		}
		for (int i = 0;i < recipe.mStacks.size();i++) {
			mStacks.get(i).copyStackIntoSelf(recipe.mStacks.get(i));
		}
		if (recipe.type == SPRecipeType.TEA) {
			this.type = SPRecipeType.TEA;
			this.setTea(recipe.getTea());
		} else {
			this.type = SPRecipeType.COFFEE;
			this.setCoffee(recipe.getCoffee());
		}
		filter = recipe.getFilter();
		grind = recipe.getGrind();
		mGrams = recipe.getGrams();
		mTeaspoons = recipe.getTeaspoons();
		setPublished(recipe.isPublished());
		setRoaster(recipe.getRoaster());
	}
}
