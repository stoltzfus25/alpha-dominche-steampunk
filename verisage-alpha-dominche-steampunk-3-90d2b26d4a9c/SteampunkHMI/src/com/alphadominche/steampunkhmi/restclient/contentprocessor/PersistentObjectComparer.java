package com.alphadominche.steampunkhmi.restclient.contentprocessor;

import com.alphadominche.steampunkhmi.model.Favorite;
import com.alphadominche.steampunkhmi.model.Recipe;
import com.alphadominche.steampunkhmi.model.Roaster;
import com.alphadominche.steampunkhmi.restclient.networkclient.RemoteClasses.RemoteFavorite;
import com.alphadominche.steampunkhmi.restclient.networkclient.RemoteClasses.RemoteRecipe;
import com.alphadominche.steampunkhmi.restclient.networkclient.RemoteClasses.RemoteRoaster;

public class PersistentObjectComparer {

	/**
	 * Check to see if a local and remote recipe contain the same information
	 * 
	 * @param localRecipe
	 * @param remoteRecipe
	 * @return
	 */
	public static boolean compareLocalAndRemoteRecipe(Recipe localRecipe,
			RemoteRecipe remoteRecipe) {
		
		boolean sameRecipe = localRecipe.getName().equals(remoteRecipe.name)
				&& localRecipe.getPublished() == remoteRecipe.published
				&& localRecipe.getType() == remoteRecipe.type
				&& localRecipe.getSteampunk_user_id() == remoteRecipe.steampunkuser
				&& localRecipe.getGrams() == remoteRecipe.grams
				&& localRecipe.getTeaspoons() == remoteRecipe.teaspoons
				&& localRecipe.getGrind() == remoteRecipe.grind
				&& localRecipe.getFilter() == remoteRecipe.filter;

		return sameRecipe;
	}
//	public static boolean compareLocalAndRemoteStack(Stack localStack,
//			RemoteStack remoteStack) {
//		
//		boolean sameStack = localStack.getDuration()==remoteStack.duration
//				&& localStack.getPull_down_time() == remoteStack.pull_down_time
//				&& localStack.getRecipe_id() == remoteStack.recipe
//				&& localStack.getStack_order() == remoteStack.order
//				&& localStack.getStart_time() == remoteStack.start_time
//				&& localStack.getTemperature() == remoteStack.temperature
//				&& localStack.getVacuum_break() == remoteStack.vacuum_break
//				&& localStack.getVolume() == remoteStack.volume;
//
//		return sameStack;
//	}
//	public static boolean compareLocalAndRemoteAgitation(
//			AgitationCycle localAgitation, RemoteAgitation remoteAgitation) {
//		boolean sameAgitation = localAgitation.getDuration()==remoteAgitation.duration
//				&& localAgitation.getStart_time() == remoteAgitation.start_time
//				&& localAgitation.getStack_id()== remoteAgitation.stack;
//
//		return sameAgitation;
//	}
	public static boolean compareLocalAndRemoteRoaster(
			Roaster localRoaster, RemoteRoaster remoteRoaster) {
		boolean sameRoaster = localRoaster.getFirst_name()==remoteRoaster.first_name
				&& localRoaster.getLast_name() == remoteRoaster.last_name
				&& localRoaster.getUsername()== remoteRoaster.username
				&& localRoaster.getId()== remoteRoaster.id
				&& localRoaster.getSteampunk_id()== remoteRoaster.steampunkuser;

		return sameRoaster;
	}
	public static boolean compareLocalAndRemoteFavorite(
			Favorite localFavorite, RemoteFavorite remoteFavorite) {
		boolean sameFavorite = localFavorite.getRecipe_uuid()==remoteFavorite.recipe_uuid
				&& localFavorite.getUser()== remoteFavorite.user;

		return sameFavorite;
	}
}
