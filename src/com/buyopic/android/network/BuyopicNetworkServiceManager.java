package com.buyopic.android.network;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.PowerManager;

import com.buyopic.android.utils.Constants;
import com.buyopic.android.utils.DeviceStatusConstants;
import com.buyopic.android.utils.Utils;
import com.radiusnetworks.ibeacon.IBeacon;

public class BuyopicNetworkServiceManager {

	private static final String REQUEST_PARAM_REQUESTFROMBUYOPIC = "requestfrombuyopic";
	private static final String REQUEST_PARAM_PROCESS_ID = "process_id";
	private static final String REQUEST_URL_PROCESSURL = "processurl/yellow";
	private static final String REQUEST_PARAM_PROCESS_URL = "process_url";
	private static final String REQUEST_PARAM_PROCESS_TYPE = "process_type";
	private static final String REQUEST_URL_GENERATEURLPROCESSID = "generateurlprocessid";
	private static final String REQUEST_URL_UPDATECONSUMERBEACONDETAILS = "updateconsumerbeacondetails";
	private static final String REQUEST_URL_RESETCONSUMERPASSWORD = "resetconsumerpassword";
	private static final String REQUEST_URL_CONFIRMCONSUMERACCOUNT = "confirmconsumeraccount";
	private static final String REQUEST_URL_DELETESHAREDBYMEALERT = "deletesharedbymealert";
	private static final String REQUEST_URL_DELETESHAREDWITHMEALERT = "deletesharedwithmealert";
	private static final String REQUEST_URL_SHARED_TO_CONSUMER_ID = "shared_to_consumer_id";
	private static final String REQUEST_URL_SHARESTOREALERTCONFIRMATION = "sharestorealertconfirmation";
	private static final String REQUEST_URL_SHARED_BY_CONSUMER_ID = "shared_by_consumer_id";
	private static final String REQUEST_URL_ALERT_ID = "alert_id";
	private static final String REQUEST_URL_SHARESTOREALERT = "sharestorealert";
	private static final String REQUEST_PARAM_REQUESTED_CONSUMER_ID = "requested_consumer_id";
	private static final String REQUEST_URL_GETCONSUMERPOSTEDALERTS = "getconsumerpostedalerts";
	private static final String REQUEST_URL_GETSHAREDALERTSBYME = "getsharedalertsbyme";
	private static final String REQUEST_URL_GETSHAREDALERTSWITHME = "getsharedalertswithme";
	private static final String REQUEST_URL_GETPOSTEDSTOREALERTINFO = "getpostedstorealertinfo";
	private static final String REQUEST_URL_GETALERTDETAILSPOSTEDBYME = "getalertdetailspostedbyme";
	private static final String REQUEST_PARAM_GETMYLISTINGS = "getmylistings";
	private static final String REQUEST_PARAM_PRICE = "price";
	private static final String REQUEST_PARAM_STATUS = "status";
	private static final String REQUEST_PARAM_END_DATE = "end_date";
	private static final String REQUEST_PARAM_START_DATE = "start_date";
	private static final String REQUEST_PARAM_ALERT_MESSAGE = "alert_message";
	private static final String REQUEST_PARAM_ALERT_TITLE = "alert_title";
	private static final String REQUEST_PARAM_ALERT_THUMBNAIL_URL = "alert_thumbnail_url";
	private static final String REQUEST_PARAM_POSTED_ALERT_ID = "posted_alert_id";
	private static final String REQUEST_URL_UPDATEPOSTEDALERT = "updatepostedalert";
	private static final String REQUEST_URL_POSTSTOREALERT = "poststorealert";
	private static final String REQUEST_PARAM_CONSUMER_FAVORITE = "consumer_favorite";
	private static final String REQUEST_PARAM_STORE_ALERT_ID = "store_alert_id";
	private static final String REQUEST_PARAM_UPDATEALERTFAVORITESTATUS = "updatealertfavoritestatus";
	private static final String REQUEST_PARAM_PHONE_NUMBER = "phone_number";
	private static final String REQUEST_URL_CONSUMERDEFAULTREGISTRATION = "consumerdefaultregistration";
	private static final String REQUEST_PARAM_EXIT_TIME = "exit_time";
	private static final String REQUEST_URL_UPDATECONSUMEREXITTIME = "updateconsumerexittime";
	private static final String REQUEST_PARAM_RSSI_LEVEL = "rssi_level";
	private static final String REQUEST_PARAM_ENTRY_TIME = "entry_time";
	private static final String REQUEST_PARAM_BEACON_MINOR = "beacon_minor";
	private static final String REQUEST_PARAM_BEACON_MAJOR = "beacon_major";
	private static final String REQUEST_PARAM_BEACON_UUID = "beacon_uuid";
	private static final String REQUEST_URL_SAVECONSUMERENTRYTIME = "saveconsumerentrytime";
	private static final String REQUEST_URL_GETSEARCHHISTORY = "getsearchhistory";
	private static final String REQUEST_PARAM_SEARCH_STRING = "search_string";
	private static final String REQUEST_URL_SEARCHALERTS = "searchalerts";
	private static final String REQUEST_URL_CHECKBEACONALERTS = "checkbeaconalerts";
	private static final String REQUEST_PARAM_IMAGE_FILE = "image_file";
	private static final String REQUEST_PARAM_IMAGE_NAME = "image_name";
	private static final String REQUEST_PARAM_COUNTRY = "country";
	private static final String REQUEST_PARAM_STATE = "state";
	private static final String REQUEST_PARAM_CITY = "city";
	private static final String REQUEST_ADDRESS_LINE1 = "address_line1";
	private static final String REQUEST_USER_NAME = "user_name";
	private static final String REQUEST_URL_UPDATECONSUMERACCOUNT = "updateconsumeraccount";
	private static final String REQUEST_URL_COMPLETECONSUMERREGISTRATION = "completeconsumerregistration";
	private static final String REQUEST_PARAM_GETMYFAVORITEOFFERSLIST = "getmyfavoriteofferslist";
	private static final String REQUEST_PARAM_PASSWORD = "password";
	private static final String REQUEST_PARAM_EMAIL_ID = "email_id";
	private static final String REQUEST_URL_CONSUMERLOGIN = "consumerlogin";
	private static final String REQUEST_PARAM_BUYOPIC_STORE_ALERT_ID = "buyopic_store_alert_id";
	private static final String REQUEST_URL_STOREALERTINFO = "storealertinfo";
	private static final String MSG_NO = "No";
	private static final String MSG_YES = "Yes";
	private static final String TAG_CLICK_REQUEST = "click_request";
	private static final String REQUEST_PARAM_USE_BEACON = "useBeacon";
	private static final String REQUEST_PARAM_LONGITUDE = "longitude";
	private static final String REQUEST_PARAM_LATITUDE = "latitude";
	private static final String REQUEST_URL_MYTRAILOFFERSLIST = "mytrailofferslist";
	private static final String REQUEST_URL_CHECKFORNEWALERTS = "checkfornewalerts";
	private static final String REQUEST_URL_GETNEAREST_STORES = "getneareststores";
	private static final String REQUEST_URL_GETNEAREST_STORES_NEW = "getNearestAlerts";
	private static final String REQUEST_PARAM_POSTED_CONSUMER_ID = "posted_consumer_id";
	private static final String REQUEST_PARAM_CONSUMER_ID = "consumer_id";
	private static final String REQUEST_PARAM_RETAILER_ID = "retailer_id";
	private static final String REQUEST_PARAM_STORE_ID = "store_id";
	private static final String REQUEST_PARAM_PHONE_NO = "phone_no";
	private static final String REQUEST_GOOGLE_PLCAE_ID = "google_place_id";
	private static final String REQUEST_GOOGLE_ICON_IMAGE = "google_icon_image";
	private static final String REQUEST_ADDRESS_IMAGENAME = "address_image_name";
	private static final String REQUEST_ADDRESS_IMAGEFILE = "address_image_file";
	private static final String REQUEST_ADDRESS_LINE2 = "address_line2";
	private static final String REQUEST_ADDRESS_POSTALCODE = "postal_code";

