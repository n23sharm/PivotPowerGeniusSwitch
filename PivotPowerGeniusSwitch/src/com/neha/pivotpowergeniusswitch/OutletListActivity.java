package com.neha.pivotpowergeniusswitch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

public class OutletListActivity extends ListActivity {
	
	private Powerstrip mPowerstrip;
	private ArrayAdapter<Outlet> mOutletListAdapter;
	private ProgressDialog mProgressDialog; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setMessage(getString(R.string.outlet_updating));
		mPowerstrip = (Powerstrip) getIntent().getSerializableExtra(Constants.INTENT_DATA_OUTLET);
		mOutletListAdapter = new ArrayAdapter<Outlet>(this, android.R.layout.simple_list_item_1, mPowerstrip.getOutletList());
		setListAdapter(mOutletListAdapter);
		
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mProgressDialog.show();
				Outlet outlet = mPowerstrip.getOutletList().get(position);
				toggleOutlet(outlet.getId(), outlet.isPowered());
			}
		});
	}
	
	private void toggleOutlet(final String id, final boolean powered) {
		new AsyncTask<Void, Void, Outlet>() {
			@Override
			protected Outlet doInBackground(Void... params) {
				Outlet outlet = null;
				try {
					HttpClient client = new DefaultHttpClient();
					HttpPut outletRequest = new HttpPut(Constants.SERVER_URL + Constants.OUTLETS_REQUEST + id);
					
					JSONObject jsonObject = new JSONObject();
					jsonObject.put(Constants.PARAM_POWERED, !powered);
					
					outletRequest.setEntity(new StringEntity(jsonObject.toString(), HTTP.UTF_8));
					String accessToken = getAccessToken();
					outletRequest.addHeader(Constants.HEADER_AUTH, "Bearer " + accessToken);
					outletRequest.addHeader(Constants.HEADER_CONTENT_TYPE, "application/json");
					
					HttpResponse response = client.execute(outletRequest);

					InputStreamReader in = new InputStreamReader(response.getEntity().getContent());
					BufferedReader reader = new BufferedReader(in);
					String line;
					while ((line = reader.readLine()) != null) {
						outlet = parseOutlet(line);
					}
					
				} catch (JSONException e) {
					
				} catch (UnsupportedEncodingException e) {
				} catch (ClientProtocolException e) {
				} catch (IOException e) {
				}

				return outlet;
			}
			
			@Override
			protected void onPostExecute(Outlet result) {
				if (result != null) {
					for (int i = 0; i < mPowerstrip.getOutletList().size(); ++i) {
						if (mPowerstrip.getOutletList().get(i).getId().equals(result.getId())) {
							mPowerstrip.getOutletList().set(i, result);
						}
					}
					mOutletListAdapter.notifyDataSetChanged();
				} else {
					showErrorDialog();
				}
				mProgressDialog.dismiss();
			}
		}.execute();
	}
	
	private void showErrorDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.outlet_dialog_error_message))
		       .setCancelable(false)
		       .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	private Outlet parseOutlet(String data) {
		try {
			JSONObject json = new JSONObject(data);
			JSONObject jsonObject = json.getJSONObject(Constants.PARAM_DATA);
			
			String outletId = jsonObject.getString(Constants.PARAM_OUTLET_ID);
	    	String outletName = jsonObject.getString(Constants.PARAM_NAME);
	    	boolean powered = jsonObject.getBoolean(Constants.PARAM_POWERED);
	    	
	    	return new Outlet(outletId, outletName, powered);
		} catch (JSONException e) {
			// Do Nothing
		}
		return null;
	}
	
	private String getAccessToken() {
		SharedPreferences prefs = this.getSharedPreferences(Constants.FILE_AUTH_PREFS, Context.MODE_PRIVATE);
		return prefs.getString(Constants.PARAM_ACCESS_TOKEN, "");
	}
	
	@Override
	public void onBackPressed() {
		// Set the outlet data as a result so the previous screen
		// can update itself
		Intent data = new Intent();
		data.putExtra(Constants.EXTRA_POWERSTRIP, mPowerstrip);
		setResult(Constants.OUTLET_ACTIVITY_REQUEST_CODE, data);
		finish();
	}
}
