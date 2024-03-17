package com.alphadominche.steampunkhmi.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;
import com.alphadominche.steampunkhmi.database.Database;
import com.alphadominche.steampunkhmi.database.tables.AgitationCycleTable;
import com.alphadominche.steampunkhmi.database.tables.FavoriteTable;
import com.alphadominche.steampunkhmi.database.tables.LogTable;
import com.alphadominche.steampunkhmi.database.tables.PersistenceQueueTable;
import com.alphadominche.steampunkhmi.database.tables.RecipeTable;
import com.alphadominche.steampunkhmi.database.tables.RemoteIdMappingTable;
import com.alphadominche.steampunkhmi.database.tables.RoasterTable;
import com.alphadominche.steampunkhmi.database.tables.StackTable;

/**
 * Content provider implementation
 * The authority of the content provider is: content://com.alphadominche.steampunkhmi.provider.HomeActivity
 * <p/>
 * More information about content providers:
 *
 * @author MDSDACP Team - goetzfred@fh-bingen.de
 * @date 2014.05.09
 * @see <a href="http://developer.android.com/reference/android/content/ContentProvider.html">Reference</a>
 * @see <a href="http://developer.android.com/guide/topics/providers/content-providers.html">Tutorial</a>
 * @see <a href="http://developer.android.com/guide/topics/testing/contentprovider_testing.html">Content Provider Testing</a>
 * <p/>
 * Generated Class. Do not modify!
 */
public class Provider extends ContentProvider {
    private static final String TAG = "com.alphadominche.steampunkhmi.contentprovider.Provider";

    public static final String AUTHORITY = "com.alphadominche.steampunkhmi.provider.HomeActivity";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    public static final Uri LOG_CONTENT_URI = Uri.withAppendedPath(
            Provider.AUTHORITY_URI, LogContent.CONTENT_PATH);

    public static final Uri RECIPE_CONTENT_URI = Uri.withAppendedPath(
            Provider.AUTHORITY_URI, RecipeContent.CONTENT_PATH);

    public static final Uri STACK_CONTENT_URI = Uri.withAppendedPath(
            Provider.AUTHORITY_URI, StackContent.CONTENT_PATH);

    public static final Uri AGITATIONCYCLE_CONTENT_URI = Uri.withAppendedPath(
            Provider.AUTHORITY_URI, AgitationCycleContent.CONTENT_PATH);

    public static final Uri REMOTEIDMAPPING_CONTENT_URI = Uri.withAppendedPath(
            Provider.AUTHORITY_URI, RemoteIdMappingContent.CONTENT_PATH);

    public static final Uri FAVORITE_CONTENT_URI = Uri.withAppendedPath(
            Provider.AUTHORITY_URI, FavoriteContent.CONTENT_PATH);

    public static final Uri ROASTER_CONTENT_URI = Uri.withAppendedPath(
            Provider.AUTHORITY_URI, RoasterContent.CONTENT_PATH);

    public static final Uri PERSISTENCEQUEUE_CONTENT_URI = Uri
            .withAppendedPath(Provider.AUTHORITY_URI,
                    PersistenceQueueContent.CONTENT_PATH);

    private static final UriMatcher URI_MATCHER;

    private Database db;

