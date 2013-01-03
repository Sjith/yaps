package com.ogunwale.android.app.yaps.ui;

import java.util.List;
import java.util.Locale;

import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.google.api.services.picasa.model.AlbumEntry;
import com.jess.ui.TwoWayGridView;
import com.ogunwale.android.app.yaps.R;
import com.ogunwale.android.app.yaps.content.FacebookGraphAlbum;
import com.ogunwale.android.app.yaps.content.PhotosProvider;
import com.ogunwale.android.app.yaps.content.PhotosProviderAccess;
import com.ogunwale.android.app.yaps.content.PhotosSourceEnum;
import com.ogunwale.android.app.yaps.content.RemoteDataAlbumListener;
import com.ogunwale.android.app.yaps.content.RemoteDataRequest;
import com.ogunwale.android.app.yaps.content.SettingsManager;

import android.os.Bundle;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

/**
 * Activity display photos based on a selected source (Picasa or Facebook) and
 * sort type (Albums, Locations, ...)
 *
 * @author ogunwale
 *
 */
public class PhotosActivity extends Activity implements LoaderCallbacks<Cursor> {

    /**
     * Intents extras and actions consumed by this activity when it starts.
     *
     * @author ogunwale
     *
     */
    public static class Extras {
        public static final String INTENT_PREFIX = "com.ogunwale.android.apps.yaps.";

        /**
         * Activity starts with the photo source set to Picasa
         */
        public static final String ACTION_SET_PHOTO_SOURCE_PICASA = INTENT_PREFIX + "ACTION_SET_PHOTO_SOURCE_PICASA";

        /**
         * Activity starts with the photo source set to Facebook
         */
        public static final String ACTION_SET_PHOTO_SOURCE_FACEBOOK = INTENT_PREFIX + "ACTION_SET_PHOTO_SOURCE_FACEBOOK";
    }

    /**
     * Processes local broadcast for this activity
     */
    private BroadcastReceiver mLocalBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();

