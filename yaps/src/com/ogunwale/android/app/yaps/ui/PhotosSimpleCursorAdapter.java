package com.ogunwale.android.app.yaps.ui;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Sub-class of simple cursor adapter to allow for special processing of the
 * image view since the data is stored in the database for albums.
 *
 * @author ogunwale
 *
 */
public class PhotosSimpleCursorAdapter extends SimpleCursorAdapter {

    public PhotosSimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.support.v4.widget.SimpleCursorAdapter#bindView(android.view.View,
     * android.content.Context, android.database.Cursor)
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ViewBinder binder = getViewBinder();
        final int count = mTo.length;
        final int[] from = mFrom;
        final int[] to = mTo;

        for (int i = 0; i < count; i++) {
            final View v = view.findViewById(to[i]);
            if (v != null) {
                boolean bound = false;
                if (binder != null) {
                    bound = binder.setViewValue(v, cursor, from[i]);
                }

                if (!bound) {
                    if (v instanceof TextView) {
                        String text = cursor.getString(from[i]);
                        if (text == null)
                            text = "";
                        setViewText((TextView) v, text);
                    } else if (v instanceof ImageView) {
                        byte[] blob = cursor.getBlob(from[i]);
                        setViewImage((ImageView) v, blob);
                    } else {
                        throw new IllegalStateException(v.getClass().getName() + " is not a "
                                + " view that can be bounds by this SimpleCursorAdapter");
                    }
                }
            }
        }
    }

    public void setViewImage(ImageView v, byte[] value) {
        v.setImageBitmap(BitmapFactory.decodeByteArray(value, 0, value.length));
    }

}
