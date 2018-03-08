package com.buyopic.android.fragments;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.buyopic.android.beacon.R;
import com.buyopic.android.beacon.R.color;
import com.buyopic.android.database.BuyopicConsumerDatabase;
import com.buyopic.android.models.Alert;
import com.buyopic.android.models.OrderStatus;
import com.buyopic.android.models.ProvisionedBeaconInfo;
import com.buyopic.android.models.StoreAlerts;
import com.buyopic.android.models.StoreInfo;
import com.buyopic.android.network.BuyopicNetworkCallBack;
import com.buyopic.android.network.BuyopicNetworkServiceManager;
import com.buyopic.android.network.GoogleStoreInfo;
import com.buyopic.android.network.JsonResponseParser;
import com.buyopic.android.radius.BackgroundMonitorService;
import com.buyopic.android.radius.BaseActivity;
import com.buyopic.android.radius.BuyOpic;
import com.buyopic.android.radius.CheckUpdatesBroadcast;
import com.buyopic.android.radius.TrackDistanceBroadCast;
import com.buyopic.android.utils.Constants;
import com.buyopic.android.utils.FetchCurrentLocation;
import com.buyopic.android.utils.FetchCurrentLocation.LocationResult;
import com.buyopic.android.utils.Utils;
import com.buyopic.android.utils.onAlertItemClickListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class CloseByListFragment extends Fragment implements
		OnItemClickListener, BuyopicNetworkCallBack, LocationResult,
		OnChildClickListener, OnGroupClickListener, OnScrollListener, OnCheckedChangeListener,OnClickListener {

	private ExpandableListView listView;
	private CheckBox mSwitchBtwnListnMap;
	private onAlertItemClickListener clickListener;
	private ConstraintChangedNotifiedReceiver finishNotifyReceiver;
	private Activity context;
	private String consumerId;
	private BuyOpic buyOpic;
	private BuyopicNetworkServiceManager buyopicNetworkServiceManager;
	private List<StoreInfo> storeInfos;
	//private List<StoreInfo> storeInfos1;
	public static final String ACTION__BEACON_DATA_CHANGED_INTENT = "custom.intent.action.beacondatachanged";
	public static final String ACTION__ORDER_NOTIFICATION = "custom.intent.action.ordernotification";
	private BuyopicConsumerDatabase wayAppdb = null;
	private AlarmManager alarmManager;
	private PendingIntent pendingIntent;
	private CustomClosebyExpandableListAdapter closeByListAdapter;
	private HashMap<Integer, Boolean> expandedGroupPositions;
	private int lastSeenItem;
	private ProgressDialog progressDialog;
	private Dialog dialog;
	private ImageButton cancelDialog;
	private TextView itemNameText;
	private TextView priceValueText;
	private TextView quantityNumber;
	private TextView timeOfDelivery;
	private TextView expTimeOfDelivery;
	private TextView orderStatusValue;
	private ImageView orderStatusImage;
	private TextView addressText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		storeInfos = new ArrayList<StoreInfo>();
		//storeInfos1 = new ArrayList<StoreInfo>();
		// mGooglestoreInfos=new ArrayList<GoogleStoreInfo>();
		dialog=new Dialog(context, R.style.Theme_Dialog);
		alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		wayAppdb = BuyopicConsumerDatabase.shareInstance(context);
		String countryname = Utils.getCountryName(context);
		if (countryname != null && countryname.equalsIgnoreCase("IN")) {
			Constants.CURRENCYSYMBOL = this.getResources().getString(
					R.string.rs);
		} else if (countryname != null && countryname.equalsIgnoreCase("US")) {
			Constants.CURRENCYSYMBOL = this.getResources().getString(
					R.string.dollar);
		}
		registerReciver();

		buyopicNetworkServiceManager = BuyopicNetworkServiceManager
				.getInstance(context);
		consumerId = buyOpic.getmConsumerId();
	

		FetchCurrentLocation currentLocation = new FetchCurrentLocation();
		currentLocation.fetchLocation(context, CloseByListFragment.this);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unRegisterReciever();
		if (alarmManager != null) {
			alarmManager.cancel(pendingIntent);
		}
		buyOpic.setUserInHomeListFragment(false);
	}

	@Override
	public void onResume() {
		super.onResume();
		expandedGroupPositions = buyOpic.getExpandedGroupPos();
		listView.setOnGroupClickListener(this);
		listView.setOnChildClickListener(this);

	}

	@Override
	public void onPause() {
		super.onPause();
		buyOpic.setExpandedGroupPos(expandedGroupPositions);
		listView.setOnGroupClickListener(null);
		buyOpic.setCloseByLastSelectionPage(lastSeenItem);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof onAlertItemClickListener) {
			clickListener = (onAlertItemClickListener) activity;
		}
		context = activity;
		buyOpic = (BuyOpic) context.getApplication();
		buyOpic.setUserInHomeListFragment(true);
		showProgressDialog();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_closeby_fragment, null);
		listView = (ExpandableListView) view
				.findViewById(R.id.expandable_listview);
		mSwitchBtwnListnMap=(CheckBox)view.findViewById(R.id.layout_map_list_switch_activate);
		mSwitchBtwnListnMap.setOnCheckedChangeListener( this);
		listView.setOnChildClickListener(this);
		listView.setOnGroupClickListener(this);
		closeByListAdapter = new CustomClosebyExpandableListAdapter(context);

		listView.setAdapter(closeByListAdapter);
		listView.setOnScrollListener(this);
		Log.i("SRT","city"+buyOpic.getmConsumerCity());

		return view;
	}

	private void showProgressDialog() {
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

	private void dismissProgressDialog() {
		try {
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void bindStoreInfoDetails(List<StoreInfo> storeInfos) {

		if (storeInfos != null && !storeInfos.isEmpty()) {
			buyOpic.setStoreinfo(storeInfos);
			listView.setAdapter(new CustomClosebyExpandableListAdapter(
					getActivity()));
		} else {
			Utils.showToast(context, "No stores found near by");
		}

		listView.clearFocus();
		listView.postDelayed(new Runnable() {
			@Override
			public void run() {

				listView.setSelection((int) buyOpic
						.getCloseByLastSelectionPage());
				listView.smoothScrollToPosition((int) buyOpic
						.getCloseByLastSelectionPage());
				listView.requestFocus();
			}
		}, 3000);
		

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		StoreInfo object = (StoreInfo) listView.getAdapter().getItem(arg2);
		if (object != null) {
			buyOpic.setmSelectedStoreLat(object.getStoreLatitude());
			buyOpic.setmSelectedStoreLong(object.getStoreLongitude());
		}
		if(object.getmGoogleStoreInfo()!=null&&object.getmGoogleStoreInfo().size()>0){
			
		}
		else{
		clickListener.onItemClicked(object);
		}
	}

	private void registerReciver() {
		IntentFilter intentFilter = new IntentFilter(
				ACTION__BEACON_DATA_CHANGED_INTENT);
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
			if (bundle != null) {
				if (bundle.containsKey(CheckUpdatesBroadcast.KEY_UPDATELIST)||bundle.containsKey(BaseActivity.KEY_UPDATELISTADRESSCHANGE)) {
					storeInfos = intent
							.getParcelableArrayListExtra(CheckUpdatesBroadcast.KEY_UPDATELIST);
					
					/*storeInfos = bundle
							.getParcelableArrayList(CheckUpdatesBroadcast.KEY_UPDATELIST);*/
				/*ArrayList<StoreInfo> 	storeInfos1 = intent
						.getParcelableArrayListExtra(CheckUpdatesBroadcast.KEY_UPDATELIST);*/
				
				/*	for(int i=0;i<storeInfos.size();i++){
						StoreInfo obj=new StoreInfo();
						 
						obj=storeInfos.get(i);
						if(obj.getmGoogleStoreInfo()==null){
						
							storeInfos1.add(obj);
						}
						else{
							
						}
					}
					bindStoreInfoDetails(storeInfos1);*/
					bindStoreInfoDetails(storeInfos);
				} else if (bundle
						.containsKey(BackgroundMonitorService.KEY_REQUESTING_SERVER)) {
				} else if (bundle
						.containsKey(BackgroundMonitorService.KEY_BEACON_CHANGED)) {
					highLightStores();
				}
				else if (bundle
						.containsKey(CheckUpdatesBroadcast.KEY_UPDATEORDERNOTIFICATION)) {
					OrderStatus orderStatus=intent.getParcelableExtra(CheckUpdatesBroadcast.KEY_UPDATEORDERNOTIFICATION);
					showNotificationOrderPopUp(orderStatus);
					Log.i("SRT", "receive");
				}
			}

		}
	}

	private void highLightStores() {
		if (storeInfos != null && !storeInfos.isEmpty()) {
			for (StoreInfo storeInfo : storeInfos) {
				if (storeInfo.getProvisionedBeacons() != null
						&& !storeInfo.getProvisionedBeacons().isEmpty()) {
					for (ProvisionedBeaconInfo beaconInfo : storeInfo
							.getProvisionedBeacons()) {

						int updatingViewIndex = storeInfos.indexOf(storeInfo);
						if (listView != null && listView.isShown()) {
							int visiblePosition = listView
									.getFirstVisiblePosition();
							View view = listView.getChildAt(updatingViewIndex
									- visiblePosition);
							if (checkIfExists(beaconInfo.getmMajor(),
									beaconInfo.getmMinor(),
									beaconInfo.getmBeaconUUID())) {
								storeInfo.setInBeaconRange(true);

								if (view != null) {
									view.setBackgroundColor(getResources()
											.getColor(
													R.color.color_high_light_store));
								}
								break;
							}

							else {
								storeInfo.setInBeaconRange(false);
								if (view != null) {
									view.setBackgroundColor(Color.TRANSPARENT);
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void onSuccess(int requestCode, Object object) {

		switch (requestCode) {
		case Constants.REQUEST_NEAREST_STORE_ALERTS:
			dismissProgressDialog();
			setAlarmForTrackConsumerDistance();
			
			storeInfos = JsonResponseParser.parseStoreInfos(context,
					(String) object);
			bindStoreInfoDetails(storeInfos);
			if(!BuyopicNetworkServiceManager.TURN_BLUETOOTH_OFF){
			if(BuyopicNetworkServiceManager.TURN_BLUETOOTH_ON){
				Log.i("BLUETOOTH", "TURN_BLUETOOTH_ON");
			if(!isEnabled())
			{
				Log.i("BLUETOOTH", "isEnabled");
			showAlertDialog();
			}
			}
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

	private boolean checkIfExists(String major, String minor, String uuid) {

		if (major != null && minor != null) {
			return wayAppdb.checkBeaconExistsOrNot(major, minor, uuid);
		} else {
			return false;
		}
	}

	@Override
	public void gotLocation(Location location) {
		if (location != null) {
			checkForNewDataWithLocation(location);
			buyOpic.setSourceLocation(location);
			Log.i("Loc changes", "closebylist setting"+"lat "+location.getLatitude()+" long "+location.getLongitude());
	//		buyOpic.setmSourceLatitude(location.getLatitude());
	//		buyOpic.setmSourceLongitude(location.getLongitude());
		} else {
			dismissProgressDialog();
			Utils.showDialog(context,
					context.getResources().getString(R.string.gps_error_message));
		}
	}

	private void setAlarmForTrackConsumerDistance() {
		Intent checkUpdatesIntent = new Intent(context,
				TrackDistanceBroadCast.class);
		pendingIntent = PendingIntent.getBroadcast(context, 0,
				checkUpdatesIntent, 0);

		if (alarmManager != null) {
			alarmManager.cancel(pendingIntent);
		}

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, 15);
		alarmManager
				.setRepeating(
						AlarmManager.RTC_WAKEUP,
						calendar.getTimeInMillis(),
						BuyopicNetworkServiceManager.INTERVAL_CHECK_FOR_NEW_ALERTS_CLOSEBY,
						pendingIntent);
	}

	private void checkForNewDataWithLocation(Location location) {
		if (location != null) {
			DecimalFormat decimalFormat = new DecimalFormat("#0.000000");
			
			String latitude = null;
			String longitude = null;
			// delete
			if(buyOpic!=null&&buyOpic.getmSourceLatitude()!=0&&buyOpic.getmSourceLongitude()!=0){
			latitude=Double.toString(buyOpic.getmSourceLatitude());
			longitude=Double.toString(buyOpic.getmSourceLongitude());
			}
			else 
			{
				 latitude = decimalFormat.format(location.getLatitude());
				 longitude = decimalFormat.format(location.getLongitude());
			}
			
			Log.i("Loc changes", "closebylist"+"lat "+latitude+" long "+longitude);
			if(Utils.isInternetOn(context)){
			buyopicNetworkServiceManager.sendNearestStoreAlertsRequest(
					Constants.REQUEST_NEAREST_STORE_ALERTS, "", "", "",
					latitude, longitude, consumerId, "NO", null, true, this,
					context);
			}
		}
	}

	private class CustomClosebyExpandableListAdapter extends
			BaseExpandableListAdapter implements OnClickListener {
		private static final String DUMMY_BUYOPIC_RETAILER_ID = "BUYOPIC_RETAILER_ID";

		private static final String DUMMY_BUYOPIC_STORE_ID = "BUYOPIC_STORE_ID";

		private Context mContext;
		ViewHolder holder;

		private ImageLoader imageLoader = ImageLoader.getInstance();
		private StoreAlerts storeAlerts;

		public CustomClosebyExpandableListAdapter(Context context) {
			this.mContext = context;
		}

		public class ViewHolder {
			private TextView mPriceView;
			private TextView mAlertTitleView;
			private TextView mStoreNameView;
			private ImageView mStoreImageView;
			private TextView mDistanceView;
			private LinearLayout mConvertViewLayout;
			private TextView mMessageView;
			private ImageView mThumbnailImageView;
			private ProgressBar mProgressBarForStore;
			private ProgressBar mProgressBarForProduct;
			private LinearLayout mProductNameLayout;
			private LinearLayout mProductNameLayoutMain;
			private LinearLayout mProductLogoLayout;
			private ImageView mShareOptionsMenu;
			private CheckBox mFavoritesButton;
			private ImageView mGroupIndicator;
			private TextView mViewMoreTextView;
			private TextView mOfferTitle;
			private TextView mOfferPrice;
		}

		private DisplayImageOptions configureOptions() {
			return new DisplayImageOptions.Builder()
					.showImageOnLoading(android.R.color.transparent)
					.showImageForEmptyUri(R.drawable.ic_placeholder_icon)
					.imageScaleType(ImageScaleType.EXACTLY)
					.cacheInMemory(false).considerExifParams(true)
					.showImageOnFail(R.drawable.ic_placeholder_icon)
					.cacheOnDisc(true).build();
		}

		@Override
		public Alert getChild(int groupPosition, int childPosition) {
			StoreInfo storeInfo = storeInfos.get(groupPosition);
			if(storeInfo.getmGoogleStoreInfo()!=null&&storeInfo.getmGoogleStoreInfo().size()>0){
				return null;
			}

			if (storeInfo.getStoreAlerts() != null
					&& !storeInfo.getStoreAlerts().isEmpty()) {
				return storeInfos.get(groupPosition).getStoreAlerts()
						.get(childPosition);
			}

			return null;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public View getChildView(final int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			StoreInfo storeInfo = getGroup(groupPosition);
			if (storeInfo != null&&(storeInfo.getmGoogleStoreInfo()==null||storeInfo.getmGoogleStoreInfo().isEmpty())) {
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.layout_custom_child_adapter_view, null);
				Utils.overrideFonts(mContext, convertView);
				holder = new ViewHolder();
				holder.mMessageView = (TextView) convertView
						.findViewById(R.id.custom_layout_offers_message_description_view);
				holder.mPriceView = (TextView) convertView
						.findViewById(R.id.custom_layout_offers_price_view);
				holder.mThumbnailImageView = (ImageView) convertView
						.findViewById(R.id.custom_layout_offers_image_view);
				holder.mAlertTitleView = (TextView) convertView
						.findViewById(R.id.custom_layout_offers_title_view);
				holder.mShareOptionsMenu = (ImageView) convertView
						.findViewById(R.id.custom_layout_offers_ic_overflow_menu);
				holder.mViewMoreTextView = (TextView) convertView
						.findViewById(R.id.custom_layout_offers_view_more);
				holder.mFavoritesButton = (CheckBox) convertView
						.findViewById(R.id.custom_layout_offers_toggle_favorites);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final Alert alert = getChild(groupPosition, childPosition);
			if (alert != null) {
				holder.mFavoritesButton.setOnCheckedChangeListener(null);
				holder.mFavoritesButton.setChecked(alert.isFavorite());
				holder.mFavoritesButton
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(
									final CompoundButton buttonView,
									final boolean isChecked) {
								BuyopicNetworkServiceManager buyopicNetworkServiceManager = BuyopicNetworkServiceManager
										.getInstance(mContext);
								buyopicNetworkServiceManager
										.sendFavoriteRequest(
												Constants.REQUEST_FAVORITE,
												consumerId,
												alert.getmOfferId(), isChecked,
												new BuyopicNetworkCallBack() {

													@Override
													public void onSuccess(
															int requestCode,
															Object object) {
														switch (requestCode) {
														case Constants.REQUEST_FAVORITE:
															boolean status = JsonResponseParser
																	.parseFavoriteResponse((String) object);
															if (status) {
																alert.setFavorite(isChecked);
																buttonView
																		.setChecked(isChecked);
															}
															break;

														default:
															break;
														}
													}

													@Override
													public void onFailure(
															int requestCode,
															String message) {

													}
												});
							}
						});

				holder.mMessageView.setText(alert.getmOfferMessage());
				holder.mAlertTitleView.setText(alert.getmOfferTitle() + ":");

				// BEGIN suppress zero price list items
				// holder.mPriceView.setText("$" + alert.getmPrice());
				if (!alert.getmPrice().equalsIgnoreCase("0.00")) {
					holder.mPriceView.setText(Constants.CURRENCYSYMBOL
							+ alert.getmPrice());
				} else {
					holder.mPriceView.setText("");
				}

				// END suppress zero price list items

				imageLoader.displayImage(alert.getmThumbnailUrl(),
						holder.mThumbnailImageView, configureOptions());
				holder.mShareOptionsMenu.setTag(alert);
				holder.mShareOptionsMenu.setOnClickListener(this);
				if (alert.ismIsAlertDiscontinued()) {
					convertView.setBackgroundColor(Color.parseColor("#D1D1D1"));
				} else {
					convertView.setBackgroundColor(Color.TRANSPARENT);
				}
				
				if (isLastChild &&( storeInfo.getmGoogleStoreInfo()==null||storeInfo.getmGoogleStoreInfo().isEmpty())&&storeInfo.getStoreAlerts() != null
						&& storeInfo.getStoreAlerts().size() > 2) {
					holder.mViewMoreTextView.setText("See All "
							+ storeInfo.getStoreAlerts().size());
					holder.mViewMoreTextView.setVisibility(View.VISIBLE);
					holder.mViewMoreTextView
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									StoreInfo object = (StoreInfo) storeInfos
											.get(groupPosition);
									if (object != null) {
										buyOpic.setmSelectedStoreLat(object
												.getStoreLatitude());
										buyOpic.setmSelectedStoreLong(object
												.getStoreLongitude());
									}
									clickListener.onItemClicked(object);
								}
							});
				} else {
					holder.mViewMoreTextView.setVisibility(View.GONE);
				}
			}
			}
			return convertView;
			
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			StoreInfo storeInfo = getGroup(groupPosition);
			if (storeInfo.getStoreAlerts() != null
					&& !storeInfo.getStoreAlerts().isEmpty()) {
				if (storeInfo.getStoreAlerts().size() > 2) {
					return 2;
				} else {
					return storeInfo.getStoreAlerts().size();
				}
			} else {
				return 0;
			}
		}

		@Override
		public StoreInfo getGroup(int groupPosition) {
			try {
				return storeInfos.get(groupPosition);
			} catch (IndexOutOfBoundsException iobe) {
				return null;
			}
		}

		@Override
		public int getGroupCount() {
			
		
			return storeInfos.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public View getGroupView(final int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			  holder = null;
			
			final StoreInfo storeInfo = (StoreInfo) getGroup(groupPosition);
		
				
				if (convertView == null) {
					convertView = LayoutInflater.from(mContext).inflate(
							R.layout.layout_custom_list_home_new, null);
					Utils.overrideFonts(mContext, convertView);
					holder = new ViewHolder();
					holder.mStoreImageView = (ImageView) convertView
							.findViewById(R.id.custom_layout_home_list_view_store_logo);
					holder.mConvertViewLayout = (LinearLayout) convertView
							.findViewById(R.id.convert_view);
					holder.mStoreNameView = (TextView) convertView
							.findViewById(R.id.custom_layout_home_list_view_store_name);
					holder.mDistanceView = (TextView) convertView
							.findViewById(R.id.custom_layout_home_list_view_distance);
					holder.mGroupIndicator = (ImageView) convertView
							.findViewById(R.id.custom_layout_home_list_view_more);
					holder.mOfferTitle = (TextView) convertView
							.findViewById(R.id.custom_layout_home_list_view_product_name);
					holder.mOfferPrice = (TextView) convertView
							.findViewById(R.id.custom_layout_home_list_view_product_price);
					holder.mThumbnailImageView = (ImageView) convertView
							.findViewById(R.id.custom_layout_home_list_view_product_logo);
					holder.mProgressBarForProduct=(ProgressBar) convertView
							.findViewById(R.id.custom_layout_home_list_view_product_logo_progressbar);
					holder.mProgressBarForStore=(ProgressBar) convertView
							.findViewById(R.id.custom_layout_home_list_view_store_logo_progressbar);
					holder.mProductNameLayoutMain=(LinearLayout) convertView
							.findViewById(R.id.custom_layout_home_list_view_product_name_linearlayout_main);
					holder.mProductNameLayout=(LinearLayout) convertView
							.findViewById(R.id.custom_layout_home_list_view_product_name_linearlayout);
					holder.mProductLogoLayout=(LinearLayout) convertView
							.findViewById(R.id.custom_layout_home_list_view_product_logo_layout);	
					
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				if (storeInfo != null&&(storeInfo.getmGoogleStoreInfo()==null||storeInfo.getmGoogleStoreInfo().size()==0)) {
					
					
				     convertView.setLayoutParams(new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
					
				     float width = getResources().getDimension(R.dimen.image_width);
				     float height = getResources().getDimension(R.dimen.image_height);
				     Log.i("CLOSEBY", "width "+width +"heiht"+height);
				     final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int)width,(int)height);
					
					 layoutParams.gravity=Gravity.LEFT|Gravity.CENTER;
					 holder.mOfferTitle.setTextSize(20);
					 holder.mDistanceView.setTextSize(18);
					 holder.mOfferPrice.setTextSize(18);
					 holder.mStoreImageView.setLayoutParams(layoutParams);
					 holder.mStoreNameView.setText(storeInfo.getmStoreName());
					 holder.mDistanceView.setText(storeInfo.getDistanceValue());
					 holder.mThumbnailImageView.setVisibility(View.VISIBLE);
					 holder.mGroupIndicator .setVisibility(View.VISIBLE);
					 holder.mProductLogoLayout.setVisibility(View.VISIBLE);
					 holder.mProgressBarForProduct.setVisibility(View.VISIBLE);
					 holder.mProgressBarForStore.setVisibility(View.VISIBLE);
					 holder.mProductNameLayoutMain.setGravity(Gravity.LEFT);
					 holder.mProductNameLayout.setGravity(Gravity.LEFT);
					 holder.mStoreNameView .setGravity(Gravity.TOP|Gravity.LEFT);
					 holder.mDistanceView.setGravity(Gravity.TOP|Gravity.CENTER);
					 holder.mOfferTitle.setGravity(Gravity.LEFT);
					 
					 imageLoader.displayImage(storeInfo.getmProductLogo(),
						holder.mThumbnailImageView, configureOptions());
				
						
					 imageLoader.displayImage(storeInfo.getmStoreLogo(),
								holder.mStoreImageView, configureOptions());
					 
					 holder.mThumbnailImageView
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								HashMap<Integer, Boolean> hashMap = buyOpic
										.getExpandedGroupPos();
								hashMap.remove(groupPosition);
								buyOpic.setExpandedGroupPos(hashMap);
								listView.collapseGroup(groupPosition);
								Alert alert = new Alert();
								alert.setInBeaconRange(storeInfo
										.isInBeaconRange());
								alert.setmDistanceValue(storeInfo
										.getDistanceValue());
								alert.setmOfferId(storeInfo.getmOfferId());
								alert.setmStoreId(storeInfo.getmStoreId());
								alert.setmRetailerId(storeInfo.getmRetailerId());
								alert.setmPostedConsumerId(storeInfo
										.getmPostedAlertConsumerId());
								clickListener.onItemClicked(alert);

							}
						});
				
				
				// BEGIN suppress zero price list items
				if (storeInfo!=null&&storeInfo.getmPrice()!=null&&!storeInfo.getmPrice().equalsIgnoreCase("0.00")){
					holder.mOfferPrice.setText(Constants.CURRENCYSYMBOL + storeInfo.getmPrice());
				} else{
					holder.mOfferPrice.setText("");
				}
				
				// END suppress zero price list items
				
				holder.mOfferPrice.setTextColor(Color.RED);
				holder.mOfferTitle.setText(storeInfo.getmTitle());
				if (isThisGroupExpanded(groupPosition)) {
					Utils.showLog("Expanded Group Position-->" + groupPosition);
					listView.expandGroup(groupPosition);
					if (listView.isGroupExpanded(groupPosition)) {
						holder.mGroupIndicator
								.setImageResource(R.drawable.ic_expandable_indicator_new);
					} else {
						holder.mGroupIndicator
								.setImageResource(R.drawable.ic_collapse_indicator_new);
					}
				} else {
					Utils.showLog("Collapsed Group Position-->" + groupPosition);
					listView.collapseGroup(groupPosition);
					holder.mGroupIndicator
							.setImageResource(R.drawable.ic_collapse_indicator_new);
				}
				if (storeInfo.isInBeaconRange()) {
					holder.mConvertViewLayout.setBackgroundColor(Color
							.parseColor("#fff98f"));
				} else {
					holder.mConvertViewLayout
							.setBackgroundColor(Color.TRANSPARENT);
				}

				holder.mGroupIndicator
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								ImageView imageView = (ImageView) v;
								if (listView.isGroupExpanded(groupPosition)) {
									imageView
											.setImageResource(R.drawable.ic_collapse_indicator_new);
									listView.collapseGroup(groupPosition);
									expandedGroupPositions
											.remove(groupPosition);
								} else {
									expandedGroupPositions.put(groupPosition,
											true);
									imageView
											.setImageResource(R.drawable.ic_expandable_indicator_new);
									listView.expandGroup(groupPosition);
								}
								buyOpic.setExpandedGroupPos(expandedGroupPositions);
							}
						});
			}
				else if(storeInfo != null&&storeInfo.getmGoogleStoreInfo()!=null&&storeInfo.getmGoogleStoreInfo().size()>0){
					 
					  float width = getResources().getDimension(R.dimen.googleimage_width);
					     float height = getResources().getDimension(R.dimen.googlimage_height);
					     float height1 = getResources().getDimension(R.dimen.googlimagechanged_height);
					 convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,(int)height));
					 FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int)width,(int)height1);
					
					 holder.mOfferTitle.setTextSize(getResources().getDimension(R.dimen.bottom_bar_text_size));
					 holder.mDistanceView.setTextSize(getResources().getDimension(R.dimen.tenspsize));
					 holder.mOfferPrice.setTextSize(getResources().getDimension(R.dimen.tenspsize));
					 holder.mStoreImageView.setLayoutParams(layoutParams);
					 holder.mProgressBarForStore.setVisibility(View.INVISIBLE);
					 holder.mThumbnailImageView.setVisibility(View.GONE);
					 holder.mGroupIndicator .setVisibility(View.GONE);
					 holder.mProductLogoLayout.setVisibility(View.GONE);
					 holder.mProgressBarForProduct.setVisibility(View.GONE);
					 holder.mProductNameLayoutMain.setGravity(Gravity.RIGHT);
					 holder.mProductNameLayout.setGravity(Gravity.RIGHT);
					 holder.mOfferTitle.setGravity(Gravity.RIGHT);
					 holder.mStoreNameView .setGravity(Gravity.RIGHT);
					 holder.mDistanceView.setGravity(Gravity.BOTTOM);
					 holder.mOfferPrice.setTextColor(color.color_alerts_listview_header_text);
					 List<GoogleStoreInfo> mgooglestoreinfo=storeInfo.getmGoogleStoreInfo();
					 Log.i("TEST", "googlestore obj "+storeInfo.getmGoogleStoreInfo().get(0));
					 if(mgooglestoreinfo!=null){
						imageLoader.displayImage(mgooglestoreinfo.get(0).getmIconURL(),
								holder.mStoreImageView, configureOptions());
						
						holder.mConvertViewLayout.setBackgroundColor(Color.LTGRAY);
						holder.mStoreNameView.setText(mgooglestoreinfo.get(0).getmName());
						holder.mDistanceView.setText(Utils.convertMetrsAndFeets(mgooglestoreinfo.get(0).getmDistance(),mContext));
						holder.mOfferTitle.setText(mgooglestoreinfo.get(0).getmName());
						holder.mOfferPrice.setText(mgooglestoreinfo.get(0).getmVicinity());
					 }

					
				}
			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		@Override
		public void onGroupCollapsed(int groupPosition) {
			super.onGroupCollapsed(groupPosition);
		}

		@Override
		public void onGroupExpanded(int groupPosition) {
			super.onGroupExpanded(groupPosition);

			Object object = getGroup(groupPosition);
			if (object != null && object instanceof StoreInfo) {

				final StoreInfo storeInfo = (StoreInfo) object;
				if (storeInfo != null
						&& storeInfo.getmGoogleStoreInfo() != null&&!storeInfo.getmGoogleStoreInfo().isEmpty()) {

				} else if (storeInfo != null
						&& (storeInfo.getmGoogleStoreInfo() == null||storeInfo.getmGoogleStoreInfo().isEmpty())) {
					if (storeInfo.getStoreAlerts() != null
							&& !storeInfo.getStoreAlerts().isEmpty()) {
					} else {
						showProgressDialog();
						BuyOpic buyOpic = (BuyOpic) mContext
								.getApplicationContext();
						BuyopicNetworkServiceManager buyopicNetworkServiceManager = BuyopicNetworkServiceManager
								.getInstance(mContext);

						if (!storeInfo.getmStoreId().equalsIgnoreCase(
								DUMMY_BUYOPIC_STORE_ID)
								&& !storeInfo.getmRetailerId()
										.equalsIgnoreCase(
												DUMMY_BUYOPIC_RETAILER_ID)) {
							buyopicNetworkServiceManager
									.sendgetStoreAlertsRequest(
											Constants.REQUEST_GETSTORE_ALERTS,
											storeInfo.getmStoreId(),
											storeInfo.getmRetailerId(),
											buyOpic.getmConsumerId(), "",
											new BuyopicNetworkCallBack() {

												@Override
												public void onSuccess(
														int requestCode,
														Object object) {
													dismissProgressDialog();
													if (requestCode == Constants.REQUEST_GETSTORE_ALERTS) {
														storeAlerts = JsonResponseParser
																.parseStoreAlertsResponse(
																		(String) object,
																		storeInfo
																				.getmStoreName(),
																		storeInfo
																				.getmPhoneNumber());
														storeInfo
																.setmPhoneNumber(storeAlerts
																		.getmStorePhoneNumber());
														storeInfo
																.setStoreAlerts(storeAlerts
																		.getmAlerts());
														CustomClosebyExpandableListAdapter.this
																.notifyDataSetChanged();
													}
												}

												@Override
												public void onFailure(
														int requestCode,
														String message) {
													dismissProgressDialog();
												}
											});
						} else {
							buyopicNetworkServiceManager
									.sendConsumerPostedAlertsRequest(
											Constants.REQUEST_CREATE_CONSUMER_ALERT,
											buyOpic.getmConsumerId(),
											storeInfo
													.getmPostedAlertConsumerId(),
											new BuyopicNetworkCallBack() {

												@Override
												public void onSuccess(
														int requestCode,
														Object object) {
													dismissProgressDialog();
													if (requestCode == Constants.REQUEST_CREATE_CONSUMER_ALERT) {
														storeAlerts = JsonResponseParser
																.parseStoreAlertsResponse(
																		(String) object,
																		storeInfo
																				.getmStoreName(),
																		storeInfo
																				.getmPhoneNumber());
														storeInfo
																.setmPhoneNumber(storeAlerts
																		.getmStorePhoneNumber());
														storeInfo
																.setStoreAlerts(storeAlerts
																		.getmAlerts());
														CustomClosebyExpandableListAdapter.this
																.notifyDataSetChanged();
													}
												}

												@Override
												public void onFailure(
														int requestCode,
														String message) {
													dismissProgressDialog();
												}
											});
						}
					}
				}

			}
		}

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.custom_layout_offers_ic_overflow_menu) {
				Alert string = (Alert) v.getTag();
				displayPopUpMenu(v, string);
			} else if (v.getId() == R.id.custom_layout_home_list_view_more) {

			}

		}

	}

	private void displayPopUpMenu(View v, final Alert alert) {
		PopupMenu popupMenu = new PopupMenu(context, v);
		try {
			Field[] fields = popupMenu.getClass().getDeclaredFields();
			for (Field field : fields) {
				if ("mPopup".equals(field.getName())) {
					field.setAccessible(true);
					Object menuPopupHelper = field.get(popupMenu);
					Class<?> classPopupHelper = Class.forName(menuPopupHelper
							.getClass().getName());
					Method setForceIcons = classPopupHelper.getMethod(
							"setForceShowIcon", boolean.class);
					setForceIcons.invoke(menuPopupHelper, true);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		popupMenu.getMenuInflater().inflate(R.menu.menu_context,
				popupMenu.getMenu());
		popupMenu.getMenu().add(0, 1, 0, "Call " + alert.getmStoreName())
				.setIcon(R.drawable.ic_phone_icon);
		popupMenu.show();

		popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if (item.getItemId() == R.id.share) {
					sendShareStoreAlertRequestToServer(alert.getmOfferId(),
							consumerId);
					Utils.shareOffer(context, alert.getmOfferId(), consumerId,
							alert.getmPostedConsumerId(), alert.getmStoreId(),
							alert.getmRetailerId());
				} else if (item.getItemId() == 1) {
					Utils.callPhone(context, alert.getmPhoneNumber());
				}
				return true;
			}
		});
	}

	protected void sendShareStoreAlertRequestToServer(String mOfferId,
			String consumerId) {
		buyopicNetworkServiceManager
				.sendShareStoreAlertRequest(
						Constants.REQUEST_SHARE_STORE_ALERT, consumerId,
						mOfferId, this);
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
//		if (storeInfos.get(groupPosition).getmGoogleStoreInfo() == null) {
//			return false;
//		}
		StoreInfo object = (StoreInfo) storeInfos.get(groupPosition);

		if (object != null) {
			buyOpic.setmSelectedStoreLat(object.getStoreLatitude());
			buyOpic.setmSelectedStoreLong(object.getStoreLongitude());
		}
		if (object != null && object.getStoreAlerts() != null
				&& !object.getStoreAlerts().isEmpty()) {
			Alert alert = object.getStoreAlerts().get(childPosition);
			if (alert != null) {
				alert.setmStoreId(object.getmStoreId());
				alert.setmRetailerId(object.getmRetailerId());
				alert.setmDistanceValue(object.getDistanceValue());
				alert.setInBeaconRange(object.isInBeaconRange());
				clickListener.onItemClicked(alert);
			}
		}

		return true;
	}

	@SuppressWarnings("rawtypes")
	private boolean isThisGroupExpanded(int groupPosition) {
		boolean status = false;
		Iterator<?> iterator = buyOpic.getExpandedGroupPos().entrySet()
				.iterator();
		if (iterator != null) {
			while (iterator.hasNext()) {
				Map.Entry pairs = (Map.Entry) iterator.next();
				Integer key = (Integer) pairs.getKey();
				if (key.intValue() == groupPosition) {
					status = true;
					break;
				} else {
					status = false;
				}
			}
		}
		return status;
	}

	@Override
	public boolean onGroupClick(ExpandableListView arg0, View arg1, int arg2,
			long arg3) {
		StoreInfo object = (StoreInfo) storeInfos.get(arg2);
		if (object != null&&(object.getmGoogleStoreInfo()==null||object.getmGoogleStoreInfo().isEmpty())) {
			buyOpic.setmSelectedStoreLat(object.getStoreLatitude());
			buyOpic.setmSelectedStoreLong(object.getStoreLongitude());
			if(object.getmGoogleStoreInfo()!=null&&object.getmGoogleStoreInfo().size()>0){}
			else{
		clickListener.onItemClicked(object);
			}
		return true;
		}
		else{
			return false;
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

		// lastSeenItem=firstVisibleItem+visibleItemCount;
		lastSeenItem = firstVisibleItem;
		// lastSeenItem=firstVisibleItem+visibleItemCount;
		Utils.showLog("Last Seen Item Is" + lastSeenItem);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
		// TODO Auto-generated method stub
		final FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
		if(!isChecked){
			// TODO Auto-generated method stub
			CloseByListFragment secFrag = new CloseByListFragment();
			
            fragTransaction.replace(R.id.container,secFrag );
            fragTransaction.commit();
			
		}
		else{
			// TODO Auto-generated method stub
			MapViewFragment fragment=new MapViewFragment();
			fragTransaction.replace(R.id.closebyfragment,fragment);
            fragTransaction.commit();
			
		}
	}
	
	private void showNotificationOrderPopUp(OrderStatus orderStatus) {

		// TODO Auto-generated method stub

		View view = null;
		try {

			view = LayoutInflater.from(getActivity()).inflate(R.layout.ordernotificationdialog, null);

			dialog.setContentView(view);
			
			cancelDialog = (ImageButton) dialog.findViewById(R.id.dialog_notification_cancel);
			itemNameText = (TextView) dialog.findViewById(R.id.itemnametext);
			priceValueText = (TextView) dialog.findViewById(R.id.pricevaluetext);
			addressText=(TextView) dialog.findViewById(R.id.addresstext);
			quantityNumber = (TextView) dialog.findViewById(R.id.quantitynumber);
			timeOfDelivery=(TextView) dialog.findViewById(R.id.timeofdelivery);
			expTimeOfDelivery=(TextView) dialog.findViewById(R.id.exptimeofdelivery);
			orderStatusValue=(TextView) dialog.findViewById(R.id.orderstatus);
			orderStatusImage=(ImageView) dialog.findViewById(R.id.orderstatusimage);
			
			itemNameText.setText(orderStatus.getOfferItemName());
			timeOfDelivery.setText(orderStatus.getRequestedDelivery());
			expTimeOfDelivery.setText(orderStatus.getExpectedDelivery());
			orderStatusValue.setText(orderStatus.getStatusDesc());
			quantityNumber.setText(orderStatus.getOrderQuantity());
	
			if(orderStatus.getStatusDesc().equalsIgnoreCase("Canceled")){
				orderStatusImage.setImageResource(R.drawable.icon_canceled);
			}
			else if(orderStatus.getStatusDesc().equalsIgnoreCase("Inbox")){
				orderStatusImage.setImageResource(R.drawable.icon_message);
			}
			else if(orderStatus.getStatusDesc().equalsIgnoreCase("Confirmed")){
				orderStatusImage.setImageResource(R.drawable.icon_confirmed);
			}
			
			else if(orderStatus.getStatusDesc().equalsIgnoreCase("Dispatched")){
				orderStatusImage.setImageResource(R.drawable.icon_dispatched);
			}
			
			else if(orderStatus.getStatusDesc().equalsIgnoreCase("Delivered")){
				orderStatusImage.setImageResource(R.drawable.icon_delivered);
			}
			
			
			if (!orderStatus.getItemPrice().equalsIgnoreCase("0.00")) {
				priceValueText
						.setText(Constants.CURRENCYSYMBOL +orderStatus.getTotalOrderPrice());
			} else {
				priceValueText.setText("");
			}
			Log.i("CLOSEBY", "address_"+orderStatus.getAddressLine1()+orderStatus.getAddressLine3()+ " nesxt"
					+orderStatus.getAddressLine2());
			addressText.setText(orderStatus.getAddressLine1()+" "+orderStatus.getAddressLine2()+" "+orderStatus.getAddressLine3());
			cancelDialog.setOnClickListener(this);

			// dialog.setCancelable(false);
			// dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

		dialog.show();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.dialog_notification_cancel){
			dialog.dismiss();
			
		}
	}
	public void showAlertDialog(){
		Log.i("BLUETOOTH", "CBLF");
		
		
		context.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
				 
		        // Setting Dialog Title
		        alertDialog.setTitle("Would you like to turn on Bluetooth?");
		 
		        // Setting Dialog Message
		        alertDialog.setMessage("Turning Bluetooth on will give you better customer service.");
		 
		        // Setting Icon to Dialog
		 
		        // Setting Positive "Yes" Button
		        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog,int which) {
		            	
		            	setBluetooth(true);
		            	Log.i("BLUETOOTH", "YES");
		            	BuyopicNetworkServiceManager.TURN_BLUETOOTH_ON=false;
		            }
		        });
		 
		        // Setting Negative "NO" Button
		        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		            // Write your code here to invoke NO event
		            	BuyopicNetworkServiceManager.TURN_BLUETOOTH_OFF=true;
		            dialog.cancel();
		            }
		        });
		 
		 
		   	 	alertDialog.show();
				
			}
		});
	
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
	
	private boolean isEnabled() {
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		boolean isEnabled = bluetoothAdapter.isEnabled();
		return isEnabled;
	}
}
