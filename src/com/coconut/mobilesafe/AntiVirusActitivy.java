package com.coconut.mobilesafe;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.List;

import com.coconut.mobilesafe.db.dao.AntiVirusDAO;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AntiVirusActitivy extends Activity {
	private TextView tv_scanner_stats;
	private ImageView iv_scanner_pointer;
	private ProgressBar pb_scan_virus;
	private LinearLayout ll_scanner_result;
	private PackageManager pm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anti_virus);
		pm = getPackageManager();
		tv_scanner_stats = (TextView) findViewById(R.id.tv_scanner_stats);
		iv_scanner_pointer = (ImageView) findViewById(R.id.iv_scanner_pointer);
		pb_scan_virus = (ProgressBar) findViewById(R.id.pb_scan_virus);
		ll_scanner_result = (LinearLayout) findViewById(R.id.ll_scanner_result);
		RotateAnimation ra = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		ra.setDuration(1000);
		ra.setRepeatCount(Animation.INFINITE);
		iv_scanner_pointer.startAnimation(ra);
		scanVirus();

	}

	private void scanVirus() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<PackageInfo> infos = pm.getInstalledPackages(0);
				pb_scan_virus.setMax(infos.size());
				int progress = 0;
				for (final PackageInfo packageInfo : infos) {
					progress++;
					String dir = packageInfo.applicationInfo.sourceDir;
					String md5 = getFileMd5(dir);
					boolean b = AntiVirusDAO.scanVirus(md5);
					final TextView view = new TextView(getApplicationContext());
					
					if (b) {
						// 存在则是病毒
						view.setText(packageInfo.packageName + ":发现病毒");
						view.setTextColor(Color.RED);
					} else {
						// 不存在则正常
						view.setText(packageInfo.packageName + ":扫描正常");
						view.setTextColor(Color.BLACK);
					}
					pb_scan_virus.setProgress(progress);
					runOnUiThread(new Runnable() {
						public void run() {
							tv_scanner_stats.setText("正在扫描:"+packageInfo.packageName);
							tv_scanner_stats.setSingleLine();
							ll_scanner_result.addView(view, 0);

						}
					});
				}
				runOnUiThread(new Runnable() {
					public void run() {
						iv_scanner_pointer.clearAnimation();
						tv_scanner_stats.setText("扫描完成!");
					}
				});
			}
		}).start();

	}

	/**
	 * 获取文件的md5值
	 * 
	 * @param path
	 *            文件的全路径名称
	 * @return
	 */
	private String getFileMd5(String path) {
		try {
			// 获取一个文件的特征信息，签名信息。
			File file = new File(path);
			// md5
			MessageDigest digest = MessageDigest.getInstance("md5");
			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int len = -1;
			while ((len = fis.read(buffer)) != -1) {
				digest.update(buffer, 0, len);
			}
			byte[] result = digest.digest();
			StringBuffer sb = new StringBuffer();
			for (byte b : result) {
				// 与运算
				int number = b & 0xff;// 加盐
				String str = Integer.toHexString(number);
				// System.out.println(str);
				if (str.length() == 1) {
					sb.append("0");
				}
				sb.append(str);
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
