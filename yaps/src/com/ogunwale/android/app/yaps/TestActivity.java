package com.ogunwale.android.app.yaps;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.picasa.PicasaClient;
import com.google.api.services.picasa.PicasaUrl;
import com.google.api.services.picasa.model.AlbumEntry;
import com.google.api.services.picasa.model.UserFeed;
import com.ogunwale.android.app.yaps.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;

public class TestActivity extends Activity {

    private static final String sTAG = TestActivity.class.getSimpleName();

    private static final String APP_NAME = "picaface";

    private String mAuthToken = null;
    private String mAccountName = null;

    private class GetAlbumsTask extends TimerTask {
        @Override
        public void run() {
            HttpTransport transport = new NetHttpTransport();
            HttpRequestFactory factory = transport.createRequestFactory(new HttpRequestInitializer() {
                @Override
                public void initialize(HttpRequest request) throws IOException {
                    GoogleHeaders headers = new GoogleHeaders();
                    headers.setApplicationName(APP_NAME);
                    headers.setGDataVersion("2");
                    headers.setGoogleLogin(mAuthToken);
                    request.setHeaders(headers);
                }
            });

            PicasaClient picasaClient = new PicasaClient(factory);
            picasaClient.setApplicationName(APP_NAME);

            // build URL for the default user feed of albums
            PicasaUrl url = PicasaUrl.relativeToRoot("feed/api/user/default");
            // execute GData request for the feed
            UserFeed feed = null;
            try {
                String nextLink = null;
                feed = picasaClient.executeGetUserFeed(url);
                do {
                    Log.i(sTAG, "User: " + feed.author.name);
                    Log.i(sTAG, "Total number of albums: " + feed.totalResults);
                    // show albums
                    if (feed.albums != null) {
                        for (AlbumEntry album : feed.albums) {
                            Log.i(sTAG, "-----------------------------------------------");
                            Log.i(sTAG, "Album title: " + album.title);
                            Log.i(sTAG, "Updated: " + album.updated);
                            Log.i(sTAG, "Album ETag: " + album.etag);
                            if (album.summary != null) {
                                Log.i(sTAG, "Description: " + album.summary);
                            }
                            // if (album.numPhotos != 0) {
                            // Log.i("Total number of photos: " +
                            // album.numPhotos);
                            // PicasaUrl url = new
                            // PicasaUrl(album.getFeedLink());
                            // AlbumFeed feed = client.executeGetAlbumFeed(url);
                            // for (PhotoEntry photo : feed.photos) {
                            // Log.i();
                            // Log.i("Photo title: " + photo.title);
                            // if (photo.summary != null) {
                            // Log.i("Photo description: " + photo.summary);
                            // }
                            // Log.i("Image MIME type: " +
                            // photo.mediaGroup.content.type);
                            // Log.i("Image URL: " +
                            // photo.mediaGroup.content.url);
                            // }
                        }
                    }
                    nextLink = feed.getNextLink();
                    if(nextLink != null)
                        feed = picasaClient.executeGetUserFeed(new PicasaUrl(nextLink));
                } while (nextLink != null);
            } catch (HttpResponseException e) {
                int code = e.getStatusCode();
                if (code == 401 /* Unauthorized */|| code == 403 /*
                                                                  * forebidden
                                                                  * token
                                                                  * expired or
                                                                  * invalid.
                                                                  */) {
                }
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private LocalBroadcastReciever mLocalBroadcastReciever = new LocalBroadcastReciever();

    private class LocalBroadcastReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (PicasaAuth.Broadcasts.ACTION_AUTHENTICATION_DONE.equals(action)) {
                mAuthToken = intent.getStringExtra(PicasaAuth.Broadcasts.EXTRA_NAME_AUTHTOKEN);
                mAccountName = intent.getStringExtra(PicasaAuth.Broadcasts.EXTRA_NAME_ACCOUNT_NAME);

                if (mAuthToken != null && mAccountName != null) {
                    GetAlbumsTask task = new GetAlbumsTask();
                    Timer timer = new Timer();
                    timer.schedule(task, 0);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // Register for authentication done broadcast.
        IntentFilter filter = new IntentFilter(PicasaAuth.Broadcasts.ACTION_AUTHENTICATION_DONE);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mLocalBroadcastReciever, filter);

        // Start authentication process if needed.
        PicasaAuth.authenticate(this);

    }

    protected void onDestory() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mLocalBroadcastReciever);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_test, menu);
        return true;
    }

}
