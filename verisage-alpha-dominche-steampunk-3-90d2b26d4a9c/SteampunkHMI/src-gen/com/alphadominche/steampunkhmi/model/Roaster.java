package com.alphadominche.steampunkhmi.model;

import android.content.ContentValues;
import android.database.Cursor;
import com.alphadominche.steampunkhmi.database.tables.RoasterTable;

/**
 * Generated model class for usage in your application, defined by classifiers in ecore diagram
 * 		
 * Generated Class. Do not modify!
 * 
 * @author MDSDACP Team - goetzfred@fh-bingen.de 
 * @date 2014.05.09	 
 */
public class Roaster {

	private Long id;
	private java.lang.String first_name;
	private java.lang.String last_name;
	private java.lang.String username;
	private long steampunk_id;
	private int subscribed_to;

	private final ContentValues values = new ContentValues();

	public Roaster() {
	}

	public Roaster(final Cursor cursor) {
		setId(cursor.getLong(cursor.getColumnIndex(RoasterTable.ID)));
		setFirst_name(cursor.getString(cursor
				.getColumnIndex(RoasterTable.FIRST_NAME)));
		setLast_name(cursor.getString(cursor
				.getColumnIndex(RoasterTable.LAST_NAME)));
		setUsername(cursor.getString(cursor
				.getColumnIndex(RoasterTable.USERNAME)));
		setSteampunk_id(cursor.getLong(cursor
				.getColumnIndex(RoasterTable.STEAMPUNK_ID)));
		setSubscribed_to(cursor.getInt(cursor
				.getColumnIndex(RoasterTable.SUBSCRIBED_TO)));

	}

	/**
	 * Set id
	 *
	 * @param id from type java.lang.Long
	 */
	public void setId(final Long id) {
		this.id = id;
		this.values.put(RoasterTable.ID, id);
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
	 * Set first_name and set content value
	 *
	 * @param first_name from type java.lang.String
	 */
	public void setFirst_name(final java.lang.String first_name) {
		this.first_name = first_name;
		this.values.put(RoasterTable.FIRST_NAME, first_name);
	}

	/**
	 * Get first_name
	 *
	 * @return first_name from type java.lang.String				
	 */
	public java.lang.String getFirst_name() {
		return this.first_name;
	}

	/**
	 * Set last_name and set content value
	 *
	 * @param last_name from type java.lang.String
	 */
	public void setLast_name(final java.lang.String last_name) {
		this.last_name = last_name;
		this.values.put(RoasterTable.LAST_NAME, last_name);
	}

	/**
	 * Get last_name
	 *
	 * @return last_name from type java.lang.String				
	 */
	public java.lang.String getLast_name() {
		return this.last_name;
	}

	/**
	 * Set username and set content value
	 *
	 * @param username from type java.lang.String
	 */
	public void setUsername(final java.lang.String username) {
		this.username = username;
		this.values.put(RoasterTable.USERNAME, username);
	}

	/**
	 * Get username
	 *
	 * @return username from type java.lang.String				
	 */
	public java.lang.String getUsername() {
		return this.username;
	}

	/**
	 * Set steampunk_id and set content value
	 *
	 * @param steampunk_id from type long
	 */
	public void setSteampunk_id(final long steampunk_id) {
		this.steampunk_id = steampunk_id;
		this.values.put(RoasterTable.STEAMPUNK_ID, steampunk_id);
	}

	/**
	 * Get steampunk_id
	 *
	 * @return steampunk_id from type long				
	 */
	public long getSteampunk_id() {
		return this.steampunk_id;
	}

	/**
	 * Set subscribed_to and set content value
	 *
	 * @param subscribed_to from type int
	 */
	public void setSubscribed_to(final int subscribed_to) {
		this.subscribed_to = subscribed_to;
		this.values.put(RoasterTable.SUBSCRIBED_TO, subscribed_to);
	}

	/**
	 * Get subscribed_to
	 *
	 * @return subscribed_to from type int				
	 */
	public int getSubscribed_to() {
		return this.subscribed_to;
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
