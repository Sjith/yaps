package com.ogunwale.android.app.yaps.content;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;

import com.facebook.Response;
import com.facebook.Session;
import com.google.api.services.picasa.model.AlbumEntry;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * Class provides access to reading and writing information from the photos
 * database
 *
 * @author ogunwale
 *
 */
public class PhotosProviderAccess {

    private static final String sTAG = PhotosProviderAccess.class.getSimpleName();

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
        public static synchronized Uri add(ContentResolver cr, ContentValues values) {
            return cr.insert(PhotosProvider.AlbumTable.CONTENT_URI, values);
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
                    PhotosProvider.AlbumTable.COLUMN_NAME_EXTERNAL_ID);
            String[] selectionArgs = new String[] { String.valueOf(PhotosSourceEnum.PICASA.getValue()), album.id };

            cursor = cr.query(PhotosProvider.AlbumTable.CONTENT_URI, projection, selection, selectionArgs, null);

            if (cursor != null && cursor.getColumnCount() > 0 && cursor.moveToFirst()) {
                // Album already exist in Database
                String updated = cursor.getString(cursor.getColumnIndexOrThrow(PhotosProvider.AlbumTable.COLUMN_NAME_UPDATED));

                long rowId = cursor.getLong(cursor.getColumnIndexOrThrow(PhotosProvider.AlbumTable._ID));
                uri = ContentUris.appendId(PhotosProvider.AlbumTable.CONTENT_URI.buildUpon(), rowId).build();
                // Update album information in database if the update time has
                // changed.
                if (!updated.equals(album.updated))
                    update(cr, getValues(album), rowId);
            } else {
                // Album does not exist in Database. Add it.
                if (album != null && album.getSelfLink() != null)
                    uri = add(cr, getValues(album));
            }

