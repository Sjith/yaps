package com.ogunwale.android.app.yaps.ui;

import java.util.List;
import java.util.Locale;

import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.google.api.services.picasa.model.AlbumEntry;
import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayAdapterView.OnItemClickListener;
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
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Activity display photos based on a selected source (Picasa or Facebook) and
 * sort type (Albums, Locations, ...)
 *
 * @author ogunwale
 *
 */
public class PhotosActivity extends Activity implements LoaderCallbacks<Cursor>, RemoteDataAlbumListener {

    private static final String sTAG = PhotosActivity.class.getSimpleName();

    /**
     * Intents extras and actions consumed by this activity when it starts.
     *
     * @author ogunwale
     *
     */
    public static interface Extras {
        public static final String INTENT_PREFIX = "com.ogunwale.android.apps.yaps.";

        /**
         * Activity starts with the photo source set to Picasa
         */
        public static final String ACTION_SET_PHOTO_SOURCE_PICASA = INTENT_PREFIX + "ACTION_SET_PHOTO_SOURCE_PICASA";

        /**
         * Activity starts with the photo source set to Facebook
         */
        public static final String ACTION_SET_PHOTO_SOURCE_FACEBOOK = INTENT_PREFIX + "ACTION_SET_PHOTO_SOURCE_FACEBOOK";

        /**
         * Request for album data refresh is complete.
         */
        public static final String ACTION_REMOTE_ALBUM_REQUEST_COMPLETE = INTENT_PREFIX + "ACTION_REMOTE_ALBUM_REQUEST_COMPLETE";
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
                } else if (Extras.ACTION_REMOTE_ALBUM_REQUEST_COMPLETE.equals(action)) {
                    mGettingRemoteData = false;
                    // invalidate options menu so we can hide the refresh
                    // button.
                    invalidateOptionsMenu();
                    // hide progress spinner.
                    setProgressBarIndeterminateVisibility(false);
                }
            }
        }
    };

    private static PhotosSourceEnum mSourceSelection;

    private PhotosSimpleCursorAdapter mAdapter;

    private TwoWayGridView mGridView;

    private UiLifecycleHelper mFacebookUiHelper;

    private boolean mGettingRemoteData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        mFacebookUiHelper = new UiLifecycleHelper(this, null);
        mFacebookUiHelper.onCreate(savedInstanceState);

        mSourceSelection = SettingsManager.getInstance(getApplicationContext()).getAlbumSelection();

        setContentView(R.layout.activity_photos);

        // Set-up cursor adapter
        mAdapter = new PhotosSimpleCursorAdapter(this);

        // Set-up thumbnail grid
        mGridView = (TwoWayGridView) findViewById(R.id.photo_gridview);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(TwoWayAdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = mAdapter.getCursor();
                if (cursor.moveToPosition(position)) {
                    DialogFragment df = TransferDialogFragment.newInstance(cursor, id);
                    df.show(getFragmentManager(), "transfer_dialog");
                } else {
                    Log.e(sTAG, "Can not move cursor to: " + position);
                }
            }
        });

        // Prepare the database loader.
        getLoaderManager().initLoader(0, null, this);

        // Register for local broadcasts
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(Extras.ACTION_SET_PHOTO_SOURCE_FACEBOOK);
        iFilter.addAction(Extras.ACTION_SET_PHOTO_SOURCE_PICASA);
        iFilter.addAction(Extras.ACTION_REMOTE_ALBUM_REQUEST_COMPLETE);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mLocalBroadcastReceiver, iFilter);
    }

    public static class TransferDialogFragment extends DialogFragment {

        private static final String ROW_ID = "row_id";
        private static final String IMAGE_BLOB = "image_blob";
        private static final String TITLE = "title";
        private static final String LOCATION = "location";
        private static final String PHOTOS_COUNT = "photos_count";

        public static TransferDialogFragment newInstance(Cursor cursor, long rowId) {
            TransferDialogFragment frag = new TransferDialogFragment();

            Bundle args = new Bundle();
            args.putLong(ROW_ID, rowId);
            args.putByteArray(IMAGE_BLOB, cursor.getBlob(cursor.getColumnIndexOrThrow(PhotosProvider.AlbumTable.COLUMN_NAME_COVER_BITMAP)));
            args.putString(TITLE, cursor.getString(cursor.getColumnIndexOrThrow(PhotosProvider.AlbumTable.COLUMN_NAME_TITLE)));
            args.putString(LOCATION, cursor.getString(cursor.getColumnIndexOrThrow(PhotosProvider.AlbumTable.COLUMN_NAME_LOCATION)));
            args.putInt(PHOTOS_COUNT, cursor.getInt(cursor.getColumnIndexOrThrow(PhotosProvider.AlbumTable.COLUMN_NAME_PHOTOS_COUNT)));

            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final long rowId = getArguments().getLong(ROW_ID);

            LayoutInflater li = LayoutInflater.from(getActivity());
            View view = li.inflate(R.layout.layout_transfer_dialog, null);

            ImageView image = (ImageView) view.findViewById(R.id.transfer_dialog_image);
            byte[] blob = getArguments().getByteArray(IMAGE_BLOB);
            if (blob != null && blob.length > 0)
                image.setImageBitmap(BitmapFactory.decodeByteArray(blob, 0, blob.length));

            TextView text = (TextView) view.findViewById(R.id.transfer_dialog_text);
            String title = getArguments().getString(TITLE);
            String location = getArguments().getString(LOCATION);
            int count = getArguments().getInt(PHOTOS_COUNT);
            text.setText(location + "\n" + count);

            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle(title);
            dialog.setView(view);
            dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            int positiveString = (mSourceSelection == PhotosSourceEnum.PICASA) ? R.string.transfer_to_facebook : R.string.transfer_to_picasa;
            dialog.setPositiveButton(positiveString, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO
                }
            });

            return dialog.create();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar_sort_photos_providers, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_item_refresh: {
            mGettingRemoteData = true;
            // Request/update album data from source
            updateAlbumData();
            // invalidate options menu so we can hide the refresh button.
            invalidateOptionsMenu();
            // display progress spinner.
            setProgressBarIndeterminateVisibility(true);
            return true;
        }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mGettingRemoteData)
            menu.removeItem(R.id.menu_item_refresh);
        else if (menu.findItem(R.id.menu_item_refresh) == null)
            menu.add(R.id.menu_item_refresh);

        return super.onPrepareOptionsMenu(menu);
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

            new RemoteDataRequest(this, true, requestType, this);
            break;
        }
        case INVALID:
        default:
            break;

        }
    }

    @Override
    public void RequestComplete(Status status) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Extras.ACTION_REMOTE_ALBUM_REQUEST_COMPLETE));
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

        //@formatter:off
        String[] projection = new String[] {
                    PhotosProvider.AlbumTable._ID,
                    PhotosProvider.AlbumTable.COLUMN_NAME_COVER_BITMAP,
                    PhotosProvider.AlbumTable.COLUMN_NAME_TITLE,
                    PhotosProvider.AlbumTable.COLUMN_NAME_PHOTOS_COUNT,
                    PhotosProvider.AlbumTable.COLUMN_NAME_LOCATION };
        String selection = String.format(Locale.getDefault(), "%s=?", PhotosProvider.AlbumTable.COLUMN_NAME_SOURCE);
        String[] selectionArgs = new String[] { String.valueOf(mSourceSelection.getValue()) };
        //@formatter:on

        // Create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(this, PhotosProvider.AlbumTable.CONTENT_URI, projection, selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in. (The framework will take care of closing the
        // old cursor once we return.)
        mAdapter.swapCursor(data);
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
