package com.buyopic.android.radius;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.buyopic.android.beacon.R;
import com.buyopic.android.fragments.CloseByListFragment;
import com.buyopic.android.fragments.FavoritesFragment;
import com.buyopic.android.fragments.OffersDetailFragment;
import com.buyopic.android.fragments.OffersListFragment;
import com.buyopic.android.fragments.SharedFragment;
import com.buyopic.android.fragments.SlideMenuOptionsFragment;
import com.buyopic.android.models.Alert;
import com.buyopic.android.models.StoreInfo;
import com.buyopic.android.network.BuyopicNetworkCallBack;
import com.buyopic.android.network.BuyopicNetworkServiceManager;
import com.buyopic.android.network.JsonResponseParser;
import com.buyopic.android.utils.Constants;
import com.buyopic.android.utils.FinishActivityReceiver;
import com.buyopic.android.utils.Utils;
import com.buyopic.android.utils.onAlertItemClickListener;

public class HomePageSetupActivity extends BaseActivity implements
		onAlertItemClickListener, TabListener, BuyopicNetworkCallBack {
	
	private static final String HOME_LOGO = "Home";
	private static final String SEARCH = "Search";
	private static final String NEW_LISTING = "New Listing";
	private static final String ALERT = "alert";
	public static final String STORE_INFO = "store-info";
	private static final String TITLE_LISTINGS = "Listings";
	private static final String TITLE_LOCATION= "My Location";
	//private static final String TITLE_LISTINGS = "Yellow - ATA (beta)";
	private static final String TAG_SHARED = "Shared";
	private static final String TAG_FAVORITES = "Favorites";
	private static final String TAG_CLOSE_BY = "Close by";

	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;
	private ActionBar actionBar;
	private CloseByListFragment homeListFragment;
	OffersDetailFragment offersDetailFragment;
	private FinishActivityReceiver activityReceiver;
	private BuyOpic buyOpic;
	private boolean isAnyFragmentAdded = false;
	private boolean showBlueToothPopUp=false;

	public HomePageSetupActivity() {
		super(R.string.action_settings);
	}

	private Tab addTab(String text) {
		return actionBar.newTab().setCustomView(makeTab(text))
				.setTabListener(this).setTag(text);
	}
	public void myClickMethod(View v){
		
		(offersDetailFragment).myClickMethod(v);
	}

	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_album);
		registerReceiver();
		BaseActivity baseActivity = (BaseActivity) this;
		
		BuyopicNetworkServiceManager buyopicNetworkServiceManager = BuyopicNetworkServiceManager
				.getInstance(this);
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.addTab(addTab(TAG_CLOSE_BY),false);
		actionBar.addTab(addTab(TAG_FAVORITES),false);
		actionBar.addTab(addTab(TAG_SHARED),false);
		actionBar.setStackedBackgroundDrawable(new ColorDrawable(getResources()
				.getColor(android.R.color.transparent)));

		fragmentManager = getSupportFragmentManager();
		buyOpic = (BuyOpic) getApplication();
		if(buyOpic.getmConsumerCity()!=null&&!buyOpic.getmConsumerCity().isEmpty())
		baseActivity.setBeaconActionBar(buyOpic.getmConsumerCity(), 2);
		else
			baseActivity.setBeaconActionBar(TITLE_LOCATION, 2);
		isAnyFragmentAdded = false;
		final Intent intent = getIntent();
		if (intent != null) {
			String url = intent.getDataString();
			if (url != null) {
				Uri uri = Uri.parse(url);
				String processType = uri
						.getQueryParameter(JsonResponseParser.TAG_PROCESS_TYPE);
				if (processType != null) {
					if (processType
							.equalsIgnoreCase(Constants.PROCESS_TYPE_SHARE)) {
						String processId = uri
								.getQueryParameter(JsonResponseParser.TAG_PROCESS_ID);
						buyopicNetworkServiceManager
								.sendUrlProcessDetailsRequest(
										Constants.REQUEST_URL_PROCESS_DETAILS,
										processId, this);
					} else if (processType
							.equalsIgnoreCase(Constants.PROCESS_TYPE_REGISTRATION)) {
						Intent intent2 = new Intent(this,
								ConsumerLoginActivity.class);
						intent2.putExtra(JsonResponseParser.TAG_PROCESS_URL,
								url);
						startActivity(intent2);
						finish();
					}
				}
			} else {
				Bundle bundle = getIntent().getExtras();
				if (bundle != null) {
					if (bundle
							.containsKey(SlideMenuOptionsFragment.KEY_IS_SHARED)) {
						boolean isFromShared = bundle
								.getBoolean(SlideMenuOptionsFragment.KEY_IS_SHARED);
						if (isFromShared) {
							actionBar.selectTab(actionBar.getTabAt(2));
						}
					} else if (bundle
							.containsKey(SlideMenuOptionsFragment.KEY_IS_FAVORITE)) {
						boolean isFromShared = bundle
								.getBoolean(SlideMenuOptionsFragment.KEY_IS_FAVORITE);
						if (isFromShared) {
							actionBar.selectTab(actionBar.getTabAt(1));
						}
					} else if (bundle
							.containsKey(BackgroundMonitorService.KEY_ISFROM_NOTIFICATION)) {
						boolean isFromNotification = bundle
								.getBoolean(BackgroundMonitorService.KEY_ISFROM_NOTIFICATION);
						if (isFromNotification) {
							OffersListFragment listFragment = new OffersListFragment();
							listFragment.setArguments(bundle);
							bundle.putBoolean(
									OffersListFragment.KEY_IS_IN_BEACON_RANGE,
									true);
							FragmentTransaction fragmentTransaction = getSupportFragmentManager()
									.beginTransaction();
							fragmentTransaction.replace(R.id.container,
									listFragment).commit();
						}
					}
					else if (bundle
							.containsKey(BackgroundMonitorService.KEY_ISFROM_ORDER_NOTIFICATION)){
						boolean isFromNotification = bundle
								.getBoolean(BackgroundMonitorService.KEY_ISFROM_ORDER_NOTIFICATION);
						if (isFromNotification) {
							if (buyOpic.getmConsumerRegistrationStatus()) {
						startActivity(new Intent(this, MyOrdersActivity.class));
							}
							else{
								Intent intent1 =new Intent(this, ConsumerLoginActivity.class);
								intent1.putExtra("callingactivity", Constants.REQUEST_ORDERS_LOGIN);
								startActivity(intent1);
								}
						}
					}
				/*	else if (bundle.containsKey(BackgroundMonitorService.KEY_ISFROM_NEAREST_BEACON_DATA)) {
						  showBlueToothPopUp=true;
					}*/
				}

				else {
					if (!isAnyFragmentAdded) {
						actionBar.selectTab(actionBar.getTabAt(0));
					}
				}

				if (buyOpic.getmConsumerId() != null
						&& buyOpic.getmConsumerEmail() != null) {
					if (!isBackgroundMonitorServiceRunning(this)) {
						buyOpic.startBackGroundBeaconMonitorService();
						 
					}
				}
				if(showBlueToothPopUp){
					if(!isEnabled())
					showAlertDialog();
				}

			}
		}
	}

	
	
	private void sendShareStoreAlertConfirmationRequest(String offerId,
			String consumerId, String LoggedInConsumerId) {

		BuyopicNetworkServiceManager buyopicNetworkServiceManager = BuyopicNetworkServiceManager
				.getInstance(this);
		buyopicNetworkServiceManager.sendShareStoreAlertConfimationRequest(
				Constants.REQUEST_SHARE_STORE_ALERT_CONFIRMATION, consumerId,
				offerId, LoggedInConsumerId, this);

	}

	private View makeTab(String tag) {
		View tabView = LayoutInflater.from(this).inflate(
				R.layout.custom_list_tab_textview, null);
		Utils.overrideFonts(this, tabView);
		TextView tabText = (TextView) tabView.findViewById(R.id.tab_text);
		if (tag.equalsIgnoreCase(TAG_CLOSE_BY)) {
			tabText.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.ic_closeby, 0, 0, 0);
		}
		else if (tag.equalsIgnoreCase(TAG_FAVORITES)) {
			tabText.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.ic_fav_tab, 0, 0, 0);
		} else if (tag.equalsIgnoreCase(TAG_SHARED)) {

			tabText.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.ic_share_tab, 0, 0, 0);
		}
		tabText.setText(tag);
		return tabView;
	}

	public boolean isEmpty(EditText editText) {
		return TextUtils.isEmpty(editText.getText().toString());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/*menu.add(0, 1, 0, NEW_LISTING).setIcon(R.drawable.ic_add_new_listings)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);*/
		menu.add(0, 1, 0, SEARCH).setIcon(R.drawable.ic_search)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		menu.add(0, 2, 0, HOME_LOGO).setIcon(R.drawable.ic_yellow)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 2:
			
			startActivity(new Intent(this, HomePageSetupActivity.class));
		
			break;
		case 1:
			startActivity(new Intent(this, SearchActivity.class));
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClicked(Object object) {
		if (object instanceof StoreInfo) {
			StoreInfo storeInfo = (StoreInfo) object;
			fragmentManager = getSupportFragmentManager();
			fragmentTransaction = fragmentManager.beginTransaction();
			Fragment fragment = new OffersListFragment();
			Bundle bundle = new Bundle();
			bundle.putString(OffersListFragment.KEY_STORE_ID,
					storeInfo.getmStoreId());
			bundle.putString(OffersListFragment.KEY_RETAILER_ID,
					storeInfo.getmRetailerId());
			bundle.putString(OffersListFragment.KEY_STORE_NAME,
					storeInfo.getmStoreName());
			bundle.putString(OffersListFragment.KEY_PHONE_NUMBER,
					storeInfo.getmPhoneNumber());
			bundle.putString(OffersListFragment.KEY_DISTANCE,
					storeInfo.getDistanceValue());
			bundle.putString(OffersListFragment.KEY_POSTED_CONSUMER_ALERT_ID,
					storeInfo.getmPostedAlertConsumerId());
			bundle.putBoolean(OffersListFragment.KEY_IS_IN_BEACON_RANGE,
					storeInfo.isInBeaconRange());

			fragment.setArguments(bundle);
			fragmentTransaction.replace(R.id.container, fragment, null)
					.addToBackStack(STORE_INFO).commit();
		} else if (object instanceof Alert) {
			Alert alert = (Alert) object;
			 offersDetailFragment = new OffersDetailFragment();
			Bundle bundle = new Bundle();
			bundle.putString(OffersDetailFragment.KEY_EXTRA_ITEM,
					alert.getmOfferId());
			bundle.putString(OffersDetailFragment.KEY_EXTRA_TITLE,
					alert.getmOfferTitle());
			bundle.putString(OffersDetailFragment.KEY_EXTRA_DISTANCE,
					alert.getmDistanceValue());
			bundle.putString(OffersDetailFragment.KEY_EXTRA_CONSUMER_ID,
					alert.getmPostedConsumerId());
			bundle.putString(OffersDetailFragment.KEY_EXTRA_PHONE_NUMBER,
					alert.getmPhoneNumber());
			bundle.putBoolean(
					OffersDetailFragment.KEY_EXTRA_IS_IN_BEACON_RANGE,
					alert.isInBeaconRange());
			bundle.putString(OffersDetailFragment.KEY_EXTRA_STORE_ID,
					alert.getmStoreId());
			bundle.putString(OffersDetailFragment.KEY_EXTRA_RETAILER_ID,
					alert.getmRetailerId());
			offersDetailFragment.setArguments(bundle);
			fragmentManager = getSupportFragmentManager();
			fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.container, offersDetailFragment, null)
					.addToBackStack(ALERT).commit();
		} else if (object instanceof Boolean) {
			boolean status = (Boolean) object;
			if (status) {
				actionBar.selectTab(actionBar.getTabAt(0));
			}
		}
	}

	private void clearBackStack() {
		FragmentManager fm = getSupportFragmentManager();
		int backstackCount = fm.getBackStackEntryCount();
		for (int i = 0; i < backstackCount; i++) {
			fm.popBackStack();
		}
	}

	@Override
	public void onTabReselected(Tab arg0, android.app.FragmentTransaction arg1) {
		clearBackStack();
		switch (arg0.getPosition()) {
		case 0:
			homeListFragment = new CloseByListFragment();
			arg1.replace(R.id.container, homeListFragment);
			break;
		case 1:
			arg1.replace(R.id.container, new FavoritesFragment());
			break;
		case 2:
			arg1.replace(R.id.container, new SharedFragment());
			break;

		default:
			break;
		}
	}

	@Override
	public void onTabSelected(Tab arg0, android.app.FragmentTransaction arg1) {
		clearBackStack();
		switch (arg0.getPosition()) {
		case 0:
			homeListFragment = new CloseByListFragment();
			arg1.replace(R.id.container, homeListFragment, String.valueOf(100));
			break;
		case 1:
			arg1.replace(R.id.container, new FavoritesFragment(),
					String.valueOf(101));
			break;
		case 2:
			arg1.replace(R.id.container, new SharedFragment(),
					String.valueOf(102));
			break;

		default:
			break;
		}
	}

	@Override
	public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver();
	}

	private boolean isBackgroundMonitorServiceRunning(Context mContext) {
		ActivityManager manager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (BackgroundMonitorService.class.getName().equals(
					service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public void registerReceiver() {
		IntentFilter filter = new IntentFilter(Constants.CUSTOM_ACTION_INTENT);
		activityReceiver = new FinishActivityReceiver(this);
		registerReceiver(activityReceiver, filter);
	}

	public void unregisterReceiver() {
		if (activityReceiver != null) {
			unregisterReceiver(activityReceiver);
			activityReceiver = null;
		}
	}

	@Override
	public void onSuccess(int requestCode, Object object) {
		if (requestCode == Constants.REQUEST_URL_PROCESS_DETAILS) {
			String url = JsonResponseParser
					.extractUrlFromGeneratedUrl((String) object);
			if (url != null) {
				Uri uri = Uri.parse(url);
				String offerId = uri
						.getQueryParameter(JsonResponseParser.TAG_OFFER_ID);
				String consumerId = uri
						.getQueryParameter(Utils.KEY_SHARED_BY_CONSUMER_ID);
				String postedConsumerId = uri
						.getQueryParameter(Utils.KEY_POSTED_CONSUMER_ID);
				String storeId = uri
						.getQueryParameter(JsonResponseParser.TAG_STORE_ID);
				String retailerId = uri
						.getQueryParameter(JsonResponseParser.TAG_RETAILER_ID);
			
				sendShareStoreAlertConfirmationRequest(offerId, consumerId,
						buyOpic.getmConsumerId());

				Bundle bundle = new Bundle();
				bundle.putString(OffersDetailFragment.KEY_EXTRA_CONSUMER_ID,
						postedConsumerId);
				bundle.putString(OffersDetailFragment.KEY_EXTRA_ITEM, offerId);
				bundle.putString(OffersDetailFragment.KEY_EXTRA_STORE_ID,
						storeId);
				bundle.putString(OffersDetailFragment.KEY_EXTRA_RETAILER_ID,
						retailerId);

				OffersDetailFragment listFragment = new OffersDetailFragment();
				listFragment.setArguments(bundle);
				FragmentTransaction fragmentTransaction = getSupportFragmentManager()
						.beginTransaction();
				fragmentTransaction.replace(R.id.container, listFragment)
						.commit();
				isAnyFragmentAdded = true;
			}

		}
	}

	@Override
	public void onFailure(int requestCode, String message) {
		actionBar.selectTab(actionBar.getTabAt(0));
	}

	public void showAlertDialog(){
		Log.i("BLUETOOTH", "CBLF");
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomePageSetupActivity.this);
		 
        // Setting Dialog Title
        alertDialog.setTitle("Would you like to turn on Bluetooth?");
 
        // Setting Dialog Message
        alertDialog.setMessage("Turning Bluetooth on will give you better customer service.");
 
        // Setting Icon to Dialog
 
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
            	
            	setBluetooth(true);
            	showBlueToothPopUp=false;
            }
        });
 
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            // Write your code here to invoke NO event
            dialog.cancel();
            }
        });
 
        // Showing Alert Message
        alertDialog.show();
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
