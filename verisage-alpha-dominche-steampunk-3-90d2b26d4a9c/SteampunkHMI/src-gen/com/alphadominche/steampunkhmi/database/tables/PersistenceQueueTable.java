package com.alphadominche.steampunkhmi.database.tables;

/**
 * This interface represents the columns and SQLite statements for the PersistenceQueueTable.
 * This table is represented in the sqlite database as PersistenceQueue column.
 * 				  
 * Generated Class. Do not modify!
 * 
 * @author MDSDACP Team - goetzfred@fh-bingen.de 
 * @date 2014.05.09
 */
public interface PersistenceQueueTable {
	String TABLE_NAME = "persistencequeue";

	String ID = "_id";
	String REQUEST_TYPE = "request_type";
	String STATE = "state";
	String TBL_NAME = "tbl_name";
	String OBJ_ID = "obj_id";
	String REQUEST_COUNT = "request_count";
	String BACKPOINTERS = "backpointers";
	String LAST_ATTEMPT = "last_attempt";
	String OBJ_UUID = "obj_uuid";

	String[] ALL_COLUMNS = new String[]{ID, REQUEST_TYPE, STATE, TBL_NAME,
			OBJ_ID, REQUEST_COUNT, BACKPOINTERS, LAST_ATTEMPT, OBJ_UUID};

	String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " ( " + ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT" + "," + REQUEST_TYPE
			+ " TEXT" + "," + STATE + " TEXT" + "," + TBL_NAME + " TEXT" + ","
			+ OBJ_ID + " INTEGER" + "," + REQUEST_COUNT + " INTEGER" + ","
			+ BACKPOINTERS + " TEXT" + "," + LAST_ATTEMPT + " INTEGER" + ","
			+ OBJ_UUID + " TEXT" + " )";

	String SQL_INSERT = "INSERT INTO " + TABLE_NAME + " (" + REQUEST_TYPE + ","
			+ STATE + "," + TBL_NAME + "," + OBJ_ID + "," + REQUEST_COUNT + ","
			+ BACKPOINTERS + "," + LAST_ATTEMPT + "," + OBJ_UUID
			+ ") VALUES ( ?, ?, ?, ?, ?, ?, ?, ? )";

	String SQL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

	String WHERE_ID_EQUALS = ID + "=?";
}
