package com.buyopic.android.utils;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @author Chaitanya.Konduru
 * 
 */

public class FetchCurrentLocation {

	private static final String TAG = FetchCurrentLocation.class.getSimpleName();
	private LocationManager locationManager = null;
	public boolean canFetchLocation = false;
	private boolean isGpsEnabled = false;
	private boolean isNetworkproviderEnabled = false;
	private boolean isPassiveProviderEnabled = false;
	private static final int OUT_OF_SERVICE = 0;
	protected  Location location=null;
	private static final int TEMPERORY_AVAILABLE = 1;
	private static final int SERVICE_AVAILABLE = 2;
	
	private static final int MIN_DISTANCE_FOR_UPDATES=0;// In meters
	private static final int MIN_TIME_FOR_UPDATES=0;//0 milliseconds
	
	private Timer timerTask=null;
	private LocationResult locationResult=null;
	
	/**
	 * 
	 * @param pass the Activity Context
	 * @param Implement the LocationResult Interface
	 */
		public void fetchLocation(Context context, LocationResult locationResult) {

		if (locationManager == null) {
			location=null;
			locationManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
		}
		this.locationResult = locationResult;

		try {
			isGpsEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception e) {
		}
		
		try {
			isNetworkproviderEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception e) {
		}
		
		isPassiveProviderEnabled = locationManager
				.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
		
		if (!isGpsEnabled && !isNetworkproviderEnabled
				&& !isPassiveProviderEnabled) {
			canFetchLocation = false;
			return;
		} else {
			canFetchLocation = true;
		}
		if (isGpsEnabled) {
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, MIN_TIME_FOR_UPDATES, MIN_DISTANCE_FOR_UPDATES, gpsLocationListener);
		}
		if (isNetworkproviderEnabled) {
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, MIN_TIME_FOR_UPDATES, MIN_DISTANCE_FOR_UPDATES,
					networkLocationListener);
		}
		if (isPassiveProviderEnabled) {
			locationManager.requestLocationUpdates(
					LocationManager.PASSIVE_PROVIDER, MIN_TIME_FOR_UPDATES, MIN_DISTANCE_FOR_UPDATES,
					passiveLocationListener);
		}
		if(timerTask==null)
		{
		timerTask = new Timer();
		}
		timerTask.schedule(new GetLastLocationTask(), 10000);
	}

	private LocationListener gpsLocationListener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			printProviderStatusMessage(provider,status);

		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
		}

		public void onLocationChanged(Location location) {
			timerTask.cancel();
			Message.obtain(handler, 100, location).sendToTarget();
			locationManager.removeUpdates(this);
			locationManager.removeUpdates(networkLocationListener);
			locationManager.removeUpdates(passiveLocationListener);
		}
	};

	private LocationListener networkLocationListener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			printProviderStatusMessage(provider,status);
		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
		}

		public void onLocationChanged(Location location) {
			timerTask.cancel();
			Message.obtain(handler, 100, location).sendToTarget();
			locationManager.removeUpdates(this);
			locationManager.removeUpdates(gpsLocationListener);
			locationManager.removeUpdates(passiveLocationListener);

		}
	};

	private LocationListener passiveLocationListener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			printProviderStatusMessage(provider,status);
		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
		}

		public void onLocationChanged(Location location) {
			timerTask.cancel();
			Message.obtain(handler, 100, location).sendToTarget();
			locationManager.removeUpdates(this);
			locationManager.removeUpdates(networkLocationListener);
			locationManager.removeUpdates(gpsLocationListener);
		}
	};

	public interface LocationResult {
		public abstract void gotLocation(Location location);
	}

	private void printProviderStatusMessage(String provider,int status) {
		switch (status) {
		case OUT_OF_SERVICE:
			Log.v(TAG,provider+":"+ getCurrentTime() + ":Out Of Service");
			break;
		case SERVICE_AVAILABLE:
			Log.v(TAG,provider+":"+getCurrentTime() + ":Service Available");
			break;
		case TEMPERORY_AVAILABLE:
			Log.v(TAG, provider+":"+getCurrentTime() + ":Temperorily Out Of Service");
			break;
		}

	}

	private String getCurrentTime() {
		return new Date().toString();
	}

	class GetLastLocationTask extends TimerTask {

		@Override
		public void run() {
			locationManager.removeUpdates(gpsLocationListener);
			locationManager.removeUpdates(networkLocationListener);
			locationManager.removeUpdates(passiveLocationListener);

			Location gps_Location = null;
			Location network_Location = null;
			Location passive_Location = null;
			if(isGpsEnabled)
			{
				canFetchLocation=true;
			gps_Location = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			}
			if(isNetworkproviderEnabled)
			{
				canFetchLocation=true;
			network_Location = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}
			if(isPassiveProviderEnabled)
			{
				canFetchLocation=true;
			passive_Location = locationManager
					.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
			}

			if (gps_Location != null && network_Location != null
					&& passive_Location != null) {
				Location recentlyKnownLocation = getRecentlyKnownLocation(
						gps_Location, network_Location, passive_Location);
				Message.obtain(handler, 100, recentlyKnownLocation).sendToTarget();
				return;
			}
			
			if(gps_Location!=null)
			{
				Message.obtain(handler, 100, gps_Location).sendToTarget();
				return;
			}
			if(network_Location!=null)
			{
				Message.obtain(handler, 100, network_Location).sendToTarget();
				return;
			}
			if(passive_Location!=null)
			{
				Message.obtain(handler, 100, passive_Location).sendToTarget();
				return;
			}
			Message.obtain(handler, 100, null).sendToTarget();
		}
	}

	private Location getRecentlyKnownLocation(Location gpsLocation,
			Location networkLocation, Location passiveLocation) {
		Location location = null;
		long recentTime = Math.max(gpsLocation.getTime(),
				Math.max(networkLocation.getTime(), passiveLocation.getTime()));
		if (recentTime == gpsLocation.getTime()) {
			location = gpsLocation;
		} else if (recentTime == networkLocation.getTime()) {
			location = networkLocation;
		} else if (recentTime == passiveLocation.getTime()) {
			location = passiveLocation;
		}
		return location;
	}
	
	Handler handler=new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(location==null)
			{
			location=(Location) msg.obj;
			locationResult.gotLocation(location);
			}
		}
		
		
	};

}
