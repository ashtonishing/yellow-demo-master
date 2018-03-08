package com.buyopic.android.models;

import java.io.Serializable;

public class MapData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String mName;
	String mStreet;
	String mCity;
	String mCountry;
	String mPostalCode;
	String mState;
	String latitude;
	String longitude;
	String placeid;
	String iconurl;
	String mAddress1;
	String mAddress2;
	
	
	public String getmAddress1() {
		return mAddress1;
	}
	public void setmAddress1(String mAddress1) {
		this.mAddress1 = mAddress1;
	}
	public String getmAddress2() {
		return mAddress2;
	}
	public void setmAddress2(String mAddress2) {
		this.mAddress2 = mAddress2;
	}
	public String getmState() {
		return mState;
	}
	public void setmState(String mState) {
		this.mState = mState;
	}
	public String getmName() {
		return mName;
	}
	public void setmName(String mName) {
		this.mName = mName;
	}
	public String getPlaceid() {
		return placeid;
	}
	public void setPlaceid(String placeid) {
		this.placeid = placeid;
	}
	public String getIconurl() {
		return iconurl;
	}
	public void setIconurl(String iconurl) {
		this.iconurl = iconurl;
	}
	
	
	
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getmStreet() {
		return mStreet;
	}
	public void setmStreet(String mStreet) {
		this.mStreet = mStreet;
	}
	public String getmCity() {
		return mCity;
	}
	public void setmCity(String mCity) {
		this.mCity = mCity;
	}
	public String getmCountry() {
		return mCountry;
	}
	public void setmCountry(String mCountry) {
		this.mCountry = mCountry;
	}
	public String getmPostalCode() {
		return mPostalCode;
	}
	public void setmPostalCode(String mPostalCode) {
		this.mPostalCode = mPostalCode;
	}
	
	
	
}
