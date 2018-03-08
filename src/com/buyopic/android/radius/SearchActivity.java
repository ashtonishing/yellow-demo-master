package com.buyopic.android.radius;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;

import com.buyopic.android.beacon.R;
import com.buyopic.android.fragments.OffersDetailFragment;
import com.buyopic.android.fragments.OffersListFragment;
import com.buyopic.android.fragments.SearchFragment;
import com.buyopic.android.models.Alert;
import com.buyopic.android.models.StoreInfo;
import com.buyopic.android.utils.Constants;
import com.buyopic.android.utils.FinishActivityReceiver;
import com.buyopic.android.utils.Utils;
import com.buyopic.android.utils.onAlertItemClickListener;

public class SearchActivity extends BaseActivity implements
		onAlertItemClickListener {

	private FinishActivityReceiver activityReceiver;
	private FragmentManager fragmentManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerReceiver();
		FrameLayout frameLayout = new FrameLayout(this);
		setContentView(frameLayout);
		BaseActivity baseActivity = this;
		if (baseActivity != null) {
			baseActivity.setBeaconActionBar("Search", 3);
		}
		frameLayout.setId(R.id.container);
		SearchFragment listFragment = new SearchFragment();
		fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.replace(R.id.container, listFragment).commit();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver();
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
	public void onItemClicked(Object object) {
		if (object instanceof Alert) {

			Alert storeInfo = (Alert) object;
			if(storeInfo.getmRetailerId()==null){
				
			}
			OffersDetailFragment offersDetailFragment = new OffersDetailFragment();
			Bundle bundle = new Bundle();

			bundle.putString(OffersDetailFragment.KEY_EXTRA_ITEM,
					storeInfo.getmOfferId());
			bundle.putString(OffersDetailFragment.KEY_EXTRA_TITLE,
					storeInfo.getmOfferTitle());
			bundle.putString(OffersDetailFragment.KEY_EXTRA_DISTANCE,
					storeInfo.getmDistanceValue());
			bundle.putString(OffersDetailFragment.KEY_EXTRA_CONSUMER_ID,
					storeInfo.getmPostedConsumerId());
			bundle.putString(OffersDetailFragment.KEY_EXTRA_PHONE_NUMBER,
					storeInfo.getmPhoneNumber());
			bundle.putBoolean(
					OffersDetailFragment.KEY_EXTRA_IS_IN_BEACON_RANGE,
					storeInfo.isInBeaconRange());
			bundle.putString(OffersDetailFragment.KEY_EXTRA_STORE_ID,
					storeInfo.getmStoreId());
			bundle.putString(OffersDetailFragment.KEY_EXTRA_RETAILER_ID,
					storeInfo.getmRetailerId());

			// bundle.putString(OffersDetailFragment.KEY_EXTRA_TITLE,
			// storeInfo.getmStoreName());
			// bundle.putBoolean(OffersDetailFragment.KEY_EXTRA_IS_IN_BEACON_RANGE,
			// storeInfo.isInBeaconRange());
			// bundle.putString(OffersDetailFragment.KEY_EXTRA_ITEM,
			// storeInfo.getmOfferId());
			offersDetailFragment.setArguments(bundle);
			FragmentTransaction fragmentTransaction = getSupportFragmentManager()
					.beginTransaction();
			fragmentTransaction
					.replace(R.id.container, offersDetailFragment)
					.addToBackStack(null).commit();
		} else if (object instanceof StoreInfo) {
			StoreInfo storeInfo = (StoreInfo) object;
			fragmentManager = getSupportFragmentManager();
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

			Utils.showLog("Is In Beacon Range" + storeInfo.isInBeaconRange());
			fragment.setArguments(bundle);
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();
			fragmentTransaction.replace(R.id.container, fragment, "103")
					.addToBackStack("store-info").commit();
		}

	}

}
