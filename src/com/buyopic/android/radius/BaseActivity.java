package com.buyopic.android.radius;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.buyopic.android.beacon.R;
import com.buyopic.android.fragments.CloseByListFragment;
import com.buyopic.android.fragments.SlideMenuOptionsFragment;
import com.buyopic.android.locationsearch.PlacesAutoCompleteAdapter;
import com.buyopic.android.models.StoreInfo;
import com.buyopic.android.network.BuyopicNetworkCallBack;
import com.buyopic.android.network.BuyopicNetworkServiceManager;
import com.buyopic.android.network.JsonResponseParser;
import com.buyopic.android.utils.Constants;
import com.buyopic.android.utils.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivityNew;

public class BaseActivity extends SlidingFragmentActivityNew implements
		OnTouchListener, BuyopicNetworkCallBack {

	public static final String KEY_UPDATELISTADRESSCHANGE = "updatelistafteraddresschange";
	protected ListFragment mFrag;
	private ActionBar actionBar;
	private SlidingMenu sm;
	private Dialog dialog;
	AutoCompleteTextView autoListView;
	private ImageButton cancelDialog;
	BuyOpic buyOpic;
	private ProgressDialog progressDialog;
	private TextView myCurrentLoc;
	private ImageButton homeButton;
	 BuyopicNetworkServiceManager buyopicNetworkServiceManager;

	public BaseActivity(int titleRes) {
		// mTitleRes = titleRes;
	}

	public BaseActivity() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prepareActionBar();
		getOverflowMenu();
		// setTitle(mTitleRes);
		setBehindContentView(R.layout.menu_frame);
		sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		sm.setMode(SlidingMenu.LEFT);
		setBehindContentView(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new SlideMenuOptionsFragment())
				.commit();
	}

	private void getOverflowMenu() {

		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void prepareActionBar() {
		actionBar = getActionBar();
		View v = LayoutInflater.from(this).inflate(
				R.layout.layout_custom_actionbar, null);
		int gravity = Gravity.CENTER | Gravity.LEFT;
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				ActionBar.LayoutParams.MATCH_PARENT,
				ActionBar.LayoutParams.MATCH_PARENT, gravity);
		actionBar.setCustomView(v, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME);
		actionBar.show();
		View homeIcon = findViewById(android.R.id.home);
		((View) homeIcon.getParent()).setVisibility(View.GONE);
		((View) homeIcon).setVisibility(View.GONE);
	}

	public void setHomeIcon(boolean status) {
		if (actionBar != null) {
			View view = actionBar.getCustomView();
			ImageButton imageButton = (ImageButton) view
					.findViewById(R.id.ic_home);
			imageButton.setVisibility(View.VISIBLE);

			if (status) {
				imageButton.setImageResource(R.drawable.ic_home_active);
			} else {
				imageButton.setImageResource(R.drawable.ic_home_inactive);
			}
		}
	}
	public void setMapActionBar(String title,int screen){

		if (screen == 0 || screen == 1) {
			View v = LayoutInflater.from(this).inflate(
					R.layout.layout_custom_actionbar_map, null);
			Utils.overrideFonts(this, v);
			TextView textView = (TextView) v
					.findViewById(R.id.actionbar_title);
			textView.setShadowLayer(1, 1, 1, Color.BLACK);
			textView.setText(title);
			ImageView homeButton = (ImageView) v
					.findViewById(R.id.ic_home);
			ImageButton closeButton = (ImageButton) v
					.findViewById(R.id.dialog_cancel);
			closeButton.setOnTouchListener(this);
			if(screen!=0)
			{
			homeButton.setOnTouchListener(this);
			}
			ActionBar.LayoutParams params = new ActionBar.LayoutParams(
					ActionBar.LayoutParams.MATCH_PARENT,
					ActionBar.LayoutParams.MATCH_PARENT, Gravity.LEFT);
			actionBar.setCustomView(v, params);
	}


}
	
	
	public void setLocationActionBar(String title,int screen){

			View v = LayoutInflater.from(this).inflate(
					R.layout.layout_custom_actionbar_location, null);
			ImageButton imageButton = (ImageButton) v
					.findViewById(R.id.ic_slidemenubutton);
			imageButton.setOnTouchListener(this);

			ImageButton homeButton = (ImageButton) v
					.findViewById(R.id.ic_location);
			homeButton.setOnTouchListener(this);

			ActionBar.LayoutParams params = new ActionBar.LayoutParams(
					ActionBar.LayoutParams.MATCH_PARENT,
					ActionBar.LayoutParams.MATCH_PARENT);
			actionBar.setCustomView(v, params);
			TextView textView = (TextView) v
					.findViewById(R.id.actionbar_title);
			textView.setText(title);
			Utils.overrideFonts(this, v);
			actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
					| ActionBar.DISPLAY_SHOW_HOME);
			actionBar.show();


}


	public  void setBeaconActionBar(String title, int screen) {
		if (actionBar != null) {
			if (screen == 0 || screen == 1) {
				View v = LayoutInflater.from(this).inflate(
						R.layout.layout_custom_actionbar_login, null);
				Utils.overrideFonts(this, v);
				TextView textView = (TextView) v
						.findViewById(R.id.actionbar_title);
			//	textView.setShadowLayer(1, 1, 1, Color.BLACK);
				textView.setText(title);
				ImageView homeButton = (ImageView) v
						.findViewById(R.id.ic_home);
				// homeButton.setOnClickListener(this);
				homeButton.setOnTouchListener(this);
				ActionBar.LayoutParams params = new ActionBar.LayoutParams(
						ActionBar.LayoutParams.MATCH_PARENT,
						ActionBar.LayoutParams.MATCH_PARENT, Gravity.LEFT);
				actionBar.setCustomView(v, params);
			} 
			else if (screen == 4 ){

				View v = LayoutInflater.from(this).inflate(
						R.layout.layout_custom_actionbar, null);
				ImageButton imageButton = (ImageButton) v
						.findViewById(R.id.menu_slideButtonbutton);
				imageButton.setOnTouchListener(this);
				 homeButton = (ImageButton) v
							.findViewById(R.id.ic_home);
				 homeButton.setVisibility(View.VISIBLE);

			
				ActionBar.LayoutParams params = new ActionBar.LayoutParams(
						ActionBar.LayoutParams.MATCH_PARENT,
						ActionBar.LayoutParams.MATCH_PARENT);
				actionBar.setCustomView(v, params);
				TextView textView = (TextView) v
						.findViewById(R.id.actionbar_title);
				textView.setText(title);
				Utils.overrideFonts(this, v);
				actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
						| ActionBar.DISPLAY_SHOW_HOME);
				actionBar.show();
			
				
			}else {
				View v = LayoutInflater.from(this).inflate(
						R.layout.layout_custom_actionbar, null);
				ImageButton imageButton = (ImageButton) v
						.findViewById(R.id.menu_slideButtonbutton);
				imageButton.setOnTouchListener(this);

				 homeButton = (ImageButton) v
						.findViewById(R.id.ic_home);
				 homeButton.setVisibility(View.VISIBLE);
				homeButton.setOnTouchListener(this);
				TextView location = (TextView) v
						.findViewById(R.id.actionbar_title);
				location.setOnTouchListener(this);

				ActionBar.LayoutParams params = new ActionBar.LayoutParams(
						ActionBar.LayoutParams.MATCH_PARENT,
						ActionBar.LayoutParams.MATCH_PARENT);
				actionBar.setCustomView(v, params);
				TextView textView = (TextView) v
						.findViewById(R.id.actionbar_title);
				textView.setText(title);
				Utils.overrideFonts(this, v);
				actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
						| ActionBar.DISPLAY_SHOW_HOME);
				actionBar.show();
			}
		}
	}

	public void closeMenu() {
		if (sm != null) {
			sm.showContent(true);
		}
	}

	public void showMenu() {
		if (sm != null) {
			sm.showMenu();
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			switch (v.getId()) {
			case R.id.menu_slideButtonbutton:
				showMenu();
				break;
			case R.id.ic_home:
				showDefaultMapListDialog();
				break;
			case R.id.actionbar_title:
				showDefaultMapListDialog();
				break;
			case R.id.dialog_cancel:
				onBackPressed();
				break;
			case R.id.dialogspotlight_cancel:
				dialog.dismiss();
				break;
				
			case R.id.button1:
				dialog.dismiss();
				showProgressDialog();
				 buyOpic=(BuyOpic) getApplication();
				 AddressTask task =new AddressTask();
				 task.execute();
				
				/* buyopicNetworkServiceManager = BuyopicNetworkServiceManager
							.getInstance(getApplicationContext());
					// TODO Auto-generated method stub
					buyopicNetworkServiceManager.sendNearestStoreAlertsRequest(
							Constants.REQUEST_NEAREST_STORE_ALERTS, "", "", "",
							Double.toString(buyOpic.getmSourceLatitude()),Double.toString( buyOpic.getmSourceLongitude()), buyOpic.getmConsumerId(), "NO", null, true, this,
							getApplicationContext());
							*/
					

				
				
				break;
			default:
				break;
			}
		}
		return false;
	}
	

	private void showDefaultMapListDialog() {
		// TODO Auto-generated method stub
		View view = null;
		dialog = new Dialog(this, R.style.Theme_Dialog);
		try {

			view = LayoutInflater.from(this).inflate(
					R.layout.layout_spotlightmap_dialog, null);
			Utils.overrideFonts(this, view);
			dialog.setContentView(view);
			 cancelDialog = (ImageButton) view
					.findViewById(R.id.dialogspotlight_cancel);
			 myCurrentLoc=(TextView)view.findViewById(R.id.dialogspotlight_currentloc);
			 showKeyBoard(myCurrentLoc);
			ImageButton okBtn=(ImageButton)view.findViewById(R.id.button1);
			okBtn.setOnTouchListener(this);
			
		 buyOpic=(BuyOpic) getApplication();
		if(buyOpic!=null)
		{
			if(buyOpic.getmConsumerCity()!=null&&!buyOpic.getmConsumerCity().isEmpty()){
				
				myCurrentLoc.setText(buyOpic.getmConsumerCity());
			}
			
		}
		  cancelDialog.setOnTouchListener(this);
			dialog.setCancelable(false);
			// dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

		autoListView = (AutoCompleteTextView) view.findViewById(R.id.auto_complete_tv_spotlight);

		autoListView.setAdapter(new PlacesAutoCompleteAdapter(
				getApplicationContext(), R.layout.list_item));

		autoListView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> arg0, final View view,
					int arg2, long arg3) {

				new Thread(new Runnable() {
					public void run() {
						autoListView.setEnabled(false);
						Looper.prepare();
						getLatLongFromAddress("" + ((TextView) view).getText(),false);
						Looper.loop();
					}
				}).start();

			}
		});
		dialog.show();

	}
	public void showKeyBoard(View v){
		
		InputMethodManager inputMethodManager = (InputMethodManager) 
				getSystemService(Context.INPUT_METHOD_SERVICE);
		// only will trigger it if no physical keyboard is open
		inputMethodManager.showSoftInput((View) v.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED);
	}
	public void getLatLongFromAddress(String youraddress,boolean isCapture) {
		try {
			String uri = "https://maps.googleapis.com/maps/api/geocode/json?address="
					+ URLEncoder.encode(youraddress, "utf8")
					+ "&key=AIzaSyBFzMDXh7JcH1XN8QtWIBqkOLzFK_vWfCY";

			HttpGet httpGet = new HttpGet(uri);
			HttpClient client = new DefaultHttpClient();
			HttpResponse response;
			StringBuilder stringBuilder = new StringBuilder();

			HttpURLConnection conn = null;

			URL url = new URL(uri);
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			int b;
			while ((b = in.read()) != -1) {
				stringBuilder.append((char) b);
			}
			JSONObject jsonObject = new JSONObject();

			jsonObject = new JSONObject(stringBuilder.toString());

			double lng = ((JSONArray) jsonObject.get("results"))
					.getJSONObject(0).getJSONObject("geometry")
					.getJSONObject("location").getDouble("lng");

			double lat = ((JSONArray) jsonObject.get("results"))
					.getJSONObject(0).getJSONObject("geometry")
					.getJSONObject("location").getDouble("lat");

			Log.d("latitude", "" + lat);
			Log.d("longitude", "" + lng);
			Log.i("Loc changes", "baseactivity setting"+"lat "+lat+" long "+lng);
			buyOpic.setmSourceLatitude(lat);
			buyOpic.setmSourceLongitude(lng);


		

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();

		}

	}
	
	
	private String getAddress(double latitude, double longitude) {
		StringBuilder result = new StringBuilder();
		try {
			Geocoder geocoder = new Geocoder(this, Locale.getDefault());
			List<Address> addresses = geocoder.getFromLocation(latitude,
					longitude, 1);
			if (addresses.size() > 0) {
				Address address = addresses.get(0);
				result.append(address.getLocality()).append("\n");
				result.append(address.getCountryName());
				result.append(address.getPostalCode());
				result.append(address.getMaxAddressLineIndex());
				result.append(address.getFeatureName()).append("\n");
				result.append(address.getAddressLine(0)).append("\n");
				buyOpic.setmConsumerCity(address.getLocality());
			}
		} 
		catch (IOException e) {
			Log.e("tag", e.getMessage());
		}
		
		
		return null;
	
	
	

}
	
	
	private void showProgressDialog() {
		try {
			if (progressDialog == null) {
				progressDialog = ProgressDialog.show(getApplicationContext(), "",
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
	
public class AddressTask extends AsyncTask<String, String, String>{
	
	

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... params) {
		getAddress((buyOpic.getmSourceLatitude()),buyOpic.getmSourceLongitude());
		return null;
	}
	
	@Override
	protected void onPostExecute(String result) {
		if(buyOpic!=null)
		{
			Log.i("TEST", "city"+buyOpic.getmConsumerCity());
			if(buyOpic.getmConsumerCity()!=null&&!buyOpic.getmConsumerCity().isEmpty()){
				
				setBeaconActionBar(buyOpic.getmConsumerCity(), 2);
				Constants.isAddresschanged=true;
				 homeButton.setImageResource(R.drawable.icon_map);
				
				 
			}
			
		}
		 switchfragment();
			
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}

	private void switchfragment() {
		// TODO Auto-generated method stub
		final FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
		// TODO Auto-generated method stub
		CloseByListFragment secFrag = new CloseByListFragment();
		
        fragTransaction.replace(R.id.container,secFrag );
        fragTransaction.commit();
	}
	
	
}

@Override
public void onSuccess(int requestCode, Object object) {
	// TODO Auto-generated method stub


	switch (requestCode) {
	case Constants.REQUEST_NEAREST_STORE_ALERTS:
		dismissProgressDialog();
		 List<StoreInfo> storeInfos;
		storeInfos = JsonResponseParser.parseStoreInfos(getApplicationContext(),
				(String) object);
	
		updateNewDataToUI(storeInfos);
		break;
	default:
		break;
	}


	
}

@Override
public void onFailure(int requestCode, String message) {
	// TODO Auto-generated method stub
	dismissProgressDialog();
}

private void updateNewDataToUI(List<StoreInfo> storeInfos) {
	if (storeInfos != null && !storeInfos.isEmpty()) {
		if (buyOpic.isUserInHomeListFragment()) {
			Intent intent = new Intent(
					CloseByListFragment.ACTION__BEACON_DATA_CHANGED_INTENT);
			intent.putParcelableArrayListExtra(KEY_UPDATELISTADRESSCHANGE,
					(ArrayList<StoreInfo>) storeInfos);
			getApplicationContext().sendBroadcast(intent);
		} else {

		}
	}
}
}
