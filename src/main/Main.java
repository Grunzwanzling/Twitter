/**
 * 
 */
package main;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/**
 * @author Maximilian
 *
 */
public class Main {

	public static void main(String[] args) throws TwitterException {
		TwitterFactory factory = new TwitterFactory();
		Twitter twitter = factory.getInstance();
		twitter.updateStatus(new StatusUpdate("Hacked by @grunzwanzling42"));
		twitter.updateStatus(new StatusUpdate(
				"Do not publish your @twitterapi tokens online! I am a friendly hacker, but not everyone is!"));
		twitter.updateProfile("Hacked", "", "", "Hacked by @grunzwanzling42");
		System.out.println(twitter.getScreenName());
	}
}
