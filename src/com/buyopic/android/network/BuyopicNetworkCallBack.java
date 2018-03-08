package com.buyopic.android.network;



public interface BuyopicNetworkCallBack {

	public void onSuccess(int requestCode,Object object);

	public void onFailure(int requestCode,String message);

}
