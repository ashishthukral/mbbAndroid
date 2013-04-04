package edu.uc.mbb.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

	private int _year;
	// 0 - 11
	private int _month;
	private int _day;
	
	public DatePickerFragment(int iDay,int iMonth,int iYear){
		_year=iYear;
		_month=iMonth;
		_day=iDay;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, _year, _month, _day);
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
		MbbProfMainActivity theMbbProfMainActivity=(MbbProfMainActivity)getActivity();
		theMbbProfMainActivity.setYear(year);
		theMbbProfMainActivity.setMonth(month);
		theMbbProfMainActivity.setDay(day);
	}

}
