package com.alphadominche.steampunkhmi.database;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.alphadominche.steampunkhmi.SPLog;
import com.alphadominche.steampunkhmi.database.tables.AgitationCycleTable;
import com.alphadominche.steampunkhmi.database.tables.FavoriteTable;
import com.alphadominche.steampunkhmi.database.tables.LogTable;
import com.alphadominche.steampunkhmi.database.tables.PersistenceQueueTable;
import com.alphadominche.steampunkhmi.database.tables.RecipeTable;
import com.alphadominche.steampunkhmi.database.tables.RemoteIdMappingTable;
import com.alphadominche.steampunkhmi.database.tables.RoasterTable;
import com.alphadominche.steampunkhmi.database.tables.StackTable;
import com.alphadominche.steampunkhmi.restclient.contentprocessor.DefaultContentProcessor;
import com.alphadominche.steampunkhmi.restclient.networkclient.RemoteClasses.UserIdMapping;
import com.alphadominche.steampunkhmi.restclient.persistenceservicehelper.DefaultPersistenceServiceHelperEvents;
import com.alphadominche.steampunkhmi.utils.Constants;
import de.greenrobot.event.EventBus;

/**
 * This database class extends the SQLiteOpenHelper
 * A database file is created: mdsdacpdatabase.db
 * <p/>
 * It is possible to implement an own mechanism to store data on database updates:
 * Write your code inside the defined block inside the "onUpgrade" method!
 * <p/>
 * More details about sqlite databases in android:
 *
 * @author MDSDACP Team - goetzfred@fh-bingen.de
 * @date 2014.05.09
 * @see <a href="http://developer.android.com/guide/topics/data/data-storage.html#db">Tutorial</a>
 * @see <a href="http://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper.html">Reference</a>
 * <p/>
 * Generated Class. Do not modify!
 */
