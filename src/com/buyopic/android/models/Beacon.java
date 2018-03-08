package com.buyopic.android.models;


public class Beacon {
	private String mBeaconUUID;

	private String mMinor;

	private String mMajor;

	private String mRssi;

	private String mEnteredTime;

	private String mBeaconId_Minor_Major;

	private String isNotified;

	public String getmBeaconUUID() {
		return mBeaconUUID;
	}

	public String getmBeaconId_Minor_Major() {
		return mBeaconId_Minor_Major;
	}

	public void setmBeaconId_Minor_Major(String mBeaconId_Minor_Major) {
		this.mBeaconId_Minor_Major = mBeaconId_Minor_Major;
	}

	public String getIsNotified() {
		return isNotified;
	}

	public void setIsNotified(String isNotified) {
		this.isNotified = isNotified;
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

	public String getmRssi() {
		return mRssi;
	}

	public void setmRssi(String mRssi) {
		this.mRssi = mRssi;
	}

	public String getmEnteredTime() {
		return mEnteredTime;
	}

	public void setmEnteredTime(String mEnteredTime) {
		this.mEnteredTime = mEnteredTime;
	}

}
