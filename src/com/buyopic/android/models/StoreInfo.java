package com.buyopic.android.models;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.buyopic.android.network.GoogleStoreInfo;

public class StoreInfo implements Parcelable {

	private String mStoreId;
	private String mStoreName;
	private String mRetailerId;
	private String mStoreAddress;
	private String mPrice;
	private String mTitle;
	private String mDescription;
	private String mStoreLogo;
	private String mProductLogo;
	private boolean isInBeaconRange;
	private String mBeaconUUID;
	private String mMinor;
	private String mMajor;
	private String mOfferId;
	private double distance;
	private String distanceValue;
	private double storeLatitude;
	private double storeLongitude;
	private List<Alert> storeAlerts;
	private boolean isFavorite;
	private String mSharedFromUserName;
	private String mPostedAlertConsumerId;
	private String mEndDate;
	private String mPhoneNumber;
	private List<ProvisionedBeaconInfo> provisionedBeacons;
	private String mStoreTier;
	List<GoogleStoreInfo> mGoogleStoreInfo;
	
	public StoreInfo(double storeLatitude, double storeLongitude) {
		this.storeLatitude = storeLatitude;
		this.storeLongitude = storeLongitude;
	}
	
	public List<GoogleStoreInfo> getmGoogleStoreInfo() {
		return mGoogleStoreInfo;
	}

	public void setmGoogleStoreInfo(List<GoogleStoreInfo> mGoogleStoreInfo) {
		this.mGoogleStoreInfo = mGoogleStoreInfo;
	}

	public String getmStoreTier() {
		return mStoreTier;
	}

	public void setmStoreTier(String mStoreTier) {
		this.mStoreTier = mStoreTier;
	}

	public List<ProvisionedBeaconInfo> getProvisionedBeacons() {
		return provisionedBeacons;
	}

	public void setProvisionedBeacons(List<ProvisionedBeaconInfo> provisionedBeacons) {
		this.provisionedBeacons = provisionedBeacons;
	}

	public String getmPhoneNumber() {
		return mPhoneNumber;
	}

	public void setmPhoneNumber(String mPhoneNumber) {
		this.mPhoneNumber = mPhoneNumber;
	}

	public String getmEndDate() {
		return mEndDate;
	}

	public void setmEndDate(String mEndDate) {
		this.mEndDate = mEndDate;
	}

	public String getmPostedAlertConsumerId() {
		return mPostedAlertConsumerId;
	}

	public void setmPostedAlertConsumerId(String mPostedAlertConsumerId) {
		this.mPostedAlertConsumerId = mPostedAlertConsumerId;
	}

	public String getmDescription() {
		return mDescription;
	}

	public void setmDescription(String mDescription) {
		this.mDescription = mDescription;
	}

	public String getmSharedFromUserName() {
		return mSharedFromUserName;
	}

	public void setmSharedFromUserName(String mSharedFromUserName) {
		this.mSharedFromUserName = mSharedFromUserName;
	}

	public boolean isFavorite() {
		return isFavorite;
	}

	public void setFavorite(boolean isFavorite) {
		this.isFavorite = isFavorite;
	}

	public List<Alert> getStoreAlerts() {
		return storeAlerts;
	}

	public void setStoreAlerts(List<Alert> storeAlerts) {
		this.storeAlerts = storeAlerts;
	}

	public double getStoreLatitude() {
		return storeLatitude;
	}

	public void setStoreLatitude(double storeLatitude) {
		this.storeLatitude = storeLatitude;
	}

	public double getStoreLongitude() {
		return storeLongitude;
	}

	public void setStoreLongitude(double storeLongitude) {
		this.storeLongitude = storeLongitude;
	}

	public String getDistanceValue() {
		return distanceValue;
	}

