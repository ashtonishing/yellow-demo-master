package com.buyopic.android.radius;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;

import com.buyopic.android.models.StoreInfo;
import com.buyopic.android.utils.Utils;
import com.crashlytics.android.Crashlytics;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.radiusnetworks.ibeacon.IBeacon;

@SuppressLint("UseSparseArrays")
public class BuyOpic extends Application {

	private SharedPreferences preferences;
	private Intent intent = null;
	private final String KEY_CONSUMER_ID = "consumer_id";
	private final String KEY_CONSUMER_EMAIL = "consumer_email";
	private final String KEY_CONSUMER_REGISTRATION = "consumer_registration";
	private final String KEY_CONSUMER_USER_NAME= "consumer_user_name";
	private final String KEY_CONSUMER_USER_ADDRESS= "consumer_user_address";
	private final String KEY_CONSUMER_USER_ADDRESS1= "user_address1";
	private final String KEY_CONSUMER_PROFILE_IMAGE= "consumer_profile_pic";
	private final String KEY_CONSUMER_USER_ADDRESS_IMAGEURL= "consumer_user_address_image_url";
	private final String KEY_CONSUMER_GOOGLE_ICON_IMAGE= "consumer_google_icon_image";
	private final String KEY_CONSUMER_GOOGLE_PLACE_ID= "consumer_google_place_id";
	private final String KEY_CONSUMER_USER_ADDRESS2= "consumer_user_address2";
	private final String KEY_CONSUMER_USER_POSTALCODE= "consumer_user_postal_code";
	private final String KEY_CONSUMER_REGISTERED_LAT= "consumer_registered_lat";
	private final String KEY_CONSUMER_REGISTERED_LONG= "consumer_registered_long";
	private String mBaseUrl;
	private boolean isUserInHomeListFragment = false;
	private String mStoreName;
	private String mStoreAddress;
	private String mStoreLogo;
	private IBeacon currentBeacon;
	private boolean isBeaconAvailable;
	private double mSourceLatitude;
	private double mSourceLongitude;
	private HashMap<String, Double> hashMap;
	private List<StoreInfo> mStoreInfo;
	private double mSelectedStoreLat;
	private double mSelectedStoreLong;
	private int duration;
	private HashMap<Integer,Boolean> expandedGroupPos;
	private long closeByLastSelectionPage=-1;
	private int favoriteLastSelectionPage=-1;
	private int sharedLastSelectionPage=-1;
	private int nearestBeaconDistance;
	

	public int getNearestBeaconDistance() {
		return nearestBeaconDistance;
	}

	public void setNearestBeaconDistance(int nearestBeaconDistance) {
		this.nearestBeaconDistance = nearestBeaconDistance;
	}

	public int getFavoriteLastSelectionPage() {
		return favoriteLastSelectionPage;
	}

	public void setFavoriteLastSelectionPage(int favoriteLastSelectionPage) {
		this.favoriteLastSelectionPage = favoriteLastSelectionPage;
	}

	public int getSharedLastSelectionPage() {
		return sharedLastSelectionPage;
	}

	public void setSharedLastSelectionPage(int sharedLastSelectionPage) {
		this.sharedLastSelectionPage = sharedLastSelectionPage;
	}

	public long getCloseByLastSelectionPage() {
		return closeByLastSelectionPage;
	}

	public void setCloseByLastSelectionPage(long closeByLastSelectionPage) {
		this.closeByLastSelectionPage = closeByLastSelectionPage;
	}

	public HashMap<Integer, Boolean> getExpandedGroupPos() {
		if(expandedGroupPos==null)
		{
			expandedGroupPos=new HashMap<Integer, Boolean>();
		}
		return expandedGroupPos;
	}

