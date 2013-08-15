package com.gtcc.library.oauth2;

import com.sina.sso.RemoteSSO;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

public class SsoHandler {
	private ServiceConnection conn;
	private static final int DEFAULT_AUTH_ACTIVITY_CODE = 32973;

	private int mAuthActivityCode;

	private static String ssoPackageName;
	private static String ssoActivityName;

	private OAuth2Provider mAuthProvider;
	private AuthListener mAuthDialogListener;
	private OAuth2AccessToken mAccessToken;
	private Activity mAuthActivity;

	public SsoHandler(Activity activity, OAuth2Provider authProvider) {
		mAuthActivity = activity;
		mAuthProvider = authProvider;

		conn = new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
				// mWeibo.startAuthDialog(mAuthActivity, mAuthDialogListener);
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				RemoteSSO remoteSSOservice = RemoteSSO.Stub
						.asInterface(service);
				try {
					ssoPackageName = remoteSSOservice.getPackageName();
					ssoActivityName = remoteSSOservice.getActivityName();
					boolean singleSignOnStarted = startSingleSignOn(
							mAuthActivity, mAuthProvider.getApiKey(),
							new String[] {}, mAuthActivityCode);
					if (!singleSignOnStarted) {
						// mWeibo.startAuthDialog(mAuthActivity,
						// mAuthDialogListener);
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}

			}
		};
	}
	
	public void authorize(final AuthListener listener) {
		authorize(DEFAULT_AUTH_ACTIVITY_CODE, listener);
	}

	private void authorize(int activityCode, final AuthListener listener) {
		mAuthActivityCode = activityCode;

		boolean bindSucced = false;
		mAuthDialogListener = listener;

		bindSucced = bindRemoteSSOService(mAuthActivity);
		if (!bindSucced) {
			// if(mWeibo!=null){
			// mWeibo.startAuthDialog(mAuthActivity, mAuthDialogListener);
			// }
		}

	}

	private boolean bindRemoteSSOService(Activity activity) {
		Context context = activity.getApplicationContext();
		Intent intent = new Intent(mAuthProvider.getRemoteSerive());
		return context.bindService(intent, conn, Context.BIND_AUTO_CREATE);
	}

	private boolean startSingleSignOn(Activity activity, String applicationId,
			String[] permissions, int activityCode) {
		boolean didSucceed = true;
		Intent intent = new Intent();
		intent.setClassName(ssoPackageName, ssoActivityName);
		intent.putExtra("appKey", applicationId);
		intent.putExtra("redirectUri", mAuthProvider.getRedirectUrl());

		if (permissions.length > 0) {
			intent.putExtra("scope", TextUtils.join(",", permissions));
		}

		if (!validateAppSignatureForIntent(activity, intent)) {
			return false;
		}

		try {
			activity.startActivityForResult(intent, activityCode);
		} catch (ActivityNotFoundException e) {
			didSucceed = false;
		}

		activity.getApplication().unbindService(conn);
		return didSucceed;
	}

	private boolean validateAppSignatureForIntent(Activity activity,
			Intent intent) {
		ResolveInfo resolveInfo = activity.getPackageManager().resolveActivity(
				intent, 0);
		if (resolveInfo == null) {
			return false;
		}

		String packageName = resolveInfo.activityInfo.packageName;
		try {
			PackageInfo packageInfo = activity.getPackageManager()
					.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
			for (Signature signature : packageInfo.signatures) {
				if (mAuthProvider.getSignature().equals(signature.toCharsString())) {
					return true;
				}
			}
		} catch (NameNotFoundException e) {
			return false;
		}

		return false;
	}

	public void authorizeCallBack(int requestCode, int resultCode, Intent data) {
		if (requestCode == mAuthActivityCode) {

			// Successfully redirected.
			if (resultCode == Activity.RESULT_OK) {

				// Check OAuth 2.0/2.10 error code.
				String error = data.getStringExtra("error");
				if (error == null) {
					error = data.getStringExtra("error_type");
				}

				// error occurred.
				if (error != null) {
					if (error.equals("access_denied")
							|| error.equals("OAuthAccessDeniedException")) {
						Log.d("Weibo-authorize", "Login canceled by user.");
						mAuthDialogListener.onCancel();
					} else {
						String description = data
								.getStringExtra("error_description");
						if (description != null) {
							error = error + ":" + description;
						}
						Log.d("Weibo-authorize", "Login failed: " + error);
						mAuthDialogListener.onError(new AuthException(error,
								resultCode, description));
					}

					// No errors.
				} else {
					if (null == mAccessToken) {
						mAccessToken = new OAuth2AccessToken();
					}
					mAccessToken.setAccessToken(data
							.getStringExtra(OAuth2AccessToken.KEY_TOKEN));
					mAccessToken.setExpiresIn(data
							.getStringExtra(OAuth2AccessToken.KEY_EXPIRES));
					mAccessToken
							.setRefreshToken(data
									.getStringExtra(OAuth2AccessToken.KEY_REFRESH_TOKEN));
					if (mAccessToken.isSessionValid()) {
						Log.d("Weibo-authorize",
								"Login Success! access_token="
										+ mAccessToken.getAccessToken() + " expires="
										+ mAccessToken.getExpiresIn()
										+ "refresh_token="
										+ mAccessToken.getRefreshToken());
						mAuthDialogListener.onComplete(data.getExtras());
					} else {
						Log.d("Weibo-authorize",
								"Failed to receive access token by SSO");
//						mWeibo.startAuthDialog(mAuthActivity,
//								mAuthDialogListener);
					}
				}

				// An error occurred before we could be redirected.
			} else if (resultCode == Activity.RESULT_CANCELED) {
				if (data != null) {
					Log.d("Weibo-authorize",
							"Login failed: " + data.getStringExtra("error"));
					mAuthDialogListener.onError(new AuthException(data
							.getStringExtra("error"), data.getIntExtra(
							"error_code", -1), data
							.getStringExtra("failing_url")));

					// User pressed the 'back' button.
				} else {
					Log.d("Weibo-authorize", "Login canceled by user.");
					mAuthDialogListener.onCancel();
				}
			}
		}
	}

}
