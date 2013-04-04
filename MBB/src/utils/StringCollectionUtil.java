package utils;

import java.util.Collection;

import android.util.Log;

import com.google.common.base.CaseFormat;

public class StringCollectionUtil {

	/**
	 * Converts string of form "_myString"/"myString" to "my_string"
	 * @param iSource
	 * @return String
	 */
	public static final String underCamelToUnderScore(String iSource){

		String result=null;
		if(iSource!=null && !iSource.isEmpty()){
			if(iSource.charAt(0)=='_'){
				iSource=iSource.substring(1);
			}
			result = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, iSource);
		}
		return result;
	}

	/**
	 * Converts string of form "my_string" to "_myString"
	 * @param iSource
	 * @return String
	 */
	public static final String underScoreToUnderCamel(String iSource,Boolean isVariableName){
		String result=null;
		if(iSource!=null && !iSource.isEmpty()){
			result = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, iSource);
			if(isVariableName){
				result="_"+result;
			}
		}
		return result;
	}

	/**
	 * Prints the passed Collection contents one in a line.
	 * @param icollection
	 */
	public static final void printCollectionLines(Collection<?> icollection){
		if(icollection!=null && !icollection.isEmpty()){
			for(Object o:icollection){
				System.out.println(o.toString());
			}
		}
	}


	/**
	 * Returns false if iCollection is null or is 0 size
	 * @param iCollection
	 * @return boolean
	 */
	public static final boolean collectionNotBlank(Collection<?> iCollection){
		return (iCollection!=null && !iCollection.isEmpty());
	}

	/**
	 * Converts passed Collection to passed Delimiter separated string.
	 * Returns null string if passed collection empty/null or delimiter null.
	 * Skips null values in the collection.
	 * @param iCollection
	 * @param iDelimiter - character like comma,etc
	 * @return String - delimiter separated String
	 */
	public static final String collectionToString(Collection<?> iCollection,Character iDelimiter){
		String theResult=null;
		if(collectionNotBlank(iCollection) && iDelimiter!=null){
			StringBuilder theStringBuilder=new StringBuilder();
			for(Object anObject:iCollection){
				if(anObject!=null){
					if(theStringBuilder.length()==0){
						theStringBuilder.append(anObject.toString());
					}else{
						theStringBuilder.append(iDelimiter+anObject.toString());
					}
				}
			}
			theResult=theStringBuilder.toString();
		}
		return theResult;
	}


	/**
	 * Prints the passed Collection contents one in a line for android Log info.
	 * @param iTag
	 * @param iCollection
	 */
	public static final void androidLogCollectionLines(String iTag,Collection<?> iCollection){
		if(iCollection!=null && !iCollection.isEmpty()){
			for(Object o:iCollection){
				Log.i(iTag, o.toString());
			}
		}
	}

	/**
	 * Returns false if iString is null or contains only white spaces or is 0 length
	 * @param iString
	 * @return boolean
	 */
	public static final boolean isStringNotBlank(String iString){
		return (iString!=null && iString.trim().length()!=0);
	}

}
