package com.ogunwale.android.app.picaface.picasa;

import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.photos.AlbumEntry;
import com.google.gdata.data.photos.UserFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * This class is the main access point to picasa web for the current user.
 * 
 * @author ogunwale
 * 
 */
public class PicasaWebAlbums {

	private static final String API_PREFIX = "http://picasaweb.google.com/data/feed/api/user/";

	/**
	 * Singleton instance of the class
	 */
	private static PicasaWebAlbums instance = null;

	/**
	 * Picasa web account User ID
	 */
	private String mUserId = null;

	/**
	 * Picasa web service
	 */
	private PicasawebService mService = null;

	/**
	 * Indicates if we are logged into a picasa web account.
	 */
	private boolean mLoggedIn = false;

	/**
	 * private constructor since this is a singleton class.
	 */
	private PicasaWebAlbums() {
	}

	/**
	 * Returns the singleton instance of {@link PicasaWebAlbums}
	 * 
	 * @return instance of {@link PicasaWebAlbums}
	 */
	public static PicasaWebAlbums getInstance() {
		if (instance == null)
			instance = new PicasaWebAlbums();
		return instance;
	}

	/**
	 * Method is used to log into a picasa web account using the provided
	 * credentials.
	 * 
	 * @param appName
	 *            name of application requesting log-in
	 * @param userId
	 *            picasa web account user ID or gmail address
	 * @param userPassword
	 *            picasa web account user password
	 * @throws AuthenticationException
	 *             this is thrown if there was a problem logging into the
	 *             account with the supplied credentials.
	 */
	public synchronized void login(String appName, String userId,
			String userPassword) throws AuthenticationException {
		mLoggedIn = false;
		mService = new PicasawebService(appName);
		mService.setUserCredentials(userId, userPassword);
		mUserId = userId;
		mLoggedIn = true;
	}

	public synchronized List<AlbumEntry> getAlbums() {
		if (mLoggedIn) {
			try {
				URL url = new URL(API_PREFIX + mUserId + "?kind=album");
				UserFeed resultFeed = mService.getFeed(url, UserFeed.class);
				return (resultFeed.getAlbumEntries());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block. Something wrong with the
				// User ID?
				e.printStackTrace();
			} catch (ServiceException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// TODO throw some exception.
		}

		return null;
	}

	public static void main(String[] args) {

		try {
			PicasaWebAlbums pwa = PicasaWebAlbums.getInstance();

			System.out.println("Logging in...\n");
			pwa.login("my app", "<user name>", "<password>");

			System.out.println("Getting Picasa Web Albums entries...\n");
			List<AlbumEntry> aEntries = pwa.getAlbums();

			for (AlbumEntry album : aEntries) {
				System.out.println("\t" + album.getTitle().getPlainText());
			}

			System.out.println("\nTotal Entries: " + aEntries.size());
		} catch (AuthenticationException e) {
			e.printStackTrace();
		}
	}
}
