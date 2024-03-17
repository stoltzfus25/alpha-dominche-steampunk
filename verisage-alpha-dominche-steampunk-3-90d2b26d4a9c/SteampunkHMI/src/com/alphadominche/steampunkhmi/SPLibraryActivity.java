package com.alphadominche.steampunkhmi;

import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.alphadominche.steampunkhmi.contentprovider.Provider;
import com.alphadominche.steampunkhmi.database.tables.FavoriteTable;
import com.alphadominche.steampunkhmi.database.tables.RecipeTable;
import com.alphadominche.steampunkhmi.database.tables.RoasterTable;
import com.alphadominche.steampunkhmi.restclient.persistenceservicehelper.DefaultPersistenceServiceHelper;
import com.alphadominche.steampunkhmi.restclient.persistenceservicehelper.DefaultPersistenceServiceHelperEvents;
import com.alphadominche.steampunkhmi.utils.AccountSettings;
import com.alphadominche.steampunkhmi.utils.Constants;
import com.alphadominche.steampunkhmi.utils.SteampunkUtils;

public class SPLibraryActivity extends SPActivity implements SPMainMenuHaver, OnClickListener, OnItemClickListener, OnItemLongClickListener, Observer {
	private SPModel mLibraryModel;
	private ImageView mMenuButton;
	private ImageView mMachineSettingsButton;
	private SearchView mSearchView ;

	private ListView mSearchList;
	private ListView mRecipeList;
	private Button mSubscribeButton;
	private long mSelectedRoaster;
	private String mSelectedRoasterName;

	private SPModel mMyRecipesModel = SPModel.getInstance(this);
	
	private FrameLayout mMenu;
	private FrameLayout mWrap;
	private boolean mShowingMenu;
	Timer mMenuAnimTimer;
	TimerTask mMenuAnimTask;
	SPMainMenuInAnim mMenuAnimIn;
	SPMainMenuOutAnim mMenuAnimOut;
	boolean mAcceptUpdates;
	private DisplayMetrics mMetrics;
	
	SPRecipe[] mStartingRecipes;
	SPRecipe[] mFavoriteRecipes;
	SPLibraryCursorAdapter mAdapter;
	Context mContext;

	SPLibraryRoasterCursorAdapter mRoasterAdapter;

	public void updateRoasterRecipes(long roasterId) {
		Long userId = SteampunkUtils.getCurrentUserId(this.getBaseContext());
			
		Cursor cursor = getContentResolver().query(Provider.RECIPE_CONTENT_URI,
			RecipeTable.ALL_COLUMNS, RecipeTable.STEAMPUNK_USER_ID + "=?",
			new String[] {Long.toString(mSelectedRoaster)}, RecipeTable.NAME + Constants.ASCENDING/*" COLLATE NOCASE"*/);
		
		Cursor favCursor = getContentResolver().query(
			Provider.FAVORITE_CONTENT_URI, FavoriteTable.ALL_COLUMNS,
			FavoriteTable.USER + "=?",
			new String[] {Long.toString(userId)}, null);
		favCursor.moveToFirst();
		
		Set<Long> favList = new TreeSet<Long>();
		for(int i = 0; i < favCursor.getCount(); i++) {
			favList.add(Long.parseLong(favCursor.getString(favCursor.getColumnIndex(FavoriteTable.RECIPE_ID)))); //taken out as part of the UUID and atomic recipe refactor and put back in now in case there is a need for it when searching
			favCursor.moveToNext();
		}
		favCursor.close();
		
		mAdapter = new SPLibraryCursorAdapter(
				this,
				R.drawable.library_recipe_name_view,
				cursor,
				new String[] {RecipeTable.NAME, RecipeTable.TYPE, RecipeTable.ID},
				new int[] {android.R.id.text1},
				favList);
		
		mRecipeList.setAdapter(mAdapter);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this.getApplicationContext();
		mLibraryModel = SPModel.getInstance(this);
		mLibraryModel.addObserver(this);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_library);
		
		mShowingMenu = false;
		  
	    mMetrics = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
	    mMenuAnimTimer = new Timer();
	  
