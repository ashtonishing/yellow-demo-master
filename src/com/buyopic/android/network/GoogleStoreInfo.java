package com.buyopic.android.network;

import android.os.Parcel;
import android.os.Parcelable;

public class GoogleStoreInfo implements Parcelable {
	
	private double mDistance;
	private String mVicinity;
	private String mPlaceId;
	private String mName;
	private String mLongitude;
	private String mLatitude;
	private String mIconURL;
	public double getmDistance() {
		return mDistance;
	}
	public void setmDistance(double mDistance) {
		this.mDistance = mDistance;
	}
	public String getmVicinity() {
		String rows[] = null;
		if(mVicinity!=null){
		 rows=mVicinity.split(",");
		}
		if(rows!=null && rows.length>0)
		return rows[rows.length-1];
		else
			return mVicinity;
	}
	public void setmVicinity(String mVicinity) {
		this.mVicinity = mVicinity;
	}
	public String getmPlaceId() {
		return mPlaceId;
	}
	public void setmPlaceId(String mPlaceId) {
		this.mPlaceId = mPlaceId;
	}
	public String getmName() {
		return mName;
	}
	public void setmName(String mName) {
		this.mName = mName;
	}
	public String getmLongitude() {
		return mLongitude;
	}
	public void setmLongitude(String mLongitude) {
		this.mLongitude = mLongitude;
	}
	public String getmLatitude() {
		return mLatitude;
	}
	public void setmLatitude(String mLatitude) {
		this.mLatitude = mLatitude;
	}
	public String getmIconURL() {
		return mIconURL;
	}
	public void setmIconURL(String mIconURL) {
		this.mIconURL = mIconURL;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(this.mVicinity);
		dest.writeString(this.mPlaceId);
		dest.writeString(this.mName);
		dest.writeString(this.mLongitude);
		dest.writeString(this.mLatitude);
		dest.writeString(this.mIconURL);
		dest.writeDouble(mDistance);
	}
	
	public GoogleStoreInfo(Parcel parcel)
	{
		this.mVicinity=parcel.readString();
		this.mPlaceId=parcel.readString();
		this.mName=parcel.readString();
		this.mLongitude=parcel.readString();
		this.mLatitude=parcel.readString();
		this.mIconURL=parcel.readString();
		this.mDistance=parcel.readDouble();
		
	}
	
	public GoogleStoreInfo() {
		// TODO Auto-generated constructor stub
	}

	public static final Creator<GoogleStoreInfo> CREATOR=new Creator<GoogleStoreInfo>() {
		
		@Override
		public GoogleStoreInfo[] newArray(int size) {
			return new GoogleStoreInfo[size];
		}
		
		@Override
		public GoogleStoreInfo createFromParcel(Parcel source) {
			return new GoogleStoreInfo(source);
		}
	};

}
