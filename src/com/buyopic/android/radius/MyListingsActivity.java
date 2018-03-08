package com.buyopic.android.radius;

import java.util.List;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.buyopic.android.adapters.CustomMyListingsAdapter;
import com.buyopic.android.beacon.R;
import com.buyopic.android.models.Alert;
import com.buyopic.android.network.BuyopicNetworkCallBack;
import com.buyopic.android.network.BuyopicNetworkServiceManager;
import com.buyopic.android.network.JsonResponseParser;
import com.buyopic.android.utils.Constants;
import com.buyopic.android.utils.FinishActivityReceiver;
import com.buyopic.android.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MyListingsActivity extends BaseActivity implements
		BuyopicNetworkCallBack, OnItemClickListener {

	private ListView mListView;
	private ProgressBar mProgressBar;
	private TextView mEmptyView;
	private BuyOpic buyOpic;
	private TextView mConsumerName;
	private TextView mConsumerAddress;
	private FinishActivityReceiver activityReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerReceiver();
		View view=LayoutInflater.from(this).inflate(R.layout.layout_my_listings_activity, null);
		setContentView(view);
		Utils.overrideFonts(this,view);
		buyOpic = (BuyOpic) getApplication();
		prepareViews();

		BuyopicNetworkServiceManager buyopicNetworkServiceManager = BuyopicNetworkServiceManager
				.getInstance(this);
		buyopicNetworkServiceManager.sendMyListingsRequest(
				Constants.REQUEST_GETSTORE_ALERTS, buyOpic.getmConsumerId(),
				this);
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
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver();
	}

	private void prepareViews() {
		mListView = (ListView) findViewById(R.id.alerts_list_view);
		mListView.setOnItemClickListener(this);
		mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
		mEmptyView = (TextView) findViewById(R.id.emptyview);
		BaseActivity baseActivity = this;
		if (baseActivity != null) {
			baseActivity.setBeaconActionBar("My Listings", 3);
		}
		mConsumerName=(TextView)findViewById(R.id.layout_offer_desc_consumer_name);
		mConsumerAddress=(TextView)findViewById(R.id.layout_offer_desc_consumer_address);
		mConsumerAddress.setText(buyOpic.getmConsumerAddress());
		mConsumerName.setText(buyOpic.getmConsumerName());
		ImageView imageView=(ImageView) findViewById(R.id.custom_layout_home_list_view_store_logo);
		ImageLoader imageLoader=ImageLoader.getInstance();
		imageLoader.displayImage(buyOpic.getmConsumerProfilePic(), imageView);
	}

	private void bindDataToViews(List<Alert> alerts) {
		mProgressBar.setVisibility(View.GONE);
		if (alerts != null && !alerts.isEmpty()) {
			mListView.setVisibility(View.VISIBLE);
			mEmptyView.setVisibility(View.GONE);
			mListView.setAdapter(new CustomMyListingsAdapter(this, alerts));
		} else {
			mListView.setVisibility(View.GONE);
			mEmptyView.setVisibility(View.VISIBLE);
			mEmptyView.setText("No Results Found");
		}
	}

	@Override
	public void onSuccess(int requestCode, Object object) {
		switch (requestCode) {
		case Constants.REQUEST_GETSTORE_ALERTS:

			List<Alert> alerts = JsonResponseParser
					.parseMyListingsResponse((String) object);
			bindDataToViews(alerts);
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/*menu.add(0, 1, 0, "Add").setIcon(R.drawable.ic_add_new_listings)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);*/
		menu.add(0, 1, 0, "Add").setIcon(R.drawable.ic_yellow)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.add(0, 2, 0, "Add").setIcon(R.drawable.ic_search)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 2:
			//startActivity(new Intent(this, CreateNewListingActivity.class));
			startActivity(new Intent(this, HomePageSetupActivity.class));
			break;
		case 1:
			startActivity(new Intent(this, SearchActivity.class));
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onFailure(int requestCode, String message) {

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Object object=mListView.getAdapter().getItem(arg2);
		if(object instanceof Alert)
		{
			Alert alert=(Alert) object;
			Intent intent=new Intent(this,CreateNewListingActivity.class);
			intent.putExtra("offer_id", alert.getmOfferId());
			startActivity(intent);
			finish();
		}
	}

}
