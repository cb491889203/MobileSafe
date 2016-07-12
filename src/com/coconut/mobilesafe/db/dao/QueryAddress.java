package com.coconut.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class QueryAddress {
	private static String path = "data/data/com.coconut.mobilesafe/files/address.db";

	public static String query(String number) {
		String address = number;
        SQLiteDatabase db =SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        
        //手机号码11位，开头13x 、14x、15x、18x;
         
        //手机电话号码
        if(number.matches("^1[3458]\\d{9}$")){
              
               Cursor cursor=   db.rawQuery("select location from data2 where id = (select outkey from data1 where id = ? )",
                             new String[]{number.substring(0, 7)});
              
               if(cursor.moveToNext()){
                      address = cursor.getString(0);
               }
        }else{
               //119 、110
               switch (number.length()) {
               case 3:
                      address = "特殊号码";
                      break;
               case 4://5556
                      address = "模拟器";
                      break;
               case 5:
                      address = "客服电话";
                      break;
                     
               case 7:
                      address = "本地电话";
                      break;
                     
               case 8:
                      address = "本地电话";
                      break;

               default:
                      if(number.length()>10&& number.startsWith("0")){
                             //010 12345678
                             Cursor cursor = db.rawQuery("select location from data2 where area = ?",
                                           new String[]{number.substring(1, 3)});
                             if(cursor.moveToNext()){
                                    String location = cursor.getString(0);
                                    address = location.substring(0, location.length()-2);
                             }
                             cursor.close();
                            
                             //0855 12345678
                              cursor = db.rawQuery("select location from data2 where area = ?",
                                           new String[]{number.substring(1, 4)});
                             if(cursor.moveToNext()){
                                    String location = cursor.getString(0);
                                    address = location.substring(0, location.length()-2);
                             }
                             cursor.close();
                      }
                      break;
               }
        }
       
       
       
        return address;
	}
}