	private static final String REQUEST_PARAM_MAJOR = "major";
	private static final String REQUEST_PARAM_MINOR = "minor";
	private static final String REQUEST_PARAM_BEACON_ID = "beacon_id";
	private static final String REQUEST_PARAM_DEVICE_STATE = "device_state";
	private static final String REQUEST_PARAM_NEXT_COMPUTATION_TIME = "next_computation_time";

	private static final String REQUEST_URL_GETSTOREALERTS = "getstorealerts";
	private static final String REQUEST_URL_GETCONSUMERDETAILS = "getConsumerDetails";

	public static long INTERVAL_CHECK_FOR_NEW_ALERTS = 60 * 1000l;
	public static long BACKGROUND_BETWEEN_SCAN_PERIOD = 15 * 1000l;
	public static long BACKGROUND_SCAN_PERIOD = 2 * 1000l;
	public static String NEXT_RUNTIME = "";
	public static long INTERVAL_CHECK_FOR_NEW_ALERTS_CLOSEBY = 15 * 1000l;
	public static boolean TURN_BLUETOOTH_ON=false;
	public static boolean TURN_BLUETOOTH_OFF=false;

	// OrderRequests
	private static final String REQUEST_URL_SUBMITNEWORDER = "submitNewOrder";

	//public final String HTTPS_BASE_URL = "https://cloud-ml.herokuapp.com/";
	//public static final String BASE_URL = "http://cloud-ml.herokuapp.com/";

	 public final String HTTPS_BASE_URL ="https://cloud-ml-client-demo-2.herokuapp.com/";
	 public static final String BASE_URL ="http://cloud-ml-client-demo-2.herokuapp.com/";

	// public final String HTTPS_BASE_URL =
	// "https://cloud-ml-client-demo.herokuapp.com/";
	// public static final String BASE_URL =
	// "http://cloud-ml-client-demo.herokuapp.com/";

	// public final String HTTPS_BASE_URL ="https://prod-yellow.herokuapp.com/";
	// public static final String BASE_URL ="http://prod-yellow.herokuapp.com/";

	//
	public static final String TAG = BuyopicNetworkServiceManager.class
			.getSimpleName();
	private static final String REQUEST_PARAM_OFFER_ITEM_ID = "offer_item_id";
	private static final String REQUEST_PARAM_ORDER_QUANTITY = "order_quantity";
	private static final String REQUEST_PARAM_OFFER_ITEM_PRICE = "offer_item_price";
	private static final String REQUEST_ADDRESS_LINE3 = "address_line3";
	private static final String REQUEST_PARAM_PINCODE = "pincode";
	private static final String REQUEST_PARAM_REQUESTED_DELIVERY_DATE = "requested_delivery_date";
	private static final String REQUEST_URL_GETORDERSDETAILS = "consumerOrders";
	private ExecutorService executorService;
	static BuyopicNetworkServiceManager buyopicNetworkServiceManager = null;

	public static BuyopicNetworkServiceManager getInstance(Context context) {
		return buyopicNetworkServiceManager == null ? new BuyopicNetworkServiceManager(
				context) : buyopicNetworkServiceManager;
	}

	public BuyopicNetworkServiceManager(Context context) {
		executorService = Executors.newFixedThreadPool(4);
	}

