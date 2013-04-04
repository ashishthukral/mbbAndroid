package edu.uc.mbb.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import common.MbbCommonCode;
import common.Professor;
import common.Student;

import edu.uc.mbb.R;
import edu.uc.mbb.service.MbbStudentFetchService;

public class MainActivity extends Activity {

	private static final String TAG=MainActivity.class.getSimpleName();;
	SharedPreferences _sharedPreferences = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		_sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
		autoFill();
		registerListener(R.id.button1);
		registerListener(R.id.button2);
		Intent service = new Intent(getApplicationContext(), MbbStudentFetchService.class);
		getApplicationContext().startService(service);
	}

	private void autoFill(){
		boolean isAutoFillEnabled=_sharedPreferences.getBoolean(MbbCommonCode.PREF_AUTOFILL,false);
		if(isAutoFillEnabled){
			EditText anEditText=(EditText) findViewById(R.id.editText1);
			String theSavedLoginId=((Integer)_sharedPreferences.getInt(MbbCommonCode.PREF_LOGIN_ID,0)).toString();
			anEditText.setText(theSavedLoginId);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}


	private void registerListener(int iButtonId){
		Button button=(Button) findViewById(iButtonId);
		switch(iButtonId){
		case R.id.button1:
			button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View iV) {
					EditText anEditText=(EditText) findViewById(R.id.editText1);
					anEditText.setText("");
				}
			});
			break;
		case R.id.button2:
			button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View iV) {
					boolean personIdentified=false;
					EditText anEditText=(EditText) findViewById(R.id.editText1);
					Integer theKey=Integer.valueOf(anEditText.getText().toString());
					for(Professor aProf:Professor.values()){
						if(aProf.getId().equals(theKey)){
							Toast.makeText(MainActivity.this, "Professor", Toast.LENGTH_SHORT).show();
							MbbProfMainActivity.PROFESSOR=aProf;
							MbbStudentMainActivity.STUDENT=null;
							_sharedPreferences.edit().putBoolean(MbbCommonCode.PREF_IS_STUDENT,false).commit();
							_sharedPreferences.edit().putInt(MbbCommonCode.PREF_LOGIN_ID,aProf.getId()).commit();
							personIdentified=true;
							break;
						}
					}
					if(!personIdentified){
						for(Student aStudent:Student.values()){
							if(aStudent.getId().equals(theKey)){
								Toast.makeText(MainActivity.this, "Student", Toast.LENGTH_SHORT).show();
								MbbStudentMainActivity.STUDENT=aStudent;
								MbbProfMainActivity.PROFESSOR=null;
								_sharedPreferences.edit().putBoolean(MbbCommonCode.PREF_IS_STUDENT,true).commit();
								_sharedPreferences.edit().putInt(MbbCommonCode.PREF_LOGIN_ID,aStudent.getId()).commit();
								personIdentified=true;
								break;
							}
						}
					}
					if(personIdentified){
						startActivity(new Intent(MainActivity.this,HomeActivity.class));
					}else{
						Toast.makeText(MainActivity.this,"Invalid Credentials !", Toast.LENGTH_SHORT).show();
					}
				}
			});
			break;
		}
	}

	protected void onPause() {
		//		Log.i(TAG, "onPause");
		super.onPause();
	}

	@Override
	protected void onResume() {
		//		Log.i(TAG, "onResume");
		super.onResume();
	}

	@Override
	protected void onStop() {
		//		Log.i(TAG, "onStop");
		finish();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		//		Log.i(TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	protected void onRestart() {
		//		Log.i(TAG, "onRestart");
		super.onRestart();
	}

}