	public void setDistanceValue(String distanceValue) {
		this.distanceValue = distanceValue;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public String getmOfferId() {
		return mOfferId;
	}

	public void setmOfferId(String mOfferId) {
		this.mOfferId = mOfferId;
	}

	public StoreInfo(String storeName, String mStoreAddress, String mPrice,
			String mTitle, String mProductLogo, boolean isInBeaconRange,
			String distance) {
		super();
		this.mStoreName = storeName;
		this.mStoreAddress = mStoreAddress;
		this.mPrice = mPrice;
		this.mTitle = mTitle;
		this.mProductLogo = mProductLogo;
		this.isInBeaconRange = isInBeaconRange;
	}

	public String getmStoreAddress() {
		return mStoreAddress;
	}

	public void setmStoreAddress(String mStoreAddress) {
		this.mStoreAddress = mStoreAddress;
	}

	public String getmBeaconUUID() {
		return mBeaconUUID;
	}

	public void setmBeaconUUID(String mBeaconUUID) {
		this.mBeaconUUID = mBeaconUUID;
	}

	public String getmMinor() {
		return mMinor;
	}

	public void setmMinor(String mMinor) {
		this.mMinor = mMinor;
	}

	public String getmMajor() {
		return mMajor;
	}

	public void setmMajor(String mMajor) {
		this.mMajor = mMajor;
	}

	public StoreInfo(String mStoreId, String mRetailerId, String mPrice,
			String mTitle, String mStoreLogo, String mProductLogo,
			String mStoreName) {
		super();
		this.mStoreId = mStoreId;
		this.mRetailerId = mRetailerId;
		this.mPrice = mPrice;
		this.mTitle = mTitle;
		this.mStoreLogo = mStoreLogo;
		this.mProductLogo = mProductLogo;
		this.mStoreName = mStoreName;
	}

	public boolean isInBeaconRange() {
		return isInBeaconRange;
	}

	public void setInBeaconRange(boolean isInBeaconRange) {
		this.isInBeaconRange = isInBeaconRange;
	}

	public StoreInfo() {
		// TODO Auto-generated constructor stub
	}

	public String getmStoreName() {
		return mStoreName;
	}

	public void setmStoreName(String mStoreName) {
		this.mStoreName = mStoreName;
	}

	public String getmStoreId() {
		return mStoreId;
	}

	public void setmStoreId(String mStoreId) {
		this.mStoreId = mStoreId;
	}

	public String getmRetailerId() {
		return mRetailerId;
	}

	public void setmRetailerId(String mRetailerId) {
		this.mRetailerId = mRetailerId;
	}

	public String getmPrice() {
		return mPrice;
	}

	public void setmPrice(String mPrice) {
		this.mPrice = mPrice;
	}

	public String getmTitle() {
		return mTitle;
	}

	public void setmTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	public String getmStoreLogo() {
		return mStoreLogo;
	}

	public void setmStoreLogo(String mStoreLogo) {
		this.mStoreLogo = mStoreLogo;
	}

	public String getmProductLogo() {
		return mProductLogo;
	}

	public void setmProductLogo(String mProductLogo) {
		this.mProductLogo = mProductLogo;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.mPrice);
		dest.writeString(this.mProductLogo);
		dest.writeString(this.mRetailerId);
		dest.writeString(this.mStoreId);
		dest.writeString(this.mStoreLogo);
		dest.writeString(this.mStoreName);
		dest.writeString(this.mTitle);
		dest.writeString(this.mBeaconUUID);
		dest.writeString(this.mMajor);
		dest.writeString(this.mMinor);
		dest.writeString(String.valueOf(isInBeaconRange));
		dest.writeString(this.distanceValue);
		dest.writeString(String.valueOf(isFavorite));
		dest.writeString(String.valueOf(mPostedAlertConsumerId));
		dest.writeString(String.valueOf(mEndDate));
		dest.writeString(String.valueOf(mPhoneNumber));
		dest.writeList(this.provisionedBeacons);
		dest.writeList(this.mGoogleStoreInfo);
	}

	public StoreInfo(Parcel storeInfoParcel) {
		this.mPrice = storeInfoParcel.readString();
		this.mProductLogo = storeInfoParcel.readString();
		this.mRetailerId = storeInfoParcel.readString();
		this.mStoreId = storeInfoParcel.readString();
		this.mStoreLogo = storeInfoParcel.readString();
		this.mStoreName = storeInfoParcel.readString();
		this.mTitle = storeInfoParcel.readString();
		this.mBeaconUUID = storeInfoParcel.readString();
		this.mMajor = storeInfoParcel.readString();
		this.mMinor = storeInfoParcel.readString();
		this.isInBeaconRange = storeInfoParcel.readString().equalsIgnoreCase("true") ? true
				: false;
		this.distanceValue=storeInfoParcel.readString();
		this.isFavorite = storeInfoParcel.readString().equalsIgnoreCase("true") ? true
				: false;
		this.mPostedAlertConsumerId=storeInfoParcel.readString();
		this.mEndDate=storeInfoParcel.readString();
		this.mPhoneNumber=storeInfoParcel.readString();
		this.provisionedBeacons=new ArrayList<ProvisionedBeaconInfo>();
		storeInfoParcel.readList(provisionedBeacons,this.getClass().getClassLoader());
		this.mGoogleStoreInfo=new ArrayList<GoogleStoreInfo>();
		storeInfoParcel.readList(mGoogleStoreInfo,this.getClass().getClassLoader());
	}

	public StoreInfo(double latitude, double longitude, String mGoogleStoreName) {
		// TODO Auto-generated constructor stub

		this.storeLatitude = latitude;
		this.storeLongitude = longitude;
		this.mStoreName=mGoogleStoreName;
	
	}

	public static final Creator<StoreInfo> CREATOR = new Creator<StoreInfo>() {

		@Override
		public StoreInfo[] newArray(int size) {
			return new StoreInfo[size];
		}

		@Override
		public StoreInfo createFromParcel(Parcel source) {
			return new StoreInfo(source);
		}
	};

	

}
