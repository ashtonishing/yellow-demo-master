package com.buyopic.android.radius;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.buyopic.android.beacon.R;
import com.buyopic.android.database.BuyopicConsumerDatabase;
import com.buyopic.android.fragments.CloseByListFragment;
import com.buyopic.android.fragments.OffersListFragment;
import com.buyopic.android.models.Beacon;
import com.buyopic.android.models.OrderStatus;
import com.buyopic.android.network.BuyopicNetworkCallBack;
import com.buyopic.android.network.BuyopicNetworkServiceManager;
import com.buyopic.android.network.JsonResponseParser;
import com.buyopic.android.utils.Constants;
import com.buyopic.android.utils.DeviceStatusConstants;
import com.buyopic.android.utils.Utils;
import com.radiusnetworks.ibeacon.IBeacon;
import com.radiusnetworks.ibeacon.IBeaconConsumer;
import com.radiusnetworks.ibeacon.IBeaconData;
import com.radiusnetworks.ibeacon.IBeaconDataNotifier;
import com.radiusnetworks.ibeacon.IBeaconManager;
import com.radiusnetworks.ibeacon.MonitorNotifier;
import com.radiusnetworks.ibeacon.RangeNotifier;
import com.radiusnetworks.ibeacon.Region;
import com.radiusnetworks.ibeacon.client.DataProviderException;

