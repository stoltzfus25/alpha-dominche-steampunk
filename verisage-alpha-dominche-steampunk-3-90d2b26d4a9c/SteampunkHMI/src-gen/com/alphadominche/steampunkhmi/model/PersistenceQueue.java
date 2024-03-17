package com.alphadominche.steampunkhmi.model;

import android.content.ContentValues;
import android.database.Cursor;
import com.alphadominche.steampunkhmi.database.tables.PersistenceQueueTable;

/**
 * Generated model class for usage in your application, defined by classifiers in ecore diagram
 * 		
 * Generated Class. Do not modify!
 * 
 * @author MDSDACP Team - goetzfred@fh-bingen.de 
 * @date 2014.05.09	 
 */
public class PersistenceQueue {

	private Long id;
	private java.lang.String request_type;
	private java.lang.String state;
	private java.lang.String tbl_name;
	private long obj_id;
	private int request_count;
	private java.lang.String backpointers;
	private long last_attempt;
	private java.lang.String obj_uuid;

	private final ContentValues values = new ContentValues();

	public PersistenceQueue() {
	}

	public PersistenceQueue(final Cursor cursor) {
		setId(cursor.getLong(cursor.getColumnIndex(PersistenceQueueTable.ID)));
		setRequest_type(cursor.getString(cursor
				.getColumnIndex(PersistenceQueueTable.REQUEST_TYPE)));
		setState(cursor.getString(cursor
				.getColumnIndex(PersistenceQueueTable.STATE)));
		setTbl_name(cursor.getString(cursor
				.getColumnIndex(PersistenceQueueTable.TBL_NAME)));
		setObj_id(cursor.getLong(cursor
				.getColumnIndex(PersistenceQueueTable.OBJ_ID)));
		setRequest_count(cursor.getInt(cursor
				.getColumnIndex(PersistenceQueueTable.REQUEST_COUNT)));
		setBackpointers(cursor.getString(cursor
				.getColumnIndex(PersistenceQueueTable.BACKPOINTERS)));
		setLast_attempt(cursor.getLong(cursor
				.getColumnIndex(PersistenceQueueTable.LAST_ATTEMPT)));
		setObj_uuid(cursor.getString(cursor
				.getColumnIndex(PersistenceQueueTable.OBJ_UUID)));

	}

	/**
	 * Set id
	 *
	 * @param id from type java.lang.Long
	 */
	public void setId(final Long id) {
		this.id = id;
		this.values.put(PersistenceQueueTable.ID, id);
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
	 * Set request_type and set content value
	 *
	 * @param request_type from type java.lang.String
	 */
	public void setRequest_type(final java.lang.String request_type) {
		this.request_type = request_type;
		this.values.put(PersistenceQueueTable.REQUEST_TYPE, request_type);
	}

	/**
	 * Get request_type
	 *
	 * @return request_type from type java.lang.String				
	 */
	public java.lang.String getRequest_type() {
		return this.request_type;
	}

	/**
	 * Set state and set content value
	 *
	 * @param state from type java.lang.String
	 */
	public void setState(final java.lang.String state) {
		this.state = state;
		this.values.put(PersistenceQueueTable.STATE, state);
	}

	/**
	 * Get state
	 *
	 * @return state from type java.lang.String				
	 */
	public java.lang.String getState() {
		return this.state;
	}

	/**
	 * Set tbl_name and set content value
	 *
	 * @param tbl_name from type java.lang.String
	 */
	public void setTbl_name(final java.lang.String tbl_name) {
		this.tbl_name = tbl_name;
		this.values.put(PersistenceQueueTable.TBL_NAME, tbl_name);
	}

	/**
	 * Get tbl_name
	 *
	 * @return tbl_name from type java.lang.String				
	 */
	public java.lang.String getTbl_name() {
		return this.tbl_name;
	}

	/**
	 * Set obj_id and set content value
	 *
	 * @param obj_id from type long
	 */
	public void setObj_id(final long obj_id) {
		this.obj_id = obj_id;
		this.values.put(PersistenceQueueTable.OBJ_ID, obj_id);
	}

	/**
	 * Get obj_id
	 *
	 * @return obj_id from type long				
	 */
	public long getObj_id() {
		return this.obj_id;
	}

	/**
	 * Set request_count and set content value
	 *
	 * @param request_count from type int
	 */
	public void setRequest_count(final int request_count) {
		this.request_count = request_count;
		this.values.put(PersistenceQueueTable.REQUEST_COUNT, request_count);
	}

	/**
	 * Get request_count
	 *
	 * @return request_count from type int				
	 */
	public int getRequest_count() {
		return this.request_count;
	}

	/**
	 * Set backpointers and set content value
	 *
	 * @param backpointers from type java.lang.String
	 */
	public void setBackpointers(final java.lang.String backpointers) {
		this.backpointers = backpointers;
		this.values.put(PersistenceQueueTable.BACKPOINTERS, backpointers);
	}

	/**
	 * Get backpointers
	 *
	 * @return backpointers from type java.lang.String				
	 */
	public java.lang.String getBackpointers() {
		return this.backpointers;
	}

	/**
	 * Set last_attempt and set content value
	 *
	 * @param last_attempt from type long
	 */
	public void setLast_attempt(final long last_attempt) {
		this.last_attempt = last_attempt;
		this.values.put(PersistenceQueueTable.LAST_ATTEMPT, last_attempt);
	}

	/**
	 * Get last_attempt
	 *
	 * @return last_attempt from type long				
	 */
	public long getLast_attempt() {
		return this.last_attempt;
	}

	/**
	 * Set obj_uuid and set content value
	 *
	 * @param obj_uuid from type java.lang.String
	 */
	public void setObj_uuid(final java.lang.String obj_uuid) {
		this.obj_uuid = obj_uuid;
		this.values.put(PersistenceQueueTable.OBJ_UUID, obj_uuid);
	}

	/**
	 * Get obj_uuid
	 *
	 * @return obj_uuid from type java.lang.String				
	 */
	public java.lang.String getObj_uuid() {
		return this.obj_uuid;
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
