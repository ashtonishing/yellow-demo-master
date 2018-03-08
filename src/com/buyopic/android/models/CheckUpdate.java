package com.buyopic.android.models;


public class CheckUpdate {

	private boolean mAreUpdatesAvailable;
	private String mMessage;
	private int mBackGrndBtwnScanPeriod;
	private int mBackGrndScanPeriod;
	private int mIntervalChkForNewAlertsCloseByList;
	private int  mIntervalChkForNewAlertsBackGrndMonitor;
	private String mNextRunTime;
	private int mSmartLocationComputingRate;
	private int  mDuration;
	private int mNearestBeaconDistance;
	private boolean mIsComputed;
	private OrderStatus mOrderStatusObj;
	
	
	

	public int getmNearestBeaconDistance() {
		return mNearestBeaconDistance;
	}

	public void setmNearestBeaconDistance(int mNearestBeaconDistance) {
		this.mNearestBeaconDistance = mNearestBeaconDistance;
	}

	public OrderStatus getmOrderStatusObj() {
		return mOrderStatusObj;
	}

	public void setmOrderStatusObj(OrderStatus mOrderStatusObj) {
		this.mOrderStatusObj = mOrderStatusObj;
	}

	public boolean getmIsComputed() {
		return mIsComputed;
	}

	public void setmIsComputed(boolean mIsComputed) {
		this.mIsComputed = mIsComputed;
	}

	public int getmBackGrndBtwnScanPeriod() {
		return mBackGrndBtwnScanPeriod;
	}

	public void setmBackGrndBtwnScanPeriod(int mBackGrndBtwnScanPeriod) {
		this.mBackGrndBtwnScanPeriod = mBackGrndBtwnScanPeriod;
	}

	public int getmBackGrndScanPeriod() {
		return mBackGrndScanPeriod;
	}

	public void setmBackGrndScanPeriod(int mBackGrndScanPeriod) {
		this.mBackGrndScanPeriod = mBackGrndScanPeriod;
	}

	public int getmIntervalChkForNewAlertsCloseByList() {
		return mIntervalChkForNewAlertsCloseByList;
	}

	public void setmIntervalChkForNewAlertsCloseByList(
			int mIntervalChkForNewAlertsCloseByList) {
		this.mIntervalChkForNewAlertsCloseByList = mIntervalChkForNewAlertsCloseByList;
	}

	public int getmIntervalChkForNewAlertsBackGrndMonitor() {
		return mIntervalChkForNewAlertsBackGrndMonitor;
	}

	public void setmIntervalChkForNewAlertsBackGrndMonitor(
			int mIntervalChkForNewAlertsBackGrndMonitor) {
		this.mIntervalChkForNewAlertsBackGrndMonitor = mIntervalChkForNewAlertsBackGrndMonitor;
	}

	public String getmNextRunTime() {
		return mNextRunTime;
	}

	public void setmNextRunTime(String mNextRunTime) {
		this.mNextRunTime = mNextRunTime;
	}

	public int getmSmartLocationComputingRate() {
		return mSmartLocationComputingRate;
	}

	public void setmSmartLocationComputingRate(int mSmartLocationComputingRate) {
		this.mSmartLocationComputingRate = mSmartLocationComputingRate;
	}

	public int getmDuration() {
		return mDuration;
	}

	public void setmDuration(int mDuration) {
		this.mDuration = mDuration;
	}

	public boolean getmAreUpdatesAvailable() {
		return mAreUpdatesAvailable;
	}

	public void setmAreUpdatesAvailable(boolean mIsAvailable) {
		this.mAreUpdatesAvailable = mIsAvailable;
	}

	public String getmMessage() {
		return mMessage;
	}

	public void setmMessage(String mMessage) {
		this.mMessage = mMessage;
	}

}
