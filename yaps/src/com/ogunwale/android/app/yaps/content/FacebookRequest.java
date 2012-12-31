package com.ogunwale.android.app.yaps.content;

import java.util.List;

import android.os.Bundle;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;

/**
 * Sub-class of Request class in Facebook SDK so we can perform custom request
 * (get albums, ...) which is not currently in the SDK.
 *
 * @author ogunwale
 *
 */
public class FacebookRequest extends Request {

    private static final String MY_ALBUMS = "me/albums";

    public FacebookRequest() {
        super();
    }

    public FacebookRequest(Session session, String graphPath) {
        super(session, graphPath);
    }

    public FacebookRequest(Session session, String graphPath, Bundle parameters, HttpMethod httpMethod) {
        super(session, graphPath, parameters, httpMethod);
    }

    public FacebookRequest(Session session, String graphPath, Bundle parameters, HttpMethod httpMethod, Callback callback) {
        super(session, graphPath, parameters, httpMethod, callback);
    }

    /**
     * Creates a new Request configured to retrieve a user's album list.
     *
     * @param session
     *            the Session to use, or null; if non-null, the session must be
     *            in an opened state
     * @param callback
     *            a callback that will be called when the request is completed
     *            to handle success or error conditions
     * @return a Request that is ready to execute
     */
    public static Request newMyAlbumsRequest(Session session, final FacebookGraphAlbumListCallback callback) {
        Callback wrapper = new Callback() {
            @Override
            public void onCompleted(Response response) {
                if (callback != null)
                    callback.onCompleted(typedListFromResponse(response, FacebookGraphAlbum.class), response);
            }
        };
        return new Request(session, MY_ALBUMS, null, null, wrapper);
    }

    /**
     * Creates a new Request configured to retrieve a photo.
     *
     * @param session
     *            the Session to use, or null; if non-null, the session must be
     *            in an opened state
     * @param callback
     *            a callback that will be called when the request is completed
     *            to handle success or error conditions
     * @return a Request that is ready to execute
     */
    public static Request newPhotoRequest(Session session, String photoId, final FacebookGraphPhotoCallback callback) {
        Callback wrapper = new Callback() {
            @Override
            public void onCompleted(Response response) {
                if (callback != null)
                    callback.onCompleted(response.getGraphObjectAs(FacebookGraphPhoto.class), response);
            }
        };
        return new Request(session, photoId, null, null, wrapper);
    }

    /**
     * Creates a new Request configured to retrieve a photo.
     * <p/>
     *
     * @param session
     *            the Session to use, or null; if non-null, the session must be
     *            in an opened state
     * @param callback
     *            a callback that will be called when the request is completed
     *            to handle success or error conditions
     * @return a RequestAsyncTask that is executing the request
     */
    public static RequestAsyncTask executeMyAlbumsRequestAsync(Session session, FacebookGraphAlbumListCallback callback) {
        return newMyAlbumsRequest(session, callback).executeAsync();
    }

    /**
     * Creates a new Request configured to retrieve a photo.
     * <p/>
     *
     * @param session
     *            the Session to use, or null; if non-null, the session must be
     *            in an opened state
     * @param callback
     *            a callback that will be called when the request is completed
     *            to handle success or error conditions
     * @return a RequestAsyncTask that is executing the request
     */
    public static RequestAsyncTask executePhotoRequestAsync(Session session, String photoId, FacebookGraphPhotoCallback callback) {
        return newPhotoRequest(session, photoId, callback).executeAsync();
    }

    /**
     * Specifies the interface that consumer of
     * {@link FacebookRequest#executeMyAlbumsRequestAsync(Session, FacebookGraphAlbumCallback)}
     * can use to be notified when the request completes, either successfully or
     * with error.
     *
     * @author ogunwale
     *
     */
    public interface FacebookGraphAlbumListCallback {
        void onCompleted(List<FacebookGraphAlbum> albums, Response response);
    }

    /**
     * Specifies the interface that consumer of
     * {@link FacebookRequest#executePhotoRequest(Session, FacebookGraphPhotoCallback)}
     * can use to be notified when the request completes, either successfully or
     * with error.
     *
     * @author ogunwale
     *
     */
    public interface FacebookGraphPhotoCallback {
        void onCompleted(FacebookGraphPhoto photo, Response response);
    }
}
