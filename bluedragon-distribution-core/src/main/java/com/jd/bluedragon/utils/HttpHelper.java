package com.jd.bluedragon.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HttpHelper {
	
	public static String getContent(String address, String characterSet) {
		StringBuilder html = new StringBuilder();
		URL url = null;
		Reader reader = null;
		try {
			url = new URL(address);
			reader = new InputStreamReader(new BufferedInputStream(url.openStream()), characterSet);
			
			int c;
			while ((c = reader.read()) != -1) {
				html.append((char) c);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return new String(html);
	}
	
	public static Integer getResponseCode(String address) {
		int code = -1;
		
		try {
			URL url = new URL(address);
			
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setConnectTimeout(1000);
			
			code = connection.getResponseCode();
			System.out.println(code);
			connection.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return code;
	}
	
	public static void main(String[] args) {
		List<String> urls = new ArrayList<String>();
		urls.add("http://www.jd.com/");
		
		for (String url : urls) {
			System.out.println(url);
			String content = HttpHelper.getContent(url, "GBK");
			System.out.println(content);
		}
	}
}
