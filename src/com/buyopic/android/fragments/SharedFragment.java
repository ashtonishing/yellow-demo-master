package com.buyopic.android.fragments;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.buyopic.android.adapters.CustomSharedWithMeFragmentAdapter;
import com.buyopic.android.beacon.R;
import com.buyopic.android.models.Alert;
import com.buyopic.android.network.BuyopicNetworkCallBack;
import com.buyopic.android.network.BuyopicNetworkServiceManager;
import com.buyopic.android.network.JsonResponseParser;
import com.buyopic.android.radius.BuyOpic;
import com.buyopic.android.utils.Constants;
import com.buyopic.android.utils.Utils;
import com.buyopic.android.utils.onAlertItemClickListener;

public class SharedFragment extends Fragment implements
		OnCheckedChangeListener, BuyopicNetworkCallBack, OnItemClickListener, OnScrollListener {

	private Context context;
	private ListView listView;
	private TextView emptyView;
	private ProgressBar mProgressBar;
	private CheckBox switchButton;
	private String mConsumerId;
	private BuyopicNetworkServiceManager buyopicNetworkServiceManager;
	private List<HashMap<String, String>> sharedWithMeList;
	private List<HashMap<String, String>> sharedByMeList;
	private onAlertItemClickListener alertItemClickListener;
	private int lastSeenItem;
	private BuyOpic buyOpic;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		sendSharedWithMeRequest();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		buyOpic = (BuyOpic) context.getApplicationContext();
		mConsumerId = buyOpic.getmConsumerId();

		buyopicNetworkServiceManager = BuyopicNetworkServiceManager
				.getInstance(context);
	}
	
	private void sendSharedWithMeRequest()
	{
		mProgressBar.setVisibility(View.VISIBLE);
		listView.setVisibility(View.GONE);
		emptyView.setVisibility(View.GONE);
		buyopicNetworkServiceManager.sendListSharedWithMeRequest(
				Constants.REQUEST_LIST_SHARE_WITH_ME, mConsumerId, this);
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
	
	private void sendSharedByMeRequest()
	{
		mProgressBar.setVisibility(View.VISIBLE);
		listView.setVisibility(View.GONE);
		emptyView.setVisibility(View.GONE);
		buyopicNetworkServiceManager.sendListSharedByMeRequest(
				Constants.REQUEST_LIST_SHARE_BY_ME, mConsumerId, this);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
		alertItemClickListener=(onAlertItemClickListener) activity;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.layout_shared_fragment_view, null);
		switchButton = (CheckBox) view
				.findViewById(R.id.layout_create_offer_checkbox_activate);
		switchButton.setVisibility(View.VISIBLE);
		switchButton.setOnCheckedChangeListener(this);
		listView = (ListView) view.findViewById(R.id.shared_list_view);
		listView.setOnItemClickListener(this);
		listView.setOnScrollListener(this);
		emptyView = (TextView) view.findViewById(R.id.emptyview);
		mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);
		return view;
	}

	

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
		if (!isChecked) {
			if (sharedWithMeList != null && !sharedWithMeList.isEmpty()) {
				bindSharedDataToViews(sharedWithMeList);
			} else {
				sendSharedWithMeRequest();
			}
		} else {
			if (sharedByMeList != null && !sharedByMeList.isEmpty()) {
				bindSharedByMeDataToViews(sharedByMeList);
			} else {
				sendSharedByMeRequest();
			}
		}
	}

	private void bindSharedByMeDataToViews(
			List<HashMap<String, String>> sharedByMeList2) {
		mProgressBar.setVisibility(View.GONE);
		if (sharedByMeList2 != null && !sharedByMeList2.isEmpty()) {
			listView.setVisibility(View.VISIBLE);
			listView.setAdapter(new CustomSharedWithMeFragmentAdapter(context,
					sharedByMeList2,false));
		} else {
			listView.setVisibility(View.GONE);
			emptyView.setVisibility(View.VISIBLE);
			emptyView.setText("No Results");
		}
	}

	private void bindSharedDataToViews(
			List<HashMap<String, String>> sharedWithMeList2) {
		mProgressBar.setVisibility(View.GONE);
		if (sharedWithMeList2 != null && !sharedWithMeList2.isEmpty()) {
			listView.setVisibility(View.VISIBLE);
			listView.setAdapter(new CustomSharedWithMeFragmentAdapter(context,
					sharedWithMeList2,true));
		} else {
			listView.setVisibility(View.GONE);
			emptyView.setVisibility(View.VISIBLE);
			emptyView.setText("No Results");
		}
	}

	@Override
	public void onSuccess(int requestCode, Object object) {
		switch (requestCode) {
		case Constants.REQUEST_LIST_SHARE_WITH_ME:
			sharedWithMeList = JsonResponseParser
					.parseSharedWithMeResponse(context,(String) object);
			bindSharedDataToViews(sharedWithMeList);
			break;
		case Constants.REQUEST_LIST_SHARE_BY_ME:
			sharedByMeList = JsonResponseParser
					.parseSharedWithMeResponse(context,(String) object);
			bindSharedByMeDataToViews(sharedByMeList);
			break;

		default:
			break;
		}

	}

	@Override
	public void onFailure(int requestCode, String message) {

	}

	@SuppressWarnings("unchecked")
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		HashMap<String, String> hashMap=(HashMap<String, String>) listView.getAdapter().getItem(arg2);
		if(hashMap!=null)
		{
			Alert alert=new Alert();
			alert.setmStoreId(hashMap.get("store_id"));
			alert.setmRetailerId(hashMap.get("retailer_id"));
			alert.setmOfferId(hashMap.get("alert_id"));
			alert.setmPostedConsumerId(hashMap.get("posted_consumer_id"));
			boolean isInBeaconRange=hashMap.containsKey("isinbeaconrange")?hashMap.get("isinbeaconrange").equalsIgnoreCase("true")?true:false:false;
			alert.setInBeaconRange(isInBeaconRange);
			alert.setmDistanceValue(hashMap.get("distance"));
			alertItemClickListener.onItemClicked(alert);
		}
	}

}
