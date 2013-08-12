package com.gtcc.library;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import com.gtcc.library.ui.LoginActivity;
import com.gtcc.library.ui.LoginActivity.UserLoginTask;
import com.gtcc.library.webserviceproxy.WebServiceUserProxy;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TestActivity extends Activity {

	Intent intent;
	Bundle bl;
	private EditText mUsername;
	private EditText mPassword;
	private EditText mEmail;
	
	private UserLoginTask mAuthTask = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);

		intent=this.getIntent();
		bl=intent.getExtras();
		TextView name = (TextView)findViewById(R.id.username);
		
		mUsername = (EditText)findViewById(R.id.editText1);
		mPassword = (EditText)findViewById(R.id.editText2);
		mEmail = (EditText)findViewById(R.id.editText3);
		
		
		Button btn1=(Button)findViewById(R.id.adduser_button);
		btn1.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				JSONObject user = new JSONObject();
	            try {
					user.put("username", mUsername.getText().toString());
					user.put("password", mPassword.getText().toString());
					user.put("email", mEmail.getText().toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            
				mAuthTask = new UserLoginTask();
				mAuthTask.execute(user);
			}
			
		});
		
		Button btn2=(Button)findViewById(R.id.removeuser_button);
		btn2.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				JSONObject user = new JSONObject();
	            try {
					user.put("username", mUsername.getText().toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            
				mAuthTask = new UserLoginTask();
				mAuthTask.execute(user);
			}
			
		});
		
		
		Button btn3=(Button)findViewById(R.id.edituser_button);
		btn3.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{	            
				
			}
			
		});
		
	}
	
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<JSONObject, Void, Boolean> {
		@Override
		protected Boolean doInBackground(JSONObject... params) {
			// TODO: attempt authentication against a network service.
			boolean isSuccessful = false;
			try {
				
				int len = params[0].length();
				if(len ==3)
					isSuccessful = WebServiceUserProxy.addUser(params[0]);
				else if(len == 1)
					isSuccessful = WebServiceUserProxy.removeUser(params[0]);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				// Simulate network access.
				Thread.sleep(1);
			} catch (InterruptedException e) {
				return false;
			}

			// TODO: register the new account here.
			return isSuccessful;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;

			if (success) {
					
			} else {
				
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			//showProgress(false);
		}
	}

}
