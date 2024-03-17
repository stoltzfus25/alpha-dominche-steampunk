package com.alphadominche.steampunkhmi.model;

import android.content.ContentValues;
import android.database.Cursor;
import com.alphadominche.steampunkhmi.database.tables.RecipeTable;

/**
 * Generated model class for usage in your application, defined by classifiers in ecore diagram
 * <p/>
 * Generated Class. Do not modify!
 *
 * @author MDSDACP Team - goetzfred@fh-bingen.de
 * @date 2014.05.09
 */
public class Recipe {

    private Long id;
    private String name;
    private int type;
    private long steampunk_user_id;
    private boolean published;
    private double grams;
    private double teaspoons;
    private int filter;
    private double grind;
    private String transaction_state;
    private String transaction_record;
    private long local_id;
    private String stacks;
    private String uuid;

    private final ContentValues values = new ContentValues();

    public Recipe() {
    }

    public Recipe(final Cursor cursor) {
        setId(cursor.getLong(cursor.getColumnIndex(RecipeTable.ID)));
        setName(cursor.getString(cursor.getColumnIndex(RecipeTable.NAME)));
        setType(cursor.getInt(cursor.getColumnIndex(RecipeTable.TYPE)));
        setSteampunk_user_id(cursor.getLong(cursor
                .getColumnIndex(RecipeTable.STEAMPUNK_USER_ID)));
        setPublished(cursor
                .isNull(cursor.getColumnIndex(RecipeTable.PUBLISHED))
                ? false
                : (cursor.getInt(cursor.getColumnIndex(RecipeTable.PUBLISHED)) != 0));
        setGrams(cursor.getDouble(cursor.getColumnIndex(RecipeTable.GRAMS)));
        setTeaspoons(cursor.getDouble(cursor
                .getColumnIndex(RecipeTable.TEASPOONS)));
        setFilter(cursor.getInt(cursor.getColumnIndex(RecipeTable.FILTER)));
        setGrind(cursor.getDouble(cursor.getColumnIndex(RecipeTable.GRIND)));
        setTransaction_state(cursor.getString(cursor
                .getColumnIndex(RecipeTable.TRANSACTION_STATE)));
        setTransaction_record(cursor.getString(cursor
                .getColumnIndex(RecipeTable.TRANSACTION_RECORD)));
        setLocal_id(cursor.getLong(cursor.getColumnIndex(RecipeTable.LOCAL_ID)));
        setStacks(cursor.getString(cursor.getColumnIndex(RecipeTable.STACKS)));
        setUuid(cursor.getString(cursor.getColumnIndex(RecipeTable.UUID)));

    }

    /**
     * Set id
     *
     * @param id from type java.lang.Long
     */
    public void setId(final Long id) {
        this.id = id;
        this.values.put(RecipeTable.ID, id);
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
     * Set name and set content value
     *
     * @param name from type java.lang.String
     */
    public void setName(final String name) {
        this.name = name;
        this.values.put(RecipeTable.NAME, name);
    }

    /**
     * Get name
     *
     * @return name from type java.lang.String
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set type and set content value
     *
     * @param type from type int
     */
    public void setType(final int type) {
        this.type = type;
        this.values.put(RecipeTable.TYPE, type);
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
     * Set steampunk_user_id and set content value
     *
     * @param steampunk_user_id from type long
     */
    public void setSteampunk_user_id(final long steampunk_user_id) {
        this.steampunk_user_id = steampunk_user_id;
        this.values.put(RecipeTable.STEAMPUNK_USER_ID, steampunk_user_id);
    }

    /**
     * Get steampunk_user_id
     *
     * @return steampunk_user_id from type long
     */
    public long getSteampunk_user_id() {
        return this.steampunk_user_id;
    }

    /**
     * Set published and set content value
     *
     * @param published from type boolean
     */
    public void setPublished(final boolean published) {
        this.published = published;
        this.values.put(RecipeTable.PUBLISHED, published);
    }

    /**
     * Get published
     *
     * @return published from type boolean
     */
    public boolean getPublished() {
        return this.published;
    }

    /**
     * Set grams and set content value
     *
     * @param grams from type double
     */
    public void setGrams(final double grams) {
        this.grams = grams;
        this.values.put(RecipeTable.GRAMS, grams);
    }

    /**
     * Get grams
     *
     * @return grams from type double
     */
    public double getGrams() {
        return this.grams;
    }

    /**
     * Set teaspoons and set content value
     *
     * @param teaspoons from type double
     */
    public void setTeaspoons(final double teaspoons) {
        this.teaspoons = teaspoons;
        this.values.put(RecipeTable.TEASPOONS, teaspoons);
    }

    /**
     * Get teaspoons
     *
     * @return teaspoons from type double
     */
    public double getTeaspoons() {
        return this.teaspoons;
    }

    /**
     * Set filter and set content value
     *
     * @param filter from type int
     */
    public void setFilter(final int filter) {
        this.filter = filter;
        this.values.put(RecipeTable.FILTER, filter);
    }

    /**
     * Get filter
     *
     * @return filter from type int
     */
    public int getFilter() {
        return this.filter;
    }

    /**
     * Set grind and set content value
     *
     * @param grind from type double
     */
    public void setGrind(final double grind) {
        this.grind = grind;
        this.values.put(RecipeTable.GRIND, grind);
    }

    /**
     * Get grind
     *
     * @return grind from type double
     */
    public double getGrind() {
        return this.grind;
    }

    /**
     * Set transaction_state and set content value
     *
     * @param transaction_state from type java.lang.String
     */
    public void setTransaction_state(final String transaction_state) {
        this.transaction_state = transaction_state;
        this.values.put(RecipeTable.TRANSACTION_STATE, transaction_state);
    }

    /**
     * Get transaction_state
     *
     * @return transaction_state from type java.lang.String
     */
    public String getTransaction_state() {
        return this.transaction_state;
    }

    /**
     * Set transaction_record and set content value
     *
     * @param transaction_record from type java.lang.String
     */
    public void setTransaction_record(final String transaction_record) {
        this.transaction_record = transaction_record;
        this.values.put(RecipeTable.TRANSACTION_RECORD, transaction_record);
    }

    /**
     * Get transaction_record
     *
     * @return transaction_record from type java.lang.String
     */
    public String getTransaction_record() {
        return this.transaction_record;
    }

    /**
     * Set local_id and set content value
     *
     * @param local_id from type long
     */
    public void setLocal_id(final long local_id) {
        this.local_id = local_id;
        this.values.put(RecipeTable.LOCAL_ID, local_id);
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
     * Set stacks and set content value
     *
     * @param stacks from type java.lang.String
     */
    public void setStacks(final String stacks) {
        this.stacks = stacks;
        this.values.put(RecipeTable.STACKS, stacks);
    }

    /**
     * Get stacks
     *
     * @return stacks from type java.lang.String
     */
    public String getStacks() {
        return this.stacks;
    }

    /**
     * Set uuid and set content value
     *
     * @param uuid from type java.lang.String
     */
    public void setUuid(final String uuid) {
        this.uuid = uuid;
        this.values.put(RecipeTable.UUID, uuid);
    }

    /**
     * Get uuid
     *
     * @return uuid from type java.lang.String
     */
    public String getUuid() {
        return this.uuid;
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
