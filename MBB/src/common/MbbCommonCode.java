package common;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import utils.MySQLFieldSkip;
import utils.ReflectionUtil;
import utils.SQLiteFieldSkip;
import utils.StringCollectionUtil;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import db_local.MbbLocalDataSource;
import domain.FeedbackData;
import domain.MbbData;
import edu.uc.mbb.activity.HomeActivity;

public final class MbbCommonCode{

	private static final String TAG = MbbCommonCode.class.getSimpleName();
	private static final MbbCommonCode MBB_COMMON_SINGLETON=new MbbCommonCode();
	// get map for ALL field names n its setter methods, "InfoType"=Method "setInfoType()"
	public static final Map<String,Method> MBBDATA_ALL_RAW_VARIABLE_METHOD_MAP=ReflectionUtil.getRawFieldSetterMap(MbbData.class);
	// get map for "info_type=InfoType" fields for Master MySQL db usage
	public static final Map<String,String> MBBDATA_MASTER_DB_RAW_NAMES=ReflectionUtil.getDBRawNamesMap(MbbData.class,MySQLFieldSkip.class);
	// get map for "info_type=InfoType" fields for Local SQLite db usage
	public static final Map<String,String> MBBDATA_LOCAL_DB_RAW_NAMES=ReflectionUtil.getDBRawNamesMap(MbbData.class,SQLiteFieldSkip.class);

	public static final Map<String,Method> FEEDBACK_ALL_RAW_VARIABLE_METHOD_MAP=ReflectionUtil.getRawFieldSetterMap(FeedbackData.class);
	public static final Map<String,String> FEEDBACK_MASTER_DB_RAW_NAMES=ReflectionUtil.getDBRawNamesMap(FeedbackData.class,MySQLFieldSkip.class);
	public static final String PREF_IS_STUDENT="isStudent";
	public static final String PREF_LOGIN_ID="loginId";
	public static final String PREF_AUTOFILL="autofill";
	public static final String PREF_REFRESH_INTERVAL="refreshInterval";
	public static final String INFO_TYPE_ANNOUNCEMENT="Announcement";
	public static final String INFO_TYPE_ASSIGNMENT="Assignment";
	public static final SimpleDateFormat SDF_RENDER=new SimpleDateFormat("hh:mm a \t dd-MMM-yy");
	public static final SimpleDateFormat SDF_MYSQL=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	
	private MbbMasterDBClient _mbbMasterDBClient=new MbbMasterDBClient();

	private MbbCommonCode(){}

	public static final MbbCommonCode getInstance(){
		return MBB_COMMON_SINGLETON;
	}

	/**
	 * Checks the network state for internet connectivity.
	 * @return true if the phone is connected to internet.
	 */
	public boolean isNetworkConnected(Context iContext) {
		final ConnectivityManager conMgr = (ConnectivityManager) iContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
		return (activeNetwork != null && activeNetwork.getState() == NetworkInfo.State.CONNECTED);
	}

