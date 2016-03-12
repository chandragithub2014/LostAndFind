package com.lostfind.DBManager;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;
import android.widget.Toast;

import com.lostfind.application.MyApplication;

import java.util.Calendar;

/**
 * Created by CHANDRASAIMOHAN on 3/10/2016.
 */
public class SiikDBHelper {
    private final String TAG = this.getClass().getSimpleName();

    public Integer insertSiiKData(String tableName, ContentValues newrowvalues) throws Exception {
        Log.i(TAG, "In insertSiiKData");
        long rowId = -2;
        Calendar cal;

        try {
            rowId = MyApplication.getInstance().getConfigDB().insert(tableName, null,
                    newrowvalues);
        } catch (SQLException e) {
            Log.e(TAG, "Error occurred in method insertTransData. Error is:"
                    + e.getMessage());
            throw new Exception(e.getMessage());
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Error occurred in method insertTransData. Error is:"
                    + e.getMessage());
            throw new Exception(e.getMessage());
        }

        return (int) rowId;
    }


    public Cursor getSearchResults(String category,String loc,Context ctx){
        Cursor searchCursor = null;
        try{
           Toast.makeText(ctx,"C"+category+" "+loc,Toast.LENGTH_LONG).show();
            searchCursor = MyApplication.getInstance().getConfigDB()
                    .query("lostorfound",
                            null,
                            "category = "
                                    + "'"+category+"' and location = "
                                    + "'"+loc+"'", null, null, null, null);


        }
        catch(Exception e){
            e.printStackTrace();
        }
        return searchCursor;
    }
}