	public void setExpandedGroupPos(HashMap<Integer, Boolean> expandedGroupPos) {
		this.expandedGroupPos = expandedGroupPos;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public double getmSelectedStoreLat() {
		return mSelectedStoreLat;
	}

	public void setmSelectedStoreLat(double mSelectedStoreLat) {
		this.mSelectedStoreLat = mSelectedStoreLat;
	}

	public double getmSelectedStoreLong() {
		return mSelectedStoreLong;
	}

	public void setmSelectedStoreLong(double mSelectedStoreLong) {
		this.mSelectedStoreLong = mSelectedStoreLong;
	}

	public double getmSourceLatitude() {
		return mSourceLatitude;
	}

	public void setmSourceLatitude(double mSourceLatitude) {
		this.mSourceLatitude = mSourceLatitude;
	}

	public double getmSourceLongitude() {
		return mSourceLongitude;
	}

	public void setmSourceLongitude(double mSourceLongitude) {
		this.mSourceLongitude = mSourceLongitude;
	}

	public IBeacon getCurrentBeacon() {
		return currentBeacon;
	}

	public void setCurrentBeacon(IBeacon currentBeacon) {
		this.currentBeacon = currentBeacon;
	}

	public boolean isBeaconAvailable() {
		return isBeaconAvailable;
	}

	public HashMap<String, Double> getSourceLocation() {
		return hashMap;
	}

	public void setSourceLocation(Location location) {
		hashMap = new HashMap<String, Double>();
		hashMap.put("lat", location.getLatitude());
		hashMap.put("long", location.getLongitude());

	}

	public void setBeaconAvailable(boolean isBeaconAvailable) {
		Utils.showLog("Beacon Available in App" + isBeaconAvailable);
		this.isBeaconAvailable = isBeaconAvailable;
	}

	public String getmStoreName() {
		return mStoreName;
	}

	public void setmStoreName(String mStoreName) {
		this.mStoreName = mStoreName;
	}

	public String getmStoreAddress() {
		return mStoreAddress;
	}

	public void setmStoreAddress(String mStoreAddress) {
		this.mStoreAddress = mStoreAddress;
	}

	public String getmStoreLogo() {
		return mStoreLogo;
	}

	public void setmStoreLogo(String mStoreLogo) {
		this.mStoreLogo = mStoreLogo;
	}

	public boolean isUserInHomeListFragment() {
		return isUserInHomeListFragment;
	}

	public void setUserInHomeListFragment(boolean isUserInHomeListFragment) {
		this.isUserInHomeListFragment = isUserInHomeListFragment;
	}

	public String getmConsumerEmail() {
		return preferences.getString(KEY_CONSUMER_EMAIL, null);
	}

	public void setmConsumerEmail(String mConsumerId) {
		preferences.edit().putString(KEY_CONSUMER_EMAIL, mConsumerId).commit();
	}

	public String getmConsumerId() {
		return preferences.getString(KEY_CONSUMER_ID, null);
	}

	public void setmConsumerId(String mConsumerId) {
		preferences.edit().putString(KEY_CONSUMER_ID, mConsumerId).commit();
	}

	public void setmConsumerRegistrationStatus(boolean status) {
		preferences.edit().putBoolean(KEY_CONSUMER_REGISTRATION, status)
				.commit();
	}

	public boolean getmConsumerRegistrationStatus() {
		return preferences.getBoolean(KEY_CONSUMER_REGISTRATION, false);
	}
	
	public String getmConsumerName() {
		return preferences.getString(KEY_CONSUMER_USER_NAME, "Anonymous");
	}

	public void setmConsumerName(String mConsumerName) {
		preferences.edit().putString(KEY_CONSUMER_USER_NAME, mConsumerName).commit();
	}
	public String getmConsumerAddress() {
		return preferences.getString(KEY_CONSUMER_USER_ADDRESS, null);
	}

	public void setmConsumerAddress(String mConsumerAddress) {
		preferences.edit().putString(KEY_CONSUMER_USER_ADDRESS,mConsumerAddress).commit();
	}
	
	public String getmConsumerAddress1() {
		return preferences.getString(KEY_CONSUMER_USER_ADDRESS1, null);
	}

	public void setmConsumerAddress1(String mConsumerAddress1) {
		preferences.edit().putString(KEY_CONSUMER_USER_ADDRESS1,mConsumerAddress1).commit();
	}

	
	public String getmConsumerProfilePic() {
		return preferences.getString(KEY_CONSUMER_PROFILE_IMAGE, null);
	}

	public void setmConsumerProfilePic(String mConsumerName) {
		preferences.edit().putString(KEY_CONSUMER_PROFILE_IMAGE, mConsumerName).commit();
	}
	public String getmConsumerUserAddressImageUrl() {
		return preferences.getString(KEY_CONSUMER_USER_ADDRESS_IMAGEURL, null);
	}

	public void setmConsumerUserAddressImageUrl(String mConsumerAddressimageurl) {
		preferences.edit().putString(KEY_CONSUMER_USER_ADDRESS_IMAGEURL,mConsumerAddressimageurl).commit();
	}
	public String getmConsumerGoogleIconImage() {
		return preferences.getString(KEY_CONSUMER_GOOGLE_ICON_IMAGE, null);
	}

	public void setmConsumerGoogleIconImage(String mConsumerName) {
		preferences.edit().putString(KEY_CONSUMER_GOOGLE_ICON_IMAGE, mConsumerName).commit();
	}
	public String getmConsumerGooglePlaceID() {
		return preferences.getString(KEY_CONSUMER_GOOGLE_PLACE_ID, null);
	}

	public void setmConsumerGooglePlaceID(String mConsumerName) {
		preferences.edit().putString(KEY_CONSUMER_GOOGLE_PLACE_ID, mConsumerName).commit();
	}
	public String getmConsumerPostalCode() {
		return preferences.getString(KEY_CONSUMER_USER_POSTALCODE, null);
	}

	public void setmConsumerPostalCode(String mConsumerName) {
		preferences.edit().putString(KEY_CONSUMER_USER_POSTALCODE, mConsumerName).commit();
	}
	public String getmConsumerLatitude() {
		return preferences.getString(KEY_CONSUMER_REGISTERED_LAT, null);
	}

	public void setmConsumerLatitude(String mLatitude) {
		preferences.edit().putString(KEY_CONSUMER_REGISTERED_LAT, mLatitude).commit();
	}
	
	public String getmConsumerLongitude() {
		return preferences.getString(KEY_CONSUMER_REGISTERED_LONG, null);
	}

	public void setmConsumerLongitude(String mLongitude) {
		preferences.edit().putString(KEY_CONSUMER_REGISTERED_LONG, mLongitude).commit();
	}
	
	
	public String getmConsumerAddress2() {
		return preferences.getString(KEY_CONSUMER_USER_ADDRESS2, null);
	}

	public void setmConsumerAddress2(String mConsumerName) {
		preferences.edit().putString(KEY_CONSUMER_USER_ADDRESS2, mConsumerName).commit();
	}
	
	public String getmConsumerPhoneNumber() {
		return preferences.getString("phone", null);
	}

	public void setmConsumerPhoneNumber(String phone) {
		preferences.edit().putString("phone", phone).commit();
	}
	
	public String getmConsumerPassword() {
		return preferences.getString("password", null);
	}

	public void setmConsumerPassword(String phone) {
		preferences.edit().putString("password", phone).commit();
	}
	
	public String getmConsumerStreet() {
		return preferences.getString("street", null);
	}

	public void setmConsumerStreet(String phone) {
		preferences.edit().putString("street", phone).commit();
	}
	
	public String getmConsumerCity() {
		return preferences.getString("city", null);
	}

	public void setmConsumerCity(String phone) {
		preferences.edit().putString("city", phone).commit();
	}
	
	public String getmConsumerState() {
		return preferences.getString("state", null);
	}

	public void setmConsumerState(String phone) {
		preferences.edit().putString("state", phone).commit();
	}
	public String getmConsumerCountry() {
		return preferences.getString("country", null);
	}

	public void setmConsumerCountry(String country) {
		preferences.edit().putString("country", country).commit();
	}
	public String getmBaseUrl() {
		return mBaseUrl;
	}

	public void setmBaseUrl(String mBaseUrl) {
		this.mBaseUrl = mBaseUrl;
	}

	
	
//	user_image_url


	@Override
	public void onCreate() {
		super.onCreate();
		Crashlytics.start(this);
		initImageLoader(this);
		preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.memoryCache(new LruMemoryCache(10 * 1024 * 1024))
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();

		ImageLoader.getInstance().init(config);
	}

	public void startBackGroundBeaconMonitorService() {
		if (intent == null) {
			intent = new Intent(this, BackgroundMonitorService.class);
		}
		startService(intent);
	}

	public void stopBackGroundBeaconMonitorService() {
		if (intent != null) {
			stopService(intent);
		}
	}

	public List<StoreInfo> getStoreinfo() {
		// TODO Auto-generated method stub
		return mStoreInfo;
	}

	public void setStoreinfo(List<StoreInfo> storeInfos) {
		// TODO Auto-generated method stub
		mStoreInfo =new ArrayList<StoreInfo>();
		mStoreInfo=storeInfos;
	}

}
