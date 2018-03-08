package com.buyopic.android.models;

import android.os.Parcel;
import android.os.Parcelable;


public class OrderStatus implements Parcelable {
	String requestedDelivery;
	String expectedDelivery;
	String state;
	String itemPrice;
	String addressLine3;
	String addressLine2;
	String addressLine1;
	String city;
	String consumerId;
	String totalOrderPrice;
	String statusId;
	String pincode;
	String consumerName;
	String offerItemName;
	String offerItemId;
	String orderQuantity;
	String statusDesc;
	String orderId;
	String storeId;
	String storeName;
	
	

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getExpectedDelivery() {
		
		return expectedDelivery;
	}

	public void setExpectedDelivery(String expectedDelivery) {
		this.expectedDelivery = expectedDelivery;
	}

	public String getRequestedDelivery() {
	/*	String newFormart = "MMM dd yyyy hh:mm a";
		String oldFormart = "yyyy-mm-dd hh:mm a";
		SimpleDateFormat df1 = new SimpleDateFormat(oldFormart);
		SimpleDateFormat df2 = new SimpleDateFormat(newFormart);
		String value = null;
		try {
			value = df2.format(df1.parse(requestedDelivery));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		return requestedDelivery;
	}

	public void setRequestedDelivery(String requestedDelivery) {
		this.requestedDelivery = requestedDelivery;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getItemPrice() {
		return itemPrice;
	}

	public void setItemPrice(String itemPrice) {
		this.itemPrice = itemPrice;
	}

	public String getAddressLine3() {
		return addressLine3;
	}

	public void setAddressLine3(String addressLine3) {
		this.addressLine3 = addressLine3;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getConsumerId() {
		return consumerId;
	}

	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
	}

	public String getTotalOrderPrice() {
		return totalOrderPrice;
	}

	public void setTotalOrderPrice(String totalOrderPrice) {
		this.totalOrderPrice = totalOrderPrice;
	}

	public String getStatusId() {
		return statusId;
	}

	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getConsumerName() {
		return consumerName;
	}

	public void setConsumerName(String consumerName) {
		this.consumerName = consumerName;
	}

	public String getOfferItemName() {
		return offerItemName;
	}

	public void setOfferItemName(String offerItemName) {
		this.offerItemName = offerItemName;
	}

	public String getOfferItemId() {
		return offerItemId;
	}

	public void setOfferItemId(String offerItemId) {
		this.offerItemId = offerItemId;
	}

	public String getOrderQuantity() {
		return orderQuantity;
	}

	public void setOrderQuantity(String orderQuantity) {
		this.orderQuantity = orderQuantity;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	public static final Creator<OrderStatus> CREATOR = new Creator<OrderStatus>() {

		@Override
		public OrderStatus[] newArray(int size) {
			return new OrderStatus[size];
		}

		@Override
		public OrderStatus createFromParcel(Parcel source) {
			return new OrderStatus(source);
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub

		dest.writeString(this.requestedDelivery);
		dest.writeString(this.state);
		dest.writeString(this.expectedDelivery);
		dest.writeString(this.itemPrice);
		dest.writeString(this.addressLine3);
		dest.writeString(this.addressLine2);
		dest.writeString(this.addressLine1);
		dest.writeString(this.totalOrderPrice);
		dest.writeString(this.consumerId);
		dest.writeString(this.city);
		dest.writeString(this.offerItemName);
		dest.writeString(this.consumerName);
		dest.writeString(this.pincode);
		dest.writeString(this.statusId);
		dest.writeString(this.statusDesc);
		dest.writeString(this.offerItemId);
		dest.writeString(this.orderQuantity);
		dest.writeString(this.orderId);

	}

	public OrderStatus(Parcel parcel) {
		this.requestedDelivery = parcel.readString();
		this.state = parcel.readString();
		this.expectedDelivery = parcel.readString();
		this.itemPrice = parcel.readString();
		this.addressLine3 = parcel.readString();
		this.addressLine2 = parcel.readString();
		this.addressLine1 = parcel.readString();
		this.totalOrderPrice = parcel.readString();
		this.consumerId = parcel.readString();
		this.city = parcel.readString();
		this.offerItemName = parcel.readString();
		this.consumerName = parcel.readString();
		this.pincode = parcel.readString();
		this.statusId = parcel.readString();
		this.statusDesc = parcel.readString();
		this.offerItemId = parcel.readString();
		this.orderQuantity = parcel.readString();
		this.orderId = parcel.readString();

	}

	public OrderStatus() {
		// TODO Auto-generated constructor stub
	}
}
