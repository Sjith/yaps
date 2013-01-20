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
package com.ogunwale.android.app.yaps.content;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.picasa.PicasaClient;
import com.google.api.services.picasa.PicasaUrl;
import com.google.api.services.picasa.model.UserFeed;
import com.ogunwale.android.app.yaps.R;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.os.Bundle;

/**
 * Class is responsible for getting data from remote providers like Picasa and
 * Facebook.
 * 
 * @author ogunwale
 * 
 */
public class RemoteDataRequest extends TimerTask implements Session.StatusCallback, FacebookRequest.FacebookGraphAlbumListCallback {

    /**
     * Requesting activity
     */
    private Activity mActivity = null;

    /**
     * Request listener
     */
    private RemoteDataListener mListener = null;

    private Session mFacebookSession = null;

    /**
     * Picasa Auth token
     */
    private String mPicasaAuthToken = null;

    /**
     * Picasa Account name
     */
    private String mPicasaAccountName = null;

    private RequestType mRequestType = RequestType.INVALID;

    /**
     * Data request types
     * 
     */
    public static enum RequestType {
        PICASA_ALBUMS, FACEBOOK_ALBUMS, INVALID
    }

    /**
     * Update interval for albums. we don't request new album information if the
     * last update is within this window and the caller is not asking for a
     * forced update.
     */
    private static final long ALBUM_UPDATE_INTERVAL = 60 * 60 * 1000;

    /**
     * Facebook permission to access user photos
     */
    private static final String FACEBOOK_USER_PHOTOS_PERMISSION = "user_photos";

    /**
     * Constructor starts the data request for the users albums.
     * 
     * @param activity
     *            requesting activity
     * @param forceUpdate
     *            forces an update for the request regardless of the update
     *            interval time
     * @param requestType
     *            request type
     * @param listener
     *            listener used to report status and results.
     */
    public RemoteDataRequest(Activity activity, boolean forceUpdate, RequestType requestType, RemoteDataAlbumListener listener) {
        super();
        long currentTime = System.currentTimeMillis();
        mActivity = activity;
        mListener = listener;
        mRequestType = requestType;

        if (requestType == RequestType.PICASA_ALBUMS) {
            long lastUpdateTime = SettingsManager.getInstance(activity).getLastPicasaAlbumUpdateTime();

            if (forceUpdate || ((currentTime - lastUpdateTime) > ALBUM_UPDATE_INTERVAL)) {
                SettingsManager.getInstance(activity).setLastPicasaAlbumUpdateTime(currentTime);
                authenticatePicasa();
            } else {
                reportCompletion(RemoteDataListener.Status.SUCCESSFUL);
            }
        } else if (requestType == RequestType.FACEBOOK_ALBUMS) {
            long lastUpdateTime = SettingsManager.getInstance(activity).getLastFacebookAlbumUpdateTime();

            if (forceUpdate || ((currentTime - lastUpdateTime) > ALBUM_UPDATE_INTERVAL)) {
                SettingsManager.getInstance(activity).setLastFacebookAlbumUpdateTime(currentTime);
                getFacebookAlbums();
            } else {
                reportCompletion(RemoteDataListener.Status.SUCCESSFUL);
            }
        } else {
            reportCompletion(RemoteDataListener.Status.INVALID_REQUEST);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
        RemoteDataListener.Status status = processRequest();

        switch (status) {
        case HTTP_401_UNAUTHORIZED:
        case HTTP_403_TOKEN_PROBLEM: {
            // Invalidate auth token and re-authenticatePicasa.
            PicasaAuth.invalidateAuthToken(mActivity, mPicasaAuthToken);
            Bundle bundle = PicasaAuth.authenticateSync(mActivity);

            if (bundle != null) {
                mPicasaAuthToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                mPicasaAccountName = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);

                // Try to process request again, hopefully with valid
                // credentials...
                reportCompletion(processRequest());
            } else {
                reportCompletion(RemoteDataListener.Status.AUTH_BUNDLE_NULL);
            }
            break;
        }
        default:
            reportCompletion(status);
            break;
        }
    }

