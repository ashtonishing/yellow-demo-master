package com.buyopic.android.fragments;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.buyopic.android.beacon.R;
import com.buyopic.android.models.StoreInfo;
import com.buyopic.android.network.GoogleStoreInfo;
import com.buyopic.android.radius.BuyOpic;
import com.buyopic.android.radius.HomePageSetupActivity;
import com.buyopic.android.utils.FetchCurrentLocation.LocationResult;
import com.buyopic.android.utils.Utils;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapViewFragment extends Fragment implements ConnectionCallbacks,
		LocationResult,OnInfoWindowClickListener {

	private GoogleMap googleMap;
	private Activity context;
	private BuyOpic buyOpic;
	private static View view;
	List<StoreInfo> mstoreInfos;
	List<GoogleStoreInfo> mgoogleSoreInfo;
	StoreInfo mStoreInfo;
	private ProgressDialog progressDialog;
	List<Double[]> latlonglist;
	private LocationClient mLocationClient;
	private double currentLatitude;
	private double currentLongitude;
	private Location mLocation;
	List<StoreInfo> yellowLatLongList = null;
	List<StoreInfo> googleLatLongList = null;
	MarkerOptions marker ;
	List<Marker> markerList ;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null)
				parent.removeView(view);
		}
		try {
			view = inflater.inflate(R.layout.mapfragmentlayout, container,
					false);
		} catch (InflateException e) {
			/* map is already there, just return view as it is */
		}
		initilizeMap();
		markerList= new ArrayList<Marker>();
		if (yellowLatLongList != null && !yellowLatLongList.isEmpty()) {
			for (int i = 0; i < yellowLatLongList.size(); i++) {
				// Adding a marker
				marker = new MarkerOptions().position(
						new LatLng(yellowLatLongList.get(i).getStoreLatitude(),
								yellowLatLongList.get(i).getStoreLongitude()))
						.title(mstoreInfos.get(i).getmStoreName()).snippet("Yellow Store")
						.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.logo));
            Marker m=googleMap.addMarker(marker);
            markerList.add(m);
				//googleMap.addMarker(marker);

			}
		}
		if (googleLatLongList != null && !googleLatLongList.isEmpty()) {
			for (int i = 0; i < googleLatLongList.size(); i++) {
				// Adding a marker
				 marker = new MarkerOptions().position(
						new LatLng(googleLatLongList.get(i).getStoreLatitude(),
								googleLatLongList.get(i).getStoreLongitude()))
						.title(googleLatLongList.get(i).getmStoreName()).icon(BitmapDescriptorFactory
								.fromResource(R.drawable.icon_bag));;

								 Marker m=googleMap.addMarker(marker);
								  markerList.add(m);
			}
		}
		
      googleMap.setOnInfoWindowClickListener(this);
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
		buyOpic = (BuyOpic) context.getApplication();
		showProgressDialog();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			mstoreInfos = new ArrayList<StoreInfo>();
			mstoreInfos = buyOpic.getStoreinfo();
			yellowLatLongList = new ArrayList<StoreInfo>();
			googleLatLongList = new ArrayList<StoreInfo>();
			for (int i = 0; i < mstoreInfos.size(); i++) {
				latlonglist = new ArrayList<Double[]>();
				if (mstoreInfos.get(i).getmGoogleStoreInfo() != null) {
					mgoogleSoreInfo = mstoreInfos.get(i).getmGoogleStoreInfo();
					double latitude = Double.parseDouble(mgoogleSoreInfo.get(0)
							.getmLatitude());
					double longitude = Double.parseDouble(mgoogleSoreInfo
							.get(0).getmLongitude());
					String mGoogleStoreName = mgoogleSoreInfo.get(0).getmName();
					googleLatLongList.add(new StoreInfo(latitude, longitude,mGoogleStoreName));
				} else if (mstoreInfos.get(i).getmGoogleStoreInfo() == null) {
					double latitude = mstoreInfos.get(i).getStoreLatitude();
					double longitude = mstoreInfos.get(i).getStoreLongitude();
					yellowLatLongList.add(new StoreInfo(latitude, longitude));
				}
			}
			mLocationClient = new LocationClient(context, this, null);
			mLocationClient.connect();

		} catch (Exception e) {
			e.printStackTrace();
		}
		dismissProgressDialog();
	}

	@Override
	public void onResume() {
		super.onResume();
		initilizeMap();
	}

	/**
	 * function to load map If map is not created it will create it for you
	 * */
	private void initilizeMap() {
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();
			Log.i("MAP", "initilizeMap googleMap" + googleMap);
			googleMap.clear();
			// Changing map type
			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

			// Showing / hiding your current location
			googleMap.setMyLocationEnabled(true);

			// Enable / Disable zooming controls
			googleMap.getUiSettings().setZoomControlsEnabled(true);

			// Enable / Disable my location button
			googleMap.getUiSettings().setMyLocationButtonEnabled(true);

			// Enable / Disable Compass icon
			googleMap.getUiSettings().setCompassEnabled(true);

			// Enable / Disable Rotate gesture
			googleMap.getUiSettings().setRotateGesturesEnabled(true);

			// Enable / Disable zooming functionality
			googleMap.getUiSettings().setZoomGesturesEnabled(true);
			mLocation = googleMap.getMyLocation();

			if (buyOpic != null && buyOpic.getmSourceLatitude() != 0
					&& buyOpic.getmSourceLongitude() != 0) {

				currentLatitude = buyOpic.getmSourceLatitude();
				currentLongitude = buyOpic.getmSourceLongitude();

			}

			else if (mLocation != null) {
				currentLatitude = mLocation.getLatitude();
				currentLongitude = mLocation.getLongitude();

			}
			if (currentLatitude != 0 && currentLongitude != 0) {
				CameraPosition cameraPosition = new CameraPosition.Builder()
						.target(new LatLng(currentLatitude, currentLongitude))
						.zoom(15).build();
				if (googleMap != null) {
					googleMap.animateCamera(CameraUpdateFactory
							.newCameraPosition(cameraPosition));
				}
			}
			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getActivity().getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}
		}
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

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		Location loc = mLocationClient.getLastLocation();
		if (buyOpic.getmSourceLatitude() != 0
				&& buyOpic.getmSourceLongitude() != 0) {
			currentLatitude = buyOpic.getmSourceLatitude();
			currentLongitude = buyOpic.getmSourceLongitude();

		} else if (loc != null) {
			currentLatitude = loc.getLatitude();
			currentLongitude = loc.getLongitude();
		} else {

			Utils.showDialog(context,
					getResources().getString(R.string.gps_error_message));

		}
		if (currentLatitude != 0 && currentLongitude != 0) {
			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(new LatLng(currentLatitude, currentLongitude))
					.zoom(15).build();
			if (googleMap != null) {
				googleMap.animateCamera(CameraUpdateFactory
						.newCameraPosition(cameraPosition));
			}
		}

	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void gotLocation(Location location) {
		if (location != null) {
		} else {
			Utils.showDialog(context,
					getResources().getString(R.string.gps_error_message));
		}
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		// TODO Auto-generated method stub
		 FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		OffersListFragment fragment = new OffersListFragment();
		Bundle bundle = new Bundle();
		String markerID=marker.getId();
		markerID=markerID.replace("m","");
		int id=Integer.parseInt(markerID);
		
		
		 int markerId = -1;

         for(int i=0; i<markerList.size(); i++) {
             markerId = i;
             Marker m = markerList.get(i);
             if(m.getId().equals(marker.getId()))
                 break;
         }

       //  markerId -= 1; // Because first item of markerList is user's marker

         // Ignore if User's marker clicked
         if(markerId < 0)
             return;


		Log.i("MARKER", "size of yellow stores "+yellowLatLongList.size());
		if(markerId<=yellowLatLongList.size()-1){
		bundle.putString(OffersListFragment.KEY_STORE_ID,mstoreInfos.get(markerId).getmStoreId());
		bundle.putString(OffersListFragment.KEY_RETAILER_ID,mstoreInfos.get(markerId).getmRetailerId());
		bundle.putString(OffersListFragment.KEY_STORE_NAME,mstoreInfos.get(markerId).getmStoreName());
		bundle.putString(OffersListFragment.KEY_PHONE_NUMBER,mstoreInfos.get(markerId).getmPhoneNumber());
		bundle.putString(OffersListFragment.KEY_DISTANCE,mstoreInfos.get(0).getDistanceValue());
		bundle.putString(OffersListFragment.KEY_POSTED_CONSUMER_ALERT_ID,mstoreInfos.get(markerId).getmPostedAlertConsumerId());
		bundle.putBoolean(OffersListFragment.KEY_IS_IN_BEACON_RANGE,mstoreInfos.get(markerId).isInBeaconRange());

		fragment.setArguments(bundle);
		fragmentTransaction.replace(R.id.container, fragment, null)
				.addToBackStack(HomePageSetupActivity.STORE_INFO).commit();
		return;
		}
		else{
			return;
		}
	}

	
}
