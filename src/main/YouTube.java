/**
 * 
 */
package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import essentials.Essentials;

/**
 * @author Maximilian
 *
 */
public class YouTube {

	static long t;
	static long i;

	static Random rand = new Random();

	public YouTube() {
		// TODO Auto-generated constructor stub
	}

	private static ArrayList<String> getChannelsToCheck()
			throws FileNotFoundException, IOException {
		ArrayList<String> list = new ArrayList<String>();
		Properties props = new Properties();
		props.load(new FileInputStream(new File(
				"C:\\twitter\\Aboerfolge\\subs.properties")));

		Set<Object> s = props.keySet();
		for (Object object : s) {
			list.add((String) object);
		}
		String[] top = getTopYouTubers();
		for (String string : top) {
			if (!list.contains(string))
				list.add(string);
		}
		System.out.println(list.size());
		return list;
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws MalformedURLException
	 * @throws TwitterException
	 */
	public static void main(String[] args) throws MalformedURLException,
			IOException, TwitterException {

		System.exit(0);

		ArrayList<String> list = getChannelsToCheck();
		int i = 0;
		String channels[] = new String[getChannelsToCheck().size()];
		for (Object object : list) {
			channels[i] = (String) object;
			i++;
		}
		Properties props = new Properties();
		props.load(new FileInputStream(new File(
				"C:\\twitter\\Aboerfolge\\subs.properties")));
		for (String channel : channels) {

			String subs = round(getSubs(channel));

			if (!props.containsKey(channel)) {
				props.setProperty(channel, subs);
				continue;
			}
			if (!props.getProperty(channel).equals(subs)) {
				createPost(channel, subs);
				props.setProperty(channel, subs);

			}
			// else
			// System.out.println(string + " still has "
			// + round(getSubs(string)));

		}
		props.store(new FileOutputStream(new File(
				"C://twitter//subs.properties")), "");

		System.out.println(t / i);

	}

	private static String round(String subs) {
		try {
			// System.out.println(subs);
			int sub = Integer.parseInt(subs);
			int down = sub / 100000;
			// System.out.println(down * 100000);
			return String.valueOf(down * 100000);
		} catch (NumberFormatException e) {
			return "0";
		}
	}

	private static void createPost(String username, String subs)
			throws IOException, TwitterException {

		// Generate the message
		String[] congratulations = getCongratulations();
		int index = new Random().nextInt(congratulations.length);
		String message = congratulations[index];
		message = message.replaceAll("@user", username);
		message = message.replaceAll("@title", getTitle(username));
		message = message.replaceAll("@subs", subs);

		// Connecting to Twitter

		TwitterFactory factory = new TwitterFactory();
		AccessToken accessToken = loadAccessToken("abos");
		Twitter twitter = factory.getInstance();
		twitter.setOAuthAccessToken(accessToken);
		StatusUpdate statusUpdate = new StatusUpdate(message);
		Status status = twitter.updateStatus(statusUpdate);
		System.out.println("Successfully updated the status to ["
				+ status.getText() + "].");
	}

	private static String[] getCongratulations() throws IOException {
		BufferedReader bufr = new BufferedReader(new FileReader(new File(
				"C://twitter//Aboerfolge//congratulations.txt")));
		ArrayList<String> list = new ArrayList<String>();
		String line = bufr.readLine();
		while (line != "") {
			list.add(line);
			line = bufr.readLine();
		}
		bufr.close();
		String[] congratulations = new String[list.size()];
		for (int i = 0; i < list.size(); i++)
			congratulations[i] = list.get(i);
		return congratulations;

	}

	private static String[] getTopYouTubers() throws MalformedURLException,
			IOException {
		String result = Essentials
				.sendHTTPRequest(new URL(
						"http://socialblade.com/youtube/top/country/de/mostsubscribed"));
		String channel = "d";
		int index = 0;
		String channels[] = new String[100];
		for (int i = 0; i < 100; i++) {
			index = result.indexOf("<a href=\"/youtube/user/", index) + 23;
			int newIndex = result.indexOf("\"", index);
			channel = result.substring(index, newIndex);
			channels[i] = channel;
			index = newIndex;
		}
		return channels;
	}

	private static String getSubs(String username)
			throws MalformedURLException, IOException {
		i++;
		long time = System.currentTimeMillis();
		String result = Essentials.sendHTTPRequest(new URL(
				"https://www.googleapis.com/youtube/v3/channels?part=statistics&forUsername="
						+ username
						+ "&key=AIzaSyCJc0FXy-09W07N0OPlMiHezPsCdNrX0mY"));
		t += (System.currentTimeMillis() - time);
		return result.substring(result.indexOf("subscriberCount") + 19,
				result.indexOf("\"", result.indexOf("subscriberCount") + 19));
	}

	private static String getTitle(String username)
			throws MalformedURLException, IOException {
		String result = Essentials.sendHTTPRequest(new URL(
				"https://www.googleapis.com/youtube/v3/channels?part=snippet&forUsername="
						+ username
						+ "&key=AIzaSyCJc0FXy-09W07N0OPlMiHezPsCdNrX0mY"));
		return result.substring(result.indexOf("title") + 9,
				result.indexOf("\"", result.indexOf("title") + 9));
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
}
