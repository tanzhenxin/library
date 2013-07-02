package com.gtcc.library.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gtcc.library.R;
import com.gtcc.library.oauth2.DefaultConfigs;
import com.gtcc.library.oauth2.DoubanException;
import com.gtcc.library.oauth2.OAuth2DoubanProvider;
import com.gtcc.library.oauth2.RequestGrantScope;
import com.gtcc.library.ui.MainActivity;

public class UserLoginActivity extends Activity {
	
	private WebView webview;
	
	private static final String AUTHORIZATION_CODE = "code=";
	private OAuth2DoubanProvider provider;
	
	private String accessToken;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		provider = new OAuth2DoubanProvider().addScope(RequestGrantScope.BASIC_COMMON_SCOPE).addScope(RequestGrantScope.BOOK_READ_SCOPE);
		
		webview = (WebView) findViewById(R.id.login_webview);
		webview.setWebViewClient(new WebViewClient() {
        	@Override
        	public boolean shouldOverrideUrlLoading(WebView view, String url) {
        		if ( url.startsWith(DefaultConfigs.ACCESS_TOKEN_REDIRECT_URL) ) {
        			if ( url.indexOf(AUTHORIZATION_CODE) != -1 ) {
        				String accessCode = extractCode(url);
        				
        				new TradeAccessTokenAsyncTask().execute(accessCode);
        			}
        			
        			return true;
        		}

        		return super.shouldOverrideUrlLoading(view, url);
        	}
        });
        
        // do OAuth2 login
        String authorizationUri = provider.getGetCodeRedirectUrl();
        webview.loadUrl(authorizationUri);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

    private String extractCode(String url) {
    	String[] sArray = url.split(AUTHORIZATION_CODE);
    	return sArray[1];
    }
    
	private class TradeAccessTokenAsyncTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String accessToken = null;
			try {
				accessToken = provider.tradeAccessTokenWithCode(params[0]);
			} catch (DoubanException e1) {
				e1.printStackTrace();
			}
			
			return accessToken;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			Intent intent = new Intent();
			intent.putExtra(MainActivity.SHPREF_KEY_ACCESS_TOKEN, result);
			setResult(Activity.RESULT_OK, intent);
			finish();
		}
    	
    }
}