	/**
	 * Renders the tableLayout passed with fresh data from the local DB.
	 * @param iContext - Activity class
	 * @param iTableLayout - tableLayout component has to be loaded n passed explicity from Activity calling it
	 * @param iNewMbbDataList
	 */
	public void renderMbbDataTable(Context iContext,TableLayout iTableLayout,MbbLocalDataSource iMbbLocalDataSource){
		// get all the rows from local DB to render on Table view
		List<MbbData> theMbbDataList=iMbbLocalDataSource.getAllMbbDataRows();
		if(StringCollectionUtil.collectionNotBlank(theMbbDataList)){
			Log.i(TAG, "Local DB row count="+theMbbDataList.size());
			_mbbMasterDBClient.processSelectData(theMbbDataList);
			LayoutParams lp=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);

			iTableLayout.removeAllViews();
			iTableLayout.setPadding(5, 5, 0, 5);
			iTableLayout.setShrinkAllColumns(true);
			iTableLayout.setStretchAllColumns(true);
			List<Integer> theNewIds=new ArrayList<Integer>();
			for(MbbData aRow:theMbbDataList){
				if(HomeActivity.INFO_TYPE.equals(MbbCommonCode.INFO_TYPE_ASSIGNMENT) && !aRow.getInfoType().equals(HomeActivity.INFO_TYPE)){
					continue;
				}else if(HomeActivity.INFO_TYPE.equals(MbbCommonCode.INFO_TYPE_ANNOUNCEMENT) && !aRow.getInfoType().equals(HomeActivity.INFO_TYPE)){
					continue;
				}
				TableRow row=new TableRow(iContext);
				row.setLayoutParams(lp);
				TextView col=new TextView(iContext);

				col.setLayoutParams(lp);
				col.setText(SDF_RENDER.format(aRow.getInfoTimeDate()));
				col.setTextColor(Color.WHITE);
				//				col.setText(aRow.getId()+"--->"+sdf.format(aRow.getInfoTimeDate()));
				row.setPadding(0, 15, 0, 0);
				row.addView(col);
				if(aRow.getRendered()=='N'){
					theNewIds.add(aRow.getId());
					row.setBackgroundColor(Color.LTGRAY);
					aRow.setRendered('Y');
				}
				iTableLayout.addView(row);

				row=new TableRow(iContext);
				row.setLayoutParams(lp);
				col=new TextView(iContext);
				col.setLayoutParams(lp);
				// in setText(var) - if var is Integer it thinks that you are trying to set the text with
				// a String resource with ID = the value of var. So always use toString() 
				col.setText("Professor="+Professor.fetchNameForId(aRow.getTeacherId()));
					col.setTextColor(Color.WHITE);
				row.addView(col);
				col=new TextView(iContext);
				col.setLayoutParams(lp);
				col.setText("Course="+Course.fetchNameForId(aRow.getCourseId()));
					col.setTextColor(Color.WHITE);
				row.addView(col);
				iTableLayout.addView(row);

				if(HomeActivity.INFO_TYPE.equals(MbbCommonCode.INFO_TYPE_ASSIGNMENT)){
					col=new TextView(iContext);
					col.setLayoutParams(lp);
					try {
						Date aDate=MbbCommonCode.SDF_MYSQL.parse(aRow.getInfoDeadlineTime());
						col.setText("Deadline="+MbbCommonCode.SDF_RENDER.format(aDate));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					col.setTextColor(Color.RED);
					iTableLayout.addView(col);
				}

				col=new TextView(iContext);
				col.setLayoutParams(lp);
				col.setText("Info="+aRow.getInfoDetails());
					col.setTextColor(Color.WHITE);
				iTableLayout.addView(col);
			}
			iMbbLocalDataSource.markReadMbbDataRows(theNewIds);

		}else{
			Log.e(TAG, "iMbbDataList null or empty !!!");
		}
	}


	/**
	 * Fetches latest data from Master DB which is after the current lastest info_time in the local DB.
	 * Then updates the local DB with the new rows fetched.
	 * @param iLocalDBDatasource
	 */
	public List<MbbData> syncLocalFromMaster(MbbLocalDataSource iLocalDBDatasource) {
		// get latest info_time from local DB
		String theLatestInfoTime=iLocalDBDatasource.getLatestMbbDataInfoTime();
		// use this info_time to get the latest data inserted from master DB
		MbbData theMbbData=new MbbData();
		if(StringCollectionUtil.isStringNotBlank(theLatestInfoTime)){
			theMbbData.setInfoTime(theLatestInfoTime);
		}else{
			// if local DB has no row ie theLatestInfoTime==null then set some very early date to get all the rows in master DB
			theMbbData.setInfoTime("2000-02-03 00:00:00");
		}
		//		theMbbData.setInfoType("Announcement");
		// get all the Announcement MbbData rows from master which are not present in the local DB
		List<MbbData> theMbbDataList=_mbbMasterDBClient.selectData(theMbbData);
		// insert all these rows from master DB in local DB
		if(StringCollectionUtil.collectionNotBlank(theMbbDataList)){
			Log.i(TAG, "Inserting new rows into Local="+theMbbDataList.size());
			for(MbbData aRow:theMbbDataList){
				iLocalDBDatasource.insertMbbDataRow(aRow);
			}
		}else{
			Log.i(TAG, "No new records from Master !!!");
		}
		return theMbbDataList;
	}


	public MbbMasterDBClient getMbbClient() {
		return _mbbMasterDBClient;
	}


}
