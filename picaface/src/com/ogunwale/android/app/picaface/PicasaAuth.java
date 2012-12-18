package com.ogunwale.android.app.picaface;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.google.api.client.googleapis.extensions.android2.auth.GoogleAccountManager;

/**
 * This class is responsible for authorization/authentication into the users
 * picasa web albums account.
 *
 * @author ogunwale
 *
 */
public class PicasaAuth {

    // private static final String sTAG = PicasaAuth.class.getSimpleName();

    /**
     * Picasa auth token type.
     */
    private static final String PICASA_AUTH_TOKEN_TYPE = "lh2";

    /**
     * Application context. mainly used to broadcast auth done action in
     * callback.
     */
    private static Context mAppContext = null;

    /**
     * All broadcasts sent by this class
     *
     * @author ogunwale
     *
     */
    public class Broadcasts {
        /**
         * Class broadcast prefix
         */
        public static final String BROADCASTS_PREFIX = "PicasaAuth.Broadcasts.";

        /**
         * Action broadcasted we authentication is complete. Intent will also
         * contain EXTRA_NAME_ACCOUNT_NAME and EXTRA_NAME_AUTHTOKEN extras.
         */
        public static final String ACTION_AUTHENTICATION_DONE = BROADCASTS_PREFIX + "ACTION_AUTHENTICATION_DONE";

        /**
         * Authentication account name string.
         */
        public static final String EXTRA_NAME_ACCOUNT_NAME = BROADCASTS_PREFIX + "EXTRA_NAME_ACCOUNT_NAME";
        /**
         * Authentication token string.
         */
        public static final String EXTRA_NAME_AUTHTOKEN = BROADCASTS_PREFIX + "EXTRA_NAME_AUTHTOKEN";
    }

    /**
     * Asynchronous method call used to authenticate access to the picasa
     * account. {@link Broadcasts.ACTION_AUTHENTICATION_DONE} will be
     * broadcasted if the authentication was successful.
     *
     * @param context
     */
    public static void authenticate(Activity context) {

        mAppContext = context.getApplicationContext();

        AccountManager.get(context).getAuthTokenByFeatures(GoogleAccountManager.ACCOUNT_TYPE, PICASA_AUTH_TOKEN_TYPE, null, context, null, null,
                new AccountManagerCallback<Bundle>() {
                    public void run(AccountManagerFuture<Bundle> future) {
                        try {
                            Bundle bundle = future.getResult();

                            Intent intent = new Intent(Broadcasts.ACTION_AUTHENTICATION_DONE);
                            intent.putExtra(Broadcasts.EXTRA_NAME_ACCOUNT_NAME, bundle.getString(AccountManager.KEY_ACCOUNT_NAME));
                            intent.putExtra(Broadcasts.EXTRA_NAME_AUTHTOKEN, bundle.getString(AccountManager.KEY_AUTHTOKEN));

                            LocalBroadcastManager.getInstance(mAppContext).sendBroadcast(intent);
                        } catch (Exception e) {
                        }
                    }
                }, null);
    }
}
