package com.buyopic.android.network;

import android.os.Handler;
import android.os.Message;

import com.buyopic.android.utils.Constants;


public class BuyopicRequestHandler extends Handler{

	BuyopicNetworkCallBack callBack;
	private int requestCode;
	public BuyopicRequestHandler(int requestCode,BuyopicNetworkCallBack callBack) {
		this.callBack = callBack;
		this.requestCode=requestCode;
	}
	
	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		if(msg.what==Constants.SUCCESS)
		{
			Object object=msg.obj;
			callBack.onSuccess(requestCode,object);
		}
		else if(msg.what==Constants.FAILURE)
		{
			Object object=msg.obj;
			callBack.onFailure(requestCode,object.toString());
		}
	}
}
