package com.ogunwale.android.app.yaps;

import java.util.Locale;

import com.google.api.services.picasa.model.AlbumEntry;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Class provides access to reading and writing information from the photos
 * database
 *
 * @author ogunwale
 *
 */
public class PhotosProviderAccess {

    /**
     * Class is a collection of accesser methods for album data in the database.
     *
     * @author ogunwale
     *
     */
    public static class Album {

        /**
         * Adds an album to the database
         *
         * @param album
         *            album to insert in database.
         * @return Uri of album in database or null if operation failed.
         */
        public static synchronized Uri add(ContentResolver cr, AlbumEntry album) {
            Uri uri = null;

            if (album != null && album.getSelfLink() != null)
                uri = cr.insert(PhotosProvider.AlbumTable.CONTENT_URI, getValues(album));

            return uri;
        }

        /**
         * Updates the album information in the database if the updated time has
         * changed. Added the album to the database if it is not already in the
         * database.
         *
         * @param album
         *            album to insert/update.
         * @return Uri of the album in the database if the operation was
         *         successful, otherwise returns null.
         */
        public static synchronized Uri updateIfChanged(ContentResolver cr, AlbumEntry album) {
            Uri uri = null;
            Cursor cursor = null;
            String[] projection = new String[] { PhotosProvider.AlbumTable._ID, PhotosProvider.AlbumTable.COLUMN_NAME_UPDATED };
            String selection = String.format(Locale.getDefault(), "%s=? AND %s=?", PhotosProvider.AlbumTable.COLUMN_NAME_SOURCE,
                    PhotosProvider.AlbumTable.COLUMN_NAME_LINK_SELF);
            String[] selectionArgs = new String[] { String.valueOf(PhotosSourceEnum.PICASA.getValue()), album.getSelfLink() };

            cursor = cr.query(PhotosProvider.AlbumTable.CONTENT_URI, projection, selection, selectionArgs, null);

            if (cursor != null && cursor.getColumnCount() > 0 && cursor.moveToFirst()) {
                // Album already exist in Database
                String updated = cursor.getString(cursor.getColumnIndexOrThrow(PhotosProvider.AlbumTable.COLUMN_NAME_UPDATED));

                long rowId = cursor.getLong(cursor.getColumnIndexOrThrow(PhotosProvider.AlbumTable._ID));
                uri = ContentUris.appendId(PhotosProvider.AlbumTable.CONTENT_URI.buildUpon(), rowId).build();
                // Update album information in database if the update time has
                // changed.
                if (!updated.equals(album.updated))
                    update(cr, album, rowId);
            } else {
                // Album does not exist in Database. Add it.
                uri = add(cr, album);
            }

            if (cursor != null)
                cursor.close();

            return uri;
        }

        /**
         * Updates the database entry with the input rowId with the input album
         * information.
         *
         * @param album
         * @param rowId
         */
        public static synchronized int update(ContentResolver cr, AlbumEntry album, long rowId) {
            String selection = PhotosProvider.AlbumTable._ID + " LIKE ?";
            String[] selectionArgs = { String.valueOf(rowId) };

            return cr.update(PhotosProvider.AlbumTable.CONTENT_URI, getValues(album), selection, selectionArgs);
        }

        /**
         * Get the content values for the specified Picasa album.
         *
         * @param album
         * @return content values for the specified album
         */
        private static ContentValues getValues(AlbumEntry album) {
            ContentValues values = new ContentValues();

            values.put(PhotosProvider.AlbumTable.COLUMN_NAME_ACCESS, album.access);
            values.put(PhotosProvider.AlbumTable.COLUMN_NAME_LINK_EDIT, album.getEditLink());
            values.put(PhotosProvider.AlbumTable.COLUMN_NAME_LINK_FEED, album.getFeedLink());
            values.put(PhotosProvider.AlbumTable.COLUMN_NAME_LINK_SELF, album.getSelfLink());
            values.put(PhotosProvider.AlbumTable.COLUMN_NAME_PHOTOS_COUNT, album.numPhotos);
            values.put(PhotosProvider.AlbumTable.COLUMN_NAME_SOURCE, PhotosSourceEnum.PICASA.getValue());
            values.put(PhotosProvider.AlbumTable.COLUMN_NAME_SUMMARY, album.summary);
            values.put(PhotosProvider.AlbumTable.COLUMN_NAME_TITLE, album.title);
            values.put(PhotosProvider.AlbumTable.COLUMN_NAME_UPDATED, album.updated);

            return values;
        }
    }
}
