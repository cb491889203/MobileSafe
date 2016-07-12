package com.coconut.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;

public class LocationService extends Service {

	private SharedPreferences sp;
	private LocationManager lm;
	private MyListener listener;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		sp = LocationService.this.getSharedPreferences("config", MODE_PRIVATE);
		lm = (LocationManager) LocationService.this.getSystemService(LOCATION_SERVICE);

		listener = new MyListener();
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		lm.requestLocationUpdates(lm.getBestProvider(criteria, true), 0, 0, listener);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		lm.removeUpdates(listener);
		listener = null;
	}

	class MyListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			String latitude = "纬度：" + location.getLatitude()+"\n"; // 纬度
			String longitude = "经度：" + location.getLongitude()+"\n"; // 经度
			String accuracy = "精度：" + location.getAccuracy()+"\n";
			Editor editor = sp.edit();
			editor.putString("lastLocation", latitude+longitude+accuracy);
			
		}

		@Override
		public void onProviderDisabled(String provider) {

		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

	}
}
