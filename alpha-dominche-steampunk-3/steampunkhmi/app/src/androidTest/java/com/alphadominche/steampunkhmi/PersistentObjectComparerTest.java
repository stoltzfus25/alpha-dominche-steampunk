package com.alphadominche.steampunkhmi;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.alphadominche.steampunkhmi.contentprovider.Provider;
import com.alphadominche.steampunkhmi.database.tables.RecipeTable;
import com.alphadominche.steampunkhmi.model.Recipe;
import com.alphadominche.steampunkhmi.restclient.contentprocessor.PersistentObjectComparer;
import com.alphadominche.steampunkhmi.restclient.networkclient.RemoteClasses.RemoteRecipe;
import com.alphadominche.steampunkhmi.utils.Constants;

public class PersistentObjectComparerTest extends AndroidTestCase {

	public void testCompareLocalAndRemoteRecipe() {
		Long expectedId = 3L;
		String expectedName = "test recipe";
		int expectedType = 2;
		Long expectedSteampunkUserId = 2L;
		boolean expectedPublished = true;
		double expectedGrams = 4.0;
		double expectedTeaspoons = 5.0;
		double expectedGrind = 2.0;
		int expectedFilter = 2;
		Long expectedLocalId = 298L;
		String expectedStacks = "";
		String expectedUUID = "";
		
		ContentValues recipeContentValues = new ContentValues();
		recipeContentValues.put(RecipeTable.LOCAL_ID,expectedLocalId);
		recipeContentValues.put(RecipeTable.NAME, expectedName);
		recipeContentValues.put(RecipeTable.TYPE, expectedType);
		recipeContentValues.put(RecipeTable.STEAMPUNK_USER_ID,
				expectedSteampunkUserId);
		recipeContentValues.put(RecipeTable.PUBLISHED, expectedPublished);
		recipeContentValues.put(RecipeTable.GRAMS, expectedGrams);
		recipeContentValues.put(RecipeTable.TEASPOONS, expectedTeaspoons);
		recipeContentValues.put(RecipeTable.GRIND, expectedGrind);
		recipeContentValues.put(RecipeTable.FILTER, expectedFilter);

		recipeContentValues.put(RecipeTable.TRANSACTION_STATE,
				Constants.STATE_POSTING);

		ContentResolver contentResolver = mContext.getContentResolver();

		Uri newRecipeUri = contentResolver.insert(Provider.RECIPE_CONTENT_URI,
				recipeContentValues);

		Cursor localRecipeCursor = contentResolver.query(newRecipeUri, null,
				null, null, null);

		localRecipeCursor.moveToFirst();

		Recipe localRecipe = new Recipe(localRecipeCursor);

		RemoteRecipe remoteRecipe = new RemoteRecipe(expectedId, expectedLocalId, expectedName,
				expectedType, expectedSteampunkUserId, expectedPublished,
				expectedGrams, expectedTeaspoons, expectedGrind, expectedFilter, expectedStacks, expectedUUID);

		assertTrue(PersistentObjectComparer.compareLocalAndRemoteRecipe(
				localRecipe, remoteRecipe));
	}
}
