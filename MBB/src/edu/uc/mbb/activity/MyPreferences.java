package edu.uc.mbb.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.util.Log;
import edu.uc.mbb.R;
import edu.uc.mbb.service.MbbStudentFetchService;


public class MyPreferences extends PreferenceActivity {

	private static final String TAG=MyPreferences.class.getSimpleName();
	private SharedPreferences _sharedPreferences = null;
	private int refreshInterval;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		addPreferencesFromResource(R.xml.preferences);
		if(MbbProfMainActivity.PROFESSOR!=null){
			EditTextPreference theEditTextPreference = (EditTextPreference) findPreference("refreshInterval");
			PreferenceCategory thePreferenceCategory = (PreferenceCategory) findPreference("mbbPreferenceCategory");
			thePreferenceCategory.removePreference(theEditTextPreference);
		}
	}
	@Override
	protected void onStart() {
		super.onStart();
		// save current state into data member(s) for comparison later
		refreshInterval=Integer.valueOf(_sharedPreferences.getString("refreshInterval", "15"));
	}

	@Override
	public void onBackPressed() {
		startActivity(new Intent(MyPreferences.this,HomeActivity.class));
		finish();
	}

	@Override
	protected void onStop() {
		super.onStop();
		int newRefreshInterval=Integer.valueOf(_sharedPreferences.getString("refreshInterval", "15"));
		if (refreshInterval !=newRefreshInterval) {
			Log.i(TAG, "refreshInterval changed from "+refreshInterval+" to "+newRefreshInterval);
			// changed notifcation status.
			Intent service = new Intent(getApplicationContext(), MbbStudentFetchService.class);
			getApplicationContext().stopService(service);
			getApplicationContext().startService(service);
		} 
	}

}
