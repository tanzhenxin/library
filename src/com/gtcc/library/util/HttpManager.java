package com.gtcc.library.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import com.gtcc.library.entity.Book;
import com.gtcc.library.entity.UserInfo;
import com.gtcc.library.oauth2.AccessToken;
import com.gtcc.library.oauth2.DefaultConfigs;
import com.gtcc.library.oauth2.DoubanException;

public class HttpManager {
	
	private AccessToken accessToken;
	
	public HttpManager() {
		
	}
	
	public HttpManager(String token) {
		try {
			accessToken = Converters.stringToAccessToken(token);
		} catch (DoubanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String postEncodedEntry (String url, Map<String, String> params, boolean needAccessToken) throws DoubanException, IOException {
//	    UrlEncodedContent content = new UrlEncodedContent(params);
//	    HttpRequest method = requestFactory.buildPostRequest(new GenericUrl(url), content);
//	    return httpRequest(method, needAccessToken).parseAsString();
		
		String resultData = "";
		
		try {
	        StringBuffer sb = new StringBuffer();  
	        for(Map.Entry<String,String> entry:params.entrySet()){  
	            sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8"));  
	            sb.append("&");  
	        }
	        String data = sb.deleteCharAt(sb.length()-1).toString(); 
	        
	        HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();  
	        urlConn.setDoOutput(true);  
	        urlConn.setDoInput(true);  
	        urlConn.setRequestMethod("POST");  
	        urlConn.setUseCaches(false);  
	        urlConn.setInstanceFollowRedirects(true);  
	        urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	        urlConn.setRequestProperty("Charset", "UTF-8");
	        

	        
	        DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());  
	        
	        out.writeBytes(data);   
	        out.close();   
	        
	        String method= urlConn.getRequestMethod();
	        
	         
	        
	        int responseCode = urlConn.getResponseCode();
//	        InputStream errorstream = urlConn.getErrorStream();
//	        BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorstream));  
//	        String errorLine = "";  
//	        
//	        while (((errorLine = errorReader.readLine()) != null))  
//	        {  
//	            resultData += errorLine + "\n";  
//	        }           
//	        errorReader.close();  
	        
	        InputStream inputStream = urlConn.getInputStream();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));  
	        String inputLine = null;  
	        
	        while (((inputLine = reader.readLine()) != null))  
	        {  
	            resultData += inputLine + "\n";  
	        }           
	        reader.close();  
	        urlConn.disconnect();  
	    }  
	    catch (IOException e)  
	    {  
	    	System.out.println(e.toString());
	    }  
		
		return resultData;
	}
	
	public UserInfo getUserInfo() throws IOException {
		String url = DefaultConfigs.API_URL_PREFIX + DefaultConfigs.API_USER_INFO;
		return new UserInfo(doGetRequest(url, true));
	}
	
	public List<Book> getStaredBooks(String uid, String param) throws IOException {
		String url = DefaultConfigs.API_URL_PREFIX + 
				String.format(DefaultConfigs.API_USER_BOOKS_COLLECTION, uid) + param;
		return Book.getSubjects(doGetRequest(url, false));
	}
	
	public String doGetRequest(String url, Boolean requireToken) throws IOException  {
		String resultData = "";

		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		if (requireToken) {
			conn.setRequestProperty("Authorization", "Bearer " + accessToken.getAccessToken());
		}
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
	        InputStream errorstream = conn.getErrorStream();
	        BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorstream));  
	        String errorLine = "";  
	        
	        while (((errorLine = errorReader.readLine()) != null))  
	        {  
	            resultData += errorLine + "\n";  
	        }           
	        errorReader.close(); 
        }
        
        InputStream stream = conn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));  
        String inputLine = null;  
        
        while (((inputLine = reader.readLine()) != null))  
        {  
            resultData += inputLine + "\n";  
        }           
        reader.close();  
        conn.disconnect();  

		return resultData;
	}

