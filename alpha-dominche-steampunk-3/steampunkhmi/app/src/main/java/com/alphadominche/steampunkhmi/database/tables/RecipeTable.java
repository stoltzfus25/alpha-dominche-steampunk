package com.alphadominche.steampunkhmi.database.tables;

/**
 * This interface represents the columns and SQLite statements for the RecipeTable.
 * This table is represented in the sqlite database as Recipe column.
 * <p/>
 * Generated Class. Do not modify!
 *
 * @author MDSDACP Team - goetzfred@fh-bingen.de
 * @date 2014.05.09
 */
public interface RecipeTable {
    String TABLE_NAME = "recipe";

    String ID = "_id";
    String NAME = "name";
    String TYPE = "type";
    String STEAMPUNK_USER_ID = "steampunk_user_id";
    String PUBLISHED = "published";
    String GRAMS = "grams";
    String TEASPOONS = "teaspoons";
    String FILTER = "filter";
    String GRIND = "grind";
    String TRANSACTION_STATE = "transaction_state";
    String TRANSACTION_RECORD = "transaction_record";
    String LOCAL_ID = "local_id";
    String STACKS = "stacks";
    String UUID = "uuid";

    String[] ALL_COLUMNS = new String[]{ID, NAME, TYPE, STEAMPUNK_USER_ID,
            PUBLISHED, GRAMS, TEASPOONS, FILTER, GRIND, TRANSACTION_STATE,
            TRANSACTION_RECORD, LOCAL_ID, STACKS, UUID};

    String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " ( " + ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT" + "," + NAME + " TEXT" + ","
            + TYPE + " INTEGER" + "," + STEAMPUNK_USER_ID + " INTEGER" + ","
            + PUBLISHED + " NUMERIC" + "," + GRAMS + " REAL" + "," + TEASPOONS
            + " REAL" + "," + FILTER + " INTEGER" + "," + GRIND + " REAL" + ","
            + TRANSACTION_STATE + " TEXT" + "," + TRANSACTION_RECORD + " TEXT"
            + "," + LOCAL_ID + " INTEGER" + "," + STACKS + " TEXT" + "," + UUID
            + " TEXT" + " )";

    String SQL_INSERT = "INSERT INTO " + TABLE_NAME + " (" + NAME + "," + TYPE
            + "," + STEAMPUNK_USER_ID + "," + PUBLISHED + "," + GRAMS + ","
            + TEASPOONS + "," + FILTER + "," + GRIND + "," + TRANSACTION_STATE
            + "," + TRANSACTION_RECORD + "," + LOCAL_ID + "," + STACKS + ","
            + UUID + ") VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    String SQL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    String WHERE_ID_EQUALS = ID + "=?";

    //added manually
    String WHERE_UUID_EQUALS = UUID + "=?";
}
