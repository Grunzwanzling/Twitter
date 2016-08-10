/**
 * 
 */
package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.JOptionPane;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * @author Maximilian
 *
 */
public class LotD {

	/**
	 * 
	 */
	public static void main(String args[]) throws Exception {
		// The factory instance is re-useable and thread safe.
		Twitter twitter = TwitterFactory.getSingleton();
		twitter.setOAuthConsumer("MuZeUQJJSlDbkp9NP2Pg6MdlE",
				"F7d768ExdJ03UWmigHLXoJ1sEmPlugh5Z8KbX19cqLGURQkCPC");
		RequestToken requestToken = twitter.getOAuthRequestToken();
		AccessToken accessToken = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (null == accessToken) {
			System.out
					.println("Open the following URL and grant access to your account:");
			System.out.println(requestToken.getAuthorizationURL());
			System.out
					.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
			String pin = br.readLine();
			try {
				if (pin.length() > 0) {
					accessToken = twitter
							.getOAuthAccessToken(requestToken, pin);
				} else {
					accessToken = twitter.getOAuthAccessToken();
				}
			} catch (TwitterException te) {
				if (401 == te.getStatusCode()) {
					System.out.println("Unable to get the access token.");
				} else {
					te.printStackTrace();
				}
			}
		}
		// persist to the accessToken for future reference.
		storeAccessToken(twitter.verifyCredentials().getId(), accessToken);
		Status status = twitter.updateStatus("API-Test");
		System.out.println("Successfully updated the status to ["
				+ status.getText() + "].");
		System.exit(0);
	}

	private static void storeAccessToken(long l, AccessToken accessToken) {
		Properties prop = new Properties();
		prop.setProperty("token", accessToken.getToken());
		prop.setProperty("tokenSecret", accessToken.getTokenSecret());
		prop.setProperty("id", String.valueOf(l));
		File f = new File("C://twitter//token.txt");
		System.out.println(f.getAbsolutePath());
		OutputStream os;
		try {
			os = new FileOutputStream(f);

			prop.store(os, "");
		} catch (IOException e) {
			JOptionPane.showConfirmDialog(null,
					"IOException while saving token\n" + e.getMessage());
			e.printStackTrace();
		}
		// store accessToken.getToken()
		// store accessToken.getTokenSecret()
	}
}
