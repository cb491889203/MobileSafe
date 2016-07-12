package com.coconut.mobilesafe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class SelectContactsActivity extends Activity {
	private ListView lv_contacts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_contacts);
		lv_contacts = (ListView) findViewById(R.id.lv_contacts);
		final List<Map<String, String>> contactsList = getContactInfo();

		lv_contacts.setAdapter(new SimpleAdapter(this, contactsList,
				R.layout.listview_contacts, new String[] { "name", "number" },
				new int[] { R.id.tv_name, R.id.tv_number }));

		lv_contacts.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			String string = contactsList.get(position).get("number");

				Intent intent = new Intent(SelectContactsActivity.this,
						Setup3Activity.class);
				intent.putExtra("safeNumber", string);
				setResult(0, intent);
				finish();
			}

		});
	}
	
//private List<Map<String, String>> getContactInfo() {
//		
//		//把所有的联系人
//		List<Map<String, String>> list  = new ArrayList<Map<String,String>>();
//
//		// 得到一个内容解析器
//		ContentResolver resolver = getContentResolver();
//		// raw_contacts uri
//		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
//		Uri uriData = Uri.parse("content://com.android.contacts/data");
//
//		Cursor cursor = resolver.query(uri, new String[] { "contact_id" },
//				null, null, null);
//
//		while (cursor.moveToNext()) {
//			String contact_id = cursor.getString(0);
//			
//			if (contact_id != null) {
//				//具体的某一个联系人
//				Map<String, String> map = new HashMap<String, String>();
//				
//				Cursor dataCursor = resolver.query(uriData, new String[] {
//						"data1", "mimetype" }, "contact_id=?",
//						new String[] { contact_id }, null);
//				
//				while (dataCursor.moveToNext()) {
//					String data1 = dataCursor.getString(0);
//					String mimetype = dataCursor.getString(1);
//					System.out.println("data1=="+data1+"==mimetype=="+mimetype);
//					
//					if("vnd.android.cursor.item/name".equals(mimetype)){
//						//联系人的姓名
//						map.put("name", data1);
//					}else if("vnd.android.cursor.item/phone_v2".equals(mimetype)){
//						//联系人的电话号码
//						map.put("phone", data1);
//					}
//					
//				}
//				
//				
//				list.add(map);
//				dataCursor.close();
//
//			}
//
//		}
//
//		cursor.close();
//		return list;
//	}

	private List<Map<String, String>> getContactInfo() {
		List<Map<String, String>> contactsList = new ArrayList<Map<String, String>>();
		ContentResolver resolver = getContentResolver();

		Uri IdUri = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri dataUri = Uri.parse("content://com.android.contacts/data");
		Cursor cursor = resolver.query(IdUri, new String[] { "contact_id" },
				null, null, null);

		Cursor cursor2;
		Map<String, String> map = null;
		String i;
		String data;
		while (cursor.moveToNext()) {
			String id = cursor.getString(0);
			map = new HashMap<String, String>();

			if (id != null) {
				cursor2 = resolver.query(dataUri, new String[] { "mimetype",
						"data1" }, "contact_id = ?", new String[] { id }, null);

				while (cursor2.moveToNext()) {
					
					i = cursor2.getString(0);
					data = cursor2.getString(1);
					System.out.println("data1==" + data + "==mimetype==" + i);

					if ("vnd.android.cursor.item/name".equals(i)) {
						map.put("name", data);
					} else if ("vnd.android.cursor.item/phone_v2".equals(i)) {
						map.put("number", data);
					}

				}
				contactsList.add(map);
				cursor2.close();
			}
		}

		cursor.close();
		return contactsList;
	}
}
