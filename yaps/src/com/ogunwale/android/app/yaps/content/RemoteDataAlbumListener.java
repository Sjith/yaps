package com.ogunwale.android.app.yaps.content;

import java.util.List;

import com.facebook.Session;
import com.google.api.services.picasa.model.AlbumEntry;

/**
 * Listener interface for remote album data requests.
 *
 * @author ogunwale
 *
 */
public interface RemoteDataAlbumListener extends RemoteDataListener {
    /**
     * Method is called with the list of Picasa albums for the get Album
     * request.
     *
     * @param album
     */
    void picasaAlbums(List<AlbumEntry> albums);

    /**
     * Method is called with the list of Facebook albums for the get Album
     * request.
     *
     * @param albums
     */
    void facebookAlbums(List<FacebookGraphAlbum> albums, Session session);

}
