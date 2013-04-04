package edu.uc.mbb.activity;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

	// 0 - 23
	private int _hourOfDay;
	// 0 - 59
	private int _minute;
	
	public TimePickerFragment(int iHourOfDay, int iMinute){
		_hourOfDay=iHourOfDay;
		_minute=iMinute;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, _hourOfDay, _minute,DateFormat.is24HourFormat(getActivity()));
	}

	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		MbbProfMainActivity theMbbProfMainActivity=(MbbProfMainActivity)getActivity();
		theMbbProfMainActivity.setHourOfDay(hourOfDay);
		theMbbProfMainActivity.setMinute(minute);
	}

}
