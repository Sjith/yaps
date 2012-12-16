package com.ogunwale.android.app.picaface;

import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

	private static final String sTAG = PicasaAuth.class.getSimpleName();

	/**
	 * Picasa auth token type.
	 */
	private static final String PICASA_AUTH_TOKEN_TYPE = "lh2";

	/**
	 * Current account name.
	 */
	private static String mAccountName = null;

	/**
	 * Account authentication token.
	 */
	private static String mAuthToken = null;

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
		 * Action broadcasted we authentication is complete.
		 */
		public static final String ACTION_AUTHENTICATION_DONE = "PicasaAuth.Broadcasts.ACTION_AUTHENTICATION_DONE";
	}

	public static void authenticate(Activity context) {

		GoogleAccountManager am = new GoogleAccountManager(context);

		final SharedPreferences settings = context.getSharedPreferences(sTAG, Context.MODE_PRIVATE);
		mAccountName = settings.getString("mAccountName", null);
		mAuthToken = settings.getString("mAuthToken", null);

		Account account = am.getAccountByName(mAccountName);

		if (account == null) {
			mAppContext = context.getApplicationContext();

			AccountManager.get(context).getAuthTokenByFeatures(GoogleAccountManager.ACCOUNT_TYPE, PICASA_AUTH_TOKEN_TYPE, null, context, null, null,
					new AccountManagerCallback<Bundle>() {
						public void run(AccountManagerFuture<Bundle> future) {
							try {
								Bundle bundle = future.getResult();
								mAccountName = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);
								mAuthToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);

								settings.edit().putString("mAccountName", mAccountName);
								settings.edit().putString("mAuthToken", mAuthToken);
								LocalBroadcastManager.getInstance(mAppContext).sendBroadcast(new Intent(Broadcasts.ACTION_AUTHENTICATION_DONE));
							} catch (Exception e) {
							}
						}
					}, null);
		} else {
			LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Broadcasts.ACTION_AUTHENTICATION_DONE));
		}
	}

	/**
	 * Method is used to refresh the auth token when ever it expires.
	 * 
	 */
	public static void refreshAuthToken(Context context) {
		AccountManager am = AccountManager.get(context);
		am.invalidateAuthToken(GoogleAccountManager.ACCOUNT_TYPE, mAuthToken);

		Account account = new Account(mAccountName, GoogleAccountManager.ACCOUNT_TYPE);

		try {
			mAuthToken = am.blockingGetAuthToken(account, GoogleAccountManager.ACCOUNT_TYPE, false);
		} catch (OperationCanceledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AuthenticatorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Returns the current picasa user account name or null if not valid
	 * 
	 * @return current picasa account name or null
	 */
	public static String getAccountName() {
		return mAccountName;
	}

	/**
	 * Returns the current picasa auth token or null if not valid
	 * 
	 * @return current picasa auth token
	 */
	public static String getAuthToken() {
		return mAuthToken;
	}
}
