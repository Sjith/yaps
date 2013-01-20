/**
 * Copyright 2013 Olawale Ogunwale
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ogunwale.android.app.yaps.ui;

import com.ogunwale.android.app.yaps.R;
import com.ogunwale.android.app.yaps.content.PhotosProvider;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Sub-class of simple cursor adapter to allow for special processing of the
 * image view since the data is stored in the database for albums.
 *
 * @author ogunwale
 *
 */
public class PhotosSimpleCursorAdapter extends SimpleCursorAdapter {

    //@formatter:off
    private static final String[] FROM_DB_COLUMNS = new String[] {
        PhotosProvider.AlbumTable.COLUMN_NAME_COVER_BITMAP,
        PhotosProvider.AlbumTable.COLUMN_NAME_TITLE,
        PhotosProvider.AlbumTable.COLUMN_NAME_PHOTOS_COUNT
        };

    private static final int[] TO_UI_VIEWS = new int[] {
        R.id.thumbnail_layout,
        R.id.thumbnail_description,
        R.id.thumbnail_count
        };
    //@formatter:on

    public PhotosSimpleCursorAdapter(Context context) {
        super(context, R.layout.layout_photo_thumbnail, null, FROM_DB_COLUMNS, TO_UI_VIEWS, 0);
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
                        if (blob != null && blob.length > 0)
                            setViewImage((ImageView) v, blob);
                        else
                            setDefaultBackground(context, (RelativeLayout) v);
                    } else if (v instanceof RelativeLayout) {
                        byte[] blob = cursor.getBlob(from[i]);
                        if (blob != null && blob.length > 0)
                            setLayoutBackground(context, (RelativeLayout) v, blob);
                        else
                            setDefaultBackground(context, (RelativeLayout) v);
                    } else {
                        throw new IllegalStateException(v.getClass().getName() + " is not a "
                                + " view that can be bounds by this SimpleCursorAdapter");
                    }
                }
            }
        }
    }

    private void setDefaultBackground(Context context, RelativeLayout v) {
        v.setBackgroundResource(android.R.drawable.screen_background_dark_transparent);
    }

    private void setViewImage(ImageView v, byte[] value) {
        v.setImageBitmap(BitmapFactory.decodeByteArray(value, 0, value.length));
    }

    private void setLayoutBackground(Context context, RelativeLayout v, byte[] value) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(value, 0, value.length);
        v.setBackground(new BitmapDrawable(v.getContext().getResources(), bitmap));
    }

}
