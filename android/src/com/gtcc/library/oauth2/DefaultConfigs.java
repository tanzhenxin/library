package com.gtcc.library.oauth2;

public class DefaultConfigs {
	  
	  public static final String API_KEY = "04629e3361e9ff6b0d27da9a25bdfe41";
	  public static final String SECRET_KEY = "dfee9ee1dbe96620";
	  public static final String API_URL_PREFIX = "https://api.douban.com";
	  public static final String AUTH_URL = "https://www.douban.com/service/auth2/auth";
	  public static final String ACCESS_TOKEN_URL = "https://www.douban.com/service/auth2/token";
	  public static final String ACCESS_TOKEN_REDIRECT_URL = "http://myapp.com/callback";
	  
	  public static final String API_USER_BOOKS_COLLECTION = "/v2/book/user/%s/collections";
	  public static final String API_USER_INFO = "/v2/user/~me";
	  public static final String API_BOOK_INFO = "/v2/book/";
	  public static final String API_BOOK_SEARCH = "/v2/book/search";
	  public static final String API_BOOK_SEARCH_KEY = "q";
}
