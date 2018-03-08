package com.buyopic.android.fragments;

import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.buyopic.android.adapters.CustomFavoriteFragmentAdapter;
import com.buyopic.android.beacon.R;
import com.buyopic.android.models.Alert;
import com.buyopic.android.models.StoreInfo;
import com.buyopic.android.network.BuyopicNetworkCallBack;
import com.buyopic.android.network.BuyopicNetworkServiceManager;
import com.buyopic.android.network.JsonResponseParser;
import com.buyopic.android.radius.BuyOpic;
import com.buyopic.android.utils.Constants;
import com.buyopic.android.utils.FetchCurrentLocation;
import com.buyopic.android.utils.FetchCurrentLocation.LocationResult;
import com.buyopic.android.utils.Utils;
import com.buyopic.android.utils.onAlertItemClickListener;

public class FavoritesFragment extends Fragment implements LocationResult,
		BuyopicNetworkCallBack, OnItemClickListener, OnScrollListener {

	private ListView mListView;
	private TextView mEmptyView;
	private ProgressBar mProgressBar;
	private Context mContext;
	private BuyopicNetworkServiceManager buyopicNetworkServiceManager;
	private BuyOpic buyOpic;
	private String consumerId;
	private onAlertItemClickListener alertItemClickListener;
	private int lastSeenItem;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		buyOpic = (BuyOpic) mContext.getApplicationContext();
		buyopicNetworkServiceManager = BuyopicNetworkServiceManager
				.getInstance(mContext);
		consumerId = buyOpic.getmConsumerId();
		FetchCurrentLocation currentLocation = new FetchCurrentLocation();
		currentLocation.fetchLocation(mContext, this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mContext = activity;
		alertItemClickListener = (onAlertItemClickListener) activity;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.layout_shared_fragment_view, null);
		mListView = (ListView) view.findViewById(R.id.shared_list_view);
		mListView.setOnItemClickListener(this);
		mListView.setOnScrollListener(this);
		mEmptyView = (TextView) view.findViewById(R.id.emptyview);
		mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);
		return view;
	}

	@Override
	public void gotLocation(Location location) {
		if (location != null) {
			//String mLat = String.valueOf(location.getLatitude());
			//String mLong = String.valueOf(location.getLongitude());
			
			// lat and long based on after changing the address
			
			String mLatitude;
			String mLongitude;
			if(buyOpic!=null&&buyOpic.getmSourceLatitude()!=0&&buyOpic.getmSourceLongitude()!=0){
				mLatitude=Double.toString(buyOpic.getmSourceLatitude());
				mLongitude=Double.toString(buyOpic.getmSourceLongitude());
				}
				else 
				{
					 mLatitude = String.valueOf(location.getLatitude());
					 mLongitude = String.valueOf(location.getLongitude());
				}
			buyopicNetworkServiceManager.sendMyFavoritesListRequest(
					Constants.REQUEST_FAVORITE, consumerId, mLatitude, mLongitude, this);
		}
		else
		{
			// to check whether the fragment is attached to activity or not
			if(!isAdded())
				return;
			else
			Utils.showDialog(mContext, getResources().getString(R.string.gps_fav_error_message));
		}

	}

	@Override
	public void onSuccess(int requestCode, Object object) {
		if (requestCode == Constants.REQUEST_FAVORITE) {
			List<StoreInfo> mFavorites = JsonResponseParser.parseStoreInfosForFavourite(
					mContext, (String) object);
			bindFavoritesToViews(mFavorites);
		}

	}

	private void bindFavoritesToViews(List<StoreInfo> mFavorites) {
		mProgressBar.setVisibility(View.GONE);
		if (mFavorites != null && !mFavorites.isEmpty()) {
			mListView.setVisibility(View.VISIBLE);
			mEmptyView.setVisibility(View.GONE);
			mListView.setAdapter(new CustomFavoriteFragmentAdapter(mContext,
					mFavorites, "", ""));
		} else {
			mListView.setVisibility(View.GONE);
			mEmptyView.setVisibility(View.VISIBLE);
			mEmptyView.setText("No Favorites");
		}

		mListView.clearFocus();
		mListView.postDelayed(new Runnable() {
			@Override
			public void run() {

				Utils.showLog("Trying to Retain the Scroll Postion:"
						+ buyOpic.getCloseByLastSelectionPage());
				;
				mListView.requestFocusFromTouch();
				mListView.setSelection((int) buyOpic
						.getCloseByLastSelectionPage());
				mListView.smoothScrollToPosition(buyOpic
						.getFavoriteLastSelectionPage());
				mListView.requestFocus();
			}
		}, 3000);
	}

	@Override
	public void onFailure(int requestCode, String message) {

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Object object = mListView.getAdapter().getItem(arg2);
		if (object instanceof StoreInfo) {
			StoreInfo storeInfo = (StoreInfo) object;
			Alert alert = new Alert();
			alert.setmPostedConsumerId(storeInfo.getmPostedAlertConsumerId());
			alert.setmStoreId(storeInfo.getmStoreId());
			alert.setmRetailerId(storeInfo.getmRetailerId());
			alert.setmOfferId(storeInfo.getmOfferId());
			alert.setmDistanceValue(storeInfo.getDistanceValue());
			alert.setInBeaconRange(storeInfo.isInBeaconRange());
			alertItemClickListener.onItemClicked(alert);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		buyOpic.setFavoriteLastSelectionPage(lastSeenItem);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		lastSeenItem = firstVisibleItem ;
//		lastSeenItem = firstVisibleItem + visibleItemCount;
		Utils.showLog("Last Seen Item Is" + lastSeenItem);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

}
