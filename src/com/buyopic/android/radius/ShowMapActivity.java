package com.buyopic.android.radius;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.buyopic.android.adapters.CustomMapListAdapter;
import com.buyopic.android.beacon.R;
import com.buyopic.android.locationsearch.PlacesAutoCompleteAdapter;
import com.buyopic.android.models.MapData;
import com.buyopic.android.models.PlacesList;
import com.buyopic.android.utils.Constants;
import com.buyopic.android.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShowMapActivity extends BaseActivity implements
		ConnectionCallbacks, OnConnectionFailedListener, OnMapClickListener,
		OnMapLongClickListener, OnClickListener {

	// Google Map
	private GoogleMap googleMap;
	private Location mLocation;
	private MarkerOptions markerOptions;
	private Marker mMarker;
	ProgressDialog mProgressDialog;

	private LocationClient mLocationClient;
	private double currentLatitude;
	private double currentLongitude;

	private AutoCompleteTextView acTextView;
	private ListView listview;
	PlaceIdServerTask task;
	ListView resultListView;
	Dialog dialog;
	Dialog mEnterAddrssDilg;
	MapData mMapData;
	List<PlacesList> listObjects = null;
	// String nameFromReg = "";
	ImageView mEnterAddressImg;
	EditText mAddress1;
	EditText mAddress2;
	EditText mAddressCity;
	EditText mAddressState;
	EditText mAddressPin;
	Button mAddressSave;
	Button mAddressEdit;
	private String imagePath;
	private BuyOpic buyOpic;
	private String longitude;
	private String latitude;
	boolean isEdited;

	public ShowMapActivity() {
		// TODO Auto-generated constructor stub
		super(R.string.action_settings);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = LayoutInflater.from(this)
				.inflate(R.layout.map_layout, null);
		setContentView(view);
		BaseActivity baseActivity = this;
		if (baseActivity != null) {
			baseActivity.setMapActionBar("Confirm Your Location", 1);
		}
		// Bundle bundle = this.getIntent().getExtras();
		/*
		 * if (bundle != null) nameFromReg = bundle.getString("username");
		 */

		buyOpic = (BuyOpic) getApplication();
		try {
			// Loading map
			mMapData = new MapData();
			if (buyOpic != null) {
				String mGoogleIconImage = buyOpic.getmConsumerGoogleIconImage();
				String mGooglePlaceID = buyOpic.getmConsumerGooglePlaceID();
				String mStoreAddress1 = buyOpic.getmConsumerAddress1();
				String mStoreCity = buyOpic.getmConsumerCity();
				String mStoreState = buyOpic.getmConsumerState();
				String mStoreAddress2 = buyOpic.getmConsumerAddress2();
				String mStorePostalCode = buyOpic.getmConsumerPostalCode();
				latitude = buyOpic.getmConsumerLatitude();
				longitude = buyOpic.getmConsumerLongitude();
				mMapData.setIconurl(mGoogleIconImage);
				mMapData.setLatitude(latitude);
				mMapData.setLongitude(longitude);
				mMapData.setmAddress1(mStoreAddress1);
				mMapData.setmAddress2(mStoreAddress2);
				mMapData.setPlaceid(mGooglePlaceID);
				mMapData.setmPostalCode(mStorePostalCode);
				mMapData.setmCity(mStoreCity);
				mMapData.setmState(mStoreState);

			}
			if (task == null)
				task = new PlaceIdServerTask();
			initilizeMap();

		} catch (Exception e) {
			e.printStackTrace();
		}

		acTextView = (AutoCompleteTextView) findViewById(R.id.auto_complete_tv);
		acTextView.setCompoundDrawablesWithIntrinsicBounds(null, null,
				getResources().getDrawable(R.drawable.ic_search), null);
		listview = (ListView) findViewById(R.id.listview);
		mEnterAddressImg = (ImageView) findViewById(R.id.entertextimg);
		mEnterAddressImg.setOnClickListener(this);
		dialog = new Dialog(this, R.style.Theme_Dialog);
		mEnterAddrssDilg = new Dialog(this, R.style.Theme_Dialog);
		showDefaultMapListDialog();

		acTextView.setAdapter(new PlacesAutoCompleteAdapter(
				getApplicationContext(), R.layout.list_item));

		acTextView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, final View view,
					int arg2, long arg3) {

				new Thread(new Runnable() {
					public void run() {
						Looper.prepare();
						getLatLongFromAddress("" + ((TextView) view).getText(),
								false);
						Looper.loop();
					}
				}).start();

			}
		});

		mLocationClient = new LocationClient(this, this, this);
		mLocationClient.connect();
	}

	private void showDefaultMapListDialog() {
		// TODO Auto-generated method stub
		View view = null;
		try {

			view = LayoutInflater.from(this).inflate(
					R.layout.layout_defaultmaplist_dialog_view, null);
			Utils.overrideFonts(this, view);
			dialog.setContentView(view);
			ImageButton cancelDialog = (ImageButton) view
					.findViewById(R.id.dialog_cancel);
			cancelDialog.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			dialog.setCancelable(false);
			// dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultListView = (ListView) view.findViewById(R.id.ListResult);

		resultListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				task = null;
				mMapData.setIconurl(listObjects.get(arg2).getIconUrl());
				mMapData.setLatitude(listObjects.get(arg2).getLatitude());
				mMapData.setLongitude(listObjects.get(arg2).getLongitude());
				mMapData.setmAddress1(listObjects.get(arg2).getName());
				mMapData.setmAddress2(listObjects.get(arg2).getVicinity());
				mMapData.setPlaceid(listObjects.get(arg2).getPlaceId());
				CameraPosition cameraPosition = new CameraPosition.Builder()
						.target(new LatLng(Double.valueOf(listObjects.get(arg2)
								.getLatitude()), Double.valueOf(listObjects
								.get(arg2).getLongitude()))).zoom(25).build();

				mMarker.setPosition(new LatLng(Double.valueOf(listObjects.get(
						arg2).getLatitude()), Double.valueOf(listObjects.get(
						arg2).getLongitude())));
				googleMap.animateCamera(CameraUpdateFactory
						.newCameraPosition(cameraPosition));
				showEnterAddressMapListDialog();
			}
		});

	}

	/**
	 * function to load map. If map is not created it will create it for you
	 * */
	private void initilizeMap() {
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();

			// googleMap.getUiSettings().setMyLocationButtonEnabled(true);
			// googleMap.setMyLocationEnabled(true);

			// enabling zoom options
			googleMap.getUiSettings().setZoomGesturesEnabled(true);

			mLocation = googleMap.getMyLocation();
			markerOptions = new MarkerOptions().position(
					new LatLng(mLocation.getLatitude(), mLocation
							.getLongitude())).draggable(true);
			// marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
			markerOptions.icon(BitmapDescriptorFactory
					.fromResource(R.drawable.ic_pin));

			mMarker = googleMap.addMarker(markerOptions);

			googleMap.setOnMapClickListener(this);

			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		initilizeMap();
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * getMenuInflater().inflate(R.menu.map_menu, menu); return
	 * super.onCreateOptionsMenu(menu); }
	 */

	/*
	 * @Override public boolean onOptionsItemSelected(MenuItem item) {
	 * 
	 * if (item.getItemId() == R.id.save_location) { Toast.makeText(
	 * getApplicationContext(), "Save Location " +
	 * markerOptions.getPosition().latitude + " xxxxx " +
	 * markerOptions.getPosition().longitude, Toast.LENGTH_LONG).show();
	 * mMapData
	 * .setLatitude(String.valueOf(markerOptions.getPosition().latitude));
	 * mMapData
	 * .setLongitude(String.valueOf(markerOptions.getPosition().longitude)); }
	 * 
	 * return super.onOptionsItemSelected(item); }
	 */
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {

	}

	@Override
	public void onConnected(Bundle arg0) {
		Location loc = mLocationClient.getLastLocation();

		if (loc != null) {

			if (latitude != null && longitude != null
					&& !latitude.equalsIgnoreCase("")
					&& !longitude.equalsIgnoreCase("")) {
				currentLatitude = Double.valueOf(latitude);
				currentLongitude = Double.valueOf(longitude);
			} else {
				currentLatitude = loc.getLatitude();
				currentLongitude = loc.getLongitude();
			}

			markerOptions = new MarkerOptions().position(
					new LatLng(currentLatitude, currentLongitude)).draggable(
					true);

			markerOptions.icon(BitmapDescriptorFactory
					.fromResource(R.drawable.ic_pin));

			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(new LatLng(currentLatitude, currentLongitude))
					.zoom(25).build();
			googleMap.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));

			mMarker = googleMap.addMarker(markerOptions);
			if (mMapData == null) {

				getAddress(currentLatitude, currentLongitude);

			}
			task.execute(String.valueOf(currentLatitude),
					String.valueOf(currentLongitude));
			googleMap.setOnMarkerDragListener(new OnMarkerDragListener() {

				@Override
				public void onMarkerDrag(Marker arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onMarkerDragEnd(Marker arg0) {
					final LatLng newLocation = arg0.getPosition();

					getAddress(newLocation.latitude, newLocation.longitude);

					if (task != null)
						task = null;
					task = new PlaceIdServerTask();

					task.execute(String.valueOf(newLocation.latitude),
							String.valueOf(newLocation.longitude));

					mMapData.setLatitude(String.valueOf(newLocation.latitude));
					mMapData.setLongitude(String.valueOf(newLocation.longitude));
				}

				@Override
				public void onMarkerDragStart(Marker arg0) {
					// TODO Auto-generated method stub

				}

			});

		} else {

			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.location_error_message),
					Toast.LENGTH_LONG).show();

		}

	}

	@Override
	public void onDisconnected() {

	}

	@Override
	public void onMapLongClick(LatLng arg0) {

	}

	@Override
	public void onMapClick(LatLng loctn) {
		googleMap.addMarker(new MarkerOptions()
				.position(loctn)
				.title("Save this location")
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
	}

	Handler addressHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg != null && msg.getData() != null) {
				double lat = msg.getData().getDouble("LATITUDE");
				double lng = msg.getData().getDouble("LONGITUDE");
				boolean isCapture = msg.getData().getBoolean("ISCAPTURE");

				CameraPosition cameraPosition = new CameraPosition.Builder()
						.target(new LatLng(lat, lng)).zoom(25).build();
				if (mMarker != null) {
					mMarker.setPosition(new LatLng(lat, lng));
					googleMap.animateCamera(CameraUpdateFactory
							.newCameraPosition(cameraPosition));
					getAddress(lat, lng);
					if (isCapture) {
						final Handler handler = new Handler();
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								// Do something after 5000ms
								CaptureMapScreen();
							}
						}, 10 * 1000);
					} else {
						showEnterAddressMapListDialog();
					}
				} else {

					Toast.makeText(
							getApplicationContext(),
							getResources().getString(R.string.location_error_message),
							Toast.LENGTH_LONG).show();

				}
			}
		};
	};

	Handler placesHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg != null && msg.getData() != null) {

			}
		};
	};

	public void getLatLongFromAddress(String youraddress, boolean isCapture) {
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

			Bundle bun = new Bundle();
			bun.putDouble("LATITUDE", lat);
			bun.putDouble("LONGITUDE", lng);
			bun.putBoolean("ISCAPTURE", isCapture);
			mMapData.setLatitude(String.valueOf(lat));
			mMapData.setLongitude(String.valueOf(lng));

			Log.i("MAP", "lat and long " + lat + " " + lng);
			Message msg = new Message();
			msg.setData(bun);

			addressHandler.sendMessage(msg);

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
				mMapData.setLatitude(String.valueOf(latitude));
				mMapData.setLongitude(String.valueOf(longitude));
				if(address.getLocality()!=null)
				mMapData.setmCity(address.getLocality());
				
				if(address.getCountryName()!=null)
				mMapData.setmCountry(address.getCountryName());
				
				if(address.getAddressLine(0)!=null)
				mMapData.setmAddress1(address.getAddressLine(0));
				
				if (address.getMaxAddressLineIndex() > 0)
					mMapData.setmAddress2(address.getAddressLine(1));
				
				if(address.getAdminArea()!=null)
				mMapData.setmState(address.getAdminArea());
				
				if(address.getPostalCode()!=null)
				mMapData.setmPostalCode(address.getPostalCode());

			}
		} catch (IOException e) {
			Log.e("tag", e.getMessage());
		}

		return result.toString();
	}

	@Override
	public void onBackPressed() {
		if (task != null) {
			task.cancel(true);
		}
		Intent mIntent = new Intent();
		mIntent.putExtra("mapdata",mMapData);
		mIntent.putExtra("imagepath",imagePath);
		setResult(Constants.RESULT_MAP,mIntent);
		this.finish();
	}

	// private ArrayList<String> buildAndInitiateSearchTask(double lat,double
	// lang) {
	//
	//
	//
	// try {
	// // Create a JSON object hierarchy from the results
	// JSONObject jsonObj = new JSONObject(jsonResults.toString());
	// JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
	//
	// Log.e("MAP", "" + jsonObj.toString());
	//
	// // Extract the Place descriptions from the results
	// resultList = new ArrayList<String>(predsJsonArray.length());
	// for (int i = 0; i < predsJsonArray.length(); i++) {
	// resultList.add(predsJsonArray.getJSONObject(i).getString(
	// "description"));
	// }
	// } catch (JSONException e) {
	// Log.e("MAP", "Cannot process JSON results", e);
	// }
	//
	//
	// return resultList;
	// }

	public class PlaceIdServerTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub

			String resultset;

			resultset = placeIdServerReq(arg0[0], arg0[1]);
			return resultset;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			if (result != null) {
				try {
					// Create a JSON object hierarchy from the results
					JSONObject jsonObj = new JSONObject(result.toString());
					String status = (String) jsonObj.get("status");
					if (status.equalsIgnoreCase("ok")) {
						JSONArray predsJsonArray = jsonObj
								.getJSONArray("results");

						Log.e("MAP", "" + jsonObj.toString());
						PlacesList list;
						// Extract the Place descriptions from the results
						listObjects = new ArrayList<PlacesList>(
								predsJsonArray.length());
						for (int i = 0; i < predsJsonArray.length(); i++) {
							list = new PlacesList();
							list.setLatitude(predsJsonArray.getJSONObject(i)
									.getString("latitude"));
							list.setLongitude(predsJsonArray.getJSONObject(i)
									.getString("longitude"));
							list.setName(predsJsonArray.getJSONObject(i)
									.getString("name"));
							list.setPlaceId(predsJsonArray.getJSONObject(i)
									.getString("placeId"));
							list.setIconUrl(predsJsonArray.getJSONObject(i)
									.getString("iconURL"));
							list.setVicinity(predsJsonArray.getJSONObject(i)
									.getString("vicinity"));
							listObjects.add(list);
						}
					} else if (status.equalsIgnoreCase("ZERO_RESULTS")) {

					}
				} catch (JSONException e) {
					Log.e("MAP", "Cannot process JSON results", e);
				}

			}

			if (listObjects != null) {

				// listview.setAdapter(new
				// PlacesAutoSearchAdapter(getApplicationContext(),R.layout.list_item,listObjects));

				customDialog(listObjects);

			}
		}

	}

	public void customDialog(List<PlacesList> listObjects) {

		if (listObjects != null) {
			List<String> placenameList = new ArrayList<String>();
			for (int i = 0; i < listObjects.size(); i++) {
				String placenames = listObjects.get(i).getName() + ""
						+ listObjects.get(i).getVicinity();
				placenameList.add(placenames);
			}

			if (placenameList != null && placenameList.size() > 0) {

				ListAdapter adapter = new CustomMapListAdapter(this,
						placenameList);
				resultListView.setAdapter(adapter);
				if (!task.isCancelled())
					dialog.show();
			} else {
				Toast.makeText(getApplicationContext(),
						"Could not found any search results", Toast.LENGTH_LONG)
						.show();

			}
		} else {
			Toast.makeText(getApplicationContext(),
					"Could not found any search results", Toast.LENGTH_LONG)
					.show();

		}

	}

	public String placeIdServerReq(String latitude, String longitude) {

		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {

			URL url = new URL(
					"https://cloud-ml.herokuapp.com/getGooglePlaceIdList?latitude="
							+ latitude + "&longitude=" + longitude + "&name="
							+ ""/* nameFromReg */);
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			Log.e("MAP", "Error processing Places API URL", e);
			return null;
		} catch (IOException e) {
			Log.e("MAP", "Error connecting to Places API", e);
			return null;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		return jsonResults.toString();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.entertextimg: {
			showEnterAddressMapListDialog();
			break;
		}
		case R.id.dialogaddressedit: {
			isEdited = true;
			mAddress1.setEnabled(true);
			mAddress2.setEnabled(true);
			mAddressCity.setEnabled(true);
			mAddressState.setEnabled(true);
			mAddressPin.setEnabled(true);
			break;
		}
		case R.id.dialogaddressave: {
			acTextView.setText("");
			if (!isEmpty(mAddress1) && !isEmpty(mAddress2)

			&& !isEmpty(mAddressCity) && !isEmpty(mAddressState)
					&& !isEmpty(mAddressPin)) {
				showProgressDialog();

				mMapData.setmAddress1(mAddress1.getText().toString());
				mMapData.setmAddress2(mAddress2.getText().toString());
				mMapData.setmCity(mAddressCity.getText().toString());
				mMapData.setmState(mAddressState.getText().toString());
				mMapData.setmPostalCode(mAddressPin.getText().toString());
				mEnterAddrssDilg.dismiss();
				if (isEdited) {
					isEdited = false;
					new Thread(new Runnable() {
						public void run() {
							Looper.prepare();
							getLatLongFromAddress(mAddress1.getText()
									.toString()
									+ " "
									+ mAddress2.getText().toString()
									+ " "
									+ mAddressCity.getText().toString()
									+ " "
									+ mAddressState.getText().toString()
									+ " "
									+ mAddressPin.getText().toString(), true);
							Looper.loop();
						}
					}).start();
				} else {
					CaptureMapScreen();
				}

			} else {

				Toast.makeText(getApplicationContext(),
						"Please enter all the fields and save the address",
						Toast.LENGTH_LONG).show();

			}
			break;
		}
		default: break;
		}

	}

	private boolean isEmpty(EditText editText) {

		return TextUtils.isEmpty(editText.getText().toString());
	}

	private void showEnterAddressMapListDialog() {
		// TODO Auto-generated method stub
		View view = null;

		try {

			view = LayoutInflater.from(this).inflate(
					R.layout.layout_enteraddressmap_dialog_view, null);
			Utils.overrideFonts(this, view);
			mEnterAddrssDilg.setContentView(view);
			ImageButton cancelDialog = (ImageButton) view
					.findViewById(R.id.dialog_cancel);
			cancelDialog.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mEnterAddrssDilg.dismiss();
				}
			});
			mEnterAddrssDilg.setCancelable(false);

			mAddress1 = (EditText) view
					.findViewById(R.id.layout_map_enteraddressname);
			mAddress2 = (EditText) view
					.findViewById(R.id.layout_map_streetaddress);

			mAddressCity = (EditText) view.findViewById(R.id.layout_map_city);
			mAddressState = (EditText) view
					.findViewById(R.id.layout_map_stateaddress);
			mAddressPin = (EditText) view
					.findViewById(R.id.layout_map_pinaddress);
			mAddressSave = (Button) view.findViewById(R.id.dialogaddressave);
			mAddressEdit = (Button) view.findViewById(R.id.dialogaddressedit);

			mAddressSave.setOnClickListener(this);
			mAddressEdit.setOnClickListener(this);
			if (mMapData != null) {
				mAddressCity.setText(mMapData.getmCity());
				mAddressPin.setText(mMapData.getmPostalCode());
				mAddressState.setText(mMapData.getmState());
				mAddress1.setText(mMapData.getmAddress1());
				mAddress2.setText(mMapData.getmAddress2());

			}
			mAddress1.setEnabled(false);
			mAddress2.setEnabled(false);
			mAddressCity.setEnabled(false);
			mAddressState.setEnabled(false);
			mAddressPin.setEnabled(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			mEnterAddrssDilg.show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getScreenCapture() {
		Log.i("SRT", "getScreenCapture");

		// image naming and path to include sd card appending name you choose
		// for file
		String mPath = Environment.getExternalStorageDirectory().toString()
				+ "/screenshots1.png";

		// create bitmap screen capture
		Bitmap bitmap;
		View v1 = findViewById(R.id.map);
		v1.getRootView();
		v1.setDrawingCacheEnabled(true);
		bitmap = Bitmap.createBitmap(v1.getDrawingCache());
		v1.setDrawingCacheEnabled(false);

		OutputStream fout = null;
		File imageFile = new File(mPath);

		try {
			fout = new FileOutputStream(imageFile);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, fout);
			fout.flush();
			fout.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) { // TODO
	 * Auto-generated method stub // CaptureMapScreen(); if (task != null) {
	 * task.cancel(true); } return super.onKeyDown(keyCode, event); }
	 */

	public void CaptureMapScreen() {
		SnapshotReadyCallback callback = new SnapshotReadyCallback() {
			Bitmap bitmap;

			@Override
			public void onSnapshotReady(Bitmap snapshot) {
				// TODO Auto-generated method stub
				bitmap = snapshot;
				saveImageToExternalStorage(bitmap);
			}
		};
		googleMap = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.map)).getMap();

		googleMap.snapshot(callback);

	}

	private void saveImageToExternalStorage(Bitmap finalBitmap) {
		// String root = Environment.getExternalStorageDirectory().toString();
		String appPath = getApplicationContext().getFilesDir()
				.getAbsolutePath();
		File myDir = new File(appPath + "/saved_images_yellow");
		myDir.mkdirs();
		// Random generator = new Random();
		// int n = 10000;
		// n = generator.nextInt(n);
		// String fname = "Image-" + n + ".png";
		String fname = "Image-" + ".png";
		File file = new File(myDir, fname);
		if (file.exists())
			file.delete();
		try {
			FileOutputStream out = new FileOutputStream(file);
			finalBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Tell the media scanner about the new file so that it is
		// immediately available to the user.
		MediaScannerConnection.scanFile(this, new String[] { file.toString() },
				null, new MediaScannerConnection.OnScanCompletedListener() {

					@Override
					public void onScanCompleted(String path, Uri uri) {
						// TODO Auto-generated method stub
						imagePath = path;
						Log.i("ExternalStorage", "Scanned " + path + ":");
						Log.i("ExternalStorage", "-> uri=" + uri);
						dismissProgressDialog();
					}
				});

	}

	private void showProgressDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = ProgressDialog.show(this, "", "Please wait",
					false, false);
		}
		mProgressDialog.show();
	}

	private void dismissProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
			onBackPressed();
		}
	}

}
