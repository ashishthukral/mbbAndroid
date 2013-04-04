package db_local;

import java.util.ArrayList;
import java.util.List;

import utils.ReflectionUtil;
import utils.StringCollectionUtil;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import common.Course;
import common.MbbCommonCode;

import domain.MbbData;
import edu.uc.mbb.activity.MbbProfMainActivity;
import edu.uc.mbb.activity.MbbStudentMainActivity;

public class MbbLocalDataSource {

	private static final String TAG=MbbLocalDataSource.class.getSimpleName();
	private static final String TABLE_MBB_DATA = "mbb_data";
	// get all DB column names
	private static final String[] ALL_COLUMNS=new String[MbbCommonCode.MBBDATA_LOCAL_DB_RAW_NAMES.keySet().size()];

	// Database fields
	private SQLiteDatabase _sqliteDatabase;
	private MbbLocalSQLiteHelper _mySQLiteHelper;

	// static block to initialize ALL_COLUMNS with db column names
	static{
		int i=0;
		for(String aKey:MbbCommonCode.MBBDATA_LOCAL_DB_RAW_NAMES.keySet()){
			ALL_COLUMNS[i++]=aKey;
		}
	}

	/**
	 * Constructor to get MbbDataSource instance
	 * @param iContext
	 */
	public MbbLocalDataSource(Context iContext) {
		_mySQLiteHelper = new MbbLocalSQLiteHelper(iContext);
	}

	/**
	 * Used to open the database for read/write access and initializes _sqliteDatabase by using _mySQLiteHelper
	 * @throws SQLException
	 */
	public void open() throws SQLException {
		_sqliteDatabase = _mySQLiteHelper.getWritableDatabase();
		// Un-comment below to drop and recreate DB tables
		//		_mySQLiteHelper.onUpgrade(_sqliteDatabase, 1, 2);
	}

	/**
	 * Used to close the database.
	 */
	public void close() {
		_mySQLiteHelper.close();
	}

	/**
	 * Insert MbbData row in local DB
	 * @param iMbbData
	 */
	public void insertMbbDataRow(MbbData iMbbData) {
		// get set of updatable ContentValues by using reflection. gives set of all not null values in iMbbData 
		ContentValues theContentValues = ReflectionUtil.getUpdatableContentValues(MbbCommonCode.MBBDATA_LOCAL_DB_RAW_NAMES, iMbbData);
		long insertId = _sqliteDatabase.insert(TABLE_MBB_DATA, null,theContentValues);
		Log.i(TAG, "MbbData inserted with id: " + insertId);
	}

	/**
	 *  Delete MbbData row in local DB based on it's 'id'
	 * @param iMbbData
	 */
	public void deleteMbbDataRow(MbbData iMbbData) {
		long id = iMbbData.getId();
		int rows=_sqliteDatabase.delete(TABLE_MBB_DATA, "id = " + id, null);
		Log.i(TAG, (rows>0?"Successfully---":"Failed---")+"MbbData deleted with id: " + id);
	}

	/**
	 *  Update MbbData row in local DB based on it's not null values using reflection based on it's 'id'
	 * @param iMbbData
	 */
	public void updateMbbDataRow(MbbData iMbbData) {
		// get set of updatable ContentValues by using reflection. gives set of all not null values in iMbbData 
		ContentValues theContentValues = ReflectionUtil.getUpdatableContentValues(MbbCommonCode.MBBDATA_LOCAL_DB_RAW_NAMES, iMbbData);
		// 		int rows=_sqliteDatabase.update(TABLE_MBB_DATA, theContentValues, "id="+iMbbData.getId(),null);
		int rows=_sqliteDatabase.update(TABLE_MBB_DATA, theContentValues, "id=?", new String[]{iMbbData.getId().toString()});
		Log.i(TAG, "updated rows: " + rows);
	}

	/**
	 * Sets the rendered flag to 'Y' for the values shown to the user by the app.
	 * @param iNewIds - List of Ids of MbbData rows to be updated.
	 */
	public void markReadMbbDataRows(List<Integer> iNewIds) {
		if(StringCollectionUtil.collectionNotBlank(iNewIds)){
			if(iNewIds.size()<1000){
				ContentValues theContentValues =new ContentValues();
				theContentValues.put("rendered", "Y");
				int rows=_sqliteDatabase.update(TABLE_MBB_DATA, theContentValues, "id IN ("+StringCollectionUtil.collectionToString(iNewIds,',')+")",null);
				Log.i(TAG, "updated rows: " + rows);
			}else{
				Log.e(TAG, "Passed rows list sized should be less than 1000 due to IN clause limitation, passed list size=" + iNewIds.size());
			}
		}
	}

	/**
	 * Gets All MbbData Rows. Gets all data and converts it into a list of MbbData objects
	 * @return List<MbbData>
	 */
	public List<MbbData> getAllMbbDataRows() {
		List<MbbData> theMbbDataRows = null;
		String theWhereClause="";
		if(MbbProfMainActivity.PROFESSOR!=null){
			theWhereClause="teacher_id="+MbbProfMainActivity.PROFESSOR.getId();
		}else if(MbbStudentMainActivity.STUDENT!=null){
			List<Integer> theCourseIds=new ArrayList<Integer>();
			for(Course aCourse:MbbStudentMainActivity.STUDENT.getCourses()){
				theCourseIds.add(aCourse.getId());
			}
			theWhereClause="course_id IN ("+StringCollectionUtil.collectionToString(theCourseIds,',')+")";
		}
		Cursor cursor = _sqliteDatabase.query(TABLE_MBB_DATA,ALL_COLUMNS,theWhereClause ,null, null, null, "id DESC");
		if(cursor.moveToFirst()){
			theMbbDataRows = new ArrayList<MbbData>();
			while (!cursor.isAfterLast()) {
				MbbData theMbbDataRow = cursorToMbbData(cursor);
				theMbbDataRows.add(theMbbDataRow);
				cursor.moveToNext();
			}
			// Make sure to close the cursor
			cursor.close();
		}
		return theMbbDataRows;
	}

	/**
	 * Return latest info_time details of the max 'id' row present
	 * @return String
	 */
	public String getLatestMbbDataInfoTime() {
		String theInfoTime=null;
		Cursor aCursor = _sqliteDatabase.query(TABLE_MBB_DATA,new String[]{"info_time"},null,null, null, null, "id DESC","1");
		if(aCursor.moveToFirst()){
			theInfoTime=aCursor.getString(0);
			// Make sure to close the cursor
			aCursor.close();
		}
		return theInfoTime;
	}

	/**
	 * Converts a cursor data row to MbbData object using reflection
	 * @param iCursor
	 * @return MbbData
	 */
	private MbbData cursorToMbbData(Cursor iCursor){
		MbbData theMbbData=new MbbData();
		for(int i=0;i<iCursor.getColumnCount();i++){
			// cursor.getColumnName(i) is of db col name - "info_type"
			// we get the raw variable name for the db col from the map and assign the cursor string value to it using Reflection
			ReflectionUtil.setValueField(MbbCommonCode.MBBDATA_LOCAL_DB_RAW_NAMES.get(iCursor.getColumnName(i)), iCursor.getString(i), theMbbData,MbbCommonCode.MBBDATA_ALL_RAW_VARIABLE_METHOD_MAP);
		}
		return theMbbData;
	}
}
