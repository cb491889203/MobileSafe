package com.coconut.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class BootCompleteReceiver extends BroadcastReceiver {
	private SharedPreferences sp;
	TelephonyManager tm;

	@Override
	public void onReceive(Context context, Intent intent) {
		sp = context.getSharedPreferences("config", context.MODE_PRIVATE);
		tm = (TelephonyManager) context
				.getSystemService(context.TELEPHONY_SERVICE);

		String savedSIM = sp.getString("SimSerialNumber", null);
		String nowCheckedSIM = tm.getSimSerialNumber();
		String safeNumber = sp.getString("safeNumber", null);
		if (savedSIM.equals(nowCheckedSIM)) {
			Log.i("检查SIM卡", "SIM卡一致，不用发送报警短信");
			Toast.makeText(context, "SIM卡一致，不用发送报警短信", 0).show();
		} else {
			Log.i("检查SIM卡", "SIM卡不一致，发送报警短信到安全号码");
			if (sp.getBoolean("protected", false)) {
				// 开启防盗保护了。 发送短信到安全号码
				SmsManager sm = SmsManager.getDefault();
				sm.sendTextMessage(safeNumber, null,
						"手机被盗了，亲！！！！！！！！！！！！！！！！！地点是在。。。。。。。", null, null);

			}
		}
		
		boolean showAddress = sp.getBoolean("showAddress", false);
		if (showAddress) {
			Intent intent2 = new Intent(context, com.coconut.mobilesafe.service.AddressService.class);
			context.startService(intent2);
		}
	}

}