//	  private static final ApacheHttpTransport APACHE_HTTP_TRANSPORT = new ApacheHttpTransport();
//	  private static final String CHARSET = "UTF-8";
//	  private String accessToken = null;
//	  HttpRequestFactory requestFactory = null;
//
//	  public HttpManager() {
//	    requestFactory = APACHE_HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
//	      @Override
//	      public void initialize(HttpRequest hr) throws IOException {
//	        hr.setParser(new XmlObjectParser(DefaultConfigs.DOUBAN_XML_NAMESPACE));
//	        HttpHeaders header = new HttpHeaders();
//	        header.setUserAgent("Dongxuexidu - Douban Java SDK");
//	        hr.setHeaders(header);
//	        hr.setNumberOfRetries(3);
//	      }
//	    });
//	  }
//
//	  public HttpManager(String accessToken) {
//	    this();
//	    this.accessToken = accessToken;
//	  }
//
//	  public void setAccessToken(String accessToken) {
//	    this.accessToken = accessToken;
//	  }
//
//	  private boolean hasAccessTokenBeenSet() {
//	    return !(this.accessToken == null || this.accessToken.isEmpty());
//	  }
//
//	  public <T extends IDoubanObject> T getResponse(String url, List<NameValuePair> params, Class<T> responseType, boolean needAccessToken) throws DoubanException, IOException {
//	    if (params != null && params.size() > 0) {
//	      String encodedParams = encodeParameters(params);
//	      url = url + "?" + encodedParams;
//	    }
//	    HttpRequest method = requestFactory.buildGetRequest(new GenericUrl(url));
//	    return httpRequest(method, needAccessToken).parseAs(responseType);
//	  }
//	  
//	  public <T extends IDoubanObject> T getResponseInJson(String url, List<NameValuePair> params, Class<T> responseType, boolean needAccessToken) throws DoubanException, IOException {
//	    if (params != null && params.size() > 0) {
//	      String encodedParams = encodeParameters(params);
//	      url = url + "?" + encodedParams;
//	    }
//	    HttpRequest method = requestFactory.buildGetRequest(new GenericUrl(url));
//	    method.setParser(new JsonObjectParser(new JacksonFactory()));
//	    return httpRequest(method, needAccessToken).parseAs(responseType);
//	  }
//	  
//	  public <T> T getResponseInJsonArray(String url, List<NameValuePair> params, Class<T> responseType, boolean needAccessToken) throws DoubanException, IOException {
//	    if (params != null && params.size() > 0) {
//	      String encodedParams = encodeParameters(params);
//	      url = url + "?" + encodedParams;
//	    }
//	    HttpRequest method = requestFactory.buildGetRequest(new GenericUrl(url));
//	    method.setParser(new JsonObjectParser(new JacksonFactory()));
//	    return httpRequest(method, needAccessToken).parseAs(responseType);
//	  }
//	  
//	  public String postEncodedEntry (String url, Map<String, String> params, boolean needAccessToken) throws DoubanException, IOException {
//	    UrlEncodedContent content = new UrlEncodedContent(params);
//	    HttpRequest method = requestFactory.buildPostRequest(new GenericUrl(url), content);
//	    return httpRequest(method, needAccessToken).parseAsString();
//	  }
//	  
//	  public String postMultipartEntry (String url, Map<String, String> params, boolean needAccessToken) throws DoubanException, IOException {
//	    UrlEncodedContent uec = new UrlEncodedContent(params);
//	    MultipartRelatedContent content = new MultipartRelatedContent(uec);
//	    HttpRequest method = requestFactory.buildPostRequest(new GenericUrl(url), content);
//	    return httpRequest(method, needAccessToken).parseAsString();
//	  }
//
//	  public <T, W extends IDoubanObject> W postResponse(String url, T requestObj, Class<W> responseType, boolean needAccessToken) throws DoubanException, IOException {
//	    AtomContent content = AtomContent.forEntry(DefaultConfigs.DOUBAN_XML_NAMESPACE, requestObj);
//	    HttpRequest method = requestFactory.buildPostRequest(new GenericUrl(url), content);
//	    return httpRequest(method, needAccessToken).parseAs(responseType);
//	  }
//	  
//	  public <T, W extends IDoubanObject> W postResponseInJson(String url, T requestObj, Class<W> responseType, boolean needAccessToken) throws DoubanException, IOException {
//	    AtomContent content = AtomContent.forEntry(DefaultConfigs.DOUBAN_XML_NAMESPACE, requestObj);
//	    HttpRequest method = requestFactory.buildPostRequest(new GenericUrl(url), content);
//	    method.setParser(new JsonObjectParser(new JacksonFactory()));
//	    return httpRequest(method, needAccessToken).parseAs(responseType);
//	  }
//
//	  public <T extends IDoubanObject> int postResponseCodeOnly(String url, T requestObj, boolean needAccessToken) throws DoubanException, IOException {
//	    AtomContent content = null;
//	    if (requestObj != null) {
//	      content = AtomContent.forEntry(DefaultConfigs.DOUBAN_XML_NAMESPACE, requestObj);
//	    } else {
//	      //Obviously the null content (proved) is not accecptable to Douban's API. Therefore, this empty obj is added for fooling Douban around, they don't care what's inside it anyway.
//	      content = AtomContent.forEntry(DefaultConfigs.DOUBAN_XML_NAMESPACE, new DoubanUserObj());
//	    }
//	    HttpRequest method = requestFactory.buildPostRequest(new GenericUrl(url), content);
//	    HttpResponse response = httpRequest(method, needAccessToken);
//	    return response.getStatusCode();
//	  }
//
//	  public <T, W extends IDoubanObject> W putResponse(String url, T requestObj, Class<W> responseType, boolean needAccessToken) throws DoubanException, IOException {
//	    AtomContent content = AtomContent.forEntry(DefaultConfigs.DOUBAN_XML_NAMESPACE, requestObj);
//	    HttpRequest method = requestFactory.buildPutRequest(new GenericUrl(url), content);
//	    return httpRequest(method, needAccessToken).parseAs(responseType);
//	  }
//	  
//	  public <T, W extends IDoubanObject> W putResponseInJson(String url, T requestObj, Class<W> responseType, boolean needAccessToken) throws DoubanException, IOException {
//	    AtomContent content = AtomContent.forEntry(DefaultConfigs.DOUBAN_XML_NAMESPACE, requestObj);
//	    HttpRequest method = requestFactory.buildPutRequest(new GenericUrl(url), content);
//	    method.setParser(new JsonObjectParser(new JacksonFactory()));
//	    return httpRequest(method, needAccessToken).parseAs(responseType);
//	  }
//
//	  public <T extends IDoubanObject> int putResponseCodeOnly(String url, T requestObj, boolean needAccessToken) throws DoubanException, IOException {
//	    AtomContent content = AtomContent.forEntry(DefaultConfigs.DOUBAN_XML_NAMESPACE, requestObj);
//	    HttpRequest method = requestFactory.buildPutRequest(new GenericUrl(url), content);
//	    return httpRequest(method, needAccessToken).getStatusCode();
//	  }
//
//	  public int deleteResponse(String url, boolean needAccessToken) throws DoubanException, IOException {
//	    HttpRequest method = requestFactory.buildDeleteRequest(new GenericUrl(url));
//	    return httpRequest(method, needAccessToken).getStatusCode();
//	  }
//
//	  private HttpResponse httpRequest(HttpRequest method, boolean needToken) throws DoubanException, IOException {
//	    try {
//	      if (needToken) {
//	        if (!hasAccessTokenBeenSet()) {
//	          throw ErrorHandler.accessTokenNotSet();
//	        }
//	        HttpHeaders headers = method.getHeaders();
//	        headers.setAuthorization("Bearer " + this.accessToken);
//	      }
//	      HttpResponse res = method.execute();
//	      return res;
//	    } catch (HttpResponseException ex) {
//	      throw ErrorHandler.handleHttpResponseError(ex);
//	    }
//	  }
//
//	  private static String encodeParameters(List<NameValuePair> params) {
//	    StringBuilder buf = new StringBuilder();
//	    int j = 0;
//	    for (NameValuePair nvp : params) {
//	      if (j != 0) {
//	        buf.append("&");
//	      }
//	      j++;
//	      try {
//	        buf.append(URLEncoder.encode(nvp.getName(), CHARSET)).append("=").append(URLEncoder.encode(nvp.getValue(), CHARSET));
//	      } catch (java.io.UnsupportedEncodingException ex) {
//	        System.out.println("Shouldn't go this far");
//	      }
//	    }
//	    return buf.toString();
//	  }
	}