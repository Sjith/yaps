package com.ogunwale.android.app.yaps;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

/**
 * Activity display photos based on a selected source (Picasa or Facebook) and
 * sort type (Albums, Locations, ...)
 *
 * @author ogunwale
 *
 */
public class PhotosActivity extends Activity {

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

    private static final int PHOTO_SOURCE_SELECTION_FACEBOOK = 0;
    private static final int PHOTO_SOURCE_SELECTION_PICASA = 1;

    private static int mSourceSelection = PHOTO_SOURCE_SELECTION_FACEBOOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (intent != null) {
            String action = intent.getAction();

            if (Extras.ACTION_SET_PHOTO_SOURCE_PICASA.equals(action))
                mSourceSelection = PHOTO_SOURCE_SELECTION_PICASA;
            else if (Extras.ACTION_SET_PHOTO_SOURCE_FACEBOOK.equals(action))
                mSourceSelection = PHOTO_SOURCE_SELECTION_FACEBOOK;
        }

        setContentView(R.layout.activity_photos);

        // Set-up thumbnail grid
        GridView gridView = (GridView) findViewById(R.id.photo_gridview);
        gridView.setAdapter(new PhotoThumbnailAdapter(this));
        gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // TODO
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar_sort_photos_providers, menu);
        return true;
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
            sourceSpinner.setSelection(mSourceSelection);
            sourceSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            // Set-up sort selection spinner
            Spinner sortSpinner = (Spinner) view.findViewById(R.id.photo_sort);
            adapter = ArrayAdapter.createFromResource(mContext, R.array.sort_photos_by, android.R.layout.simple_spinner_dropdown_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sortSpinner.setAdapter(adapter);
            sortSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            return view;
        }
    }
}
