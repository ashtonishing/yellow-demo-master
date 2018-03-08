package com.buyopic.android.utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.buyopic.android.beacon.R;
import com.buyopic.android.network.BuyopicNetworkCallBack;
import com.buyopic.android.network.BuyopicNetworkServiceManager;
import com.buyopic.android.network.JsonResponseParser;
import com.buyopic.android.radius.BuyOpic;

public class Utils {

	public static final String KEY_POSTED_CONSUMER_ID = "posted_consumer_id";
	public static final String KEY_SHARED_BY_CONSUMER_ID = "shared_by_consumer_id";
	private static final String SHARE_MIME_TYPE = "text/plain";
	private static final String TAG_NAME = "BuyOpic";
	private static ProgressDialog progressDialog;

	public static void showLog(String message) {
		Log.v(TAG_NAME, message);
	}

	public static void showToast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	public static void showProgressDialog(Context context) {
		try {
			if (progressDialog == null) {
				progressDialog = ProgressDialog.show(context, "",
						"Please wait..", false, true);
			}
			progressDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void dismissProgressDialog() {
		try {
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void overrideFonts(final Context context, final View v) {
		try {

			if (v instanceof ViewGroup) {
				ViewGroup vg = (ViewGroup) v;
				for (int i = 0; i < vg.getChildCount(); i++) {
					View child = vg.getChildAt(i);
					overrideFonts(context, child);
				}
			} else if (v instanceof Button) {
				Typeface typeFace = Typeface.createFromAsset(
						context.getAssets(), "Tahoma.ttf");
				((Button) v).setTypeface(typeFace);
			} else if (v instanceof TextView) {
				TextView textView = (TextView) v;
				if (textView.getTypeface() != null
						&& textView.getTypeface().getStyle() == Typeface.BOLD) {
					Typeface typeFace = Typeface.createFromAsset(
							context.getAssets(), "tahomabd.ttf");
					textView.setTypeface(typeFace);
				} else {
					Typeface typeFace = Typeface.createFromAsset(
							context.getAssets(), "Tahoma.ttf");
					textView.setTypeface(typeFace);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<String> getStrings() {
		List<String> stringList = new ArrayList<String>();
		stringList.add("Home");
		// stringList.add("Logout");
		return stringList;
	}

	public final static Pattern emailPattern = Pattern
			.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");
	//no of minutes for beacon to be deleted
	public static final int CONSTANT_TIME_DIFF_BEACON = 3;

	public static boolean isValidEmail(EditText email) {
		return emailPattern.matcher(email.getText().toString().trim())
				.matches();
	}

	/**
	 * 
	 * @param lat1
	 *            Source Latitude
	 * @param lon1
	 *            Source Longitude
	 * @param lat2
	 *            Destination Latitude
	 * @param lon2
	 *            Destination Longitude
	 * @param unit
	 *            M for Meters and K for Kilometers and N for Miles
	 * @return
	 */
	public static double calculateDistance(double lat1, double lon1,
			double lat2, double lon2, char unit) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
				* Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		if (unit == 'K') {
			dist = dist * 1.609344;
		} else if (unit == 'N') {
			dist = dist * 0.8684;
		} else if (unit == 'M') {
			dist = dist * 1.609344 * 1000;

		}
		return (dist);
	}

	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	/* :: This function converts decimal degrees to radians : */
	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	/* :: This function converts radians to decimal degrees : */
	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	private static double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}

	public static String convertMetrsAndFeets(double d,Context context) {
		String result = "N/A";
		try {
			DecimalFormat decimalFormat = new DecimalFormat("#0.00");
			String formattedMilesValue = decimalFormat.format(d);
			double metersValue = Double.parseDouble(formattedMilesValue);
			double feetValue = metersValue * 3.2808;
			Utils.showLog("MetersValue::" + metersValue);
			
			String countryname=getCountryName(context);
			
			if (countryname != null && countryname.equalsIgnoreCase("IN")) {
				if (metersValue > 500) {
					result = String.valueOf(decimalFormat.format(metersValue/1000))+ " Km";
				} else {
					result = String.valueOf((int) metersValue) + "meters";
				}
			} else {
				if (feetValue > 500) {
					double milesValue = feetValue * 0.000189394;
					decimalFormat = new DecimalFormat("#0.0");
					result = String.valueOf(decimalFormat.format(milesValue))
							+ " Mi";
				} else {
					// decimalFormat = new DecimalFormat("#0.0");
					result = String.valueOf(((int) d)) + " ft";
					// result = String.valueOf(feetValue) + " Ft";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void callPhone(Context context, String phoneNumber) {
		if (phoneNumber != null) {
			Utils.showLog("Calling Phone NUmber" + phoneNumber);
			String uri = "tel:" + phoneNumber.trim();
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.setData(Uri.parse(uri));
			context.startActivity(intent);
		} else {
			Utils.showToast(context, "Invalid Phone Number");
		}
	}

	public static void shareOffer(final Context context, String offerId,
			String consumerId, String postedConsumerId, String storeId,
			String retailerId) {
		final BuyOpic buyOpic = (BuyOpic) context.getApplicationContext();
		BuyopicNetworkServiceManager buyopicNetworkServiceManager = BuyopicNetworkServiceManager
				.getInstance(context);

		String url = Constants.SHARE_BASE_URL + JsonResponseParser.TAG_OFFER_ID
				+ "=" + offerId + "&" + KEY_SHARED_BY_CONSUMER_ID + "="
				+ consumerId + "&" + KEY_POSTED_CONSUMER_ID + "="
				+ postedConsumerId + "&" + JsonResponseParser.TAG_STORE_ID
				+ "=" + storeId + "&" + JsonResponseParser.TAG_RETAILER_ID
				+ "=" + retailerId;
//		Utils.showLog("------>" + url);
		buyopicNetworkServiceManager.sendGenerateUrlProcessIdRequest(
				Constants.REQUEST_GENERATE_URL, url,
				new BuyopicNetworkCallBack() {

					@Override
					public void onSuccess(int requestCode, Object object) {
						if (requestCode == Constants.REQUEST_GENERATE_URL) {
							String processId = JsonResponseParser
									.extractIdFromGeneratedUrl((String) object);
							if (processId != null) {
								
								String url = Constants.SHARE_BYPASS_BASE_URL
										+ JsonResponseParser.TAG_PROCESS_TYPE
										+ "=share&"
										+ JsonResponseParser.TAG_PROCESS_ID
										+ "=" + processId;
								Intent sendIntent = new Intent();
//								String htmlText="<a href="+"\""+url+"\">"+"Click Here</a>";
								sendIntent.setAction(Intent.ACTION_SEND);
								sendIntent.putExtra(Intent.EXTRA_SUBJECT,
										context.getString(
												R.string.share_offer_subject,
												buyOpic.getmConsumerName()));
								sendIntent
										.putExtra(
												Intent.EXTRA_TEXT,
												context.getResources()
														.getString(
																R.string.share_offer_message_text)
														+ " "
														+ url
														+ "\""
														+ context
																.getString(R.string.play_store_link));
/*								sendIntent
								.putExtra(
										Intent.EXTRA_TEXT,Html.fromHtml(
												context.getResources()
												.getString(
														R.string.share_offer_message_text)
														+ " "
														+ htmlText
														+ "\""
														+ context
														.getString(R.string.play_store_link)));
*/								sendIntent.setType(SHARE_MIME_TYPE);
								context.startActivity(Intent.createChooser(
										sendIntent, "Send to"));
							}
						}
					}

					@Override
					public void onFailure(int requestCode, String message) {

					}
				});

	}

	public static void showDialog(Context context, String message) {
		AlertDialog.Builder builder = new Builder(context);
		try {
			builder.setMessage(message);
			builder.setPositiveButton("OK", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			builder.create().show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void showDialogFinish(final Context context, String message) {
		AlertDialog.Builder builder = new Builder(context);
		try {
			builder.setMessage(message);
			builder.setPositiveButton("OK", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					((Activity)context).finish();
				}
			});

			builder.create().show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static String  getCountryName(Context context){

	    String CountryID="";

	   TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	          //getSimCountryIso
	    CountryID= manager.getSimCountryIso().toUpperCase();
	    
		return CountryID;
	}
	  public final static boolean isInternetOn(Context context) {
	         
	        // get Connectivity Manager object to check connection
	        ConnectivityManager connec =  
	                       (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	         
	           // Check for network connections
	            if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
	                 connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
	                 connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
	                 connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {
	                
	                // if connected with internet
	                 
	             //   Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();
	                return true;
	                 
	            } else if ( 
	              connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
	              connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {
	               
	               // Toast.makeText(this, " Not Connected ", Toast.LENGTH_LONG).show();
	            	Utils.showDialogFinish(context,
	            			context.getResources().getString(R.string.network_error_message));
	                return false;
	            }
	          return false;
	        }

	
}
