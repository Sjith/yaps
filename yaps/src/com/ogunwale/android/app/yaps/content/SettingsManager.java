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

import com.ogunwale.android.app.yaps.R;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Application settings manager.
 *
 * @author ogunwale
 *
 */
public class SettingsManager {

    private static SettingsManager sInstance = null;

    private SharedPreferences mSharedPreferences = null;

    /**
     * Last Picasa album update time.
     */
    private static final String LAST_PICASA_ALBUM_UPDATE_TIME_KEY = "LAST_PICASA_ALBUM_UPDATE_TIME_KEY";

    /**
     * Last Facebook album update time
     */
    private static final String LAST_FACEBOOK_ALBUM_UPDATE_TIME_KEY = "LAST_FACEBOOK_ALBUM_UPDATE_TIME_KEY";

    /**
     * Last album source selection
     */
    private static final String ALBUM_SELECTION_KEY = "ALBUM_SELECTION_KEY";

    /**
     * Private constructor for settings manager.
     *
     * @param context
     */
    private SettingsManager(Context context) {
        mSharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
    }

    /**
     * Returns settings manager singleton instance
     *
     * @param context
     *            current context
     * @return settings manager instance
     */
    public static final SettingsManager getInstance(Context context) {
        if (sInstance == null)
            sInstance = new SettingsManager(context);
        return sInstance;
    }

    /**
     * Sets the Picasa album last update time
     *
     * @param value
     *            update time
     */
    public void setLastPicasaAlbumUpdateTime(long value) {
        mSharedPreferences.edit().putLong(LAST_PICASA_ALBUM_UPDATE_TIME_KEY, value).commit();
    }

    /**
     * Returns the Picasa album last update time
     *
     * @return last update time for Picasa album
     */
    public long getLastPicasaAlbumUpdateTime() {
        return (mSharedPreferences.getLong(LAST_PICASA_ALBUM_UPDATE_TIME_KEY, -1));
    }

    /**
     * Sets the Facebook album last update time
     *
     * @param value
     *            update time
     */
    public void setLastFacebookAlbumUpdateTime(long value) {
        mSharedPreferences.edit().putLong(LAST_FACEBOOK_ALBUM_UPDATE_TIME_KEY, value).commit();
    }

    /**
     * Returns the Facebook last update time
     *
     * @return last update time for Facebook album
     */
    public long getLastFacebookAlbumUpdateTime() {
        return (mSharedPreferences.getLong(LAST_FACEBOOK_ALBUM_UPDATE_TIME_KEY, -1));
    }

    /**
     * Sets the last album source selection
     *
     * @param value
     *            album source selection
     */
    public void setAlbumSelection(PhotosSourceEnum value) {
        mSharedPreferences.edit().putInt(ALBUM_SELECTION_KEY, value.getValue()).commit();
    }

    /**
     * Returns the last album source selection
     *
     * @return last album source selection
     */
    public PhotosSourceEnum getAlbumSelection() {
        return PhotosSourceEnum.getEnum(mSharedPreferences.getInt(ALBUM_SELECTION_KEY, PhotosSourceEnum.PICASA.getValue()));
    }
}
