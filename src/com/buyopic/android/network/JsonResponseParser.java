package com.buyopic.android.network;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.buyopic.android.database.BuyopicConsumerDatabase;
import com.buyopic.android.models.Alert;
import com.buyopic.android.models.AlertDetail;
import com.buyopic.android.models.CheckUpdate;
import com.buyopic.android.models.Consumer;
import com.buyopic.android.models.ConsumerDetails;
import com.buyopic.android.models.OrderList;
import com.buyopic.android.models.OrderStatus;
import com.buyopic.android.models.ProvisionedBeaconInfo;
import com.buyopic.android.models.StoreAlerts;
import com.buyopic.android.models.StoreInfo;
import com.buyopic.android.models.YelpReviewItem;
import com.buyopic.android.radius.BuyOpic;
import com.buyopic.android.utils.Utils;

/*
 * This Class Used to Parse the Json Response
 */

public class JsonResponseParser {

	private static final String TAG_DISTANCE = "distance";
	public static final String TAG_PROCESS_URL = "process_url";
	public static final String TAG_PROCESS_ID = "process_id";
	public static final String TAG_PROCESS_TYPE = "process_type";

	private static final String KEY_ISINBEACONRANGE = "isinbeaconrange";
	private static final String TAG_POSTED_BY_CONSUMER_IMAGE_URL = "posted_by_consumer_image_url";
	private static final String TAG_POSTED_BY_CONSUMER_PHONE_NO = "posted_by_consumer_phone_no";
	private static final String TAG_PRICE = "price";
	private static final String TAG_ALERT_LONGITUDE = "alert_longitude";
	private static final String TAG_ALERT_LATITUDE = "alert_latitude";
	private static final String TAG_POSTED_BY_CONSUMER_PHONE_NUMBER = "posted_by_consumer_phone_number";
	private static final String TAG_ALERT_END_DATE = "alert_end_date";
	private static final String TAG_POSTED_BY_CONSUMER_ID = "posted_by_consumer_id";
	private static final String TAG_BEACON_UUID = "beacon_uuid";
	private static final String TAG_CONSUMER_STATE = "consumer_state";
	private static final String TAG_CONSUMER_CITY = "consumer_city";
	private static final String TAG_CONSUMER_ADDRESS1 = "consumer_address1";
	private static final String TAG_CONSUMER_NAME = "consumer_name";
	private static final String TAG_CONSUMER_PHONE_NO = "consumer_phone_no";
	private static final String TAG_STORE_ASSOCIATE_PHONE_NO = "store_associate_phone_no";
	private static final String TAG_BEACONS = "beacons";
	private static final String TAG_CONSUMER_IMAGE_URL = "consumer_image_url";
	private static final String STATUS_OK = "ok";
	private static final String ERROR_ONE_TIME_REGISTRATION = "Complete the One time Registration";
	private static final String STATUS_NOTCONFIRMED = "notconfirmed";
	private static final String ERROR_AUTH = "Login Failed. Please try again";
	private static final String STATUS_ERROR = "error";
	private static final String TAG_REGISTERED = "registered";
	private static final String TAG_USER_STATUS = "user_status";
	private static final String TAG_USER_COUNTRY = "user_country";
	private static final String TAG_USER_STATE = "user_state";
	private static final String TAG_USER_CITY = "user_city";
	private static final String TAG_USER_ADDRESS1 = "user_address1";
	private static final String TAG_PASSWORD = "password";
	private static final String TAG_PHONE_NO = "phone_no";
	private static final String TAG_USER_NAME = "user_name";
	private static final String TAG_USER_IMAGE_URL = "user_image_url";
	private static final String TAG_USER_ADDRESSIMAGEURL = "user_address_image_url";
	private static final String TAG_USER_GOOGLEICONIMAGE = "google_icon_image";
	private static final String TAG_USER_GOOGLEPLACEID = "google_place_id";
	private static final String TAG_USER_ADDRESS2 = "user_address2";
	private static final String TAG_USER_POSTALCODE = "user_postal_code";

	private static final String TAG_CONSUMER = "consumer";
	private static final String TAG_POSTED_CONSUMER_ID = "posted_consumer_id";
	private static final String TAG_CONSUMER_FAVORITE = "consumer_favorite";
	private static final String TAG_DESCRIPTION = "description";
	private static final String TAG_ASSOCIATE_PHONE_NUMBER = "associate_phone_number";
	private static final String TAG_BUYOPIC_ALERTS = "buyopic_alerts";
	private static final String TAG_STORE_TIER = "store_tier";
	
	private static final String TAG_ALERTS = "alerts";
	private static final String TAG_ALERT = "alert";
	private static final String TAG_YELP_BUSINESS_NAME = "name";
	private static final String TAG_IMAGE_URL = "image_url";
	private static final String TAG_REVIEW_COUNT = "review_count";
	private static final String TAG_ADDRESS = "location";
	private static final String TAG_RATING_PRICE_URL = "rating_img_url";
	private static final String TAG_CITY = "city";
	private static final String TAG_POSTAL_CODE = "postal_code";
	private static final String TAG_COUNTRY_CODE = "country_code";
	private static final String TAG_STATUS = "status";
	
	private static final String TAG_ALERT_ID = "alert_id";
	private static final String TAG_TITLE = "title";
	private static final String TAG_ALERT_TITLE = "alert_title";
	private static final String TAG_ALERT_DESCRIPTION = "desctiption";
	private static final String TAG_ALERT_PRICE = TAG_PRICE;
	private static final String TAG_ALERT_THUMBNAIL = "image_url";
	private static final String TAG_START_DATE = "start_date";
	private static final String TAG_END_DATE = "end_date";
	private static final String TAG_IS_DISCONTINUED = "is_alert_discontinued";

	private static final String TAG_USER = "user";
	private static final String TAG_EMAIL = "email";
	private static final String TAG_CONSUMER_ID = "consumer_id";
	public static final int KEY_SPECIAL_OFFERS = 0;
	public static final int KEY_BEST_OFFERS = 1;

	public static final String TAG_STORE_STATE = "store_state";
	public static final String TAG_STORE_CITY = "store_city";
	public static final String TAG_STORE_ID = "store_id";
	public static final String TAG_STORE_ADDRESS = "store_address";
	public static final String TAG_STORE_ADDRESS1 = "store_address1";
	public static final String TAG_STORE_MERCHANT_NAME = "merchat_name";
	private static final String TAG_STORE_NAME = "store_name";
	private static final String TAG_STORE_WELCOME_MESSAGE = "store_welcome_message";
	private static final String TAG_STORE_IMAGE = "store_image";
	public static final String TAG_OFFER_TITLE = "alert_title";
	public static final String TAG_OFFER_ID = "alert_id";
	public static final String TAG_OFFER_PRICE = "alert_price";
	public static final String TAG_ALERT_IMAGE = "alert_image_url";

	public static final String TAG_STORES = "stores";
	public static final String TAG_STORE_IMAGE_URL = "store_image_url";
	public static final String TAG_STORE_ALERT_MESSAGE = "alert_message";
	public static final String TAG_STORE_ALERT_PRICE = "alert_price";
	public static final String TAG_PRODUCT_IMAGE_URL = "alert_image_url";
	public static final String TAG_BEACON_UUI = "beacon_uui";
	public static final String TAG_BEACON_MAJOR = "beacon_major";
	public static final String TAG_BEACON_MINOR = "beacon_minor";
	public static final String TAG_CLOSE_BY_ALERTS = "closeByAlerts";
	private static final String TAG_POSTED_ALERT_ID = "posted_alert_id";
	private static final String TAG_ALERT_MESSAGE = "alert_message";
	private static final String TAG_ALERT_THUMBNAIL_URL = "alert_thumbnail_url";

	public static final String TAG_IN_THE_AREA_ALERTS = "inTheAreaAlerts";
	public static final String TAG_MERCHANT_NAME = "merchat_name";
	
	
	public static final String TAG__STORE_ADDRESS_IMAGE = "store_address_image";
	private static final String TAG_STORE_ADDRESS2 = "store_address2";
	private static final String TAG_STORE_POSTALCODE = "store_postal_code";
	
	
	/*Tags for detailspostedbyme (consumer)*/
	private static final String TAG_CONSUMER_ADDRESS_IMAGE_URL="consumer_address_image_url";
	private static final String TAG_CONSUMER_ADDRESS2="consumer_address2";
	private static final String TAG_CONSUMER_POSTAL_CODE="consumer_postal_code";
	private static final String TAG_CONSUMER_REGISTERED_LAT="consumer_registered_lat";
	private static final String TAG_CONSUMER_REGISTERED_LONG="consumer_registered_long";
	

	private static final String TAG_MESSAGE = "message";
	public static final String TAG_RETAILER_ID = "retailer_id";
	private static final String TAG_YELP_ID = "yelp_id";
	public static final String TAG_IS_AVAILABLE = "available";
	public static final String TAG_CHECK_MESSAGE = "message";
	public static final String TAG_IS_COMPUTED = "computed";
	public static final String TAG_CHECK_BACKGROUND_BETWEEN_SCAN_PERIOD="background_between_scan_period";
	public static final String TAG_CHECK_BACKGROUND_SCAN_PERIOD="background_scan_period";
	public static final String TAG_CHECK_INTERVAL_CHECK_FOR_NEW_ALERTS_CLOSEBY_LIST="interval_check_for_new_alerts_closeby_list";
	public static final String TAG_INTERVAL_CHECK_FOR_NEW_ALERTS_BACKGROUND_MONITOR="interval_check_for_new_alerts_background_monitor";
	public static final String TAG_CHECK_NEXT_RUN_TIME="next_run_time";
	public static final String TAG_CHECK_SMART_LOCATION_COMPUTING_RATE="smart_location_computing_rate";
	public static final String TAG_CHECK_DURATION="duration";
	public static final String TAG_NEAREST_BEACON_DISTANCE="nearest_beacon_distance";
	

	
	
	
	/*for new nearbylist including google store*/
	
	public static final String TAG_GOOGLE_STORES="google_stores";
	public static final String TAG_VICINITY="vicinity";
	public static final String TAG_PLACEID="placeId";
	public static final String TAG_NAME="name";
	public static final String TAG_LONGITUDE="longitude";
	public static final String TAG_LATITUDE="latitude";
	public static final String TAG_ICONURL="iconURL";
	
	
	
	
	
	
	public static final String TAG_ORDERID="order_id";
	
	public static final String TAG_ORDER_STATUS="order_status";
	public static final String TAG_REQUESTEDDELIVERY=   "requestedDelivery";
	public static final String TAG_STATE="state";
	public static final String TAG_EXPECTEDDELIVERY=   "expectedDelivery";
	public static final String TAG_ITEMPRICE="itemPrice";
	public static final String TAG_ADDRESSLINE3=  "addressLine3";
	public static final String TAG_ADDRESSLINE2=  "addressLine2";
	public static final String TAG_ADDRESSLINE1=    "addressLine1";
//	public static final String TAG_CITY=   "city";
	public static final String TAG_CONSUMERID=    "consumerId";
	public static final String TAG_TOTALORDERPRICE=    "totalOrderPrice";
	public static final String TAG_STATUSID=   "statusId";
	public static final String TAG_PINCODE=   "pincode";
	public static final String TAG_CONSUMERNAME=  "consumerName";
	public static final String TAG_OFFERITEMNAME=    "offerItemName";
	public static final String TAG_OFFERITEMID=   "offerItemId";
	public static final String TAG_ORDERQUANTITY=    "orderQuantity";
	public static final String TAG_ORDERID1=    "orderId";
	public static final String TAG_STATUSDESC=    "statusDesc";
	public static final String TAG_CONSUMER_DATA=    "consumer_data";
	public static final String TAG_ORDER_STOREID=    "storeId";
	public static final String TAG_ORDER_STORENAME=    "storeName";
	
	
	// consumer address details
	
