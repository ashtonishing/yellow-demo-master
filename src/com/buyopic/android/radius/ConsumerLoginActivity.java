package com.buyopic.android.radius;

import java.text.DecimalFormat;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.buyopic.android.beacon.R;
import com.buyopic.android.models.Consumer;
import com.buyopic.android.network.BuyopicNetworkCallBack;
import com.buyopic.android.network.BuyopicNetworkServiceManager;
import com.buyopic.android.network.JsonResponseParser;
import com.buyopic.android.utils.Constants;
import com.buyopic.android.utils.FetchCurrentLocation.LocationResult;
import com.buyopic.android.utils.Utils;

public class ConsumerLoginActivity extends BaseActivity implements
		OnClickListener, BuyopicNetworkCallBack, LocationResult {

	private EditText mEmailView;
	private EditText mPassword;
	private ProgressDialog mProgressDialog;
	private String latitude;
	private String longitude;
	private BuyOpic buyOpic;
	private TextView mForgotPasswordView;
	public static boolean IS_LOGGED_IN = false;
	private int callingActivity=0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = LayoutInflater.from(this).inflate(R.layout.layout_register,
				null);
		setContentView(view);
		Utils.overrideFonts(this, view);
		callingActivity = getIntent().getIntExtra("callingactivity",0);
		prepareViews();
		buyOpic = (BuyOpic) getApplication();
		Bundle bundle=getIntent().getExtras();
		if (bundle != null && bundle.containsKey(JsonResponseParser.TAG_PROCESS_URL) ) {
			String url = bundle.getString(JsonResponseParser.TAG_PROCESS_URL);
			if (url != null) {
				Uri uri = Uri.parse(url);
				String processId = uri.getQueryParameter(JsonResponseParser.TAG_PROCESS_ID);
				showProgressDialog();
				BuyopicNetworkServiceManager buyopicNetworkServiceManager = BuyopicNetworkServiceManager
						.getInstance(this);
				buyopicNetworkServiceManager
						.sendUrlProcessDetailsRequest(Constants.REQUEST_MERCHANT_CONFIRMATION, processId, this);

			}
		}
	}

	private void showProgressDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = ProgressDialog.show(this, "", "Please wait",
					false, false);
		}
		mProgressDialog.show();
	}

	private void dismissProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (buyOpic.getmConsumerRegistrationStatus()) {
			finish();
		}
	}

	private void prepareViews() {
		mEmailView = (EditText) findViewById(R.id.layout_register_email);
		mPassword = (EditText) findViewById(R.id.layout_register_password);
		TextView registerTextView = (TextView) findViewById(R.id.layout_register_login_button);
		registerTextView.setOnClickListener(this);
		findViewById(R.id.layout_register_button).setOnClickListener(this);
		String str = "<html><body><u>Register Here</u></body></html>";
		registerTextView.setText(Html.fromHtml(str));
		mForgotPasswordView = (TextView) findViewById(R.id.consumer_login_forgotpassword_view);
		str = "<html><body><u>Forgot Password</u></body></html>";
		mForgotPasswordView.setText(Html.fromHtml(str));
		mForgotPasswordView.setOnClickListener(this);
		BaseActivity baseActivity = this;
		baseActivity.setBeaconActionBar("Login", 1);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.layout_register_button:
			submitDataToServer();
			break;
		case R.id.layout_register_login_button:
			Intent intent = new Intent(this, ConsumerRegisterActivity.class);
			startActivity(intent);
			break;
		case R.id.consumer_login_forgotpassword_view:
			BuyopicNetworkServiceManager buyopicNetworkServiceManager = BuyopicNetworkServiceManager
					.getInstance(this);
			if (!isEmpty(mEmailView)) {
				if (Utils.isValidEmail(mEmailView)) {
					showProgressDialog();
					buyopicNetworkServiceManager.sendResetPasswordRequest(
							Constants.REQUEST_RESET_PASSWORD, mEmailView
									.getText().toString(), this);
				} else {
					Utils.showToast(this, "Please give valid email id");
				}
			} else {
				Utils.showToast(this, "Please fill the email Id");
			}
		default:
			break;
		}

	}

	private void forgotPassword() {
		final Dialog dialog = new Dialog(this, R.style.Theme_Dialog);
		View view = LayoutInflater.from(this).inflate(
				R.layout.layout_forgotpassword_dialog_view, null);
		Utils.overrideFonts(this, view);
		dialog.setContentView(view);
		TextView forgotPasswordText = (TextView) view
				.findViewById(R.id.forgot_password_text);
		String forgotPasswordResponseText = "We have just sent you an email with a new temporary password (to your email address above). Please open that email and enter that password into the Password field above.\n\n(Once you are logged in to Yellow, for your own security, go to the Edit Profile item on the main menu and change your password immediately.)";
		forgotPasswordText.setText(forgotPasswordResponseText);
		ImageButton cancelDialog = (ImageButton) view
				.findViewById(R.id.dialog_cancel);
		cancelDialog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	private void submitDataToServer() {
		if (!isEmpty(mEmailView) && !isEmpty(mPassword)) {
			if (Utils.isValidEmail(mEmailView)) {
				showProgressDialog();
				BuyopicNetworkServiceManager buyopicNetworkServiceManager = BuyopicNetworkServiceManager
						.getInstance(this);
				buyopicNetworkServiceManager.sendConsumerLoginRequest(
						Constants.REQUEST_CONSUMER_LOGIN, mEmailView.getText()
								.toString(), mPassword.getText().toString(),
						this);
			} else {
				Utils.showToast(this, "Please give a valid email address");
			}
		} else {
			Utils.showToast(this, "Email and Password are mandatory");
		}
	}

	private boolean isEmpty(EditText editText) {
		return TextUtils.isEmpty(editText.getText().toString());
	}

	@Override
	public void onSuccess(int requestCode, Object object) {
		dismissProgressDialog();
		switch (requestCode) {

		case Constants.REQUEST_CONSUMER_LOGIN:
			Consumer consumer = JsonResponseParser
					.parseFinalConsumerRegistrationResponse((String) object);
			if (consumer != null && consumer.getmStatusMessage().equalsIgnoreCase("ok")) {
				IS_LOGGED_IN = true;
				BuyOpic buyOpic = (BuyOpic) getApplication();
				Log.i("CREDENTIALS", "login act consumer login" +consumer.getmConsumerEmail());
				buyOpic.setmConsumerEmail(consumer.getmConsumerEmail());
				buyOpic.setmBaseUrl(BuyopicNetworkServiceManager.BASE_URL);
				buyOpic.setmConsumerId(consumer.getmConsumerId());
				buyOpic.setmConsumerRegistrationStatus(consumer
						.isConsumerFinalRegistered());
				buyOpic.setmConsumerName(consumer.getmConsumerUserName());
				buyOpic.setmConsumerAddress(consumer.getmConsumerAddress());
				buyOpic.setmConsumerAddress1(consumer.getmConsumerAddress1());
				buyOpic.setmConsumerProfilePic(consumer
						.getmConsumerProfilePic());
				buyOpic.setmConsumerPhoneNumber(consumer.getmPhoneNumber());
				buyOpic.setmConsumerPassword(consumer.getmConsumerPassword());
				buyOpic.setmConsumerCity(consumer.getmConsumerCity());
				buyOpic.setmConsumerState(consumer.getmConsumerState());
				buyOpic.setmConsumerCountry(consumer.getmConsumerCountry());
				buyOpic.setmConsumerStreet(consumer.getmConsumerStreet());
				if(consumer.getmUserAddressImageUrl()!=null)
					buyOpic.setmConsumerUserAddressImageUrl(consumer.getmUserAddressImageUrl());
					else
						buyOpic.setmConsumerUserAddressImageUrl(consumer.getmConsumerAddressImageUrl());
				
				buyOpic.setmConsumerAddress(consumer.getmConsumerAddress());
				buyOpic.setmConsumerAddress1(consumer.getmConsumerAddress1());
				buyOpic.setmConsumerAddress2(consumer.getmConsumerAddress2());
				buyOpic.setmConsumerGooglePlaceID(consumer.getmGooglePlaceId());
				buyOpic.setmConsumerGoogleIconImage(consumer.getmGoogleIconImage());
				buyOpic.setmConsumerPostalCode(consumer.getmPostalCode());
				if(callingActivity==Constants.REQUEST_ORDERS_LOGIN){
					Intent intent = new Intent(this, MyOrdersActivity.class);
					startActivity(intent);
					finish();
				}
				else if(callingActivity==Constants.REQUEST_ORDERS_REQUEST_OFFERDETAILS){
					super.onBackPressed();
					finish();
				}
				else{
				Intent intent = new Intent(this, CreateNewListingActivity.class);
				startActivity(intent);
				finish();
				}
			} else {
				Utils.showToast(this, consumer.getmStatusMessage());
			}
			break;
		case Constants.REQUEST_MERCHANT_CONFIRMATION:
			consumer = JsonResponseParser
					.parseFinalConsumerRegistrationResponse((String) object);
			if (consumer != null) {
				IS_LOGGED_IN = true;
				BuyOpic buyOpic = (BuyOpic) getApplication();

				/*
				 * buyOpic.setmConsumerId(consumer.getmConsumerId());
				 * buyOpic.setmConsumerEmail(consumer.getmConsumerEmail());
				 * buyOpic.setmConsumerRegistrationStatus(consumer.
				 * isConsumerFinalRegistered());
				 */
                Log.i("CREDENTIALS", "login act merchant conf" +consumer.getmConsumerEmail());
				buyOpic.setmConsumerEmail(consumer.getmConsumerEmail());
				buyOpic.setmConsumerId(consumer.getmConsumerId());
				buyOpic.setmConsumerRegistrationStatus(consumer
						.isConsumerFinalRegistered());
				buyOpic.setmConsumerName(consumer.getmConsumerUserName());
				buyOpic.setmConsumerAddress(consumer.getmConsumerAddress());
				buyOpic.setmConsumerAddress1(consumer.getmConsumerAddress1());
				buyOpic.setmConsumerProfilePic(consumer
						.getmConsumerProfilePic());
				buyOpic.setmConsumerPhoneNumber(consumer.getmPhoneNumber());
				buyOpic.setmConsumerPassword(consumer.getmConsumerPassword());
				buyOpic.setmConsumerCity(consumer.getmConsumerCity());
				buyOpic.setmConsumerState(consumer.getmConsumerState());
				buyOpic.setmConsumerCountry(consumer.getmConsumerCountry());
				buyOpic.setmConsumerStreet(consumer.getmConsumerStreet());
				if(consumer.getmUserAddressImageUrl()!=null)
				buyOpic.setmConsumerUserAddressImageUrl(consumer.getmUserAddressImageUrl());
				else
					buyOpic.setmConsumerUserAddressImageUrl(consumer.getmConsumerAddressImageUrl());
				buyOpic.setmConsumerAddress2(consumer.getmConsumerAddress2());
				buyOpic.setmConsumerGooglePlaceID(consumer.getmGooglePlaceId());
				buyOpic.setmConsumerGoogleIconImage(consumer.getmGoogleIconImage());
				buyOpic.setmConsumerPostalCode(consumer.getmPostalCode());
			
				Utils.showToast(this, "Registration confirmed successfully.");
				if(callingActivity==Constants.REQUEST_ORDERS_LOGIN){
					Intent intent = new Intent(this, MyOrdersActivity.class);
					startActivity(intent);
					finish();
				}
				else if(callingActivity==Constants.REQUEST_ORDERS_REQUEST_OFFERDETAILS){
					super.onBackPressed();
					finish();
				}
				else{
				Intent intent = new Intent(this, CreateNewListingActivity.class);
				startActivity(intent);
				finish();
				}
			}
			break;
		case Constants.REQUEST_RESET_PASSWORD:
			String response = JsonResponseParser
					.parseConsumerRegistrationResponse((String) object);
			if (response
					.equalsIgnoreCase("Password updated successfully, please check your mail.")) {
				forgotPassword();
			}
			else
			{
				Utils.showToast(this, response);
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
			if (email != null) {
				if (latitude != null && longitude != null) {

					BuyopicNetworkServiceManager buyopicNetworkServiceManager = BuyopicNetworkServiceManager
							.getInstance(this);
					buyopicNetworkServiceManager
							.sendConsumerRegistrationRequest(
									Constants.REQUEST_CONSUMER_REGISTRATION,
									email, phoneNumber, latitude, longitude,
									this);
				} else {
					Utils.showToast(this,
							"Please wait a moment We are retrieving your location");
				}
			} else {
				Utils.showToast(this, "We are unable to retrieve email");
			}
		}
	}

	private String getEmail(Context context) {
		AccountManager accountManager = AccountManager.get(context);
		Account account = getAccount(accountManager);

		if (account == null) {
			return null;
		} else {
			return account.name;
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		ImageView imageView = new ImageView(this);
		imageView.setImageResource(R.drawable.ic_search);
		imageView.setColorFilter(getResources().getColor(
				R.color.screen_bg_color));
		Drawable drawable = imageView.getDrawable();
		menu.add(0, 1, 0, "Add").setIcon(drawable)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.add(0, 2, 0, "Settings").setIcon(R.drawable.ic_yellow)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 1) {
			startActivity(new Intent(this, SearchActivity.class));
		}
		return super.onOptionsItemSelected(item);
	}

}
