package com.buyopic.android.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Alert implements Parcelable {

	private String mOfferMessage;
	private String mPrice;
	private String mThumbnailUrl;
	private String mOfferTitle;
	private String mStartDate;
	private String mEndDate;
	private String mOfferId;
	private boolean mIsSpecialOffer;
	private boolean isInBeaconRange;
	private boolean mIsAlertDiscontinued;
	private String mDistanceValue;
	private boolean mIsActivated;
	private boolean isFavorite;
	private String mPostedAlertId;
	private String mPostedConsumerId;
	private String mStoreId;
	private String mRetailerId;
	private String mPhoneNumber;
	private String mStoreName;

	public String getmStoreName() {
		return mStoreName;
	}

	public void setmStoreName(String mStoreName) {
		this.mStoreName = mStoreName;
	}

	public String getmPhoneNumber() {
		return mPhoneNumber;
	}

	public void setmPhoneNumber(String mPhoneNumber) {
		this.mPhoneNumber = mPhoneNumber;
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

	public String getmPostedConsumerId() {
		return mPostedConsumerId;
	}

	public void setmPostedConsumerId(String mPostedConsumerId) {
		this.mPostedConsumerId = mPostedConsumerId;
	}

	public String getmPostedAlertId() {
		return mPostedAlertId;
	}

	public void setmPostedAlertId(String mPostedAlertId) {
		this.mPostedAlertId = mPostedAlertId;
	}

	public boolean isFavorite() {
		return isFavorite;
	}

	public void setFavorite(boolean isFavorite) {
		this.isFavorite = isFavorite;
	}

	public boolean ismIsActivated() {
		return mIsActivated;
	}

	public void setmIsActivated(boolean mIsActivated) {
		this.mIsActivated = mIsActivated;
	}

	public String getmDistanceValue() {
		return mDistanceValue;
	}

	public void setmDistanceValue(String mDistanceValue) {
		this.mDistanceValue = mDistanceValue;
	}

	public boolean ismIsAlertDiscontinued() {
		return mIsAlertDiscontinued;
	}

	public void setmIsAlertDiscontinued(boolean mIsAlertDiscontinued) {
		this.mIsAlertDiscontinued = mIsAlertDiscontinued;
	}

	public boolean isInBeaconRange() {
		return isInBeaconRange;
	}

	public void setInBeaconRange(boolean isInBeaconRange) {
		this.isInBeaconRange = isInBeaconRange;
	}

	public boolean ismIsSpecialOffer() {
		return mIsSpecialOffer;
	}

	public void setmIsSpecialOffer(boolean mIsSpecialOffer) {
		this.mIsSpecialOffer = mIsSpecialOffer;
	}

	public String getmOfferTitle() {
		return mOfferTitle;
	}

	public void setmOfferTitle(String mOfferTitle) {
		this.mOfferTitle = mOfferTitle;
	}

	public String getmStartDate() {
		return mStartDate;
	}

	public void setmStartDate(String mStartDate) {
		this.mStartDate = mStartDate;
	}

	public String getmEndDate() {
		return mEndDate;
	}

	public void setmEndDate(String mEndDate) {
		this.mEndDate = mEndDate;
	}

	public String getmOfferId() {
		return mOfferId;
	}

	public void setmOfferId(String mOfferId) {
		this.mOfferId = mOfferId;
	}

	public String getmThumbnailUrl() {
		return mThumbnailUrl;
	}

	public void setmThumbnailUrl(String mThumbnailUrl) {
		this.mThumbnailUrl = mThumbnailUrl;
	}

	public String getmOfferMessage() {
		return mOfferMessage;
	}

	public void setmOfferMessage(String mOfferMessage) {
		this.mOfferMessage = mOfferMessage;
	}

	public String getmPrice() {
		return mPrice;
	}

	public void setmPrice(String mPrice) {
		this.mPrice = mPrice;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mDistanceValue);
		dest.writeString(this.mEndDate);
		dest.writeString(this.mOfferId);
		dest.writeString(this.mOfferMessage);
		dest.writeString(this.mOfferTitle);
		dest.writeString(this.mPhoneNumber);
		dest.writeString(this.mPostedAlertId);
		dest.writeString(this.mPostedConsumerId);
		dest.writeString(this.mPrice);
		dest.writeString(this.mRetailerId);
		dest.writeString(this.mStartDate);
		dest.writeString(this.mStoreId);
		dest.writeString(this.mStoreName);
		dest.writeString(this.mThumbnailUrl);
		dest.writeString(String.valueOf(this.isFavorite));
		dest.writeString(String.valueOf(this.isInBeaconRange));
		dest.writeString(String.valueOf(this.mIsActivated));
		dest.writeString(String.valueOf(this.mIsAlertDiscontinued));
		dest.writeString(String.valueOf(this.mIsSpecialOffer));
	}

	public Alert() {
	}

	public Alert(Parcel parcel) {
		mDistanceValue=parcel.readString();
		mEndDate=parcel.readString();
		mOfferId=parcel.readString();
		mOfferMessage=parcel.readString();
		mOfferTitle=parcel.readString();
		mPhoneNumber=parcel.readString();
		mPostedAlertId=parcel.readString();
		mPostedConsumerId=parcel.readString();
		mPrice=parcel.readString();
		mRetailerId=parcel.readString();
		mStartDate=parcel.readString();
		mStoreId=parcel.readString();
		mStoreName=parcel.readString();
		mThumbnailUrl=parcel.readString();
		isFavorite=parcel.readString().equalsIgnoreCase("true")?true:false;
		isInBeaconRange=parcel.readString().equalsIgnoreCase("true")?true:false;
		mIsActivated=parcel.readString().equalsIgnoreCase("true")?true:false;
		mIsAlertDiscontinued=parcel.readString().equalsIgnoreCase("true")?true:false;
		mIsSpecialOffer=parcel.readString().equalsIgnoreCase("true")?true:false;
	}

	public static final Creator<Alert> CREATOR=new Creator<Alert>() {
		
		@Override
		public Alert[] newArray(int size) {
			return new Alert[size];
		}
		
		@Override
		public Alert createFromParcel(Parcel source) {
			return new Alert(source);
		}
	};
}