            if (cursor != null)
                cursor.close();

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
        public static synchronized Uri updateIfChanged(ContentResolver cr, FacebookGraphAlbum album, Session session) {
            Uri uri = null;
            Cursor cursor = null;
            String[] projection = new String[] { PhotosProvider.AlbumTable._ID, PhotosProvider.AlbumTable.COLUMN_NAME_UPDATED };
            String selection = String.format(Locale.getDefault(), "%s=? AND %s=?", PhotosProvider.AlbumTable.COLUMN_NAME_SOURCE,
                    PhotosProvider.AlbumTable.COLUMN_NAME_EXTERNAL_ID);
            String[] selectionArgs = new String[] { String.valueOf(PhotosSourceEnum.FACEBOOK.getValue()), album.getId() };

            cursor = cr.query(PhotosProvider.AlbumTable.CONTENT_URI, projection, selection, selectionArgs, null);

            if (cursor != null && cursor.getColumnCount() > 0 && cursor.moveToFirst()) {
                // Album already exist in Database
                String updated = cursor.getString(cursor.getColumnIndexOrThrow(PhotosProvider.AlbumTable.COLUMN_NAME_UPDATED));

                long rowId = cursor.getLong(cursor.getColumnIndexOrThrow(PhotosProvider.AlbumTable._ID));
                uri = ContentUris.appendId(PhotosProvider.AlbumTable.CONTENT_URI.buildUpon(), rowId).build();
                // Update album information in database if the update time has
                // changed.
                if (!updated.equals(album.getUpdatedTime()))
                    update(cr, getValues(album, session), rowId);
            } else {
                // Album does not exist in Database. Add it.
                if (album != null && album.getId() != null)
                    uri = add(cr, getValues(album, session));
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
        public static synchronized int update(ContentResolver cr, ContentValues values, long rowId) {
            String selection = PhotosProvider.AlbumTable._ID + " LIKE ?";
            String[] selectionArgs = { String.valueOf(rowId) };

            return cr.update(PhotosProvider.AlbumTable.CONTENT_URI, values, selection, selectionArgs);
        }

        /**
         * Get the content values for the specified Facebook album.
         *
         * @param album
         * @return content values for the specified album
         */
        private static ContentValues getValues(FacebookGraphAlbum album, Session session) {
            ContentValues values = new ContentValues();

            values.put(PhotosProvider.AlbumTable.COLUMN_NAME_EXTERNAL_ID, album.getId());
            values.put(PhotosProvider.AlbumTable.COLUMN_NAME_ACCESS, album.getPrivacy());
            values.put(PhotosProvider.AlbumTable.COLUMN_NAME_LINK_SELF, album.getLink());
            values.put(PhotosProvider.AlbumTable.COLUMN_NAME_PHOTOS_COUNT, (album.getCount() == null) ? 0 : Integer.valueOf(album.getCount()));
            values.put(PhotosProvider.AlbumTable.COLUMN_NAME_SOURCE, PhotosSourceEnum.FACEBOOK.getValue());
            values.put(PhotosProvider.AlbumTable.COLUMN_NAME_SUMMARY, album.getDescription());
            values.put(PhotosProvider.AlbumTable.COLUMN_NAME_TITLE, album.getName());
            values.put(PhotosProvider.AlbumTable.COLUMN_NAME_UPDATED, album.getUpdatedTime());
            values.put(PhotosProvider.AlbumTable.COLUMN_NAME_LOCATION, album.getLocation());

            Response response = FacebookRequest.newPhotoRequest(session, album.getCoverPhoto(), null).executeAndWait();
            FacebookGraphPhoto coverPhoto = response.getGraphObjectAs(FacebookGraphPhoto.class);
            if (coverPhoto != null && coverPhoto.getPicture() != null) {
                values.put(PhotosProvider.AlbumTable.COLUMN_NAME_COVER_URL, coverPhoto.getPicture());
                values.put(PhotosProvider.AlbumTable.COLUMN_NAME_COVER_BITMAP, getAlbumCover(coverPhoto.getPicture()));
            }

            return values;
        }

        /**
         * Get the content values for the specified Picasa album.
         *
         * @param album
         * @return content values for the specified album
         */
        private static ContentValues getValues(AlbumEntry album) {
            ContentValues values = new ContentValues();

            values.put(PhotosProvider.AlbumTable.COLUMN_NAME_EXTERNAL_ID, album.id);
            values.put(PhotosProvider.AlbumTable.COLUMN_NAME_ACCESS, album.access);
            values.put(PhotosProvider.AlbumTable.COLUMN_NAME_LINK_EDIT, album.getEditLink());
            values.put(PhotosProvider.AlbumTable.COLUMN_NAME_LINK_FEED, album.getFeedLink());
            values.put(PhotosProvider.AlbumTable.COLUMN_NAME_LINK_SELF, album.getSelfLink());
            values.put(PhotosProvider.AlbumTable.COLUMN_NAME_PHOTOS_COUNT, album.numPhotos);
            values.put(PhotosProvider.AlbumTable.COLUMN_NAME_SOURCE, PhotosSourceEnum.PICASA.getValue());
            values.put(PhotosProvider.AlbumTable.COLUMN_NAME_SUMMARY, album.summary);
            values.put(PhotosProvider.AlbumTable.COLUMN_NAME_TITLE, album.title);
            values.put(PhotosProvider.AlbumTable.COLUMN_NAME_UPDATED, album.updated);
            values.put(PhotosProvider.AlbumTable.COLUMN_NAME_LOCATION, album.location);
            values.put(PhotosProvider.AlbumTable.COLUMN_NAME_COVER_URL, album.mediaGroup.thumbnail.url);
            values.put(PhotosProvider.AlbumTable.COLUMN_NAME_COVER_BITMAP, getAlbumCover(album.mediaGroup.thumbnail.url));

            return values;
        }

        private static byte[] getAlbumCover(String url) {
            byte[] cover = null;
            InputStream in = null;
            ByteArrayOutputStream out = null;

            try {
                in = new BufferedInputStream(new URL(url).openStream());
                out = new ByteArrayOutputStream();
                byte[] buffer = new byte[8 * 1024];
                int bytesRead;

                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }

                cover = out.toByteArray();
            } catch (IOException e) {
                Log.e(sTAG, "Error getting album cover: " + url);
                cover = null;
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                    }
                }
            }

            return cover;
        }

    }
}
