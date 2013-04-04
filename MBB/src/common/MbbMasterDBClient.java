package common;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import utils.HttpUtil;
import utils.ReflectionUtil;
import utils.StringCollectionUtil;
import android.util.Log;
import domain.FeedbackData;
import domain.MbbData;

public class MbbMasterDBClient{

	private static final String AUTH_ERROR = "AUTH_ERROR";
	private static final String COL_PAIR_SEPARATOR = "=";
	// escape the col_separator '||' to use in string split
	private static final String COL_SEPARATOR = "\\|\\|";
	private static final String ROW_SEPARATOR = "ROW_BREAK";
	private static final String SELECT_RES_STOP = "SELECT_RES_STOP";
	private static final String SELECT_RES_START = "SELECT_RES_START";

	// if not assigned any value, then can be assigned a value by a 'static' block even though final
	private static final String HTTP_PHP_URL;
	private static final String TAG=MbbMasterDBClient.class.getSimpleName();

	// to be used for Prof app
	private static final String APP_SECURE_KEY="666666";

	static {
		//		// if running in Emulator, then set localhost URL
		//		if(	Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")){
		//			/* Use "http://10.0.2.2/index.php" instead of "10.0.2.2/index.php" to prevent below exception
		//			 * java.lang.IllegalStateException: Target host must not be null, or set in parameters.
		//			 */
		//			// Also use 10.0.2.2 to hit the host device's 127.0.0.1 (localhost)
		//			HTTP_PHP_URL = "http://10.0.2.2/index.php";
		//		}else{
		//			// if running in real device, then set web URL
		//			HTTP_PHP_URL = "http://mbb.comyr.com/index.php";
		//		}
		HTTP_PHP_URL = "http://mbb.comyr.com/index.php";
	}



	public void testInsertData(){
		MbbData theMbbData=new MbbData();
		theMbbData.setTeacherId(6);
		theMbbData.setCourseId(77);
		theMbbData.setInfoType("123 testing info type");
		theMbbData.setInfoDetails("test info");
		insertData(theMbbData);
	}

	public void testUpdateData(){
		MbbData theMbbData=new MbbData();
		theMbbData.setId(49);
		theMbbData.setInfoDetails("6676 new info details 123");
		theMbbData.setInfoType("new info type");
		updateData(theMbbData);
	}

	public void testRetireData(){
		MbbData theMbbData=new MbbData();
		theMbbData.setId(55);
		retireData(theMbbData);
	}



