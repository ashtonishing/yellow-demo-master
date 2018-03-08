package com.buyopic.android.radius;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.buyopic.android.beacon.R;
import com.buyopic.android.models.Consumer;
import com.buyopic.android.network.BuyopicNetworkCallBack;
import com.buyopic.android.network.BuyopicNetworkServiceManager;
import com.buyopic.android.network.JsonResponseParser;
import com.buyopic.android.utils.Constants;
import com.buyopic.android.utils.FetchCurrentLocation;
import com.buyopic.android.utils.FetchCurrentLocation.LocationResult;
import com.buyopic.android.utils.Utils;
import com.crashlytics.android.Crashlytics;

public class DefaultConsumerRegistrationActivity extends Activity implements
		 BuyopicNetworkCallBack, LocationResult {

	private ProgressDialog mProgressDialog;
	private String latitude;
	private String longitude;
	BuyOpic buyOpic;
	Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Crashlytics.start(this);
		mContext=this;
		BuyopicNetworkServiceManager.TURN_BLUETOOTH_OFF=false;
		View view = LayoutInflater.from(this).inflate(R.layout.layout_register,
				null);
		Utils.overrideFonts(this, view);
		// prepareViews();
		 buyOpic = (BuyOpic) getApplication();
		if (buyOpic.getmConsumerId() != null
				&& buyOpic.getmConsumerEmail() != null 
				&& BuyopicNetworkServiceManager.BASE_URL.equals(buyOpic.getmBaseUrl())) {
			Log.i("SRT", "consumer id is"+buyOpic.getmConsumerId());
			Intent intent = new Intent(this, HomePageSetupActivity.class);
			startActivity(intent);
			finish();
		} else {
			showProgressDialog();
			FetchCurrentLocation currentLocation = new FetchCurrentLocation();
			currentLocation.fetchLocation(this, this);
		}

	}

	private void showProgressDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = ProgressDialog.show(this, "",
					"Loading", false, false);
		}
		mProgressDialog.show();
	}

	private void dismissProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}



	@Override
	public void onSuccess(int requestCode, Object object) {
		dismissProgressDialog();
		switch (requestCode) {

		case Constants.REQUEST_CONSUMER_REGISTRATION:
			Consumer consumer = JsonResponseParser
					.parseFinalConsumerRegistrationResponse((String) object);
			if (consumer != null) {
				BuyOpic buyOpic = (BuyOpic) getApplication();
				buyOpic.setmConsumerId(consumer.getmConsumerId());
				buyOpic.setmBaseUrl(BuyopicNetworkServiceManager.BASE_URL);
				Log.i("CREDENTIALS", "Def consumer act" +consumer.getmConsumerEmail());
				buyOpic.setmConsumerEmail(consumer.getmConsumerEmail());
			//	buyOpic.setmConsumerPassword(consumer.getmConsumerPassword());
				/*buyOpic.setmConsumerRegistrationStatus(consumer
						.isConsumerFinalRegistered());*/
				Intent intent = new Intent(this, HomePageSetupActivity.class);
				startActivity(intent);
				finish();
			} else {
				Utils.showToast(this, "Registration Failed.Please try again");
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onFailure(int requestCode, String message) {
		dismissProgressDialog();
	}

	@Override
	public void gotLocation(Location location) {
		if (location != null) {
			DecimalFormat decimalFormat = new DecimalFormat("#0.000000");
			latitude = decimalFormat.format(location.getLatitude());
			longitude = decimalFormat.format(location.getLongitude());
			String email = getEmail(this);
			String phoneNumber = getPhoneNumber();
			Utils.showLog("Email:" + email + ",Phone No:" + phoneNumber);
			Log.i("CREDENTIALS", "Email " +buyOpic.getmConsumerEmail());
		
			if (email != null) {
				if (latitude != null && longitude != null) {
					getAddress(location.getLatitude(), location.getLongitude());
					BuyopicNetworkServiceManager buyopicNetworkServiceManager = BuyopicNetworkServiceManager
							.getInstance(this);
					if(Utils.isInternetOn(mContext))
					{
					buyopicNetworkServiceManager
							.sendConsumerRegistrationRequest(
									Constants.REQUEST_CONSUMER_REGISTRATION,
									email, phoneNumber, latitude, longitude,
									this);
					}
				} else {
					Utils.showToast(this,
							"Please wait a moment We are retrieving your location");
				}
			} else {
				Utils.showToast(this, "We are unable to retrieve email");
			}
		}
		 else {
				dismissProgressDialog();
				Utils.showDialogFinish(this,
						getResources().getString(R.string.gps_error_message));
			}
	}

	private String getEmail(Context context) {
		if(buyOpic.getmConsumerEmail()!=null&&!buyOpic.getmConsumerEmail().equalsIgnoreCase("")){
			
			return buyOpic.getmConsumerEmail();
		}
		else {
		AccountManager accountManager = AccountManager.get(context);
		Account account = getAccount(accountManager);

		if (account == null) {
			return null;
		} else {
			return account.name;
		}
		}
	}

	private Account getAccount(AccountManager accountManager) {
		Account[] accounts = accountManager.getAccountsByType("com.google");
		Account account;
		if (accounts.length > 0) {
			account = accounts[0];
		} else {
			account = null;
		}
		return account;
	}

	private String getPhoneNumber() {
		TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		return TelephonyMgr.getLine1Number();
	}
	private String getAddress(double latitude, double longitude) {
		StringBuilder result = new StringBuilder();
		try {
			Geocoder geocoder = new Geocoder(this, Locale.getDefault());
			List<Address> addresses = geocoder.getFromLocation(latitude,
					longitude, 1);
			if (addresses.size() > 0) {
				Address address = addresses.get(0);
				result.append(address.getLocality()).append("\n");
				result.append(address.getCountryName());
				result.append(address.getPostalCode());
				result.append(address.getMaxAddressLineIndex());
				result.append(address.getFeatureName()).append("\n");
				result.append(address.getAddressLine(0)).append("\n");
				buyOpic.setmSourceLatitude(latitude);
				buyOpic.setmSourceLongitude(longitude);
				buyOpic.setmConsumerCity(address.getLocality());
				buyOpic.setmConsumerCountry(address.getCountryName());
				buyOpic.setmConsumerAddress1(address.getAddressLine(0));
				if (address.getMaxAddressLineIndex() > 0)
					buyOpic.setmConsumerAddress2(address.getAddressLine(1));
				buyOpic.setmConsumerState(address.getAdminArea());
				buyOpic.setmConsumerPostalCode(address.getPostalCode());

			}
		} catch (IOException e) {
			Log.e("tag", e.getMessage());
		}

		return result.toString();
	}



}
