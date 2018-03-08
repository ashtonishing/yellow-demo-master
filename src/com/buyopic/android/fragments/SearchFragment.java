package com.buyopic.android.fragments;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.buyopic.android.adapters.CustomRecentSearchAdapter;
import com.buyopic.android.adapters.CustomSearchAdapter;
import com.buyopic.android.beacon.R;
import com.buyopic.android.models.Alert;
import com.buyopic.android.models.StoreInfo;
import com.buyopic.android.network.BuyopicNetworkCallBack;
import com.buyopic.android.network.BuyopicNetworkServiceManager;
import com.buyopic.android.network.JsonResponseParser;
import com.buyopic.android.radius.BaseActivity;
import com.buyopic.android.radius.BuyOpic;
import com.buyopic.android.utils.Constants;
import com.buyopic.android.utils.FetchCurrentLocation;
import com.buyopic.android.utils.FetchCurrentLocation.LocationResult;
import com.buyopic.android.utils.MergeAdapter;
import com.buyopic.android.utils.Utils;
import com.buyopic.android.utils.onAlertItemClickListener;

public class SearchFragment extends Fragment implements OnItemClickListener,
		OnEditorActionListener, BuyopicNetworkCallBack, LocationResult, OnClickListener {

	private EditText searchEditText;
	private ListView recentHistoryListView;
	private Activity mContext;
	private BuyopicNetworkServiceManager buyopicNetworkServiceManager;
	private BuyOpic buyOpic;
	private String mConsumerId;
	private String latitude = null;
	private String longitude = null;
	private ProgressDialog progressDialog;
	private onAlertItemClickListener alertItemClickListener;
	private TextView recentTextView;
	private HashMap<String, List<StoreInfo>> searchResultsMap;
	private ImageButton mSearchView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		buyopicNetworkServiceManager = BuyopicNetworkServiceManager
				.getInstance(mContext);
		buyOpic = (BuyOpic) mContext.getApplicationContext();
		mConsumerId = buyOpic.getmConsumerId();
		FetchCurrentLocation currentLocation = new FetchCurrentLocation();
		currentLocation.fetchLocation(mContext, this);
		buyopicNetworkServiceManager.sendSearchHistoryRequest(Constants.REQUEST_SEARCH_HISTORY, mConsumerId, this);

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mContext = activity;
		alertItemClickListener = (onAlertItemClickListener) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_search, null);
		searchEditText = (EditText) view.findViewById(R.id.search_edittext);
		showKeyBoard(searchEditText);
		searchEditText.setOnEditorActionListener(this);
		
		recentHistoryListView = (ListView) view
				.findViewById(R.id.layout_search_frag_recent_list);
		recentHistoryListView.setOnItemClickListener(this);
		recentTextView = (TextView) view.findViewById(R.id.recenttextview);
		mSearchView=(ImageButton)view.findViewById(R.id.actionSearch);
		mSearchView.setOnClickListener(this);
		Utils.overrideFonts(getActivity(), view);
		return view;
	}

	public void dismissKeyBoard(View v) {
		InputMethodManager inputManager = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);

	}

	
	public void showKeyBoard(View v){
		
		InputMethodManager inputMethodManager = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		// only will trigger it if no physical keyboard is open
		inputMethodManager.showSoftInput((View) v.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if(searchResultsMap!=null)
		{
			bindSearchResultsToViews(searchResultsMap);
		}
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		searchResultsMap=null;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Object object = recentHistoryListView.getAdapter().getItem(arg2);
		if (object instanceof String) {
			String string = (String) object;
			searchEditText.setText(string);
		} else if (object instanceof StoreInfo) {
			StoreInfo storeInfo=(StoreInfo) object;
			
			Alert alert=new Alert();
			alert.setmOfferId(storeInfo.getmOfferId());
			alert.setmOfferTitle(storeInfo.getmTitle());
			alert.setmDistanceValue(storeInfo.getDistanceValue());
			alert.setmPostedConsumerId(storeInfo.getmPostedAlertConsumerId());
			alert.setmPhoneNumber(storeInfo.getmPhoneNumber());
			alert.setInBeaconRange(storeInfo.isInBeaconRange());
			alert.setmStoreId(storeInfo.getmStoreId());
			alert.setmRetailerId(storeInfo.getmRetailerId());
			if(storeInfo.getmRetailerId()!=null)
			alertItemClickListener.onItemClicked(alert);
		}
	}

	@Override
	public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
		if (arg1 == EditorInfo.IME_ACTION_SEARCH) {
			dismissKeyBoard(arg0);
			performSearch(arg0.getText().toString());
			return true;
		}
		return false;
	}

	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = ProgressDialog.show(mContext, "", "Please wait",
					false, false);
		}
		progressDialog.show();
	}

	private void dismissProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	private void performSearch(String searchTerm) {
		if (!TextUtils.isEmpty(searchTerm.trim())) {
			if (latitude != null && longitude != null) {
				showProgressDialog();
				buyopicNetworkServiceManager.sendSearchRequest(
						Constants.REQUEST_SEARCH, mConsumerId,
						searchTerm.trim(), latitude, longitude, this);
			} else {
				Utils.showToast(mContext, "Please wait while fetching your location");
			}
		} else {
		}

	}

	@Override
	public void onSuccess(int requestCode, Object object) {
		dismissProgressDialog();
		switch (requestCode) {
		case Constants.REQUEST_SEARCH:
			searchResultsMap = JsonResponseParser
					.parseSearchResults(mContext,(String) object);
			bindSearchResultsToViews(searchResultsMap);
			break;
		case Constants.REQUEST_SEARCH_HISTORY:
			List<String> searchHistoryList=JsonResponseParser.parseSearchHistoryResponse((String)object);
			if(searchHistoryList!=null && !searchHistoryList.isEmpty()&& recentHistoryListView!=null)
			{
				
				recentHistoryListView.setAdapter(new CustomRecentSearchAdapter(
						getActivity(), searchHistoryList));
			}
		default:
			break;
		}
	}

	@Override
	public void onFailure(int requestCode, String message) {

	}

	private void bindSearchResultsToViews(
			HashMap<String, List<StoreInfo>> hashMap) {
		recentTextView.setVisibility(View.GONE);
		if (getActivity() != null) {
			BaseActivity baseActivity = (BaseActivity) getActivity();
			baseActivity.setBeaconActionBar("Search Results", 3);
		}

		MergeAdapter mergeAdapter = new MergeAdapter();
		if (hashMap != null && !hashMap.isEmpty()) {
			List<StoreInfo> mCloseByResults = null;
			if (hashMap.containsKey("close_by")) {

				mCloseByResults = hashMap.get("close_by");
				if (mCloseByResults != null && !mCloseByResults.isEmpty()) {
					mergeAdapter.addAdapter(new CustomSearchAdapter(mContext,
							mCloseByResults));
				}
			}
			
			if(mCloseByResults!=null && !mCloseByResults.isEmpty())
			{
			recentHistoryListView.setAdapter(mergeAdapter);
			}
			else
			{
				Utils.showToast(mContext, "No Search Results Found");
			}
		}
		
		
	}

	@Override
	public void gotLocation(Location location) {
		if (location != null) {
			DecimalFormat decimalFormat=new DecimalFormat("#0.000000");
			if(buyOpic!=null&&buyOpic.getmSourceLatitude()!=0&&buyOpic.getmSourceLongitude()!=0){
				latitude=Double.toString(buyOpic.getmSourceLatitude());
				longitude=Double.toString(buyOpic.getmSourceLongitude());
				}
				else 
				{
					 latitude = decimalFormat.format(location.getLatitude());
					 longitude = decimalFormat.format(location.getLongitude());
				}
		}
		else
		{
			Utils.showDialog(mContext, getResources().getString(R.string.gps_fav_error_message));
		}
	}

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.actionSearch)
		{
			dismissKeyBoard(v);
			if(!TextUtils.isEmpty(searchEditText.getText().toString()))
			{
			performSearch(searchEditText.getText().toString());
			}
			else
			{
				Utils.showToast(mContext, "Please enter a search keyword");
			}
		}
	}

}
