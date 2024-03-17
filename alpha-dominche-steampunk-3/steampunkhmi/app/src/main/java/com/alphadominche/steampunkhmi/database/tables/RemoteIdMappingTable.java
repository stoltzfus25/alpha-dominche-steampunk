package com.alphadominche.steampunkhmi.database.tables;

/**
 * This interface represents the columns and SQLite statements for the RemoteIdMappingTable.
 * This table is represented in the sqlite database as RemoteIdMapping column.
 * <p/>
 * Generated Class. Do not modify!
 *
 * @author MDSDACP Team - goetzfred@fh-bingen.de
 * @date 2014.05.09
 */
public interface RemoteIdMappingTable {
    String TABLE_NAME = "remoteidmapping";

    String ID = "_id";
    String LOCAL_TABLE_NAME = "local_table_name";
    String LOCAL_ID = "local_id";
    String REMOTE_ID = "remote_id";

    String[] ALL_COLUMNS = new String[]{ID, LOCAL_TABLE_NAME, LOCAL_ID,
            REMOTE_ID};

    String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " ( " + ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT" + "," + LOCAL_TABLE_NAME
            + " TEXT" + "," + LOCAL_ID + " INTEGER" + "," + REMOTE_ID
            + " INTEGER" + " )";

    String SQL_INSERT = "INSERT INTO " + TABLE_NAME + " (" + LOCAL_TABLE_NAME
            + "," + LOCAL_ID + "," + REMOTE_ID + ") VALUES ( ?, ?, ? )";

    String SQL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    String WHERE_ID_EQUALS = ID + "=?";
}
