/**
 *
 */
package com.ogunwale.android.app.yaps.content;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Database provider for information relating to photos
 *
 * @author ogunwale
 *
 */
public class PhotosProvider extends ContentProvider {

    public static final String AUTHORITY = "com.ogunwale.android.app.yaps.content.PhotosProvider";

    public static final int DATABASE_VERSION = 5;
    public static final int DELETE_VERSION_MIN = 5;
    public static final String DATABASE_NAME = "Photos.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String BLOB_TYPE = " BLOB";
    private static final String COMMA_SEP = ",";

    private static final int ALBUMS_CODE = 1;
    private static final int ALBUMS_ID_CODE = 2;

    /**
     * Class defines the information stored for albums in the database.
     *
     * @author ogunwale
     *
     */
    public interface AlbumTable extends BaseColumns {
        // Table name
        public static final String TABLE_NAME = "album";

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.garmin.album";
        public static final String CONTENT_ITEMTYPE = "vnd.android.cursor.dir/vnd.garmin.album";

        // columns
        public static final String COLUMN_NAME_EXTERNAL_ID = "external_id";
        public static final String COLUMN_NAME_SOURCE = "source";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_UPDATED = "updated";
        public static final String COLUMN_NAME_LINK_SELF = "link_self";
        public static final String COLUMN_NAME_LINK_EDIT = "link_edit";
        public static final String COLUMN_NAME_LINK_FEED = "link_feed";
        public static final String COLUMN_NAME_ACCESS = "access";
        public static final String COLUMN_NAME_SUMMARY = "summary";
        public static final String COLUMN_NAME_PHOTOS_COUNT = "photos_count";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_COVER_URL = "cover_url";
        public static final String COLUMN_NAME_COVER_BITMAP = "cover_bitmap";

        // Create statement
        // @formatter:off
        public static final String CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_EXTERNAL_ID + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_SOURCE + INTEGER_TYPE + COMMA_SEP +
                COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_UPDATED + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_LINK_SELF + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_LINK_EDIT + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_LINK_FEED + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_ACCESS + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_SUMMARY + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_PHOTOS_COUNT + INTEGER_TYPE + COMMA_SEP +
                COLUMN_NAME_LOCATION + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_COVER_URL + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_COVER_BITMAP + BLOB_TYPE +
                " )";
        // @formatter:on

        // Delete statement
        public static final String DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

    /**
     * Database helper class for information relating relating to photos.
     *
     * @author ogunwale
     *
     */
    public static class PhotosDatabaseHelper extends SQLiteOpenHelper {

        public PhotosDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database
         * .sqlite .SQLiteDatabase)
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(AlbumTable.CREATE_ENTRIES);
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database
         * .sqlite .SQLiteDatabase, int, int)
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion < DELETE_VERSION_MIN) {
                db.execSQL(AlbumTable.DELETE_ENTRIES);
                onCreate(db);
            }
        }

    }

    private final UriMatcher urlMatcher;

    private SQLiteDatabase db;

    public PhotosProvider() {
        urlMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        urlMatcher.addURI(AUTHORITY, AlbumTable.TABLE_NAME, ALBUMS_CODE);
        urlMatcher.addURI(AUTHORITY, AlbumTable.TABLE_NAME + "/#", ALBUMS_ID_CODE);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.content.ContentProvider#onCreate()
     */
    @Override
    public boolean onCreate() {
        PhotosDatabaseHelper helper = new PhotosDatabaseHelper(getContext());
        db = helper.getWritableDatabase();
        return db != null;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.content.ContentProvider#query(android.net.Uri,
     * java.lang.String[], java.lang.String, java.lang.String[],
     * java.lang.String)
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String order) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        int match = urlMatcher.match(uri);
        String sortOrder = null;

        switch (match) {
        case ALBUMS_CODE:
            qb.setTables(AlbumTable.TABLE_NAME);
            sortOrder = (order != null) ? order : AlbumTable._ID;
            break;
        case ALBUMS_ID_CODE:
            qb.setTables(AlbumTable.TABLE_NAME);
            qb.appendWhere("_id=" + uri.getPathSegments().get(1));
            break;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.content.ContentProvider#getType(android.net.Uri)
     */
    @Override
    public String getType(Uri uri) {
        String type = null;
        int match = urlMatcher.match(uri);
        switch (match) {
        case ALBUMS_CODE:
            type = AlbumTable.CONTENT_TYPE;
            break;
        case ALBUMS_ID_CODE:
            type = AlbumTable.CONTENT_ITEMTYPE;
            break;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return type;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.content.ContentProvider#insert(android.net.Uri,
     * android.content.ContentValues)
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = urlMatcher.match(uri);
        String table = null;

        switch (match) {
        case ALBUMS_CODE:
            table = AlbumTable.TABLE_NAME;
            break;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        long rowId = db.insert(table, null, values);
        if (rowId < 0)
            throw new SQLiteException("Failed to insert row into " + uri);

        Uri returnUri = ContentUris.appendId(AlbumTable.CONTENT_URI.buildUpon(), rowId).build();
        getContext().getContentResolver().notifyChange(uri, null, true);
        return returnUri;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.content.ContentProvider#delete(android.net.Uri,
     * java.lang.String, java.lang.String[])
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = urlMatcher.match(uri);
        String table = null;

        switch (match) {
        case ALBUMS_CODE:
            table = AlbumTable.TABLE_NAME;
            break;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        int count = db.delete(table, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null, true);
        return count;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.content.ContentProvider#update(android.net.Uri,
     * android.content.ContentValues, java.lang.String, java.lang.String[])
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = urlMatcher.match(uri);
        int count = 0;
        String segment = null;

        switch (match) {
        case ALBUMS_CODE:
            count = db.update(AlbumTable.TABLE_NAME, values, selection, selectionArgs);
            break;
        case ALBUMS_ID_CODE:
            segment = uri.getPathSegments().get(1);
            count = db.update(AlbumTable.TABLE_NAME, values, "_id=" + segment
                    + ((selection != null && !selection.equals("")) ? " AND (" + selection + ')' : ""), selectionArgs);
            break;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null, true);
        return count;
    }
}
