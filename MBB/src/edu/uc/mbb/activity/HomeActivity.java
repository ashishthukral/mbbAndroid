package edu.uc.mbb.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import common.MbbCommonCode;

import edu.uc.mbb.R;

public class HomeActivity extends Activity {

	private static final String TAG=HomeActivity.class.getSimpleName();;
	public static String INFO_TYPE;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_activity);
		// announcements
		registerListener(R.id.button2);
		// rating
		registerListener(R.id.button1);
		// assignments
		registerListener(R.id.button3);
	}

	@Override
	public void onBackPressed() {
		startActivity(new Intent(HomeActivity.this,MainActivity.class));
	}


	private void registerListener(int iButtonId){
		Button button=(Button) findViewById(iButtonId);
		switch(iButtonId){
		case R.id.button2:
			button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View iV) {
					INFO_TYPE=MbbCommonCode.INFO_TYPE_ANNOUNCEMENT;
					if(MbbProfMainActivity.PROFESSOR!=null){
						startActivity(new Intent(HomeActivity.this,MbbProfMainActivity.class));
					}else if(MbbStudentMainActivity.STUDENT!=null){
						startActivity(new Intent(HomeActivity.this,MbbStudentMainActivity.class));
					}
				}
			});
			break;
		case R.id.button3:
			button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View iV) {
					INFO_TYPE=MbbCommonCode.INFO_TYPE_ASSIGNMENT;
					if(MbbProfMainActivity.PROFESSOR!=null){
						startActivity(new Intent(HomeActivity.this,MbbProfMainActivity.class));
					}else if(MbbStudentMainActivity.STUDENT!=null){
						startActivity(new Intent(HomeActivity.this,MbbStudentMainActivity.class));
					}
				}
			});
			break;
		case R.id.button1:
			button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View iV) {
					startActivity(new Intent(HomeActivity.this,RatingActivity.class));
				}
			});
			break;
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Intent settingsActivity = new Intent(getBaseContext(),MyPreferences.class);
		startActivity(settingsActivity);			
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {
		finish();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

}
