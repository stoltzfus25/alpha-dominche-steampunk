package com.alphadominche.steampunkhmi.model;

import android.content.ContentValues;
import android.database.Cursor;
import com.alphadominche.steampunkhmi.database.tables.RemoteIdMappingTable;

/**
 * Generated model class for usage in your application, defined by classifiers in ecore diagram
 * 		
 * Generated Class. Do not modify!
 * 
 * @author MDSDACP Team - goetzfred@fh-bingen.de 
 * @date 2014.05.09	 
 */
public class RemoteIdMapping {

	private Long id;
	private java.lang.String local_table_name;
	private long local_id;
	private long remote_id;

	private final ContentValues values = new ContentValues();

	public RemoteIdMapping() {
	}

	public RemoteIdMapping(final Cursor cursor) {
		setId(cursor.getLong(cursor.getColumnIndex(RemoteIdMappingTable.ID)));
		setLocal_table_name(cursor.getString(cursor
				.getColumnIndex(RemoteIdMappingTable.LOCAL_TABLE_NAME)));
		setLocal_id(cursor.getLong(cursor
				.getColumnIndex(RemoteIdMappingTable.LOCAL_ID)));
		setRemote_id(cursor.getLong(cursor
				.getColumnIndex(RemoteIdMappingTable.REMOTE_ID)));

	}

	/**
	 * Set id
	 *
	 * @param id from type java.lang.Long
	 */
	public void setId(final Long id) {
		this.id = id;
		this.values.put(RemoteIdMappingTable.ID, id);
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
	 * Set local_table_name and set content value
	 *
	 * @param local_table_name from type java.lang.String
	 */
	public void setLocal_table_name(final java.lang.String local_table_name) {
		this.local_table_name = local_table_name;
		this.values
				.put(RemoteIdMappingTable.LOCAL_TABLE_NAME, local_table_name);
	}

	/**
	 * Get local_table_name
	 *
	 * @return local_table_name from type java.lang.String				
	 */
	public java.lang.String getLocal_table_name() {
		return this.local_table_name;
	}

	/**
	 * Set local_id and set content value
	 *
	 * @param local_id from type long
	 */
	public void setLocal_id(final long local_id) {
		this.local_id = local_id;
		this.values.put(RemoteIdMappingTable.LOCAL_ID, local_id);
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
	 * Set remote_id and set content value
	 *
	 * @param remote_id from type long
	 */
	public void setRemote_id(final long remote_id) {
		this.remote_id = remote_id;
		this.values.put(RemoteIdMappingTable.REMOTE_ID, remote_id);
	}

	/**
	 * Get remote_id
	 *
	 * @return remote_id from type long				
	 */
	public long getRemote_id() {
		return this.remote_id;
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
