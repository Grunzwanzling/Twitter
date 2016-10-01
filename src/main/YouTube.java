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
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import essentials.SimpleLog;

/**
 * @author Maximilian
 *
 */
public class YouTube {

	String path;
	String name;
	String congrats;
	String tokenID;
	String[] topURL;
	String YouTubeAPIkey = "AIzaSyCJc0FXy-09W07N0OPlMiHezPsCdNrX0mY";
	String infoPath;
	static long t;
	static long i;

	Random rand = new Random();
	String[] congratulations;
	SimpleLog log;
	static DecimalFormat nf = new DecimalFormat();

	TwitterFactory factory;
	AccessToken accessToken;
	Twitter twitter;

	static DateFormat dateFormat = new SimpleDateFormat("mm");

	public YouTube(String path, String name, String congrats, String tokenID,
			String[] topURLs, String infoPath, SimpleLog log) {
		this.path = path;
		this.name = name;
		this.congrats = congrats;
		this.tokenID = tokenID;
		this.log = log;
		this.topURL = topURLs;
		this.infoPath = infoPath;
		congratulations = getCongratulations();

		// Connecting to Twitter API
		factory = new TwitterFactory();
		accessToken = loadAccessToken(tokenID);
		twitter = factory.getInstance();
		twitter.setOAuthAccessToken(accessToken);
		log.info("Successfully loaded congratulations and connected to Twitter");
	}

	private void updateProfile(int count) {
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(new File(infoPath)));

