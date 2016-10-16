/**
 * 
 */
package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * @author Maximilian
 *
 */
public class WelcomeOnTwitter {

	Twitter twitter;

	public WelcomeOnTwitter() throws TwitterException {
		TwitterFactory factory = new TwitterFactory();
		AccessToken accessToken = loadAccessToken("abos");
		twitter = factory.getInstance();
		twitter.setOAuthAccessToken(accessToken);
		check();
	}

	private void check() {
		try {
			Query query = new Query("#meinErsterTweet");
			QueryResult result;

			result = twitter.search(query);

			List<Status> statusList = result.getTweets();

			for (Status status : statusList) {
				System.out.println("@" + status.getUser().getScreenName() + ":"
						+ status.getText());
				System.out.println(status.getId());
			}
			// for (int i = statusList.size(); i >= 0; i--) {
			twitter.createFavorite(statusList.get(1).getId());
			// }
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static AccessToken loadAccessToken(String id) {
		Properties prop = new Properties();
		InputStream in;
		try {
			in = new FileInputStream(new File("C://twitter//token.txt"));

			prop.load(in);
			String token = prop.getProperty(id + ".token");
			String tokenSecret = prop.getProperty(id + ".tokenSecret");
			return new AccessToken(token, tokenSecret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param args
	 * @throws TwitterException
	 */
	public static void main(String[] args) throws TwitterException {
		new WelcomeOnTwitter();
	}

}
