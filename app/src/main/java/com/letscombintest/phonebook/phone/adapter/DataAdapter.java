package com.letscombintest.phonebook.phone.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.letscombintest.phonebook.phone.DataBaseHelper;

import java.io.IOException;

/**
 * Created by konamgil on 2017-05-10.
 */

public class DataAdapter
{
    protected static final String TAG = "DataAdapter";

    private final Context mContext;
    private DataBaseHelper mDbHelper;

    private SQLiteDatabase mDb;
    public DataAdapter(Context context)
    {
        this.mContext = context;
        mDbHelper = new DataBaseHelper(mContext);
    }

    public DataAdapter createDatabase() throws SQLException
    {
        try
        {
            mDbHelper.createDataBase();
        }
        catch (IOException mIOException)
        {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public DataAdapter open() throws SQLException {
        try {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        }
        catch (SQLException mSQLException) {
            Log.e(TAG, "open >>"+ mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public Cursor getTestData() {
        try {
            String sql ="SELECT display_name FROM raw_contacts";

            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur!=null) {
                mCur.moveToNext();
            }
            return mCur;
        }
        catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

    public Cursor getNameAndPhone() {
        try {
            String sql = "SELECT display_name, normalized_number FROM raw_contacts INNER JOIN phone_lookup ON _id=raw_contact_id";
            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur != null) {
                mCur.moveToNext();
            }
            return mCur;
        } catch (SQLException mSQLException){
            Log.e(TAG, "getNameAndPhone >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

    public Cursor insertPhoneList(){
        try {
            String sql = "";
            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur != null) {
                mCur.moveToNext();
            }
            return mCur;
        }catch (SQLException mSQLException){
            Log.e(TAG, "insertPhoneList >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }
}