package com.buyopic.android.radius;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;

import com.buyopic.android.fragments.CloseByListFragment;
import com.buyopic.android.fragments.OffersListFragment;
import com.buyopic.android.models.StoreInfo;
import com.buyopic.android.network.BuyopicNetworkCallBack;
import com.buyopic.android.network.BuyopicNetworkServiceManager;
import com.buyopic.android.network.JsonResponseParser;
import com.buyopic.android.utils.Constants;
import com.buyopic.android.utils.FetchCurrentLocation;
import com.buyopic.android.utils.FetchCurrentLocation.LocationResult;
import com.buyopic.android.utils.Utils;

public class TrackDistanceBroadCast extends BroadcastReceiver implements
		LocationResult, BuyopicNetworkCallBack {

	private Context mContext;
	private BuyOpic buyOpic;
	private BuyopicNetworkServiceManager buyopicNetworkServiceManager;

	/*
	 * Default distance to Update the List in Meters
	 */
	private static final float DEFAULT_DISTANCE_TO_UPDATE_LIST = 30;

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		mContext = arg0;
		buyOpic = (BuyOpic) mContext.getApplicationContext();
		FetchCurrentLocation currentLocation = new FetchCurrentLocation();
		currentLocation.fetchLocation(mContext, this);
		buyopicNetworkServiceManager = BuyopicNetworkServiceManager
				.getInstance(mContext);
	}

	@Override
	public void gotLocation(Location location) {
		if(location!=null)
		{
		HashMap<String, Double> hashMap = buyOpic.getSourceLocation();
		if (hashMap != null && !hashMap.isEmpty() && hashMap.size() == 2) {
			double sourceLat = hashMap.get("lat");
			double sourceLong = hashMap.get("long");

			if (location != null) {
				double destinationLat = location.getLatitude();
				double destinationLong = location.getLongitude();
				
				double distance=Utils.calculateDistance(sourceLat, sourceLong, destinationLat, destinationLong, 'M');
//				Location.distanceBetween(sourceLat, sourceLong, destinationLat,
//						destinationLong, results);
				if (distance> 0) {
					if (distance >= DEFAULT_DISTANCE_TO_UPDATE_LIST) {
						buyOpic.setSourceLocation(location);
						if(buyOpic.getmSourceLatitude()!=0&&buyOpic.getmSourceLongitude()!=0){
							destinationLat=buyOpic.getmSourceLatitude();
							destinationLong=buyOpic.getmSourceLongitude();
						}
						else{
						buyOpic.setmSourceLatitude(destinationLat);
						buyOpic.setmSourceLongitude(destinationLong);
						}
//						Utils.showToast(mContext, "Travelled 30Meters Distance");
						
						Log.i("Loc changes", "trackdistance"+"lat "+String.valueOf(buyOpic.getmSourceLatitude())+" long "+String.valueOf(buyOpic.getmSourceLongitude()));
						if (buyOpic.isUserInHomeListFragment()) {
							buyopicNetworkServiceManager
									.sendNearestStoreAlertsRequest(
											Constants.REQUEST_NEAREST_STORE_ALERTS,
											"", "", "",
											String.valueOf(destinationLat),
											//String.valueOf(buyOpic.getmSourceLatitude()),
											String.valueOf(destinationLong),
											//String.valueOf(buyOpic.getmSourceLongitude()),
											buyOpic.getmConsumerId(), "NO",null,false,
											this,mContext);
						}
						notifyDistanceUpdate(destinationLat, destinationLong);
					}
				}
			}

		}
		}
	}

	private void notifyDistanceUpdate(double destinationLat,
			double destinationLong) {
//		Utils.showToast(mContext, "Distance Update");
		double sourceLatitude = buyOpic.getmSelectedStoreLat();
		double sourceLongitude = buyOpic.getmSelectedStoreLong();
		if (sourceLatitude != 0.0 && sourceLongitude != 0.0
				&& destinationLat != 0.0 && destinationLong != 0.0) {
			double distanceInMeters=Utils.calculateDistance(sourceLatitude, sourceLongitude, destinationLat, destinationLong, 'M');
			String distanceInMilesorFeets=Utils.convertMetrsAndFeets(distanceInMeters,mContext);
			if(!TextUtils.isEmpty(distanceInMilesorFeets))
			{
				Intent intent = new Intent(
						OffersListFragment.ACTION__DISTANCE_CHANGED);
				intent.putExtra(OffersListFragment.KEY_EXTRA_DISTANCE,
						distanceInMilesorFeets);
				mContext.sendBroadcast(intent);
			}
		}
	}

	@Override
	public void onSuccess(int requestCode, Object object) {
		if (requestCode == Constants.REQUEST_NEAREST_STORE_ALERTS) {
			List<StoreInfo> storeInfos = JsonResponseParser.parseStoreInfos(
					mContext, (String) object);
			updateNewDataToUI(storeInfos);
		}

	}

	private void updateNewDataToUI(List<StoreInfo> storeInfos) {
		if (storeInfos != null && !storeInfos.isEmpty()) {
			if (buyOpic.isUserInHomeListFragment()) {
				Intent intent = new Intent(
						CloseByListFragment.ACTION__BEACON_DATA_CHANGED_INTENT);
				intent.putParcelableArrayListExtra("updatelist",
						(ArrayList<StoreInfo>) storeInfos);
				mContext.sendBroadcast(intent);
			} else {

			}
		}
	}

	@Override
	public void onFailure(int requestCode, String message) {
		// TODO Auto-generated method stub

	}

	/*
	 * @SuppressLint("UseValueOf") public static float distFrom(float
	 * sourceLatitude, float sourceLongitude, float destinationLatitude, float
	 * distanceLongitude) { double earthRadius = 3958.75; double dLat =
	 * Math.toRadians(destinationLatitude - sourceLatitude); double dLng =
	 * Math.toRadians(distanceLongitude - sourceLongitude); double a =
	 * Math.sin(dLat / 2) * Math.sin(dLat / 2) +
	 * Math.cos(Math.toRadians(sourceLatitude))
	 * Math.cos(Math.toRadians(destinationLatitude)) Math.sin(dLng / 2) *
	 * Math.sin(dLng / 2); double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 -
	 * a)); double dist = earthRadius * c; int meterConversion = 1609; return
	 * new Float(dist * meterConversion).floatValue(); }
	 * 
	 * private double calculateDistance(double lat1, double lon1, double lat2,
	 * double lon2, String unit) { double theta = lon1 - lon2; double dist =
	 * Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) +
	 * Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
	 * Math.cos(deg2rad(theta)); dist = Math.acos(dist); dist = rad2deg(dist);
	 * dist = dist * 60 * 1.1515; if (unit == "K") { dist = dist * 1.609344; }
	 * else if (unit == "M") { dist = dist * 0.8684; } return (dist); }
	 * 
	 * private double deg2rad(double deg) { return (deg * Math.PI / 180.0); }
	 * 
	 * private double rad2deg(double rad) { return (rad * 180.0 / Math.PI); }
	 * 
	 * public String getDistance(double lat1, double lon1, double lat2, double
	 * lon2) { String result_in_kms = ""; String url =
	 * "http://maps.google.com/maps/api/directions/xml?origin=" + lat1 + "," +
	 * lon1 + "&destination=" + lat2 + "," + lon2 +
	 * "&sensor=false&units=metric"; String tag[] = { "text" }; HttpResponse
	 * response = null; try { HttpClient httpClient = new DefaultHttpClient();
	 * HttpContext localContext = new BasicHttpContext(); HttpPost httpPost =
	 * new HttpPost(url); response = httpClient.execute(httpPost, localContext);
	 * InputStream is = response.getEntity().getContent();
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } return result_in_kms; }
	 * 
	 * @Override public void onSuccess(int requestCode, Object object) { // TODO
	 * Auto-generated method stub
	 * 
	 * if (requestCode == Constants.REQUEST_DISTANCE_TRACK_REQUEST) {
	 * 
	 * parseData((String)object); String tag[] = { "text" }; DocumentBuilder
	 * builder; try { builder = DocumentBuilderFactory.newInstance()
	 * .newDocumentBuilder();
	 * 
	 * Document doc = builder.parse((String) object); if (doc != null) {
	 * NodeList nl; ArrayList args = new ArrayList(); for (String s : tag) { nl
	 * = doc.getElementsByTagName(s); if (nl.getLength() > 0) { Node node =
	 * nl.item(nl.getLength() - 1); args.add(node.getTextContent()); } else {
	 * args.add(" - "); } } String result_in_kms = String.format("%s",
	 * args.get(0)); Utils.showLog("Distance Travelled-->" + result_in_kms);
	 * Utils.showToast(mContext, "Distance Travelled-->" + result_in_kms); } }
	 * catch (ParserConfigurationException e) { // TODO Auto-generated catch
	 * block e.printStackTrace(); } catch (SAXException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } catch (IOException e) {
	 * // TODO Auto-generated catch block e.printStackTrace(); } } }
	 * 
	 * @Override public void onFailure(int requestCode, String message) { //
	 * TODO Auto-generated method stub
	 * 
	 * }
	 * 
	 * private void parseData(String response) { // Sortout JSONresponse
	 * JSONObject object; try { object = (JSONObject) new
	 * JSONTokener(response).nextValue();
	 * 
	 * JSONArray array = object.getJSONArray("routes"); //
	 * Log.d("JSON","array: "+array.toString());
	 * 
	 * // Routes is a combination of objects and arrays JSONObject routes =
	 * array.getJSONObject(0); // Log.d("JSON","routes: "+routes.toString());
	 * 
	 * String summary = routes.getString("summary"); //
	 * Log.d("JSON","summary: "+summary);
	 * 
	 * JSONArray legs = routes.getJSONArray("legs"); //
	 * Log.d("JSON","legs: "+legs.toString());
	 * 
	 * JSONObject steps = legs.getJSONObject(0); //
	 * Log.d("JSON","steps: "+steps.toString());
	 * 
	 * JSONObject distance = steps.getJSONObject("distance"); //
	 * Log.d("JSON","distance: "+distance.toString());
	 * 
	 * String sDistance = distance.getString("text"); int iDistance =
	 * distance.getInt("value");
	 * Utils.showLog("Distance Travelled-->"+iDistance);
	 * Utils.showToast(mContext, "Distance Travelled-->"+iDistance);
	 * 
	 * } catch (JSONException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } }
	 */
}
