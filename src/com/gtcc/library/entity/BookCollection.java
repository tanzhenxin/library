package com.gtcc.library.entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gtcc.library.util.HttpManager;
import com.gtcc.library.util.LogUtils;

public class BookCollection {
	private static final String TAG = LogUtils.makeLogTag(BookCollection.class);
	
	public static final String TOTAL = "total";
	public static final String START = "start";
	public static final String COUNT = "count";
	
	private int total = 101;
	private int start = 0;
	private int count = 100;
	
	public Boolean hasMoreBooks() {
		return total > start + count;
	}
	
	public List<Book> getBooks(HttpManager httpManager, String uid) throws IOException {
		List<Book> books = new ArrayList<Book>();
		Map<String, String> params = new HashMap<String, String>();
		params.put(START, Integer.valueOf(start).toString());
		params.put(COUNT, Integer.valueOf(count).toString());
		
		String response = httpManager.doGetRequest(httpManager.getStaredBooksUrl(uid), params, false);
		try {
			JSONObject jObj = new JSONObject(response);
			
			total = jObj.getInt(TOTAL);
			start = jObj.getInt(START);
			count = jObj.getInt(COUNT);
			
			JSONArray jArray = jObj.getJSONArray("collections");
			for (int i = 0; i < jArray.length(); ++i) {
				Book book = new Book();
				
				JSONObject oneObject = jArray.getJSONObject(i);
				book.setStatus(oneObject.getString("status"));
				
				JSONObject bookObj = oneObject.getJSONObject("book");
				book.setUrl(bookObj.getString("url"));
				book.setTitle(bookObj.getString("title"));
				book.setDescription(bookObj.getString("author"));
				book.setSummary(bookObj.getString("summary"));
				book.setRating((float) bookObj.getJSONObject("rating").getDouble("average"));
				book.setImgUrl(bookObj.getString("image").replace("mpic", "lpic"));
				
				books.add(book);
			}

		} catch (JSONException e) {
			total = 0;
			LogUtils.LOGE(TAG, "Unable to parse json string: " + response);
		}
		
		return books;
	}
}