package com.neha.pivotpowergeniusswitch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

public class MainActivity extends ActionBarActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initalize();
	}
	
	private void initalize() {
		new AsyncTask<Void, Void, ArrayList<Powerstrip>>() {
			@Override
			protected ArrayList<Powerstrip> doInBackground(Void... params) {
				ArrayList<Powerstrip> powerstripList = new ArrayList<Powerstrip>();
				try {
					HttpClient client = new DefaultHttpClient();
					HttpPost authRequest = new HttpPost(Constants.SERVER_URL + Constants.AUTH_REQUEST);
					
					JSONObject jsonObject = new JSONObject();
					jsonObject.put(Constants.PARAM_CLIENT_ID, "a436b2284fc0722ab6b4c968b47f896f");
					jsonObject.put(Constants.PARAM_CLIENT_SECRET, "0a3c42456c58d29d3adadd73e74a6dbe");
					jsonObject.put(Constants.PARAM_USERNAME, "izzy+pvp@winkapp.com");
					jsonObject.put(Constants.PARAM_PASSWORD, "izzy");
					jsonObject.put(Constants.PARAM_GRANT_TYPE, "password");

					authRequest.setEntity(new StringEntity(jsonObject.toString(), HTTP.UTF_8));
					authRequest.addHeader(Constants.HEADER_CONTENT_TYPE, "application/json");
					
					HttpResponse response = client.execute(authRequest);

					InputStreamReader in = new InputStreamReader(response.getEntity().getContent());
					BufferedReader reader = new BufferedReader(in);
					
					String line;
					String accessToken = "";
					while ((line = reader.readLine()) != null) {
						// Parse out the bearer token
						accessToken = getAccessTokenFromResponse(line);
					}
					in.close();
					
					HttpGet userRequest = new HttpGet(Constants.SERVER_URL + Constants.DEVICES_REQUEST);
					userRequest.addHeader(Constants.HEADER_AUTH, "Bearer " + accessToken);
					response = client.execute(userRequest);
					in = new InputStreamReader(response.getEntity().getContent());
					reader = new BufferedReader(in);
					
					while ((line = reader.readLine()) != null) {
						powerstripList = getPowerstripList(line);
					}
					
				} catch (ClientProtocolException e) {
				} catch (IOException e) {
				} catch (JSONException e) {
				}
				
				return powerstripList;
			}
			
			@Override
			protected void onPostExecute(ArrayList<Powerstrip> result) {
				if (result != null) {
					launchPowerstripActivity(result);
				} else {
					showErrorDialog();
				}
			}
		}.execute();
	}
	
	private void showErrorDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.initialize_dialog_error_message))
			.setCancelable(false)
			.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
			})
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					initalize();
					dialog.dismiss();
				}
			});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	private void launchPowerstripActivity(ArrayList<Powerstrip> data) {
		Intent intent = new Intent(this, PowerstripListActivity.class);
		intent.putExtra(Constants.INTENT_DATA_POWERSTRIP, data);
		startActivity(intent);
		finish();
	}
	
	private ArrayList<Powerstrip> getPowerstripList(String data) {
		ArrayList<Powerstrip> powerstripList = new ArrayList<Powerstrip>();
		try {
			JSONObject jsonObject = new JSONObject(data);
			JSONArray dataArray = jsonObject.getJSONArray(Constants.PARAM_DATA);
			
			for (int i = 0 ; i < dataArray.length() ; ++i) {
			    String powerStripId = dataArray.getJSONObject(i).getString(Constants.PARAM_POWERSTRIP_ID);
			    String powerStripName = dataArray.getJSONObject(i).getString(Constants.PARAM_NAME);
			    ArrayList<Outlet> outletList = new ArrayList<Outlet>();
			    
			    JSONArray outletArray = dataArray.getJSONObject(i).getJSONArray(Constants.PARAM_OUTLETS);
			    for (int j = 0; j < outletArray.length(); ++j) {
			    	String outletId = outletArray.getJSONObject(j).getString(Constants.PARAM_OUTLET_ID);
			    	String outletName = outletArray.getJSONObject(j).getString(Constants.PARAM_NAME);
			    	boolean powered = outletArray.getJSONObject(j).getBoolean(Constants.PARAM_POWERED);
			    	Outlet outlet = new Outlet(outletId, outletName, powered);
			    	outletList.add(outlet);
			    }
			    Powerstrip powerstrip = new Powerstrip(powerStripId, powerStripName, outletList);
			    powerstripList.add(powerstrip);
			}
			
		} catch (JSONException e) {
			// Do Nothing
		}
		return powerstripList;
	}
	
	private String getAccessTokenFromResponse(String data) {
		try {
			JSONObject jsonObject = new JSONObject(data);
			String accessToken = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
			
			SharedPreferences prefs = this.getSharedPreferences(Constants.FILE_AUTH_PREFS, Context.MODE_PRIVATE);
			prefs.edit().putString(Constants.PARAM_ACCESS_TOKEN, accessToken).commit();
			
			return accessToken;
		} catch (JSONException e) {
			return null;
		}
	}
}
