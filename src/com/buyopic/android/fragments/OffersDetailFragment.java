package com.buyopic.android.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buyopic.android.beacon.R;
import com.buyopic.android.models.Alert;
import com.buyopic.android.models.AlertDetail;
import com.buyopic.android.models.ConsumerDetails;
import com.buyopic.android.models.StoreInfo;
import com.buyopic.android.network.BuyopicNetworkCallBack;
import com.buyopic.android.network.BuyopicNetworkServiceManager;
import com.buyopic.android.network.JsonResponseParser;
import com.buyopic.android.radius.BuyOpic;
import com.buyopic.android.radius.ConsumerLoginActivity;
import com.buyopic.android.utils.Constants;
import com.buyopic.android.utils.DateTimePicker;
import com.buyopic.android.utils.Utils;
import com.buyopic.android.utils.onAlertItemClickListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class OffersDetailFragment extends Fragment implements
		BuyopicNetworkCallBack, OnClickListener, OnCheckedChangeListener {

	public static final String KEY_EXTRA_ITEM = "item";
	public static final String KEY_EXTRA_TITLE = "title";
	public static final String KEY_EXTRA_DISTANCE = "distance";
	public static final String KEY_EXTRA_CONSUMER_ID = "consumer_id";
	public static final String KEY_EXTRA_PHONE_NUMBER = "phone_number";
	public static final String KEY_EXTRA_IS_IN_BEACON_RANGE = "isinrange";
	public static final String KEY_EXTRA_STORE_ID = "store_id";
	public static final String KEY_EXTRA_RETAILER_ID = "retailer_id";
	public static final String ACTION__DISTANCE_CHANGED = "com.buyopic.action.distancechanged";
	private ImageView mStoreLogo;
	private TextView mStoreAddress;
	private ImageView mProductImage;
	private TextView mofferTitle;
	private TextView mPriceView;
	private TextView mDescription;
	private TextView mEndDate;
	private String consumerId;
	private ProgressDialog progressDialog;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private LinearLayout layout;
	private boolean isinBeaconRange = false;
	private TextView mStoreNameView;
	private String mDistanceValue;
	private TextView mStoreDistance;
	private Context mContext;
	private ConstraintChangedNotifiedReceiver finishNotifyReceiver;
	private ImageView mShareView;
	private CheckBox mFavoritesView;
	private String offerId;
	private String mPostedAlertConsumerId;
	private AlertDetail alertDetail;
	private BuyopicNetworkServiceManager buyopicNetworkServiceManager;
	private BuyOpic buyOpic;
	private ImageView mCallView;
	private ImageView mOrderNowView;
	private String mPhoneNumber;
	private onAlertItemClickListener alertItemClickListener;
	Dialog dialog;

	TextView itemNameText;
	TextView priceValueText;
	TextView addressText;
	EditText quantityNumber;
	EditText orderDeliveryTime;
	Button cancelBtn;
	Button submitButton;
	ImageButton cancelDialog;
	private String storeId;
	private String retailerId;
	ConsumerDetails mConsumerDetails;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerReciver();
		buyOpic = (BuyOpic) getActivity().getApplication();
		dialog = new Dialog(getActivity(), R.style.Theme_Dialog);
		consumerId = buyOpic.getmConsumerId();
		buyopicNetworkServiceManager = BuyopicNetworkServiceManager
				.getInstance(getActivity());

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_offer_details, null);
		layout = (LinearLayout) view.findViewById(R.id.storedetails);
		view.findViewById(R.id.dummy_area).setOnClickListener(this);
		layout.setOnClickListener(this);
		mStoreLogo = (ImageView) view
				.findViewById(R.id.layout_offer_desc_store_logo);
		mStoreAddress = (TextView) view
				.findViewById(R.id.layout_offer_desc_store_address);
		mStoreDistance = (TextView) view
				.findViewById(R.id.layout_offer_desc_store_distance);
		mProductImage = (ImageView) view
				.findViewById(R.id.layout_offer_desc_product_image);
		mofferTitle = (TextView) view
				.findViewById(R.id.layout_offers_desc_message_title_view);
		mPriceView = (TextView) view
				.findViewById(R.id.layout_offers_desc_price_view);
		mDescription = (TextView) view
				.findViewById(R.id.layout_offers_desc_description_view);
		mEndDate = (TextView) view
				.findViewById(R.id.layout_offers_desc_end_date_view);
		mStoreNameView = (TextView) view
				.findViewById(R.id.layout_offer_desc_store_name);
		mShareView = (ImageView) view
				.findViewById(R.id.layout_offer_desc_share);
		mCallView = (ImageView) view.findViewById(R.id.layout_offer_desc_call);
		mOrderNowView = (ImageView) view
				.findViewById(R.id.layout_offer_ordernow);
		mCallView.setOnClickListener(this);
		mOrderNowView.setOnClickListener(this);
		mShareView.setOnClickListener(this);
		mFavoritesView = (CheckBox) view
				.findViewById(R.id.layout_offer_desc_check_box_favorite);

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mContext = activity;
		alertItemClickListener = (onAlertItemClickListener) activity;

	}

	private void registerReciver() {
		IntentFilter intentFilter = new IntentFilter(ACTION__DISTANCE_CHANGED);
		finishNotifyReceiver = new ConstraintChangedNotifiedReceiver();
		mContext.registerReceiver(finishNotifyReceiver, intentFilter);
	}

	private void unRegisterReciever() {
		mContext.unregisterReceiver(finishNotifyReceiver);
	}

	class ConstraintChangedNotifiedReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			if (bundle != null && bundle.containsKey(KEY_EXTRA_DISTANCE)) {
				String distance = bundle.getString(KEY_EXTRA_DISTANCE);
				mStoreDistance.setText(distance);
			}

		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Bundle bundle = getArguments();
		if (bundle != null && bundle.containsKey(KEY_EXTRA_ITEM)) {
			offerId = bundle.getString(KEY_EXTRA_ITEM);
			mPostedAlertConsumerId = bundle.getString(KEY_EXTRA_CONSUMER_ID);
			if (bundle.containsKey(KEY_EXTRA_DISTANCE)) {
				mDistanceValue = bundle.getString(KEY_EXTRA_DISTANCE);
				mStoreDistance.setText(mDistanceValue);
			}

			 storeId = bundle.getString(KEY_EXTRA_STORE_ID);
			 retailerId = bundle.getString(KEY_EXTRA_RETAILER_ID);
			mPhoneNumber = bundle.getString(KEY_EXTRA_PHONE_NUMBER);
			if (mPostedAlertConsumerId != null
					&& !mPostedAlertConsumerId.equalsIgnoreCase("null")
					&& !mPostedAlertConsumerId.equals("") /* && offerId != null */) {
				showProgressDialog();
				buyopicNetworkServiceManager
						.sendConsumerPostedAlertDetailsRequest(
								Constants.REQUEST_CONSUMER_ALERT_DETAILS,
								mPostedAlertConsumerId, offerId, storeId,
								retailerId, buyOpic.getmConsumerId(), this);
			} else if (consumerId != null /* && offerId != null */) {
				showProgressDialog();

				buyopicNetworkServiceManager.sendAlertDetailsRequest(
						Constants.REQUEST_GETSTORE_ALERTS_DETAILS, consumerId,
						offerId, storeId, retailerId, this);
			}

			if (bundle != null
					&& bundle.containsKey(KEY_EXTRA_IS_IN_BEACON_RANGE)) {
				isinBeaconRange = bundle
						.getBoolean(KEY_EXTRA_IS_IN_BEACON_RANGE);
			}

			if (isinBeaconRange) {
				layout.setBackgroundResource(R.drawable.four_border_bg_in_beacon_range);
			} else {
				layout.setBackgroundResource(R.drawable.four_border_bg);
			}

			mFavoritesView.setOnCheckedChangeListener(this);
		}
	}

	@Override
	public void onSuccess(int requestCode, Object object) {
		dismissProgresDialog();
		switch (requestCode) {
		case Constants.REQUEST_GETSTORE_ALERTS_DETAILS:
			alertDetail = JsonResponseParser
					.parseAlertDetailsResponse((String) object);
			if (alertDetail != null) {
				mPhoneNumber = alertDetail.getmPhoneNUmber();
				if (!TextUtils.isEmpty(alertDetail.getmStoreLogo())) {
					imageLoader.displayImage(alertDetail.getmStoreLogo(),
							mStoreLogo, configureOptions());
				} else {
					imageLoader.displayImage("drawable://"
							+ R.drawable.ic_placeholder_icon, mStoreLogo,
							configureOptions());
				}
				mStoreAddress.setText(alertDetail.getmStoreAddress());
				mStoreNameView.setText(alertDetail.getmStoreName());
				bindDetailstoViews(alertDetail.getAlert());
			}
			break;
		case Constants.REQUEST_CONSUMER_ALERT_DETAILS:
			alertDetail = JsonResponseParser
					.parseConsumerAlertDetail((String) object);
			if (alertDetail != null) {
				alertDetail.setmStoreId("BUYOPIC_STORE_ID");
				alertDetail.setmRetailerId("BUYOPIC_RETAILER_ID");

				mPhoneNumber = alertDetail.getmPhoneNUmber();
				if (!TextUtils.isEmpty(alertDetail.getmStoreLogo())) {
					imageLoader.displayImage(alertDetail.getmStoreLogo(),

					mStoreLogo, configureOptions());
				} else {
					imageLoader.displayImage("drawable://"
							+ R.drawable.ic_placeholder_icon, mStoreLogo,
							configureOptions());
				}
				mStoreAddress.setText(alertDetail.getmStoreAddress());
				mStoreNameView.setText(alertDetail.getmStoreName());
				bindDetailstoViews(alertDetail.getAlert());
				mEndDate.setText("Offer Valid until : "
						+ alertDetail.getAlert().getmEndDate());
			}
		case Constants.REQUEST_URL_SUBMIT_NEWORDER:
			dialog.dismiss();
			HashMap<String, String> mHashMap=JsonResponseParser.parseSubmitOrderStatus((String)object);
			String status=mHashMap.get("status");
			if(status!=null&&status.equalsIgnoreCase("ok")){
				Utils.showToast(mContext,"ordered succesfully");
			}
			else if(status!=null&&status.equalsIgnoreCase("error")){
				Utils.showToast(mContext, "order failed");
			}
			else{
				Utils.showToast(mContext,"Order not submitted ");
			}	
			break;
		case Constants.REQUEST_URL_ADDRESS_DETAILS:
			dismissProgresDialog();
			mConsumerDetails =JsonResponseParser.parseConsumerDetailsForOrderStatusAddress((String)object);
			if(mConsumerDetails!=null
				&&mConsumerDetails.getAddresscity()!=null&&!mConsumerDetails.getAddresscity().equalsIgnoreCase("")
						&&mConsumerDetails.getAddressline1()!=null&&!mConsumerDetails.getAddressline1().equalsIgnoreCase("")
						&&mConsumerDetails.getAddressline2()!=null&&!mConsumerDetails.getAddressline2().equalsIgnoreCase("")
						&&mConsumerDetails.getAddressstate()!=null&&!mConsumerDetails.getAddressstate().equalsIgnoreCase("")
						&&mConsumerDetails.getAddresszip()!=null&&!mConsumerDetails.getAddresszip().equalsIgnoreCase("")){
				showSubmitOrderPopUp();
			}
			else{
				Utils.showToast(getActivity(), "Invalid ConsumerID . Please Register");
				Intent intent=new Intent(getActivity(), ConsumerLoginActivity.class);
				intent.putExtra("callingactivity",Constants.REQUEST_ORDERS_REQUEST_OFFERDETAILS);
				startActivity(new Intent(intent));
			}
			  
		default:
			break;
		}
	}

	private void bindDetailstoViews(Alert alert) {
		if (alert != null) {
			imageLoader.displayImage(alert.getmThumbnailUrl(), mProductImage,
					configureOptions());
			mDescription.setText(alert.getmOfferMessage());

			mEndDate.setText("Offer Valid until : "
					+ getFormattedTime(alert.getmEndDate()));
			mofferTitle.setText(alert.getmOfferTitle() + ":");

			// BEGIN suppress zero price list items
			// mPriceView.setText("$" + alert.getmPrice());
			if (!alert.getmPrice().equalsIgnoreCase("0.00")) {
				mPriceView
						.setText(Constants.CURRENCYSYMBOL + alert.getmPrice());
			} else {
				mPriceView.setText("");
			}
			// END suppress zero price list items

			mFavoritesView.setChecked(alert.isFavorite());
		}
	}

	private static String getFormattedTime(String string) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.US);
		try {
			Date date = dateFormat.parse(string);
			SimpleDateFormat dateFormat2 = new SimpleDateFormat(
					"MMM dd, yyyy - hh:mm a", Locale.US);
			return dateFormat2.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private void showProgressDialog() {
		try {
			if (progressDialog == null) {
				progressDialog = ProgressDialog.show(getActivity(), "",
						"Please wait...", false, false);
			}
			progressDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void dismissProgresDialog() {
		try {
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private DisplayImageOptions configureOptions() {
		return new DisplayImageOptions.Builder()
				.showImageOnLoading(android.R.color.transparent)
				.showImageForEmptyUri(R.drawable.ic_placeholder_icon)
				.imageScaleType(ImageScaleType.EXACTLY).cacheInMemory(false)
				.considerExifParams(true)
				.showImageOnFail(R.drawable.ic_placeholder_icon)
				.cacheOnDisc(true).build();
	}

	@Override
	public void onFailure(int requestCode, String message) {
		dismissProgresDialog();
		Utils.showToast(getActivity(), message);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.layout_offer_desc_share) {
			shareThisOffer();
		} else if (v.getId() == R.id.storedetails) {
			if (alertDetail != null) {
				StoreInfo storeInfo = new StoreInfo();
				storeInfo.setDistanceValue(mDistanceValue);
				storeInfo.setmStoreId(alertDetail.getmStoreId());
				storeInfo.setmRetailerId(alertDetail.getmRetailerId());
				storeInfo.setInBeaconRange(isinBeaconRange);
				storeInfo.setmPostedAlertConsumerId(mPostedAlertConsumerId);
				storeInfo.setmStoreName(mStoreNameView.getText().toString());
				alertItemClickListener.onItemClicked(storeInfo);
			}

		} else if (v.getId() == R.id.layout_offer_desc_call) {
			Utils.callPhone(mContext, mPhoneNumber);
		} else if (v.getId() == R.id.layout_offer_ordernow) {
			
			
			if(buyOpic.getmConsumerRegistrationStatus()){
			showProgressDialog();
			buyopicNetworkServiceManager.sendGetConsumerDetailsRequest(Constants.REQUEST_URL_ADDRESS_DETAILS, consumerId, this);
			}
			else if(!buyOpic.getmConsumerRegistrationStatus()){
				Utils.showToast(mContext,"Please Login to order");
				Intent intent=new Intent(getActivity(), ConsumerLoginActivity.class);
				intent.putExtra("callingactivity",Constants.REQUEST_ORDERS_REQUEST_OFFERDETAILS);
				startActivity(new Intent(intent));
			}
			else{
				Utils.showToast(mContext,"Please Login to order");
				Intent intent=new Intent(getActivity(), ConsumerLoginActivity.class);
				intent.putExtra("callingactivity",Constants.REQUEST_ORDERS_REQUEST_OFFERDETAILS);
				startActivity(new Intent(intent));
			}

			// show ordering pop up
			
		//	showSubmitOrderPopUp();

		} else if (v.getId() == R.id.dialog_cancel) {
			dialog.dismiss();
		} else if (v.getId() == R.id.cancelbuttoninorderdialog) {
			dialog.dismiss();
		} else if (v.getId() == R.id.orderdeliverytime) {
			showDateTimeDialog((EditText) v);
		}

		else if (v.getId() == R.id.submitbuttoninorderdialog) {
			// submit request to server
			Log.i("TEST", "order request"
						+storeId + "  "
						+retailerId+  "  "+
						buyOpic.getmConsumerId()+
						alertDetail.getAlert().getmOfferId()+ "  "+
						quantityNumber.getText().toString()+ "  "+
						alertDetail.getAlert().getmPrice()+ "  "+
						mConsumerDetails.getAddressline1()+ "  "+
						mConsumerDetails.getAddressline2()+ "  "+
						""+ "  "+
						mConsumerDetails.getAddresscity()+ "  "+
						mConsumerDetails.getAddressstate()+ "  "+
						mConsumerDetails.getAddresszip()+ "  "+
						orderDeliveryTime.getText().toString());
			
						buyopicNetworkServiceManager.sendSubmitNewOrderRequest(Constants.REQUEST_URL_SUBMIT_NEWORDER, 
						storeId,
						retailerId, 
						buyOpic.getmConsumerId(),
						alertDetail.getAlert().getmOfferId(), 
						quantityNumber.getText().toString(), 
						alertDetail.getAlert().getmPrice(),
						mConsumerDetails.getAddressline1(),
						mConsumerDetails.getAddressline2(), 
						"", 
						mConsumerDetails.getAddresscity(),
						mConsumerDetails.getAddressstate(),
						mConsumerDetails.getAddresszip(),
						orderDeliveryTime.getText().toString(), 
						this);
		}

	}

	private void shareThisOffer() {
		if (alertDetail != null && alertDetail.getAlert() != null) {
			sendShareStoreAlertRequestToServer(alertDetail.getAlert()
					.getmOfferId(), consumerId);
			Utils.shareOffer(mContext, alertDetail.getAlert().getmOfferId(),
					consumerId, alertDetail.getAlert().getmPostedConsumerId(),
					alertDetail.getAlert().getmStoreId(), alertDetail
							.getAlert().getmRetailerId());
		}
	}

	protected void sendShareStoreAlertRequestToServer(String mOfferId,
			String consumerId) {
		buyopicNetworkServiceManager
				.sendShareStoreAlertRequest(
						Constants.REQUEST_SHARE_STORE_ALERT, consumerId,
						mOfferId, this);
	}

	@Override
	public void onCheckedChanged(final CompoundButton buttonView,
			final boolean isChecked) {

		BuyopicNetworkServiceManager buyopicNetworkServiceManager = BuyopicNetworkServiceManager
				.getInstance(mContext);
		buyopicNetworkServiceManager.sendFavoriteRequest(
				Constants.REQUEST_FAVORITE, consumerId, offerId, isChecked,
				new BuyopicNetworkCallBack() {

					@Override
					public void onSuccess(int requestCode, Object object) {
						if (requestCode == Constants.REQUEST_FAVORITE) {
							boolean status = JsonResponseParser
									.parseFavoriteResponse((String) object);
							if (status) {
								buttonView.setChecked(isChecked);
							}
						}
					}

					@Override
					public void onFailure(int requestCode, String message) {

					}
				});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unRegisterReciever();
	}

	private void showSubmitOrderPopUp() {

		// TODO Auto-generated method stub

		View view = null;
		try {

			view = LayoutInflater.from(getActivity()).inflate(
					R.layout.ordernowdialog, null);

			dialog.setContentView(view);
			cancelDialog = (ImageButton) dialog
					.findViewById(R.id.dialog_cancel);
			orderDeliveryTime = (EditText) dialog
					.findViewById(R.id.orderdeliverytime);
			itemNameText = (TextView) dialog.findViewById(R.id.itemnametext);
			priceValueText = (TextView) dialog
					.findViewById(R.id.pricevaluetext);
			addressText=(TextView)dialog.findViewById(R.id.addresstext);
			quantityNumber = (EditText) dialog
					.findViewById(R.id.quantitynumber);
			cancelBtn = (Button) dialog
					.findViewById(R.id.cancelbuttoninorderdialog);
			submitButton = (Button) dialog
					.findViewById(R.id.submitbuttoninorderdialog);
			orderDeliveryTime.setOnClickListener(this);
			itemNameText.setText(""+alertDetail.getAlert().getmOfferTitle());
			
			if (!alertDetail.getAlert().getmPrice().equalsIgnoreCase("0.00")) {
				priceValueText
						.setText(Constants.CURRENCYSYMBOL + alertDetail.getAlert().getmPrice());
			} else {
				priceValueText.setText("");
			}
			addressText.setText(mConsumerDetails.getAddressline1()+" "+mConsumerDetails.getAddressline2());
			itemNameText.setOnClickListener(this);
			priceValueText.setOnClickListener(this);
			quantityNumber.setOnClickListener(this);
			submitButton.setOnClickListener(this);
			cancelBtn.setOnClickListener(this);
			cancelDialog.setOnClickListener(this);

			// dialog.setCancelable(false);
			// dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

		dialog.show();
	}

	private void showDateTimeDialog(final EditText edittext) {
		// Create the dialog
		final Dialog mDateTimeDialog = new Dialog(getActivity());
		// Inflate the root layout
		final RelativeLayout mDateTimeDialogView = (RelativeLayout) getActivity()
				.getLayoutInflater().inflate(R.layout.date_time_dialog, null);
		// Grab widget instance
		final DateTimePicker mDateTimePicker = (DateTimePicker) mDateTimeDialogView
				.findViewById(R.id.DateTimePicker);
		// Check is system is set to use 24h time (this doesn't seem to work as
		// expected though)
		final String timeS = android.provider.Settings.System.getString(
				getActivity().getContentResolver(),
				android.provider.Settings.System.TIME_12_24);
		final boolean is24h = !(timeS == null || timeS.equals("12"));

		// Update demo TextViews when the "OK" button is clicked
		((Button) mDateTimeDialogView.findViewById(R.id.SetDateTime))
				.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						mDateTimePicker.clearFocus();

						String dateTime = ((mDateTimePicker.get(Calendar.MONTH)) + 1)
								+ "/"
								+ mDateTimePicker.get(Calendar.DAY_OF_MONTH)
								+ "/"
								+ mDateTimePicker.get(Calendar.YEAR)
								+ " "
								
								;
						int	hourTime=	 mDateTimePicker.get(Calendar.HOUR);
						String hourValue=String.format("%02d",hourTime);
					int	minuteTime=	 mDateTimePicker.get(Calendar.MINUTE);
					String minuteValue=String.format("%02d",minuteTime);
					dateTime	=	dateTime+hourValue+":"+minuteValue+""+" "+
						 (mDateTimePicker.get(Calendar.AM_PM) == Calendar.AM ? "AM"
								: "PM");
						

						Calendar calendar = Calendar.getInstance();
						String a = (calendar.get(Calendar.MONTH) + 1)
								+ "/"
								+ calendar.get(Calendar.DAY_OF_MONTH)
								+ "/"
								+ calendar.get(Calendar.YEAR)
								+ " "
								+ calendar.get(Calendar.HOUR)
								+ ":"
								+ calendar.get(Calendar.MINUTE)
								+ " "
								+ (calendar.get(Calendar.AM_PM) == 0 ? "AM"
										: "PM");

						// showing time

						edittext.setText(dateTime);
						mDateTimeDialog.dismiss();
					}
				});

		// Cancel the dialog when the "Cancel" button is clicked
		((Button) mDateTimeDialogView.findViewById(R.id.CancelDialog))
				.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						// TODO Auto-generated method stub
						mDateTimeDialog.cancel();
					}
				});

		// Reset Date and Time pickers when the "Reset" button is clicked
		((Button) mDateTimeDialogView.findViewById(R.id.ResetDateTime))
				.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						// TODO Auto-generated method stub
						mDateTimePicker.reset();
					}
				});

		// Setup TimePicker
		mDateTimePicker.setIs24HourView(is24h);
		// No title on the dialog window
		mDateTimeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Set the dialog content view
		mDateTimeDialog.setContentView(mDateTimeDialogView);
		// Display the dialog
		mDateTimeDialog.show();
	}

	
	public void myClickMethod(View v) {
		if (v.getId() == R.id.dialog_cancel) {
			dialog.dismiss();
		} else if (v.getId() == R.id.cancelbuttoninorderdialog) {
			dialog.dismiss();
		} else if (v.getId() == R.id.orderdeliverytime) {
			showDateTimeDialog((EditText) v);
		}

		else if (v.getId() == R.id.submitbuttoninorderdialog) {
			// submit request to server

		}
	}

}
