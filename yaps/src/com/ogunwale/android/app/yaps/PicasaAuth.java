package com.ogunwale.android.app.yaps;

import java.io.IOException;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android2.auth.GoogleAccountManager;

/**
 * This class is responsible for authorization/authentication into the users
 * Picasa web albums account.
 *
 * @author ogunwale
 *
 */
public class PicasaAuth {

    private static final String sTAG = PicasaAuth.class.getSimpleName();

    /**
     * Picasa auth token type.
     */
    private static final String PICASA_AUTH_TOKEN_TYPE = "lh2";

    /**
     * Asynchronous method call used to authenticate access to the user's Picasa
     * account. The provided callback will be called once the process is done.
     *
     * @param activity
     *            calling activity
     * @param callback
     *            callback to call when the authentication process is done.
     */
    public static void authenticateAsync(Activity activity, AccountManagerCallback<Bundle> callback) {
        AccountManager.get(activity).getAuthTokenByFeatures(GoogleAccountManager.ACCOUNT_TYPE, PICASA_AUTH_TOKEN_TYPE, null, activity, null, null,
                callback, null);
    }

    /**
     * Synchronous method call used to authenticate access to the user's Picasa
     * account. If successful, the call can use
     * bundle.getString(AccountManager.KEY_ACCOUNT_NAME) and
     * bundle.getString(AccountManager.KEY_AUTHTOKEN) to get the name and token
     * information.
     *
     * @param activity
     *            calling activity.
     * @return Returns a valid Bundle is the authentication was successful, else
     *         null
     */
    public static Bundle authenticateSync(Activity activity) {
        Bundle bundle = null;

        AccountManagerFuture<Bundle> future = AccountManager.get(activity).getAuthTokenByFeatures(GoogleAccountManager.ACCOUNT_TYPE,
                PICASA_AUTH_TOKEN_TYPE, null, activity, null, null, null, null);

        if (future != null) {
            try {
                bundle = future.getResult();
            } catch (OperationCanceledException e) {
                Log.e(sTAG, "OperationCanceledException");
                bundle = null;
            } catch (AuthenticatorException e) {
                Log.e(sTAG, "AuthenticatorException");
                bundle = null;
            } catch (IOException e) {
                Log.e(sTAG, "IOException");
                bundle = null;
            }
        }

        return bundle;
    }

    /**
     * Invalidates the auth token for the google account.
     *
     * @param context
     *            current context
     * @param authToken
     *            auth token to invalidate.
     */
    public static void invalidateAuthToken(Context context, String authToken) {
        AccountManager.get(context).invalidateAuthToken(GoogleAccountManager.ACCOUNT_TYPE, authToken);
    }
}
