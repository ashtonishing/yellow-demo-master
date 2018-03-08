package com.buyopic.android.radius;

import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.buyopic.android.adapters.OrdersListCustomAdapter;
import com.buyopic.android.beacon.R;
import com.buyopic.android.models.OrderList;
import com.buyopic.android.network.BuyopicNetworkCallBack;
import com.buyopic.android.network.BuyopicNetworkServiceManager;
import com.buyopic.android.network.JsonResponseParser;
import com.buyopic.android.utils.Constants;
import com.buyopic.android.utils.Utils;


public class MyOrdersActivity extends BaseActivity implements OnItemClickListener,BuyopicNetworkCallBack,OnClickListener{
	
	
	private ListView mListView;
	private ProgressDialog mProgressDialog;
	private List<OrderList> orderStatusList;
	Dialog dialog;
	TextView itemNameText;
	TextView consumerMailId;
	TextView consumerPhoneNum;
	TextView priceValueText;
	TextView addressText;
	TextView quantityNumber;
	TextView orderDeliveryTime;
	Button cancelBtn;
	ImageButton cancelDialog;
	TextView timeOfDelivery;
	ImageView orderStatusImage;
	OrderList orderitem;
	private TextView orderStatusValue;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		View view = LayoutInflater.from(this).inflate(R.layout.layout_orders_fragment,null);
		dialog = new Dialog(this, R.style.Theme_Dialog);
		setContentView(view);
		Utils.overrideFonts(this, view);
		mListView = (ListView) view.findViewById(R.id.listviewinorders);
		buyOpic = (BuyOpic) getApplicationContext();
		buyopicNetworkServiceManager = BuyopicNetworkServiceManager
				.getInstance(this);
		String consumerId=buyOpic.getmConsumerId();
		showProgressDialog();
		setBeaconActionBar("My Orders",4);
		buyopicNetworkServiceManager.getMyOrdersRequest(Constants.REQUEST_ORDERS_DETAILS, consumerId, this);
	}


	@Override
	public void onSuccess(int requestCode, Object object) {
		// TODO Auto-generated method stub
		dismissProgressDialog();
		if(requestCode==Constants.REQUEST_ORDERS_DETAILS){
			orderStatusList = JsonResponseParser
					.parseOrderListStatus((String) object);
			bindViews(orderStatusList);
		}
	}
	private void bindViews(List<OrderList> orderStatusList2) {
		// TODO Auto-generated method stub


		if (orderStatusList2 != null && !orderStatusList2.isEmpty()) {
			mListView.setAdapter(new OrdersListCustomAdapter(this,
					orderStatusList2));
		}
		mListView.setOnItemClickListener(this);
		mListView.setSelection(0);
	
	}

	private void showProgressDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = ProgressDialog.show(this, "", "Please wait",
					false, false);
		}
		mProgressDialog.show();
	}

	private void dismissProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
		orderitem = (OrderList) mListView.getAdapter().getItem(arg2);
		showUpdateOrderPopUp(orderitem);
	}

	private void showUpdateOrderPopUp(OrderList orderitem) {

		// TODO Auto-generated method stub

		View view = null;
		try {
			view = LayoutInflater.from(this).inflate(
					R.layout.updatenowdialog, null);

			dialog.setContentView(view);
			cancelDialog = (ImageButton) dialog
					.findViewById(R.id.dialog_cancel);
			orderDeliveryTime = (TextView) dialog
					.findViewById(R.id.exporderdeliverytime);
			itemNameText = (TextView) dialog.findViewById(R.id.itemnametext);
			consumerMailId = (TextView) dialog.findViewById(R.id.consumermailidtext);
			consumerPhoneNum = (TextView) dialog.findViewById(R.id.consumerphonenumtext);
			timeOfDelivery = (TextView) dialog
					.findViewById(R.id.timeofdelivery);
			orderStatusImage = (ImageView) dialog
					.findViewById(R.id.orderstatusspinner);
			priceValueText = (TextView) dialog
					.findViewById(R.id.pricevaluetext);
			addressText = (TextView) dialog.findViewById(R.id.addresstext);
			quantityNumber = (TextView) dialog
					.findViewById(R.id.quantitynumber);
			
	
			orderStatusValue=(TextView) dialog.findViewById(R.id.orderstatus);
			
			orderStatusValue.setText(orderitem.getStatusDesc());
			itemNameText.setText(orderitem.getOfferItemName());
			consumerMailId.setText(orderitem.getConsumerMailId());
			consumerPhoneNum.setText(orderitem.getConsumerPhoneNo());
			timeOfDelivery.setText(orderitem.getRequestedDelivery());
			priceValueText.setText(orderitem.getTotalOrderPrice());
			quantityNumber.setText(orderitem.getOrderQuantity());
			addressText.setText(orderitem.getAddressLine1() + " "
					+ orderitem.getAddressLine2());
			
			orderDeliveryTime.setText(orderitem.getExpectedDelivery());
			if(orderitem.getStatusDesc().equalsIgnoreCase("Canceled")){
				orderStatusImage.setImageResource(R.drawable.icon_canceled);
			}
			else if(orderitem.getStatusDesc().equalsIgnoreCase("Inbox")){
				orderStatusImage.setImageResource(R.drawable.icon_message);
			}
			else if(orderitem.getStatusDesc().equalsIgnoreCase("Confirmed")){
				orderStatusImage.setImageResource(R.drawable.icon_confirmed);
			}
			
			else if(orderitem.getStatusDesc().equalsIgnoreCase("Dispatched")){
				orderStatusImage.setImageResource(R.drawable.icon_dispatched);
			}
			
			else if(orderitem.getStatusDesc().equalsIgnoreCase("Delivered")){
				orderStatusImage.setImageResource(R.drawable.icon_delivered);
			}
			cancelDialog.setOnClickListener(this);

		} catch (Exception e) {
			e.printStackTrace();
		}

		

		dialog.show();
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if((v.getId()==R.id.dialog_cancel)){
			dialog.dismiss();
			
		}
		
	}


	



}
