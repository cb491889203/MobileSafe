package com.coconut.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.coconut.mobilesafe.db.AppLockDBOpenHelper;
import com.coconut.mobilesafe.db.BlackNumberDBOpenHelper;
import com.coconut.mobilesafe.domain.BlackNumberInfo;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AppLockDAO {

	private AppLockDBOpenHelper helper;
	private Context context;

	public AppLockDAO(Context context) {
		helper = new AppLockDBOpenHelper(context);
		this.context = context;
	}

	/**
	 * 查询APP是否加锁,即是否在数据库中存在
	 * 
	 * @param number
	 *           要查询的APP包名
	 * @return 如果有这个APP,就返回true,没有就返回false
	 */
	public boolean find(String packageName) {
		SQLiteDatabase database = helper.getReadableDatabase();
		Cursor cursor = database.rawQuery("select * from applock where packagename = ?", new String[] { packageName });
		boolean result = cursor.moveToNext();
		cursor.close();
		database.close();
		return result;

	}


	/**
	 * 查询所有加锁的APP
	 * 
	 * @return 将所有APP放入一个List中,并返回该List
	 */
	public List<String> findAll() {
		
		List<String> list = new ArrayList<String>();
		SQLiteDatabase database = helper.getReadableDatabase();
		Cursor cursor = database.query("applock", new String[] { "packagename"}, null, null, null, null,
				"_id desc");
		while (cursor.moveToNext()) {
			String packagename = cursor.getString(0);
			list.add(packagename);
		}
		database.close();
		cursor.close();
		return list;
	}

	
	/**
	 * 增加一个app
	 * 
	 * @return 如果增加成功,返回true,不成功就返回false
	 */
	public boolean insert(String packagename) {
		SQLiteDatabase database = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("packagename", packagename);
		long l = database.insert("applock", null, values);
		database.close();
		Intent intent = new Intent("com.coconut.mobilesafe.lockedAPPsChangfe");
		context.sendBroadcast(intent);
		if (l != -1) {
			return true;
		} else {
			return false;
		}
	}


	/**
	 * 删除一个APP锁
	 * 
	 * @param number
	 *            要删除的APP
	 * @return 删除成功返回true,不成功false.
	 */
	public boolean delete(String packagename) {
		SQLiteDatabase database = helper.getWritableDatabase();
		int l = database.delete("applock", "packagename = ?", new String[] { packagename });
		database.close();
		Intent intent = new Intent("com.coconut.mobilesafe.lockedAPPsChangfe");
		context.sendBroadcast(intent);
		if (l > 0) {
			return true;
		} else {
			return false;
		}
	}
}
