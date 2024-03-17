package com.alphadominche.steampunkhmi.database.tables;

/**
 * This interface represents the columns and SQLite statements for the RoasterTable.
 * This table is represented in the sqlite database as Roaster column.
 * <p/>
 * Generated Class. Do not modify!
 *
 * @author MDSDACP Team - goetzfred@fh-bingen.de
 * @date 2014.05.09
 */
public interface RoasterTable {
    String TABLE_NAME = "roaster";

    String ID = "_id";
    String FIRST_NAME = "first_name";
    String LAST_NAME = "last_name";
    String USERNAME = "username";
    String STEAMPUNK_ID = "steampunk_id";
    String SUBSCRIBED_TO = "subscribed_to";

    String[] ALL_COLUMNS = new String[]{ID, FIRST_NAME, LAST_NAME, USERNAME,
            STEAMPUNK_ID, SUBSCRIBED_TO};

    String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " ( " + ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT" + "," + FIRST_NAME + " TEXT"
            + "," + LAST_NAME + " TEXT" + "," + USERNAME + " TEXT" + ","
            + STEAMPUNK_ID + " INTEGER" + "," + SUBSCRIBED_TO + " INTEGER"
            + " )";

    String SQL_INSERT = "INSERT INTO " + TABLE_NAME + " (" + FIRST_NAME + ","
            + LAST_NAME + "," + USERNAME + "," + STEAMPUNK_ID + ","
            + SUBSCRIBED_TO + ") VALUES ( ?, ?, ?, ?, ? )";

    String SQL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    String WHERE_ID_EQUALS = ID + "=?";

    //added manually
    String WHERE_SP_ID_EQUALS = STEAMPUNK_ID + "=?";
}
