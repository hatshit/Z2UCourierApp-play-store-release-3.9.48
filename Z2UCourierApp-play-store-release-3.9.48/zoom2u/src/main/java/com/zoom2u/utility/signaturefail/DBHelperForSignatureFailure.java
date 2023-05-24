package com.zoom2u.utility.signaturefail;

/**
 * Created by Arun on 30/3/17.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.List;


public class DBHelperForSignatureFailure {
    public static final String SIGNATURE_IMG_ID = "id";
    public static final String SIGNATURE_IMG_NAME = "signatureImgName";
    public static final String SIGNATURE_PHOTO = "photo";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "SignatureImgDB.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SIGNATURE_IMG_TABLE = "SignatureImg";

    private static final String CREATE_SIGNATURE_IMG_TABLE = "create table "
            + SIGNATURE_IMG_TABLE + " (" + SIGNATURE_IMG_ID
            + " integer primary key autoincrement," + SIGNATURE_IMG_NAME
            + " text not null unique, " + SIGNATURE_PHOTO
            + " blob not null);";

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_SIGNATURE_IMG_TABLE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + SIGNATURE_IMG_TABLE);
            onCreate(db);
        }
    }

    public void Reset() {
        mDbHelper.onUpgrade(this.mDb, 1, 1);
    }

    public DBHelperForSignatureFailure(Context ctx) {
        mCtx = ctx;
        mDbHelper = new DatabaseHelper(mCtx);
    }

    public DBHelperForSignatureFailure open() throws SQLException {
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public void insertSignatureDetails(SignatureImg_DB_Model signatureImg_DB_Model) {
        ContentValues cv = new ContentValues();
        cv.put(SIGNATURE_IMG_NAME, signatureImg_DB_Model.getBookingIdSignatureFailImg());
        cv.put(SIGNATURE_PHOTO, UtilityToStoreSignatureImageInBLOB.getBytes(signatureImg_DB_Model.getSignatureImgBitmap()));
        mDb.insert(SIGNATURE_IMG_TABLE, null, cv);
    }

    public SignatureImg_DB_Model retriveSignatureImgDetails(String bookingIdForSignature) throws SQLException {
        Cursor cur =  this.mDb.rawQuery("select * from " + SIGNATURE_IMG_TABLE + " where " + SIGNATURE_IMG_NAME + "='" + bookingIdForSignature + "'" , null);
        if (cur.moveToFirst()) {
            String signatureBookingID = cur.getString(cur.getColumnIndex(SIGNATURE_IMG_NAME));
            byte[] blob = cur.getBlob(cur.getColumnIndex(SIGNATURE_PHOTO));
            cur.close();
            return new SignatureImg_DB_Model(signatureBookingID, UtilityToStoreSignatureImageInBLOB.getPhoto(blob));
        }
        cur.close();
        return null;
    }

    //---deletes a particular title---
    public void deletePerticularItem(String name) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(SIGNATURE_IMG_TABLE, SIGNATURE_IMG_NAME + " = ?", new String[] { String.valueOf(name) });
        db.close();
    }

    public List <SignatureImg_DB_Model> getAllSignatureFromDB (){
        List <SignatureImg_DB_Model> arrayOfSignatureModel = new ArrayList<SignatureImg_DB_Model>();

     //   mDb = mDbHelper.getWritableDatabase();
        Cursor cursor = mDb.rawQuery("select * from " + SIGNATURE_IMG_TABLE, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                cursor.moveToNext();
                SignatureImg_DB_Model signatureImgDbModel = new SignatureImg_DB_Model(cursor.getString(1), BitmapFactory.decodeByteArray(cursor.getBlob(2), 0, cursor.getBlob(2).length));
                arrayOfSignatureModel.add(signatureImgDbModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return arrayOfSignatureModel;
    }
}