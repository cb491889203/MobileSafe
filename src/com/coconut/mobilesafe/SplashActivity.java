package com.coconut.mobilesafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.coconut.mobilesafe.utils.StreamTool;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;


/**
 * @author Administrator
 * 
 */
public class SplashActivity extends Activity {
	protected static final int ENTER_HOME = 0;
	protected static final int SHOW_UPDATE_DIALOG = 1;
	protected static final int URL_ERROR = 2;
	protected static final int IO_ERROR = 3;
	protected static final int JSON_ERROR = 4;
	private static final String TAG = "SplashActivity";
	TextView tv_vision;
	private String desc;
	private String apkurl;
	private String updateName;
	private TextView tv_download;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		tv_vision = (TextView) findViewById(R.id.tv_version);
		tv_vision.setText("版本：" + getVersionName());
		tv_download = (TextView) findViewById(R.id.tv_download);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean b = sp.getBoolean("update", false);
		if (b) {
			checkVersion();
		} else {
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					enterHome();
				}
			}, 1000);
		}

		AlphaAnimation aaAlphaAnimation = new AlphaAnimation(0.3f, 1.0f);
		aaAlphaAnimation.setDuration(2000);
		findViewById(R.id.rl_root).setAnimation(aaAlphaAnimation);
		loadDatabase("address.db");
		loadDatabase("antivirus.db");
		installShortcut();
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// 处理消息
			switch (msg.what) {
			case ENTER_HOME:
				// 进入主页
				Log.i("主线程", "马上进入主页！！！！！！！！！！！！！！！！");
				enterHome();
				break;
			case SHOW_UPDATE_DIALOG:
				Log.i("主线程", "马上打开对话框！！！！！！！！！！！！！！！！");
				showUpdateDialog();
				break;
			case URL_ERROR:
				// 进入主页
				enterHome();
				Toast.makeText(getApplicationContext(), "URL错误", 0).show();
				break;
			case IO_ERROR:
				// 进入主页
				enterHome();
				Toast.makeText(getApplicationContext(), "IO错误", 0).show();
				break;
			case JSON_ERROR:
				// 进入主页
				enterHome();
				Toast.makeText(getApplicationContext(), "JSON错误", 0).show();
				break;
			default:
				break;
			}
		}

	};

	/**
	 * 检查版本，并提示更新
	 */
	private void checkVersion() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// 访问网络获取服务器端数据，展示新版本号及下载地址。
				Message msg = Message.obtain();
				Long startTime = System.currentTimeMillis();

				try {
					URL url = new URL(getString(R.string.serviceURL));
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.setReadTimeout(4000);
					int code = conn.getResponseCode();
					Log.i("返回码", "返回码是：" + code);
					if (conn.getResponseCode() == 200) {
						String resultString = StreamTool.getStringFromStream(conn.getInputStream());
						Log.i("联网检测", "联网成功了！！！！！！！！！！！！！");
						JSONObject object = new JSONObject(resultString);
						String version = (String) object.get("version");
						desc = (String) object.get("description");
						apkurl = (String) object.get("apkurl");
						updateName = object.getString("updatename");
						// 判断获得的版本信息
						if (version.equals(SplashActivity.this.getVersionName())) {
							// 如果版本相同的情况，则返回进入主页的代码信息，在主线程中打开主页。
							Log.i("子线程", "版本正确，发送进入主页的消息！！！！！！！！！！！！！！！！！！");
							msg.what = ENTER_HOME;
						} else {
							// 如果不同，则弹出对话框，提示下载更新。
							Log.i("子线程", "版本不对，发送打开对话框的消息！！！！！！！！！！！！！！");
							msg.what = SHOW_UPDATE_DIALOG;
						}
					}

				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.what = URL_ERROR;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.what = IO_ERROR;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.what = JSON_ERROR;
				} finally {
					Long endTime = System.currentTimeMillis();
					Long durTime = endTime - startTime;
					if (durTime < 1000) {
						try {
							Thread.sleep(1000 - durTime);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					handler.sendMessage(msg);
				}

			}

		}).start();
	}

	/**
	 * 需要更新时，弹出升级对话框。
	 */
	protected void showUpdateDialog() {
		AlertDialog.Builder builder = new Builder(SplashActivity.this);
		builder.setTitle("提示更新");
		builder.setMessage(desc);
		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				enterHome();
				dialog.dismiss();
			}
		});
		Log.i("对话框中", "builder创建成功！！！！！！！！！！！！！！！！！！！！");
		builder.setPositiveButton("立即更新", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 点击后更新，先联网获取下载更新包。使用afinal工具包。
				FinalHttp finalHttp = new FinalHttp();
				finalHttp.download(apkurl, Environment.getDataDirectory().getAbsolutePath() + "/" + updateName,
						new AjaxCallBack<File>() {

							@Override
							public void onFailure(Throwable t, int errorNo, String strMsg) {
								super.onFailure(t, errorNo, strMsg);
								t.printStackTrace();
								Toast.makeText(getApplicationContext(), "下载失败", 0).show();
							}

							@Override
							public void onLoading(long count, long current) {
								super.onLoading(count, current);
								tv_download.setVisibility(View.VISIBLE);
								int progress = (int) (current * 100 / count);
								tv_download.setText("正在下载：" + progress + "%");

							}

							/*
							 * (non-Javadoc)
							 * 
							 * @see
							 * net.tsz.afinal.http.AjaxCallBack#onSuccess(java
							 * .lang. Object)
							 */
							@Override
							public void onSuccess(File t) {
								super.onSuccess(t);
								// 下载成功，安装APK
								installAPK(t);
							}

							private void installAPK(File t) {
								Intent intent = new Intent();
								intent.setAction("android.intent.action.VIEW");
								intent.addCategory("android.intent.category.DEFAULT");
								intent.setDataAndType(Uri.fromFile(t), "application/vnd.android.package-archive");
								startActivity(intent);
							}

						});
			}
		});
		builder.setNegativeButton("下次再说", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				enterHome();

			}
		});
		builder.show();
	}

	/**
	 * 进入APP主页
	 */
	private void enterHome() {
		Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * 
	 * @return 获得当前APP的版本号。
	 */
	private String getVersionName() {
		PackageManager pm = getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void loadDatabase(String dbName) {
		InputStream inputStream = null;
		OutputStream os = null;
		File file = null;
		// 数据库的建立.
		try {
			file = new File(getFilesDir(), dbName);
			if (file.exists() && file.length() > 0) {
				return;
			} else {
				inputStream = this.getAssets().open(dbName);
				os = new FileOutputStream(file);
				int len = 0;
				byte[] b = new byte[1024];
				while ((len = inputStream.read(b)) != -1) {
					os.write(b, 0, len);
					os.flush();
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void installShortcut() {
		
		if (sp.getBoolean("shortcut", false)) {
			Toast.makeText(getApplicationContext(), "已经创建快捷方式", 0).show();
			return;
		}
		Log.i(TAG, "开始创建快捷方式");
		// 发送创建快捷方式的广播意图.
		Intent intent = new Intent();
		//注意这个action是特殊的,与一般的不同.
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "安全卫士");
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON,
				BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
		// 快捷方式打开应用的意图. 其中的classname 需要指定想要打开的明确哪个页面.
		Intent shortcutIntent = new Intent();
		shortcutIntent.setAction("android.intent.action.MAIN");
		shortcutIntent.addCategory("android.intent.category.LAUNCHER");
		shortcutIntent.setClassName(getPackageName(), "com.coconut.mobilesafe.SplashActivity");
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		sendBroadcast(intent);
		Editor editor = sp.edit();
		editor.putBoolean("shortcut", true);
		editor.commit();
		

	}
}
