package edu.uc.mbb.activity;

import java.text.NumberFormat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import common.MbbCommonCode;
import common.MbbMasterDBClient;

import domain.FeedbackData;
import edu.uc.mbb.R;

public class RatingActivity extends Activity {

	private static final int NUM_STARS = 4;
	private static final String TAG=RatingActivity.class.getSimpleName();
	private final MbbCommonCode _mbbCommonActivityCodeInstance=MbbCommonCode.getInstance();
	private final MbbMasterDBClient _mbbMasterDBClient=_mbbCommonActivityCodeInstance.getMbbClient();
	private static NumberFormat NUMBERFORMAT=NumberFormat.getNumberInstance();


	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rate_activity);
		registerListener(R.id.button1);
		registerListener(R.id.button2);
		NUMBERFORMAT.setMaximumFractionDigits(2);
		for(int id:new int[]{R.id.ratingBar1,R.id.ratingBar2,R.id.ratingBar4}){
			RatingBar aRatingBar=(RatingBar)findViewById(id);
			aRatingBar.setStepSize(1F);
			aRatingBar.setNumStars(NUM_STARS);
		}
		ProgressBar aProgressBar=(ProgressBar)findViewById(R.id.progressBar1);
		aProgressBar.setMax(NUM_STARS*100);

		getRatings();
	}

	private void registerListener(int iButtonId){
		Button button=(Button) findViewById(iButtonId);
		switch(iButtonId){
		case R.id.button2:
			button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View iV) {
					boolean isNetworkConnected=_mbbCommonActivityCodeInstance.isNetworkConnected(RatingActivity.this);
					if(isNetworkConnected){
						FeedbackData fd=new FeedbackData();
						fd.setLookfeel(((RatingBar)findViewById(R.id.ratingBar4)).getRating());
						fd.setQuality(((RatingBar)findViewById(R.id.ratingBar1)).getRating());
						fd.setUsability(((RatingBar)findViewById(R.id.ratingBar2)).getRating());
						_mbbMasterDBClient.insertRatingData(fd);
						getRatings();
					}else{
						Toast.makeText(RatingActivity.this, "No Internet !", Toast.LENGTH_SHORT).show();
					}
				}
			});
			break;
		case R.id.button1:
			button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View iV) {
					for(int id:new int[]{R.id.ratingBar1,R.id.ratingBar2,R.id.ratingBar4}){
						RatingBar rb=(RatingBar)findViewById(id);
						rb.setRating(0.0F);
					}
				}
			});
			break;
		}
	}

	@Override
	public void onBackPressed() {
		startActivity(new Intent(RatingActivity.this,HomeActivity.class));
	}

	private void getRatings() {
		FeedbackData theFeedbackData=null;
		boolean isNetworkConnected=_mbbCommonActivityCodeInstance.isNetworkConnected(RatingActivity.this);
		if(isNetworkConnected){
			theFeedbackData=_mbbMasterDBClient.selectRatingData();
			if(theFeedbackData!=null){
				TextView tv=(TextView)findViewById(R.id.TextView01);
				tv.setTextColor(Color.WHITE);
				tv.setText(getString(R.string.rating_lookfeel)+"="+NUMBERFORMAT.format(theFeedbackData.getLookfeel()));
				tv=(TextView)findViewById(R.id.TextView02);
				tv.setTextColor(Color.WHITE);
				tv.setText(getString(R.string.rating_usability)+"="+NUMBERFORMAT.format(theFeedbackData.getUsability()));
				tv.setTextColor(Color.WHITE);
				tv=(TextView)findViewById(R.id.TextView03);
				tv.setText(getString(R.string.rating_quality)+"="+NUMBERFORMAT.format(theFeedbackData.getQuality()));
				tv.setTextColor(Color.WHITE);
				tv=(TextView)findViewById(R.id.textView1);
				tv.setText(getString(R.string.rating_all)+"="+NUMBERFORMAT.format(theFeedbackData.getAvg()));
				tv.setTextColor(Color.WHITE);
				ProgressBar aProgressBar=(ProgressBar)findViewById(R.id.progressBar1);
				int progressValue=(int) (theFeedbackData.getAvg()*100);
				//				System.out.println(progressValue);
				aProgressBar.setProgress(progressValue);
				aProgressBar.setBackgroundColor(Color.WHITE);
			}
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
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
