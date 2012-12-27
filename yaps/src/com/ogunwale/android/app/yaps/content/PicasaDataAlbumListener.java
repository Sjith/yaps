package com.ogunwale.android.app.yaps.content;

import com.google.api.services.picasa.model.AlbumEntry;
import com.google.api.services.picasa.model.UserFeed;

/**
 * Listener interface for Picasa album data requests.
 *
 * @author ogunwale
 *
 */
public interface PicasaDataAlbumListener extends PicasaDataListener {
    /**
     * Method is called when we get an album entry for the get Album request.
     * This will be called once for each album in the users account until the
     * request is fully complete.
     *
     * @param album
     */
    void albumEntry(AlbumEntry album);

    /**
     * Method is called for each user feed we get for the Album request.
     *
     * @param feed
     */
    void userFeed(UserFeed feed);
}