	    mMenu = (FrameLayout)findViewById(R.id.menu_wrap);
	    mWrap = (FrameLayout)findViewById(R.id.layout_wrap);
	    mMenuAnimIn = new SPMainMenuInAnim(mMenuAnimTask, mWrap, mMetrics.density, this, SPMainMenu.MAIN_MENU_LAYOUT_OFFSET);
	    mMenuAnimOut = new SPMainMenuOutAnim(mMenuAnimTask, mWrap, mMetrics.density, this, SPMainMenu.MAIN_MENU_LAYOUT_OFFSET);

		mMenuButton = (ImageView)findViewById(R.id.menu_button);
		mMenuButton.setOnClickListener(this);
		mMachineSettingsButton = (ImageView)findViewById(R.id.machine_settings_button);
		mMachineSettingsButton.setOnClickListener(this);
		mSubscribeButton = (Button)findViewById(R.id.subscribe_button);
		mSubscribeButton.setOnClickListener(this);
		
		// this section manages setting up the searchview apearance
		SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
		mSearchView = (SearchView)findViewById(R.id.library_search_bar);
		mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		
		mSearchView.setIconifiedByDefault(false);
		mSearchView.setBackground(getResources().getDrawable(R.drawable.search_bkgd));
		ImageView imageView = (ImageView)mSearchView.findViewById(getResources().getIdentifier("android:id/search_mag_icon", null, null));
		imageView.setColorFilter(Color.WHITE);
		
		Cursor roasterCursor = getContentResolver().query(
			Provider.ROASTER_CONTENT_URI,
			RoasterTable.ALL_COLUMNS,
			null,
			null,
			RoasterTable.USERNAME + " COLLATE NOCASE");
		roasterCursor.moveToFirst();
		mRoasterAdapter = new SPLibraryRoasterCursorAdapter(
				this,
				R.layout.roaster_list_textview,
				roasterCursor,
				new String[]{RoasterTable.USERNAME, RoasterTable.ID, RoasterTable.SUBSCRIBED_TO},
				new int[] {});
		mRecipeList = (ListView)findViewById(R.id.library_list_recipes);
		mRecipeList.setAdapter(mAdapter);
		mSearchList = (ListView)findViewById(R.id.search_list);
		mSearchList.setAdapter(mRoasterAdapter);
		mSearchList.setOnItemClickListener(this);
		mRecipeList.setOnItemClickListener(this);
		mRecipeList.setOnItemLongClickListener(this);
		