	public void sendYelpReviewsRequest(final int requestCode,
			final String businessId, BuyopicNetworkCallBack callBack) {

		final BuyopicRequestHandler buyopicHandler = new BuyopicRequestHandler(
				requestCode, callBack);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				HttpRestConn.getYelpReviewResponse(businessId, buyopicHandler);

			}
		});
	}

	public void sendgetStoreAlertsRequest(final int requestCode,
			final String storeId, String retailerId, String consumerId,
			String mPostedConsumerId, BuyopicNetworkCallBack callBack) {
		String postedConsumerId = "";
		if (mPostedConsumerId != null) {
			postedConsumerId = mPostedConsumerId;
		}
		if (mPostedConsumerId == null
				|| mPostedConsumerId.equalsIgnoreCase("null")) {
			postedConsumerId = "";
		}
		final String url = Uri
				.parse(BASE_URL + REQUEST_URL_GETSTOREALERTS)
				.buildUpon()
				.appendQueryParameter(REQUEST_PARAM_STORE_ID, storeId)
				.appendQueryParameter(REQUEST_PARAM_RETAILER_ID,
						String.valueOf(retailerId))
				.appendQueryParameter(REQUEST_PARAM_CONSUMER_ID,
						String.valueOf(consumerId))
				.appendQueryParameter(REQUEST_PARAM_POSTED_CONSUMER_ID,
						String.valueOf(postedConsumerId)).build().toString();
		final BuyopicRequestHandler buyopicHandler = new BuyopicRequestHandler(
				requestCode, callBack);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				HttpRestConn.GetRequestData(url, buyopicHandler);

			}
		});
	}

	public void sendNearestStoreAlertsRequest(final int requestCode,
			final String beaconId, final String major, final String minor,
			final String latitude, final String longitude,
			final String consumerId, final String useBeacon,
			String checkForNew, boolean isCloseByClicked,
			BuyopicNetworkCallBack callBack, Context context) {
		String url = "";
		if (requestCode == Constants.REQUEST_NEAREST_STORE_ALERTS) {
			url = BASE_URL + REQUEST_URL_GETNEAREST_STORES;
		} else if (requestCode == Constants.REQUEST_CHECK_FOR_NEW_DATA) {
			url = BASE_URL + REQUEST_URL_CHECKFORNEWALERTS;
		} else if (requestCode == Constants.REQUEST_MYTRAILS) {
			url = BASE_URL + REQUEST_URL_MYTRAILOFFERSLIST;
		}
		final String totalUrl;
		if (requestCode == Constants.REQUEST_CHECK_FOR_NEW_DATA) {

			/*
			 * totalUrl = Uri .parse(url) .buildUpon()
			 * .appendQueryParameter(REQUEST_PARAM_BEACON_ID, beaconId)
			 * .appendQueryParameter(REQUEST_PARAM_MAJOR, String.valueOf(major))
			 * .appendQueryParameter(REQUEST_PARAM_MINOR, String.valueOf(minor))
			 * .appendQueryParameter(REQUEST_PARAM_LATITUDE,
			 * String.valueOf(latitude))
			 * .appendQueryParameter(REQUEST_PARAM_LONGITUDE,
			 * String.valueOf(longitude))
			 * .appendQueryParameter(REQUEST_PARAM_CONSUMER_ID,
			 * String.valueOf(consumerId))
			 * .appendQueryParameter(REQUEST_PARAM_USE_BEACON,
			 * String.valueOf(useBeacon))
			 * .appendQueryParameter("checkForNewAlerts", checkForNew)
			 * .build().toString();
			 */

			Builder uriBuilder = Uri
					.parse(url)
					.buildUpon()
					.appendQueryParameter(REQUEST_PARAM_BEACON_ID, beaconId)
					.appendQueryParameter(REQUEST_PARAM_MAJOR,
							String.valueOf(major))
					.appendQueryParameter(REQUEST_PARAM_MINOR,
							String.valueOf(minor))
					.appendQueryParameter(REQUEST_PARAM_LATITUDE,
							String.valueOf(latitude))
					.appendQueryParameter(REQUEST_PARAM_LONGITUDE,
							String.valueOf(longitude))
					.appendQueryParameter(REQUEST_PARAM_CONSUMER_ID,
							String.valueOf(consumerId))
					.appendQueryParameter(REQUEST_PARAM_USE_BEACON,
							String.valueOf(useBeacon))
					.appendQueryParameter("checkForNewAlerts", checkForNew);

			if (context != null)
				uriBuilder = appendUriBuilder(context, uriBuilder);

			totalUrl = uriBuilder.build().toString();

		} else {
			url = BASE_URL + REQUEST_URL_GETNEAREST_STORES_NEW;
			totalUrl = Uri
					.parse(url)
					.buildUpon()
					.appendQueryParameter(REQUEST_PARAM_BEACON_ID, beaconId)
					.appendQueryParameter(REQUEST_PARAM_MAJOR,
							String.valueOf(major))
					.appendQueryParameter(REQUEST_PARAM_MINOR,
							String.valueOf(minor))
					.appendQueryParameter(REQUEST_PARAM_LATITUDE,
							String.valueOf(latitude))
					.appendQueryParameter(REQUEST_PARAM_LONGITUDE,
							String.valueOf(longitude))
					.appendQueryParameter(REQUEST_PARAM_CONSUMER_ID,
							String.valueOf(consumerId))
					.appendQueryParameter(REQUEST_PARAM_USE_BEACON,
							String.valueOf(useBeacon))
					.appendQueryParameter(TAG_CLICK_REQUEST,
							isCloseByClicked ? MSG_YES : MSG_NO).build()
					.toString();
		}

		final BuyopicRequestHandler buyopicHandler = new BuyopicRequestHandler(
				requestCode, callBack);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				HttpRestConn.GetRequestData(totalUrl, buyopicHandler);
			}
		});
	}

	private Builder appendUriBuilder(Context context, Builder uriBuilder) {
		// TODO Auto-generated method stub
		int mDeviceState;

		PowerManager powerManager = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);

		boolean devicemode = powerManager.isScreenOn();

		if (devicemode == true) {

			ActivityManager am = (ActivityManager) context
					.getSystemService(context.ACTIVITY_SERVICE);
			// The first in the list of RunningTasks is always the foreground
			// task.
			RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);

			String packagename = context.getApplicationContext()
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

		uriBuilder.appendQueryParameter(REQUEST_PARAM_DEVICE_STATE,
				String.valueOf(mDeviceState)).appendQueryParameter(
				REQUEST_PARAM_NEXT_COMPUTATION_TIME, NEXT_RUNTIME);

		return uriBuilder;

	}

	public void sendAlertDetailsRequest(final int requestCode,
			final String mConsumerId, String mAlertId, String storeId,
			String retailerId, BuyopicNetworkCallBack callBack) {
		final String totalurl = Uri
				.parse(BASE_URL + REQUEST_URL_STOREALERTINFO)
				.buildUpon()
				.appendQueryParameter(REQUEST_PARAM_BUYOPIC_STORE_ALERT_ID,
						mAlertId)
				.appendQueryParameter(REQUEST_PARAM_CONSUMER_ID, mConsumerId)
				.appendQueryParameter(REQUEST_PARAM_STORE_ID, storeId)
				.appendQueryParameter(REQUEST_PARAM_RETAILER_ID, retailerId)
				.appendQueryParameter(REQUEST_PARAM_POSTED_CONSUMER_ID, "")
				.build().toString();
		final BuyopicRequestHandler buyopicHandler = new BuyopicRequestHandler(
				requestCode, callBack);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				HttpRestConn.GetRequestData(totalurl, buyopicHandler);
			}
		});
	}

	public void sendMyFavoritesListRequest(final int requestCode,
			final String mConsumerId, final String mLatitude,
			final String mLongitude, BuyopicNetworkCallBack callBack) {

		final String totalUrl = Uri
				.parse(BASE_URL + REQUEST_PARAM_GETMYFAVORITEOFFERSLIST)
				.buildUpon()
				.appendQueryParameter(REQUEST_PARAM_BEACON_ID, "")
				.appendQueryParameter(REQUEST_PARAM_MAJOR, String.valueOf(""))
				.appendQueryParameter(REQUEST_PARAM_MINOR, String.valueOf(""))
				.appendQueryParameter(REQUEST_PARAM_LATITUDE,
						String.valueOf(mLatitude))
				.appendQueryParameter(REQUEST_PARAM_LONGITUDE,
						String.valueOf(mLongitude))
				.appendQueryParameter(REQUEST_PARAM_CONSUMER_ID,
						String.valueOf(mConsumerId))
				.appendQueryParameter(REQUEST_PARAM_USE_BEACON,
						String.valueOf("NO")).build().toString();
		final BuyopicRequestHandler buyopicHandler = new BuyopicRequestHandler(
				requestCode, callBack);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				HttpRestConn.GetRequestData(totalUrl, buyopicHandler);

			}
		});
	}

	public void sendConsumerLoginRequest(final int requestCode,
			final String mConsumerEmail, String mConsumerPassword,
			BuyopicNetworkCallBack callBack) {
		final String url = HTTPS_BASE_URL + REQUEST_URL_CONSUMERLOGIN;
		final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_EMAIL_ID,
				mConsumerEmail));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_PASSWORD,
				mConsumerPassword));
		final BuyopicRequestHandler buyopicHandler = new BuyopicRequestHandler(
				requestCode, callBack);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				HttpRestConn.postRequestData(url, nameValuePairs,
						buyopicHandler);

			}
		});
	}

	public void sendConsumerRegistrationRequest(final int requestCode,
			boolean isFromUpdate, final String mConsumerEmail,
			String mConsumerPassword, String mConsumerUserName,
			String latitude, String longitude, String street, String city,
			String state, String country, String consumerId, String imageName,
			String imageFile, String phoneNumber, String placeid,
			String iconurl, String addressImageName, String addressImageFile,
			String addressline2, String postalcode,
			BuyopicNetworkCallBack callBack) {
		final String url;
		if (!isFromUpdate) {
			url = Uri
					.parse(HTTPS_BASE_URL
							+ REQUEST_URL_COMPLETECONSUMERREGISTRATION)
					.buildUpon().build().toString();
		} else {
			url = Uri.parse(HTTPS_BASE_URL + REQUEST_URL_UPDATECONSUMERACCOUNT)
					.buildUpon().build().toString();
		}
		final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_EMAIL_ID,
				mConsumerEmail));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_PASSWORD,
				mConsumerPassword));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_USER_NAME,
				mConsumerUserName));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_LONGITUDE,
				longitude));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_LATITUDE,
				latitude));
		nameValuePairs
				.add(new BasicNameValuePair(REQUEST_ADDRESS_LINE1, street));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_CITY, city));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_STATE, state));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_COUNTRY,
				country));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_CONSUMER_ID,
				consumerId));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_IMAGE_NAME,
				imageName));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_IMAGE_FILE,
				imageFile));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_PHONE_NO,
				phoneNumber));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_GOOGLE_PLCAE_ID,
				placeid));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_GOOGLE_ICON_IMAGE,
				iconurl));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_ADDRESS_IMAGENAME,
				addressImageName));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_ADDRESS_IMAGEFILE,
				addressImageFile));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_ADDRESS_LINE2,
				addressline2));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_ADDRESS_POSTALCODE,
				postalcode));
		printNameValuePairs(nameValuePairs);

		final BuyopicRequestHandler buyopicHandler = new BuyopicRequestHandler(
				requestCode, callBack);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				HttpRestConn.postRequestData(url, nameValuePairs,
						buyopicHandler);
			}
		});
	}

	public void sendCheckBeaconAlertsRequest(final int requestCode,
			final String mBeaconUUID, final String mMinor, final String mMajor,
			BuyopicNetworkCallBack callBack) {
		final String totalurl = Uri
				.parse(BASE_URL + REQUEST_URL_CHECKBEACONALERTS).buildUpon()
				.appendQueryParameter(REQUEST_PARAM_BEACON_ID, mBeaconUUID)
				.appendQueryParameter(REQUEST_PARAM_MAJOR, mMajor)
				.appendQueryParameter(REQUEST_PARAM_MINOR, mMinor).build()
				.toString();
		final BuyopicRequestHandler buyopicHandler = new BuyopicRequestHandler(
				requestCode, callBack);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				HttpRestConn.GetRequestData(totalurl, buyopicHandler);

			}
		});
	}

	public void sendSearchRequest(final int requestCode,
			final String mConsumerId, final String searchKey, String latitude,
			String longitude, BuyopicNetworkCallBack callBack) {
		final String totalurl = Uri.parse(BASE_URL + REQUEST_URL_SEARCHALERTS)
				.buildUpon()
				.appendQueryParameter(REQUEST_PARAM_SEARCH_STRING, searchKey)
				.appendQueryParameter(REQUEST_PARAM_CONSUMER_ID, mConsumerId)
				.appendQueryParameter(REQUEST_PARAM_LATITUDE, latitude)
				.appendQueryParameter(REQUEST_PARAM_LONGITUDE, longitude)
				.build().toString();

		final BuyopicRequestHandler buyopicHandler = new BuyopicRequestHandler(
				requestCode, callBack);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				HttpRestConn.GetRequestData(totalurl, buyopicHandler);

			}
		});

	}

	public void sendSearchHistoryRequest(final int requestCode,
			final String mConsumerId, BuyopicNetworkCallBack callBack) {
		final String totalurl = Uri
				.parse(BASE_URL + REQUEST_URL_GETSEARCHHISTORY).buildUpon()
				.appendQueryParameter(REQUEST_PARAM_CONSUMER_ID, mConsumerId)
				.build().toString();

		final BuyopicRequestHandler buyopicHandler = new BuyopicRequestHandler(
				requestCode, callBack);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				HttpRestConn.GetRequestData(totalurl, buyopicHandler);

			}
		});

	}

	public void sendSaveConsumerEntryRequest(final int requestCode,
			final String mConsumerId, String beaconUUID, String major,
			String minor, String entryTime, String rssi,
			BuyopicNetworkCallBack callBack) {
		final String url = Uri
				.parse(BASE_URL + REQUEST_URL_SAVECONSUMERENTRYTIME)
				.buildUpon().build().toString();
		final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_CONSUMER_ID,
				mConsumerId));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_BEACON_UUID,
				beaconUUID));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_BEACON_MAJOR,
				major));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_BEACON_MINOR,
				minor));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_ENTRY_TIME,
				entryTime));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_RSSI_LEVEL,
				rssi));
		printNameValuePairs(nameValuePairs);
		final BuyopicRequestHandler buyopicHandler = new BuyopicRequestHandler(
				requestCode, callBack);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				HttpRestConn.postRequestData(url, nameValuePairs,
						buyopicHandler);

			}
		});

	}

	public void sendSaveConsumerExitRequest(final int requestCode,
			final String mConsumerId, String beaconUUID, String major,
			String minor, String entryTime, String exitTime, String rssi,
			BuyopicNetworkCallBack callBack) {
		final String url = Uri
				.parse(BASE_URL + REQUEST_URL_UPDATECONSUMEREXITTIME)
				.buildUpon().build().toString();
		final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_CONSUMER_ID,
				mConsumerId));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_BEACON_UUID,
				beaconUUID));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_BEACON_MAJOR,
				major));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_BEACON_MINOR,
				minor));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_ENTRY_TIME,
				entryTime));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_EXIT_TIME,
				exitTime));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_RSSI_LEVEL,
				rssi));
		printNameValuePairs(nameValuePairs);
		final BuyopicRequestHandler buyopicHandler = new BuyopicRequestHandler(
				requestCode, callBack);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				HttpRestConn.postRequestData(url, nameValuePairs,
						buyopicHandler);

			}
		});

	}

	public void sendConsumerRegistrationRequest(int requestCode, String email,
			String phoneNumber, String lat, String longitude,
			BuyopicNetworkCallBack callBack) {
		final String url = Uri
				.parse(HTTPS_BASE_URL + REQUEST_URL_CONSUMERDEFAULTREGISTRATION)
				.buildUpon().build().toString();
		final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs
				.add(new BasicNameValuePair(REQUEST_PARAM_EMAIL_ID, email));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_LATITUDE, lat));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_LONGITUDE,
				longitude));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_PHONE_NUMBER,
				phoneNumber));

		Utils.showLog("-------------------------------------------");
		Utils.showLog("Default Consumer Registration-->" + url);
		printNameValuePairs(nameValuePairs);
		Utils.showLog("-------------------------------------------");

		final BuyopicRequestHandler buyopicHandler = new BuyopicRequestHandler(
				requestCode, callBack);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				HttpRestConn.postRequestData(url, nameValuePairs,
						buyopicHandler);

			}
		});
	}

	public void sendFavoriteRequest(int requestCode, String consumerId,
			String storeAlertId, boolean isFavorite,
			BuyopicNetworkCallBack buyopicNetworkCallBack) {
		final String url = Uri
				.parse(BASE_URL + REQUEST_PARAM_UPDATEALERTFAVORITESTATUS)
				.buildUpon().build().toString();
		final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_CONSUMER_ID,
				consumerId));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_STORE_ALERT_ID,
				storeAlertId));
		nameValuePairs
				.add(new BasicNameValuePair(REQUEST_PARAM_CONSUMER_FAVORITE,
						isFavorite ? MSG_YES : MSG_NO));

		final BuyopicRequestHandler buyopicHandler = new BuyopicRequestHandler(
				requestCode, buyopicNetworkCallBack);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				HttpRestConn.postRequestData(url, nameValuePairs,
						buyopicHandler);

			}
		});
	}

	public void sendConsumerCreateOffersDataRequest(final int requestCode,
			final String mAlertId, boolean isInEditMode,
			final String mConsumerId, final String mOfferTitle,
			final String mOfferDescription, final String mStartDate,
			final String mEndDate, final String mPrice,
			final String activeStatus, final String imageName,
			final String encodedImageString, String mThumbnailPath,
			BuyopicNetworkCallBack callBack) {
		final String url;
		if (!isInEditMode) {
			url = BASE_URL + REQUEST_URL_POSTSTOREALERT;
		} else {
			url = BASE_URL + REQUEST_URL_UPDATEPOSTEDALERT;
		}

		final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		if (isInEditMode) {
			nameValuePairs.add(new BasicNameValuePair(
					REQUEST_PARAM_POSTED_ALERT_ID, mAlertId));
			nameValuePairs.add(new BasicNameValuePair(
					REQUEST_PARAM_ALERT_THUMBNAIL_URL, mThumbnailPath));
		}
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_CONSUMER_ID,
				mConsumerId));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_ALERT_TITLE,
				mOfferTitle));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_ALERT_MESSAGE,
				mOfferDescription));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_START_DATE,
				mStartDate));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_END_DATE,
				mEndDate));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_STATUS, String
				.valueOf(activeStatus)));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_PRICE, mPrice));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_IMAGE_NAME,
				imageName));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_IMAGE_FILE,
				encodedImageString));

		printNameValuePairs(nameValuePairs);

		final BuyopicRequestHandler buyopicHandler = new BuyopicRequestHandler(
				requestCode, callBack);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				HttpRestConn.postRequestData(url, nameValuePairs,
						buyopicHandler);
			}
		});
	}

	public void sendMyListingsRequest(final int requestCode,
			String mConsumerId, BuyopicNetworkCallBack back) {
		final String totalurl = Uri
				.parse(BASE_URL + REQUEST_PARAM_GETMYLISTINGS).buildUpon()
				.appendQueryParameter(REQUEST_PARAM_CONSUMER_ID, mConsumerId)
				.build().toString();
		final BuyopicRequestHandler buyopicHandler = new BuyopicRequestHandler(
				requestCode, back);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				HttpRestConn.GetRequestData(totalurl, buyopicHandler);

			}
		});

	}

	public void sendConsumerAlertDetailsPostedByMeRequest(
			final int requestCode, String mConsumerId, String mAlertId,
			BuyopicNetworkCallBack buyopicNetworkCallBack) {
		final String totalurl = Uri
				.parse(BASE_URL + REQUEST_URL_GETALERTDETAILSPOSTEDBYME)
				.buildUpon()
				.appendQueryParameter(REQUEST_PARAM_CONSUMER_ID, mConsumerId)
				.appendQueryParameter(REQUEST_PARAM_POSTED_ALERT_ID, mAlertId)
				.build().toString();
		final BuyopicRequestHandler buyopicHandler = new BuyopicRequestHandler(
				requestCode, buyopicNetworkCallBack);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				HttpRestConn.GetRequestData(totalurl, buyopicHandler);

			}
		});
	}

	public void sendConsumerPostedAlertDetailsRequest(final int requestCode,
			String mPostedConsumerId, String mAlertId, String storeId,
			String retailerId, String mConsumerIda,
			BuyopicNetworkCallBack buyopicNetworkCallBack) {
		final String totalurl = Uri
				.parse(BASE_URL + REQUEST_URL_GETPOSTEDSTOREALERTINFO)
				.buildUpon()
				.appendQueryParameter(REQUEST_PARAM_CONSUMER_ID, mConsumerIda)
				.appendQueryParameter(REQUEST_PARAM_POSTED_ALERT_ID, mAlertId)
				.appendQueryParameter(REQUEST_PARAM_STORE_ID, "")
				.appendQueryParameter(REQUEST_PARAM_RETAILER_ID, "")
				.appendQueryParameter(REQUEST_PARAM_POSTED_CONSUMER_ID,
						mPostedConsumerId).build().toString();
		final BuyopicRequestHandler buyopicHandler = new BuyopicRequestHandler(
				requestCode, buyopicNetworkCallBack);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				HttpRestConn.GetRequestData(totalurl, buyopicHandler);

			}
		});
	}

	public void sendListSharedWithMeRequest(final int requestCode,
			String mConsumerId, BuyopicNetworkCallBack back) {
		final String totalurl = Uri
				.parse(BASE_URL + REQUEST_URL_GETSHAREDALERTSWITHME)
				.buildUpon()
				.appendQueryParameter(REQUEST_PARAM_CONSUMER_ID, mConsumerId)
				.build().toString();
		final BuyopicRequestHandler buyopicHandler = new BuyopicRequestHandler(
				requestCode, back);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				HttpRestConn.GetRequestData(totalurl, buyopicHandler);

			}
		});

	}

	public void sendListSharedByMeRequest(final int requestCode,
			String mConsumerId, BuyopicNetworkCallBack back) {
		final String totalurl = Uri
				.parse(BASE_URL + REQUEST_URL_GETSHAREDALERTSBYME).buildUpon()
				.appendQueryParameter(REQUEST_PARAM_CONSUMER_ID, mConsumerId)
				.build().toString();
		final BuyopicRequestHandler buyopicHandler = new BuyopicRequestHandler(
				requestCode, back);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				HttpRestConn.GetRequestData(totalurl, buyopicHandler);

			}
		});

	}

	public void sendConsumerPostedAlertsRequest(final int requestCode,
			String mLoggedInConsumerId, String mOfferPostedConsumerId,
			BuyopicNetworkCallBack back) {
		final String totalurl = Uri
				.parse(BASE_URL + REQUEST_URL_GETCONSUMERPOSTEDALERTS)
				.buildUpon()
				.appendQueryParameter(REQUEST_PARAM_POSTED_CONSUMER_ID,
						mOfferPostedConsumerId)
				.appendQueryParameter(REQUEST_PARAM_STORE_ID, "")
				.appendQueryParameter(REQUEST_PARAM_RETAILER_ID, "")
				.appendQueryParameter(REQUEST_PARAM_REQUESTED_CONSUMER_ID,
						mLoggedInConsumerId).build().toString();
		final BuyopicRequestHandler buyopicHandler = new BuyopicRequestHandler(
				requestCode, back);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				HttpRestConn.GetRequestData(totalurl, buyopicHandler);

			}
		});

	}

	public void sendShareStoreAlertRequest(int requestCode, String consumerId,
			String storeAlertId, BuyopicNetworkCallBack buyopicNetworkCallBack) {
		final String url = Uri.parse(BASE_URL + REQUEST_URL_SHARESTOREALERT)
				.buildUpon().build().toString();

		final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair(REQUEST_URL_ALERT_ID,
				storeAlertId));
		nameValuePairs.add(new BasicNameValuePair(
				REQUEST_URL_SHARED_BY_CONSUMER_ID, consumerId));

		final BuyopicRequestHandler buyopicHandler = new BuyopicRequestHandler(
				requestCode, buyopicNetworkCallBack);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				HttpRestConn.postRequestData(url, nameValuePairs,
						buyopicHandler);

			}
		});
	}

	public void sendShareStoreAlertConfimationRequest(int requestCode,
			String consumerId, String storeAlertId,
			String shared_To_ConsumerId,
			BuyopicNetworkCallBack buyopicNetworkCallBack) {
		final String url = Uri
				.parse(BASE_URL + REQUEST_URL_SHARESTOREALERTCONFIRMATION)
				.buildUpon().build().toString();
		Utils.showLog(url);
		final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair(REQUEST_URL_ALERT_ID,
				storeAlertId));
		nameValuePairs.add(new BasicNameValuePair(
				REQUEST_URL_SHARED_BY_CONSUMER_ID, consumerId));
		nameValuePairs.add(new BasicNameValuePair(
				REQUEST_URL_SHARED_TO_CONSUMER_ID, shared_To_ConsumerId));

		final BuyopicRequestHandler buyopicHandler = new BuyopicRequestHandler(
				requestCode, buyopicNetworkCallBack);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				HttpRestConn.postRequestData(url, nameValuePairs,
						buyopicHandler);

			}
		});
	}

	public void sendDeleteItemFromSharedWithMeRequest(int requestCode,
			boolean isFromSharedWithMe, String consumerId, String alertId,
			BuyopicNetworkCallBack back) {
		final String url;
		if (isFromSharedWithMe) {
			url = Uri.parse(BASE_URL + REQUEST_URL_DELETESHAREDWITHMEALERT)
					.buildUpon().build().toString();
		} else {
			url = Uri.parse(BASE_URL + REQUEST_URL_DELETESHAREDBYMEALERT)
					.buildUpon().build().toString();
		}

		final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs
				.add(new BasicNameValuePair(REQUEST_URL_ALERT_ID, alertId));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_CONSUMER_ID,
				consumerId));

		final BuyopicRequestHandler buyopicHandler = new BuyopicRequestHandler(
				requestCode, back);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				HttpRestConn.postRequestData(url, nameValuePairs,
						buyopicHandler);

			}
		});
	}

	public void sendMerchantConfirmAssociateAccount(int requestCode,
			String emailId, String associateId, BuyopicNetworkCallBack back) {
		final String url = Uri
				.parse(BASE_URL + REQUEST_URL_CONFIRMCONSUMERACCOUNT)
				.buildUpon().build().toString();

		final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_EMAIL_ID,
				emailId));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_CONSUMER_ID,
				associateId));

		final BuyopicRequestHandler buyopicHandler = new BuyopicRequestHandler(
				requestCode, back);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				HttpRestConn.postRequestData(url, nameValuePairs,
						buyopicHandler);

			}
		});
	}

	public void sendResetPasswordRequest(int requestCode, String emailId,
			BuyopicNetworkCallBack back) {
		final String url = Uri
				.parse(BASE_URL + REQUEST_URL_RESETCONSUMERPASSWORD)
				.buildUpon().build().toString();

		final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_EMAIL_ID,
				emailId));

		final BuyopicRequestHandler buyopicHandler = new BuyopicRequestHandler(
				requestCode, back);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				HttpRestConn.postRequestData(url, nameValuePairs,
						buyopicHandler);

			}
		});
	}

	public void sendUpdateBeaconRssiLevelsRequest(int requestCode,
			String consumerId, IBeacon iBeacon,
			BuyopicNetworkCallBack buyopicNetworkCallBack) {

		final String url = Uri
				.parse(BASE_URL + REQUEST_URL_UPDATECONSUMERBEACONDETAILS)
				.buildUpon().build().toString();

		final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_CONSUMER_ID,
				consumerId));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_BEACON_UUID,
				iBeacon.getProximityUuid()));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_BEACON_MAJOR,
				String.valueOf(iBeacon.getMajor())));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_BEACON_MINOR,
				String.valueOf(iBeacon.getMinor())));
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_RSSI_LEVEL,
				String.valueOf(iBeacon.getRssi())));
		printNameValuePairs(nameValuePairs);

		final BuyopicRequestHandler buyopicHandler = new BuyopicRequestHandler(
				requestCode, buyopicNetworkCallBack);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				HttpRestConn.postRequestData(url, nameValuePairs,
						buyopicHandler);

			}
		});
	}

	public void sendGenerateUrlProcessIdRequest(int requestCode,
			String processUrl, BuyopicNetworkCallBack buyopicNetworkCallBack) {
		final String url = Uri
				.parse(BASE_URL + REQUEST_URL_GENERATEURLPROCESSID).buildUpon()
				.build().toString();

		final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		if (requestCode == Constants.REQUEST_GENERATE_URL) {
			nameValuePairs.add(new BasicNameValuePair(
					REQUEST_PARAM_PROCESS_TYPE, "share"));
		}
		nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_PROCESS_URL,
				processUrl));
		printNameValuePairs(nameValuePairs);

		final BuyopicRequestHandler buyopicHandler = new BuyopicRequestHandler(
				requestCode, buyopicNetworkCallBack);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				HttpRestConn.postRequestData(url, nameValuePairs,
						buyopicHandler);

			}
		});
	}

	public void sendUrlProcessDetailsRequest(int requestCode, String processId,
			BuyopicNetworkCallBack buyopicNetworkCallBack) {

		final String totalurl;

		if (requestCode == Constants.REQUEST_URL_PROCESS_DETAILS) {
			totalurl = Uri
					.parse(BASE_URL + REQUEST_URL_PROCESSURL)
					.buildUpon()
					.appendQueryParameter(REQUEST_PARAM_PROCESS_TYPE,
							Constants.PROCESS_TYPE_SHARE)
					.appendQueryParameter(REQUEST_PARAM_PROCESS_ID, processId)
					.appendQueryParameter(REQUEST_PARAM_REQUESTFROMBUYOPIC,
							MSG_YES).build().toString();
		} else {
			totalurl = Uri
					.parse(BASE_URL + REQUEST_URL_PROCESSURL)
					.buildUpon()
					.appendQueryParameter(REQUEST_PARAM_PROCESS_TYPE,
							Constants.PROCESS_TYPE_REGISTRATION)
					.appendQueryParameter(REQUEST_PARAM_PROCESS_ID, processId)
					.appendQueryParameter(REQUEST_PARAM_REQUESTFROMBUYOPIC,
							MSG_YES).build().toString();
		}

		final BuyopicRequestHandler buyopicHandler = new BuyopicRequestHandler(
				requestCode, buyopicNetworkCallBack);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				HttpRestConn.GetRequestData(totalurl, buyopicHandler);

			}
		});

	}

	public void sendSubmitNewOrderRequest(int requestCode, String store_id,
			String retailer_id, String consumer_id, String offer_item_id,
			String order_quantity, String offer_item_price,
			String address_line1, String address_line2, String address_line3,
			String city, String state, String pincode,
			String requested_delivery_date,
			BuyopicNetworkCallBack buyopicNetworkCallBack) {

		final String url = Uri.parse(BASE_URL + REQUEST_URL_SUBMITNEWORDER)
				.buildUpon().build().toString();
		final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		if (requestCode == Constants.REQUEST_URL_SUBMIT_NEWORDER) {

			nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_STORE_ID,
					store_id));
			nameValuePairs.add(new BasicNameValuePair(
					REQUEST_PARAM_RETAILER_ID, retailer_id));
			nameValuePairs.add(new BasicNameValuePair(
					REQUEST_PARAM_CONSUMER_ID, consumer_id));
			nameValuePairs.add(new BasicNameValuePair(
					REQUEST_PARAM_OFFER_ITEM_ID, offer_item_id));
			nameValuePairs.add(new BasicNameValuePair(
					REQUEST_PARAM_ORDER_QUANTITY, order_quantity));

			nameValuePairs.add(new BasicNameValuePair(
					REQUEST_PARAM_OFFER_ITEM_PRICE, offer_item_price));
			nameValuePairs.add(new BasicNameValuePair(REQUEST_ADDRESS_LINE1,
					address_line1));
			nameValuePairs.add(new BasicNameValuePair(REQUEST_ADDRESS_LINE2,
					address_line2));
			nameValuePairs.add(new BasicNameValuePair(REQUEST_ADDRESS_LINE3,
					address_line3));

			nameValuePairs
					.add(new BasicNameValuePair(REQUEST_PARAM_CITY, city));
			nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_STATE,
					state));
			nameValuePairs.add(new BasicNameValuePair(REQUEST_PARAM_PINCODE,
					pincode));
			nameValuePairs.add(new BasicNameValuePair(
					REQUEST_PARAM_REQUESTED_DELIVERY_DATE,
					requested_delivery_date));
		}

		final BuyopicRequestHandler buyopicHandler = new BuyopicRequestHandler(
				requestCode, buyopicNetworkCallBack);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				// post request

				HttpRestConn.postRequestData(url, nameValuePairs,
						buyopicHandler);

			}
		});

	}

	private void printNameValuePairs(List<NameValuePair> nameValuePairs) {
		for (NameValuePair nameValuePair : nameValuePairs) {
			Utils.showLog(nameValuePair.getName() + ","
					+ nameValuePair.getValue());
		}
	}

	public void sendGetConsumerDetailsRequest(int requestCode,
			String consumerId, BuyopicNetworkCallBack callBack) {
		final String url = Uri.parse(BASE_URL + REQUEST_URL_GETCONSUMERDETAILS)
				.buildUpon()
				.appendQueryParameter(REQUEST_PARAM_CONSUMER_ID, consumerId)
				.build().toString();
		final BuyopicRequestHandler buyOpicHandler = new BuyopicRequestHandler(
				requestCode, callBack);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpRestConn.GetRequestData(url, buyOpicHandler);
			}
		});

	}

	public void getMyOrdersRequest(int requestCode,
			String consumerId, BuyopicNetworkCallBack callBack) {
		final String url = Uri.parse(BASE_URL + REQUEST_URL_GETORDERSDETAILS)
				.buildUpon()
				.appendQueryParameter(REQUEST_PARAM_CONSUMER_ID, consumerId)
				.build().toString();
		final BuyopicRequestHandler buyOpicHandler = new BuyopicRequestHandler(
				requestCode, callBack);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpRestConn.GetRequestData(url, buyOpicHandler);
			}
		});

	}

}
