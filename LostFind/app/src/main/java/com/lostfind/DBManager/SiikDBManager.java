package com.lostfind.DBManager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.lostfind.application.MyApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by CHANDRASAIMOHAN on 3/10/2016.
 */
public class SiikDBManager extends SQLiteOpenHelper {

    private final static String TAG = SiikDBManager.class.getSimpleName();

    private static Context localContext;
    private static SQLiteDatabase configDB;
    private String CONFIG_DB_NAME = "siik.db";
    private boolean isConfigCreated;

    public SiikDBManager(Context context) {
        super(context,  "siik.db", null, 1);
        localContext = context;

    }
    private SiikDBManager() {

        super(localContext, "siik.db", null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public static SQLiteDatabase getConfigDB() {
        return configDB;
    }

    public void load() {
        Log.d(TAG, "SiikDBManager load()");
        createSiiKDataBase();
    }


    private void createSiiKDataBase() {
        Log.i(TAG, "method createConfigDataBase");
        boolean dbExist = isConfigDBExists();
        Log.i(TAG, "is config DB exists- " + dbExist);
        if (dbExist) {
        } else {
            this.getReadableDatabase();
            try {
                copyConfigDatabase();
            } catch (IOException e) {
                Log.e(TAG,
                        "IOException occurred in method createConfigDataBase . Error is:"
                                + e.getMessage());
                e.printStackTrace();
            }
        }
        isConfigCreated = true;
        openConfigDBConnection();
    }

    private boolean isConfigDBExists() {

		/*
		 * SQLiteDatabase checkDB = null; try { checkDB =
		 * SQLiteDatabase.openDatabase( localContext.getDatabasePath(
		 * DatabaseConstants.CONFIG_DB_NAME).toString(), null,
		 * SQLiteDatabase.OPEN_READONLY); } catch (SQLiteException e) {
		 * Log.e(TAG, "Config DB does not exist"); } if (checkDB != null) {
		 * checkDB.close(); } return checkDB != null ? true : false;
		 */

        return new File(localContext.getDatabasePath(
               CONFIG_DB_NAME).toString()).isFile();
    }


    private void copyConfigDatabase() throws IOException {
        Log.i(TAG,
                "method copyConfigDatabase, copying configDB schema from local to device");

        InputStream inputStream = localContext.getAssets().open(CONFIG_DB_NAME);
        String outFileName = localContext.getDatabasePath(CONFIG_DB_NAME).toString();
        OutputStream outputStream = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
        this.close();
    }

    private void openConfigDBConnection() {
        Log.i(TAG, "Opening Config DB Connection");
        if (configDB == null || !configDB.isOpen()) {
            configDB = SQLiteDatabase.openDatabase(localContext
                    .getDatabasePath(CONFIG_DB_NAME)
                    .toString(), null, SQLiteDatabase.OPEN_READWRITE);
            MyApplication.getInstance().setConfigDB(configDB);
        //    Toast.makeText(localContext,"DB Created",Toast.LENGTH_SHORT).show();
        }
    }

}
