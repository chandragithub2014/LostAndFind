/**
 * 
 */
package com.lostfind;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * @author CHANDRASAIMOHAN
 *
 */
public class SharedPreferencesUtils {
	public static final String PREFS_NAME = "BIKE_PREFS";
	SharedPreferences sharedPreferences;
	
	public void saveIntegerPreferences(Context ctx,String key,int value){
		SharedPreferences quizPreferences;
	    Editor editor;
	    quizPreferences = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
	    editor = quizPreferences.edit(); //2
	    editor.putInt(key, value); //3
	    editor.commit(); //4
	}
	
	public int getIntegerPreferences(Context ctx,String key){
		SharedPreferences quizPreferences;
	    int preferenceValue;
	    quizPreferences = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
	    preferenceValue = quizPreferences.getInt(key, 1);
	    return preferenceValue;
	}
	
	
	public void saveStringPreferences(Context ctx,String key,String value){
		SharedPreferences quizPreferences;
	    Editor editor;
	    quizPreferences = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
	    editor = quizPreferences.edit(); //2
	    editor.putString(key, value); //3
	    editor.commit(); //4
	}
	
	public String getStringPreferences(Context ctx,String key){
		SharedPreferences quizPreferences;
	    String preferenceValue;
	    quizPreferences = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
	    preferenceValue = quizPreferences.getString(key, "");
	    return preferenceValue;
	}

	public void saveBooleanPreferences(Context ctx,String key,Boolean value){
		SharedPreferences quizPreferences;
		Editor editor;
		quizPreferences = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
		editor = quizPreferences.edit(); //2
		editor.putBoolean(key, value); //3
		editor.commit(); //4
	}

	public Boolean getBooleanPreferences(Context ctx,String key){
		SharedPreferences quizPreferences;
		Boolean preferenceValue;
		quizPreferences = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
		preferenceValue = quizPreferences.getBoolean(key, false);
		return preferenceValue;
	}
	
	public void clearSharedPreference(Context context) {
	    SharedPreferences settings;
	    Editor editor;
	 
	    settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
	    editor = settings.edit();
	 
	    editor.clear();
	    editor.commit();
	}
	
	public void removeValue(Context context,String key) {
	    SharedPreferences settings;
	    Editor editor;
	    settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
	    editor = settings.edit();
	 
	    editor.remove(key);
	    editor.commit();
	}
}
