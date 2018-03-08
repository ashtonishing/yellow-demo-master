package com.buyopic.android.radius;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.util.Log;

import com.buyopic.android.fragments.CloseByListFragment;
import com.buyopic.android.models.CheckUpdate;
import com.buyopic.android.models.OrderStatus;
import com.buyopic.android.models.StoreInfo;
import com.buyopic.android.network.BuyopicNetworkCallBack;
import com.buyopic.android.network.BuyopicNetworkServiceManager;
import com.buyopic.android.network.JsonResponseParser;
import com.buyopic.android.utils.Constants;
import com.buyopic.android.utils.FetchCurrentLocation;
import com.buyopic.android.utils.FetchCurrentLocation.LocationResult;
import com.buyopic.android.utils.Utils;
import com.radiusnetworks.ibeacon.IBeacon;

public class CheckUpdatesBroadcast extends BroadcastReceiver implements
		LocationResult, BuyopicNetworkCallBack {

	public static final String KEY_UPDATELIST = "updatelist";
	public static final String KEY_UPDATEORDERNOTIFICATION = "updateordernotification";
	private BuyOpic app = null;
	private BuyopicNetworkServiceManager buyopicNetworkServiceManager;
	private Location currentLocation = null;
	private boolean isBeaconAvailable = false;
	private String uuid = "";
	private String major = "";
	private String minor = "";
	private Context mContext;
	private int duration;
	private BackgroundMonitorService serOb;
	Handler handler;

	@Override
	public void onReceive(Context context, Intent intent) {
		app = (BuyOpic) context.getApplicationContext();
		Utils.showLog("on Receive");
		isBeaconAvailable = app.isBeaconAvailable();
		duration = app.getDuration();
		if (app.getmConsumerId() != null) {
			mContext = context;
			buyopicNetworkServiceManager = new BuyopicNetworkServiceManager(
					context);
			checkForNewOffers(context);
		}
	}

	private void checkForNewOffers(Context context) {
		duration += 15;
		app.setDuration(duration);
		Utils.showLog("isBeaconAvailable" + isBeaconAvailable);
		if (isBeaconAvailable) {
			Utils.showLog("IS BEACON AVAILBLE" + isBeaconAvailable);
			IBeacon iBeacon = app.getCurrentBeacon();
			uuid = iBeacon.getProximityUuid();
			major = String.valueOf(iBeacon.getMajor());
			minor = String.valueOf(iBeacon.getMinor());
			sendRequestToServer(Constants.REQUEST_CHECK_FOR_NEW_DATA,
					isBeaconAvailable);
		} else {

			FetchCurrentLocation fetchCurrentLocation = new FetchCurrentLocation();
			fetchCurrentLocation.fetchLocation(context, this);
		}

	}

	private void sendRequestToServer(int requestCode, boolean isBeaconAvailable) {
		if (requestCode == Constants.REQUEST_CHECK_FOR_NEW_DATA) {
			Log.i("UPDATE", "REQUEST_CHECK_FOR_NEW_DATA");
			Utils.showLog("duration " + duration);
			String isCheckForNewAlerts = "no";
			// if(duration==60)
			// {
			Utils.showLog("duration block" + duration);
			app.setDuration(0);
			isCheckForNewAlerts = "yes";
			// }

			if (isBeaconAvailable) {
				buyopicNetworkServiceManager.sendNearestStoreAlertsRequest(
						requestCode, uuid, major, minor,
						String.valueOf(app.getmSourceLatitude()),
						String.valueOf(app.getmSourceLongitude()),
						app.getmConsumerId(), "YES", isCheckForNewAlerts,
						false, this, mContext);
			}

			// switchspotlight functionality
			else if (app.getmSourceLatitude() != 0
					&& app.getmSourceLongitude() != 0) {

				Log.i("Loc changes",
						"chkupdate" + "lat "
								+ String.valueOf(app.getmSourceLatitude())
								+ " long "
								+ String.valueOf(app.getmSourceLatitude()));
				buyopicNetworkServiceManager.sendNearestStoreAlertsRequest(
						requestCode, "", "", "",
						String.valueOf(app.getmSourceLatitude()),
						String.valueOf(app.getmSourceLongitude()),
						app.getmConsumerId(), "NO", isCheckForNewAlerts, false,
						this, mContext);

			} else {
				if (currentLocation != null) {
					DecimalFormat decimalFormat = new DecimalFormat("#0.000000");
					String latitude = decimalFormat.format(currentLocation
							.getLatitude());
					String longitude = decimalFormat.format(currentLocation
							.getLongitude());
					buyopicNetworkServiceManager.sendNearestStoreAlertsRequest(
							requestCode, "", "", "", latitude, longitude,
							app.getmConsumerId(), "NO", isCheckForNewAlerts,
							false, this, mContext);
				}
			}
		} else if (requestCode == Constants.REQUEST_NEAREST_STORE_ALERTS) {
			Log.i("UPDATE", "REQUEST_NEAREST_STORE_ALERTS");
			if (isBeaconAvailable) {
				buyopicNetworkServiceManager
						.sendNearestStoreAlertsRequest(requestCode, uuid,
								major, minor,
								String.valueOf(app.getmSourceLatitude()),
								String.valueOf(app.getmSourceLongitude()),
								app.getmConsumerId(), "YES", null, true, this,
								mContext);
			}
			// switchspotlight functionality
			else if (app.getmSourceLatitude() != 0
					&& app.getmSourceLongitude() != 0

			) {
				Log.i("Loc changes",
						"chkupdate" + "lat "
								+ String.valueOf(app.getmSourceLatitude())
								+ " long "
								+ String.valueOf(app.getmSourceLatitude()));
				buyopicNetworkServiceManager.sendNearestStoreAlertsRequest(
						requestCode, "", "", "",
						String.valueOf(app.getmSourceLatitude()),
						String.valueOf(app.getmSourceLongitude()),
						app.getmConsumerId(), "NO", null, true, this, mContext);

			} else {
				if (currentLocation != null) {
					DecimalFormat decimalFormat = new DecimalFormat("#0.000000");
					String latitude = decimalFormat.format(currentLocation
							.getLatitude());
					String longitude = decimalFormat.format(currentLocation
							.getLongitude());
					buyopicNetworkServiceManager.sendNearestStoreAlertsRequest(
							requestCode, "", "", "", latitude, longitude,
							app.getmConsumerId(), "NO", null, true, this,
							mContext);
				}
			}
		} else {
			Log.i("UPDATE", "else");
			if (isBeaconAvailable) {
				buyopicNetworkServiceManager.sendNearestStoreAlertsRequest(
						requestCode, uuid, major, minor,
						String.valueOf(app.getmSourceLatitude()),
						String.valueOf(app.getmSourceLongitude()),
						app.getmConsumerId(), "YES", null, false, this,
						mContext);
			}
			// switchspotlight functionality
			else if (app.getmSourceLatitude() != 0
					&& app.getmSourceLongitude() != 0) {
				Log.i("Loc changes",
						"chkupdate" + "lat "
								+ String.valueOf(app.getmSourceLatitude())
								+ " long "
								+ String.valueOf(app.getmSourceLatitude()));
				buyopicNetworkServiceManager
						.sendNearestStoreAlertsRequest(requestCode, "", "", "",
								String.valueOf(app.getmSourceLatitude()),
								String.valueOf(app.getmSourceLongitude()),
								app.getmConsumerId(), "NO", null, false, this,
								mContext);

			} else {
				if (currentLocation != null) {
					DecimalFormat decimalFormat = new DecimalFormat("#0.000000");
					String latitude = decimalFormat.format(currentLocation
							.getLatitude());
					String longitude = decimalFormat.format(currentLocation
							.getLongitude());
					buyopicNetworkServiceManager.sendNearestStoreAlertsRequest(
							requestCode, "", "", "", latitude, longitude,
							app.getmConsumerId(), "NO", null, false, this,
							mContext);

				}
			}
		}

	}

	@Override
	public void gotLocation(Location location) {
		if (location != null) {
			isBeaconAvailable = false;
			this.currentLocation = location;
			sendRequestToServer(Constants.REQUEST_CHECK_FOR_NEW_DATA,
					isBeaconAvailable);
		}

	}

	@Override
	public void onSuccess(int requestCode, Object object) {
		switch (requestCode) {
		case Constants.REQUEST_CHECK_FOR_NEW_DATA:
			CheckUpdate checkUpdate = JsonResponseParser
					.parseCheckUpdatesResponse((String) object);
			if (checkUpdate != null) {

				if (checkUpdate.getmIsComputed()) {
					serOb = new BackgroundMonitorService();

					if (checkUpdate.getmNextRunTime() != null)
						BuyopicNetworkServiceManager.NEXT_RUNTIME = checkUpdate
								.getmNextRunTime();

					if (checkUpdate.getmBackGrndBtwnScanPeriod() != 0)
						BuyopicNetworkServiceManager.BACKGROUND_BETWEEN_SCAN_PERIOD = checkUpdate
								.getmBackGrndBtwnScanPeriod() * 1000l;

					if (checkUpdate.getmBackGrndScanPeriod() != 0)
						BuyopicNetworkServiceManager.BACKGROUND_SCAN_PERIOD = checkUpdate
								.getmBackGrndScanPeriod() * 1000l;

					if (checkUpdate
							.getmIntervalChkForNewAlertsBackGrndMonitor() != 0) {
						BuyopicNetworkServiceManager.INTERVAL_CHECK_FOR_NEW_ALERTS = checkUpdate
								.getmIntervalChkForNewAlertsBackGrndMonitor() * 1000;
					}

					if (checkUpdate.getmIntervalChkForNewAlertsCloseByList() != 0)
						BuyopicNetworkServiceManager.INTERVAL_CHECK_FOR_NEW_ALERTS_CLOSEBY = checkUpdate
								.getmIntervalChkForNewAlertsCloseByList() * 1000l;
					Log.i("BLUETOOTH", "getmNearestBeaconDistance"
							+ checkUpdate.getmNearestBeaconDistance());

					if (checkUpdate.getmNearestBeaconDistance() != 0) {

						handler = new Handler();
						Log.i("BLUETOOTH", "getmNearestBeaconDistance"
								+ checkUpdate.getmNearestBeaconDistance());

						if (checkUpdate.getmNearestBeaconDistance()>0&&checkUpdate.getmNearestBeaconDistance() <= 300) {
							BuyopicNetworkServiceManager.TURN_BLUETOOTH_ON=true;
							Log.i("BLUETOOTH", "getmNearestBeaconDistance>300");
							//app.setNearestBeaconDistance(checkUpdate.getmNearestBeaconDistance());
							//serOb.showBlueToothPopUp(mContext);
							
						}
					}

					serOb.setAlarmForCheckUpdates(mContext);

					if (checkUpdate.getmAreUpdatesAvailable()
							|| Constants.isAddresschanged) {
						Log.i("UPDATE", "updates available");
						Constants.isAddresschanged = false;
						sendRequestToServer(
								Constants.REQUEST_NEAREST_STORE_ALERTS,
								isBeaconAvailable);
					}
					if (checkUpdate.getmOrderStatusObj() != null) {
						Log.i("NOTIFY", "updateNewDataToUIForNotification");
						updateNewDataToUIForNotification(checkUpdate
								.getmOrderStatusObj());
					}
				}
			}

			break;
		case Constants.REQUEST_NEAREST_STORE_ALERTS:
			resetBeaconLocationValues();
			List<StoreInfo> storeInfos = JsonResponseParser.parseStoreInfos(
					mContext, (String) object);
			updateNewDataToUI(storeInfos);
		}
	}

	@Override
	public void onFailure(int requestCode, String message) {

	}

	private void updateNewDataToUI(List<StoreInfo> storeInfos) {
		if (storeInfos != null && !storeInfos.isEmpty()) {
			if (app.isUserInHomeListFragment()) {
				Intent intent = new Intent(
						CloseByListFragment.ACTION__BEACON_DATA_CHANGED_INTENT);
				intent.putParcelableArrayListExtra(KEY_UPDATELIST,
						(ArrayList<StoreInfo>) storeInfos);
				mContext.sendBroadcast(intent);
			} else {

			}
		}
	}

	private void updateNewDataToUIForNotification(OrderStatus mOrderStatus) {
		Log.i("NOTIFY", "mOrderStatus");
		if (mOrderStatus != null) {
			if (app.isUserInHomeListFragment()) {
				Intent intent = new Intent(
						CloseByListFragment.ACTION__BEACON_DATA_CHANGED_INTENT);
				intent.putExtra(KEY_UPDATEORDERNOTIFICATION, mOrderStatus);
				mContext.sendBroadcast(intent);
			} else {
				Log.i("NOTIFY", "sendOrderNotification");
				serOb.sendOrderNotification(
						"Order Status from " + mOrderStatus.getStoreName()
								+ " " + mOrderStatus.getStatusDesc(), mContext,
						mOrderStatus);

			}
		}
	}

	private void resetBeaconLocationValues() {
		this.currentLocation = null;
		isBeaconAvailable = false;
		uuid = "";
		major = "";
		minor = "";
	}

}
