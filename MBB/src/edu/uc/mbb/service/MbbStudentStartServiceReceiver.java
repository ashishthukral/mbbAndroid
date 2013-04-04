package edu.uc.mbb.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MbbStudentStartServiceReceiver extends BroadcastReceiver {
	private static final String TAG = "MbbStartServiceReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "onReceive sending startService command to LocalMbbService");
		Intent service = new Intent(context, MbbStudentFetchService.class);
		context.startService(service);
	}
} 