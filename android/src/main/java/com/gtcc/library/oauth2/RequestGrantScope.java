package com.gtcc.library.oauth2;

public class RequestGrantScope {

	private String value;
	private String name;

	private static final String SCOPE_SHUO_READ = "shuo_basic_r";
	private static final String SCOPE_SHUO_WRITE = "shuo_basic_w";
	private static final String SCOPE_MAIL_READ = "community_advanced_doumail_r";
	private static final String SCOPE_MAIL_WRITE = "community_advanced_doumail_w";
	private static final String SCOPE_BASIC_COMMON = "douban_basic_common";
	private static final String SCOPE_BASIC_NOTE = "community_basic_note";
	private static final String SCOPE_BOOK_READ = "book_basic_r";
	private static final String SCOPE_MOVIE_READ = "movie_basic_r";
	private static final String SCOPE_MUSIC_READ = "music_basic_r";
	private static final String SCOPE_EVENT_READ = "event_basic_r";
	private static final String SCOPE_EVENT_WRITE = "event_basic_w";
	private static final String SCOPE_ALL = "all";

	public static final RequestGrantScope SHUO_READ_SCOPE = new RequestGrantScope(
			"DoubanShuoRead", RequestGrantScope.SCOPE_SHUO_READ);
	public static final RequestGrantScope SHUO_WRITE_SCOPE = new RequestGrantScope(
			"DoubanShuoWrite", RequestGrantScope.SCOPE_SHUO_WRITE);
	public static final RequestGrantScope BASIC_COMMON_SCOPE = new RequestGrantScope(
			"BasicCommon", RequestGrantScope.SCOPE_BASIC_COMMON);
	public static final RequestGrantScope MAIL_READ_SCOPE = new RequestGrantScope(
			"MailRead", RequestGrantScope.SCOPE_MAIL_READ);
	public static final RequestGrantScope MAIL_WRITE_SCOPE = new RequestGrantScope(
			"DoubanShuoWrite", RequestGrantScope.SCOPE_MAIL_WRITE);
	public static final RequestGrantScope BASIC_NOTE_SCOPE = new RequestGrantScope(
			"BasicNote", RequestGrantScope.SCOPE_BASIC_NOTE);
	public static final RequestGrantScope BOOK_READ_SCOPE = new RequestGrantScope(
			"BookRead", RequestGrantScope.SCOPE_BOOK_READ);
	public static final RequestGrantScope MOVIE_READ_SCOPE = new RequestGrantScope(
			"MovieRead", RequestGrantScope.SCOPE_MOVIE_READ);
	public static final RequestGrantScope MUSIC_READ_SCOPE = new RequestGrantScope(
			"MusicRead", RequestGrantScope.SCOPE_MUSIC_READ);
	public static final RequestGrantScope EVENT_READ_SCOPE = new RequestGrantScope(
			"EventRead", RequestGrantScope.SCOPE_EVENT_READ);
	public static final RequestGrantScope EVENT_WRITE_SCOPE = new RequestGrantScope(
			"EventWrite", RequestGrantScope.SCOPE_EVENT_WRITE);
	public static final RequestGrantScope ALL_SCOPE = new RequestGrantScope(
			"All", RequestGrantScope.SCOPE_ALL);

	private RequestGrantScope(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public String getName() {
		return name;
	}

}