public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "mdsdacpdatabase.db";
    private static final int DATABASE_VERSION = 31;

    private Context mContext;

    public Database(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        mContext = context;
    }

    @Override
    public final void onCreate(final SQLiteDatabase db) {
        db.execSQL(LogTable.SQL_CREATE);
        db.execSQL(RecipeTable.SQL_CREATE);
        db.execSQL(StackTable.SQL_CREATE);
        db.execSQL(AgitationCycleTable.SQL_CREATE);
        db.execSQL(RemoteIdMappingTable.SQL_CREATE);
        db.execSQL(FavoriteTable.SQL_CREATE);
        db.execSQL(RoasterTable.SQL_CREATE);
        db.execSQL(PersistenceQueueTable.SQL_CREATE);
    }

    @Override
    public final void onUpgrade(final SQLiteDatabase db, final int oldVersion,
                                final int newVersion) {
        /*PROTECTED REGION ID(DatabaseUpdate) ENABLED START*/

        // This is all in here so that it doesn't get overidden by the content
        // generator tool

        class Migration {
            public void forwards(SQLiteDatabase db) {
            }
        }

        Migration[] migrations = new Migration[]{null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null,
                null, new Migration() {
            public void forwards(SQLiteDatabase db) {
            }
        }, new Migration() {
            public void forwards(SQLiteDatabase db) {
            }
        }, new Migration() {
            public void forwards(SQLiteDatabase db) {
                // Rename the roaster field
                db.beginTransaction();
                try {
                    db.execSQL("ALTER TABLE recipe RENAME TO recipe_backup");
                    db.execSQL("CREATE TABLE recipe ( _id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, "
                            + "type INTEGER, steampunk_user_id INTEGER, published NUMERIC, grams REAL, teaspoons "
                            + "REAL, filter INTEGER, grind REAL, transaction_state TEXT, transaction_result TEXT, logid INTEGER )");
                    db.execSQL("INSERT INTO recipe(_id, name, type, steampunk_user_id, "
                            + "published, grams, teaspoons, filter, grind, transaction_state, "
                            + "transaction_result, logid) SELECT _id, name, type, roaster, "
                            + "published, grams, teaspoons, filter, grind, transaction_state, "
                            + "transaction_result, logid FROM recipe_backup");
                    db.execSQL("DROP TABLE recipe_backup");
                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    SPLog.debug("got SQLException in migration 19: "
                            + e.toString());
                } finally {
                    db.endTransaction();
                }
            }
        }, new Migration() {
            public void forwards(SQLiteDatabase db) {
                // Remove the logid column
                db.beginTransaction();
                try {
                    db.execSQL("ALTER TABLE recipe RENAME TO recipe_backup");
                    db.execSQL("CREATE TABLE recipe ( _id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, "
                            + "type INTEGER, steampunk_user_id INTEGER, published NUMERIC, grams REAL, teaspoons "
                            + "REAL, filter INTEGER, grind REAL, transaction_state TEXT, transaction_result TEXT)");
                    db.execSQL("INSERT INTO recipe(_id, name, type, steampunk_user_id, "
                            + "published, grams, teaspoons, filter, grind, transaction_state, "
                            + "transaction_result) SELECT _id, name, type, steampunk_user_id, "
                            + "published, grams, teaspoons, filter, grind, transaction_state, "
                            + "transaction_result FROM recipe_backup");
                    db.execSQL("DROP TABLE recipe_backup");
                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    SPLog.debug("got SQLException in migration 20: "
                            + e.toString());
                } finally {
                    db.endTransaction();
                }
            }
        }, new Migration() {
            public void forwards(SQLiteDatabase db) {

                db.beginTransaction();
                try {
                    db.execSQL("ALTER TABLE recipe ADD COLUMN local_id INTEGER DEFAULT -1");
                    db.execSQL("ALTER TABLE recipe ADD COLUMN local_mac_address TEXT DEFAULT ''");
                    db.execSQL("ALTER TABLE stack ADD COLUMN local_id INTEGER DEFAULT -1");
                    db.execSQL("ALTER TABLE stack ADD COLUMN local_mac_address TEXT DEFAULT ''");
                    db.execSQL("ALTER TABLE agitationcycle ADD COLUMN local_id INTEGER DEFAULT -1");
                    db.execSQL("ALTER TABLE agitationcycle ADD COLUMN local_mac_address TEXT DEFAULT ''");
                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    SPLog.debug("got SQLException in migration 21: "
                            + e.toString());
                } finally {
                    db.endTransaction();
                }
            }
        }, new Migration() {
            public void forwards(SQLiteDatabase db) {

                db.beginTransaction();
                try {
                    db.execSQL("ALTER TABLE recipe RENAME TO recipe_backup");
                    db.execSQL("CREATE TABLE recipe ( _id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, "
                            + "type INTEGER, steampunk_user_id INTEGER, published NUMERIC, grams REAL, teaspoons "
                            + "REAL, filter INTEGER, grind REAL, transaction_state TEXT, transaction_record TEXT,local_id INTEGER )");
                    db.execSQL("INSERT INTO recipe(_id, name, type, steampunk_user_id, "
                            + "published, grams, teaspoons, filter, grind, transaction_state, "
                            + "transaction_record,local_id) SELECT _id, name, type, steampunk_user_id, "
                            + "published, grams, teaspoons, filter, grind, transaction_state, "
                            + "transaction_result,local_id FROM recipe_backup");
                    db.execSQL("DROP TABLE recipe_backup");
                    db.execSQL("ALTER TABLE stack RENAME TO stack_backup");
                    db.execSQL("CREATE TABLE stack( _id INTEGER PRIMARY KEY AUTOINCREMENT, stack_order INTEGER, "
                            + "volume REAL, start_time INTEGER, duration INTEGER, temperature REAL, vacuum_break REAL, pull_down_time "
                            + "INTEGER, transaction_state TEXT, transaction_result TEXT,recipe_id INTEGER, local_id INTEGER )");
                    db.execSQL("INSERT INTO stack(_id, stack_order, volume, start_time, duration, "
                            + "temperature, vacuum_break, pull_down_time,  transaction_state, "
                            + "transaction_result,recipe_id, local_id) SELECT _id, stack_order, volume, start_time, duration, "
                            + "temperature, vacuum_break, pull_down_time,  transaction_state, "
                            + "transaction_result,recipeid,local_id FROM stack_backup");
                    db.execSQL("DROP TABLE stack_backup");
                    db.execSQL("ALTER TABLE agitationcycle RENAME TO agitationcycle_backup");
                    db.execSQL("CREATE TABLE agitationcycle( _id INTEGER PRIMARY KEY AUTOINCREMENT, start_time INTEGER, "
                            + "duration INTEGER, transaction_state TEXT, transaction_result TEXT, stack_id INTEGER, "
                            + "local_id INTEGER)");
                    db.execSQL("INSERT INTO agitationcycle(_id, start_time, duration, transaction_state, "
                            + "transaction_result,stack_id, local_id) SELECT _id, start_time, duration, transaction_state, "
                            + "transaction_result,stackid, local_id FROM agitationcycle_backup");
                    db.execSQL("DROP TABLE agitationcycle_backup");
                    db.execSQL("CREATE TABLE roaster( _id INTEGER PRIMARY KEY AUTOINCREMENT, first_name TEXT, "
                            + "last_name TEXT, username TEXT, steampunk_id INTEGER)");
                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    SPLog.debug("got SQLException in migration 22: "
                            + e.toString());
                } finally {
                    db.endTransaction();
                }
            }
        }, new Migration() {
            public void forwards(SQLiteDatabase db) {
                // created perisistence queue
                db.beginTransaction();
                try {
                    db.execSQL("CREATE TABLE persistencequeue( _id INTEGER PRIMARY KEY AUTOINCREMENT,backpointers TEXT, request_count INTEGER, "
                            + "state TEXT, request_type TEXT,tbl_name TEXT, obj_id INTEGER)");
                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    SPLog.debug("got SQLException in migration 23: "
                            + e.toString());
                } finally {
                    db.endTransaction();
                }
            }
        }, new Migration() {
            public void forwards(SQLiteDatabase db) {
                db.beginTransaction();

                // Get mapping to update UserId to SteampunkUserId
                DefaultContentProcessor contentProcessor = DefaultContentProcessor
                        .getInstance(mContext, null);
                List<UserIdMapping> userMapping = contentProcessor
                        .getUserMapping();

                try {
                    // Update Recipe ID
                    Cursor recipeCursor = db.query(
                            RecipeTable.TABLE_NAME,
                            RecipeTable.ALL_COLUMNS, null, null, null,
                            null, null);

                    if (recipeCursor.moveToFirst()) {
                        int recipeIndex = recipeCursor
                                .getColumnIndexOrThrow(RecipeTable.ID);
                        int userIndex = recipeCursor
                                .getColumnIndexOrThrow(RecipeTable.STEAMPUNK_USER_ID);

                        while (!recipeCursor.isAfterLast()) {
                            long recipe_id = recipeCursor
                                    .getLong(recipeIndex);
                            long newId = recipe_id
                                    + Constants.LOCAL_ID_MIN;

                            long user_id = recipeCursor
                                    .getLong(userIndex);

                            ContentValues updatedRecipeContentValues = new ContentValues();
                            updatedRecipeContentValues.put(
                                    RecipeTable.ID, newId);

                            // Get steampunk_id by user_id
                            for (int i = 0; i < userMapping.size(); i++) {
                                if (userMapping.get(i).id == user_id) {
                                    updatedRecipeContentValues
                                            .put(RecipeTable.STEAMPUNK_USER_ID,
                                                    userMapping.get(i).steampunkuser);
                                    break;
                                }
                            }

                            String selectionClause = RecipeTable.WHERE_ID_EQUALS;
                            String[] selectionArgs = {Long
                                    .toString(recipe_id)};

                            db.update(RecipeTable.TABLE_NAME,
                                    updatedRecipeContentValues,
                                    selectionClause, selectionArgs);
                            recipeCursor.moveToNext();
                        }
                    }
                    recipeCursor.close();
                    System.out.println("Recipes complete");

                    // Update Stack ID and RECIPE_ID
                    Cursor stackCursor = db.query(
                            StackTable.TABLE_NAME, new String[]{
                                    StackTable.ID,
                                    StackTable.RECIPE_ID,
                                    StackTable.TEMPERATURE,
                                    StackTable.VOLUME}, null, null,
                            null, null, null);

                    if (stackCursor.moveToFirst()) {
                        int stackIndex = stackCursor
                                .getColumnIndexOrThrow(StackTable.ID);
                        int recipeIndex = stackCursor
                                .getColumnIndexOrThrow(StackTable.RECIPE_ID);
                        int tempIndex = stackCursor
                                .getColumnIndexOrThrow(StackTable.TEMPERATURE);
                        int volumeIndex = stackCursor
                                .getColumnIndex(StackTable.VOLUME);

                        while (!stackCursor.isAfterLast()) {
                            long stack_id = stackCursor
                                    .getLong(stackIndex);
                            long recipe_id = stackCursor
                                    .getLong(recipeIndex);
                            double stackTemperature = stackCursor
                                    .getDouble(tempIndex);
                            double stackVolume = stackCursor
                                    .getDouble(volumeIndex);
                            System.out
                                    .println("stackTempInLastMigration"
                                            + stackCursor
                                            .getString(stackCursor
                                                    .getColumnIndex(StackTable.TEMPERATURE)));
                            SPLog.debug("stackTempBefore: "
                                    + stackCursor.getDouble(stackCursor
                                    .getColumnIndex(StackTable.TEMPERATURE)));
                            long newStackId = stack_id
                                    + Constants.LOCAL_ID_MIN;
                            long newRecipeId = recipe_id
                                    + Constants.LOCAL_ID_MIN;

                            ContentValues updatedStackContentValues = new ContentValues();
                            updatedStackContentValues.put(
                                    StackTable.ID, newStackId);
                            updatedStackContentValues.put(
                                    StackTable.LOCAL_ID, newStackId);
                            updatedStackContentValues.put(
                                    StackTable.RECIPE_ID, newRecipeId);
                            updatedStackContentValues
                                    .put(StackTable.TEMPERATURE,
                                            Math.round(stackTemperature * 100) / 100.0);
                            updatedStackContentValues
                                    .put(StackTable.VOLUME,
                                            Math.round(stackVolume * 100) / 100.0);
                            String selectionClause = StackTable.WHERE_ID_EQUALS;
                            String[] selectionArgs = {Long
                                    .toString(stack_id)};
                            db.update(StackTable.TABLE_NAME,
                                    updatedStackContentValues,
                                    selectionClause, selectionArgs);
                            stackCursor.moveToNext();
                        }
                    }
                    stackCursor.close();
                    System.out.println("Stacks complete");

                    // Update AgitationCycle ID and STACK_ID
                    Cursor agitationCursor = db.query(
                            AgitationCycleTable.TABLE_NAME,
                            new String[]{AgitationCycleTable.ID,
                                    AgitationCycleTable.STACK_ID},
                            null, null, null, null, null);

                    if (agitationCursor.moveToFirst()) {
                        int agitationIndex = agitationCursor
                                .getColumnIndexOrThrow(AgitationCycleTable.ID);
                        int stackIndex = agitationCursor
                                .getColumnIndexOrThrow(AgitationCycleTable.STACK_ID);

                        while (!agitationCursor.isAfterLast()) {
                            long agitation_id = agitationCursor
                                    .getLong(agitationIndex);
                            long stack_id = agitationCursor
                                    .getLong(stackIndex);

                            long newAgitationId = agitation_id
                                    + Constants.LOCAL_ID_MIN;
                            long newStackId = stack_id
                                    + Constants.LOCAL_ID_MIN;

                            ContentValues updatedAgitationContentValues = new ContentValues();
                            updatedAgitationContentValues.put(
                                    AgitationCycleTable.ID,
                                    newAgitationId);
                            updatedAgitationContentValues.put(
                                    AgitationCycleTable.LOCAL_ID,
                                    newAgitationId);
                            updatedAgitationContentValues.put(
                                    AgitationCycleTable.STACK_ID,
                                    newStackId);
                            String selectionClause = AgitationCycleTable.WHERE_ID_EQUALS;
                            String[] selectionArgs = {Long
                                    .toString(agitation_id)};
                            db.update(AgitationCycleTable.TABLE_NAME,
                                    updatedAgitationContentValues,
                                    selectionClause, selectionArgs);
                            agitationCursor.moveToNext();
                        }
                    }
                    agitationCursor.close();
                    System.out.println("Agitations complete");

                    // Update Favorite ID and RECIPE_ID
                    Cursor favoriteCursor = db.query(
                            FavoriteTable.TABLE_NAME, new String[]{
                                    FavoriteTable.ID, "recipeid"},
                            null, null, null, null, null);

                    if (favoriteCursor.moveToFirst()) {
                        int favoriteIndex = favoriteCursor
                                .getColumnIndexOrThrow(FavoriteTable.ID);
                        int recipeIndex = favoriteCursor
                                .getColumnIndexOrThrow("recipeid");

                        while (!favoriteCursor.isAfterLast()) {
                            long favorite_id = favoriteCursor
                                    .getLong(favoriteIndex);
                            long recipe_id = favoriteCursor
                                    .getLong(recipeIndex);

                            long newFavoriteId = favorite_id
                                    + Constants.LOCAL_ID_MIN;
                            long newRecipeId = recipe_id
                                    + Constants.LOCAL_ID_MIN;

                            ContentValues updatedFavoriteContentValues = new ContentValues();
                            updatedFavoriteContentValues.put(
                                    FavoriteTable.ID, newFavoriteId);
                            updatedFavoriteContentValues.put(
                                    "recipeid", newRecipeId);
                            String selectionClause = FavoriteTable.WHERE_ID_EQUALS;
                            String[] selectionArgs = {Long
                                    .toString(favorite_id)};
                            db.update(FavoriteTable.TABLE_NAME,
                                    updatedFavoriteContentValues,
                                    selectionClause, selectionArgs);
                            favoriteCursor.moveToNext();
                        }
                    }
                    favoriteCursor.close();

                    System.out.println("Favorites complete");

                    // renamed table name
                    db.execSQL("ALTER TABLE persistencequeue ADD COLUMN last_attempt INTEGER DEFAULT 1381881600");

                    // update recipeid to recipe_id in log and favorite

                    db.execSQL("ALTER TABLE favorite RENAME TO favorite_backup");
                    db.execSQL("CREATE TABLE favorite( _id INTEGER PRIMARY KEY AUTOINCREMENT, user INTEGER, recipe_id INTEGER)");
                    db.execSQL("INSERT INTO favorite( _id, user, recipe_id) SELECT _id, user,"
                            + "recipeid FROM favorite_backup");
                    db.execSQL("DROP TABLE favorite_backup");

                    db.execSQL("ALTER TABLE log RENAME TO log_backup");
                    db.execSQL("CREATE TABLE log( _id INTEGER PRIMARY KEY AUTOINCREMENT, machine TEXT, crucible INTEGER, date TEXT, severity INTEGER,type INTEGER, message TEXT, user INTEGER,transaction_state TEXT,transaction_result TEXT,recipe_id INTEGER)");
                    db.execSQL("INSERT INTO log( _id, machine, crucible, date, severity, type, message, user, transaction_state, transaction_result, recipe_id) SELECT _id, machine,"
                            + "crucible, date, severity, type, message, user, transaction_state, transaction_result,recipeid FROM log_backup");
                    db.execSQL("DROP TABLE log_backup");
                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    SPLog.debug("got SQLException in migration 24: "
                            + e.toString());
                } finally {
                    db.endTransaction();
                }

                SPLog.debug("eventbus upgrading called");

                EventBus.getDefault()
                        .post(new DefaultPersistenceServiceHelperEvents.Upgrading());

            }

        }, new Migration() {
            public void forwards(SQLiteDatabase db) {

                db.beginTransaction();
                try {
                    db.execSQL("ALTER TABLE roaster ADD COLUMN subscribed_to INTEGER DEFAULT 1");
                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    SPLog.debug("got SQLException in migration 25: "
                            + e.toString());
                } finally {
                    db.endTransaction();
                }
            }
        }, new Migration() {
            public void forwards(SQLiteDatabase db) {
            }
        }, new Migration() {
            public void forwards(SQLiteDatabase db) {
            }
        }, new Migration() {
            public void forwards(SQLiteDatabase db) {
                SPLog.debug("migration 28");
                db.beginTransaction();
                try {
                    db.execSQL("ALTER TABLE recipe ADD COLUMN stacks TEXT DEFAULT ''");
                    db.execSQL("ALTER TABLE recipe ADD COLUMN uuid TEXT DEFAULT ''");
                    db.execSQL("ALTER TABLE favorite ADD COLUMN uuid TEXT DEFAULT ''");
                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    SPLog.debug("got SQLException in migration 28: "
                            + e.toString());
                } finally {
                    db.endTransaction();
                }
            }
        }, new Migration() {
            public void forwards(SQLiteDatabase db) {
            }
        }, new Migration() {
            public void forwards(SQLiteDatabase db) {
                SPLog.debug("migration 30");
                db.beginTransaction();
                try {
                    db.execSQL("ALTER TABLE persistencequeue ADD COLUMN obj_uuid TEXT DEFAULT ''");
                    db.execSQL("ALTER TABLE favorite ADD COLUMN recipe_uuid TEXT DEFAULT ''");
                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    SPLog.debug("got SQLException in migration 30: "
                            + e.toString());
                } finally {
                    db.endTransaction();
                }
            }
//				}, new Migration() {
//					public void forwards(SQLiteDatabase db) { SPLog.debug("migration 31");
//						db.beginTransaction();
//						try {
//							db.execSQL("ALTER TABLE favorite ADD COLUMN recipe_uuid TEXT DEFAULT ''");
//							db.setTransactionSuccessful();
//						} catch (SQLException e) {
//							SPLog.debug("got SQLException in migration 31: "
//									+ e.toString());
//						} finally {
//							db.endTransaction();
//						}
//					}
        }};

        for (int i = oldVersion; i < newVersion; i++) {
            int migrationIndex = i - 1;
            if (migrations[migrationIndex] != null) {
                migrations[migrationIndex].forwards(db);
            } else {
                System.out.println("WHEN ARE YOU DELETING EVERYTHING");
                onUpgradeDropTablesAndCreate(db);
            }
        }

		/*PROTECTED REGION END*/
    }

    /**
     * This basic upgrade functionality will destroy all old data on upgrade
     */
    private final void onUpgradeDropTablesAndCreate(final SQLiteDatabase db) {
        db.execSQL(LogTable.SQL_DROP);
        db.execSQL(RecipeTable.SQL_DROP);
        db.execSQL(StackTable.SQL_DROP);
        db.execSQL(AgitationCycleTable.SQL_DROP);
        db.execSQL(RemoteIdMappingTable.SQL_DROP);
        db.execSQL(FavoriteTable.SQL_DROP);
        db.execSQL(RoasterTable.SQL_DROP);
        db.execSQL(PersistenceQueueTable.SQL_DROP);
        onCreate(db);
    }
}
