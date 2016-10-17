/**
 * 
 */
package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Properties;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import essentials.Essentials;

/**
 * @author Maximilian
 *
 */
public class ToneAnalyzer {
	Twitter twitter;
	String[] tone_ids = { "anger", "disgust", "fear", "joy", "sadness",
			"analytical", "confiden", "tentative", "openness",
			"conscientiousness", "extraversion", "agreeableness",
			"emotional_range" };

	private int[] topFive(float[] array) {
		float max1 = Float.MIN_VALUE;
		float max2 = Float.MIN_VALUE;
		float max3 = Float.MIN_VALUE;
		float max4 = Float.MIN_VALUE;
		float max5 = Float.MIN_VALUE;

		int i1 = -1;
		int i2 = -1;
		int i3 = -1;
		int i4 = -1;
		int i5 = -1;

		for (int i = 0; i < array.length; i++) {
			if (array[i] > max1) {
				max5 = max4;
				max4 = max3;
				max3 = max2;
				max2 = max1;
				max1 = array[i];
				i5 = i4;
				i4 = i3;
				i3 = i2;
				i2 = i1;
				i1 = i;
			} else if (array[i] > max2) {
				max5 = max4;
				max4 = max3;
				max3 = max2;
				max2 = array[i];
				i5 = i4;
				i4 = i3;
				i3 = i2;
				i2 = i;
			} else if (array[i] > max3) {
				max5 = max4;
				max4 = max3;
				max3 = array[i];
				i5 = i4;
				i4 = i3;
				i3 = i;

			} else if (array[i] > max4) {
				max5 = max4;
				max4 = array[i];
				i5 = i4;
				i4 = i;
			} else if (array[i] > max5) {
				max5 = array[i];
				i5 = i;
			}
		}
		int[] result = { i1, i2, i3 };
		return result;
	}

	/**
	 * @throws IOException
	 * 
	 */
	public ToneAnalyzer() throws TwitterException, IOException {
		TwitterFactory factory = new TwitterFactory();
		AccessToken accessToken = loadAccessToken("abos");
		twitter = factory.getInstance();
		twitter.setOAuthAccessToken(accessToken);
		float[] t = check("#HillaryBecause");
		System.out.println("#HillaryBecause");
		for (int i : topFive(t)) {
			System.out.println(tone_ids[i] + " " + t[i]);
		}
	}

	private float[] check(String hashtag) throws IOException {
		float[] scores = new float[13];
		try {
			Query query = new Query(hashtag);
			QueryResult result;
			query.setCount(1);
			result = twitter.search(query);

			List<Status> statusList = result.getTweets();
			String text = "";
			for (Status status : statusList) {

				text = text + status.getText() + "\n";
			}
			// System.out.println(text);
			scores = analyzeTone(text);
			// for (int i = statusList.size(); i >= 0; i--) {
			// twitter.createFavorite(statusList.get(1).getId());
			// }
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return scores;
	}

	private static float[] analyzeTone(String message) throws IOException {
		URL url = new URL(
				"https://gateway.watsonplatform.net/tone-analyzer/api/v3/tone/?version=2016-05-19&text="
						+ message);
		// params = URLEncoder.encode(params);
		// params = "test";
		try {
			String result = Essentials.sendHTTPRequest(url,
					"28d2ffc3-e805-470c-99f8-c4062ecb2569", "MGdeoIyimoVU");
			float[] values = new float[13];
			int start = 0;
			System.out.println(result);
			for (int i = 0; i < 13; i++) {
				int s = result.indexOf("\"score\":", start) + 8;
				int e = result.indexOf(",", s);
				String snippet = result.substring(s, e);
				start = e;
				float score = Float.parseFloat(snippet);
				values[i] = score;
			}
			return values;
		} catch (IOException e) {
			e.printStackTrace();
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

	public static void main(String[] args) throws TwitterException, IOException {
		// analyzeTone("Don't judge me by my past. I don't live there anymore. - Petteri Tarkkonen ");
		new ToneAnalyzer();
	}
}
