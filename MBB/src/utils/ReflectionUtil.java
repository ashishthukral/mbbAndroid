package utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.ContentValues;

import com.google.common.base.CaseFormat;

public class ReflectionUtil {

	/**
	 * Returns a Map of the form - "info_type","InfoType" for a pojo field "_infoType". Converts the passed 
	 * Class variables to _ separated variable names to be used in php scripts/db column names. Prepares the map for above 
	 * conversion for all variables.
	 * Skips the fields marked with the passed annotation.
	 * Saves the pojo field name by stripping of the leading _ and capitalizing the first char so that
	 * it can be used easily for forming getter/setter names.
	 * @param iClass
	 * @param iSkipAnnotatedClass
	 * @return Map<String,String> - bean_variable=BeanVariable
	 */
	public static final Map<String,String> getDBRawNamesMap(Class<?> iClass,Class<? extends Annotation> iSkipAnnotatedClass){
		Map<String,String> theMap=new HashMap<String,String>();
		Field fields[]=iClass.getDeclaredFields();
		for(Field aField:fields){
			// if Field marked with the Skip annotation then don't add it
			Annotation aFieldSkip=aField.getAnnotation(iSkipAnnotatedClass);
			if(aFieldSkip!=null){
				continue;
			}
			String rawVariableName=aField.getName().substring(1);
			rawVariableName=CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, rawVariableName);
			theMap.put(StringCollectionUtil.underCamelToUnderScore(aField.getName()),rawVariableName );
		}
		//				System.out.println(theMap);
		return theMap;
	}


	/**
	 * Returns a List of NameValuePair to be used in POST request with php_var_name=BeanValue pairs. 
	 * @param iMap - Mapping of bean_variable=BeanVariable type
	 * @param iBean - the bean containing the values to be mapped
	 * @return List<NameValuePair>
	 */
	public static final List<NameValuePair> getNameValuePairs(Map<String,String> iMap,Object iBean){
		List<NameValuePair> theNameValuePairs=new ArrayList<NameValuePair>();
		for(Entry<String,String> anEntry:iMap.entrySet()){
			String theValue=getValueField(anEntry.getValue(),iBean);
			theNameValuePairs.add(new BasicNameValuePair(anEntry.getKey(),theValue));
		}
		return theNameValuePairs;
	}


	/**
	 * Gets a map containing raw variable names and their Reflection based setter Method.
	 * eg - "InfoType"="setInfoType(...)"
	 * @param iClass
	 * @return Map<String,Method>
	 */
	public static final Map<String,Method> getRawFieldSetterMap(Class<?> iClass){
		Method methods[]=iClass.getMethods();
		Map<String,Method> theMap=new HashMap<String,Method>();
		for(Method theMethod:methods){
			if(theMethod.getName().startsWith("set")){
				String theRawVariableName=theMethod.getName().substring(3);
				theMap.put(theRawVariableName,theMethod);
			}
		}
		//		System.out.println(theMap);
		return theMap;
	}

	/**
	 * Receives a field name like - "InfoType" and calls "getInfoType" for it and returns the value.
	 * @param iRawFieldName
	 * @param iBean
	 * @return String - null or not-null fieldValue
	 */
	public static final String getValueField(String iRawFieldName,Object iBean){
		String theValue=null;
		try {
			Method theMethod = iBean.getClass().getMethod("get"+iRawFieldName);
			Object object=theMethod.invoke(iBean, (Object[])null);
			if(object!=null){
				theValue=object.toString();
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return theValue;
	}

	/**
	 * Receives a raw field name like - "InfoType" and calls "setInfoType" for the passed bean.
	 * @param iRawFieldName - "InfoType"
	 * @param iParamValue
	 * @param iBean
	 * @param iRawVariableMethodMap
	 */
	public static final void setValueField(String iRawFieldName,String iParamValue,Object iBean,Map<String,Method> iRawVariableMethodMap){
		try {
			Method theMethod = iRawVariableMethodMap.get(iRawFieldName);
			Class<?> theParameter[]=theMethod.getParameterTypes();
			Object value=null;
			if(iParamValue==null || iParamValue.trim().length()==0 ){
				iParamValue=null;
			}
			if(iParamValue!=null){
				if(theParameter[0]==String.class){
					value= iParamValue;
				}else if(Number.class.isAssignableFrom(theParameter[0]) || theParameter[0]==Boolean.class){
					Method valueOfMethod=theParameter[0].getMethod("valueOf", String.class);
					value=valueOfMethod.invoke(null, iParamValue);
				}else if(theParameter[0]==Character.class){
					value=iParamValue.charAt(0);
				}else {
					throw new IllegalAccessException("Conversion not supported, theParameter Class="+theParameter[0]);
				}
			}
			theMethod.invoke(iBean, value);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Returns the list of php_var_names for all not-null value fields in the bean passed. Skips the "id" field as 
	 * that is normally used only in the WHERE clause not in the SET clause of UPDATE query.
	 * @param iMap - Mapping of bean_variable=_beanVariable type
	 * @param iBean - the bean containing the values to be mapped
	 * @return List<String>
	 */
	public static final List<String> getUpdatableFields(Map<String,String> iMap,Object iBean){
		List<String> theUpdatableFields=new ArrayList<String>();
		for(Entry<String,String> anEntry:iMap.entrySet()){
			String theKey=anEntry.getKey();
			if(!theKey.equals("id")){
				String theValue=getValueField(anEntry.getValue(),iBean);
				if(theValue!=null){
					theUpdatableFields.add(anEntry.getKey());
				}
			}
		}
		return theUpdatableFields;
	}


	/**
	 * Creates and returns the ContentValues for all not-null value fields in the bean passed.
	 * @param iMap - Mapping of bean_variable=BeanVariable type
	 * @param iBean - the bean containing the values to be mapped
	 * @return ContentValues
	 */
	public static final ContentValues getUpdatableContentValues(Map<String,String> iMap,Object iBean){
		ContentValues theContentValues = new ContentValues();
		for(Entry<String,String> anEntry:iMap.entrySet()){
			String theKey=anEntry.getKey();
			String theValue=getValueField(anEntry.getValue(),iBean);
			if(theValue!=null){
				theContentValues.put(theKey, theValue);
			}
		}
		return theContentValues;
	}


}
