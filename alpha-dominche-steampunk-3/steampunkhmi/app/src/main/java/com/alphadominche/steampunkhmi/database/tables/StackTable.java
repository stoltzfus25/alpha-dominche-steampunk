package com.alphadominche.steampunkhmi.database.tables;

/**
 * This interface represents the columns and SQLite statements for the StackTable.
 * This table is represented in the sqlite database as Stack column.
 * <p/>
 * Generated Class. Do not modify!
 *
 * @author MDSDACP Team - goetzfred@fh-bingen.de
 * @date 2014.05.09
 */
public interface StackTable {
    String TABLE_NAME = "stack";

    String ID = "_id";
    String STACK_ORDER = "stack_order";
    String VOLUME = "volume";
    String START_TIME = "start_time";
    String DURATION = "duration";
    String TEMPERATURE = "temperature";
    String VACUUM_BREAK = "vacuum_break";
    String PULL_DOWN_TIME = "pull_down_time";
    String TRANSACTION_STATE = "transaction_state";
    String TRANSACTION_RESULT = "transaction_result";
    String RECIPE_ID = "recipe_id";
    String LOCAL_ID = "local_id";

    String[] ALL_COLUMNS = new String[]{ID, STACK_ORDER, VOLUME, START_TIME,
            DURATION, TEMPERATURE, VACUUM_BREAK, PULL_DOWN_TIME,
            TRANSACTION_STATE, TRANSACTION_RESULT, RECIPE_ID, LOCAL_ID};

    String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " ( " + ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT" + "," + STACK_ORDER
            + " INTEGER" + "," + VOLUME + " REAL" + "," + START_TIME
            + " INTEGER" + "," + DURATION + " INTEGER" + "," + TEMPERATURE
            + " REAL" + "," + VACUUM_BREAK + " REAL" + "," + PULL_DOWN_TIME
            + " INTEGER" + "," + TRANSACTION_STATE + " TEXT" + ","
            + TRANSACTION_RESULT + " TEXT" + "," + RECIPE_ID + " INTEGER" + ","
            + LOCAL_ID + " INTEGER" + " )";

    String SQL_INSERT = "INSERT INTO " + TABLE_NAME + " (" + STACK_ORDER + ","
            + VOLUME + "," + START_TIME + "," + DURATION + "," + TEMPERATURE
            + "," + VACUUM_BREAK + "," + PULL_DOWN_TIME + ","
            + TRANSACTION_STATE + "," + TRANSACTION_RESULT + "," + RECIPE_ID
            + "," + LOCAL_ID + ") VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    String SQL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    String WHERE_ID_EQUALS = ID + "=?";
}