    private static final int LOG_DIR = 0;
    private static final int LOG_ID = 1;
    private static final int RECIPE_DIR = 2;
    private static final int RECIPE_ID = 3;
    private static final int STACK_DIR = 4;
    private static final int STACK_ID = 5;
    private static final int AGITATIONCYCLE_DIR = 6;
    private static final int AGITATIONCYCLE_ID = 7;
    private static final int REMOTEIDMAPPING_DIR = 8;
    private static final int REMOTEIDMAPPING_ID = 9;
    private static final int FAVORITE_DIR = 10;
    private static final int FAVORITE_ID = 11;
    private static final int ROASTER_DIR = 12;
    private static final int ROASTER_ID = 13;
    private static final int PERSISTENCEQUEUE_DIR = 14;
    private static final int PERSISTENCEQUEUE_ID = 15;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AUTHORITY, LogContent.CONTENT_PATH, LOG_DIR);
        URI_MATCHER.addURI(AUTHORITY, LogContent.CONTENT_PATH + "/#", LOG_ID);
        URI_MATCHER.addURI(AUTHORITY, RecipeContent.CONTENT_PATH, RECIPE_DIR);
        URI_MATCHER.addURI(AUTHORITY, RecipeContent.CONTENT_PATH + "/#",
                RECIPE_ID);
        URI_MATCHER.addURI(AUTHORITY, StackContent.CONTENT_PATH, STACK_DIR);
        URI_MATCHER.addURI(AUTHORITY, StackContent.CONTENT_PATH + "/#",
                STACK_ID);
        URI_MATCHER.addURI(AUTHORITY, AgitationCycleContent.CONTENT_PATH,
                AGITATIONCYCLE_DIR);
        URI_MATCHER.addURI(AUTHORITY,
                AgitationCycleContent.CONTENT_PATH + "/#", AGITATIONCYCLE_ID);
        URI_MATCHER.addURI(AUTHORITY, RemoteIdMappingContent.CONTENT_PATH,
                REMOTEIDMAPPING_DIR);
        URI_MATCHER.addURI(AUTHORITY, RemoteIdMappingContent.CONTENT_PATH
                + "/#", REMOTEIDMAPPING_ID);
        URI_MATCHER.addURI(AUTHORITY, FavoriteContent.CONTENT_PATH,
                FAVORITE_DIR);
        URI_MATCHER.addURI(AUTHORITY, FavoriteContent.CONTENT_PATH + "/#",
                FAVORITE_ID);
        URI_MATCHER.addURI(AUTHORITY, RoasterContent.CONTENT_PATH, ROASTER_DIR);
        URI_MATCHER.addURI(AUTHORITY, RoasterContent.CONTENT_PATH + "/#",
                ROASTER_ID);
        URI_MATCHER.addURI(AUTHORITY, PersistenceQueueContent.CONTENT_PATH,
                PERSISTENCEQUEUE_DIR);
        URI_MATCHER.addURI(AUTHORITY, PersistenceQueueContent.CONTENT_PATH
                + "/#", PERSISTENCEQUEUE_ID);
    }

    /**
     * Provides the content information of the LogTable.
     * <p/>
     * CONTENT_PATH: log (String)
     * CONTENT_TYPE: vnd.android.cursor.dir/vnd.mdsdacp.log (String)
     * CONTENT_ITEM_TYPE: vnd.android.cursor.item/vnd.mdsdacp.log (String)
     * ALL_COLUMNS: Provides the same information as LogTable.ALL_COLUMNS (String[])
     */
    public static final class LogContent implements BaseColumns {
        /**
         * Specifies the content path of the LogTable for the required uri
         * Exact URI: content://com.alphadominche.steampunkhmi.provider.HomeActivity/log
         */
        public static final String CONTENT_PATH = "log";

        /**
         * Specifies the type for the folder and the single item of the LogTable
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mdsdacp.log";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.mdsdacp.log";

        /**
         * Contains all columns of the LogTable
         */
        public static final String[] ALL_COLUMNS = LogTable.ALL_COLUMNS;
    }

    /**
     * Provides the content information of the RecipeTable.
     * <p/>
     * CONTENT_PATH: recipe (String)
     * CONTENT_TYPE: vnd.android.cursor.dir/vnd.mdsdacp.recipe (String)
     * CONTENT_ITEM_TYPE: vnd.android.cursor.item/vnd.mdsdacp.recipe (String)
     * ALL_COLUMNS: Provides the same information as RecipeTable.ALL_COLUMNS (String[])
     */
    public static final class RecipeContent implements BaseColumns {
        /**
         * Specifies the content path of the RecipeTable for the required uri
         * Exact URI: content://com.alphadominche.steampunkhmi.provider.HomeActivity/recipe
         */
        public static final String CONTENT_PATH = "recipe";

        /**
         * Specifies the type for the folder and the single item of the RecipeTable
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mdsdacp.recipe";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.mdsdacp.recipe";

        /**
         * Contains all columns of the RecipeTable
         */
        public static final String[] ALL_COLUMNS = RecipeTable.ALL_COLUMNS;
    }

    /**
     * Provides the content information of the StackTable.
     * <p/>
     * CONTENT_PATH: stack (String)
     * CONTENT_TYPE: vnd.android.cursor.dir/vnd.mdsdacp.stack (String)
     * CONTENT_ITEM_TYPE: vnd.android.cursor.item/vnd.mdsdacp.stack (String)
     * ALL_COLUMNS: Provides the same information as StackTable.ALL_COLUMNS (String[])
     */
    public static final class StackContent implements BaseColumns {
        /**
         * Specifies the content path of the StackTable for the required uri
         * Exact URI: content://com.alphadominche.steampunkhmi.provider.HomeActivity/stack
         */
        public static final String CONTENT_PATH = "stack";

        /**
         * Specifies the type for the folder and the single item of the StackTable
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mdsdacp.stack";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.mdsdacp.stack";

        /**
         * Contains all columns of the StackTable
         */
        public static final String[] ALL_COLUMNS = StackTable.ALL_COLUMNS;
    }

    /**
     * Provides the content information of the AgitationCycleTable.
     * <p/>
     * CONTENT_PATH: agitationcycle (String)
     * CONTENT_TYPE: vnd.android.cursor.dir/vnd.mdsdacp.agitationcycle (String)
     * CONTENT_ITEM_TYPE: vnd.android.cursor.item/vnd.mdsdacp.agitationcycle (String)
     * ALL_COLUMNS: Provides the same information as AgitationCycleTable.ALL_COLUMNS (String[])
     */
    public static final class AgitationCycleContent implements BaseColumns {
        /**
         * Specifies the content path of the AgitationCycleTable for the required uri
         * Exact URI: content://com.alphadominche.steampunkhmi.provider.HomeActivity/agitationcycle
         */
        public static final String CONTENT_PATH = "agitationcycle";

        /**
         * Specifies the type for the folder and the single item of the AgitationCycleTable
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mdsdacp.agitationcycle";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.mdsdacp.agitationcycle";

        /**
         * Contains all columns of the AgitationCycleTable
         */
        public static final String[] ALL_COLUMNS = AgitationCycleTable.ALL_COLUMNS;
    }

    /**
     * Provides the content information of the RemoteIdMappingTable.
     * <p/>
     * CONTENT_PATH: remoteidmapping (String)
     * CONTENT_TYPE: vnd.android.cursor.dir/vnd.mdsdacp.remoteidmapping (String)
     * CONTENT_ITEM_TYPE: vnd.android.cursor.item/vnd.mdsdacp.remoteidmapping (String)
     * ALL_COLUMNS: Provides the same information as RemoteIdMappingTable.ALL_COLUMNS (String[])
     */
    public static final class RemoteIdMappingContent implements BaseColumns {
        /**
         * Specifies the content path of the RemoteIdMappingTable for the required uri
         * Exact URI: content://com.alphadominche.steampunkhmi.provider.HomeActivity/remoteidmapping
         */
        public static final String CONTENT_PATH = "remoteidmapping";

        /**
         * Specifies the type for the folder and the single item of the RemoteIdMappingTable
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mdsdacp.remoteidmapping";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.mdsdacp.remoteidmapping";

        /**
         * Contains all columns of the RemoteIdMappingTable
         */
        public static final String[] ALL_COLUMNS = RemoteIdMappingTable.ALL_COLUMNS;
    }

    /**
     * Provides the content information of the FavoriteTable.
     * <p/>
     * CONTENT_PATH: favorite (String)
     * CONTENT_TYPE: vnd.android.cursor.dir/vnd.mdsdacp.favorite (String)
     * CONTENT_ITEM_TYPE: vnd.android.cursor.item/vnd.mdsdacp.favorite (String)
     * ALL_COLUMNS: Provides the same information as FavoriteTable.ALL_COLUMNS (String[])
     */
    public static final class FavoriteContent implements BaseColumns {
        /**
         * Specifies the content path of the FavoriteTable for the required uri
         * Exact URI: content://com.alphadominche.steampunkhmi.provider.HomeActivity/favorite
         */
        public static final String CONTENT_PATH = "favorite";

        /**
         * Specifies the type for the folder and the single item of the FavoriteTable
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mdsdacp.favorite";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.mdsdacp.favorite";

        /**
         * Contains all columns of the FavoriteTable
         */
        public static final String[] ALL_COLUMNS = FavoriteTable.ALL_COLUMNS;
    }

    /**
     * Provides the content information of the RoasterTable.
     * <p/>
     * CONTENT_PATH: roaster (String)
     * CONTENT_TYPE: vnd.android.cursor.dir/vnd.mdsdacp.roaster (String)
     * CONTENT_ITEM_TYPE: vnd.android.cursor.item/vnd.mdsdacp.roaster (String)
     * ALL_COLUMNS: Provides the same information as RoasterTable.ALL_COLUMNS (String[])
     */
    public static final class RoasterContent implements BaseColumns {
        /**
         * Specifies the content path of the RoasterTable for the required uri
         * Exact URI: content://com.alphadominche.steampunkhmi.provider.HomeActivity/roaster
         */
        public static final String CONTENT_PATH = "roaster";

        /**
         * Specifies the type for the folder and the single item of the RoasterTable
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mdsdacp.roaster";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.mdsdacp.roaster";

        /**
         * Contains all columns of the RoasterTable
         */
        public static final String[] ALL_COLUMNS = RoasterTable.ALL_COLUMNS;
    }

    /**
     * Provides the content information of the PersistenceQueueTable.
     * <p/>
     * CONTENT_PATH: persistencequeue (String)
     * CONTENT_TYPE: vnd.android.cursor.dir/vnd.mdsdacp.persistencequeue (String)
     * CONTENT_ITEM_TYPE: vnd.android.cursor.item/vnd.mdsdacp.persistencequeue (String)
     * ALL_COLUMNS: Provides the same information as PersistenceQueueTable.ALL_COLUMNS (String[])
     */
    public static final class PersistenceQueueContent implements BaseColumns {
        /**
         * Specifies the content path of the PersistenceQueueTable for the required uri
         * Exact URI: content://com.alphadominche.steampunkhmi.provider.HomeActivity/persistencequeue
         */
        public static final String CONTENT_PATH = "persistencequeue";

        /**
         * Specifies the type for the folder and the single item of the PersistenceQueueTable
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mdsdacp.persistencequeue";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.mdsdacp.persistencequeue";

        /**
         * Contains all columns of the PersistenceQueueTable
         */
        public static final String[] ALL_COLUMNS = PersistenceQueueTable.ALL_COLUMNS;
    }

    /**
     * Instantiate the database, when the content provider is created
     */
    @Override
    public final boolean onCreate() {
        db = new Database(getContext());
        return true;
    }

    /**
     * Providing information whether uri returns an item or an directory.
     *
     * @param uri from type Uri
     * @return content_type from type Content.CONTENT_TYPE or Content.CONTENT_ITEM_TYPE
     */
    @Override
    public final String getType(final Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case LOG_DIR:
                return LogContent.CONTENT_TYPE;
            case LOG_ID:
                return LogContent.CONTENT_ITEM_TYPE;
            case RECIPE_DIR:
                return RecipeContent.CONTENT_TYPE;
            case RECIPE_ID:
                return RecipeContent.CONTENT_ITEM_TYPE;
            case STACK_DIR:
                return StackContent.CONTENT_TYPE;
            case STACK_ID:
                return StackContent.CONTENT_ITEM_TYPE;
            case AGITATIONCYCLE_DIR:
                return AgitationCycleContent.CONTENT_TYPE;
            case AGITATIONCYCLE_ID:
                return AgitationCycleContent.CONTENT_ITEM_TYPE;
            case REMOTEIDMAPPING_DIR:
                return RemoteIdMappingContent.CONTENT_TYPE;
            case REMOTEIDMAPPING_ID:
                return RemoteIdMappingContent.CONTENT_ITEM_TYPE;
            case FAVORITE_DIR:
                return FavoriteContent.CONTENT_TYPE;
            case FAVORITE_ID:
                return FavoriteContent.CONTENT_ITEM_TYPE;
            case ROASTER_DIR:
                return RoasterContent.CONTENT_TYPE;
            case ROASTER_ID:
                return RoasterContent.CONTENT_ITEM_TYPE;
            case PERSISTENCEQUEUE_DIR:
                return PersistenceQueueContent.CONTENT_TYPE;
            case PERSISTENCEQUEUE_ID:
                return PersistenceQueueContent.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    /**
     * Insert given values to given uri. Uri has to be from type directory (see switch-cases).
     * Returns uri of inserted element.
     *
     * @param uri    from type Uri
     * @param values from type ContentValues
     * @return uri of inserted element from type Uri
     */
    @Override
    public final Uri insert(final Uri uri, final ContentValues values) {
        final SQLiteDatabase dbConnection = db.getWritableDatabase();

        try {
            dbConnection.beginTransaction();

            switch (URI_MATCHER.match(uri)) {
                case LOG_DIR:
                case LOG_ID:
                    final long logid = dbConnection.insertOrThrow(
                            LogTable.TABLE_NAME, null, values);
                    final Uri newLog = ContentUris.withAppendedId(
                            LOG_CONTENT_URI, logid);
                    getContext().getContentResolver()
                            .notifyChange(newLog, null);
                    dbConnection.setTransactionSuccessful();
                    return newLog;
                case RECIPE_DIR:
                case RECIPE_ID:
                    final long recipeid = dbConnection.insertOrThrow(
                            RecipeTable.TABLE_NAME, null, values);
                    final Uri newRecipe = ContentUris.withAppendedId(
                            RECIPE_CONTENT_URI, recipeid);
                    getContext().getContentResolver().notifyChange(newRecipe,
                            null);
                    dbConnection.setTransactionSuccessful();
                    return newRecipe;
                case STACK_DIR:
                case STACK_ID:
                    final long stackid = dbConnection.insertOrThrow(
                            StackTable.TABLE_NAME, null, values);
                    final Uri newStack = ContentUris.withAppendedId(
                            STACK_CONTENT_URI, stackid);
                    getContext().getContentResolver().notifyChange(newStack,
                            null);
                    dbConnection.setTransactionSuccessful();
                    return newStack;
                case AGITATIONCYCLE_DIR:
                case AGITATIONCYCLE_ID:
                    final long agitationcycleid = dbConnection.insertOrThrow(
                            AgitationCycleTable.TABLE_NAME, null, values);
                    final Uri newAgitationCycle = ContentUris.withAppendedId(
                            AGITATIONCYCLE_CONTENT_URI, agitationcycleid);
                    getContext().getContentResolver().notifyChange(
                            newAgitationCycle, null);
                    dbConnection.setTransactionSuccessful();
                    return newAgitationCycle;
                case REMOTEIDMAPPING_DIR:
                case REMOTEIDMAPPING_ID:
                    final long remoteidmappingid = dbConnection.insertOrThrow(
                            RemoteIdMappingTable.TABLE_NAME, null, values);
                    final Uri newRemoteIdMapping = ContentUris.withAppendedId(
                            REMOTEIDMAPPING_CONTENT_URI, remoteidmappingid);
                    getContext().getContentResolver().notifyChange(
                            newRemoteIdMapping, null);
                    dbConnection.setTransactionSuccessful();
                    return newRemoteIdMapping;
                case FAVORITE_DIR:
                case FAVORITE_ID:
                    final long favoriteid = dbConnection.insertOrThrow(
                            FavoriteTable.TABLE_NAME, null, values);
                    final Uri newFavorite = ContentUris.withAppendedId(
                            FAVORITE_CONTENT_URI, favoriteid);
                    getContext().getContentResolver().notifyChange(newFavorite,
                            null);
                    dbConnection.setTransactionSuccessful();
                    return newFavorite;
                case ROASTER_DIR:
                case ROASTER_ID:
                    final long roasterid = dbConnection.insertOrThrow(
                            RoasterTable.TABLE_NAME, null, values);
                    final Uri newRoaster = ContentUris.withAppendedId(
                            ROASTER_CONTENT_URI, roasterid);
                    getContext().getContentResolver().notifyChange(newRoaster,
                            null);
                    dbConnection.setTransactionSuccessful();
                    return newRoaster;
                case PERSISTENCEQUEUE_DIR:
                case PERSISTENCEQUEUE_ID:
                    final long persistencequeueid = dbConnection.insertOrThrow(
                            PersistenceQueueTable.TABLE_NAME, null, values);
                    final Uri newPersistenceQueue = ContentUris.withAppendedId(
                            PERSISTENCEQUEUE_CONTENT_URI, persistencequeueid);
                    getContext().getContentResolver().notifyChange(
                            newPersistenceQueue, null);
                    dbConnection.setTransactionSuccessful();
                    return newPersistenceQueue;
                default:
                    throw new IllegalArgumentException("Unsupported URI:" + uri);
            }
        } catch (Exception e) {
            Log.e(TAG, "Insert Exception", e);
        } finally {
            dbConnection.endTransaction();
        }

        return null;
    }

    /**
     * Updates given values of given uri, returning number of affected rows.
     *
     * @param uri           from type Uri
     * @param values        from type ContentValues
     * @param selection     from type String
     * @param selectionArgs from type String[]
     * @return number of affected rows from type int
     */
    @Override
    public final int update(final Uri uri, final ContentValues values,
                            final String selection, final String[] selectionArgs) {

        final SQLiteDatabase dbConnection = db.getWritableDatabase();
        int updateCount = 0;

        try {
            dbConnection.beginTransaction();

            switch (URI_MATCHER.match(uri)) {

                case LOG_DIR:
                    updateCount = dbConnection.update(LogTable.TABLE_NAME,
                            values, selection, selectionArgs);
                    dbConnection.setTransactionSuccessful();
                    break;
                case LOG_ID:
                    final Long logId = ContentUris.parseId(uri);
                    updateCount = dbConnection.update(
                            LogTable.TABLE_NAME,
                            values,
                            LogTable.ID
                                    + "="
                                    + logId
                                    + (TextUtils.isEmpty(selection)
                                    ? ""
                                    : " AND (" + selection + ")"),
                            selectionArgs);
                    dbConnection.setTransactionSuccessful();
                    break;

                case RECIPE_DIR:
                    updateCount = dbConnection.update(RecipeTable.TABLE_NAME,
                            values, selection, selectionArgs);
                    dbConnection.setTransactionSuccessful();
                    break;
                case RECIPE_ID:
                    final Long recipeId = ContentUris.parseId(uri);
                    updateCount = dbConnection.update(RecipeTable.TABLE_NAME,
                            values, RecipeTable.ID
                                    + "="
                                    + recipeId
                                    + (TextUtils.isEmpty(selection)
                                    ? ""
                                    : " AND (" + selection + ")"),
                            selectionArgs);
                    dbConnection.setTransactionSuccessful();
                    break;

                case STACK_DIR:
                    updateCount = dbConnection.update(StackTable.TABLE_NAME,
                            values, selection, selectionArgs);
                    dbConnection.setTransactionSuccessful();
                    break;
                case STACK_ID:
                    final Long stackId = ContentUris.parseId(uri);
                    updateCount = dbConnection.update(
                            StackTable.TABLE_NAME,
                            values,
                            StackTable.ID
                                    + "="
                                    + stackId
                                    + (TextUtils.isEmpty(selection)
                                    ? ""
                                    : " AND (" + selection + ")"),
                            selectionArgs);
                    dbConnection.setTransactionSuccessful();
                    break;

                case AGITATIONCYCLE_DIR:
                    updateCount = dbConnection.update(
                            AgitationCycleTable.TABLE_NAME, values, selection,
                            selectionArgs);
                    dbConnection.setTransactionSuccessful();
                    break;
                case AGITATIONCYCLE_ID:
                    final Long agitationcycleId = ContentUris.parseId(uri);
                    updateCount = dbConnection.update(
                            AgitationCycleTable.TABLE_NAME, values,
                            AgitationCycleTable.ID
                                    + "="
                                    + agitationcycleId
                                    + (TextUtils.isEmpty(selection)
                                    ? ""
                                    : " AND (" + selection + ")"),
                            selectionArgs);
                    dbConnection.setTransactionSuccessful();
                    break;

                case REMOTEIDMAPPING_DIR:
                    updateCount = dbConnection.update(
                            RemoteIdMappingTable.TABLE_NAME, values, selection,
                            selectionArgs);
                    dbConnection.setTransactionSuccessful();
                    break;
                case REMOTEIDMAPPING_ID:
                    final Long remoteidmappingId = ContentUris.parseId(uri);
                    updateCount = dbConnection.update(
                            RemoteIdMappingTable.TABLE_NAME, values,
                            RemoteIdMappingTable.ID
                                    + "="
                                    + remoteidmappingId
                                    + (TextUtils.isEmpty(selection)
                                    ? ""
                                    : " AND (" + selection + ")"),
                            selectionArgs);
                    dbConnection.setTransactionSuccessful();
                    break;

                case FAVORITE_DIR:
                    updateCount = dbConnection.update(FavoriteTable.TABLE_NAME,
                            values, selection, selectionArgs);
                    dbConnection.setTransactionSuccessful();
                    break;
                case FAVORITE_ID:
                    final Long favoriteId = ContentUris.parseId(uri);
                    updateCount = dbConnection.update(FavoriteTable.TABLE_NAME,
                            values, FavoriteTable.ID
                                    + "="
                                    + favoriteId
                                    + (TextUtils.isEmpty(selection)
                                    ? ""
                                    : " AND (" + selection + ")"),
                            selectionArgs);
                    dbConnection.setTransactionSuccessful();
                    break;

                case ROASTER_DIR:
                    updateCount = dbConnection.update(RoasterTable.TABLE_NAME,
                            values, selection, selectionArgs);
                    dbConnection.setTransactionSuccessful();
                    break;
                case ROASTER_ID:
                    final Long roasterId = ContentUris.parseId(uri);
                    updateCount = dbConnection.update(RoasterTable.TABLE_NAME,
                            values, RoasterTable.ID
                                    + "="
                                    + roasterId
                                    + (TextUtils.isEmpty(selection)
                                    ? ""
                                    : " AND (" + selection + ")"),
                            selectionArgs);
                    dbConnection.setTransactionSuccessful();
                    break;

                case PERSISTENCEQUEUE_DIR:
                    updateCount = dbConnection.update(
                            PersistenceQueueTable.TABLE_NAME, values,
                            selection, selectionArgs);
                    dbConnection.setTransactionSuccessful();
                    break;
                case PERSISTENCEQUEUE_ID:
                    final Long persistencequeueId = ContentUris.parseId(uri);
                    updateCount = dbConnection.update(
                            PersistenceQueueTable.TABLE_NAME, values,
                            PersistenceQueueTable.ID
                                    + "="
                                    + persistencequeueId
                                    + (TextUtils.isEmpty(selection)
                                    ? ""
                                    : " AND (" + selection + ")"),
                            selectionArgs);
                    dbConnection.setTransactionSuccessful();
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported URI:" + uri);
            }
        } finally {
            dbConnection.endTransaction();
        }

        if (updateCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return updateCount;

    }

    /**
     * Deletes given elements by their uri (items or directories) and returns number of deleted rows.
     *
     * @param uri           from type Uri
     * @param selection     from type String
     * @param selectionArgs from type String[]
     * @return number of deleted rows from type int
     */
    @Override
    public final int delete(final Uri uri, final String selection,
                            final String[] selectionArgs) {

        final SQLiteDatabase dbConnection = db.getWritableDatabase();
        int deleteCount = 0;

        try {
            dbConnection.beginTransaction();

            switch (URI_MATCHER.match(uri)) {
                case LOG_DIR:
                    deleteCount = dbConnection.delete(LogTable.TABLE_NAME,
                            selection, selectionArgs);
                    dbConnection.setTransactionSuccessful();
                    break;
                case LOG_ID:
                    deleteCount = dbConnection.delete(LogTable.TABLE_NAME,
                            LogTable.WHERE_ID_EQUALS, new String[]{uri
                                    .getPathSegments().get(1)});
                    dbConnection.setTransactionSuccessful();
                    break;
                case RECIPE_DIR:
                    deleteCount = dbConnection.delete(RecipeTable.TABLE_NAME,
                            selection, selectionArgs);
                    dbConnection.setTransactionSuccessful();
                    break;
                case RECIPE_ID:
                    deleteCount = dbConnection.delete(RecipeTable.TABLE_NAME,
                            RecipeTable.WHERE_ID_EQUALS, new String[]{uri
                                    .getPathSegments().get(1)});
                    dbConnection.setTransactionSuccessful();
                    break;
                case STACK_DIR:
                    deleteCount = dbConnection.delete(StackTable.TABLE_NAME,
                            selection, selectionArgs);
                    dbConnection.setTransactionSuccessful();
                    break;
                case STACK_ID:
                    deleteCount = dbConnection.delete(StackTable.TABLE_NAME,
                            StackTable.WHERE_ID_EQUALS, new String[]{uri
                                    .getPathSegments().get(1)});
                    dbConnection.setTransactionSuccessful();
                    break;
                case AGITATIONCYCLE_DIR:
                    deleteCount = dbConnection.delete(
                            AgitationCycleTable.TABLE_NAME, selection,
                            selectionArgs);
                    dbConnection.setTransactionSuccessful();
                    break;
                case AGITATIONCYCLE_ID:
                    deleteCount = dbConnection.delete(
                            AgitationCycleTable.TABLE_NAME,
                            AgitationCycleTable.WHERE_ID_EQUALS,
                            new String[]{uri.getPathSegments().get(1)});
                    dbConnection.setTransactionSuccessful();
                    break;
                case REMOTEIDMAPPING_DIR:
                    deleteCount = dbConnection.delete(
                            RemoteIdMappingTable.TABLE_NAME, selection,
                            selectionArgs);
                    dbConnection.setTransactionSuccessful();
                    break;
                case REMOTEIDMAPPING_ID:
                    deleteCount = dbConnection.delete(
                            RemoteIdMappingTable.TABLE_NAME,
                            RemoteIdMappingTable.WHERE_ID_EQUALS,
                            new String[]{uri.getPathSegments().get(1)});
                    dbConnection.setTransactionSuccessful();
                    break;
                case FAVORITE_DIR:
                    deleteCount = dbConnection.delete(FavoriteTable.TABLE_NAME,
                            selection, selectionArgs);
                    dbConnection.setTransactionSuccessful();
                    break;
                case FAVORITE_ID:
                    deleteCount = dbConnection.delete(FavoriteTable.TABLE_NAME,
                            FavoriteTable.WHERE_ID_EQUALS, new String[]{uri
                                    .getPathSegments().get(1)});
                    dbConnection.setTransactionSuccessful();
                    break;
                case ROASTER_DIR:
                    deleteCount = dbConnection.delete(RoasterTable.TABLE_NAME,
                            selection, selectionArgs);
                    dbConnection.setTransactionSuccessful();
                    break;
                case ROASTER_ID:
                    deleteCount = dbConnection.delete(RoasterTable.TABLE_NAME,
                            RoasterTable.WHERE_ID_EQUALS, new String[]{uri
                                    .getPathSegments().get(1)});
                    dbConnection.setTransactionSuccessful();
                    break;
                case PERSISTENCEQUEUE_DIR:
                    deleteCount = dbConnection.delete(
                            PersistenceQueueTable.TABLE_NAME, selection,
                            selectionArgs);
                    dbConnection.setTransactionSuccessful();
                    break;
                case PERSISTENCEQUEUE_ID:
                    deleteCount = dbConnection.delete(
                            PersistenceQueueTable.TABLE_NAME,
                            PersistenceQueueTable.WHERE_ID_EQUALS,
                            new String[]{uri.getPathSegments().get(1)});
                    dbConnection.setTransactionSuccessful();
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported URI:" + uri);
            }
        } finally {
            dbConnection.endTransaction();
        }

        if (deleteCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deleteCount;

    }

    /**
     * Executes a query on a given uri and returns a Cursor with results.
     *
     * @param uri           from type Uri
     * @param projection    from type String[]
     * @param selection     from type String
     * @param selectionArgs from type String[]
     * @param sortOrder     from type String
     * @return cursor with results from type Cursor
     */
    @Override
    public final Cursor query(final Uri uri, final String[] projection,
                              final String selection, final String[] selectionArgs,
                              final String sortOrder) {

        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        final SQLiteDatabase dbConnection = db.getReadableDatabase();

        switch (URI_MATCHER.match(uri)) {
            case LOG_ID:
                queryBuilder.appendWhere(LogTable.ID + "="
                        + uri.getPathSegments().get(1));
            case LOG_DIR:
                queryBuilder.setTables(LogTable.TABLE_NAME);
                break;
            case RECIPE_ID:
                queryBuilder.appendWhere(RecipeTable.ID + "="
                        + uri.getPathSegments().get(1));
            case RECIPE_DIR:
                queryBuilder.setTables(RecipeTable.TABLE_NAME);
                break;
            case STACK_ID:
                queryBuilder.appendWhere(StackTable.ID + "="
                        + uri.getPathSegments().get(1));
            case STACK_DIR:
                queryBuilder.setTables(StackTable.TABLE_NAME);
                break;
            case AGITATIONCYCLE_ID:
                queryBuilder.appendWhere(AgitationCycleTable.ID + "="
                        + uri.getPathSegments().get(1));
            case AGITATIONCYCLE_DIR:
                queryBuilder.setTables(AgitationCycleTable.TABLE_NAME);
                break;
            case REMOTEIDMAPPING_ID:
                queryBuilder.appendWhere(RemoteIdMappingTable.ID + "="
                        + uri.getPathSegments().get(1));
            case REMOTEIDMAPPING_DIR:
                queryBuilder.setTables(RemoteIdMappingTable.TABLE_NAME);
                break;
            case FAVORITE_ID:
                queryBuilder.appendWhere(FavoriteTable.ID + "="
                        + uri.getPathSegments().get(1));
            case FAVORITE_DIR:
                queryBuilder.setTables(FavoriteTable.TABLE_NAME);
                break;
            case ROASTER_ID:
                queryBuilder.appendWhere(RoasterTable.ID + "="
                        + uri.getPathSegments().get(1));
            case ROASTER_DIR:
                queryBuilder.setTables(RoasterTable.TABLE_NAME);
                break;
            case PERSISTENCEQUEUE_ID:
                queryBuilder.appendWhere(PersistenceQueueTable.ID + "="
                        + uri.getPathSegments().get(1));
            case PERSISTENCEQUEUE_DIR:
                queryBuilder.setTables(PersistenceQueueTable.TABLE_NAME);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI:" + uri);
        }

        Cursor cursor = queryBuilder.query(dbConnection, projection, selection,
                selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;

    }

}
