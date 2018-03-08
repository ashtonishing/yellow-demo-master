package com.buyopic.android.fragments;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.buyopic.android.adapters.CustomAlertMessageAdapter;
import com.buyopic.android.beacon.R;
import com.buyopic.android.models.Alert;
import com.buyopic.android.models.StoreAlerts;
import com.buyopic.android.models.YelpReviewItem;
import com.buyopic.android.network.BuyopicNetworkCallBack;
import com.buyopic.android.network.BuyopicNetworkServiceManager;
import com.buyopic.android.network.JsonResponseParser;
import com.buyopic.android.radius.BuyOpic;
import com.buyopic.android.utils.Constants;
import com.buyopic.android.utils.Utils;
import com.buyopic.android.utils.onAlertItemClickListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class OffersListFragment extends Fragment implements
		OnItemClickListener, BuyopicNetworkCallBack, OnClickListener {
	private ListView listView;
	private ProgressBar mProgressBar;
	private TextView mEmptyView;
	private TextView mBusinessNameView;
	private TextView mBusinessReviewCount;
	private TextView mBusinessAddressView;
	private ImageView mBusinessLogoView;
	private ImageView mBusinessReviewCountView;
	public StoreAlerts storeAlerts;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private String storeName;
	private onAlertItemClickListener alertItemClickListener;
	private String storeid;
	private String retailerId;
	private String consumerId;
	private ImageView mStoreLogo;
	private TextView mStoreAddress;
	
	public static final String KEY_STORE_NAME = "storename";
	public static final String KEY_STORE_ID = "store_id";
	public static final String KEY_RETAILER_ID = "retailer_id";
	public static final String KEY_DISTANCE = "distance";
	public static final String KEY_PHONE_NUMBER= "phone_number";
	public static final String KEY_POSTED_CONSUMER_ALERT_ID = "posted_consumer_id";
	public static final String KEY_IS_IN_BEACON_RANGE = "is_in_range";
	public static final String ACTION__DISTANCE_CHANGED = "com.buyopic.action.distancechanged";
	public static final String KEY_EXTRA_DISTANCE = "distance";
	
	private boolean isStoreInfoLoaded = false;
	private Context context;
	private TextView mStoreNameView;
	private boolean isinBeaconRange = false;
	private LinearLayout layout;
	private TextView mStoreDistanceView;
	private String mDistance;
	private ConstraintChangedNotifiedReceiver finishNotifyReceiver;
	private String mPostedConsumerId="";
	private String mPhoneNumber;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BuyOpic buyOpic = (BuyOpic) context.getApplicationContext();
		consumerId = buyOpic.getmConsumerId();
		isStoreInfoLoaded = false;
		registerReciver();
	}

	private void registerReciver() {
		IntentFilter intentFilter = new IntentFilter(ACTION__DISTANCE_CHANGED);
		finishNotifyReceiver = new ConstraintChangedNotifiedReceiver();
		context.registerReceiver(finishNotifyReceiver, intentFilter);
	}

	private void unRegisterReciever() {
		context.unregisterReceiver(finishNotifyReceiver);
	}

	class ConstraintChangedNotifiedReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			if (bundle != null && bundle.containsKey(KEY_EXTRA_DISTANCE)) {
				String distance = bundle.getString(KEY_EXTRA_DISTANCE);
				mStoreDistanceView.setText(distance);
			}

		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		isStoreInfoLoaded = true;
		
	}
	
	

	@Override
	public void onDestroy() {
		super.onDestroy();
		unRegisterReciever();
		isStoreInfoLoaded = false;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Bundle bundle = getArguments();

		if (!isStoreInfoLoaded && bundle != null
				&& bundle.containsKey(KEY_RETAILER_ID)
				&& bundle.containsKey(KEY_STORE_ID)) {
			storeName = bundle.getString(KEY_STORE_NAME);
			storeid = bundle.getString(KEY_STORE_ID);
			retailerId = bundle.getString(KEY_RETAILER_ID);
			mDistance = bundle.getString(KEY_DISTANCE);
			mPhoneNumber= bundle.getString(KEY_PHONE_NUMBER);
			mPostedConsumerId = bundle.getString(KEY_POSTED_CONSUMER_ALERT_ID);
			BuyopicNetworkServiceManager buyopicNetworkServiceManager = BuyopicNetworkServiceManager
					.getInstance(context);
			if ((storeid!=null&&storeid.equalsIgnoreCase("BUYOPIC_STORE_ID")
					)&&( retailerId!=null&&retailerId.equalsIgnoreCase("BUYOPIC_RETAILER_ID"))) {
				buyopicNetworkServiceManager.sendConsumerPostedAlertsRequest(
						Constants.REQUEST_LIST_CONSUMER_POSTED_ALERTS,
						consumerId, mPostedConsumerId, this);
			} else {
				buyopicNetworkServiceManager.sendgetStoreAlertsRequest(
						Constants.REQUEST_GETSTORE_ALERTS, storeid, retailerId,
						consumerId,mPostedConsumerId, this);
			}
		} else {
			if (storeAlerts != null) {
				imageLoader.displayImage(storeAlerts.getmStoreImageUrl(),
						mStoreLogo, configureOptions());
				mStoreAddress.setText(storeAlerts.getmStoreAddress());
				mStoreNameView.setText(storeAlerts.getmStoreName());
				mStoreDistanceView.setText(mDistance);
				bindDataToListView(storeAlerts.getmAlerts());
			}
		}

		if (bundle != null && bundle.containsKey(KEY_IS_IN_BEACON_RANGE)) {
			isinBeaconRange = bundle.getBoolean(KEY_IS_IN_BEACON_RANGE);
		}

		if (isinBeaconRange) {
			layout.setBackgroundResource(R.drawable.four_border_bg_in_beacon_range);
		} else {
			layout.setBackgroundResource(R.drawable.four_border_bg);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_home, null);
		Utils.overrideFonts(context, view);
		layout = (LinearLayout) view.findViewById(R.id.storedetails);
		layout.setOnClickListener(this);
		view.findViewById(R.id.offer_list_area).setOnClickListener(this);
		listView = (ListView) view.findViewById(R.id.alerts_list_view);
		listView.setOnItemClickListener(this);
		mStoreLogo = (ImageView) view
				.findViewById(R.id.custom_layout_home_list_view_store_logo);
		mStoreNameView = (TextView) view
				.findViewById(R.id.layout_offer_desc_store_name);
		mStoreAddress = (TextView) view
				.findViewById(R.id.layout_offer_desc_store_address);
		mStoreDistanceView = (TextView) view
				.findViewById(R.id.layout_offer_desc_store_distance);

		mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);
		mEmptyView = (TextView) view.findViewById(R.id.emptyview);
		mBusinessNameView = (TextView) view
				.findViewById(R.id.yelp_business_item_name_view);
		mBusinessReviewCount = (TextView) view
				.findViewById(R.id.yelp_business_review_count_view);
		mBusinessAddressView = (TextView) view
				.findViewById(R.id.yelp_business_address_view);
		mBusinessLogoView = (ImageView) view
				.findViewById(R.id.yelp_business_item_logo_view);
		mBusinessReviewCountView = (ImageView) view
				.findViewById(R.id.yelp_business_review_count_image_view);
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		alertItemClickListener = (onAlertItemClickListener) activity;
		context = activity;
	}

	@Override
	public void onResume() {
		super.onResume();
		listView.setVisibility(View.VISIBLE);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

		Object object = (Object) listView.getAdapter().getItem(arg2);
		if (object != null && object instanceof Alert) {
			Alert alert = (Alert) object;
			alert.setmDistanceValue(mDistance);
			alert.setInBeaconRange(isinBeaconRange);
			alert.setmStoreId(storeid);
			alert.setmRetailerId(retailerId);
			alertItemClickListener.onItemClicked(alert);
		}
	}

	private void bindDataToListView(List<Alert> alerts) {
		mProgressBar.setVisibility(View.GONE);
		if (alerts != null && !alerts.isEmpty()) {
			listView.setVisibility(View.VISIBLE);
			mEmptyView.setVisibility(View.GONE);

			listView.setAdapter(new CustomAlertMessageAdapter(context, alerts,storeName));
		} else {
			listView.setVisibility(View.GONE);
			mEmptyView.setText("There are no store alerts Found");
			mEmptyView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onSuccess(int requestCode, Object object) {
		if (object != null && object instanceof String) {
			switch (requestCode) {
			case 1:
				String yelpBusinessResponse = (String) object;
				YelpReviewItem reviewItem = JsonResponseParser
						.parseYelpReviewResponse(yelpBusinessResponse);
				if (reviewItem != null) {
					bindReviewsToViews(reviewItem);
				}
				break;
			case Constants.REQUEST_GETSTORE_ALERTS:
				storeAlerts = JsonResponseParser
						.parseStoreAlertsResponse((String) object,storeName,mPhoneNumber);
				if (storeAlerts != null) {

					if (storeAlerts.getmStoreImageUrl() != null
							&& !TextUtils.isEmpty(storeAlerts
									.getmStoreImageUrl())
							&& !storeAlerts.getmStoreImageUrl()
									.equalsIgnoreCase("null")) {
						imageLoader.displayImage(
								storeAlerts.getmStoreImageUrl(), mStoreLogo,
								configureOptions());
					} else {
						imageLoader.displayImage("drawable://"
								+ R.drawable.ic_placeholder_icon, mStoreLogo,
								configureOptions());
					}
					mStoreNameView.setText(storeAlerts.getmStoreName());
					String storeName=storeAlerts.getmStoreAddress();
					if(storeName!=null){
					storeName=storeName.replace(", ",",");
					storeName=storeName.replace(",", ", ");
					mStoreAddress.setText(storeName);
					}
					mStoreDistanceView.setText(mDistance);
					bindDataToListView(storeAlerts.getmAlerts());
				}
				break;
			case Constants.REQUEST_LIST_CONSUMER_POSTED_ALERTS:
				storeAlerts = JsonResponseParser
						.parseStoreAlertsResponse((String) object,storeName,mPhoneNumber);
				if (storeAlerts != null) {

					if (storeAlerts.getmStoreImageUrl() != null
							&& !TextUtils.isEmpty(storeAlerts
									.getmStoreImageUrl())
							&& !storeAlerts.getmStoreImageUrl()
									.equalsIgnoreCase("null")) {
						imageLoader.displayImage(
								storeAlerts.getmStoreImageUrl(), mStoreLogo,
								configureOptions());
					} else {
						imageLoader.displayImage("drawable://"
								+ R.drawable.ic_placeholder_icon, mStoreLogo,
								configureOptions());
					}
					mStoreNameView.setText(storeAlerts.getmStoreName());
					mStoreAddress.setText(storeAlerts.getmStoreAddress());
					mStoreDistanceView.setText(mDistance);
					bindDataToListView(storeAlerts.getmAlerts());
				}
			default:
				break;
			}
		}

	}

	private void bindReviewsToViews(YelpReviewItem reviewItem) {
		mBusinessAddressView.setText(reviewItem.getmAddress());
		mBusinessNameView.setText(reviewItem.getmName());
		mBusinessReviewCount.setText(reviewItem.getmReviewCount() + " Reviews");
		imageLoader.displayImage(reviewItem.getmImageUrl(), mBusinessLogoView,
				configureOptions());
		imageLoader.displayImage(reviewItem.getmReviewImageUrl(),
				mBusinessReviewCountView, configureOptions());
	}

	@Override
	public void onFailure(int requestCode, String message) {
		Utils.showLog("yelp reviews not found");
	}

	private DisplayImageOptions configureOptions() {
		return new DisplayImageOptions.Builder()
				.showImageOnLoading(android.R.color.transparent)
				.showImageForEmptyUri(R.drawable.ic_placeholder_icon)
				.imageScaleType(ImageScaleType.EXACTLY).cacheInMemory(true)
				.cacheOnDisc(true).considerExifParams(true)
				.showImageOnFail(R.drawable.ic_placeholder_icon).build();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
