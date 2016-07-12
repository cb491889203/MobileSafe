package com.coconut.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AntiVirusDAO {
	
	
	public static boolean scanVirus(String md5){
		String path = "/data/data/com.coconut.mobilesafe/files/antivirus.db";
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select * from datable where md5=?", new String []{md5});
		if (cursor.moveToNext()) {
			return true;
		}
		cursor.close();
		db.close();
		return false;
	}
}
