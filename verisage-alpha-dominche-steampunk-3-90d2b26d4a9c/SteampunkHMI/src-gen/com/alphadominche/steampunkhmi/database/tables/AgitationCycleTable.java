package com.alphadominche.steampunkhmi.database.tables;

/**
 * This interface represents the columns and SQLite statements for the AgitationCycleTable.
 * This table is represented in the sqlite database as AgitationCycle column.
 * 				  
 * Generated Class. Do not modify!
 * 
 * @author MDSDACP Team - goetzfred@fh-bingen.de 
 * @date 2014.05.09
 */
public interface AgitationCycleTable {
	String TABLE_NAME = "agitationcycle";

	String ID = "_id";
	String START_TIME = "start_time";
	String DURATION = "duration";
	String TRANSACTION_STATE = "transaction_state";
	String TRANSACTION_RESULT = "transaction_result";
	String STACK_ID = "stack_id";
	String LOCAL_ID = "local_id";

	String[] ALL_COLUMNS = new String[]{ID, START_TIME, DURATION,
			TRANSACTION_STATE, TRANSACTION_RESULT, STACK_ID, LOCAL_ID};

	String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " ( " + ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT" + "," + START_TIME
			+ " INTEGER" + "," + DURATION + " INTEGER" + ","
			+ TRANSACTION_STATE + " TEXT" + "," + TRANSACTION_RESULT + " TEXT"
			+ "," + STACK_ID + " INTEGER" + "," + LOCAL_ID + " INTEGER" + " )";

	String SQL_INSERT = "INSERT INTO " + TABLE_NAME + " (" + START_TIME + ","
			+ DURATION + "," + TRANSACTION_STATE + "," + TRANSACTION_RESULT
			+ "," + STACK_ID + "," + LOCAL_ID + ") VALUES ( ?, ?, ?, ?, ?, ? )";

	String SQL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

	String WHERE_ID_EQUALS = ID + "=?";
}
