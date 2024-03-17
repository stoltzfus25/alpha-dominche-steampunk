package com.alphadominche.steampunkhmi.model;

import android.content.ContentValues;
import android.database.Cursor;
import com.alphadominche.steampunkhmi.database.tables.StackTable;

/**
 * Generated model class for usage in your application, defined by classifiers in ecore diagram
 * 		
 * Generated Class. Do not modify!
 * 
 * @author MDSDACP Team - goetzfred@fh-bingen.de 
 * @date 2014.05.09	 
 */
public class Stack {

	private Long id;
	private int stack_order;
	private double volume;
	private int start_time;
	private int duration;
	private double temperature;
	private double vacuum_break;
	private int pull_down_time;
	private java.lang.String transaction_state;
	private java.lang.String transaction_result;
	private long recipe_id;
	private long local_id;

	private final ContentValues values = new ContentValues();

	public Stack() {
	}

	public Stack(final Cursor cursor) {
		setId(cursor.getLong(cursor.getColumnIndex(StackTable.ID)));
		setStack_order(cursor.getInt(cursor
				.getColumnIndex(StackTable.STACK_ORDER)));
		setVolume(cursor.getDouble(cursor.getColumnIndex(StackTable.VOLUME)));
		setStart_time(cursor.getInt(cursor
				.getColumnIndex(StackTable.START_TIME)));
		setDuration(cursor.getInt(cursor.getColumnIndex(StackTable.DURATION)));
		setTemperature(cursor.getDouble(cursor
				.getColumnIndex(StackTable.TEMPERATURE)));
		setVacuum_break(cursor.getDouble(cursor
				.getColumnIndex(StackTable.VACUUM_BREAK)));
		setPull_down_time(cursor.getInt(cursor
				.getColumnIndex(StackTable.PULL_DOWN_TIME)));
		setTransaction_state(cursor.getString(cursor
				.getColumnIndex(StackTable.TRANSACTION_STATE)));
		setTransaction_result(cursor.getString(cursor
				.getColumnIndex(StackTable.TRANSACTION_RESULT)));
		setRecipe_id(cursor
				.getLong(cursor.getColumnIndex(StackTable.RECIPE_ID)));
		setLocal_id(cursor.getLong(cursor.getColumnIndex(StackTable.LOCAL_ID)));

	}

	/**
	 * Set id
	 *
	 * @param id from type java.lang.Long
	 */
	public void setId(final Long id) {
		this.id = id;
		this.values.put(StackTable.ID, id);
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
	 * Set stack_order and set content value
	 *
	 * @param stack_order from type int
	 */
	public void setStack_order(final int stack_order) {
		this.stack_order = stack_order;
		this.values.put(StackTable.STACK_ORDER, stack_order);
	}

	/**
	 * Get stack_order
	 *
	 * @return stack_order from type int				
	 */
	public int getStack_order() {
		return this.stack_order;
	}

	/**
	 * Set volume and set content value
	 *
	 * @param volume from type double
	 */
	public void setVolume(final double volume) {
		this.volume = volume;
		this.values.put(StackTable.VOLUME, volume);
	}

	/**
	 * Get volume
	 *
	 * @return volume from type double				
	 */
	public double getVolume() {
		return this.volume;
	}

	/**
	 * Set start_time and set content value
	 *
	 * @param start_time from type int
	 */
	public void setStart_time(final int start_time) {
		this.start_time = start_time;
		this.values.put(StackTable.START_TIME, start_time);
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
		this.values.put(StackTable.DURATION, duration);
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
	 * Set temperature and set content value
	 *
	 * @param temperature from type double
	 */
	public void setTemperature(final double temperature) {
		this.temperature = temperature;
		this.values.put(StackTable.TEMPERATURE, temperature);
	}

	/**
	 * Get temperature
	 *
	 * @return temperature from type double				
	 */
	public double getTemperature() {
		return this.temperature;
	}

	/**
	 * Set vacuum_break and set content value
	 *
	 * @param vacuum_break from type double
	 */
	public void setVacuum_break(final double vacuum_break) {
		this.vacuum_break = vacuum_break;
		this.values.put(StackTable.VACUUM_BREAK, vacuum_break);
	}

	/**
	 * Get vacuum_break
	 *
	 * @return vacuum_break from type double				
	 */
	public double getVacuum_break() {
		return this.vacuum_break;
	}

	/**
	 * Set pull_down_time and set content value
	 *
	 * @param pull_down_time from type int
	 */
	public void setPull_down_time(final int pull_down_time) {
		this.pull_down_time = pull_down_time;
		this.values.put(StackTable.PULL_DOWN_TIME, pull_down_time);
	}

	/**
	 * Get pull_down_time
	 *
	 * @return pull_down_time from type int				
	 */
	public int getPull_down_time() {
		return this.pull_down_time;
	}

	/**
	 * Set transaction_state and set content value
	 *
	 * @param transaction_state from type java.lang.String
	 */
	public void setTransaction_state(final java.lang.String transaction_state) {
		this.transaction_state = transaction_state;
		this.values.put(StackTable.TRANSACTION_STATE, transaction_state);
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
		this.values.put(StackTable.TRANSACTION_RESULT, transaction_result);
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
	 * Set recipe_id and set content value
	 *
	 * @param recipe_id from type long
	 */
	public void setRecipe_id(final long recipe_id) {
		this.recipe_id = recipe_id;
		this.values.put(StackTable.RECIPE_ID, recipe_id);
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
	 * Set local_id and set content value
	 *
	 * @param local_id from type long
	 */
	public void setLocal_id(final long local_id) {
		this.local_id = local_id;
		this.values.put(StackTable.LOCAL_ID, local_id);
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
