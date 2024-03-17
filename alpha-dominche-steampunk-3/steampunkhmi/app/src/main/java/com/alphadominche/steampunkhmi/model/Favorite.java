package com.alphadominche.steampunkhmi.model;

import android.content.ContentValues;
import android.database.Cursor;
import com.alphadominche.steampunkhmi.database.tables.FavoriteTable;

/**
 * Generated model class for usage in your application, defined by classifiers in ecore diagram
 * <p/>
 * Generated Class. Do not modify!
 *
 * @author MDSDACP Team - goetzfred@fh-bingen.de
 * @date 2014.05.09
 */
public class Favorite {

    private Long id;
    private long user;
    private long recipe_id;
    private String uuid;
    private String recipe_uuid;

    private final ContentValues values = new ContentValues();

    public Favorite() {
    }

    public Favorite(final Cursor cursor) {
        setId(cursor.getLong(cursor.getColumnIndex(FavoriteTable.ID)));
        setUser(cursor.getLong(cursor.getColumnIndex(FavoriteTable.USER)));
        setRecipe_id(cursor.getLong(cursor
                .getColumnIndex(FavoriteTable.RECIPE_ID)));
        setUuid(cursor.getString(cursor.getColumnIndex(FavoriteTable.UUID)));
        setRecipe_uuid(cursor.getString(cursor
                .getColumnIndex(FavoriteTable.RECIPE_UUID)));

    }

    /**
     * Set id
     *
     * @param id from type java.lang.Long
     */
    public void setId(final Long id) {
        this.id = id;
        this.values.put(FavoriteTable.ID, id);
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
     * Set user and set content value
     *
     * @param user from type long
     */
    public void setUser(final long user) {
        this.user = user;
        this.values.put(FavoriteTable.USER, user);
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
     * Set recipe_id and set content value
     *
     * @param recipe_id from type long
     */
    public void setRecipe_id(final long recipe_id) {
        this.recipe_id = recipe_id;
        this.values.put(FavoriteTable.RECIPE_ID, recipe_id);
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
     * Set uuid and set content value
     *
     * @param uuid from type java.lang.String
     */
    public void setUuid(final String uuid) {
        this.uuid = uuid;
        this.values.put(FavoriteTable.UUID, uuid);
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
     * Set recipe_uuid and set content value
     *
     * @param recipe_uuid from type java.lang.String
     */
    public void setRecipe_uuid(final String recipe_uuid) {
        this.recipe_uuid = recipe_uuid;
        this.values.put(FavoriteTable.RECIPE_UUID, recipe_uuid);
    }

    /**
     * Get recipe_uuid
     *
     * @return recipe_uuid from type java.lang.String
     */
    public String getRecipe_uuid() {
        return this.recipe_uuid;
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
