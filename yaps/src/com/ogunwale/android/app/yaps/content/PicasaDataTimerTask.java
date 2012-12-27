package com.ogunwale.android.app.yaps.content;

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
import com.ogunwale.android.app.yaps.R.string;
import com.ogunwale.android.app.yaps.content.PicasaDataListener.FailureCause;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.os.Bundle;

/**
 * Timer task for getting various Picasa data.
 *
 * @author ogunwale
 *
 */
public class PicasaDataTimerTask extends TimerTask {

    /**
     * Requesting activity
     */
    private Activity mActivity = null;

    /**
     * Request listener
     */
    private PicasaDataListener mListener = null;

    /**
     * Auth token
     */
    private String mAuthToken = null;

    /**
     * Account name
     */
    private String mAccountName = null;

    private RequestType mRequestType = RequestType.INVALID;

    /**
     * Picasa data request types
     *
     */
    private static enum RequestType {
        USER_ALBUMS, INVALID
    }

    private static enum Status {
        SUCCESSFUL, HTTP_401_UNAUTHORIZED, HTTP_403_TOKEN_PROBLEM, HTTP_RESPONSE_EXCEPTION, IO_EXCEPTION, INVALID_REQUEST
    }

    /**
     * update interval for Picasa albums. we don't request new album information
     * if the last update is within this window and the caller is not asking for
     * a forced update.
     */
    private static final long PICASA_ALBUM_UPDATE_INTERVAL = 60 * 60 * 1000;

    /**
     * Constructor starts the data request for the users albums.
     *
     * @param activity
     *            requesting activity
     * @param listener
     *            listener used to report status and results.
     */
    public PicasaDataTimerTask(Activity activity, boolean forceUpdate, PicasaDataAlbumListener listener) {
        super();
        long currentTime = System.currentTimeMillis();
        long lastUpdateTime = SettingsManager.getInstance(activity).getLastPicasaAlbumUpdateTime();

        if (forceUpdate || ((currentTime - lastUpdateTime) > PICASA_ALBUM_UPDATE_INTERVAL)) {
            SettingsManager.getInstance(activity).setLastPicasaAlbumUpdateTime(currentTime);
            mActivity = activity;
            mListener = listener;
            mRequestType = RequestType.USER_ALBUMS;
            authenticate();
        } else {
            listener.RequestComplete();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
        Status status = processRequest();

        switch (status) {
        case HTTP_401_UNAUTHORIZED:
        case HTTP_403_TOKEN_PROBLEM: {
            // Invalidate auth token and re-authenticate.
            PicasaAuth.invalidateAuthToken(mActivity, mAuthToken);
            Bundle bundle = PicasaAuth.authenticateSync(mActivity);

            if (bundle != null) {
                mAuthToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                mAccountName = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);

                // Try to process request again, hopefully with valid
                // credentials...
                status = processRequest();
                switch (status) {
                case HTTP_401_UNAUTHORIZED:
                    reportFailure(FailureCause.HTTP_401_UNAUTHORIZED);
                    break;
                case HTTP_403_TOKEN_PROBLEM:
                    reportFailure(FailureCause.HTTP_403_TOKEN_PROBLEM);
                    break;
                case SUCCESSFUL:
                    reportCompletion();
                    break;
                case INVALID_REQUEST:
                    reportFailure(FailureCause.INVALID_REQUEST);
                    break;
                case IO_EXCEPTION:
                    reportFailure(FailureCause.IO_EXCEPTION);
                    break;
                case HTTP_RESPONSE_EXCEPTION:
                    reportFailure(FailureCause.HTTP_RESPONSE_EXCEPTION);
                    break;
                default:
                    reportFailure(FailureCause.UNKNOWN);
                    break;

                }
            } else {
                reportFailure(FailureCause.AUTH_BUNDLE_NULL);
            }
            break;
        }
        case SUCCESSFUL:
            reportCompletion();
            break;
        case INVALID_REQUEST:
            reportFailure(FailureCause.INVALID_REQUEST);
            break;
        case IO_EXCEPTION:
            reportFailure(FailureCause.IO_EXCEPTION);
            break;
        case HTTP_RESPONSE_EXCEPTION:
            reportFailure(FailureCause.HTTP_RESPONSE_EXCEPTION);
            break;
        default:
            reportFailure(FailureCause.UNKNOWN);
            break;

        }
    }