@SuppressLint("HandlerLeak")
public class BackgroundMonitorService extends Service implements
		IBeaconConsumer, IBeaconDataNotifier, BuyopicNetworkCallBack {

	public static final String KEY_ISFROM_NOTIFICATION = "isfromNotification";
	public static final String KEY_ISFROM_ORDER_NOTIFICATION = "isfromOrderNotification";
	public static final String KEY_ISFROM_NEAREST_BEACON_DATA = "isfromnearestbeacondata";
	private IBeaconManager iBeaconManager = IBeaconManager
			.getInstanceForApplication(this);
	private BuyopicNetworkServiceManager buyopicNetworkServiceManager;
	private IBeacon currentBeacon = null;
	protected boolean isBeaconAvailable = false;
	private BuyopicConsumerDatabase buyopicDatabase;

	private static final int BEACON_FOUND = 200;
	private static final int BEACON_NOT_FOUND = 201;

	// private static final int DEFAULT_RSSI_POWER = -90; //June 25
	private static final int DEFAULT_RSSI_POWER = -70; // June 28
	public static final String KEY_REQUESTING_SERVER = "requesting_server";
	public static final String KEY_BEACON_CHANGED = "beacon_changed";
	public static final String KEY_LATITUDE = "latitude";
	public static final String KEY_LONGITUDE = "longitude";

	private BuyOpic buyOpic;
	protected int count = 1;
	private int mDeviceState;

	@Override
	public void onCreate() {
		super.onCreate();
	//	setBluetooth(true);
		buyOpic = (BuyOpic) getApplication();
		
		buyopicNetworkServiceManager = BuyopicNetworkServiceManager
				.getInstance(this);
		buyopicDatabase = BuyopicConsumerDatabase.shareInstance(this);
		buyopicDatabase.truncateDatabase();
		iBeaconManager.bind(BackgroundMonitorService.this);
		if (iBeaconManager.isBound(BackgroundMonitorService.this))
			iBeaconManager.setBackgroundMode(BackgroundMonitorService.this,
					true);
		setAlarmForCheckUpdates(this);

	}

	public void setAlarmForCheckUpdates(Context context) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
		Intent checkUpdatesIntent = new Intent(context,
				CheckUpdatesBroadcast.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
				checkUpdatesIntent, 0);

		if (alarmManager != null) {
			alarmManager.cancel(pendingIntent);
		}

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, 15);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(),
				BuyopicNetworkServiceManager.INTERVAL_CHECK_FOR_NEW_ALERTS,
				pendingIntent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		iBeaconManager.unBind(this);
	}

	public List<IBeacon> isBeaconsFound(HashMap<Integer, List<IBeacon>> hashmap) {
		List<IBeacon> firstIterationBeacons = null;
		if (hashmap != null && !hashmap.isEmpty()) {
			firstIterationBeacons = hashmap.get(1);
			List<IBeacon> secondIterationBeacons = hashmap.get(2);
			List<IBeacon> thirdIterationBeacons = hashmap.get(3);
			secondIterationBeacons.retainAll(thirdIterationBeacons);
			firstIterationBeacons.retainAll(secondIterationBeacons);
		}
		return firstIterationBeacons;
	}

	@Override
	public void onIBeaconServiceConnect() {

		iBeaconManager
				.setBackgroundBetweenScanPeriod(BuyopicNetworkServiceManager.BACKGROUND_BETWEEN_SCAN_PERIOD);
		iBeaconManager
				.setBackgroundScanPeriod(BuyopicNetworkServiceManager.BACKGROUND_SCAN_PERIOD);

		iBeaconManager
				.setForegroundBetweenScanPeriod(BuyopicNetworkServiceManager.BACKGROUND_BETWEEN_SCAN_PERIOD);
		iBeaconManager
				.setForegroundScanPeriod(BuyopicNetworkServiceManager.BACKGROUND_SCAN_PERIOD);

		try {
			iBeaconManager.updateScanPeriods();
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}

		iBeaconManager.setRangeNotifier(new RangeNotifier() {

			@Override
			public void didRangeBeaconsInRegion(Collection<IBeacon> iBeacons,
					Region region) {

				iBeaconManager
						.setBackgroundBetweenScanPeriod(BuyopicNetworkServiceManager.BACKGROUND_BETWEEN_SCAN_PERIOD);
				iBeaconManager
						.setBackgroundScanPeriod(BuyopicNetworkServiceManager.BACKGROUND_BETWEEN_SCAN_PERIOD);

				iBeaconManager
						.setForegroundBetweenScanPeriod(BuyopicNetworkServiceManager.BACKGROUND_BETWEEN_SCAN_PERIOD);
				iBeaconManager
						.setForegroundScanPeriod(BuyopicNetworkServiceManager.BACKGROUND_BETWEEN_SCAN_PERIOD);

				try {
					iBeaconManager.updateScanPeriods();
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}

				try {
					if (iBeacons != null && iBeacons.size() > 0) {
						Utils.showLog("Beacons Size:" + iBeacons.size());
						Iterator<IBeacon> beaconIterator = iBeacons.iterator();
						count++;

						while ((null != beaconIterator)
								&& (beaconIterator.hasNext())) {
							IBeacon ibeacon = beaconIterator.next();
							// if (ibeacon.getRssi() >= DEFAULT_RSSI_POWER) {
							printLog(ibeacon);
							buyOpic.setBeaconAvailable(true);
							buyOpic.setCurrentBeacon(ibeacon);
							currentBeacon = ibeacon;
							isBeaconAvailable = true;
							// removeBeaconsBasedOnTimeStamp();
							Message.obtain(handler, 106, ibeacon)
									.sendToTarget();
							boolean isExists = buyopicDatabase
									.checkBeaconExistsOrNot(
											String.valueOf(ibeacon.getMajor()),
											String.valueOf(ibeacon.getMinor()),
											ibeacon.getProximityUuid());
							Log.i("BEACON", "isExists " + isExists);
							if (!isExists) {
								Message.obtain(handler, BEACON_FOUND, ibeacon)
										.sendToTarget();
							}
							// }

						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (count == 3) {
					List<IBeacon> iBeaconsList = new ArrayList<IBeacon>(
							iBeacons);
					Message.obtain(handler, 105, iBeaconsList).sendToTarget();
					count = 1;
				}

			}

		});

		iBeaconManager.setMonitorNotifier(new MonitorNotifier() {
			@Override
			public void didEnterRegion(Region region) {
				try {
					iBeaconManager.startRangingBeaconsInRegion(region);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void didExitRegion(Region region) {
				Log.i("BEACON", "region is " + region
						+ " currentBeacon minor is" + currentBeacon.getMinor());
				isBeaconAvailable = false;
				buyOpic.setBeaconAvailable(false);
				buyOpic.setCurrentBeacon(currentBeacon);
				Message.obtain(handler, BEACON_NOT_FOUND).sendToTarget();
				Utils.showLog("No Beacons Found");
			}

			@Override
			public void didDetermineStateForRegion(int state, Region region) {
			}

		});

		try {
			iBeaconManager.startMonitoringBeaconsInRegion(new Region(
					"myMonitoringUniqueId", null, null, null));
			/*
			 * iBeaconManager.startMonitoringBeaconsInRegion(new Region(
			 * "myMonitoringUniqueId", "8deefbb9-f738-4297-8040-96668bb44281",
			 * null, null));
			 */} catch (RemoteException e) {
		}
		catch(Exception e){
			
		}
	}

	private void printLog(IBeacon ibeacon) {
		Utils.showLog("Beacon Minor -->" + ibeacon.getMinor() + ",Rssi-->"
				+ ibeacon.getRssi());
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case BEACON_FOUND:
				Log.i("BEACON", "BEACON_FOUND");
				IBeacon iBeacon = (IBeacon) msg.obj;
				Beacon beacon = new Beacon();
				beacon.setIsNotified("f");
				beacon.setmBeaconUUID(iBeacon.getProximityUuid());
				beacon.setmMajor(String.valueOf(iBeacon.getMajor()));
				beacon.setmMinor(String.valueOf(iBeacon.getMinor()));
				beacon.setmRssi(String.valueOf(iBeacon.getRssi()));
				beacon.setmEnteredTime(getFormattedTime());
				beacon.setmBeaconId_Minor_Major(beacon.getmMajor() + "_"
						+ beacon.getmMinor());
				long id = buyopicDatabase.insertItem(beacon);
				if (id != -1) {
					buyopicNetworkServiceManager.sendSaveConsumerEntryRequest(
							Constants.REQUEST_SEND_ENTRY_REQUEST,
							buyOpic.getmConsumerId(), beacon.getmBeaconUUID(),
							beacon.getmMajor(), beacon.getmMinor(),
							getFormattedTime(), beacon.getmRssi(),
							BackgroundMonitorService.this);
					notifyBeaconDataChanged("string");
					buyopicNetworkServiceManager.sendCheckBeaconAlertsRequest(
							Constants.REQUEST_CHECK_BEACON_ALERTS,
							beacon.getmBeaconUUID(), beacon.getmMinor(),
							beacon.getmMajor(), BackgroundMonitorService.this);

				}
				break;
			case BEACON_NOT_FOUND:
				truncateTheBeaconInfo();
				break;
			case 105:
				@SuppressWarnings("unchecked")
				List<IBeacon> iBeaconsList = (List<IBeacon>) msg.obj;
				if (iBeaconsList != null && !iBeaconsList.isEmpty()) {
					String majors_and_minors = "";
					for (IBeacon iBeacon2 : iBeaconsList) {
						buyopicNetworkServiceManager
								.sendUpdateBeaconRssiLevelsRequest(
										Constants.REQUEST_RSSI_UPDATE,
										buyOpic.getmConsumerId(), iBeacon2,
										BackgroundMonitorService.this);
						if (TextUtils.isEmpty(majors_and_minors)) {
							majors_and_minors = "\'" + iBeacon2.getMajor()
									+ "_" + iBeacon2.getMinor() + "\'";
						} else {
							majors_and_minors += "," + "\'"
									+ iBeacon2.getMajor() + "_"
									+ iBeacon2.getMinor() + "\'";
						}
					}
					List<Beacon> notInBeaconList = buyopicDatabase
							.getNotInBeaconDetails(majors_and_minors);
					Log.i("BEACON", "notInBeaconList " + notInBeaconList.size()
							+ " " + notInBeaconList);
					for (int i = 0; i < notInBeaconList.size(); i++) {
						Log.i("BEACON",
								"notInBeaconList  minor and major values "
										+ notInBeaconList.get(i)
												.getmBeaconId_Minor_Major());
					}
					if (notInBeaconList != null && !notInBeaconList.isEmpty()) {

						// printBeaconsInformation(notInBeaconList);
						sendExitTimeRequests(notInBeaconList);

						int rowsAffected = buyopicDatabase
								.deleteExitedBeacons(majors_and_minors);
						Log.i("BEACON", "deleteExitedBeacons  rowsAffected "
								+ rowsAffected);
						if (rowsAffected > 0) {
							notifyBeaconDataChanged("string");
						}
					}

					Utils.showLog("All Records Which are in database");
					List<Beacon> beacons = buyopicDatabase
							.getAllBeaconDetails();
					printBeaconsInformation(beacons);
				}
				break;
			case 106: {
				removeBeaconsBasedOnTimeStamp();
			}
				break;
			default:
				break;
			}

		}

	};

	private void notifyBeaconDataChanged(Object object) {
		Intent intent = new Intent(
				CloseByListFragment.ACTION__BEACON_DATA_CHANGED_INTENT);
		if (object instanceof Boolean) {
			boolean status = (Boolean) object;
			intent.putExtra(KEY_REQUESTING_SERVER, status);
		} else if (object instanceof String) {
			intent.putExtra(KEY_BEACON_CHANGED, true);
		}
		sendBroadcast(intent);
	}

	protected void sendExitTimeRequests(List<Beacon> beacons) {
		if (beacons != null && !beacons.isEmpty()) {
			for (Beacon beacon : beacons) {
				Log.i("BEACON", "sendExitTimeRequests " + beacons.size() + " "
						+ beacons.get(0) + " minor value " + beacon.getmMinor());
				buyopicNetworkServiceManager.sendSaveConsumerExitRequest(
						Constants.REQUEST_SEND_ENTRY_REQUEST,
						buyOpic.getmConsumerId(), beacon.getmBeaconUUID(),
						beacon.getmMajor(), beacon.getmMinor(),
						beacon.getmEnteredTime(), getFormattedTime(),
						beacon.getmRssi(), BackgroundMonitorService.this);

			}
		}

	}

	private void removeBeaconsBasedOnTimeStamp() {
		// code to be implemented (removing the details of beacons which are
		// more than 5 minutes and above from DB and also we need to remove the
		// beacon list)

		List<Beacon> beaconsList = buyopicDatabase.getAllBeaconDetails();
		if (beaconsList != null && !beaconsList.isEmpty()) {
			String majors_and_minors = "";
			for (Beacon beacon : beaconsList) {

				SimpleDateFormat dateFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss Z");
				Date date1 = null, date2 = null;

				try {
					date1 = dateFormat.parse(dateFormat.format(new Date()));
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					date2 = dateFormat.parse(beacon.getmEnteredTime());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				long diff = date1.getTime() - date2.getTime();
				long diffSeconds = diff / 1000 % 60;
				long diffMinutes = diff / (60 * 1000) % 60;
				long diffHours = diff / (60 * 60 * 1000);
				int diffInDays = (int) ((diff) / (1000 * 60 * 60 * 24));
				if (diffInDays > 0 || diffHours > 0
						|| diffMinutes > Utils.CONSTANT_TIME_DIFF_BEACON) {
					System.out.println("Comparison Result:"
							+ "diff and minutes is " + diffMinutes);

					if (TextUtils.isEmpty(majors_and_minors)) {
						majors_and_minors = "\'" + beacon.getmMajor() + "_"
								+ beacon.getmMinor() + "\'";
					} else {
						majors_and_minors += "," + "\'" + beacon.getmMajor()
								+ "_" + beacon.getmMajor() + "\'";
					}
					try {
						int rowsAffected = buyopicDatabase
								.deleteExitedBeaconsRow(majors_and_minors);
						Log.i("BEACON", "deleteExitedBeacons  rowsAffected "
								+ rowsAffected);
						if (rowsAffected > 0) {
							notifyBeaconDataChanged("string");
						}

						buyopicNetworkServiceManager
								.sendSaveConsumerExitRequest(
										Constants.REQUEST_SEND_ENTRY_REQUEST,
										buyOpic.getmConsumerId(),
										beacon.getmBeaconUUID(),
										beacon.getmMajor(), beacon.getmMinor(),
										beacon.getmEnteredTime(),
										getFormattedTime(), beacon.getmRssi(),
										BackgroundMonitorService.this);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}

		}
	}

	private static String getFormattedTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss Z", Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat.format(new Date());
	}

	private void truncateTheBeaconInfo() {
		isBeaconAvailable = false;
		currentBeacon = null;
		buyOpic.setBeaconAvailable(false);
		buyOpic.setCurrentBeacon(currentBeacon);

		List<Beacon> beaconsList = buyopicDatabase.getAllBeaconDetails();
		if (beaconsList != null && !beaconsList.isEmpty()) {
			Log.i("BEACON",
					"truncateTheBeaconInfo beaconsList" + beaconsList.size()
							+ "beaconlist minor "
							+ beaconsList.get(0).getmMinor() + " uuid "
							+ beaconsList.get(0).getmBeaconUUID());
			sendExitTimeRequests(beaconsList);

		}
		buyopicDatabase.truncateDatabase();
		notifyBeaconDataChanged("string");
	}

	protected void printBeaconsInformation(List<Beacon> beacons) {

		if (beacons != null && !beacons.isEmpty()) {
			for (Beacon beacon : beacons) {
				Utils.showLog(beacon.getmMajor() + " and " + beacon.getmMinor()
						+ " Found In Database");
			}
		} else {
			Utils.showLog("No Records In Data base");
		}
	}

	public void sendNotification(String msg, String storeId, String retailerId,
			String storeName) {
		NotificationManager mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = new Intent(this, HomePageSetupActivity.class);
		intent.putExtra(KEY_ISFROM_NOTIFICATION, true);
		intent.putExtra(OffersListFragment.KEY_STORE_NAME, storeName);
		intent.putExtra(OffersListFragment.KEY_STORE_ID, storeId);
		intent.putExtra(OffersListFragment.KEY_RETAILER_ID, retailerId);
		PendingIntent pendingIntent = null;
		int id = (int) System.currentTimeMillis();
		pendingIntent = PendingIntent.getActivity(this, id, intent, 0);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.logo)
				.setContentTitle(getString(R.string.app_name))
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setAutoCancel(true).setContentText(msg);
		if (pendingIntent != null) {
			mBuilder.setContentIntent(pendingIntent);
		}
		@SuppressWarnings("resource")
		Scanner in = new Scanner(storeId).useDelimiter("[^0-9]+");
		long integer = in.nextLong();
		mNotificationManager.notify((int) integer, mBuilder.build());
	}
	public void sendOrderNotification(String msg,Context mContext,OrderStatus mOrderStatusObj) {
		NotificationManager mNotificationManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Log.i("NOTIFY", "mNotificationManager"+mNotificationManager);
		Intent intent = new Intent(mContext, HomePageSetupActivity.class);
		intent.putExtra(KEY_ISFROM_ORDER_NOTIFICATION, true);
		PendingIntent pendingIntent = null;
		int id = (int) System.currentTimeMillis();
		pendingIntent = PendingIntent.getActivity(mContext, id, intent, 0);
		Log.i("CLOSEBY", "sendOrderNotification"+msg);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				mContext).setSmallIcon(R.drawable.logo)
				.setContentTitle(mContext.getString(R.string.app_name))
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setAutoCancel(true).setContentText(msg);
		if (pendingIntent != null) {
			mBuilder.setContentIntent(pendingIntent);
		}
		@SuppressWarnings("resource")
		Scanner in = new Scanner(mOrderStatusObj.getStoreId()).useDelimiter("[^0-9]+");
		long integer = in.nextLong();
		mNotificationManager.notify((int) integer, mBuilder.build());
	}

	private boolean setBluetooth(boolean enable) {
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		boolean isEnabled = bluetoothAdapter.isEnabled();
		if (enable && !isEnabled) {
			return bluetoothAdapter.enable();
		} else if (!enable && isEnabled) {
			return bluetoothAdapter.disable();
		}
		return true;
	}

	@Override
	public void iBeaconDataUpdate(IBeacon arg0, IBeaconData arg1,
			DataProviderException arg2) {
		Utils.showLog("Major:" + arg0.getMajor() + "Minor:" + arg0.getMinor());
	}

	@Override
	public void onSuccess(int requestCode, Object object) {
		switch (requestCode) {
		case Constants.REQUEST_CHECK_BEACON_ALERTS:
			HashMap<String, String> hashMap = JsonResponseParser
					.parseCheckBeaconAlertResponse((String) object);
			if (hashMap != null && !hashMap.isEmpty()) {
				String storeId = hashMap.get(OffersListFragment.KEY_STORE_ID);
				String retailerId = hashMap
						.get(OffersListFragment.KEY_RETAILER_ID);
				String storeName = hashMap.get("store_name");
				/*sendNotification(
						"The " + storeName + " has something for you!",
						storeId, retailerId, storeName);*/
				if (currentBeacon != null) {
					Beacon beacon = buyopicDatabase.getBeaconDetails(
							String.valueOf(currentBeacon.getMajor()),
							String.valueOf(currentBeacon.getMinor()));
					if (beacon != null
							&& beacon.getIsNotified().equalsIgnoreCase("f")) {

						beacon.setIsNotified("t");
						// buyopicDatabase.insertItem(beacon);
					}
				}

			}
		default:
			break;
		}
	}

	@Override
	public void onFailure(int requestCode, String message) {

	}

	public int getDeviceState(Context mContext) {

		PowerManager powerManager = (PowerManager) mContext
				.getSystemService(Context.POWER_SERVICE);

		boolean devicemode = powerManager.isScreenOn();

		if (devicemode == true) {

			ActivityManager am = (ActivityManager) mContext
					.getSystemService(mContext.ACTIVITY_SERVICE);
			// The first in the list of RunningTasks is always the foreground
			// task.
			RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);

			String packagename = mContext.getApplicationContext()
					.getPackageName();

			String foregroundTaskPackageName = foregroundTaskInfo.topActivity
					.getPackageName();

			if (packagename.equalsIgnoreCase(foregroundTaskPackageName)) {
				mDeviceState = DeviceStatusConstants.YELLOW_APP_IN_USE;
			} else {
				mDeviceState = DeviceStatusConstants.YELLOW_APP_IN_BACKGROUND;
			}
		} else {
			mDeviceState = DeviceStatusConstants.DEVICE_ASLEEP;
		}
		return mDeviceState;
	}

	public void showBlueToothPopUp(Context mContext) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(mContext, HomePageSetupActivity.class);
		intent.putExtra(KEY_ISFROM_NEAREST_BEACON_DATA, true);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);
		
	}
	

}
