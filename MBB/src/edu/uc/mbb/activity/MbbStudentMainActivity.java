package edu.uc.mbb.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.widget.TableLayout;
import android.widget.TextView;

import common.MbbCommonCode;
import common.Student;

import db_local.MbbLocalDataSource;
import edu.uc.mbb.R;

public class MbbStudentMainActivity extends Activity {

	private static final int ANNOUNCEMENT_TABLE_REFRESH_SECS = 15*1000;
	private static final String TAG=MbbStudentMainActivity.class.getSimpleName();;

	private final MbbCommonCode _mbbCommonActivityCodeInstance=MbbCommonCode.getInstance();
	//	private MbbMasterDBClient _mbbMasterDBClient=_mbbCommonActivityCodeInstance.getMbbClient();
	private Timer _timer;
	private TimerTask _renderTask;
	//	private MbbStudentFetchService _mbbStudentFetchService;
	private MbbLocalDataSource _localDBDatasource;
	public static Student STUDENT;


	/* 
		    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
			<uses-permission android:name="android.permission.INTERNET"/>
    		<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
	 */
	//	private ServiceConnection _mBBServiceConnection = new ServiceConnection() {
	//
	//		public void onServiceConnected(ComponentName className, IBinder binder) {
	//			_mbbStudentFetchService = ((MbbStudentFetchService.MyBinder) binder).getService();
	//			Toast.makeText(MbbStudentMainActivity.this, "Connected",Toast.LENGTH_SHORT).show();
	//		}
	//
	//		public void onServiceDisconnected(ComponentName className) {
	//			_mbbStudentFetchService = null;
	//			Toast.makeText(MbbStudentMainActivity.this, "Dis-Connected",Toast.LENGTH_SHORT).show();
	//		}
	//	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mbb_student_main_activity);
		_localDBDatasource=new MbbLocalDataSource(this);
		//		doBindService();
		TextView tv=(TextView) findViewById(R.id.textView1);
		tv.append(STUDENT.getName());
		refreshTable();
	}
	
	@Override
	public void onBackPressed() {
		startActivity(new Intent(this,HomeActivity.class));
	}

	//	private void doBindService() {
	//		bindService(new Intent(this, MbbStudentFetchService.class), _mBBServiceConnection,	Context.BIND_AUTO_CREATE);
	//	}

	//	private void showServiceData() {
	//		if (_mbbStudentFetchService != null) {
	//			Toast.makeText(this, "_count=" + _mbbStudentFetchService.getCount(),	Toast.LENGTH_SHORT).show();
	//		}
	//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
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
		if(_timer!=null){
			_timer.cancel();
			_timer.purge();
		}
		finish();
		//		unbindService(_mBBServiceConnection);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy");
		if(_timer!=null){
			_timer.cancel();
			_timer.purge();
		}
		super.onDestroy();
	}

	@Override
	protected void onRestart() {
		Log.i(TAG, "onRestart");
		TableLayout table = (TableLayout) findViewById(R.id.myTable);
		table.removeAllViews();
		super.onRestart();
	}



	/**
	 * Refreshes the Table data to show new Announcements along with the already existing one.
	 * Syncs data with the local DB only, which is kept updated by the Service running in the background.
	 * Refreshes every ANNOUNCEMENT_TABLE_REFRESH_SECS 
	 */
	public void refreshTable() {
		final Handler handler = new Handler();
		if(_timer!=null){
			_timer.cancel();
			_timer.purge();
		}
		_timer = new Timer();
		_renderTask = new TimerTask() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					public void run() {
						TableLayout aTableLayout = (TableLayout) findViewById(R.id.myTable);
						_localDBDatasource.open();
						_mbbCommonActivityCodeInstance.renderMbbDataTable(MbbStudentMainActivity.this,aTableLayout,_localDBDatasource);
						_localDBDatasource.close();
					}
				});
			}
		};
		//execute in every ANNOUNCEMENT_TABLE_REFRESH_SECS sec
		_timer.schedule(_renderTask, 0,ANNOUNCEMENT_TABLE_REFRESH_SECS);
	}
}
