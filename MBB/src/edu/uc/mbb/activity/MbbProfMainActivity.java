package edu.uc.mbb.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import utils.StringCollectionUtil;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import common.Course;
import common.MbbCommonCode;
import common.MbbMasterDBClient;
import common.Professor;

import db_local.MbbLocalDataSource;
import domain.MbbData;
import edu.uc.mbb.R;

public class MbbProfMainActivity extends FragmentActivity{

	private static final String TAG=MbbProfMainActivity.class.getSimpleName();;
	private final MbbCommonCode _mbbCommonActivityCodeInstance=MbbCommonCode.getInstance();
	private final MbbMasterDBClient _mbbMasterDBClient=_mbbCommonActivityCodeInstance.getMbbClient();
	private MbbLocalDataSource _localDBDatasource;
	public static Professor PROFESSOR;
	private Integer _courseId;

	// 0 - 23
	private int _hourOfDay;
	// 0 - 59
	private int _minute;
	private int _year;
	// 0 - 11
	private int _month;
	private int _day;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		if(HomeActivity.INFO_TYPE.equals(MbbCommonCode.INFO_TYPE_ANNOUNCEMENT)){
			setContentView(R.layout.mbb_prof_main_activity);
		}else{
			final Calendar c = Calendar.getInstance();
			_year = c.get(Calendar.YEAR);
			_month = c.get(Calendar.MONTH);
			_day = c.get(Calendar.DAY_OF_MONTH);
			_hourOfDay=23;
			_minute=59;
			setContentView(R.layout.mbb_prof_assign_main_activity);
			// time picker
			registerListener(R.id.button3);
			// date picker
			registerListener(R.id.button4);

		}
		TextView tv=(TextView) findViewById(R.id.textView1);
		tv.append(PROFESSOR.getName());
		_localDBDatasource=new MbbLocalDataSource(getApplicationContext());
		initSpinner();
		registerListener(R.id.button2);
		registerListener(R.id.button1);
		TableLayout aTableLayout = (TableLayout) findViewById(R.id.myTable);
		_localDBDatasource.open();
		_mbbCommonActivityCodeInstance.renderMbbDataTable(MbbProfMainActivity.this,aTableLayout,_localDBDatasource);
		_localDBDatasource.close();
		Button button=(Button) findViewById(R.id.button2);
		button.setEnabled(false);
		validator();
	}

	private void validator(){
		final EditText anEditText=(EditText)findViewById(R.id.editText1);
		anEditText.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence iS, int iStart, int iBefore,int iCount) {}

			public void beforeTextChanged(CharSequence iS, int iStart, int iCount,int iAfter) {}

			public void afterTextChanged(Editable iEditable) {
				if(!StringCollectionUtil.isStringNotBlank(iEditable.toString())){
					//					anEditText.setError("Cannot Be empty");
					Button button=(Button) findViewById(R.id.button2);
					button.setEnabled(false);
				
				}else{
					Button button=(Button) findViewById(R.id.button2);
					button.setEnabled(true);
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public void onBackPressed() {
		startActivity(new Intent(this,HomeActivity.class));
	}

	private void initSpinner(){
		Spinner theSpinner=(Spinner)findViewById(R.id.spinner1);
		List<String> list = new ArrayList<String>();
		for(Course c:PROFESSOR.getCourses()){
			list.add(c.getName());
		}
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MbbProfMainActivity.this,android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		theSpinner.setAdapter(dataAdapter);
		theSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			public void onItemSelected(AdapterView<?> iArg0, View iArg1,int iPosition, long iArg3) {
				_courseId=PROFESSOR.getCourses().get(iPosition).getId();
			}

			public void onNothingSelected(AdapterView<?> iArg0) {}
		});
	}


	private void registerListener(int iButtonId){
		Button button=(Button) findViewById(iButtonId);
		switch(iButtonId){
		case R.id.button2:
			button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View iV) {
					insertAsync();
				}
			});
			break;
		case R.id.button1:
			button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View iV) {
					clearInfoDetailsBox();
				}
			});
			break;
		case R.id.button3:
			button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View iV) {
					TimePickerFragment newFragment = new TimePickerFragment(_hourOfDay,_minute);
					newFragment.show(getSupportFragmentManager(), "timePicker");
				}
			});
			break;
		case R.id.button4:
			button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View iV) {
					DatePickerFragment newFragment = new DatePickerFragment(_day,_month,_year);
					newFragment.show(getSupportFragmentManager(), "datePicker");
				}
			});
			break;
		}
	}

	private void clearInfoDetailsBox(){
		//		System.out.println(_hourOfDay+"="+_minute);
		//		System.out.println(_day+"="+_month+"="+_year);
		EditText anEditText=(EditText)findViewById(R.id.editText1);
		anEditText.setText("");
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause");
		super.onPause();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "onResume");
		super.onResume();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "onStop");
		finish();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	protected void onRestart() {
		Log.i(TAG, "onRestart");
		TableLayout table = (TableLayout) findViewById(R.id.myTable);
		table.removeAllViews();
		super.onRestart();
	}



	public void insertAsync(){
		MbbData aMbbData=new MbbData();
		EditText anEditText=(EditText)findViewById(R.id.editText1);
		String theInfoDetails=anEditText.getText().toString().trim();
		aMbbData.setInfoDetails(theInfoDetails);
		//		Log.i(TAG, "_courseId INSERT="+_courseId);
		aMbbData.setCourseId(_courseId);
		aMbbData.setTeacherId(PROFESSOR.getId());
		aMbbData.setInfoType(HomeActivity.INFO_TYPE);
		if(HomeActivity.INFO_TYPE.equals(MbbCommonCode.INFO_TYPE_ASSIGNMENT)){
			//			System.out.println(_hourOfDay+"="+_minute);
			//			System.out.println(_day+"="+_month+"="+_year);
			String aDateString=_year+"-"+(_month+1)+"-"+_day+" "+_hourOfDay+":"+_minute+":00";
			aMbbData.setInfoDeadlineTime(aDateString);
		}
		boolean isNetworkConnected=_mbbCommonActivityCodeInstance.isNetworkConnected(MbbProfMainActivity.this);
		Log.i(TAG, "isNetworkConnected="+isNetworkConnected);
		if(isNetworkConnected){
			//PostAsyncTask class that extends AsyncTask 
			new InsertPostAsyncTask().execute(aMbbData);
		}else{
			Toast.makeText(MbbProfMainActivity.this, "No Internet !", Toast.LENGTH_SHORT).show();
		}
		clearInfoDetailsBox();
	}


	/**
	 * Inserts the data to master DB, then fetches the latest data from master DB and saves in the local DB.
	 * Then renders the tableView to show the latest data
	 */
	private class InsertPostAsyncTask extends AsyncTask<MbbData, Void, Void>{

		@Override
		protected Void doInBackground(MbbData... iMbbDatas) {
			// insert into master DB
			_mbbMasterDBClient.insertData(iMbbDatas[0]);
			_localDBDatasource.open();
			_mbbCommonActivityCodeInstance.syncLocalFromMaster(_localDBDatasource);
			return null;
		}

		@Override
		protected void onPostExecute(Void iVoid) {
			TableLayout aTableLayout = (TableLayout) findViewById(R.id.myTable);
			_mbbCommonActivityCodeInstance.renderMbbDataTable(MbbProfMainActivity.this,aTableLayout,_localDBDatasource);
			_localDBDatasource.close();
			Toast.makeText(MbbProfMainActivity.this, "Posted !", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onPreExecute() {}

		@Override
		protected void onProgressUpdate(Void... values) {}

	}


	public int getHourOfDay() {
		return _hourOfDay;
	}

	public void setHourOfDay(int iHourOfDay) {
		_hourOfDay = iHourOfDay;
	}

	public int getMinute() {
		return _minute;
	}

	public void setMinute(int iMinute) {
		_minute = iMinute;
	}

	public int getYear() {
		return _year;
	}

	public void setYear(int iYear) {
		_year = iYear;
	}

	public int getMonth() {
		return _month;
	}

	public void setMonth(int iMonth) {
		_month = iMonth;
	}

	public int getDay() {
		return _day;
	}

	public void setDay(int iDay) {
		_day = iDay;
	}


}