	/**
	 * General method to POST a request and adds the APP_SECURE_KEY.
	 * @param iNameValuePairs
	 * @return StringBuilder - null if no response returned or AUTH_ERROR faced
	 */
	private StringBuilder postRequest(List<NameValuePair> iNameValuePairs){
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(HTTP_PHP_URL);
		StringBuilder theStringBuilder=null;
		try {
			iNameValuePairs.add(new BasicNameValuePair("APP_SECURE_KEY", APP_SECURE_KEY));
			httppost.setEntity(new UrlEncodedFormEntity(iNameValuePairs));
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			theStringBuilder=HttpUtil.inputStreamToString(response.getEntity().getContent());
			if(theStringBuilder.indexOf(AUTH_ERROR)!=-1){
				System.out.println(AUTH_ERROR);
				theStringBuilder=null;
			}else{
				//				System.out.println(iNameValuePairs);
				//				System.out.println(theStringBuilder.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return theStringBuilder;
	}


	/**
	 * Inserts data passed in the bean. id and infoTime values are automatically added by DB, so should not be manually sent.
	 * @param iMbbData
	 */
	public void insertData(MbbData iMbbData){
		List<NameValuePair> theNameValuePairs= ReflectionUtil.getNameValuePairs(MbbCommonCode.MBBDATA_MASTER_DB_RAW_NAMES,iMbbData);
		theNameValuePairs.add(new BasicNameValuePair("query_type", "insert"));
		postRequest(theNameValuePairs);
	}


	public void insertRatingData(FeedbackData iFeedbackData){
		List<NameValuePair> theNameValuePairs= ReflectionUtil.getNameValuePairs(MbbCommonCode.FEEDBACK_MASTER_DB_RAW_NAMES,iFeedbackData);
		theNameValuePairs.add(new BasicNameValuePair("query_type", "insert_rating"));
		postRequest(theNameValuePairs);
	}

	public FeedbackData selectRatingData(){
		FeedbackData theFeedbackData=null;
		List<NameValuePair> theNameValuePairs=new ArrayList<NameValuePair>();
		theNameValuePairs.add(new BasicNameValuePair("query_type", "select_rating"));
		StringBuilder theStringBuilder=postRequest(theNameValuePairs);
		if(theStringBuilder!=null && theStringBuilder.length()>0){
			//			System.out.println(theStringBuilder);
			String theSelectRawColumns[]=theStringBuilder.toString().split(COL_SEPARATOR);
			if(StringCollectionUtil.isStringNotBlank(theSelectRawColumns[0])){
				theFeedbackData=new FeedbackData();
				theFeedbackData.setUsability(Float.valueOf(theSelectRawColumns[0]));
				theFeedbackData.setQuality(Float.valueOf(theSelectRawColumns[1]));
				theFeedbackData.setLookfeel(Float.valueOf(theSelectRawColumns[2]));
			}
		}
		return theFeedbackData;
	}


	/**
	 * Updates the row data depending on the not-null values set in the passed iMbbData bean except the id passed in the bean
	 * @param iMbbData
	 */
	public void updateData(MbbData iMbbData){
		List<NameValuePair> theNameValuePairs= ReflectionUtil.getNameValuePairs(MbbCommonCode.MBBDATA_MASTER_DB_RAW_NAMES,iMbbData);
		List<String> theFields= ReflectionUtil.getUpdatableFields(MbbCommonCode.MBBDATA_MASTER_DB_RAW_NAMES,iMbbData);
		String theFieldString=theFields.toString();
		// convert a list to a comma separated string
		theFieldString=theFieldString.substring(1,theFieldString.length()-1).replaceAll(", ", ",");
		//			System.out.println(theFieldString);
		theNameValuePairs.add(new BasicNameValuePair("query_type", "update"));
		theNameValuePairs.add(new BasicNameValuePair("update_fields", theFieldString));
		postRequest(theNameValuePairs);
	}



	/**
	 * Retires the data row to perform a soft delete for the passed id in the bean
	 * @param iMbbData
	 */
	public void retireData(MbbData iMbbData){
		List<NameValuePair> theNameValuePairs= ReflectionUtil.getNameValuePairs(MbbCommonCode.MBBDATA_MASTER_DB_RAW_NAMES,iMbbData);
		theNameValuePairs.add(new BasicNameValuePair("query_type", "retire"));
		postRequest(theNameValuePairs);
	}


	/**
	 * Fetches retired='N' rows
	 * @param iMbbData
	 */
	public List<MbbData> selectData(MbbData iMbbData){
		List<MbbData> theSelectedRows=null;
		if(StringCollectionUtil.isStringNotBlank(iMbbData.getInfoTime())){
			List<NameValuePair> theNameValuePairs= ReflectionUtil.getNameValuePairs(MbbCommonCode.MBBDATA_MASTER_DB_RAW_NAMES,iMbbData);
			theNameValuePairs.add(new BasicNameValuePair("query_type", "select"));
			StringBuilder theStringBuilder=postRequest(theNameValuePairs);
			theSelectedRows=null;
			if(theStringBuilder!=null && theStringBuilder.length()>0){
				//				System.out.println(theStringBuilder);
				theSelectedRows=filterSelectData(theStringBuilder);
				processSelectData(theSelectedRows);
				Log.i(TAG, "Selected rows count="+theSelectedRows.size());
				//StringUtil.printCollectionLines(theSelectedRows);
				StringCollectionUtil.androidLogCollectionLines(TAG,theSelectedRows);
			}
		}else{
			Log.e(TAG, "SelectData method requires non-blank values for infoTime");
		}
		return theSelectedRows;
	}

	/**
	 * Converts the textual response to a list of MbbData beans.
	 * @param iResponseStringBuilder
	 * @return List<MbbData>
	 */
	private List<MbbData> filterSelectData(StringBuilder iResponseStringBuilder){
		List<MbbData> theMbbDataRows=new ArrayList<MbbData>();
		// pull out the rows information contained between the SELECT_RES_START n SELECT_RES_STOP tags
		String theSelectRawData=iResponseStringBuilder.substring(iResponseStringBuilder.indexOf(SELECT_RES_START)+SELECT_RES_START.length(), iResponseStringBuilder.indexOf(SELECT_RES_STOP));
		// get different rows of data
		String theSelectRawRows[]=theSelectRawData.split(ROW_SEPARATOR);
		for(String aSelectRawRow:theSelectRawRows){
			// even if the response is empty, it will contain 1 string of 0 length, so safe if condition put
			if(StringCollectionUtil.isStringNotBlank(aSelectRawRow)){
				MbbData theMbbData=new MbbData();
				// get column pairs out of the row data
				String theSelectRawColumns[]=aSelectRawRow.split(COL_SEPARATOR);
				for(String aSelectColumn:theSelectRawColumns){
					// split the column info pair to field-value type
					String theColumnPair[]=aSelectColumn.split(COL_PAIR_SEPARATOR);
					// theColumnPair[0] is of php var type - "info_type"
					// we get the accessor name from the map for the php var and assign the value to it using Reflection
					// to handle empty column values from php server response
					if(theColumnPair.length==2 && StringCollectionUtil.isStringNotBlank(theColumnPair[1])){
						ReflectionUtil.setValueField(MbbCommonCode.MBBDATA_MASTER_DB_RAW_NAMES.get(theColumnPair[0]), theColumnPair[1], theMbbData, MbbCommonCode.MBBDATA_ALL_RAW_VARIABLE_METHOD_MAP);
					}
				}
				theMbbDataRows.add(theMbbData);
			}
		}
		return theMbbDataRows;
	}

	/**
	 * Converts info_time timestamp info to infoTimeDate java Date object and update the list
	 * @param iSelectedRows
	 */
	public void processSelectData(List<MbbData> iSelectedRows){
		if(StringCollectionUtil.collectionNotBlank(iSelectedRows)){
			try {
				for(MbbData aRow:iSelectedRows){
					//					System.out.println(aRow);
					aRow.setInfoTimeDate(MbbCommonCode.SDF_MYSQL.parse(aRow.getInfoTime()));
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}



}
