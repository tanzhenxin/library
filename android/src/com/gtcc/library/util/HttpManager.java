package com.gtcc.library.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import com.gtcc.library.entity.Book;
import com.gtcc.library.entity.UserInfo;
import com.gtcc.library.oauth2.AccessToken;
import com.gtcc.library.oauth2.Converters;
import com.gtcc.library.oauth2.DefaultConfigs;
import com.gtcc.library.oauth2.DoubanException;
import com.gtcc.library.webserviceproxy.WebServiceBookProxy;
import com.gtcc.library.webserviceproxy.WebServiceBorrowProxy;
import com.gtcc.library.webserviceproxy.WebServiceUserProxy;

public class HttpManager {

	private AccessToken accessToken;
	private static final String TAG = LogUtils.makeLogTag(HttpManager.class);
	public static WebServiceBookProxy webServiceBookProxy = new WebServiceBookProxy();
	public static WebServiceBorrowProxy webServiceBorrowProxy = new WebServiceBorrowProxy();
	public static WebServiceUserProxy webServiceUserProxy = new WebServiceUserProxy();

	public HttpManager() {

	}

	public HttpManager(String token) {
		try {
			accessToken = Converters.stringToAccessToken(token);
		} catch (DoubanException e) {
			LogUtils.LOGE(TAG, "Unable to parse access token: " + accessToken);
		}
	}

	public String postEncodedEntry(String url, Map<String, String> params,
			boolean needAccessToken) throws DoubanException, IOException {
		String resultData = "";

		HttpURLConnection urlConn = (HttpURLConnection) new URL(url)
				.openConnection();
		urlConn.setDoOutput(true);
		urlConn.setDoInput(true);
		urlConn.setRequestMethod("POST");
		urlConn.setUseCaches(false);
		urlConn.setInstanceFollowRedirects(true);
		urlConn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		urlConn.setRequestProperty("Charset", "UTF-8");

		DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());
		out.writeBytes(constructParams(params));
		out.close();

		int responseCode = urlConn.getResponseCode();
		if (responseCode != 200) {
			InputStream errorstream = urlConn.getErrorStream();
			BufferedReader errorReader = new BufferedReader(
					new InputStreamReader(errorstream));
			String errorLine = "";

			while (((errorLine = errorReader.readLine()) != null)) {
				resultData += errorLine + "\n";
			}
			errorReader.close();

			LogUtils.LOGE(TAG, "postEncodedEntry failed. Reason: " + resultData);
		} else {
			InputStream inputStream = urlConn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream));
			String inputLine = null;

			while (((inputLine = reader.readLine()) != null)) {
				resultData += inputLine + "\n";
			}
			reader.close();

			LogUtils.LOGV(TAG, "postEncodedEntry succeed.");
		}
		urlConn.disconnect();

		return resultData;
	}

	public UserInfo getUserInfo() throws IOException {
		String url = DefaultConfigs.DOUBAN_API_URL_PREFIX
				+ DefaultConfigs.DOUBAN_API_USER_INFO;
		return new UserInfo(doGetRequest(url, true));
	}
	
	public String doGetRequest(String url, Map<String, String> params, Boolean requireToken) throws IOException {
		String theParam = constructParams(params);
		
		if (url.contains("?")) 
			url += ("&" + theParam);
		else
			url += ("?" + theParam);
		
		return doGetRequest(url, requireToken);
	}

	public String doGetRequest(String url, Boolean requireToken)
			throws IOException {
		LogUtils.LOGV(TAG, "Start doGetRequest. Url: " + url);
		String resultData = "";
		
		HttpURLConnection conn = (HttpURLConnection) new URL(appendApiKey(url))
				.openConnection();
		if (requireToken) {
			conn.setRequestProperty("Authorization",
					"Bearer " + accessToken.getAccessToken());
		}
		conn.setReadTimeout(10000 /* milliseconds */);
		conn.setConnectTimeout(15000 /* milliseconds */);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		conn.connect();

		int responseCode = conn.getResponseCode();
		if (responseCode != 200) {
			InputStream errorstream = conn.getErrorStream();
			BufferedReader errorReader = new BufferedReader(
					new InputStreamReader(errorstream));
			String errorLine = "";

			while (((errorLine = errorReader.readLine()) != null)) {
				resultData += errorLine + "\n";
			}
			errorReader.close();

			LogUtils.LOGE(TAG, "doGetRequest failed. Reason: " + resultData);
		} else {
			InputStream stream = conn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					stream));
			String inputLine = null;

			while (((inputLine = reader.readLine()) != null)) {
				resultData += inputLine + "\n";
			}
			reader.close();
			
			LogUtils.LOGV(TAG, "doGetRequest succeed.");
		}
		conn.disconnect();

		return resultData;
	}
	
	public static List<Book> getDoubanNewBooks() throws IOException {
		List<Book> books = new ArrayList<Book>();
		URL uri = new URL("http://book.douban.com/latest");
		HttpURLConnection httpConn = (HttpURLConnection) uri.openConnection();
		httpConn.setDoInput(true);
		httpConn.connect();
		InputStream is = httpConn.getInputStream();
		Source source = new Source(is);
		List<Element> divs = source.getAllElements("li");
		for (Element e : divs) {
			List<Element> childs = e.getChildElements();
			if (childs.size() == 2) {
				Element contents = childs.get(0);
				Element otherinfo = childs.get(1);
				String id = otherinfo.getAttributeValue("href");
				
				String img = "";
				List<Element> childElements = otherinfo.getChildElements();
				if (childElements.size() > 0)
					img = childElements.get(0).getAttributeValue("src");

				if ("detail-frame".equals(childs.get(0).getAttributeValue("class"))) {

					Book book = new Book();

					id = id.substring(0, id.length() - 1);
					id = id.substring(id.lastIndexOf("/") + 1);
					id = DefaultConfigs.DOUBAN_API_BOOK_INFO + id;
					book.setUrl(id);
					book.setImgUrl(img);
					book.setTitle(contents.getChildElements().get(0)
							.getTextExtractor().toString());
					book.setAuthor(contents.getChildElements().get(1)
							.getTextExtractor().toString());
					book.setSummary(contents.getChildElements().get(2)
							.getTextExtractor().toString());

					books.add(book);
				}

			}

		}
		is.close();
		Collections.shuffle(books);

		return books;
	}
	
	private String constructParams(Map<String, String> params)
			throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(entry.getKey()).append("=")
					.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
			sb.append("&");
		}
		return sb.deleteCharAt(sb.length() - 1).toString();
	}
	
	private String appendApiKey(String url) throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
		if (url.contains("?"))
			sb.append("&");
		else
			sb.append("?");
		
		sb.append("apikey").append("=")
			.append(URLEncoder.encode(DefaultConfigs.DOUBAN_API_KEY, "UTF-8"));
		return url + sb.toString();
	}
}