	public static final String TAG_PHONENUMBER ="phonenumber";
	public static final String TAG_ADDRESSCOUNTRY="addresscountry";
	public static final String TAG_CONSUMERIMAGEURLTRUE="consumerImageUrlTrue";
	public static final String TAG_FIRSTNAME="firstname";
	public static final String TAG_TRANSPIN="transPin";
//	public static final String TAG_CONSUMERID="consumerId";
	public static final String TAG_ADDRESSLINE2_CONSUMERDETAILS="addressline2";
	public static final String TAG_GOOGLEPLACEID="googlePlaceId";
	public static final String TAG_EMAILID="emailId";
	public static final String TAG_ADDRESSLINE1_CONSUMERDETAILS="addressline1";
	public static final String TAG_ADDRESSZIP="addresszip";
	public static final String TAG_REGISTRATION_CONFIRMED="registration_confirmed";
	public static final String TAG_CONSUMERADDRESSIMAGEURL="consumerAddressImageURL";
	public static final String TAG_ADDRESSSTATE="addressstate";
	public static final String TAG_ADDRESSCITY="addresscity";
	public static final String TAG_CONSUMER_LONGITUDE="consumer_longitude";
	public static final String TAG_CONSUMERADDRESSIMAGEURLFLAG="consumerAddressImageUrlFlag";
	public static final String TAG_CONSUMER_LATITUDE="consumer_latitude";
	public static final String TAG_GOOGLEICONIMAGE="googleIconImage";
	public static final String TAG_CONSUMER_IMAGE_URL_CONSUMERDETAILS="consumer_image_url";
	public static final String TAG_ACCOUNT_TYPE="account_type";
	
	
	
	
	
	public static final String TAG_SEARCH_PARAMS = "searchParams";
	private static final String TAG_MY_ORDERS_LIST = "my_orders_list";
	private static final String TAG_CONSUMERMAILID = "consumerMailId";
	private static final String TAG_CONSUMERPHONENO = "consumerPhoneNo";
	private static final String TAG_STORENAME ="storeName";
	private static final String TAG_RETAILERID ="retailerId";	
	
	
	
	
	
	
	
	
	private static BuyopicConsumerDatabase buyopicConsumerDatabase;

