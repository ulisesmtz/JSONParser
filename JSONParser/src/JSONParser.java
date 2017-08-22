import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSONParser {
	
	/**
	 * Opens up an URL, extracts the JSON content and returns the content as a String
	 * @param myURL the URL to open
	 * @return content of URL in String format 
	 */
	public static String callURL(String myURL) {
		StringBuilder sb = new StringBuilder();
		URLConnection urlConn = null;
		InputStreamReader in = null;
		try {
			URL url = new URL(myURL);
			urlConn = url.openConnection();
			if (urlConn != null)
				urlConn.setReadTimeout(60 * 1000);
			if (urlConn != null && urlConn.getInputStream() != null) {
				in = new InputStreamReader(urlConn.getInputStream(), Charset.defaultCharset());
				BufferedReader bufferedReader = new BufferedReader(in);
				if (bufferedReader != null) {
					// append characters to stringbuilder
					int cp;
					while ((cp = bufferedReader.read()) != -1) {
						sb.append((char) cp);
					}

					bufferedReader.close();
				}
			}
			in.close();
		} catch (Exception e) {
			throw new RuntimeException("Exception while calling URL:" + myURL, e);
		}

		return sb.toString();
	}
	
	
	public static void main(String[] args) {
		int hdTrue = 0;
		int hdFalse = 0;
		int page = 1;  // holds the current page number
		
		System.out.println("Parsing results...");

		while (true) { 
			String jsonString = callURL("http://api.viki.io/v4/videos.json?app=100250a&per_page=10&page=" + page++);
			JSONObject jsonObject = new JSONObject(jsonString);

			// do NOT parse page's JSON if more equals false
			if (jsonObject.get("more").toString().equals("false"))
				break; 

			JSONArray jsonArrayResponse = (JSONArray) jsonObject.get("response");

			// loop through the array, get all JSON objects with "flags" as key, and compare value
			for (int i = 0; i < jsonArrayResponse.length(); i++) {
				JSONObject jsonObjectResponse = jsonArrayResponse.getJSONObject(i);
				JSONObject jsonObjectFlags = (JSONObject) jsonObjectResponse.get("flags");

				if (jsonObjectFlags.get("hd").toString().equals("true"))
					hdTrue++;
				else
					hdFalse++;
			}
		}
		
		System.out.println("HD True Count:  " + hdTrue);
		System.out.println("HD False Count: " + hdFalse);
	}
}