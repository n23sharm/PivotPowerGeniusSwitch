package com.neha.pivotpowergeniusswitch;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

public class PowerstripListActivity extends ListActivity {
	
	private ArrayList<Powerstrip> mPowerstripList;
	
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mPowerstripList = (ArrayList<Powerstrip>) getIntent().getSerializableExtra(Constants.INTENT_DATA_POWERSTRIP);
		ArrayAdapter<Powerstrip> powerstripAdapter = new ArrayAdapter<Powerstrip>(this, android.R.layout.simple_list_item_1, mPowerstripList);
		setListAdapter(powerstripAdapter);
		
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Powerstrip powerstrip = mPowerstripList.get(position);
				launchOutletListActivity(powerstrip);
			}
		});
	}
	
	private void launchOutletListActivity(Powerstrip powerstrip) {
		Intent intent = new Intent(this, OutletListActivity.class);
		intent.putExtra(Constants.INTENT_DATA_OUTLET, powerstrip);
		startActivityForResult(intent, Constants.OUTLET_ACTIVITY_REQUEST_CODE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == Constants.OUTLET_ACTIVITY_REQUEST_CODE) {
			Powerstrip powerstrip = (Powerstrip) data.getSerializableExtra(Constants.EXTRA_POWERSTRIP);
			for (int i = 0; i < mPowerstripList.size(); ++i) {
				if (mPowerstripList.get(i).getId().equals(powerstrip.getId())) {
					mPowerstripList.set(i, powerstrip);
				}
			}
		}
	}
}
