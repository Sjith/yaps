package com.ogunwale.android.app.yaps.ui;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.google.api.client.http.HttpTransport;
import com.google.api.services.picasa.model.AlbumEntry;
import com.ogunwale.android.app.yaps.R;
import com.ogunwale.android.app.yaps.content.FacebookGraphAlbum;
import com.ogunwale.android.app.yaps.content.RemoteDataAlbumListener;
import com.ogunwale.android.app.yaps.content.RemoteDataRequest;
import com.ogunwale.android.app.yaps.ui.PhotosActivity.Extras;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.Window;

public class TestActivity extends Activity implements RemoteDataAlbumListener {

    private static final String sTAG = TestActivity.class.getSimpleName();

    private UiLifecycleHelper mFacebookUiHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_test);

        mFacebookUiHelper = new UiLifecycleHelper(this, null);
        mFacebookUiHelper.onCreate(savedInstanceState);

        setProgressBarIndeterminateVisibility(true);

        Intent intent = getIntent();
        RemoteDataRequest.RequestType requestType = RemoteDataRequest.RequestType.PICASA_ALBUMS;

        if (intent != null) {
            String action = intent.getAction();

            if (Extras.ACTION_SET_PHOTO_SOURCE_PICASA.equals(action))
                requestType = RemoteDataRequest.RequestType.PICASA_ALBUMS;
            else if (Extras.ACTION_SET_PHOTO_SOURCE_FACEBOOK.equals(action))
                requestType = RemoteDataRequest.RequestType.FACEBOOK_ALBUMS;
        }

        Logger.getLogger(HttpTransport.class.getName()).setLevel(Level.CONFIG);

        new RemoteDataRequest(this, true, requestType, this);
    }

    @Override
    public void RequestComplete(Status status) {
        if (status == Status.SUCCESSFUL)
            Log.i(sTAG, "Request complete");
        else
            Log.e(sTAG, "Request failed: " + status.toString());
    }

    @Override
    public void picasaAlbums(List<AlbumEntry> albums) {
        if (albums != null) {
            for (AlbumEntry album : albums) {
                Log.i(sTAG, "-----------------------------------------------");
                Log.i(sTAG, "Album title: " + album.title);
                Log.i(sTAG, "Updated: " + album.updated);
                Log.i(sTAG, "Album ETag: " + album.etag);
                Log.i(sTAG, "Thumbnail URL: " + album.mediaGroup.thumbnail.url);
                if (album.location != null)
                    Log.i(sTAG, "Location: " + album.location);
                if (album.summary != null)
                    Log.i(sTAG, "Description: " + album.summary);
            }
        }
    }

    @Override
    public void facebookAlbums(List<FacebookGraphAlbum> albums, Session session) {
        if (albums != null) {
            for (FacebookGraphAlbum album : albums) {
                Log.i(sTAG, "-----------------------------------------------");
                Log.i(sTAG, "Album title: " + album.getName());
                Log.i(sTAG, "Updated: " + album.getUpdatedTime());
                Log.i(sTAG, "Cover photo id: " + album.getCoverPhoto());
                Log.i(sTAG, "Location: " + album.getLocation());
                Log.i(sTAG, "Description: " + album.getDescription());
                Log.i(sTAG, "Link: " + album.getLink());
                Log.i(sTAG, "Id: " + album.getId());
            }
        }
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_test, menu);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFacebookUiHelper.onActivityResult(requestCode, resultCode, data);
    }

}
