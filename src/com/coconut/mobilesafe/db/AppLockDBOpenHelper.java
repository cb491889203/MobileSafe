package com.coconut.mobilesafe.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AppLockDBOpenHelper extends SQLiteOpenHelper {

	public AppLockDBOpenHelper(Context context) {
		super(context, "applock.db", null, 1);
	}

	public AppLockDBOpenHelper(Context context, String name, CursorFactory factory, int version,
			DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(
				"create table applock (_id integer primary key autoincrement,packagename varchar(20))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
