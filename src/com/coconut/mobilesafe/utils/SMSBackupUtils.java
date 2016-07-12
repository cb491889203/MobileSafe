package com.coconut.mobilesafe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.xml.sax.Parser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

public class SMSBackupUtils {

	public interface BackupCallBack {

		/**
		 * 在备份前设置进度的最大值.
		 * 
		 * @param max
		 *            最大值
		 */
		public void beforeBackup(int max);

		/**
		 * 设置在备份过程中的进度值.
		 * 
		 * @param process
		 *            当前备份的进度值
		 */
		public void onBackup(int process);
	}

	public static void backup(Context context, BackupCallBack callback) throws Exception {

		Uri uri = Uri.parse("content://sms/");
		Cursor cursor = context.getContentResolver().query(uri, new String[] { "address", "type", "body", "date" },
				null, null, null);
		callback.beforeBackup(cursor.getCount());
		int progress = 0;
		XmlSerializer serializer = Xml.newSerializer();
		FileOutputStream fos;

		fos = new FileOutputStream(Environment.getDataDirectory() + "/backupSMS.xml");
		serializer.setOutput(fos, "utf-8");
		serializer.startDocument("utf-8", true);
		serializer.startTag(null, "smss");
		// 将短信的数量存入smss 标签中,用于还原时设置进度的最大值.
		serializer.attribute(null, "count", cursor.getCount() + "");
		String address;
		String type;
		String body;
		String date;
//		StringBuffer sb;
//		String sub;
		while (cursor.moveToNext()) {
			Thread.sleep(50);
			address = cursor.getString(0);
			type = cursor.getString(1);
			body = cursor.getString(2);
			date = cursor.getString(3);
			serializer.startTag(null, "sms");

			serializer.startTag(null, "address");
			serializer.text(address);
			serializer.endTag(null, "address");

			serializer.startTag(null, "type");
			serializer.text(type);
			serializer.endTag(null, "type");

			serializer.startTag(null, "body");
//			sb = new StringBuffer();
//			for (int i = 0; i < body.length(); i++) {
//				sub = body.substring(i, i + 1);
//
//				byte[] bytes = sub.getBytes("utf-8");
//				if ((bytes[0] & 0xF8) == 0xF0) {
//					System.out.println(sub);
//					sb.append("*");
//				} else {
//					sb.append(sub);
//				}
//			}
//
//			System.out.println("====================");
//			body = sb.toString();
			serializer.text(body);
			serializer.endTag(null, "body");

			serializer.startTag(null, "date");
			serializer.text(date);
			serializer.endTag(null, "date");

			serializer.endTag(null, "sms");

			progress++;
			// pd.setProgress(progress);
			callback.onBackup(progress);
		}
		serializer.endDocument();
		cursor.close();
		fos.close();

	}

	public interface RestoreCallBack {

		/**
		 * 还原前设置最大值,用于显示总进度
		 * 
		 * @param max
		 *            最大值
		 */
		public void beforeRestore(int max);

		/**
		 * 还原中显示当前进度值.
		 * 
		 * @param process
		 *            当前进度值
		 */
		public void onRestore(int process);
	}

	/**
	 * 还原短信. 安卓5.0后无法直接通过内容提供者还原. 后续再查资料解决.
	 * 
	 * @param context
	 *            传入一个上下文.
	 * @param callback
	 *            回调函数实例.用于还原进度设置最大值和当前进度值.
	 * @throws Exception
	 *             抛出所有的异常.
	 */
	public static void restore(Context context, RestoreCallBack callback) throws Exception {
		// new String[] { "address", "type", "body", "date" }
		Uri uri = Uri.parse("content://sms/");
		XmlPullParser parser = Xml.newPullParser();
		FileInputStream fis = new FileInputStream(new File(Environment.getDataDirectory(), "backupSMS.xml"));
		// parser.setInput(new FileReader(new
		// File(Environment.getDataDirectory(), "backupSMS.xml")));
		parser.setInput(fis, "utf-8");
		ContentValues values = null;
		int event = parser.getEventType();
		int process = 0;
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_TAG:
				if ("smss".equals(parser.getName())) {
					// 获取短信数量.并将之设置为还原进度的最大值.
					String count = parser.getAttributeValue(null, "count");
					callback.beforeRestore(Integer.valueOf(count));
				}
				if ("sms".equals(parser.getName())) {
					values = new ContentValues();
				}
				if ("address".equals(parser.getName())) {
					values.put("address", parser.getText());
				} else if ("type".equals(parser.getName())) {
					values.put("type", parser.getText());
				} else if ("body".equals(parser.getName())) {
					values.put("body", parser.getText());
				} else if ("date".equals(parser.getName())) {
					values.put("date", parser.getText());
				}
				break;
			case XmlPullParser.END_TAG:
				if ("sms".equals(parser.getName())) {
					context.getContentResolver().insert(uri, values);
					process++;
					callback.onRestore(process);
				}
			default:
				break;
			}
		}

	}
}
