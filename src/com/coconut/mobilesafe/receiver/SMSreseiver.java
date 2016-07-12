package com.coconut.mobilesafe.receiver;

import com.coconut.mobilesafe.R;
import com.coconut.mobilesafe.service.LocationService;

import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class SMSreseiver extends BroadcastReceiver {

	private SharedPreferences sp;
	public DevicePolicyManager dpm;

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {
		dpm = (DevicePolicyManager) context.getSystemService(context.DEVICE_POLICY_SERVICE);
		Object[] object = (Object[]) intent.getExtras().get("pdus");
		sp = context.getSharedPreferences("config", context.MODE_PRIVATE);
		String safeSender = sp.getString("safeNumber", null);
		for (Object object2 : object) {
			SmsMessage message = SmsMessage.createFromPdu((byte[]) object2);
			String body = message.getMessageBody();
			String sender = message.getOriginatingAddress();
			// 以上是19 以下的方法。 下面的新方法。
			/*
			 * SmsMessage[] messagesFromIntent =
			 * Telephony.Sms.Intents.getMessagesFromIntent(intent); for
			 * (SmsMessage smsMessage : messagesFromIntent) { String body =
			 * smsMessage.getMessageBody(); String sender =
			 * smsMessage.getOriginatingAddress(); if
			 * (sender.equals(context.getSharedPreferences("config",
			 * context.MODE_PRIVATE).getString("safeNumber", null))) {
			 * 
			 * } }
			 */
			Log.i("短信接收者", "正在檢查信息。。。。。。。");
			if (!TextUtils.isEmpty(safeSender)) { // 如果存在safesender 才去进行判断.

				if (sender.endsWith(safeSender)) {
					if ("#*location*#".equals(body)) {
						Log.i("短信接收者", "获取手机位置信息");
						Intent intent2 = new Intent(context, LocationService.class);
						context.startService(intent2);
						SmsManager sm = SmsManager.getDefault();
						sm.sendTextMessage(sender, null, sp.getString("lastLocation", null), null, null);
						abortBroadcast();
					} else if ("#*alarm*#".equals(body)) {
						Log.i("短信接收者", "播放报警铃声");
						MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.alarm);
						mediaPlayer.setVolume(1.0f, 1.0f);
						mediaPlayer.setLooping(false);
						mediaPlayer.start();
						abortBroadcast();
					} else if ("#*lockscreen*#".equals(body)) {
						Log.i("短信接收者", "锁定屏幕");
						ComponentName componentName = new ComponentName(context, MyDeviceReceiver.class);
						// Intent intent2 = new
						// Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
						// intent2.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
						// componentName);
						// intent2.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
						// "开启设备管理员，有很多好处！！！！！！！！！");
						// intent2.addFlags(intent2.FLAG_ACTIVITY_NEW_TASK);
						// context.startActivity(intent2);

						boolean isAdminActive = dpm.isAdminActive(componentName);

						if (!isAdminActive) {// 这一句一定要有...
							Intent intent2 = new Intent();
							// 指定动作
							intent2.setAction(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
							// 指定给那个组件授权
							intent2.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
							// 在增加最外面的那个判断safesender是否为空之前,是不需要添加下面这个Flags的.
							// 但是增加if判断之后就要添加flags.否则报错 Calling startActivity()
							// from outside of an Activity context requires the
							// FLAG_ACTIVITY_NEW_TASK flag. Is this really what
							// you want?
							//非常奇怪,. 这一点与视频中讲的不一致.
							intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //
							context.startActivity(intent2);
						}

						if (isAdminActive) {
							Toast.makeText(context, "具有权限,将进行锁屏....", 0).show();
							dpm.lockNow();
							dpm.resetPassword("", 0);
						}
						// 创建一个意图
						// dpm.lockNow();
						// dpm.resetPassword("1234", 0);

						// Intent intent2 = new Intent(context,
						// DevicePolicyManager.);

					} else if ("#*wipedata*#".equals(body)) {
						Log.i("短信接收者", "删除数据");
						dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
					} else {
						Log.i("短信接收者", "没有匹配的短信.");
					}
				}
			}
		}

	}

}