	public static List<Alert> parseAlertsResponse(String response) {
		List<Alert> list = new ArrayList<Alert>();
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(response);
			if (jsonObject.has(TAG_ALERTS)) {
				JSONArray jAlertArrayObj = jsonObject.getJSONArray(TAG_ALERTS);
				if (jAlertArrayObj != null && jAlertArrayObj.length() > 0) {
					for (int i = 0; i < jAlertArrayObj.length(); i++) {
						Alert alert = null;
						JSONObject jAlertObj = jAlertArrayObj.getJSONObject(i);
						alert = parseAlertResponse(jAlertObj);
						list.add(alert);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return list;

	}

	public static List<Alert> parseMyListingsResponse(String response) {
		List<Alert> list = new ArrayList<Alert>();
		try {
			JSONObject jsonObject = new JSONObject(response);
			if (jsonObject.has(TAG_STATUS)
					&& jsonObject.getString(TAG_STATUS).equalsIgnoreCase(
							STATUS_OK)) {
				if (jsonObject.has(TAG_ALERTS)) {
					JSONArray jsonArray = jsonObject.getJSONArray(TAG_ALERTS);
					if (jsonArray != null && jsonArray.length() > 0) {
						for (int i = 0; i < jsonArray.length(); i++) {
							Alert alert = new Alert();
							JSONObject jsonObject2 = jsonArray.getJSONObject(i);
							DecimalFormat decimalFormat = new DecimalFormat(
									"#0.00");
							String mPrice = decimalFormat.format(Double
									.valueOf(jsonObject2
											.getString(TAG_ALERT_PRICE)));
							alert.setmPrice(jsonObject2.has(TAG_ALERT_PRICE) ? mPrice
									: null);
							alert.setmOfferId(jsonObject2
									.has(TAG_POSTED_ALERT_ID) ? jsonObject2
									.getString(TAG_POSTED_ALERT_ID) : null);
							alert.setmOfferMessage(jsonObject2
									.has(TAG_ALERT_MESSAGE) ? jsonObject2
									.getString(TAG_ALERT_MESSAGE) : null);
							alert.setmIsActivated(jsonObject2.has(TAG_STATUS) ? jsonObject2
									.getBoolean(TAG_STATUS) : false);
							alert.setmOfferTitle(jsonObject2
									.has(TAG_ALERT_TITLE) ? jsonObject2
									.getString(TAG_ALERT_TITLE) : null);
							alert.setmThumbnailUrl(jsonObject2
									.has(TAG_ALERT_THUMBNAIL_URL) ? jsonObject2
									.getString(TAG_ALERT_THUMBNAIL_URL) : null);
							list.add(alert);
						}
					}
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;

	}

	public static AlertDetail parseAlertDetailsResponse(String string) {
		AlertDetail alertDetail = new AlertDetail();

		try {
			JSONObject jsonObject = new JSONObject(string);
			if (jsonObject.has(TAG_STATUS)
					&& jsonObject.getString(TAG_STATUS).equalsIgnoreCase(
							STATUS_OK)) {
				if (jsonObject.has(TAG_ALERT)) {
					Alert alert = null;
					JSONArray jAlertsArrayObj = jsonObject
							.getJSONArray(TAG_ALERT);
					if (jAlertsArrayObj != null && jAlertsArrayObj.length() > 0) {
						alert = parseAlertResponse(jAlertsArrayObj
								.getJSONObject(0));
					}
					alertDetail.setAlert(alert);
				}
				alertDetail
						.setmStoreAddress(jsonObject.has(TAG_STORE_ADDRESS) ? jsonObject
								.getString(TAG_STORE_ADDRESS) : null);
				alertDetail.setmPhoneNUmber(jsonObject
						.has(TAG_ASSOCIATE_PHONE_NUMBER) ? jsonObject
						.getString(TAG_ASSOCIATE_PHONE_NUMBER) : null);

				alertDetail
						.setmStoreLogo(jsonObject.has(TAG_STORE_IMAGE) ? jsonObject
								.getString(TAG_STORE_IMAGE) : null);
				alertDetail
						.setmStoreName(jsonObject.has(TAG_STORE_NAME) ? jsonObject
								.getString(TAG_STORE_NAME) : null);
				alertDetail
						.setmStoreId(jsonObject.has(TAG_STORE_ID) ? jsonObject
								.getString(TAG_STORE_ID) : null);
				alertDetail
						.setmRetailerId(jsonObject.has(TAG_RETAILER_ID) ? jsonObject
								.getString(TAG_RETAILER_ID) : null);
				
				alertDetail
				.setmStoreAddressImage(jsonObject.has(TAG__STORE_ADDRESS_IMAGE) ? jsonObject
						.getString(TAG__STORE_ADDRESS_IMAGE) : null);

				alertDetail
				.setmGoogleIconImage(jsonObject.has(TAG_USER_GOOGLEICONIMAGE) ? jsonObject
						.getString(TAG_USER_GOOGLEICONIMAGE) : null);

				alertDetail
				.setmGooglePlaceId(jsonObject.has(TAG_USER_GOOGLEPLACEID) ? jsonObject
						.getString(TAG_USER_GOOGLEPLACEID) : null);

				alertDetail
				.setmStoreAddress1(jsonObject.has(TAG_STORE_ADDRESS1) ? jsonObject
						.getString(TAG_STORE_ADDRESS1) : null);
				alertDetail
				.setmStoreAddress2(jsonObject.has(TAG_STORE_ADDRESS2) ? jsonObject
						.getString(TAG_STORE_ADDRESS2) : null);
				
				alertDetail
				.setmCity(jsonObject.has(TAG_STORE_CITY) ? jsonObject
						.getString(TAG_STORE_CITY) : null);

				alertDetail
				.setmState(jsonObject.has(TAG_STORE_STATE) ? jsonObject
						.getString(TAG_STORE_STATE) : null);
				alertDetail
				.setmPostalCode(jsonObject.has(TAG_STORE_POSTALCODE) ? jsonObject
						.getString(TAG_STORE_POSTALCODE) : null);




			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return alertDetail;
	}

	private static Alert parseAlertResponse(JSONObject jAlertObj) {
		Alert alert = new Alert();
		try {
			alert.setmOfferId(jAlertObj.has(TAG_ALERT_ID) ? jAlertObj
					.getString(TAG_ALERT_ID) : null);

			alert.setmOfferTitle(jAlertObj.has(TAG_TITLE) ? jAlertObj
					.getString(TAG_TITLE) : null);
			if (jAlertObj.has(TAG_ALERT_DESCRIPTION)) {
				alert.setmOfferMessage(jAlertObj
						.getString(TAG_ALERT_DESCRIPTION));
			} else if (jAlertObj.has(TAG_DESCRIPTION)) {
				alert.setmOfferMessage(jAlertObj.getString(TAG_DESCRIPTION));
			} else {
				alert.setmOfferMessage("");
			}

			if (jAlertObj.has(TAG_CONSUMER_FAVORITE)) {
				alert.setFavorite(jAlertObj.getBoolean(TAG_CONSUMER_FAVORITE));
			}
			alert.setmPostedAlertId(jAlertObj.has(TAG_POSTED_ALERT_ID) ? jAlertObj
					.getString(TAG_POSTED_ALERT_ID) : null);

			alert.setmPostedConsumerId(jAlertObj.has(TAG_POSTED_CONSUMER_ID) ? jAlertObj
					.getString(TAG_POSTED_CONSUMER_ID) : null);

			DecimalFormat decimalFormat = new DecimalFormat("#0.00");
			String mPrice = decimalFormat.format(Double.valueOf(jAlertObj
					.getString(TAG_ALERT_PRICE)));

			alert.setmPrice(jAlertObj.has(TAG_ALERT_PRICE) ? mPrice : null);
			alert.setmThumbnailUrl(jAlertObj.has(TAG_ALERT_THUMBNAIL) ? jAlertObj
					.getString(TAG_ALERT_THUMBNAIL) : null);
			alert.setmStartDate(jAlertObj.has(TAG_START_DATE) ? jAlertObj
					.getString(TAG_START_DATE) : null);
			alert.setmEndDate(jAlertObj.has(TAG_END_DATE) ? jAlertObj
					.getString(TAG_END_DATE) : null);
			alert.setmIsAlertDiscontinued(jAlertObj.has(TAG_IS_DISCONTINUED) ? jAlertObj
					.getString(TAG_IS_DISCONTINUED).equalsIgnoreCase("true") ? true
					: false
					: false);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return alert;
	}

	public static YelpReviewItem parseYelpReviewResponse(String response) {
		YelpReviewItem item = null;
		try {
			JSONObject jsonObject = new JSONObject(response);
			if (jsonObject.has("businesses"))
				;
			{
				JSONArray jBusinessArrayObj = jsonObject
						.getJSONArray("businesses");
				if (jBusinessArrayObj != null && jBusinessArrayObj.length() > 0) {
					JSONObject object = jBusinessArrayObj.getJSONObject(0);
					item = new YelpReviewItem();
					item.setmName(object.has(TAG_YELP_BUSINESS_NAME) ? object
							.getString(TAG_YELP_BUSINESS_NAME) : null);
					item.setmImageUrl(object.has(TAG_IMAGE_URL) ? object
							.getString(TAG_IMAGE_URL) : null);
					item.setmReviewCount(object.has(TAG_REVIEW_COUNT) ? object
							.getString(TAG_REVIEW_COUNT) : null);
					item.setmReviewImageUrl(object.has(TAG_RATING_PRICE_URL) ? object
							.getString(TAG_RATING_PRICE_URL) : null);
					if (object.has(TAG_ADDRESS)) {
						String address = "";
						JSONObject jAddressObject = object
								.getJSONObject(TAG_ADDRESS);
						if (jAddressObject.has(TAG_CITY)) {
							address += jAddressObject.getString(TAG_CITY);
						}
						if (jAddressObject.has(TAG_POSTAL_CODE)) {
							address += ","
									+ jAddressObject.getString(TAG_POSTAL_CODE);
						}

						if (jAddressObject.has(TAG_COUNTRY_CODE)) {
							address += ","
									+ jAddressObject
											.getString(TAG_COUNTRY_CODE);
						}
						item.setmAddress(address);
					}

				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return item;
	}

	public static Consumer parseConsumerLoginResponse(String string) {
		Consumer consumer = null;
		try {
			JSONObject jsonObject = new JSONObject(string);
			if (jsonObject.has(TAG_STATUS)
					&& jsonObject.getString(TAG_STATUS).equalsIgnoreCase(
							STATUS_OK)) {
				if (jsonObject.has(TAG_USER)) {
					JSONObject consumerObject = jsonObject
							.getJSONObject(TAG_USER);
					consumer = new Consumer();
					consumer.setmConsumerEmail(consumerObject.has(TAG_EMAIL) ? consumerObject
							.getString(TAG_EMAIL) : null);
					consumer.setmConsumerId(consumerObject.has(TAG_CONSUMER_ID) ? consumerObject
							.getString(TAG_CONSUMER_ID) : null);

				} else if (jsonObject.has(TAG_MESSAGE)) {
					consumer = new Consumer();
					JSONObject consumerObject = jsonObject
							.getJSONObject(TAG_MESSAGE);
					consumer.setmConsumerEmail(consumerObject.has(TAG_EMAIL) ? consumerObject
							.getString(TAG_EMAIL) : null);
					consumer.setmConsumerId(consumerObject.has(TAG_CONSUMER_ID) ? consumerObject
							.getString(TAG_CONSUMER_ID) : null);

				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return consumer;
	}

	public static String parseConsumerRegistrationResponse(String string) {
		try {
			JSONObject jsonObject = new JSONObject(string);
			return jsonObject.has(TAG_MESSAGE) ? jsonObject
					.getString(TAG_MESSAGE) : "Unknown Error";
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;

	}

	public static Consumer parseFinalConsumerRegistrationResponse(String string) {
		Consumer consumer = null;
		try {
			JSONObject jsonObject = new JSONObject(string);
			if (jsonObject.has(TAG_STATUS)
					&& jsonObject.getString(TAG_STATUS).equalsIgnoreCase(
							STATUS_OK)) {
				
				if (jsonObject.has(TAG_CONSUMER)) {
					JSONObject jsonObject2 = jsonObject
							.getJSONObject(TAG_CONSUMER);
					consumer = new Consumer();
					consumer.setmStatusMessage(STATUS_OK);
			//		consumer.setConsumerFinalRegistered(true);
					consumer.setmConsumerProfilePic(jsonObject2
							.has(TAG_USER_IMAGE_URL) ? jsonObject2
							.getString(TAG_USER_IMAGE_URL) : null);
					consumer.setmConsumerEmail(jsonObject2.has(TAG_EMAIL) ? jsonObject2
							.getString(TAG_EMAIL) : null);
					consumer.setmConsumerId(jsonObject2.has(TAG_CONSUMER_ID) ? jsonObject2
							.getString(TAG_CONSUMER_ID) : null);
					consumer.setmConsumerUserName(jsonObject2
							.has(TAG_USER_NAME) ? jsonObject2
							.getString(TAG_USER_NAME) : null);
					consumer.setmPhoneNumber(jsonObject2.has(TAG_PHONE_NO) ? jsonObject2
							.getString(TAG_PHONE_NO) : null);
					consumer.setmConsumerPassword(jsonObject2.has(TAG_PASSWORD) ? jsonObject2
							.getString(TAG_PASSWORD) : null);

					//

					String consumerAddress = "";
					if (jsonObject2.has(TAG_USER_ADDRESS1)) {
						consumer.setmConsumerAddress1(jsonObject2
								.getString(TAG_USER_ADDRESS1));
						consumerAddress += jsonObject2
								.getString(TAG_USER_ADDRESS1);
					}
					if (jsonObject2.has(TAG_USER_CITY)) {
						consumer.setmConsumerCity(jsonObject2
								.getString(TAG_USER_CITY));
						consumerAddress += ", "
								+ jsonObject2.getString(TAG_USER_CITY);
					}
					if (jsonObject2.has(TAG_USER_STATE)) {
						consumer.setmConsumerState(jsonObject2
								.getString(TAG_USER_STATE));
						consumerAddress += ", "
								+ jsonObject2.getString(TAG_USER_STATE);
					}
					if (jsonObject2.has(TAG_USER_COUNTRY)) {
						consumer.setmConsumerCountry(jsonObject2
								.getString(TAG_USER_COUNTRY));
					
						consumerAddress += ", "
								+ jsonObject2.getString(TAG_USER_COUNTRY);
					}
					if (jsonObject2.has(TAG_USER_ADDRESSIMAGEURL)) {
						/*consumerAddress += ", "
								+ jsonObject2.getString(TAG_USER_ADDRESSIMAGEURL);*/
						consumer.setmUserAddressImageUrl( jsonObject2.getString(TAG_USER_ADDRESSIMAGEURL));
					}
					if (jsonObject2.has(TAG_USER_GOOGLEICONIMAGE)) {
				/*		consumerAddress += ", "
								+ jsonObject2.getString(TAG_USER_GOOGLEICONIMAGE);*/
						consumer.setmGoogleIconImage(jsonObject2.getString(TAG_USER_GOOGLEICONIMAGE));
					}
					if (jsonObject2.has(TAG_USER_GOOGLEPLACEID)) {
						/*consumerAddress += ", "
								+ jsonObject2.getString(TAG_USER_GOOGLEPLACEID);*/
						consumer.setmGooglePlaceId(jsonObject2.getString(TAG_USER_GOOGLEPLACEID));
					}
					if (jsonObject2.has(TAG_USER_ADDRESS2)) {
						consumerAddress += ", "
								+ jsonObject2.getString(TAG_USER_ADDRESS2);
						consumer.setmConsumerAddress2(jsonObject2.getString(TAG_USER_ADDRESS2));
					}
					if (jsonObject2.has(TAG_USER_POSTALCODE)) {
						consumerAddress += ", "
								+ jsonObject2.getString(TAG_USER_POSTALCODE);
						consumer.setmConsumerPostalCode(jsonObject2.getString(TAG_USER_POSTALCODE));
					}

					consumer.setmConsumerAddress(consumerAddress);
					consumer.setConsumerFinalRegistered(jsonObject2
							.has(TAG_USER_STATUS) ? jsonObject2.getString(
							TAG_USER_STATUS).equalsIgnoreCase(TAG_REGISTERED) : true);
					
					/*new parsimg values for consumer */
					consumer.setmGoogleIconImage(jsonObject2.has(TAG_USER_GOOGLEICONIMAGE) ? jsonObject2
							.getString(TAG_USER_GOOGLEICONIMAGE) : null);
	
					consumer.setmGooglePlaceId(jsonObject2.has(TAG_USER_GOOGLEPLACEID) ? jsonObject2
							.getString(TAG_USER_GOOGLEPLACEID) : null);
	
					consumer.setmConsumerAddressImageUrl(jsonObject2.has(TAG_CONSUMER_ADDRESS_IMAGE_URL) ? jsonObject2
							.getString(TAG_CONSUMER_ADDRESS_IMAGE_URL) : null);
					
					consumer.setmConsumerLatitude(jsonObject2.has(TAG_CONSUMER_REGISTERED_LAT) ? jsonObject2
							.getString(TAG_CONSUMER_REGISTERED_LAT) : null);
					
					consumer.setmConsumerLongitude(jsonObject2.has(TAG_CONSUMER_REGISTERED_LONG) ? jsonObject2
							.getString(TAG_CONSUMER_REGISTERED_LONG) : null);
	
					
				}

			} else {
				consumer = new Consumer();
				if (jsonObject.has(TAG_STATUS)
						&& jsonObject.getString(TAG_STATUS).equalsIgnoreCase(
								STATUS_ERROR)) {
					consumer.setmStatusMessage(ERROR_AUTH);
				} else if (jsonObject.has(TAG_STATUS)
						&& jsonObject.getString(TAG_STATUS).equalsIgnoreCase(
								STATUS_NOTCONFIRMED)) {
					consumer.setmStatusMessage(ERROR_ONE_TIME_REGISTRATION);
				}
				
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return consumer;

	}

	public static boolean parseFavoriteResponse(String string) {
		try {
			JSONObject jsonObject = new JSONObject(string);
			if (jsonObject.has(TAG_STATUS)) {
				return jsonObject.getString(TAG_STATUS).equalsIgnoreCase(
						STATUS_OK) ? true : false;
			} else {
				return false;
			}
		} catch (JSONException jsonException) {
			jsonException.printStackTrace();
		}
		return false;
	}

	public static StoreAlerts parseStoreAlertsResponse(String string,
			String storeName, String phoneNumber) {

		StoreAlerts storeAlerts = new StoreAlerts();
		try {
			JSONObject jsonObject = new JSONObject(string);
			storeAlerts
					.setmRetailerId(jsonObject.has(TAG_RETAILER_ID) ? jsonObject
							.getString(TAG_RETAILER_ID) : null);
			storeAlerts.setmStoreId(jsonObject.has(TAG_STORE_ID) ? jsonObject
					.getString(TAG_STORE_ID) : null);

			if (jsonObject.has(TAG_STORE_IMAGE)) {
				storeAlerts.setmStoreImageUrl(jsonObject
						.getString(TAG_STORE_IMAGE));
			}

			if (jsonObject.has(TAG_CONSUMER_IMAGE_URL)) {
				storeAlerts.setmStoreImageUrl(jsonObject
						.getString(TAG_CONSUMER_IMAGE_URL));
			}
			storeAlerts.setmStoreWelcomeMessage(jsonObject
					.has(TAG_STORE_WELCOME_MESSAGE) ? jsonObject
					.getString(TAG_STORE_WELCOME_MESSAGE) : null);
			storeAlerts.setmYelpId(jsonObject.has(TAG_YELP_ID) ? jsonObject
					.getString(TAG_YELP_ID) : null);
			storeAlerts
					.setmStoreName(jsonObject.has(TAG_STORE_NAME) ? jsonObject
							.getString(TAG_STORE_NAME) : null);
			storeAlerts
					.setmStoreAddress(jsonObject.has(TAG_STORE_ADDRESS) ? jsonObject
							.getString(TAG_STORE_ADDRESS) : null);

			String phoneNumber1 = null;
			if (jsonObject.has(TAG_STORE_ASSOCIATE_PHONE_NO)) {
				phoneNumber1 = jsonObject
						.getString(TAG_STORE_ASSOCIATE_PHONE_NO);
			}
			if (jsonObject.has(TAG_CONSUMER_PHONE_NO)) {
				phoneNumber1 = jsonObject.getString(TAG_CONSUMER_PHONE_NO);
			}
			storeAlerts.setmStorePhoneNumber(phoneNumber1);

			if (jsonObject.has(TAG_CONSUMER_NAME)) {
				storeAlerts.setmStoreName(jsonObject
						.getString(TAG_CONSUMER_NAME));
			}
			String storeAddress = "";

			if (jsonObject.has(TAG_CONSUMER_ADDRESS1)) {
				storeAddress += jsonObject.getString(TAG_CONSUMER_ADDRESS1);
			}

			if (jsonObject.has(TAG_CONSUMER_CITY)) {
				storeAddress += "," + jsonObject.getString(TAG_CONSUMER_CITY);
			}

			if (jsonObject.has(TAG_CONSUMER_STATE)) {
				storeAddress += "," + jsonObject.getString(TAG_CONSUMER_STATE);
			}
			if (!TextUtils.isEmpty(storeAddress)) {
				storeAlerts.setmStoreAddress(storeAddress);
			}

			if (jsonObject.has(TAG_ALERTS)) {
				List<Alert> alerts = new ArrayList<Alert>();
				JSONArray jsonArray = jsonObject.getJSONArray(TAG_ALERTS);
				if (jsonArray != null && jsonArray.length() > 0) {
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject object = jsonArray.getJSONObject(i);
						Alert alert = parseAlertResponse(object);
						alert.setmStoreId(storeAlerts.getmStoreId());
						alert.setmRetailerId(storeAlerts.getmRetailerId());
						alert.setmPhoneNumber(storeAlerts
								.getmStorePhoneNumber());
						alert.setmStoreName(storeName);
						if (alert != null) {
							alerts.add(alert);
						}
					}
					storeAlerts.setmAlerts(alerts);
				}
			}
			
			/*parsing new address data related to map changes*/
			if (jsonObject.has(TAG__STORE_ADDRESS_IMAGE)) {
				storeAlerts.setmStoreAddressImage(jsonObject
						.getString(TAG__STORE_ADDRESS_IMAGE));
			}
			if (jsonObject.has(TAG_USER_GOOGLEICONIMAGE)) {
				storeAlerts.setmGoogleIconImage(jsonObject
						.getString(TAG_USER_GOOGLEICONIMAGE));
			}
			if (jsonObject.has(TAG_USER_GOOGLEPLACEID)) {
				storeAlerts.setmGooglePlaceId(jsonObject
						.getString(TAG_USER_GOOGLEPLACEID));
			}
			if (jsonObject.has(TAG_STORE_ADDRESS1)) {
				storeAlerts.setmStoreAddress1(jsonObject
						.getString(TAG_STORE_ADDRESS1));
			}
			if (jsonObject.has(TAG_STORE_ADDRESS2)) {
				storeAlerts.setmStoreAddress2(jsonObject
						.getString(TAG_STORE_ADDRESS2));
			}
			if (jsonObject.has(TAG_STORE_CITY)) {
				storeAlerts.setmStoreCity(jsonObject
						.getString(TAG_STORE_CITY));
			}
			if (jsonObject.has(TAG_STORE_STATE)) {
				storeAlerts.setmStoreState(jsonObject
						.getString(TAG_STORE_STATE));
			}
			if (jsonObject.has(TAG_STORE_POSTALCODE)) {
				storeAlerts.setmStorePostalCode(jsonObject
						.getString(TAG_STORE_POSTALCODE));
			}
			
			
			/*parsing new address data related to map changes consumer specific*/
			
			if (jsonObject.has(TAG_CONSUMER_ADDRESS_IMAGE_URL)) {
				storeAlerts.setmConsumerAddressImageUrl(jsonObject
						.getString(TAG_CONSUMER_ADDRESS_IMAGE_URL));
			}
			if (jsonObject.has(TAG_CONSUMER_ADDRESS2)) {
				storeAlerts.setmConsumerAddress2(jsonObject
						.getString(TAG_CONSUMER_ADDRESS2));
			}
			if (jsonObject.has(TAG_CONSUMER_POSTAL_CODE)) {
				storeAlerts.setmConsumerPostalCode(jsonObject
						.getString(TAG_CONSUMER_POSTAL_CODE));
			}
			

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return storeAlerts;

	}

	public static List<Alert> parseMyTrailsResponse(String string) {
		List<Alert> alerts = null;

		try {
			JSONObject jsonObject = new JSONObject(string);
			if (jsonObject.has(TAG_ALERTS)) {
				alerts = new ArrayList<Alert>();
				JSONArray jsonArray = jsonObject.getJSONArray(TAG_ALERTS);
				if (jsonArray != null && jsonArray.length() > 0) {
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject object = jsonArray.getJSONObject(i);
						Alert alert = new Alert();
						alert.setmPrice(object.has(TAG_STORE_ALERT_PRICE) ? object
								.getString(TAG_STORE_ALERT_PRICE) : null);
						alert.setmThumbnailUrl(object
								.has(TAG_PRODUCT_IMAGE_URL) ? object
								.getString(TAG_PRODUCT_IMAGE_URL) : null);
						alert.setmOfferTitle(object.has(TAG_ALERT_TITLE) ? object
								.getString(TAG_ALERT_TITLE) : null);
						alert.setmOfferMessage("");
						alert.setmOfferId(object.has(TAG_ALERT_ID) ? object
								.getString(TAG_ALERT_ID) : null);
						alert.setmIsAlertDiscontinued(object
								.has(TAG_IS_DISCONTINUED) ? object.getString(
								TAG_IS_DISCONTINUED).equalsIgnoreCase("true") ? true
								: false
								: false);
						if (alert != null) {
							alerts.add(alert);
						}
					}
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return alerts;

	}

	public static List<StoreInfo> parseStoreInfos(Context context, String string) {
		List<StoreInfo> storeInfos = new ArrayList<StoreInfo>();
		buyopicConsumerDatabase = BuyopicConsumerDatabase
				.shareInstance(context);
		BuyOpic buyOpic = (BuyOpic) context.getApplicationContext();
		double sourceLatitude = buyOpic.getmSourceLatitude();
		double sourceLongitude = buyOpic.getmSourceLongitude();
		
		try {
			JSONObject jsonObject = new JSONObject(string);
			if (jsonObject.has(TAG_BUYOPIC_ALERTS)) {
				JSONArray jsonArray = jsonObject.getJSONArray(TAG_BUYOPIC_ALERTS);
				
				if (jsonArray != null && jsonArray.length() > 0) {

					for (int i = 0; i < jsonArray.length(); i++) {
						StoreInfo storeInfo = new StoreInfo();
						JSONObject object = jsonArray.getJSONObject(i);
						storeInfo.setmOfferId(object.has(TAG_ALERT_ID) ? object
								.getString(TAG_ALERT_ID) : null);
						DecimalFormat decimalFormat = new DecimalFormat("#0.00");
						String mPrice = decimalFormat.format(Double
								.valueOf(object
										.getString(TAG_STORE_ALERT_PRICE)));
						storeInfo
								.setmPrice(object.has(TAG_STORE_ALERT_PRICE) ? mPrice
										: null);
						storeInfo.setmStoreTier(object.has(TAG_STORE_TIER) ? object
								.getString(TAG_STORE_TIER) : null);

						if (object.has(TAG_BEACONS)) {
							JSONArray beaconJArray = object
									.getJSONArray(TAG_BEACONS);
							if (beaconJArray != null
									&& beaconJArray.length() > 0) {
								List<ProvisionedBeaconInfo> beaconInfos = new ArrayList<ProvisionedBeaconInfo>();

								for (int j = 0; j < beaconJArray.length(); j++) {
									ProvisionedBeaconInfo beaconInfo = new ProvisionedBeaconInfo();
									JSONObject jsonObject2 = beaconJArray
											.getJSONObject(j);
									beaconInfo.setmBeaconUUID(jsonObject2
											.has(TAG_BEACON_UUID) ? jsonObject2
											.getString(TAG_BEACON_UUID) : "");
									beaconInfo
											.setmMajor(jsonObject2
													.has(TAG_BEACON_MAJOR) ? jsonObject2
													.getString(TAG_BEACON_MAJOR)
													: "");
									beaconInfo
											.setmMinor(jsonObject2
													.has(TAG_BEACON_MINOR) ? jsonObject2
													.getString(TAG_BEACON_MINOR)
													: "");
									beaconInfos.add(beaconInfo);

								}
								storeInfo.setProvisionedBeacons(beaconInfos);

								if (storeInfo.getProvisionedBeacons() != null
										&& !storeInfo.getProvisionedBeacons()
												.isEmpty()) {
									for (ProvisionedBeaconInfo provisionedBeaconInfo : storeInfo
											.getProvisionedBeacons()) {

										if (checkIfExists(
												provisionedBeaconInfo
														.getmMajor(),
												provisionedBeaconInfo
														.getmMinor(),
												provisionedBeaconInfo
														.getmBeaconUUID())) {
											storeInfo.setInBeaconRange(true);
											break;
										} else {
											storeInfo.setInBeaconRange(false);
										}
									}
								}
							}

						}

						String address = "";

						storeInfo.setmPostedAlertConsumerId(object
								.has(TAG_POSTED_BY_CONSUMER_ID) ? object
								.getString(TAG_POSTED_BY_CONSUMER_ID) : null);

						if (object.has(TAG_STORE_ADDRESS1)) {
							address += object.getString(TAG_STORE_ADDRESS1);
						}

						if (object.has(TAG_STORE_CITY)) {
							address += "," + object.getString(TAG_STORE_CITY);
						}
						if (object.has(TAG_STORE_STATE)) {
							address += "," + object.getString(TAG_STORE_STATE);
						}
						storeInfo
								.setmEndDate(object.has(TAG_ALERT_END_DATE) ? object
										.getString(TAG_ALERT_END_DATE) : null);

						storeInfo.setmStoreAddress(address);

						if (object.has(TAG_POSTED_CONSUMER_ID)) {
							storeInfo.setmPostedAlertConsumerId(object
									.getString(TAG_POSTED_CONSUMER_ID));
						}
						if (storeInfo.getmPostedAlertConsumerId() != null
								&& !storeInfo.getmPostedAlertConsumerId()
										.equalsIgnoreCase("null")) {
							storeInfo
									.setmPhoneNumber(object
											.has(TAG_POSTED_BY_CONSUMER_PHONE_NUMBER) ? object
											.getString(TAG_POSTED_BY_CONSUMER_PHONE_NUMBER)
											: null);
						} else {
							storeInfo.setmPhoneNumber(object
									.has(TAG_STORE_ASSOCIATE_PHONE_NO) ? object
									.getString(TAG_STORE_ASSOCIATE_PHONE_NO)
									: null);
						}

						storeInfo
								.setmDescription(object.has(TAG_ALERT_MESSAGE) ? object
										.getString(TAG_ALERT_MESSAGE) : null);

						storeInfo.setmProductLogo(object
								.has(TAG_PRODUCT_IMAGE_URL) ? object
								.getString(TAG_PRODUCT_IMAGE_URL) : null);
						storeInfo
								.setmRetailerId(object.has(TAG_RETAILER_ID) ? object
										.getString(TAG_RETAILER_ID) : null);
						storeInfo.setmStoreId(object.has(TAG_STORE_ID) ? object
								.getString(TAG_STORE_ID) : null);
						storeInfo
								.setmStoreLogo(object.has(TAG_STORE_IMAGE_URL) ? object
										.getString(TAG_STORE_IMAGE_URL) : null);
						storeInfo
								.setmStoreName(object.has(TAG_STORE_NAME) ? object
										.getString(TAG_STORE_NAME) : null);
						storeInfo
								.setmTitle(object.has(TAG_ALERT_TITLE) ? object
										.getString(TAG_ALERT_TITLE) : null);

						if (object.has(TAG_CONSUMER_FAVORITE)) {
							storeInfo.setFavorite(object
									.getBoolean(TAG_CONSUMER_FAVORITE));
						}
						storeInfo.setStoreLatitude(object
								.has(TAG_ALERT_LATITUDE) ? object
								.getDouble(TAG_ALERT_LATITUDE) : null);
						storeInfo.setStoreLongitude(object
								.has(TAG_ALERT_LONGITUDE) ? object
								.getDouble(TAG_ALERT_LONGITUDE) : null);

						double distanceInMeters = Utils.calculateDistance(
								sourceLatitude, sourceLongitude,
								storeInfo.getStoreLatitude(),
								storeInfo.getStoreLongitude(),'M');
						storeInfo.setDistanceValue(Utils
								.convertMetrsAndFeets(distanceInMeters,context));

						if (object.has(TAG_BEACON_UUI)) {
							storeInfo.setmBeaconUUID(object
									.getString(TAG_BEACON_UUI));
						} else if (object.has(TAG_BEACON_UUID)) {
							storeInfo.setmBeaconUUID(object
									.getString(TAG_BEACON_UUID));
						} else {
							storeInfo.setmBeaconUUID("");
						}
						// storeInfo
						// .setmBeaconUUID(object.has(TAG_BEACON_UUID) ? object
						// .getString(TAG_BEACON_UUID) : "");
						storeInfo
								.setmMajor(object.has(TAG_BEACON_MAJOR) ? object
										.getString(TAG_BEACON_MAJOR) : "");
						storeInfo
								.setmMinor(object.has(TAG_BEACON_MINOR) ? object
										.getString(TAG_BEACON_MINOR) : "");

						// storeInfo.setInBeaconRange(checkIfExists(
						// storeInfo.getmMajor(), storeInfo.getmMinor(),
						// storeInfo.getmBeaconUUID()));
						storeInfos.add(storeInfo);
					}
				}
				
	
			}
			 if(jsonObject.has(TAG_GOOGLE_STORES)){
					JSONArray jsonArray1 = jsonObject.getJSONArray(TAG_GOOGLE_STORES);
					if (jsonArray1 != null && jsonArray1.length() > 0) {

						for (int i = 0; i < jsonArray1.length(); i++) {
							List<GoogleStoreInfo> listObjstoreInfo=new ArrayList<GoogleStoreInfo>();
							GoogleStoreInfo googleStoreInfo = new GoogleStoreInfo();
							StoreInfo storeInfo=new StoreInfo();
							JSONObject object = jsonArray1.getJSONObject(i);
							googleStoreInfo.setmDistance(object.has(TAG_DISTANCE) ? object
										.getDouble(TAG_DISTANCE) : null);
							googleStoreInfo.setmVicinity(object.has(TAG_VICINITY) ? object
									.getString(TAG_VICINITY) : null);
							googleStoreInfo.setmPlaceId(object.has(TAG_PLACEID) ? object
									.getString(TAG_PLACEID) : null);
							googleStoreInfo.setmName(object.has(TAG_NAME) ? object
									.getString(TAG_NAME) : null);
							googleStoreInfo.setmLongitude(object.has(TAG_LONGITUDE) ? object
									.getString(TAG_LONGITUDE) : null);
							googleStoreInfo.setmLatitude(object.has(TAG_LATITUDE) ? object
									.getString(TAG_LATITUDE) : null);
							googleStoreInfo.setmIconURL(object.has(TAG_ICONURL) ? object
									.getString(TAG_ICONURL) : null);
							listObjstoreInfo.add(googleStoreInfo);
							storeInfo.setmGoogleStoreInfo(listObjstoreInfo);

							storeInfos.add(storeInfo);
							
						}
					}
			}
			

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return storeInfos;
	}


	public static List<StoreInfo> parseStoreInfosForFavourite(Context context, String string) {
		List<StoreInfo> storeInfos = new ArrayList<StoreInfo>();
		buyopicConsumerDatabase = BuyopicConsumerDatabase
				.shareInstance(context);
		BuyOpic buyOpic = (BuyOpic) context.getApplicationContext();
		double sourceLatitude = buyOpic.getmSourceLatitude();
		double sourceLongitude = buyOpic.getmSourceLongitude();
		try {
			JSONObject jsonObject = new JSONObject(string);
			if (jsonObject.has(TAG_ALERTS)) {
				JSONArray jsonArray = jsonObject.getJSONArray(TAG_ALERTS);
				if (jsonArray != null && jsonArray.length() > 0) {

					for (int i = 0; i < jsonArray.length(); i++) {
						StoreInfo storeInfo = new StoreInfo();
						JSONObject object = jsonArray.getJSONObject(i);
						storeInfo.setmOfferId(object.has(TAG_ALERT_ID) ? object
								.getString(TAG_ALERT_ID) : null);
						DecimalFormat decimalFormat = new DecimalFormat("#0.00");
						String mPrice = decimalFormat.format(Double
								.valueOf(object
										.getString(TAG_STORE_ALERT_PRICE)));
						storeInfo
								.setmPrice(object.has(TAG_STORE_ALERT_PRICE) ? mPrice
										: null);
						

						if (object.has(TAG_BEACONS)) {
							JSONArray beaconJArray = object
									.getJSONArray(TAG_BEACONS);
							if (beaconJArray != null
									&& beaconJArray.length() > 0) {
								List<ProvisionedBeaconInfo> beaconInfos = new ArrayList<ProvisionedBeaconInfo>();

								for (int j = 0; j < beaconJArray.length(); j++) {
									ProvisionedBeaconInfo beaconInfo = new ProvisionedBeaconInfo();
									JSONObject jsonObject2 = beaconJArray
											.getJSONObject(j);
									beaconInfo.setmBeaconUUID(jsonObject2
											.has(TAG_BEACON_UUID) ? jsonObject2
											.getString(TAG_BEACON_UUID) : "");
									beaconInfo
											.setmMajor(jsonObject2
													.has(TAG_BEACON_MAJOR) ? jsonObject2
													.getString(TAG_BEACON_MAJOR)
													: "");
									beaconInfo
											.setmMinor(jsonObject2
													.has(TAG_BEACON_MINOR) ? jsonObject2
													.getString(TAG_BEACON_MINOR)
													: "");
									beaconInfos.add(beaconInfo);

								}
								storeInfo.setProvisionedBeacons(beaconInfos);

								if (storeInfo.getProvisionedBeacons() != null
										&& !storeInfo.getProvisionedBeacons()
												.isEmpty()) {
									for (ProvisionedBeaconInfo provisionedBeaconInfo : storeInfo
											.getProvisionedBeacons()) {

										if (checkIfExists(
												provisionedBeaconInfo
														.getmMajor(),
												provisionedBeaconInfo
														.getmMinor(),
												provisionedBeaconInfo
														.getmBeaconUUID())) {
											storeInfo.setInBeaconRange(true);
											break;
										} else {
											storeInfo.setInBeaconRange(false);
										}
									}
								}
							}

						}

						String address = "";

						storeInfo.setmPostedAlertConsumerId(object
								.has(TAG_POSTED_BY_CONSUMER_ID) ? object
								.getString(TAG_POSTED_BY_CONSUMER_ID) : null);

						if (object.has(TAG_STORE_ADDRESS1)) {
							address += object.getString(TAG_STORE_ADDRESS1);
						}

						if (object.has(TAG_STORE_CITY)) {
							address += "," + object.getString(TAG_STORE_CITY);
						}
						if (object.has(TAG_STORE_STATE)) {
							address += "," + object.getString(TAG_STORE_STATE);
						}
						storeInfo
								.setmEndDate(object.has(TAG_ALERT_END_DATE) ? object
										.getString(TAG_ALERT_END_DATE) : null);

						storeInfo.setmStoreAddress(address);

						if (object.has(TAG_POSTED_CONSUMER_ID)) {
							storeInfo.setmPostedAlertConsumerId(object
									.getString(TAG_POSTED_CONSUMER_ID));
						}
						if (storeInfo.getmPostedAlertConsumerId() != null
								&& !storeInfo.getmPostedAlertConsumerId()
										.equalsIgnoreCase("null")) {
							storeInfo
									.setmPhoneNumber(object
											.has(TAG_POSTED_BY_CONSUMER_PHONE_NUMBER) ? object
											.getString(TAG_POSTED_BY_CONSUMER_PHONE_NUMBER)
											: null);
						} else {
							storeInfo.setmPhoneNumber(object
									.has(TAG_STORE_ASSOCIATE_PHONE_NO) ? object
									.getString(TAG_STORE_ASSOCIATE_PHONE_NO)
									: null);
						}

						storeInfo
								.setmDescription(object.has(TAG_ALERT_MESSAGE) ? object
										.getString(TAG_ALERT_MESSAGE) : null);

						storeInfo.setmProductLogo(object
								.has(TAG_PRODUCT_IMAGE_URL) ? object
								.getString(TAG_PRODUCT_IMAGE_URL) : null);
						storeInfo
								.setmRetailerId(object.has(TAG_RETAILER_ID) ? object
										.getString(TAG_RETAILER_ID) : null);
						storeInfo.setmStoreId(object.has(TAG_STORE_ID) ? object
								.getString(TAG_STORE_ID) : null);
						storeInfo
								.setmStoreLogo(object.has(TAG_STORE_IMAGE_URL) ? object
										.getString(TAG_STORE_IMAGE_URL) : null);
						storeInfo
								.setmStoreName(object.has(TAG_STORE_NAME) ? object
										.getString(TAG_STORE_NAME) : null);
						storeInfo
								.setmTitle(object.has(TAG_ALERT_TITLE) ? object
										.getString(TAG_ALERT_TITLE) : null);

						if (object.has(TAG_CONSUMER_FAVORITE)) {
							storeInfo.setFavorite(object
									.getBoolean(TAG_CONSUMER_FAVORITE));
						}
						storeInfo.setStoreLatitude(object
								.has(TAG_ALERT_LATITUDE) ? object
								.getDouble(TAG_ALERT_LATITUDE) : null);
						storeInfo.setStoreLongitude(object
								.has(TAG_ALERT_LONGITUDE) ? object
								.getDouble(TAG_ALERT_LONGITUDE) : null);

						double distanceInMeters = Utils.calculateDistance(
								sourceLatitude, sourceLongitude,
								storeInfo.getStoreLatitude(),
								storeInfo.getStoreLongitude(), 'M');
						storeInfo.setDistanceValue(Utils
								.convertMetrsAndFeets(distanceInMeters,context));

						if (object.has(TAG_BEACON_UUI)) {
							storeInfo.setmBeaconUUID(object
									.getString(TAG_BEACON_UUI));
						} else if (object.has(TAG_BEACON_UUID)) {
							storeInfo.setmBeaconUUID(object
									.getString(TAG_BEACON_UUID));
						} else {
							storeInfo.setmBeaconUUID("");
						}
						// storeInfo
						// .setmBeaconUUID(object.has(TAG_BEACON_UUID) ? object
						// .getString(TAG_BEACON_UUID) : "");
						storeInfo
								.setmMajor(object.has(TAG_BEACON_MAJOR) ? object
										.getString(TAG_BEACON_MAJOR) : "");
						storeInfo
								.setmMinor(object.has(TAG_BEACON_MINOR) ? object
										.getString(TAG_BEACON_MINOR) : "");

						// storeInfo.setInBeaconRange(checkIfExists(
						// storeInfo.getmMajor(), storeInfo.getmMinor(),
						// storeInfo.getmBeaconUUID()));
						storeInfos.add(storeInfo);
					}
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return storeInfos;
	}

	public static CheckUpdate parseCheckUpdatesResponse(String string) {
		CheckUpdate checkUpdate = new CheckUpdate();
		Log.i("SRT", "notification update"+string);
		try {
			JSONObject jsonObject = new JSONObject(string);
			checkUpdate.setmAreUpdatesAvailable(jsonObject
					.has(TAG_IS_AVAILABLE) ? (jsonObject.getString(
					TAG_IS_AVAILABLE).equalsIgnoreCase("t") ? true : false)
					: false);
			checkUpdate
					.setmMessage(jsonObject.has(TAG_CHECK_MESSAGE) ? jsonObject
							.getString(TAG_CHECK_MESSAGE) : null);
			checkUpdate.setmIsComputed(jsonObject
					.has(TAG_IS_COMPUTED) ? (jsonObject.getString(
							TAG_IS_COMPUTED).equalsIgnoreCase("true") ? true : false)
					: false);
			
			checkUpdate.setmBackGrndBtwnScanPeriod(jsonObject.has(TAG_CHECK_BACKGROUND_BETWEEN_SCAN_PERIOD) ? 
					jsonObject.getInt(TAG_CHECK_BACKGROUND_BETWEEN_SCAN_PERIOD):0);
			
			checkUpdate.setmBackGrndScanPeriod(jsonObject.has(TAG_CHECK_BACKGROUND_SCAN_PERIOD) ? 
					jsonObject.getInt(TAG_CHECK_BACKGROUND_SCAN_PERIOD):0);
	
			checkUpdate.setmIntervalChkForNewAlertsCloseByList(jsonObject.has(TAG_CHECK_INTERVAL_CHECK_FOR_NEW_ALERTS_CLOSEBY_LIST) ? 
					jsonObject.getInt(TAG_CHECK_INTERVAL_CHECK_FOR_NEW_ALERTS_CLOSEBY_LIST):0);
	
			checkUpdate.setmIntervalChkForNewAlertsBackGrndMonitor(jsonObject.has(TAG_INTERVAL_CHECK_FOR_NEW_ALERTS_BACKGROUND_MONITOR) ? 
					jsonObject.getInt(TAG_INTERVAL_CHECK_FOR_NEW_ALERTS_BACKGROUND_MONITOR):0);
	
			checkUpdate.setmNextRunTime(jsonObject.has(TAG_CHECK_NEXT_RUN_TIME) ? 
					jsonObject.getString(TAG_CHECK_NEXT_RUN_TIME):null);
	
			checkUpdate.setmSmartLocationComputingRate(jsonObject.has(TAG_CHECK_SMART_LOCATION_COMPUTING_RATE) ? 
					jsonObject.getInt(TAG_CHECK_SMART_LOCATION_COMPUTING_RATE):0);
	
			checkUpdate.setmDuration(jsonObject.has(TAG_CHECK_DURATION) ? 
					jsonObject.getInt(TAG_CHECK_DURATION):0);

			checkUpdate.setmNearestBeaconDistance(jsonObject.has(TAG_NEAREST_BEACON_DISTANCE) ? 
					jsonObject.getInt(TAG_NEAREST_BEACON_DISTANCE):0);

			if(jsonObject.has(TAG_ORDER_STATUS)){
				OrderStatus orderStatusObject=new OrderStatus();
				
				JSONObject orderStatusJsonObj=jsonObject.getJSONObject(TAG_ORDER_STATUS);
				orderStatusObject.setRequestedDelivery(orderStatusJsonObj.has(TAG_REQUESTEDDELIVERY)?
						orderStatusJsonObj.getString(TAG_REQUESTEDDELIVERY):null);
				orderStatusObject.setState(orderStatusJsonObj.has(TAG_STATE)?
						orderStatusJsonObj.getString(TAG_STATE):null);
				orderStatusObject.setExpectedDelivery(orderStatusJsonObj.has(TAG_EXPECTEDDELIVERY)?
						orderStatusJsonObj.getString(TAG_EXPECTEDDELIVERY):null);
				orderStatusObject.setItemPrice(orderStatusJsonObj.has(TAG_ITEMPRICE)?
						orderStatusJsonObj.getString(TAG_ITEMPRICE):null);
				orderStatusObject.setAddressLine3(orderStatusJsonObj.has(TAG_ADDRESSLINE3)?
						orderStatusJsonObj.getString(TAG_ADDRESSLINE3):null);
				orderStatusObject.setAddressLine2(orderStatusJsonObj.has(TAG_ADDRESSLINE2)?
						orderStatusJsonObj.getString(TAG_ADDRESSLINE2):null);
				orderStatusObject.setAddressLine1(orderStatusJsonObj.has(TAG_ADDRESSLINE1)?
						orderStatusJsonObj.getString(TAG_ADDRESSLINE1):null);
				orderStatusObject.setCity(orderStatusJsonObj.has(TAG_CITY)?
						orderStatusJsonObj.getString(TAG_CITY):null);
				orderStatusObject.setConsumerId(orderStatusJsonObj.has(TAG_CONSUMERID)?
						orderStatusJsonObj.getString(TAG_CONSUMERID):null);
				orderStatusObject.setTotalOrderPrice(orderStatusJsonObj.has(TAG_TOTALORDERPRICE)?
						orderStatusJsonObj.getString(TAG_TOTALORDERPRICE):null);
				orderStatusObject.setStatusId(orderStatusJsonObj.has(TAG_STATUSID)?
						orderStatusJsonObj.getString(TAG_STATUSID):null);
				orderStatusObject.setPincode(orderStatusJsonObj.has(TAG_PINCODE)?
						orderStatusJsonObj.getString(TAG_PINCODE):null);
				orderStatusObject.setConsumerName(orderStatusJsonObj.has(TAG_CONSUMERNAME)?
						orderStatusJsonObj.getString(TAG_CONSUMERNAME):null);
				orderStatusObject.setOfferItemName(orderStatusJsonObj.has(TAG_OFFERITEMNAME)?
						orderStatusJsonObj.getString(TAG_OFFERITEMNAME):null);
				orderStatusObject.setOfferItemId(orderStatusJsonObj.has(TAG_OFFERITEMID)?
						orderStatusJsonObj.getString(TAG_OFFERITEMID):null);
				orderStatusObject.setOrderQuantity(orderStatusJsonObj.has(TAG_ORDERQUANTITY)?
						orderStatusJsonObj.getString(TAG_ORDERQUANTITY):null);
				orderStatusObject.setOrderId(orderStatusJsonObj.has(TAG_ORDERID1)?
						orderStatusJsonObj.getString(TAG_ORDERID1):null);
				orderStatusObject.setStatusDesc(orderStatusJsonObj.has(TAG_STATUSDESC)?
						orderStatusJsonObj.getString(TAG_STATUSDESC):null);
				orderStatusObject.setStoreId(orderStatusJsonObj.has(TAG_ORDER_STOREID)?
						orderStatusJsonObj.getString(TAG_ORDER_STOREID):null);
				orderStatusObject.setStoreName(orderStatusJsonObj.has(TAG_ORDER_STORENAME)?
						orderStatusJsonObj.getString(TAG_ORDER_STORENAME):null);
				checkUpdate.setmOrderStatusObj(orderStatusObject);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return checkUpdate;
	}

	public static HashMap<String, String> parseCheckBeaconAlertResponse(
			String string) {
		HashMap<String, String> hashMap = new HashMap<String, String>();

		try {
			JSONObject jsonObject = new JSONObject(string);
			if (jsonObject.has("available")
					&& jsonObject.getString("available")
							.equalsIgnoreCase("yes")) {
				if (jsonObject.has("beacondetails")) {
					JSONObject beaconDetObject = jsonObject
							.getJSONObject("beacondetails");
					Iterator<?> iterator = beaconDetObject.keys();
					if (iterator != null) {
						while (iterator.hasNext()) {
							String key = (String) iterator.next();
							String value = beaconDetObject.getString(key);

							Utils.showLog("Key-->" + key + ",Value-->" + value);
							hashMap.put(key, value);
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return hashMap;
	}

	public static HashMap<String, List<StoreInfo>> parseSearchResults(
			Context context, String response) {

		buyopicConsumerDatabase = BuyopicConsumerDatabase
				.shareInstance(context);
		HashMap<String, List<StoreInfo>> hashMap = new HashMap<String, List<StoreInfo>>();
		try {
			JSONObject jsonObject = new JSONObject(response);
			if (jsonObject.has(TAG_STATUS)
					&& jsonObject.getString(TAG_STATUS).equalsIgnoreCase(
							STATUS_OK)) {
				if (jsonObject.has(TAG_CLOSE_BY_ALERTS)) {
					JSONArray jsonArray = jsonObject
							.getJSONArray(TAG_CLOSE_BY_ALERTS);
					List<StoreInfo> closeBySearchResults = parseSearchResultsArray(
							jsonArray, true,context,jsonObject);
					hashMap.put("close_by", closeBySearchResults);
				}

				if (jsonObject.has(TAG_IN_THE_AREA_ALERTS)) {
					JSONArray jsonArray = jsonObject
							.getJSONArray(TAG_IN_THE_AREA_ALERTS);
					List<StoreInfo> closeBySearchResults = parseSearchResultsArray(
							jsonArray, false,context,jsonObject);
					hashMap.put("in_the_area", closeBySearchResults);
				}

			}
		} catch (JSONException je) {
			je.printStackTrace();
		}
		return hashMap;
	}

	private static List<StoreInfo> parseSearchResultsArray(JSONArray jsonArray,
			boolean isCloseByResults,Context context, JSONObject jsonObject) {
	//	List<StoreInfo> storeInfos = null;
		List<StoreInfo> storeInfos = new ArrayList<StoreInfo>();
		try {

			if (jsonArray != null && jsonArray.length() > 0) {
			//	storeInfos = new ArrayList<StoreInfo>();
				for (int i = 0; i < jsonArray.length(); i++) {
					StoreInfo storeInfo = new StoreInfo();
					JSONObject searchResultJObject = jsonArray.getJSONObject(i);
					storeInfo.setmProductLogo(searchResultJObject
							.has(TAG_ALERT_IMAGE) ? searchResultJObject
							.getString(TAG_ALERT_IMAGE) : null);

					String address = (searchResultJObject
							.has(TAG_STORE_ADDRESS) ? searchResultJObject
							.getString(TAG_STORE_ADDRESS) : "")
							+ ", "
							+ (searchResultJObject.has(TAG_STORE_CITY) ? searchResultJObject
									.getString(TAG_STORE_CITY) : "")
							+ ", "
							+ (searchResultJObject.has(TAG_STORE_STATE) ? searchResultJObject
									.getString(TAG_STORE_STATE) : "");

					storeInfo
							.setmStoreId(searchResultJObject.has("store_id") ? searchResultJObject
									.getString("store_id") : null);

					storeInfo.setmRetailerId(searchResultJObject
							.has("retailer_id") ? searchResultJObject
							.getString("retailer_id") : null);

					//RRT
					storeInfo.setmPostedAlertConsumerId(searchResultJObject
							.has("posted_consumer_id") ? searchResultJObject
							.getString("posted_consumer_id") : null);
					//RRT
					
					storeInfo.setmStoreAddress(address);
					// associate_phone_number
					storeInfo
							.setmPhoneNumber(searchResultJObject
									.has(TAG_ASSOCIATE_PHONE_NUMBER) ? searchResultJObject
									.getString(TAG_ASSOCIATE_PHONE_NUMBER)
									: null);
					storeInfo.setmDescription(searchResultJObject
							.has("alert_message") ? searchResultJObject
							.getString("alert_message") : null);
					storeInfo.setmTitle(searchResultJObject
							.has(TAG_OFFER_TITLE) ? searchResultJObject
							.getString(TAG_OFFER_TITLE) : null);
					storeInfo.setmPrice(searchResultJObject
							.has(TAG_OFFER_PRICE) ? searchResultJObject
							.getString(TAG_OFFER_PRICE) : null);
					storeInfo.setmStoreName(searchResultJObject
							.has(TAG_MERCHANT_NAME) ? searchResultJObject
							.getString(TAG_MERCHANT_NAME) : null);
					storeInfo
							.setmOfferId(searchResultJObject.has(TAG_OFFER_ID) ? searchResultJObject
									.getString(TAG_OFFER_ID) : null);
					storeInfo.setmBeaconUUID(searchResultJObject
							.has(TAG_BEACON_UUI) ? searchResultJObject
							.getString(TAG_BEACON_UUI) : null);
					storeInfo.setmMajor(searchResultJObject
							.has(TAG_BEACON_MAJOR) ? searchResultJObject
							.getString(TAG_BEACON_MAJOR) : null);
					storeInfo.setmMinor(searchResultJObject
							.has(TAG_BEACON_MINOR) ? searchResultJObject
							.getString(TAG_BEACON_MINOR) : null);
					storeInfo
							.setDistance(searchResultJObject.has(TAG_DISTANCE) ? searchResultJObject
									.getDouble(TAG_DISTANCE) : null);
					storeInfo.setDistanceValue(Utils
							.convertMetrsAndFeets(storeInfo.getDistance(),context));
					if (isCloseByResults) {
						boolean isInBeaconRange = checkIfExists(
								storeInfo.getmMajor(), storeInfo.getmMinor(),
								storeInfo.getmBeaconUUID());
						storeInfo.setInBeaconRange(isInBeaconRange);
					}
					storeInfos.add(storeInfo);
				}
			}
			 if(jsonObject.has(TAG_GOOGLE_STORES)){
					JSONArray jsonArray1 = jsonObject.getJSONArray(TAG_GOOGLE_STORES);
					if (jsonArray1 != null && jsonArray1.length() > 0) {

						for (int i = 0; i < jsonArray1.length(); i++) {
							List<GoogleStoreInfo> listObjstoreInfo=new ArrayList<GoogleStoreInfo>();
							GoogleStoreInfo googleStoreInfo = new GoogleStoreInfo();
							StoreInfo storeInfo=new StoreInfo();
							JSONObject object = jsonArray1.getJSONObject(i);
							googleStoreInfo.setmDistance(object.has(TAG_DISTANCE) ? object
										.getDouble(TAG_DISTANCE) : null);
							googleStoreInfo.setmVicinity(object.has(TAG_VICINITY) ? object
									.getString(TAG_VICINITY) : null);
							googleStoreInfo.setmPlaceId(object.has(TAG_PLACEID) ? object
									.getString(TAG_PLACEID) : null);
							googleStoreInfo.setmName(object.has(TAG_NAME) ? object
									.getString(TAG_NAME) : null);
							googleStoreInfo.setmLongitude(object.has(TAG_LONGITUDE) ? object
									.getString(TAG_LONGITUDE) : null);
							googleStoreInfo.setmLatitude(object.has(TAG_LATITUDE) ? object
									.getString(TAG_LATITUDE) : null);
							googleStoreInfo.setmIconURL(object.has(TAG_ICONURL) ? object
									.getString(TAG_ICONURL) : null);
							listObjstoreInfo.add(googleStoreInfo);
							storeInfo.setmGoogleStoreInfo(listObjstoreInfo);

							storeInfos.add(storeInfo);
							
						}
					}
			}
		} catch (JSONException je) {
			je.printStackTrace();
		}
		return storeInfos;
	}

	

	public static List<String> parseSearchHistoryResponse(String string) {
		List<String> searchList = new ArrayList<String>();
		try {
			JSONObject jsonObject = new JSONObject(string);
			if (jsonObject.has(TAG_STATUS)
					&& jsonObject.getString(TAG_STATUS).equalsIgnoreCase(
							STATUS_OK)) {
				if (jsonObject.has(TAG_SEARCH_PARAMS)) {
					JSONArray jsonArray = jsonObject
							.getJSONArray(TAG_SEARCH_PARAMS);
					if (jsonArray != null && jsonArray.length() > 0) {
						for (int i = 0; i < jsonArray.length(); i++) {
							String searchString = jsonArray.getString(i);
							searchList.add(searchString);
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return searchList;
	}

	private static boolean checkIfExists(String major, String minor, String uuid) {

		if (major != null && minor != null) {
			return buyopicConsumerDatabase.checkBeaconExistsOrNot(major, minor,
					uuid);
		} else {
			return false;
		}
	}

	public static String parseCreateAlertResponse(String string) {
		try {
			JSONObject jsonObject = new JSONObject(string);
			return jsonObject.has(TAG_MESSAGE) ? jsonObject
					.getString(TAG_MESSAGE) : "Unknown Error";
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "Unknown Error";
	}

	public static AlertDetail parseConsumerAlertDetail(String string) {
		AlertDetail alertDetail = null;

		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(string);

			if (jsonObject.has(TAG_STATUS)
					&& jsonObject.getString(TAG_STATUS).equalsIgnoreCase(
							STATUS_OK)) {
				if (jsonObject.has(TAG_ALERT)) {
					alertDetail = new AlertDetail();
					JSONObject jAlertObj = jsonObject.getJSONObject(TAG_ALERT);
					Alert alert = new Alert();
					alert.setmEndDate(jAlertObj.has(TAG_END_DATE) ? getFormattedTime(jAlertObj
							.getString(TAG_END_DATE)) : null);

					DecimalFormat decimalFormat = new DecimalFormat("#0.00");
					if (jAlertObj.has(TAG_PRICE)) {
						String mPrice = decimalFormat.format(Double
								.valueOf(jAlertObj.getString(TAG_PRICE)));
						alert.setmPrice(mPrice);
					}
					alert.setmOfferMessage(jAlertObj.has(TAG_ALERT_MESSAGE) ? jAlertObj
							.getString(TAG_ALERT_MESSAGE) : null);
					alert.setmIsActivated(jAlertObj.has(TAG_STATUS) ? jAlertObj
							.getBoolean(TAG_STATUS) : false);
					alert.setmOfferTitle(jAlertObj.has(TAG_ALERT_TITLE) ? jAlertObj
							.getString(TAG_ALERT_TITLE) : null);
					alert.setmStartDate(jAlertObj.has(TAG_START_DATE) ? getFormattedTime(jAlertObj
							.getString(TAG_START_DATE)) : null);
					alert.setmThumbnailUrl(jAlertObj.has(TAG_ALERT_IMAGE) ? jAlertObj
							.getString(TAG_ALERT_IMAGE) : null);
					alert.setFavorite(jAlertObj.has(TAG_CONSUMER_FAVORITE) ? jAlertObj
							.getBoolean(TAG_CONSUMER_FAVORITE) : false);
					alert.setmOfferId(jAlertObj.has(TAG_ALERT_ID) ? jAlertObj
							.getString(TAG_ALERT_ID) : null);
					alert.setmPostedConsumerId(jAlertObj
							.has(TAG_POSTED_CONSUMER_ID) ? jAlertObj
							.getString(TAG_POSTED_CONSUMER_ID) : null);

					if (jAlertObj.has(TAG_POSTED_BY_CONSUMER_PHONE_NO)) {
						alertDetail.setmPhoneNUmber(jAlertObj
								.getString(TAG_POSTED_BY_CONSUMER_PHONE_NO));
					}

					if (jAlertObj.has(TAG_POSTED_BY_CONSUMER_IMAGE_URL)) {
						alertDetail.setmStoreLogo(jAlertObj
								.getString(TAG_POSTED_BY_CONSUMER_IMAGE_URL));
					}
					alertDetail.setAlert(alert);
				}
				alertDetail
						.setmStoreName(jsonObject.has(TAG_CONSUMER_NAME) ? jsonObject
								.getString(TAG_CONSUMER_NAME) : null);
				String storeAddress = "";

				if (jsonObject.has(TAG_CONSUMER_ADDRESS1)) {
					storeAddress += jsonObject.getString(TAG_CONSUMER_ADDRESS1);
				}

				if (jsonObject.has(TAG_CONSUMER_CITY)) {
					storeAddress += ","
							+ jsonObject.getString(TAG_CONSUMER_CITY);
				}

				if (jsonObject.has(TAG_CONSUMER_STATE)) {
					storeAddress += ","
							+ jsonObject.getString(TAG_CONSUMER_STATE);
				}

				alertDetail.setmStoreAddress(storeAddress);
				

				/*parsing new address data related to map changes*/
				
				if (jsonObject.has(TAG_USER_GOOGLEICONIMAGE)) {
					alertDetail.setmGoogleIconImage(jsonObject
							.getString(TAG_USER_GOOGLEICONIMAGE));
				}
				if (jsonObject.has(TAG_USER_GOOGLEPLACEID)) {
					alertDetail.setmGooglePlaceId(jsonObject
							.getString(TAG_USER_GOOGLEPLACEID));
				}
				if (jsonObject.has(TAG_CONSUMER_ADDRESS_IMAGE_URL)) {
					alertDetail.setmConsumerAddressImageUrl(jsonObject
							.getString(TAG_CONSUMER_ADDRESS_IMAGE_URL));
				}
				if (jsonObject.has(TAG_CONSUMER_ADDRESS2)) {
					alertDetail.setmConsumerAddress2(jsonObject
							.getString(TAG_CONSUMER_ADDRESS2));
				}
				if (jsonObject.has(TAG_CONSUMER_POSTAL_CODE)) {
					alertDetail.setmConsumerPostalCode(jsonObject
							.getString(TAG_CONSUMER_POSTAL_CODE));
				}
				

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return alertDetail;
	}

	private static String getFormattedTime(String string) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.US);
		try {
			Date date = dateFormat.parse(string);
			SimpleDateFormat dateFormat2 = new SimpleDateFormat(
					"MM/dd/yyyy hh:mm a", Locale.US);
			return dateFormat2.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static List<HashMap<String, String>> parseSharedWithMeResponse(
			Context mContext, String response) {
		buyopicConsumerDatabase = BuyopicConsumerDatabase
				.shareInstance(mContext);
		BuyOpic buyOpic=(BuyOpic) mContext.getApplicationContext();
		List<HashMap<String, String>> sharedWithMeList = new ArrayList<HashMap<String, String>>();
		try {
			JSONObject jsonObject = new JSONObject(response);
			if (jsonObject.has(TAG_STATUS)
					&& jsonObject.getString(TAG_STATUS).equalsIgnoreCase(
							STATUS_OK)) {

				if (jsonObject.has(TAG_ALERTS)) {
					JSONArray jsonArray = jsonObject.getJSONArray(TAG_ALERTS);
					if (jsonArray != null && jsonArray.length() > 0) {
						for (int i = 0; i < jsonArray.length(); i++) {
							HashMap<String, String> hashMap = new HashMap<String, String>();
							JSONObject jsonObject2 = jsonArray.getJSONObject(i);
							Iterator<?> iterator = jsonObject2.keys();
							while (iterator.hasNext()) {
								String key = (String) iterator.next();
								Object object = jsonObject2.get(key);

								if (object instanceof JSONArray) {
									JSONArray jsonArray2 = jsonObject2
											.getJSONArray(key);
									if (jsonArray2 != null
											&& jsonArray2.length() > 0) {
										for (int j = 0; j < jsonArray2.length(); j++) {
											JSONObject jsonObject3 = jsonArray2
													.getJSONObject(j);
											String uuid = jsonObject3
													.has(TAG_BEACON_UUID) ? jsonObject3
													.getString(TAG_BEACON_UUID)
													: "";
											String minor = jsonObject3
													.has(TAG_BEACON_MINOR) ? jsonObject3
													.getString(TAG_BEACON_MINOR)
													: "";
											String major = jsonObject3
													.has(TAG_BEACON_MAJOR) ? jsonObject3
													.getString(TAG_BEACON_MAJOR)
													: "";
											if (checkIfExists(major, minor,
													uuid)) {
												hashMap.put(
														KEY_ISINBEACONRANGE,
														String.valueOf(true));
												break;
											} else {
												hashMap.put(
														KEY_ISINBEACONRANGE,
														String.valueOf(false));
											}
										}
									}
								} else {
									String value = jsonObject2.getString(key);
									hashMap.put(key, value);
								}
								
							}
							double distanceInMeters = Utils.calculateDistance(
									buyOpic.getmSourceLatitude(),buyOpic.getmSourceLongitude(),
									Double.parseDouble(hashMap.get(TAG_ALERT_LATITUDE)),
									Double.parseDouble(hashMap.get(TAG_ALERT_LONGITUDE)), 'M');
							hashMap.put(TAG_DISTANCE,Utils
									.convertMetrsAndFeets(distanceInMeters,mContext));

							sharedWithMeList.add(hashMap);

						}
					}

				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return sharedWithMeList;
	}

	public static String extractIdFromGeneratedUrl(String response) {
		String processId = null;
		try {
			JSONObject jsonObject = new JSONObject(response);

			if (jsonObject.has(TAG_STATUS)
					&& jsonObject.getString(TAG_STATUS).equalsIgnoreCase(
							STATUS_OK)) {
				processId = jsonObject.has(TAG_PROCESS_ID) ? jsonObject
						.getString(TAG_PROCESS_ID) : null;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return processId;

	}

	public static String extractUrlFromGeneratedUrl(String response) {
		String processId = null;
		try {
			JSONObject jsonObject = new JSONObject(response);

			if (jsonObject.has(TAG_STATUS)
					&& jsonObject.getString(TAG_STATUS).equalsIgnoreCase(
							STATUS_OK)) {
				processId = jsonObject.has(TAG_PROCESS_URL) ? jsonObject
						.getString(TAG_PROCESS_URL) : null;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return processId;

	}
	public static HashMap<String, String>  parseSubmitOrderStatus(String object) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		try {
			JSONObject jsonObject = new JSONObject(object);
			if (jsonObject.has(TAG_STATUS)) {
				hashMap.put(TAG_STATUS, jsonObject.getString(TAG_STATUS));
			}
			if (jsonObject.has(TAG_ORDERID)) {
				hashMap.put(TAG_ORDERID, jsonObject.getString(TAG_ORDERID));
			}
		}catch (JSONException e) {
			e.printStackTrace();
		}
		return hashMap;
	}
	public static ConsumerDetails parseConsumerDetailsForOrderStatusAddress(String object){
		ConsumerDetails mConsumerDetails=null;
		try {
			JSONObject jsonObject = new JSONObject(object);
			
		
			if(jsonObject.has(TAG_STATUS)&&jsonObject.getString(TAG_STATUS).equalsIgnoreCase(STATUS_OK)){
				
				if(jsonObject.has(TAG_CONSUMER_DATA)){
					mConsumerDetails=new ConsumerDetails();
					JSONObject jConsumerDataObj=jsonObject.getJSONObject(TAG_CONSUMER_DATA);
					
					mConsumerDetails.setPhonenumber(jConsumerDataObj.has(TAG_PHONENUMBER)?
							jConsumerDataObj.getString(TAG_PHONENUMBER):null);
					mConsumerDetails.setAddresscountry(jConsumerDataObj.has(TAG_ADDRESSCOUNTRY)?
							jConsumerDataObj.getString(TAG_ADDRESSCOUNTRY):null);
					mConsumerDetails.setConsumer_image_url(jConsumerDataObj.has(TAG_CONSUMER_IMAGE_URL)?
							jConsumerDataObj.getString(TAG_CONSUMER_IMAGE_URL):null);
					mConsumerDetails.setFirstname(jConsumerDataObj.has(TAG_FIRSTNAME)?
							jConsumerDataObj.getString(TAG_FIRSTNAME):null);
					mConsumerDetails.setTransPin(jConsumerDataObj.has(TAG_TRANSPIN)?
							jConsumerDataObj.getString(TAG_TRANSPIN):null);
					mConsumerDetails.setConsumerId(jConsumerDataObj.has(TAG_CONSUMERID)?
							jConsumerDataObj.getString(TAG_CONSUMERID):null);
					mConsumerDetails.setAddressline2(jConsumerDataObj.has(TAG_ADDRESSLINE2_CONSUMERDETAILS)?
							jConsumerDataObj.getString(TAG_ADDRESSLINE2_CONSUMERDETAILS):null);
					mConsumerDetails.setGooglePlaceId(jConsumerDataObj.has(TAG_GOOGLEPLACEID)?
							jConsumerDataObj.getString(TAG_GOOGLEPLACEID):null);
					mConsumerDetails.setEmailId(jConsumerDataObj.has(TAG_EMAILID)?
							jConsumerDataObj.getString(TAG_EMAILID):null);
					mConsumerDetails.setAddressline1(jConsumerDataObj.has(TAG_ADDRESSLINE1_CONSUMERDETAILS)?
							jConsumerDataObj.getString(TAG_ADDRESSLINE1_CONSUMERDETAILS):null);
					mConsumerDetails.setAddresszip(jConsumerDataObj.has(TAG_ADDRESSZIP)?
							jConsumerDataObj.getString(TAG_ADDRESSZIP):null);
					mConsumerDetails.setRegistration_confirmed(jConsumerDataObj.has(TAG_REGISTRATION_CONFIRMED)?
							jConsumerDataObj.getString(TAG_REGISTRATION_CONFIRMED):null);
					mConsumerDetails.setConsumerAddressImageURL(jConsumerDataObj.has(TAG_CONSUMERADDRESSIMAGEURL)?
							jConsumerDataObj.getString(TAG_CONSUMERADDRESSIMAGEURL):null);
					mConsumerDetails.setAddressstate(jConsumerDataObj.has(TAG_ADDRESSSTATE)?
							jConsumerDataObj.getString(TAG_ADDRESSSTATE):null)	;
					mConsumerDetails.setAddresscity(jConsumerDataObj.has(TAG_ADDRESSCITY)?
							jConsumerDataObj.getString(TAG_ADDRESSCITY):null)	;
					mConsumerDetails.setConsumer_latitude(jConsumerDataObj.has(TAG_CONSUMER_LATITUDE)?
							jConsumerDataObj.getString(TAG_CONSUMER_LATITUDE):null)	;
					mConsumerDetails.setConsumer_longitude(jConsumerDataObj.has(TAG_CONSUMER_LONGITUDE)?
							jConsumerDataObj.getString(TAG_CONSUMER_LONGITUDE):null)	;
					mConsumerDetails.setConsumerAddressImageUrlFlag(jConsumerDataObj.has(TAG_CONSUMERADDRESSIMAGEURLFLAG)?
							jConsumerDataObj.getString(TAG_CONSUMERADDRESSIMAGEURLFLAG):null)	;
					mConsumerDetails.setGoogleIconImage(jConsumerDataObj.has(TAG_GOOGLEICONIMAGE)?
							jConsumerDataObj.getString(TAG_GOOGLEICONIMAGE):null)	;
					mConsumerDetails.setConsumer_image_url(jConsumerDataObj.has(TAG_CONSUMER_IMAGE_URL_CONSUMERDETAILS)?
							jConsumerDataObj.getString(TAG_CONSUMER_IMAGE_URL_CONSUMERDETAILS):null)	;
					mConsumerDetails.setAccount_type(jConsumerDataObj.has(TAG_ACCOUNT_TYPE)?
							jConsumerDataObj.getString(TAG_ACCOUNT_TYPE):null)	;
					
					
					
				}
			}
		}
		
		catch(JSONException e){
			e.printStackTrace();
			
		}
		
		
		return mConsumerDetails;
	}
	public static List<OrderList> parseOrderListStatus(String response) {
		List<OrderList> orderStatusList = new ArrayList<OrderList>();

		try {
			JSONObject jsonObject = new JSONObject(response);

			if (jsonObject.has(TAG_MY_ORDERS_LIST)) {
				JSONArray jsonArray = jsonObject.getJSONArray(TAG_MY_ORDERS_LIST);
				if (jsonArray != null && jsonArray.length() > 0) {
					for (int i = 0; i < jsonArray.length(); i++) {
						OrderList mOrderListObj = new OrderList();
						JSONObject jsonObject2 = jsonArray.getJSONObject(i);
						
						mOrderListObj.setRequestedDelivery(jsonObject2
								.has(TAG_REQUESTEDDELIVERY) ? jsonObject2
								.getString(TAG_REQUESTEDDELIVERY) : null);
						
						mOrderListObj
						.setStoreName(jsonObject2.has(TAG_STORENAME) ? jsonObject2
								.getString(TAG_STORENAME) : null);
						
						mOrderListObj
						.setRetailerId(jsonObject2.has(TAG_RETAILERID) ? jsonObject2
								.getString(TAG_RETAILERID) : null);
						
						mOrderListObj
								.setState(jsonObject2.has(TAG_STATE) ? jsonObject2
										.getString(TAG_STATE) : null);
						
						mOrderListObj.setExpectedDelivery(jsonObject2
								.has(TAG_EXPECTEDDELIVERY) ? jsonObject2
								.getString(TAG_EXPECTEDDELIVERY) : null);
						mOrderListObj.setAddressLine3(jsonObject2
								.has(TAG_ADDRESSLINE3) ? jsonObject2
								.getString(TAG_ADDRESSLINE3) : null);
						
						mOrderListObj.setItemPrice(jsonObject
								.has(TAG_ITEMPRICE) ? jsonObject2
								.getString(TAG_ITEMPRICE) : null);
						
							mOrderListObj.setAddressLine2(jsonObject2
								.has(TAG_ADDRESSLINE2) ? jsonObject2
								.getString(TAG_ADDRESSLINE2) : null);
						
						mOrderListObj.setAddressLine1(jsonObject2
								.has(TAG_ADDRESSLINE1) ? jsonObject2
								.getString(TAG_ADDRESSLINE1) : null);
						
						mOrderListObj.setTotalOrderPrice(jsonObject2
								.has(TAG_TOTALORDERPRICE) ? jsonObject2
								.getString(TAG_TOTALORDERPRICE) : null);
						mOrderListObj
						.setCity(jsonObject2.has(TAG_CITY) ? jsonObject2
								.getString(TAG_CITY) : null);
						
						mOrderListObj.setConsumerId(jsonObject2
								.has(TAG_CONSUMERID) ? jsonObject2
								.getString(TAG_CONSUMERID) : null);
						
						mOrderListObj.setOfferItemName(jsonObject2
								.has(TAG_OFFERITEMNAME) ? jsonObject2
								.getString(TAG_OFFERITEMNAME) : null);
						mOrderListObj
						.setConsumerPhoneNo(jsonObject2.has(TAG_CONSUMERPHONENO) ? jsonObject2
								.getString(TAG_CONSUMERPHONENO) : null);
						
						mOrderListObj.setConsumerName(jsonObject2
								.has(TAG_CONSUMERNAME) ? jsonObject2
								.getString(TAG_CONSUMERNAME) : null);
						
							mOrderListObj
								.setPincode(jsonObject2.has(TAG_PINCODE) ? jsonObject2
										.getString(TAG_PINCODE) : null);
							mOrderListObj
							.setStatusId(jsonObject2.has(TAG_STATUSID) ? jsonObject2
									.getString(TAG_STATUSID) : null);
					
							mOrderListObj
						.setConsumerMailId(jsonObject2.has(TAG_CONSUMERMAILID) ? jsonObject2
								.getString(TAG_CONSUMERMAILID) : null);
						
							mOrderListObj.setOfferItemId(jsonObject2
								.has(TAG_OFFERITEMID) ? jsonObject2
								.getString(TAG_OFFERITEMID) : null);
						mOrderListObj.setStoreId(jsonObject2.has(TAG_ORDER_STOREID)?
								jsonObject2.getString(TAG_ORDER_STOREID):null);
						
						mOrderListObj.setOrderQuantity(jsonObject2
								.has(TAG_ORDERQUANTITY) ? jsonObject2
								.getString(TAG_ORDERQUANTITY) : null);
						
						mOrderListObj.setStatusDesc(jsonObject2
								.has(TAG_STATUSDESC) ? jsonObject2
								.getString(TAG_STATUSDESC) : null);
						
						mOrderListObj
								.setOrderId(jsonObject2.has(TAG_ORDERID) ? jsonObject2
										.getString(TAG_ORDERID) : null);
						
						orderStatusList.add(mOrderListObj);
					}
				}
			}



		} catch (JSONException e) {
			e.printStackTrace();
		}

		return orderStatusList;
	}

}
