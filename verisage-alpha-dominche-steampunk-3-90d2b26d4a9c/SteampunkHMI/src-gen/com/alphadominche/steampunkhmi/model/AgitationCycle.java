package com.alphadominche.steampunkhmi.model;

import android.content.ContentValues;
import android.database.Cursor;
import com.alphadominche.steampunkhmi.database.tables.AgitationCycleTable;

/**
 * Generated model class for usage in your application, defined by classifiers in ecore diagram
 * 		
 * Generated Class. Do not modify!
 * 
 * @author MDSDACP Team - goetzfred@fh-bingen.de 
 * @date 2014.05.09	 
 */
public class AgitationCycle {

	private Long id;
	private int start_time;
	private int duration;
	private java.lang.String transaction_state;
	private java.lang.String transaction_result;
	private long stack_id;
	private long local_id;

	private final ContentValues values = new ContentValues();

	public AgitationCycle() {
	}

	public AgitationCycle(final Cursor cursor) {
		setId(cursor.getLong(cursor.getColumnIndex(AgitationCycleTable.ID)));
		setStart_time(cursor.getInt(cursor
				.getColumnIndex(AgitationCycleTable.START_TIME)));
		setDuration(cursor.getInt(cursor
				.getColumnIndex(AgitationCycleTable.DURATION)));
		setTransaction_state(cursor.getString(cursor
				.getColumnIndex(AgitationCycleTable.TRANSACTION_STATE)));
		setTransaction_result(cursor.getString(cursor
				.getColumnIndex(AgitationCycleTable.TRANSACTION_RESULT)));
		setStack_id(cursor.getLong(cursor
				.getColumnIndex(AgitationCycleTable.STACK_ID)));
		setLocal_id(cursor.getLong(cursor
				.getColumnIndex(AgitationCycleTable.LOCAL_ID)));

	}

	/**
	 * Set id
	 *
	 * @param id from type java.lang.Long
	 */
	public void setId(final Long id) {
		this.id = id;
		this.values.put(AgitationCycleTable.ID, id);
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
	 * Set start_time and set content value
	 *
	 * @param start_time from type int
	 */
	public void setStart_time(final int start_time) {
		this.start_time = start_time;
		this.values.put(AgitationCycleTable.START_TIME, start_time);
	}

	/**
	 * Get start_time
	 *
	 * @return start_time from type int				
	 */
	public int getStart_time() {
		return this.start_time;
	}

	/**
	 * Set duration and set content value
	 *
	 * @param duration from type int
	 */
	public void setDuration(final int duration) {
		this.duration = duration;
		this.values.put(AgitationCycleTable.DURATION, duration);
	}

	/**
	 * Get duration
	 *
	 * @return duration from type int				
	 */
	public int getDuration() {
		return this.duration;
	}

	/**
	 * Set transaction_state and set content value
	 *
	 * @param transaction_state from type java.lang.String
	 */
	public void setTransaction_state(final java.lang.String transaction_state) {
		this.transaction_state = transaction_state;
		this.values.put(AgitationCycleTable.TRANSACTION_STATE,
				transaction_state);
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
	 * Set transaction_result and set content value
	 *
	 * @param transaction_result from type java.lang.String
	 */
	public void setTransaction_result(final java.lang.String transaction_result) {
		this.transaction_result = transaction_result;
		this.values.put(AgitationCycleTable.TRANSACTION_RESULT,
				transaction_result);
	}

	/**
	 * Get transaction_result
	 *
	 * @return transaction_result from type java.lang.String				
	 */
	public java.lang.String getTransaction_result() {
		return this.transaction_result;
	}

	/**
	 * Set stack_id and set content value
	 *
	 * @param stack_id from type long
	 */
	public void setStack_id(final long stack_id) {
		this.stack_id = stack_id;
		this.values.put(AgitationCycleTable.STACK_ID, stack_id);
	}

	/**
	 * Get stack_id
	 *
	 * @return stack_id from type long				
	 */
	public long getStack_id() {
		return this.stack_id;
	}

	/**
	 * Set local_id and set content value
	 *
	 * @param local_id from type long
	 */
	public void setLocal_id(final long local_id) {
		this.local_id = local_id;
		this.values.put(AgitationCycleTable.LOCAL_ID, local_id);
	}

	/**
	 * Get local_id
	 *
	 * @return local_id from type long				
	 */
	public long getLocal_id() {
		return this.local_id;
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
