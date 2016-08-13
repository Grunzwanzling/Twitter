/**
 * 
 */
package main;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import essentials.Essentials;

/**
 * @author Maximilian
 *
 */
public class Weather {

	/**
	 * 
	 */
	public Weather() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public static void main(String[] args) throws MalformedURLException,
			IOException {
		getWeather("Aalen,de");

	}

	static String getWeather(String city) throws MalformedURLException,
			IOException {

		String response = Essentials.sendHTTPRequest(new URL(
				"http://api.openweathermap.org/data/2.5/forecast?q=" + city
						+ "&mode=xml&appid=cb91c543c26c9841bc43af7a888415d0"));

		response = response.substring(response.indexOf("<forecast>") + 10,
				response.indexOf("</forecast>"));
		String[] responses = response.split("<time");
		Properties prop = new Properties();
		for (String string : responses) {
			// System.out.println(string + "\n\n");
			if (string.length() > 25) {
				String timestamp = string.substring(7, 26);
				String content = string.substring(53, string.length());
				System.out.println(timestamp + ": " + content + "\n\n");
				prop.setProperty(timestamp, content);
				parseData(content);

			}
		}

		return response;

	}

	static void parseData(String content) {
		String symbol = content.substring(
				content.indexOf("symbol number=") + 15,
				content.indexOf("symbol number=") + 18);
		System.out.println(symbol);
		String temp = content
				.substring(
						content.indexOf("temperature unit=\"celsius\" value=\"") + 34,
						content.indexOf(
								"\"",
								content.indexOf("temperature unit=\"celsius\" value=\"") + 34));
		System.out.println(temp);
	}
}
