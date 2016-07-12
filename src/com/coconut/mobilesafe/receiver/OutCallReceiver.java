package com.coconut.mobilesafe.receiver;

import com.coconut.mobilesafe.db.dao.QueryAddress;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class OutCallReceiver extends BroadcastReceiver {


	@Override
	public void onReceive(Context context, Intent intent) {
		String number = getResultData();
		String address = QueryAddress.query(number);
		Toast.makeText(context, address, 1);
	}

}
