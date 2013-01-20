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
}
