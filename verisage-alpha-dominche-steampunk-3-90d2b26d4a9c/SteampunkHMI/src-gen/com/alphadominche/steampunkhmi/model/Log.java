package com.alphadominche.steampunkhmi.model;

import android.content.ContentValues;
import android.database.Cursor;
import com.alphadominche.steampunkhmi.database.tables.LogTable;

/**
 * Generated model class for usage in your application, defined by classifiers in ecore diagram
 * 		
 * Generated Class. Do not modify!
 * 
 * @author MDSDACP Team - goetzfred@fh-bingen.de 
 * @date 2014.05.09	 
 */
public class Log {

	private Long id;
	private java.lang.String machine;
	private int crucible;
	private java.lang.String date;
	private int severity;
	private int type;
	private java.lang.String message;
	private long user;
	private java.lang.String transaction_state;
	private java.lang.String transaction_record;
	private long recipe_id;

	private final ContentValues values = new ContentValues();

	public Log() {
	}

	public Log(final Cursor cursor) {
		setId(cursor.getLong(cursor.getColumnIndex(LogTable.ID)));
		setMachine(cursor.getString(cursor.getColumnIndex(LogTable.MACHINE)));
		setCrucible(cursor.getInt(cursor.getColumnIndex(LogTable.CRUCIBLE)));
		setDate(cursor.getString(cursor.getColumnIndex(LogTable.DATE)));
		setSeverity(cursor.getInt(cursor.getColumnIndex(LogTable.SEVERITY)));
		setType(cursor.getInt(cursor.getColumnIndex(LogTable.TYPE)));
		setMessage(cursor.getString(cursor.getColumnIndex(LogTable.MESSAGE)));
		setUser(cursor.getLong(cursor.getColumnIndex(LogTable.USER)));
		setTransaction_state(cursor.getString(cursor
				.getColumnIndex(LogTable.TRANSACTION_STATE)));
		setTransaction_record(cursor.getString(cursor
				.getColumnIndex(LogTable.TRANSACTION_RECORD)));
		setRecipe_id(cursor.getLong(cursor.getColumnIndex(LogTable.RECIPE_ID)));

	}

	/**
	 * Set id
	 *
	 * @param id from type java.lang.Long
	 */
	public void setId(final Long id) {
		this.id = id;
		this.values.put(LogTable.ID, id);
	}

	/**
	 * Get id
	 *
	 * @return id from type java.lang.Long				
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * Set machine and set content value
	 *
	 * @param machine from type java.lang.String
	 */
	public void setMachine(final java.lang.String machine) {
		this.machine = machine;
		this.values.put(LogTable.MACHINE, machine);
	}

	/**
	 * Get machine
	 *
	 * @return machine from type java.lang.String				
	 */
	public java.lang.String getMachine() {
		return this.machine;
	}

	/**
	 * Set crucible and set content value
	 *
	 * @param crucible from type int
	 */
	public void setCrucible(final int crucible) {
		this.crucible = crucible;
		this.values.put(LogTable.CRUCIBLE, crucible);
	}

	/**
	 * Get crucible
	 *
	 * @return crucible from type int				
	 */
	public int getCrucible() {
		return this.crucible;
	}

	/**
	 * Set date and set content value
	 *
	 * @param date from type java.lang.String
	 */
	public void setDate(final java.lang.String date) {
		this.date = date;
		this.values.put(LogTable.DATE, date);
	}

	/**
	 * Get date
	 *
	 * @return date from type java.lang.String				
	 */
	public java.lang.String getDate() {
		return this.date;
	}

	/**
	 * Set severity and set content value
	 *
	 * @param severity from type int
	 */
	public void setSeverity(final int severity) {
		this.severity = severity;
		this.values.put(LogTable.SEVERITY, severity);
	}

	/**
	 * Get severity
	 *
	 * @return severity from type int				
	 */
	public int getSeverity() {
		return this.severity;
	}

	/**
	 * Set type and set content value
	 *
	 * @param type from type int
	 */
	public void setType(final int type) {
		this.type = type;
		this.values.put(LogTable.TYPE, type);
	}

	/**
	 * Get type
	 *
	 * @return type from type int				
	 */
	public int getType() {
		return this.type;
	}

	/**
	 * Set message and set content value
	 *
	 * @param message from type java.lang.String
	 */
	public void setMessage(final java.lang.String message) {
		this.message = message;
		this.values.put(LogTable.MESSAGE, message);
	}

	/**
	 * Get message
	 *
	 * @return message from type java.lang.String				
	 */
	public java.lang.String getMessage() {
		return this.message;
	}

	/**
	 * Set user and set content value
	 *
	 * @param user from type long
	 */
	public void setUser(final long user) {
		this.user = user;
		this.values.put(LogTable.USER, user);
	}

	/**
	 * Get user
	 *
	 * @return user from type long				
	 */
	public long getUser() {
		return this.user;
	}

	/**
	 * Set transaction_state and set content value
	 *
	 * @param transaction_state from type java.lang.String
	 */
	public void setTransaction_state(final java.lang.String transaction_state) {
		this.transaction_state = transaction_state;
		this.values.put(LogTable.TRANSACTION_STATE, transaction_state);
	}

	/**
	 * Get transaction_state
	 *
	 * @return transaction_state from type java.lang.String				
	 */
	public java.lang.String getTransaction_state() {
		return this.transaction_state;
	}

	/**
	 * Set transaction_record and set content value
	 *
	 * @param transaction_record from type java.lang.String
	 */
	public void setTransaction_record(final java.lang.String transaction_record) {
		this.transaction_record = transaction_record;
		this.values.put(LogTable.TRANSACTION_RECORD, transaction_record);
	}

	/**
	 * Get transaction_record
	 *
	 * @return transaction_record from type java.lang.String				
	 */
	public java.lang.String getTransaction_record() {
		return this.transaction_record;
	}

	/**
	 * Set recipe_id and set content value
	 *
	 * @param recipe_id from type long
	 */
	public void setRecipe_id(final long recipe_id) {
		this.recipe_id = recipe_id;
		this.values.put(LogTable.RECIPE_ID, recipe_id);
	}

	/**
	 * Get recipe_id
	 *
	 * @return recipe_id from type long				
	 */
	public long getRecipe_id() {
		return this.recipe_id;
	}

	/**
	 * Get ContentValues
	 *
	 * @return id from type android.content.ContentValues with the values of this object				
	 */
	public ContentValues getContentValues() {
		return this.values;
	}
}
