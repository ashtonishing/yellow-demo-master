package com.buyopic.android.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ProvisionedBeaconInfo implements Parcelable{

	private String mBeaconUUID;
	private String mMajor;
	private String mMinor;
	
	
	public String getmBeaconUUID() {
		return mBeaconUUID;
	}
	public void setmBeaconUUID(String mBeaconUUID) {
		this.mBeaconUUID = mBeaconUUID;
	}
	public String getmMajor() {
		return mMajor;
	}
	public void setmMajor(String mMajor) {
		this.mMajor = mMajor;
	}
	public String getmMinor() {
		return mMinor;
	}
	public void setmMinor(String mMinor) {
		this.mMinor = mMinor;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.mBeaconUUID);
		dest.writeString(this.mMajor);
		dest.writeString(this.mMinor);
	}
	
	public ProvisionedBeaconInfo(Parcel parcel)
	{
		this.mBeaconUUID=parcel.readString();
		this.mMajor=parcel.readString();
		this.mMinor=parcel.readString();
	}
	
	public ProvisionedBeaconInfo() {
		// TODO Auto-generated constructor stub
	}

	public static final Creator<ProvisionedBeaconInfo> CREATOR=new Creator<ProvisionedBeaconInfo>() {
		
		@Override
		public ProvisionedBeaconInfo[] newArray(int size) {
			return new ProvisionedBeaconInfo[size];
		}
		
		@Override
		public ProvisionedBeaconInfo createFromParcel(Parcel source) {
			return new ProvisionedBeaconInfo(source);
		}
	};
	
	
}
