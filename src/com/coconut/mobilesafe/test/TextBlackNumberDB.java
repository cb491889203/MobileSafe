package com.coconut.mobilesafe.test;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import com.coconut.mobilesafe.db.BlackNumberDBOpenHelper;
import com.coconut.mobilesafe.db.dao.BlackNumberDAO;

import android.database.sqlite.SQLiteDatabase;
import android.provider.SyncStateContract.Helpers;
import android.test.AndroidTestCase;
import net.tsz.afinal.utils.Utils;

public class TextBlackNumberDB extends AndroidTestCase {

	private BlackNumberDBOpenHelper helper;

	public void testIntert() {
		BlackNumberDAO dao = new BlackNumberDAO(getContext());
		Random random = new Random();
		for (int i = 0; i < 100; i++) {
			long basenumber = 13500000001l + i;
			dao.insert(String.valueOf(basenumber), String.valueOf(random.nextInt(3)));
		}

	}

	public void testFind() {
		BlackNumberDAO dao = new BlackNumberDAO(getContext());
		dao.find("110");
	}

	public void testUpdate() {
		BlackNumberDAO dao = new BlackNumberDAO(getContext());
		dao.update("110", "2");
	}

	public void testDelete() {
		BlackNumberDAO dao = new BlackNumberDAO(getContext());
		BlackNumberDBOpenHelper helper = new BlackNumberDBOpenHelper(getContext());
		SQLiteDatabase database = helper.getWritableDatabase();
		database.delete("blacknumber", "_id< ?", new String[] { "202" });

	}

	public void testString() {
		String str = "你好啊,你是jim吗? are you jim ?";
		try {
			String str2 = new String(str.getBytes(), "utf-8");
			byte[] bytes = str2.getBytes();
			for (byte b : bytes) {
				System.out.println(b);
			}
			
			System.out.println("-------------");
			
			byte[] bytes2 = str.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// char[] charArray = str.toCharArray();
		// for (char c : charArray) {
		// System.out.println((int)c);
		//
		// }
		// byte[] bytes = str.getBytes();
		// System.out.println(bytes.length+"-------------------");
		// for (byte b : bytes) {
		// System.out.println(b);
		// }
		// System.out.println(2<<23);

	}

}
