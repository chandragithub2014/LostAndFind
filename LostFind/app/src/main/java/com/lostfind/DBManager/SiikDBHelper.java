package com.lostfind.DBManager;

import android.content.ContentValues;
import android.database.SQLException;
import android.util.Log;

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
}
