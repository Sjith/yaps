package com.ogunwale.android.app.yaps;

/**
 * Enumeration of photo sources.
 *
 * @author ogunwale
 *
 */
public enum PhotosSourceEnum {
    FACEBOOK(0), PICASA(1), INVALID(2);

    private int value;

    private PhotosSourceEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static PhotosSourceEnum getEnum(int value) {
        PhotosSourceEnum enumConst = INVALID;
        switch (value) {
        case 0:
            enumConst = FACEBOOK;
            break;
        case 1:
            enumConst = PICASA;
            break;
        case 2:
            enumConst = INVALID;
            break;
        }
        return enumConst;
    }

    public String toString() {
        String string = "???";
        switch (this) {
        case FACEBOOK:
            string = "Facebook";
            break;
        case PICASA:
            string = "Picasa";
            break;
        case INVALID:
            string = "Invalid";
            break;
        }
        return string;
    }
}
