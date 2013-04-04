package edu.uc.mbb.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import utils.StringCollectionUtil;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import common.Course;
import common.MbbCommonCode;
import common.Student;

import db_local.MbbLocalDataSource;
import domain.MbbData;
import edu.uc.mbb.R;
import edu.uc.mbb.activity.MainActivity;

public class MbbStudentFetchService extends Service {
	private static final String TAG=MbbStudentFetchService.class.getSimpleName();

	private final IBinder _mBinder = new MyBinder();
	private Timer _timer;
	private final MbbCommonCode _mbbCommonActivityCodeInstance=MbbCommonCode.getInstance();
	private MbbLocalDataSource _localDBDatasource;
	SharedPreferences _sharedPreferences = null;

	/**
	 * This method called whenever unstarted service is bound by bindService() method by the flag BIND_AUTO_CREATE OR 
	 * alarm manager receiver starts the service.
	 * NOTE: the app has to be started first time to allow the service to run or the device has to be rebooted for
	 * alarm manager receiver to kick-in and auto-start the service on BOOT COMPLETE
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		Toast.makeText(this,"Service created ...", Toast.LENGTH_SHORT).show();
		Log.i(TAG, "onCreate");
		_sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MbbStudentFetchService.this);

		fetchRenderAsync();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "onStartCommand");
		return Service.START_NOT_STICKY;
	}

	/**
	 * Fetch new records from Master DB if any, add to the local DB, and create Notification for it.
	 * Runs async every FETCH_RENDER_TIME_INTERVAL secs
	 */
	public void fetchRenderAsync() {
		final Handler handler = new Handler();
		if(_timer!=null){
			_timer.cancel();
			_timer.purge();
		}
		_timer = new Timer();
		TimerTask _doAsynchronousTask = new TimerTask() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					public void run() {
						boolean isStudent=_sharedPreferences.getBoolean(MbbCommonCode.PREF_IS_STUDENT, false);
						Log.i(TAG, "isStudent="+isStudent);
						if(isStudent){
							//PostAsyncTask class that extends AsyncTask 
							new FetchRenderAsyncTask().execute();
						}
					}
				});
			}
		};
		Integer theRefreshInterval=Integer.valueOf(_sharedPreferences.getString(MbbCommonCode.PREF_REFRESH_INTERVAL, "15"));
		Log.i(TAG, "theRefreshInterval="+theRefreshInterval);
		//execute in every theRefreshInterval secs
		_timer.schedule(_doAsynchronousTask, 0,theRefreshInterval*1000);
	}

	/**
	 * Fetches the latest data from master DB and saves in the local DB.
	 * Then renders the tableView to show the latest data
	 */
	private class FetchRenderAsyncTask extends AsyncTask<String, Void, List<MbbData>>{
		@Override
		protected List<MbbData> doInBackground(String... iParams) {
			_localDBDatasource=new MbbLocalDataSource(getApplicationContext());
			_localDBDatasource.open();
			List<MbbData> theListMbbData=_mbbCommonActivityCodeInstance.syncLocalFromMaster(_localDBDatasource);
			_localDBDatasource.close();
			return theListMbbData;
		}

		@Override
		protected void onPostExecute(List<MbbData> iNewListMbbData) {
			// create notification if new records fetched
			// create notifications only acc to the courses taken up by the student
			if(StringCollectionUtil.collectionNotBlank(iNewListMbbData)){
				int theStudentId=_sharedPreferences.getInt(MbbCommonCode.PREF_LOGIN_ID, -1);
				int countAnno=0,countAssign=0;
				Student theStudent=null;
				for(Student aStudent:Student.values()){
					if(aStudent.getId().equals(theStudentId)){
						theStudent=aStudent;
						break;
					}
				}
				if(theStudent!=null){
					List<Integer> theCourseIds=new ArrayList<Integer>();
					for(Course aCourse:theStudent.getCourses()){
						theCourseIds.add(aCourse.getId());
					}
					for(MbbData aMbbData:iNewListMbbData){
						if(theCourseIds.contains(aMbbData.getCourseId())){
							if(aMbbData.getInfoType().equals(MbbCommonCode.INFO_TYPE_ASSIGNMENT)){
								countAssign++;
							}else if(aMbbData.getInfoType().equals(MbbCommonCode.INFO_TYPE_ANNOUNCEMENT)){
								countAnno++;
							}
						}
					}
					if(countAnno>0 || countAssign>0){
						createNotification(countAnno,countAssign);
					}
				}
			}
		}

		@Override
		protected void onPreExecute() {}

		@Override
		protected void onProgressUpdate(Void... values) {}
	}

	/**
	 * Creates notification for Announcements whenever new records fetched from Master DB.
	 * @param iNewRecords - no of new announcements records
	 */
	private void createNotification(int iCountAnno,int iCountAssign){
		Log.i(TAG, "createNotification");
		// new Intent/app which shows up on clicking of the notification
		Intent newIntent = new Intent(MbbStudentFetchService.this, MainActivity.class);
		newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
		// The ongoing intent which starts the newIntent
		PendingIntent pendingIntent = PendingIntent.getActivity(MbbStudentFetchService.this, 0, newIntent, 0);
		// Build notification
		Notification theNotification = new NotificationCompat.Builder(MbbStudentFetchService.this)
		.setContentTitle((iCountAnno+iCountAssign)+" New Updates !!!")
		.setContentText(iCountAnno+" "+MbbCommonCode.INFO_TYPE_ANNOUNCEMENT+"s, "+iCountAssign+" "+MbbCommonCode.INFO_TYPE_ASSIGNMENT+"s")
		.setSmallIcon(R.drawable.ic_launcher)
		.setAutoCancel(true)
		.setTicker(iCountAnno+" New "+MbbCommonCode.INFO_TYPE_ANNOUNCEMENT+"s, "+iCountAssign+" New "+MbbCommonCode.INFO_TYPE_ASSIGNMENT+"s !!!")
		.setContentIntent(pendingIntent).build();
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(0, theNotification);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(_timer!=null){
			_timer.cancel();
			_timer.purge();
		}
		Toast.makeText(this, "Service destroyed ...", Toast.LENGTH_SHORT).show();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return _mBinder;
	}

	public class MyBinder extends Binder {
		public MbbStudentFetchService getService() {
			return MbbStudentFetchService.this;
		}
	}

}