                if (Extras.ACTION_SET_PHOTO_SOURCE_FACEBOOK.equals(action) && mSourceSelection != PhotosSourceEnum.FACEBOOK) {
                    changeSource(PhotosSourceEnum.FACEBOOK);
                } else if (Extras.ACTION_SET_PHOTO_SOURCE_PICASA.equals(action) && mSourceSelection != PhotosSourceEnum.PICASA) {
                    changeSource(PhotosSourceEnum.PICASA);
                }
            }
        }
    };

    private static PhotosSourceEnum mSourceSelection;

    private PhotosSimpleCursorAdapter mAdapter;

    private TwoWayGridView mGridView;

    private UiLifecycleHelper mFacebookUiHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFacebookUiHelper = new UiLifecycleHelper(this, null);
        mFacebookUiHelper.onCreate(savedInstanceState);

        mSourceSelection = SettingsManager.getInstance(getApplicationContext()).getAlbumSelection();

        setContentView(R.layout.activity_photos);

        // Set-up cursor adapter
        String[] from = new String[] { PhotosProvider.AlbumTable.COLUMN_NAME_COVER_BITMAP, PhotosProvider.AlbumTable.COLUMN_NAME_TITLE,
                PhotosProvider.AlbumTable.COLUMN_NAME_PHOTOS_COUNT };
        int[] to = new int[] { R.id.thumbnail_layout, R.id.thumbnail_description, R.id.thumbnail_count };
        mAdapter = new PhotosSimpleCursorAdapter(this, R.layout.layout_photo_thumbnail, null, from, to, 0);

        // Set-up thumbnail grid
        mGridView = (TwoWayGridView) findViewById(R.id.photo_gridview);
        mGridView.setAdapter(mAdapter);
        // mGridView.setOnClickListener(new
        // TwoWayAdapterView.OnItemClickListener() {
        // @Override
        // public void onItemClick(TwoWayAdapterView<?> parent, View view, int
        // position, long id) {
        // TODO Auto-generated method stub
        // }
        // });

        // Request/update album data from source
        updateAlbumData();

        // Prepare the database loader.
        getLoaderManager().initLoader(0, null, this);

        // Register for local broadcasts
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(Extras.ACTION_SET_PHOTO_SOURCE_FACEBOOK);
        iFilter.addAction(Extras.ACTION_SET_PHOTO_SOURCE_PICASA);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mLocalBroadcastReceiver, iFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar_sort_photos_providers, menu);
        return true;
    }

    /**
     * Updates album data
     */
    private void updateAlbumData() {
        switch (mSourceSelection) {
        case FACEBOOK:
        case PICASA: {
            RemoteDataRequest.RequestType requestType = (mSourceSelection == PhotosSourceEnum.FACEBOOK) ? RemoteDataRequest.RequestType.FACEBOOK_ALBUMS
                    : RemoteDataRequest.RequestType.PICASA_ALBUMS;

            new RemoteDataRequest(this, false, requestType, new RemoteDataAlbumListener() {
                @Override
                public void RequestComplete(Status status) {
                    // TODO
                }

                @Override
                public void facebookAlbums(List<FacebookGraphAlbum> albums, Session session) {
                    if (albums != null) {
                        for (FacebookGraphAlbum album : albums) {
                            PhotosProviderAccess.Album.updateIfChanged(getContentResolver(), album, session);
                        }
                    }
                }

                @Override
                public void picasaAlbums(List<AlbumEntry> albums) {
                    if (albums != null) {
                        for (AlbumEntry album : albums) {
                            PhotosProviderAccess.Album.updateIfChanged(getContentResolver(), album);
                        }
                    }

                }
            });
            break;
        }
        case INVALID:
        default:
            break;

        }
    }

    /**
     * Class implements action provider used to display photo source and sort
     * selection in the action bar.
     *
     * @author ogunwale
     *
     */
    public static class PhotosActionProvider extends ActionProvider {

        private final Context mContext;

        public PhotosActionProvider(Context context) {
            super(context);
            mContext = context;
        }

        @Override
        public View onCreateActionView() {
            LayoutInflater li = LayoutInflater.from(mContext);
            View view = li.inflate(R.layout.action_bar_sort_photos_providers, null);

            // Set-up source selection spinner
            Spinner sourceSpinner = (Spinner) view.findViewById(R.id.photo_source);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext, R.array.sources,
                    android.R.layout.simple_spinner_dropdown_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sourceSpinner.setAdapter(adapter);
            sourceSpinner.setSelection(mSourceSelection.getValue());
            sourceSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position != mSourceSelection.getValue()) {
                        Intent intent = new Intent();

                        switch (PhotosSourceEnum.getEnum(position)) {
                        case FACEBOOK:
                            intent.setAction(Extras.ACTION_SET_PHOTO_SOURCE_FACEBOOK);
                            break;
                        case PICASA:
                            intent.setAction(Extras.ACTION_SET_PHOTO_SOURCE_PICASA);
                            break;
                        case INVALID:
                        default:
                            break;
                        }

                        if (intent.getAction() != null)
                            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            return view;
        }
    }

    /**
     * Change photo source
     */
    private void changeSource(PhotosSourceEnum source) {
        if (source != mSourceSelection) {
            mSourceSelection = source;
            SettingsManager.getInstance(getApplicationContext()).setAlbumSelection(mSourceSelection);
            updateAlbumData();
            getLoaderManager().restartLoader(0, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created. We
        // only has one Loader, so we don't care about the ID.
        String[] projection = new String[] { PhotosProvider.AlbumTable._ID, PhotosProvider.AlbumTable.COLUMN_NAME_COVER_BITMAP,
                PhotosProvider.AlbumTable.COLUMN_NAME_TITLE, PhotosProvider.AlbumTable.COLUMN_NAME_PHOTOS_COUNT };
        String selection = String.format(Locale.getDefault(), "%s=?", PhotosProvider.AlbumTable.COLUMN_NAME_SOURCE);
        String[] selectionArgs = new String[] { String.valueOf(mSourceSelection.getValue()) };

        // Create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(this, PhotosProvider.AlbumTable.CONTENT_URI, projection, selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in. (The framework will take care of closing the
        // old cursor once we return.)
        mAdapter.swapCursor(data);

        // TODO
        // ProgressBar pb = (ProgressBar) findViewById(R.id.photo_progress_bar);
        // pb.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed. We need to make sure we are no longer
        // using it.
        mAdapter.swapCursor(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        mFacebookUiHelper.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mFacebookUiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        mFacebookUiHelper.onPause();
    }

    protected void onDestory() {
        super.onDestroy();
        mFacebookUiHelper.onDestroy();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mLocalBroadcastReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFacebookUiHelper.onActivityResult(requestCode, resultCode, data);
    }
}
