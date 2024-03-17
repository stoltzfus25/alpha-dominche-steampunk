package com.alphadominche.steampunkhmi.test;

import android.content.Intent;
import android.test.AndroidTestCase;

import com.alphadominche.steampunkhmi.database.tables.RecipeTable;
import com.alphadominche.steampunkhmi.model.Recipe;
import com.alphadominche.steampunkhmi.restclient.presistenceservice.IntentParser;
import com.alphadominche.steampunkhmi.restclient.presistenceservice.PersistenceService;
import com.alphadominche.steampunkhmi.utils.Constants;

public class IntentParserTest extends AndroidTestCase {

	public void testParseSaveRecipeIntent() {
		IntentParser testIntentParser = new IntentParser(mContext);
		Intent testCreateRecipeIntent = new Intent();
		int expectedRequestId = 3;
		String expectedName = "test";
		int expectedType = 3;
		boolean expectedPublished = true;
		double expectedGrams = 4.0;
		double expectedTeaspoons = 5.0;
		int expectedFilter = 3;
		double expectedGrind = 3.5;

		testCreateRecipeIntent
				.setAction(PersistenceService.ACTION_CREATE_RECIPE);

		testCreateRecipeIntent.putExtra(Constants.INTENT_EXTRA_REQUEST_ID,
				expectedRequestId);

		testCreateRecipeIntent.putExtra(RecipeTable.NAME, expectedName);
		testCreateRecipeIntent.putExtra(RecipeTable.TYPE, expectedType);
		testCreateRecipeIntent.putExtra(RecipeTable.PUBLISHED, expectedPublished);
		testCreateRecipeIntent.putExtra(RecipeTable.GRAMS, expectedGrams);
		testCreateRecipeIntent.putExtra(RecipeTable.TEASPOONS, expectedTeaspoons);
		testCreateRecipeIntent.putExtra(RecipeTable.GRIND, expectedGrind);
		testCreateRecipeIntent.putExtra(RecipeTable.FILTER, expectedFilter);

		Recipe actualRecipe = testIntentParser
				.parseSaveRecipeIntent(testCreateRecipeIntent);

		assertNull(actualRecipe.getId());
		assertEquals(expectedName, actualRecipe.getName());
		assertEquals(expectedType, actualRecipe.getType());
		assertEquals(expectedPublished, actualRecipe.getPublished());
		assertEquals(expectedGrams, actualRecipe.getGrams());
		assertEquals(expectedTeaspoons, actualRecipe.getTeaspoons());
		assertEquals(expectedFilter, actualRecipe.getFilter());
		assertEquals(expectedGrind, actualRecipe.getGrind());
	}

	public void testParseUpdateRecipeIntent() {
		IntentParser testIntentParser = new IntentParser(mContext);
		Intent testCreateRecipeIntent = new Intent();
		int expectedRequestId = 3;
		Long expectedRecipeId = 3L;
		String expectedName = "test";
		int expectedType = 3;
		boolean expectedPublished = true;
		double expectedGrams = 4.0;
		double expectedTeaspoons = 5.0;
		int expectedFilter = 3;
		double expectedGrind = 3.5;

		testCreateRecipeIntent
				.setAction(PersistenceService.ACTION_UPDATE_RECIPE);

		testCreateRecipeIntent.putExtra(Constants.INTENT_EXTRA_REQUEST_ID,
				expectedRequestId);

		testCreateRecipeIntent.putExtra(RecipeTable.ID, expectedRecipeId);
		testCreateRecipeIntent.putExtra(RecipeTable.NAME, expectedName);
		testCreateRecipeIntent.putExtra(RecipeTable.TYPE, expectedType);
		testCreateRecipeIntent.putExtra(RecipeTable.PUBLISHED,
				expectedPublished);
		testCreateRecipeIntent.putExtra(RecipeTable.GRAMS, expectedGrams);
		testCreateRecipeIntent.putExtra(RecipeTable.TEASPOONS,
				expectedTeaspoons);
		testCreateRecipeIntent.putExtra(RecipeTable.GRIND, expectedGrind);
		testCreateRecipeIntent.putExtra(RecipeTable.FILTER, expectedFilter);

		Recipe actualRecipe = testIntentParser
				.parseSaveRecipeIntent(testCreateRecipeIntent);

		assertEquals(expectedRecipeId, actualRecipe.getId());
		assertEquals(expectedName, actualRecipe.getName());
		assertEquals(expectedType, actualRecipe.getType());
		assertEquals(expectedPublished, actualRecipe.getPublished());
		assertEquals(expectedGrams, actualRecipe.getGrams());
		assertEquals(expectedTeaspoons, actualRecipe.getTeaspoons());
		assertEquals(expectedFilter, actualRecipe.getFilter());
		assertEquals(expectedGrind, actualRecipe.getGrind());
	}

	public void testParseSaveMachineSettingsIntent() {
		// TODO add tests
	}

	public void testParseSaveAccountIntent() {
		// TODO add tests
	}

}
