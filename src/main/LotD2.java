/**
 * 
 */
package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

import javax.imageio.ImageIO;

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
	static BufferedImage bi;
	static Graphics g;
	static String[] fontNames = GraphicsEnvironment
			.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
	static Random rand = new Random();

	public static void main(String args[]) throws Exception {
		char c = (char) (rand.nextInt(26) + 'A');
		generateImage(c);
		// The factory instance is re-useable and thread safe.
		TwitterFactory factory = new TwitterFactory();
		AccessToken accessToken = loadAccessToken();
		Twitter twitter = factory.getInstance();
		twitter.setOAuthAccessToken(accessToken);
		StatusUpdate statusUpdate = new StatusUpdate("API-Test4");
		statusUpdate.setMedia(new File("C://twitter//image.png"));
		Status status = twitter.updateStatus(statusUpdate);
		System.out.println("Successfully updated the status to ["
				+ status.getText() + "].");
		System.exit(0);
	}

	private static void generateImage(char c) throws IOException {
		new File("C://twitter").mkdir();
		File outputfile = new File("C:\\twitter\\image.png");
		if (outputfile.exists())
			outputfile.delete();

		bi = new BufferedImage(1500, 1500, BufferedImage.TYPE_INT_ARGB_PRE);
		g = bi.getGraphics();

		// System.out.println("Rendering Image " + i);

		long t = 0, t1 = 0, t2 = 0, time = 0;

		for (int e = 0; e < 700; e++) {

			// Color
			g.setColor(Color.getHSBColor(rand.nextFloat(), 1.0f, 1.0f));

			String fontName = fontNames[(int) (Math.random() * (fontNames.length - 1))];

			int size = rand.nextInt(800) + 50;

			Font font = new Font(fontName, Font.PLAIN, size);
			g.setFont(font);

			int r = rand.nextInt(6);
			int x = rand.nextInt(1500);
			int y = rand.nextInt(1500);

			int x2 = rand.nextInt(700);
			int y2 = rand.nextInt(700);

			int x3 = rand.nextInt(1300) - 100;
			int y3 = rand.nextInt(1300) - 100;

			switch (r) {
			case 0:
				time = System.currentTimeMillis();
				g.fillRect(x3, y3, x2, y2);
				t = System.currentTimeMillis() - time + t;

				break;
			case 1:
				time = System.currentTimeMillis();
				g.fillOval(x3, y3, x2, y2);
				t1 = System.currentTimeMillis() - time + t1;
				break;
			case 2:
				time = System.currentTimeMillis();
				g.drawString(String.valueOf(c), x, y);
				t2 = System.currentTimeMillis() - time + t2;
				break;
			case 3:
				time = System.currentTimeMillis();
				g.drawString(String.valueOf(c), x, y);
				t2 = System.currentTimeMillis() - time + t2;
				break;
			case 4:
				time = System.currentTimeMillis();
				g.drawString(String.valueOf(c), x, y);
				t2 = System.currentTimeMillis() - time + t2;
				break;
			case 5:
				time = System.currentTimeMillis();
				g.drawString(String.valueOf(c), x, y);
				t2 = System.currentTimeMillis() - time + t2;
				break;
			}

		}

		System.out.println(t2);

		ImageIO.write(bi, "png", outputfile);

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
