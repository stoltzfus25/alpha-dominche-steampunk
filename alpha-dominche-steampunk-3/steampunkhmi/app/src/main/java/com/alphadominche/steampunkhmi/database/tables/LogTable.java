package com.alphadominche.steampunkhmi.database.tables;

/**
 * This interface represents the columns and SQLite statements for the LogTable.
 * This table is represented in the sqlite database as Log column.
 * <p/>
 * Generated Class. Do not modify!
 *
 * @author MDSDACP Team - goetzfred@fh-bingen.de
 * @date 2014.05.09
 */
public interface LogTable {
    String TABLE_NAME = "log";

    String ID = "_id";
    String MACHINE = "machine";
    String CRUCIBLE = "crucible";
    String DATE = "date";
    String SEVERITY = "severity";
    String TYPE = "type";
    String MESSAGE = "message";
    String USER = "user";
    String TRANSACTION_STATE = "transaction_state";
    String TRANSACTION_RECORD = "transaction_record";
    String RECIPE_ID = "recipe_id";

    String[] ALL_COLUMNS = new String[]{ID, MACHINE, CRUCIBLE, DATE, SEVERITY,
            TYPE, MESSAGE, USER, TRANSACTION_STATE, TRANSACTION_RECORD,
            RECIPE_ID};

    String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " ( " + ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT" + "," + MACHINE + " TEXT"
            + "," + CRUCIBLE + " INTEGER" + "," + DATE + " TEXT" + ","
            + SEVERITY + " INTEGER" + "," + TYPE + " INTEGER" + "," + MESSAGE
            + " TEXT" + "," + USER + " INTEGER" + "," + TRANSACTION_STATE
            + " TEXT" + "," + TRANSACTION_RECORD + " TEXT" + "," + RECIPE_ID
            + " INTEGER" + " )";

    String SQL_INSERT = "INSERT INTO " + TABLE_NAME + " (" + MACHINE + ","
            + CRUCIBLE + "," + DATE + "," + SEVERITY + "," + TYPE + ","
            + MESSAGE + "," + USER + "," + TRANSACTION_STATE + ","
            + TRANSACTION_RECORD + "," + RECIPE_ID
            + ") VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    String SQL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    String WHERE_ID_EQUALS = ID + "=?";
}