    /**
     * Asynchronous authentication with Picasa account.
     */
    private void authenticatePicasa() {
        PicasaAuth.authenticateAsync(mActivity, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    Bundle bundle = future.getResult();
                    mPicasaAuthToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                    mPicasaAccountName = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);

                    if (mPicasaAccountName != null && mPicasaAuthToken != null) {
                        picasaAuthenticated();
                    } else if (mPicasaAccountName == null) {
                        reportCompletion(RemoteDataListener.Status.ACCOUNT_NAME_NULL);
                    } else if (mPicasaAuthToken == null) {
                        reportCompletion(RemoteDataListener.Status.AUTH_TOKEN_NULL);
                    }

                } catch (OperationCanceledException e) {
                    reportCompletion(RemoteDataListener.Status.OPERATION_CANCELED_EXCEPTION);
                } catch (AuthenticatorException e) {
                    reportCompletion(RemoteDataListener.Status.AUTHENTICATOR_EXCEPTION);
                } catch (IOException e) {
                    reportCompletion(RemoteDataListener.Status.AUTH_IO_EXCEPTION);
                }
            }
        });
    }

    /**
     * Account is successfully authenticated Picasa. Schedule the requested
     * task.
     */
    private void picasaAuthenticated() {
        Timer timer = new Timer("PicasaData: " + mRequestType.toString());
        timer.schedule(this, 0);
    }

    /**
     * Calls the request listener to report the status and removes reference to
     * the calling activity.
     * 
     * @param status
     */
    private void reportCompletion(RemoteDataListener.Status status) {
        mListener.RequestComplete(status);
        mActivity = null;
        mListener = null;
    }

    /**
     * Process request and return status to caller.
     * 
     * @return status of request
     */
    private RemoteDataListener.Status processRequest() {
        switch (mRequestType) {
        case PICASA_ALBUMS:
            return (getPicasaAlbums());
        case INVALID:
        default:
            return (RemoteDataListener.Status.INVALID_REQUEST);
        }
    }

    /**
     * Get Picasa albums.
     * 
     * @return Status of getting Picasa albums
     */
    private RemoteDataListener.Status getPicasaAlbums() {
        HttpTransport transport = new NetHttpTransport();
        HttpRequestFactory factory = transport.createRequestFactory(new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
                GoogleHeaders headers = new GoogleHeaders();
                headers.setApplicationName(mActivity.getResources().getString(R.string.app_name));
                headers.setGDataVersion("2");
                headers.setGoogleLogin(mPicasaAuthToken);
                request.setHeaders(headers);
            }
        });

        PicasaClient picasaClient = new PicasaClient(factory);
        picasaClient.setApplicationName(mActivity.getResources().getString(R.string.app_name));

        // build URL for the default user feed of albums
        PicasaUrl url = PicasaUrl.relativeToRoot("feed/api/user/default");

        // execute GData request for the feed
        UserFeed feed = null;
        try {
            String nextLink = null;
            feed = picasaClient.executeGetUserFeed(url);
            Timer timer = new Timer("PicasaAlbumData");
            do {
                processPicasaAlbumData(timer, (RemoteDataAlbumListener) mListener, feed);
                nextLink = feed.getNextLink();
                if (nextLink != null)
                    feed = picasaClient.executeGetUserFeed(new PicasaUrl(nextLink));
            } while (nextLink != null);
            return (RemoteDataListener.Status.SUCCESSFUL);
        } catch (HttpResponseException e) {
            switch (e.getStatusCode()) {
            case 401:
                return (RemoteDataListener.Status.HTTP_401_UNAUTHORIZED);
            case 403:
                return (RemoteDataListener.Status.HTTP_403_TOKEN_PROBLEM);
            default:
                return (RemoteDataListener.Status.HTTP_RESPONSE_EXCEPTION);
            }
        } catch (IOException e) {
            return (RemoteDataListener.Status.IO_EXCEPTION);
        }
    }

    /**
     * Get Facebook albums 
     */
    private void getFacebookAlbums() {
        mFacebookSession = Session.getActiveSession();

        if (mFacebookSession == null || !mFacebookSession.isOpened())
            Session.openActiveSession(mActivity, true, this);
        else if (gotFacebookPermission(mFacebookSession, FACEBOOK_USER_PHOTOS_PERMISSION))
            call(mFacebookSession, SessionState.OPENED_TOKEN_UPDATED, null);
        else
            call(mFacebookSession, SessionState.OPENED, null);
    }

    /* (non-Javadoc)
     * @see com.facebook.Session.StatusCallback#call(com.facebook.Session, com.facebook.SessionState, java.lang.Exception)
     */
    @Override
    public void call(Session session, SessionState state, Exception exception) {
        if (exception != null) {
            if (exception instanceof FacebookOperationCanceledException)
                reportCompletion(RemoteDataListener.Status.OPERATION_CANCELED_EXCEPTION);
            else if (exception instanceof FacebookAuthorizationException)
                reportCompletion(RemoteDataListener.Status.AUTHENTICATOR_EXCEPTION);
            else
                reportCompletion(RemoteDataListener.Status.UNKNOWN);
        } else if (state == SessionState.OPENED) {
            if (gotFacebookPermission(session, FACEBOOK_USER_PHOTOS_PERMISSION))
                call(session, SessionState.OPENED_TOKEN_UPDATED, null);
            else
                session.requestNewReadPermissions(new Session.NewPermissionsRequest(mActivity, Arrays.asList(FACEBOOK_USER_PHOTOS_PERMISSION)));
        } else if (state == SessionState.OPENED_TOKEN_UPDATED) {
            session.removeCallback(this);

            mFacebookSession = session;

            // make request to the /me/ablums API
            FacebookRequest.executeMyAlbumsRequestAsync(session, this);
        }
    }

    /**
     * Method checks if the input session has a specific permission
     * 
     * @param session
     *            session to check
     * @param permission
     *            permission to check of
     * @return true if the session has the permission
     */
    private boolean gotFacebookPermission(Session session, String permission) {
        List<String> permissions = session.getPermissions();

        for (String p : permissions) {
            if (p.equals(permission))
                return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see com.ogunwale.android.app.yaps.content.FacebookRequest.FacebookGraphAlbumListCallback#onCompleted(java.util.List, com.facebook.Response)
     */
    @Override
    public void onCompleted(final List<FacebookGraphAlbum> albums, final Response response) {
        Timer timer = new Timer("FacebookAlbumData");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ((RemoteDataAlbumListener) mListener).facebookAlbums(albums, mFacebookSession);
                reportCompletion(RemoteDataListener.Status.SUCCESSFUL);
            }
        }, 0);
    }

    /**
     * Processes the album feed data on a separate thread to reduce slow down in
     * getting more data from the server.
     * 
     * @param timer
     * @param feed
     */
    private void processPicasaAlbumData(final Timer timer, final RemoteDataAlbumListener listener, final UserFeed feed) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                listener.picasaAlbums((feed == null) ? null : feed.albums);
            }
        }, 0);
    }
}
