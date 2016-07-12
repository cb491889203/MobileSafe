package com.coconut.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.coconut.mobilesafe.db.BlackNumberDBOpenHelper;
import com.coconut.mobilesafe.domain.BlackNumberInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BlackNumberDAO {

	private BlackNumberDBOpenHelper helper;

	public BlackNumberDAO(Context context) {
		helper = new BlackNumberDBOpenHelper(context);
	}

	/**
	 * 查询黑名单号码
	 * 
	 * @param number
	 *            要查询的号码
	 * @return 如果有这个黑名单号码,就返回true,没有就返回false
	 */
	public boolean find(String number) {
		SQLiteDatabase database = helper.getReadableDatabase();
		Cursor cursor = database.rawQuery("select * from blacknumber where number = ?", new String[] { number });
		boolean result = cursor.moveToNext();
		cursor.close();
		database.close();
		return result;

	}

	/**
	 * 根据黑名单号码查找模式
	 * 
	 * @param number
	 *            要查找的号码
	 * @return 返回查找的哦到的模式, 如果没有找到,就返回null.
	 */
	public String findMode(String number) {
		SQLiteDatabase database = helper.getReadableDatabase();
		String result = null;
		Cursor cursor = database.rawQuery("select mode from blacknumber where number = ?", new String[] { number });
		if (cursor.moveToNext()) {
			result = cursor.getString(0);
			cursor.close();
			database.close();
		} else
			cursor.close();
		database.close();
		return result;
	}

	/**
	 * 查询所有黑名单
	 * 
	 * @return 将所有黑名单放入一个List中,并返回该List
	 */
	public List<BlackNumberInfo> findAll() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();
		SQLiteDatabase database = helper.getReadableDatabase();
		Cursor cursor = database.query("blacknumber", new String[] { "number", "mode" }, null, null, null, null,
				"_id desc");
		BlackNumberInfo info;
		while (cursor.moveToNext()) {
			String number = cursor.getString(0);
			String mode = cursor.getString(1);
			info = new BlackNumberInfo();
			info.setBlackNumber(number);
			info.setMode(mode);
			list.add(info);
		}
		database.close();
		cursor.close();
		return list;
	}

	/**根据给出的数量查找部分数据
	 * @param offset 从哪个位置开始查找.
	 * @param maxNumber 查找多少个数据.
	 * @return 将查找的数据存入list中,并返回该list.
	 */
	public List<BlackNumberInfo> findPart(int offset ,int maxNumber) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();
		SQLiteDatabase database = helper.getReadableDatabase();
		Cursor cursor = database.rawQuery("select number , mode from blacknumber order by _id desc limit ? offset ? ", new String[]{ String.valueOf(maxNumber),String.valueOf(offset)});
		BlackNumberInfo info;
		while (cursor.moveToNext()) {
			String number = cursor.getString(0);
			String mode = cursor.getString(1);
			info = new BlackNumberInfo();
			info.setBlackNumber(number);
			info.setMode(mode);
			list.add(info);
		}
		database.close();
		cursor.close();
		return list;
	}

	
	/**
	 * 增加一个黑名单号码
	 * 
	 * @param numebr
	 *            要拦截的号码
	 * @param mode
	 *            拦截模式 1 电话拦截 , 2 短信拦截 , 3全部拦截
	 * @return 如果增加成功,返回true,不成功就返回false
	 */
	public boolean insert(String number, String mode) {
		SQLiteDatabase database = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("number", number);
		values.put("mode", mode);
		long l = database.insert("blacknumber", null, values);
		database.close();
		if (l != -1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 修改更新黑名单号码
	 * 
	 * @param numebr
	 *            要修改的号码
	 * @param newNumebr
	 *            修改后的新号码
	 * @return 修改成功返回true,不成功返回false
	 */
	public boolean update(String number, String newMode) {
		SQLiteDatabase database = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", newMode);
		int l = database.update("blacknumber", values, "number = ?", new String[] { number });
		database.close();
		if (l > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 删除黑名单号码
	 * 
	 * @param number
	 *            要删除的号码
	 * @return 删除成功返回true,不成功false.
	 */
	public boolean delete(String number) {
		SQLiteDatabase database = helper.getWritableDatabase();
		int l = database.delete("blacknumber", "number = ?", new String[] { number });
		database.close();
		if (l > 0) {
			return true;
		} else {
			return false;
		}
	}
}
