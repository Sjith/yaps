package com.ogunwale.android.app.yaps;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Class manages application settings
 *
 * @author ogunwale
 *
 */
public class SettingsManager {

    private static SettingsManager sInstance = null;

    private SharedPreferences mSharedPreferences = null;

    private static final String SETTINGS_TAG = "yaps_settings";
    private static final String LAST_PICASA_ALBUM_UPDATE_TIME_KEY = "LAST_PICASA_ALBUM_UPDATE_TIME_KEY";

    private SettingsManager(Context context) {
        mSharedPreferences = context.getSharedPreferences(SETTINGS_TAG, Context.MODE_PRIVATE);
    }

    public static final SettingsManager getInstance(Context context) {
        if (sInstance == null)
            sInstance = new SettingsManager(context);
        return sInstance;
    }

    public void setLastPicasaAlbumUpdateTime(long value) {
        mSharedPreferences.edit().putLong(LAST_PICASA_ALBUM_UPDATE_TIME_KEY, value).commit();
    }

    public long getLastPicasaAlbumUpdateTime() {
        return (mSharedPreferences.getLong(LAST_PICASA_ALBUM_UPDATE_TIME_KEY, -1));
    }
}