			twitter.updateProfile(
					props.getProperty("name"),
					props.getProperty("url"),
					props.getProperty("location"),
					props.getProperty("bio").replaceAll("count",
							String.valueOf(count)));
		} catch (TwitterException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ArrayList<String> getChannelsToCheck(String[] paths)
			throws FileNotFoundException, IOException {
		ArrayList<String> list = new ArrayList<String>();
		Properties props = new Properties();
		props.load(new FileInputStream(new File(path)));

		Set<Object> s = props.keySet();
		for (Object object : s) {
			list.add((String) object);
		}
		for (String path : paths) {
			int size = 100;
			if (path.contains("500"))
				size = 500;
			String[] top = getTopYouTubers(path, size);
			for (String string : top) {
				if (!list.contains(string))
					list.add(string);
			}
		}

		return list;
	}

	private int check() {
		try {
			log.info("Checking for events for " + name);
			long time = System.currentTimeMillis();
			ArrayList<String> list = getChannelsToCheck(topURL);
			int i = 0;
			String channels[] = new String[list.size()];
			for (Object object : list) {
				channels[i] = (String) object;
				i++;
			}
			Properties props = new Properties();
			props.load(new FileInputStream(new File(path)));
			for (String channel : channels) {
				String subs = round(getSubs(channel));
				Thread.sleep(20);
				if (!props.containsKey(channel)) {
					log.info("Found a new channel \"" + channel + "\"");
					props.setProperty(channel, subs);
					continue;
				}
				if (!props.getProperty(channel).equals(subs)) {
					log.info("Found a event on channel \"" + channel + "\"");
					if (!subs.equals("0")) {
						String twitter = getTwitterName(channel);
						if (twitter != null)
							createPost(channel, subs, twitter);
						else
							createPost(channel, subs, getTitle(channel));
						props.setProperty(channel, subs);
					}

				}
				// else
				// System.out.println(string + " still has "
				// + round(getSubs(string)));

			}
			props.store(new FileOutputStream(new File(path)), "");

			log.info("Checked " + channels.length + " channels in "
					+ (System.currentTimeMillis() - time) + " ms");
			return channels.length;
		} catch (IOException | InterruptedException e) {
			log.error("Error occured while checking");
			log.logStackTrace(e);
			return 0;
		}

	}

	private static String round(String subs) {
		try {
			// System.out.println(subs);
			int sub = Integer.parseInt(subs);
			int down = sub / 100000;

			return String.valueOf(nf.format(down * 100000));
		} catch (NumberFormatException e) {
			return "0";
		}
	}

	private void createPost(String username, String subs, String title) {
		try {
			System.out.println(username);
			System.out.println(title);
			// Generate the message
			int index = new Random().nextInt(congratulations.length);
			String message = congratulations[index];
			message = message.replaceAll("@user", username);
			message = message.replaceAll("@title", title);
			message = message.replaceAll("@subs", subs);

			StatusUpdate statusUpdate = new StatusUpdate(message);
			Status status = twitter.updateStatus(statusUpdate);
			log.info("Successfully updated the status to [" + status.getText()
					+ "].");
		} catch (TwitterException e) {
			log.error("Error occured while creating Post");
			log.logStackTrace(e);
		}
	}

	private String[] getCongratulations() {
		try {
			BufferedReader bufr = new BufferedReader(new FileReader(new File(
					congrats)));
			ArrayList<String> list = new ArrayList<String>();
			String line = bufr.readLine();
			while (line != null) {
				list.add(line);
				line = bufr.readLine();
			}
			bufr.close();
			congratulations = new String[list.size()];
			for (int i = 0; i < list.size(); i++)
				congratulations[i] = list.get(i);
		} catch (IOException e) {
			log.error("Error occured while getting congratulations");
			log.logStackTrace(e);
		}
		return congratulations;

	}

	private String[] getTopYouTubers(String topURL, int size) {
		try {
			String result = Essentials.sendHTTPRequest(new URL(topURL));
			String channel = "d";
			int index = 0;
			String channels[] = new String[size];
			for (int i = 0; i < size; i++) {
				index = result.indexOf("<a href=\"/youtube/user/", index) + 23;
				int newIndex = result.indexOf("\"", index);
				channel = result.substring(index, newIndex);
				channels[i] = channel;
				index = newIndex;
			}
			return channels;
		} catch (IOException e) {
			log.error("Error occured while getting top Youtubers");
			log.logStackTrace(e);
			return null;
		}

	}

	private String getSubs(String username) {
		try {
			i++;
			try {
				String result = Essentials.sendHTTPRequest(new URL(
						"https://www.googleapis.com/youtube/v3/channels?part=statistics&forUsername="
								+ username + "&key=" + YouTubeAPIkey));
				return result.substring(result.indexOf("subscriberCount") + 19,
						result.indexOf("\"",
								result.indexOf("subscriberCount") + 19));
			} catch (MalformedURLException e) {
				// ignore this shit
				return null;
			}
		} catch (IOException e) {
			log.error("Error occured while getting subscribers of " + username);
			log.logStackTrace(e);
			return null;
		}
	}

	private String getTitle(String username) {
		try {
			String result = Essentials.sendHTTPRequest(new URL(
					"https://www.googleapis.com/youtube/v3/channels?part=snippet&forUsername="
							+ username + "&key=" + YouTubeAPIkey));
			return result.substring(result.indexOf("title") + 9,
					result.indexOf("\"", result.indexOf("title") + 9));
		} catch (IOException e) {
			log.error("Error occured while getting title of " + username);
			log.logStackTrace(e);
			return null;
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

	private String getTwitterName(String username) {
		try {

			String result = Essentials.sendHTTPRequest(new URL(
					"https://www.youtube.com/user/" + username + "/about"));
			if (result.indexOf("twitter.com/") == -1)
				return null;

			return "@"
					+ result.substring(result.indexOf("twitter.com/") + 12,
							result.indexOf("\"",
									result.indexOf("twitter.com/") + 12));
		} catch (IOException e) {
			log.error("Error occured while getting the Twitter name of "
					+ username);
			log.logStackTrace(e);
			return null;
		}
	}

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		SimpleLog log = new SimpleLog(new File(
				"C://twitter//Aboerfolge//log.txt"), true, true);
		log.startupMessage("Starting Aboerfolg-Bot...");
		String[] yt1top = {
				"http://socialblade.com/youtube/top/country/de/mostsubscribed",
				"http://socialblade.com/youtube/top/country/de" };
		String[] yt2top = {
				"http://socialblade.com/youtube/top/500/mostsubscribed",
				"http://socialblade.com/youtube/top/500",
				"http://socialblade.com/youtube/top/5001d" };
		YouTube yt = new YouTube("C:\\twitter\\Aboerfolge\\subs.properties",
				"AboerfolgeDE", "C:\\twitter\\Aboerfolge\\congratulations.txt",
				"abos", yt1top, "C:\\twitter\\Aboerfolge\\info.properties", log);
		YouTube yt2 = new YouTube("C:\\twitter\\Aboerfolge\\subs2.properties",
				"SubcounterINT",
				"C:\\twitter\\Aboerfolge\\congratulationsEN.txt", "abo2",
				yt2top, "C:\\twitter\\Aboerfolge\\info2.properties", log);

		while (true) {
			Date d = new Date();
			if (dateFormat.format(d).endsWith("0")) {

				yt.updateProfile(yt.check());
				yt2.updateProfile(yt2.check());
				Thread.sleep(61000);
			}

		}
	}
}
