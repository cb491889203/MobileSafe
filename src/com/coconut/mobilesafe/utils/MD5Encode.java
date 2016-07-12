package com.coconut.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MD5Encode {
      public static String encode(String password){
    	  try {
			MessageDigest md =MessageDigest.getInstance("MD5");
			byte[] bytes = md.digest(password.getBytes());
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < bytes.length; i++) {
				int result = bytes[i] & 0xff;
				String string = Integer.toHexString(result); 
				if (string.length() ==1) {
					buffer.append("0");
				}
				buffer.append(string);
			}
			return buffer.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
     }
}
