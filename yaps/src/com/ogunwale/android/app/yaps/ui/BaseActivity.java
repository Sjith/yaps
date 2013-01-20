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
package com.ogunwale.android.app.yaps.ui;

import com.facebook.UiLifecycleHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Base activity that takes care of functionality common to all activities.
 * Mainly removing Facebook UI helper clutter from other activities.
 *
 * @author ogunwale
 *
 */
public class BaseActivity extends Activity {

    private UiLifecycleHelper mFacebookUiHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFacebookUiHelper = new UiLifecycleHelper(this, null);
        mFacebookUiHelper.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mFacebookUiHelper.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mFacebookUiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        mFacebookUiHelper.onPause();
    }

    protected void onDestory() {
        super.onDestroy();
        mFacebookUiHelper.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFacebookUiHelper.onActivityResult(requestCode, resultCode, data);
    }

}
