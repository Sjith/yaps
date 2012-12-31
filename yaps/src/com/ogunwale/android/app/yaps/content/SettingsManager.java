package com.ogunwale.android.app.yaps.content;

import com.ogunwale.android.app.yaps.R;

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

    private static final String LAST_PICASA_ALBUM_UPDATE_TIME_KEY = "LAST_PICASA_ALBUM_UPDATE_TIME_KEY";

    private static final String LAST_FACEBOOK_ALBUM_UPDATE_TIME_KEY = "LAST_FACEBOOK_ALBUM_UPDATE_TIME_KEY";

    private static final String ALBUM_SELECTION_KEY = "ALBUM_SELECTION_KEY";

    private SettingsManager(Context context) {
        mSharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
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

    public void setLastFacebookAlbumUpdateTime(long value) {
        mSharedPreferences.edit().putLong(LAST_FACEBOOK_ALBUM_UPDATE_TIME_KEY, value).commit();
    }

    public long getLastFacebookAlbumUpdateTime() {
        return (mSharedPreferences.getLong(LAST_FACEBOOK_ALBUM_UPDATE_TIME_KEY, -1));
    }

    public void setAlbumSelection(PhotosSourceEnum value) {
        mSharedPreferences.edit().putInt(ALBUM_SELECTION_KEY, value.getValue()).commit();
    }

    public PhotosSourceEnum getAlbumSelection() {
        return PhotosSourceEnum.getEnum(mSharedPreferences.getInt(ALBUM_SELECTION_KEY, PhotosSourceEnum.PICASA.getValue()));
    }
}
