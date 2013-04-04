package db_local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class MbbLocalSQLiteHelper extends SQLiteOpenHelper {

	private static final String TABLE_MBB_DATA = "mbb_data";
	private static final String TAG=MbbLocalSQLiteHelper.class.getSimpleName();
	private static final String DATABASE_NAME = "mbb.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_TABLE_MBB_DATA_CREATE = "create table "
			+ TABLE_MBB_DATA 
			//			+ "(" + COLUMN_ID
			//			+ " integer primary key autoincrement, " + COLUMN_INFO_DETAILS
			//			+ " text not null);";
			+"(id INTEGER PRIMARY KEY,"
			+"info_time TEXT NOT NULL,"
			+"info_deadline_time TEXT,"
			+"teacher_id INTEGER NOT NULL,"
			+"course_id INTEGER NOT NULL,"
			+"info_type TEXT NOT NULL,"
			+"rendered TEXT NOT NULL DEFAULT 'N',"
			+"info_details TEXT NOT NULL);";

	/**
	 * Constructor 
	 * @param iContext
	 */
	public MbbLocalSQLiteHelper(Context iContext) {
		super(iContext, DATABASE_NAME, null,DATABASE_VERSION);
	}

	/**
	 * To recreate database
	 * @param - iDatabase
	 */
	@Override
	public void onCreate(SQLiteDatabase iDatabase) {
		iDatabase.execSQL(DATABASE_TABLE_MBB_DATA_CREATE);

	}

	/**
	 * Useful to drop and recreate database
	 * @param - iDatabase
	 * @param - iOldVersion
	 * @param - iNewVersion
	 */
	@Override
	public void onUpgrade(SQLiteDatabase iDatabase, int iOldVersion, int iNewVersion) {
		Log.w(TAG,"Upgrading database from version " + iOldVersion 
				+ " to "+ iNewVersion + ", which will destroy all old data");
		iDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_MBB_DATA);
		onCreate(iDatabase);
	}

}
