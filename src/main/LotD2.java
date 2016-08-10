/**
 * 
 */
package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * @author Maximilian
 *
 */
public class LotD2 {

	public static void main(String args[]) throws Exception {
		// The factory instance is re-useable and thread safe.
		TwitterFactory factory = new TwitterFactory();
		AccessToken accessToken = loadAccessToken();
		Twitter twitter = factory.getInstance();
		twitter.setOAuthConsumer("MuZeUQJJSlDbkp9NP2Pg6MdlE",
				"F7d768ExdJ03UWmigHLXoJ1sEmPlugh5Z8KbX19cqLGURQkCPC");
		twitter.setOAuthAccessToken(accessToken);
		StatusUpdate statusUpdate = new StatusUpdate("API-Test4");
		statusUpdate.setMedia(new File("C://twitter//image.png"));
		Status status = twitter.updateStatus(statusUpdate);
		System.out.println("Successfully updated the status to ["
				+ status.getText() + "].");
		System.exit(0);
	}

	private static AccessToken loadAccessToken() {
		Properties prop = new Properties();
		InputStream in;
		try {
			in = new FileInputStream(new File("C://twitter//token.txt"));

			prop.load(in);
			String token = prop.getProperty("token");
			String tokenSecret = prop.getProperty("tokenSecret");
			return new AccessToken(token, tokenSecret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
