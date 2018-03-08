package com.buyopic.android.utils;

import com.buyopic.android.network.BuyopicNetworkServiceManager;

public class Constants {

	/*
	 * Response Code Constants
	 */
	public static int SUCCESS = 100;
	public static int FAILURE = 101;
	public static String CURRENCYSYMBOL="";
	public static boolean isAddresschanged=false;

	/*
	 * Network Call Request Constants
	 */

	public static final int REQUEST_YELP_REVIEWS=1000;
	public static final int REQUEST_GETSTORE_ALERTS=1001;
	public static final int REQUEST_GETSTORE_ALERTS_DETAILS=1002;
	public static final int REQUEST_CONSUMER_LOGIN=1003;
	public static final int REQUEST_CONSUMER_REGISTRATION=1004;
	public static final int REQUEST_CONSUMER_OFFLINE_BEACON=1005;
	public static final int REQUEST_NEAREST_STORE_ALERTS=1006;
	public static final int REQUEST_STORE_ALERTS=1007;
	public static final int REQUEST_CHECK_FOR_NEW_DATA=1008;
	public static final int REQUEST_MYTRAILS=1009;
	public static final int REQUEST_CHECK_BEACON_ALERTS=1010;
	public static final int REQUEST_SEARCH=1011;
	public static final int REQUEST_SEARCH_HISTORY=1012;
	public static final int REQUEST_SEND_ENTRY_REQUEST=1013;
	public static final int REQUEST_SEND_EXIT_REQUEST=1014;
	public static final int REQUEST_DISTANCE_TRACK_REQUEST=1015;
	public static final int REQUEST_FAVORITE=1016;
	public static final int REQUEST_CREATE_CONSUMER_ALERT=1017;
	public static final int REQUEST_CONSUMER_ALERT_DETAILS=1018;
	public static final int REQUEST_LIST_CONSUMER_POSTED_ALERTS=1019;
	public static final int REQUEST_SHARE_STORE_ALERT=1020;
	public static final int REQUEST_SHARE_STORE_ALERT_CONFIRMATION=1021;
	public static final int REQUEST_LIST_SHARE_WITH_ME=1022;
	public static final int REQUEST_LIST_SHARE_BY_ME=1023;
	public static final int REQUEST_DELETE_SHARE_ITEM=1024;
	public static final int REQUEST_MERCHANT_CONFIRMATION = 1025;
	public static final int REQUEST_RESET_PASSWORD = 1026;
	public static final int REQUEST_CONSUMER_UPDATE= 1027;
	public static final int REQUEST_RSSI_UPDATE= 1028;
	public static final int REQUEST_GENERATE_URL= 1029;
	public static final int REQUEST_URL_PROCESS_DETAILS= 1030;
	
	public static final int RESULT_MAP = 1032;
	public static final int REQUEST_URL_SUBMIT_NEWORDER= 1033;
	public static final int REQUEST_URL_ADDRESS_DETAILS= 1034;
	public static final int REQUEST_ORDERS_DETAILS= 1035;
	public static final int REQUEST_ORDERS_LOGIN= 1036;
	public static final int REQUEST_ORDERS_REQUEST_OFFERDETAILS= 1037;
	
	public static final String PARAM_ID="id";
	public static final String CUSTOM_ACTION_INTENT = "com.buyopic.android";
	
	public static final String SHARE_BASE_URL=BuyopicNetworkServiceManager.BASE_URL+"share?";
//	public static final String SHARE_BYPASS_BASE_URL="http://buyopic.com/share?"+PARAM_ID+"=";
	public static final String SHARE_BYPASS_BASE_URL=BuyopicNetworkServiceManager.BASE_URL+"processurl/yellow?";
	public static final String PROCESS_TYPE_SHARE="share";
	public static final String PROCESS_TYPE_REGISTRATION="registration";
}
