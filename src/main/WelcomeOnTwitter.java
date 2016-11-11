/**
 * 
 */
package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import twitter4j.Query;
import twitter4j.Query.ResultType;
import twitter4j.QueryResult;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import essentials.Essentials;

/**
 * @author Maximilian
 *
 */
public class WelcomeOnTwitter {

	Twitter twitter;

	public WelcomeOnTwitter() throws TwitterException, FileNotFoundException,
			IOException {
		TwitterFactory factory = new TwitterFactory();
		AccessToken accessToken = loadAccessToken("abos");
		twitter = factory.getInstance();
		twitter.setOAuthAccessToken(accessToken);
		check();
	}

	private void check() throws FileNotFoundException, IOException {
		Properties props = new Properties();
		props.load(new FileInputStream(new File(
				"C://twitter//sarcasm.properties")));
		try {
			Query query = new Query("#sarcasm");
			QueryResult result;
			query.setCount(100);
			query.setResultType(query.POPULAR);
			result = twitter.search(query);
			int count = 0;
			// there is more pages to load
			do {
				System.out.println("Check");

				for (Status status : result.getTweets()) {
					count++;

					String text = status.getText();
					text = text.replaceAll("/\r?\n|\r/g", " ");
					text = text.replace("#sarcasm", "");
					if (!props.containsKey(String.valueOf(status.getId()))) {
						props.put(String.valueOf(status.getId()), text);
						System.out.print("Tweets No. " + count + ": " + text);
					}
					System.out.println("Double tweet skipped");
				}
				if (result.hasNext())
					query = result.nextQuery();
			} while (result.hasNext());
			props.store(new FileOutputStream(new File(
					"C://twitter//sarcasm.properties")),
					"Search results for #sarcasm");

			// for (int i = statusList.size(); i >= 0; i--) {
			// twitter.createFavorite(statusList.get(1).getId());
			// }
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void stream() {
		TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
		twitterStream.setOAuthAccessToken(loadAccessToken("abos"));
		StatusListener listener = new StatusListener() {
			@Override
			public void onStatus(Status status) {
				if (status.getLang().equalsIgnoreCase("en")) {
					Essentials.printStringToFile(status.getText() + "\n",
							new File("C://twitter//normal.csv"));
				}
			}

			@Override
			public void onDeletionNotice(
					StatusDeletionNotice statusDeletionNotice) {
				System.out.println("Got a status deletion notice id:"
						+ statusDeletionNotice.getStatusId());
			}

			@Override
			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				System.out.println("Got track limitation notice:"
						+ numberOfLimitedStatuses);
			}

			@Override
			public void onScrubGeo(long userId, long upToStatusId) {
				System.out.println("Got scrub_geo event userId:" + userId
						+ " upToStatusId:" + upToStatusId);
			}

			@Override
			public void onStallWarning(StallWarning warning) {
				System.out.println("Got stall warning:" + warning);
			}

			@Override
			public void onException(Exception ex) {
				ex.printStackTrace();
			}
		};
		twitterStream.addListener(listener);
		twitterStream.sample();
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
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws TwitterException,
			FileNotFoundException, IOException {
		// stream();
		new WelcomeOnTwitter();
	}

}
