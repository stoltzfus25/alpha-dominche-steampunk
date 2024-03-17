package com.alphadominche.steampunkhmi;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.alphadominche.steampunkhmi.database.tables.RoasterTable;

public class SPLibraryRoasterCursorAdapter extends SimpleCursorAdapter {
    private int mLayout;
    private LayoutInflater mInflater;

    public SPLibraryRoasterCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
        super(context, layout, c, from, to, FLAG_REGISTER_CONTENT_OBSERVER);
        mLayout = layout;
        mInflater = LayoutInflater.from(context);
    }

    public String getId(int position) {
        Cursor c = (Cursor) this.getItem(position);
        return c.getString(c.getColumnIndex(RoasterTable.ID));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(mLayout, null);
    }

    @Override
    public void bindView(View convertView, Context context, Cursor cursor) {
        super.bindView(convertView, context, cursor);
        ((TextView) convertView).setText(cursor.getString(cursor.getColumnIndex(RoasterTable.USERNAME)));
    }
}
