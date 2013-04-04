package edu.uc.mbb.service;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MbbStudentAlarmScheduleReceiver extends BroadcastReceiver {
	// Restart service every REPEAT_TIME_SECS seconds
	private static final int REPEAT_TIME_SECS = 30;
	private static final String TAG = MbbStudentAlarmScheduleReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context iContext, Intent iIntent) {
		Log.i(TAG, "onReceive by BootComplete-Alarm receiver, setting broadcast repetition for MbbStartServiceReceiver");
		AlarmManager theAlarmService = (AlarmManager) iContext.getSystemService(Context.ALARM_SERVICE);
		Intent theNewIntent = new Intent(iContext, MbbStudentStartServiceReceiver.class);
		PendingIntent thePendingIntent = PendingIntent.getBroadcast(iContext, 0, theNewIntent,PendingIntent.FLAG_CANCEL_CURRENT);
		Calendar cal = Calendar.getInstance();
		// Start REPEAT_TIME_SECS seconds after boot completed
		cal.add(Calendar.SECOND, REPEAT_TIME_SECS);
		// Fetch every REPEAT_TIME_SECS seconds
		// InexactRepeating allows Android to optimize the energy consumption
		theAlarmService.setInexactRepeating(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(), 1000 * REPEAT_TIME_SECS, thePendingIntent);

	}
}
