/**
 * 
 */
package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.JOptionPane;

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
		System.out.println(accessToken.getToken());
		System.out.println(accessToken.getTokenSecret());
		// persist to the accessToken for future reference.
		storeAccessToken("abo2", accessToken);
		// Status status = twitter.updateStatus("API-Test");
		// System.out.println("Successfully updated the status to ["
		// + status.getText() + "].");
		System.exit(0);
	}

	private static void storeAccessToken(String id, AccessToken accessToken) {
		Properties prop = new Properties();
		try {
			InputStream in = new FileInputStream(new File(
					"C://twitter//token.txt"));
			prop.load(in);
			prop.setProperty(id + ".token", accessToken.getToken());
			prop.setProperty(id + ".tokenSecret", accessToken.getTokenSecret());
			File f = new File("C://twitter//token.txt");
			System.out.println(f.getAbsolutePath());
			OutputStream os;

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
