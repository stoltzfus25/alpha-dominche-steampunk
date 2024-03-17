package com.alphadominche.steampunkhmi.database.tables;

/**
 * This interface represents the columns and SQLite statements for the FavoriteTable.
 * This table is represented in the sqlite database as Favorite column.
 * <p/>
 * Generated Class. Do not modify!
 *
 * @author MDSDACP Team - goetzfred@fh-bingen.de
 * @date 2014.05.09
 */
public interface FavoriteTable {
    String TABLE_NAME = "favorite";

    String ID = "_id";
    String USER = "user";
    String RECIPE_ID = "recipe_id";
    String UUID = "uuid";
    String RECIPE_UUID = "recipe_uuid";

    String[] ALL_COLUMNS = new String[]{ID, USER, RECIPE_ID, UUID, RECIPE_UUID};

    String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " ( " + ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT" + "," + USER + " INTEGER"
            + "," + RECIPE_ID + " INTEGER" + "," + UUID + " TEXT" + ","
            + RECIPE_UUID + " TEXT" + " )";

    String SQL_INSERT = "INSERT INTO " + TABLE_NAME + " (" + USER + ","
            + RECIPE_ID + "," + UUID + "," + RECIPE_UUID
            + ") VALUES ( ?, ?, ?, ? )";

    String SQL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    String WHERE_ID_EQUALS = ID + "=?";

    //added manually
    String WHERE_UUID_EQUALS = UUID + "=?";
}
