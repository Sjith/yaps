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
