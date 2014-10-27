package com.gtcc.library.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import com.gtcc.library.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class UploadImageActivity extends Activity {
	InputStream inputStream;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_main);

		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.person_image_empty);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream); 
		byte[] byte_arr = stream.toByteArray();
		String image_str = Base64.encodeToString(byte_arr, Base64.DEFAULT);
		final ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("image", image_str));

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(
							"http://192.168.21.1/upload_image.php");
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = httpclient.execute(httppost);
					final String the_string_response = convertResponseToString(response);
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(UploadImageActivity.this,
									"Response " + the_string_response,
									Toast.LENGTH_LONG).show();
						}
					});

				} catch (final Exception e) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(UploadImageActivity.this,
									"ERROR " + e.getMessage(),
									Toast.LENGTH_LONG).show();
						}
					});
					System.out.println("Error in http connection "
							+ e.toString());
				}
			}
		});
		t.start();
	}

	public String convertResponseToString(HttpResponse response)
			throws IllegalStateException, IOException {

		String res = "";
		StringBuffer buffer = new StringBuffer();
		inputStream = response.getEntity().getContent();
		final int contentLength = (int) response.getEntity().getContentLength();

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(UploadImageActivity.this,
						"contentLength : " + contentLength, Toast.LENGTH_LONG)
						.show();
			}
		});

		if (contentLength < 0) {
		} else {
			byte[] data = new byte[512];
			int len = 0;
			try {
				while (-1 != (len = inputStream.read(data))) {
					buffer.append(new String(data, 0, len));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			res = buffer.toString();

			final String ret = buffer.toString();
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(UploadImageActivity.this, "Result : " + ret,
							Toast.LENGTH_LONG).show();
				}
			});
		}
		return res;
	}
}