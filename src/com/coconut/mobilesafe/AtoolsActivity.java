package com.coconut.mobilesafe;

import com.coconut.mobilesafe.utils.SMSBackupUtils;
import com.coconut.mobilesafe.utils.SMSBackupUtils.BackupCallBack;
import com.coconut.mobilesafe.utils.SMSBackupUtils.RestoreCallBack;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.View;
import android.widget.Toast;

public class AtoolsActivity extends Activity {
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
	}

	public void numberQuery(View view) {
		Intent intent = new Intent(AtoolsActivity.this, NumberAddressActivty.class);
		startActivity(intent);
	}

	public void backupSMS(View view) {
		pd = new ProgressDialog(this);
		pd.setMessage("正在备份");
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					SMSBackupUtils.backup(AtoolsActivity.this, new BackupCallBack() {

						@Override
						public void onBackup(int process) {
							pd.setProgress(process);
						}

						@Override
						public void beforeBackup(int max) {
							pd.setMax(max);
						}
					});
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(AtoolsActivity.this, "备份成功", 0).show();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(AtoolsActivity.this, "备份失败", 0).show();
						}
					});
				} finally {
					pd.dismiss();
				}

			}
		}).start();

	}

	@SuppressLint("NewApi")
	public void restoreSMS(View view) {
		final String defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(getApplicationContext());
		Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
		intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, this.getPackageName());
		startActivity(intent);
		pd = new ProgressDialog(this);
		pd.setMessage("正在还原");
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					SMSBackupUtils.restore(getApplicationContext(), new RestoreCallBack() {

						@Override
						public void onRestore(int process) {
							pd.setProgress(process);
						}

						@Override
						public void beforeRestore(int max) {
							pd.setMax(max);
						}

					});

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(AtoolsActivity.this, "还原成功", 0).show();

						}
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(AtoolsActivity.this, "还原失败", 0).show();
						}
					});
				} finally {
					pd.dismiss();
					Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
					intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, defaultSmsApp);
					startActivity(intent);
				}
			}
		}).start();

	}
}