		mSelectedRoasterName = "";
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}
	
	public void setShowingMenu(boolean is) {
		if (mShowingMenu == is) return;
		mShowingMenu = is;
	}
	
	public void showMenu() {
		mMenuAnimOut.reset();
		mMenu.setVisibility(View.VISIBLE);
		mMenuAnimIn.reset();
		mMenuAnimTask = new TimerTask() {
			public void run() {
				runOnUiThread(mMenuAnimIn);
			}
		};
		mMenuAnimIn.setTask(mMenuAnimTask);
		mMenuAnimTimer.schedule(mMenuAnimTask, 0, SPMainMenu.ANIMATION_FRAME_DELAY);
	}
	
	public void hideMenu() {
		mMenuAnimIn.reset();
		mMenuAnimOut.reset();
		mMenuAnimTask = new TimerTask() {
			public void run() {
				runOnUiThread(mMenuAnimOut);
			}
		};
		mMenuAnimOut.setTask(mMenuAnimTask);
		mMenuAnimTimer.schedule(mMenuAnimTask, 0, SPMainMenu.ANIMATION_FRAME_DELAY);
		mMenu.setVisibility(View.GONE);
	}
	
	

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent(intent);
	}

	private Cursor doSearch(Context context, String query) {
		Cursor roasterCursor = getContentResolver().query(
			Provider.ROASTER_CONTENT_URI,
			RoasterTable.ALL_COLUMNS,
			RoasterTable.USERNAME + " LIKE ?",
			new String[] {query + "%"},
			RoasterTable.USERNAME + " COLLATE NOCASE");
		
		return roasterCursor;
	}

	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY); //SPLog.debug("roaster query string: " + query);
			
			Cursor searchResult = doSearch(this.getApplicationContext(), query);
			if (query == null || (query != null && query.equals(""))) {
				Cursor roasterCursor = getContentResolver().query(
					Provider.ROASTER_CONTENT_URI,
					RoasterTable.ALL_COLUMNS,
					null,
					null,
					RoasterTable.USERNAME + " COLLATE NOCASE");
				roasterCursor.moveToFirst();
				searchResult = roasterCursor;
			}
			SPLibraryRoasterCursorAdapter cAdapter = new SPLibraryRoasterCursorAdapter(
					this,
					R.layout.roaster_list_textview,
					searchResult,
					RoasterTable.ALL_COLUMNS,
					new int[] {});      
			mSearchList.setAdapter(cAdapter);
		} else {
			
		}
	}

	// OnClickListener
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.menu_button:
			mSearchView.clearFocus();
			if (mShowingMenu) {
				hideMenu();
			} else {
				showMenu();
			}
			break;
		case R.id.machine_settings_button:
			Intent intent = new Intent(this, SPFavoritesActivity.class);
			startActivity(intent);
			break;
		case R.id.subscribe_button:
			subscribeAction();
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> item, View view, int position, long arg3) {
		
		
		switch (item.getId()) {
		case R.id.search_list:
			// this piece here is to test functionality it should be getting the
			// values by the roaster username
			
			Cursor roasterCursor = (Cursor)item.getAdapter().getItem(position);
			String username = roasterCursor.getString(roasterCursor.getColumnIndex(RoasterTable.USERNAME));
			mSelectedRoasterName = username;
			mSelectedRoaster = roasterCursor.getLong(roasterCursor.getColumnIndex(RoasterTable.STEAMPUNK_ID));
			boolean subscribedTo = roasterCursor.getInt(roasterCursor.getColumnIndex(RoasterTable.SUBSCRIBED_TO)) == 1 ? true : false;
			
			TextView roasterTitle=(TextView)findViewById(R.id.roaster_name);
			roasterTitle.setText(username);
			Cursor roastersRecipes=getContentResolver().query(
					Provider.RECIPE_CONTENT_URI,
					RecipeTable.ALL_COLUMNS,
					RecipeTable.STEAMPUNK_USER_ID + "=?",
					new String[] {Long.toString(mSelectedRoaster)},
					RecipeTable.NAME + Constants.ASCENDING);
			roastersRecipes.moveToFirst();
			
			if (roastersRecipes.getCount() == 0) {
					
				if (!subscribedTo) {
					System.out.println(username);
					mRecipeList.setVisibility(View.GONE);
					mSubscribeButton.setVisibility(View.VISIBLE);
					SPLibraryCursorAdapter adapter = new SPLibraryCursorAdapter(
							this,
							R.drawable.library_recipe_name_view,
							roastersRecipes,
							new String[] {RecipeTable.ID, RecipeTable.TYPE, RecipeTable.NAME},
							new int[] {android.R.id.text1},
							new TreeSet<Long>());
						
					mRecipeList.setAdapter(adapter);
					break;
				} else {
					mRecipeList.setVisibility(View.GONE);
					mSubscribeButton.setVisibility(View.GONE);
					break;
				}
			}
			mRecipeList.setVisibility(View.VISIBLE);
			mSubscribeButton.setVisibility(View.GONE);
			String userType = SteampunkUtils
					.getCurrentSteampunkUserType(getApplicationContext());
			long userId = SteampunkUtils
					.getCurrentSteampunkUserId(getApplicationContext());

			String thisUsersName = SPModel.getInstance(this).getUser().getName();
			SPUser currentSPUser = new SPUser(userId,thisUsersName,userType); //we may need access to the name
			
			Cursor favoritesCursor = getContentResolver().query(Provider.FAVORITE_CONTENT_URI, FavoriteTable.ALL_COLUMNS, null, null, null);
			favoritesCursor.moveToFirst();
			TreeSet<Long> favorites = new TreeSet<Long>();
			
			for (int i = 0; i < favoritesCursor.getCount(); i++){
				long recipeId=favoritesCursor.getLong(favoritesCursor.getColumnIndex(FavoriteTable.RECIPE_ID));
				favorites.add(recipeId);
				favoritesCursor.moveToNext();
			}
			favoritesCursor.close();
			
			SPLibraryCursorAdapter adapter = new SPLibraryCursorAdapter(
					this,
					R.drawable.library_recipe_name_view,
					roastersRecipes,
					new String[] {RecipeTable.ID, RecipeTable.TYPE, RecipeTable.NAME},
					new int[] {android.R.id.text1},
					favorites);
			// TODO set adapter to recipes from the selected roaster or a subscribe button if not subscribed still a work in progress
			
			mRecipeList.setAdapter(adapter);
			
			break;
		case R.id.library_list_recipes:
			SPLibraryCursorAdapter recipeListAdapter = (SPLibraryCursorAdapter) item.getAdapter();
			recipeListAdapter.toggleFavorite(position);
			recipeListAdapter.notifyDataSetChanged();
			break;
		}
	}

	private void subscribeAction() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
		builder.setTitle(getResources().getString(R.string.subscribe_action_dialog_title));
		String msg = getResources().getString(R.string.subscribe_action_dialog_body) +
				mSelectedRoasterName +
				getResources().getString(R.string.subscribe_action_dialog_body_end);
		builder.setMessage(msg);
		builder.setInverseBackgroundForced(true);
		final SPActivity me = this;
		
		builder.setPositiveButton(getResources().getString(R.string.ok_capitalized), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				DefaultPersistenceServiceHelper.getInstance(me).subscribeToRoaster(mSelectedRoaster);
				dialog.dismiss();
			}
		});
		
		builder.setNegativeButton(getResources().getString(R.string.cancel_capitalized), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		Dialog dialog = builder.create();
		dialog.show();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> item, View arg1, int position, long arg3) {
		SPLibraryCursorAdapter recipeListAdapter=(SPLibraryCursorAdapter) item.getAdapter();
		Cursor recipeCursor = (Cursor) recipeListAdapter.getItem(position);
		SPRecipe recipe = new SPRecipe(recipeCursor.getLong(recipeCursor.getColumnIndex(RecipeTable.ID)),this);
		mMyRecipesModel.setCurrentlyEditedRecipe(recipe);
		long userId = SteampunkUtils.getCurrentSteampunkUserId(this.getApplicationContext());
		String userType = SteampunkUtils.getCurrentSteampunkUserType(this.getApplicationContext());
		String username = AccountSettings.getAccountSettingsFromSharedPreferences(this).getUsername();
		SPUser user = new SPUser(userId,username,userType); //this needs to be updated to get the username of the recipe createor not the user in next version
		mMyRecipesModel.setUser(user);
		
		if (recipeCursor.getInt(recipeCursor.getColumnIndex(RecipeTable.TYPE)) == Constants.RECIPE_TYPE_COFFEE) {
			Intent intent = new Intent();
			intent.setClass(this, SPRecipeEditorActivity.class);
			startActivity(intent);
		} else {
			Intent intent = new Intent();
			intent.setClass(this, SPRecipeEditorActivity.class);
			startActivity(intent);
		}
		return true;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		//mAdapter.notifyDataSetChanged();
//		runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				updateRoasterRecipes(mSelectedRoaster);
//			}
//		});
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (!mShowingMenu) {
			mMenu.setVisibility(View.GONE);
		}
		updateRoasterRecipes(mSelectedRoaster);
	}
	
	public void onEvent(DefaultPersistenceServiceHelperEvents.SubscribeEvent sub) {
		if (sub.wasSuccessful()) {
			final String message = getResources().getString(R.string.subscription_successful_message);
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
					toast.show();
				}
			});
		} else {
			final String message = getResources().getString(R.string.subscription_failed_message);
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
					toast.show();
				}
			});
		}
	}

	@Override
	public void needToBlank() {
		if (mShowingMenu) {
			hideMenu();
		}
	}
}
