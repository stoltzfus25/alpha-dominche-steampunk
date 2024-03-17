package com.alphadominche.steampunkhmi.restclient.persistenceservicehelper;

import java.util.ArrayList;

import com.alphadominche.steampunkhmi.SPTempUnitType;
import com.alphadominche.steampunkhmi.SPVolumeUnitType;

/**
 * Interface with which the activities interact for CRUD operations on server
 * data.
 * 
 * @author jnuss
 * 
 */
public interface PersistenceServiceHelper {

	/**
	 * Log in a user
	 * 
	 * @param username
	 *            the username of the user to login
	 * @param password
	 *            the user's password
	 */
	public void login(String username, String password);

	/**
	 * Log out the currently logged in user
	 */
	public void logout();

	/**
	 * Refresh list of roasters
	 */
	public void syncRoasters();

	/**
	 * Save the recipe
	 * 
	 * @param id
	 *            id of the recipe
	 * @param name
	 *            name of the recipe
	 * @param type
	 *            type of recipe
	 * @param published
	 *            whether the recipe has been published
	 * @param grams
	 *            grams of tea or coffee
	 * @param teaspoons
	 *            teaspoons of tea
	 * @param grind
	 *            the amount the coffee has been ground
	 * @param filter
	 *            the type of filter to be used
	 */
	public void saveRecipe(final long id, final String name, final int type,
			final boolean published, final double grams,
			final double teaspoons, final double grind, final int filter,
			String stacks);

	/**
	 * Refresh the list of recipes by getting new ones, updating existing ones,
	 * and deleting old ones
	 */
	public void syncRecipes();

	/**
	 * Delete a given recipe
	 * 
	 * @param recipeId
	 *            ID of the recipe to delete
	 */
	public void deleteRecipe(long recipeId);

	/**
	 * Create a new log
	 * 
	 * @param recipeId
	 *            recipe that was running when log message was generated, null
	 *            if no recipe is running
	 * @param crucibleIndex
	 *            crucible the log applies to, null if it doesn't apply to a
	 *            specific crucible
	 * @param severity
	 *            severity of the log
	 * @param type
	 *            type of log
	 * @param message
	 *            message of the log, null if no message
	 */
	public void createLog(String recipeId, Integer crucibleIndex,
			Integer severity, Integer type, String message);

	/**
	 * Add a recipe to the favorites list of the currently logged in user
	 * 
	 * @param recipdeId
	 *            the recipe to add to the favorites list
	 */
	public void createFavorite(long recipdeId);

	/**
	 * Delete a recipe from the favorites list of the currently logged in user
	 * 
	 * @param recipeId
	 *            the recipe to delete from the favorites list
	 */
	public void deleteFavorite(long recipeId);

	/**
	 * Persist the machine settings
	 * 
	 * @param serialNumber
	 *            serial number of the machine
	 * @param boilerTemp
	 *            the temperature of the boiler
	 * @param rinseTemp
	 *            the temperature of the rinse cycle
	 * @param rinseVoulme
	 *            volume of the rise cycle
	 * @param elevation
	 *            elevation of where the machine is
	 * @param crucibleStates
	 *            a list of which crucibles are enabled and which are disabled
	 * @param tempUnitType
	 *            unit type of the temperatures
	 * @param volumeUnitType
	 *            unit type of the volume
	 */
	public void saveMachineSettings(String serialNumber, Double boilerTemp,
			Double rinseTemp, Double rinseVoulme, Double elevation,
			ArrayList<Boolean> crucibleStates, SPTempUnitType tempUnitType,
			SPVolumeUnitType volumeUnitType, boolean localOnly);

	/**
	 * Persist the user settings
	 * 
	 * @param username
	 *            the user's username
	 * @param email
	 *            the user's email
	 * @param address
	 *            the user's address
	 * @param city
	 *            the user's city
	 * @param state
	 *            the user's state of residence
	 * @param country
	 *            the user's country of residence
	 * @param zip
	 *            the user's zip
	 * @param protectRecipes
	 *            whether or not recipes that are created are public or not
	 */
	public void saveAccountSettings(String username, String email,
			String address, String city, String state, String country,
			String zipCode, Boolean protectRecipes);

	/**
	 * Request the password to be reset
	 */
	public void requestPasswordReset(String identifier);

	public void checkForUpdates();

	void getMachineSettings();
	
	public void enableNetworking();
	public void disableNetworking();

}