    /**
     * Asynchronous authentication with Picasa account.
     */
    private void authenticate() {
        PicasaAuth.authenticateAsync(mActivity, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    Bundle bundle = future.getResult();
                    mAuthToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                    mAccountName = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);

                    if (mAccountName != null && mAuthToken != null) {
                        accountAuthenticated();
                    } else if (mAccountName == null) {
                        reportFailure(FailureCause.ACCOUNT_NAME_NULL);
                    } else if (mAuthToken == null) {
                        reportFailure(FailureCause.AUTH_TOKEN_NULL);
                    }

                } catch (OperationCanceledException e) {
                    reportFailure(FailureCause.OPERATION_CANCELED_EXCEPTION);
                } catch (AuthenticatorException e) {
                    reportFailure(FailureCause.AUTHENTICATOR_EXCEPTION);
                } catch (IOException e) {
                    reportFailure(FailureCause.AUTH_IO_EXCEPTION);
                }
            }
        });
    }

    /**
     * Account is successfully authenticated. Schedule the requested task.
     */
    private void accountAuthenticated() {
        Timer timer = new Timer("PicasaData: " + mRequestType.toString());
        timer.schedule(this, 0);
    }

    /**
     * Calls the request listener to report the failure and removes reference to
     * the calling activity.
     *
     * @param cause
     */
    private void reportFailure(FailureCause cause) {
        mListener.RequestFailed(cause);
        mActivity = null;
        mListener = null;
    }

    private void reportCompletion() {
        mListener.RequestComplete();
        mActivity = null;
        mListener = null;
    }

    private Status processRequest() {
        switch (mRequestType) {
        case USER_ALBUMS:
            return (getUserAlbums());
        case INVALID:
        default:
            return (Status.INVALID_REQUEST);
        }
    }

    private Status getUserAlbums() {
        HttpTransport transport = new NetHttpTransport();
        HttpRequestFactory factory = transport.createRequestFactory(new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
                GoogleHeaders headers = new GoogleHeaders();
                headers.setApplicationName(mActivity.getResources().getString(R.string.app_name));
                headers.setGDataVersion("2");
                headers.setGoogleLogin(mAuthToken);
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
            Timer timer = new Timer("AlbumFeedData");
            do {
                processAlbumFeedData(timer, (PicasaDataAlbumListener) mListener, feed);
                nextLink = feed.getNextLink();
                if (nextLink != null)
                    feed = picasaClient.executeGetUserFeed(new PicasaUrl(nextLink));
            } while (nextLink != null);
            return (Status.SUCCESSFUL);
        } catch (HttpResponseException e) {
            switch (e.getStatusCode()) {
            case 401:
                return (Status.HTTP_401_UNAUTHORIZED);
            case 403:
                return (Status.HTTP_403_TOKEN_PROBLEM);
            default:
                return (Status.HTTP_RESPONSE_EXCEPTION);
            }
        } catch (IOException e) {
            return (Status.IO_EXCEPTION);
        }
    }

    /**
     * Processes the album feed data on a separate thread to reduce slow down in
     * getting more data from the server.
     *
     * @param timer
     * @param feed
     */
    private void processAlbumFeedData(final Timer timer, final PicasaDataAlbumListener listener, final UserFeed feed) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                listener.userFeed(feed);
                if (feed.albums != null) {
                    for (AlbumEntry album : feed.albums)
                        listener.albumEntry(album);
                }
            }
        }, 0);
    }
}
