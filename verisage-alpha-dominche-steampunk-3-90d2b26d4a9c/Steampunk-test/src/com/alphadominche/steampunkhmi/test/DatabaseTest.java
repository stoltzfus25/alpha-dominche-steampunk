package com.alphadominche.steampunkhmi.test;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.alphadominche.steampunkhmi.contentprovider.Provider;
import com.alphadominche.steampunkhmi.database.tables.FavoriteTable;
import com.alphadominche.steampunkhmi.database.tables.RecipeTable;
import com.alphadominche.steampunkhmi.database.tables.RoasterTable;
import com.alphadominche.steampunkhmi.model.Favorite;
import com.alphadominche.steampunkhmi.model.Recipe;
import com.alphadominche.steampunkhmi.model.Roaster;
import com.alphadominche.steampunkhmi.restclient.contentprocessor.PersistentObjectComparer;
import com.alphadominche.steampunkhmi.restclient.networkclient.RemoteClasses.RemoteFavorite;
import com.alphadominche.steampunkhmi.restclient.networkclient.RemoteClasses.RemoteRecipe;
import com.alphadominche.steampunkhmi.restclient.networkclient.RemoteClasses.RemoteRoaster;
import com.alphadominche.steampunkhmi.utils.Constants;

public class DatabaseTest extends AndroidTestCase 
{
	public void testCompareLocalAndRemoteRecipe() 
	{
		Long expectedId = 3L;
		String expectedName = "test recipe";
		int expectedType = 2;
		Long expectedSteampunkUserId = 2L;
		boolean expectedPublished = true;
		double expectedGrams = 4.0;
		double expectedTeaspoons = 5.0;
		double expectedGrind = 2.0;
		int expectedFilter = 2;
		Long expectedLocalId = 10L;
		String expectedStacks = "";
		String expectedUUID = "";
		

		ContentValues recipeContentValues = new ContentValues();

		recipeContentValues.put(RecipeTable.NAME, expectedName);
		recipeContentValues.put(RecipeTable.TYPE, expectedType);
		recipeContentValues.put(RecipeTable.STEAMPUNK_USER_ID,
				expectedSteampunkUserId);
		recipeContentValues.put(RecipeTable.PUBLISHED, expectedPublished);
		recipeContentValues.put(RecipeTable.GRAMS, expectedGrams);
		recipeContentValues.put(RecipeTable.TEASPOONS, expectedTeaspoons);
		recipeContentValues.put(RecipeTable.GRIND, expectedGrind);
		recipeContentValues.put(RecipeTable.FILTER, expectedFilter);
		recipeContentValues.put(RecipeTable.LOCAL_ID, expectedLocalId);
		//recipeContentValues.put(RecipeTable.LOCAL_MAC_ADDRESS, expectedMacAddress);

		recipeContentValues.put(RecipeTable.TRANSACTION_STATE,
				Constants.STATE_POSTING);

		ContentResolver contentResolver = mContext.getContentResolver();

		Uri newRecipeUri = contentResolver.insert(Provider.RECIPE_CONTENT_URI,
				recipeContentValues);

		Cursor localRecipeCursor = contentResolver.query(newRecipeUri, null,
				null, null, null);

		localRecipeCursor.moveToFirst();

		Recipe localRecipe = new Recipe(localRecipeCursor);

		RemoteRecipe remoteRecipe = new RemoteRecipe(expectedId,expectedLocalId, expectedName,
				expectedType, expectedSteampunkUserId, expectedPublished,
				expectedGrams, expectedTeaspoons, expectedGrind, expectedFilter, expectedStacks, expectedUUID);

		assertTrue(PersistentObjectComparer.compareLocalAndRemoteRecipe(
				localRecipe, remoteRecipe));
	}
	
	public void testCompareLocalAndRemoteFavorite() {
		Long userId = 1L;
		Long recipeId = 2L;
		ContentValues favoriteContentValues = new ContentValues();
		favoriteContentValues.put(FavoriteTable.USER, userId);
		favoriteContentValues.put(FavoriteTable.RECIPE_ID, recipeId);
		ContentResolver contentResolver = mContext.getContentResolver();
		Long expectedUser = 1l;

		Uri newFavoriteUri = contentResolver.insert(Provider.ROASTER_CONTENT_URI,
				favoriteContentValues);

		Cursor localFavoriteCursor = contentResolver.query(newFavoriteUri, null,
				null, null, null);	
		localFavoriteCursor.moveToFirst();

		Favorite localFavorite = new Favorite(localFavoriteCursor);

		RemoteFavorite remoteFavorite = new RemoteFavorite(String.valueOf(recipeId), userId, String.valueOf(userId));

		assertTrue(PersistentObjectComparer.compareLocalAndRemoteFavorite(
				localFavorite, remoteFavorite));
	
	}

	public void testCompareLocalAndRemoteRoaster() {
		String firstName = "john";
		String lastName = "doe";
		String username = "deadonarival";
		Long userId = 1L;
		Long steampunkId = 3L;
		int subscribedTo = 0;
		ContentValues roasterContentValues = new ContentValues();
		roasterContentValues.put(RoasterTable.FIRST_NAME, firstName);
		roasterContentValues.put(RoasterTable.LAST_NAME, lastName);
		roasterContentValues.put(RoasterTable.USERNAME, username);
		roasterContentValues.put(RoasterTable.ID, userId);
		roasterContentValues.put(RoasterTable.STEAMPUNK_ID, steampunkId);
		ContentResolver contentResolver = mContext.getContentResolver();

		Uri newRoastereUri = contentResolver.insert(Provider.ROASTER_CONTENT_URI,
				roasterContentValues);

		Cursor localRoasterCursor = contentResolver.query(newRoastereUri, null, null, null, null);	
		Roaster localRecipe = new Roaster(localRoasterCursor);

		RemoteRoaster remoteRecipe = new RemoteRoaster(firstName, lastName, username, userId, steampunkId, subscribedTo);

		assertTrue(PersistentObjectComparer.compareLocalAndRemoteRoaster(localRecipe, remoteRecipe));
	
	}
}
