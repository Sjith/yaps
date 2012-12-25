package com.ogunwale.android.app.yaps;

import com.google.api.services.picasa.model.AlbumEntry;
import com.google.api.services.picasa.model.UserFeed;
import com.ogunwale.android.app.yaps.R;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class PicasaTestActivity extends Activity {

    private static final String sTAG = PicasaTestActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        new PicasaDataTimerTask(this, false, new PicasaDataAlbumListener() {
            @Override
            public void RequestFailed(FailureCause cause) {
                Log.e(sTAG, "Request failed: " + FailureCause.toString(cause));
            }

            @Override
            public void RequestComplete() {
                Log.i(sTAG, "Request complete");
            }

            @Override
            public void userFeed(UserFeed feed) {
                Log.i(sTAG, "User: " + feed.author.name);
                Log.i(sTAG, "Albums count: " + feed.totalResults);
            }

            @Override
            public void albumEntry(AlbumEntry album) {
                Log.i(sTAG, "-----------------------------------------------");
                Log.i(sTAG, "Album title: " + album.title);
                Log.i(sTAG, "Updated: " + album.updated);
                Log.i(sTAG, "Album ETag: " + album.etag);
                if (album.summary != null)
                    Log.i(sTAG, "Description: " + album.summary);
            }
        });
    }

    protected void onDestory() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_test, menu);
        return true;
    }

}
