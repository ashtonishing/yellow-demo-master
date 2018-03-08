package com.buyopic.android.adapters;

import java.util.LinkedHashMap;
import java.util.List;

import com.buyopic.android.beacon.R;
import com.buyopic.android.models.OrderList;
import com.buyopic.android.radius.BuyOpic;
import com.buyopic.android.utils.Utils;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class OrdersListCustomAdapter extends ArrayAdapter<OrderList> {

	Context mContext;
	List<OrderList> orderListObject;
	BuyOpic buyOpic;

	public OrdersListCustomAdapter(Context context, List<OrderList> objects) {

		super(context, -1, objects);
		mContext = context;
		buyOpic = (BuyOpic) mContext.getApplicationContext();
		orderListObject = objects;

		// TODO Auto-generated constructor stub
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return orderListObject.size();
	}

	@Override
	public OrderList getItem(int position) {
		// TODO Auto-generated method stub
		return super.getItem(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.orderlistitem, null);
			Utils.overrideFonts(mContext, convertView);
			holder = new ViewHolder();
			holder.mOrderTimeView = (TextView) convertView
					.findViewById(R.id.orderstimetext);
			holder.mOrderNameView = (TextView) convertView
					.findViewById(R.id.ordersnametext);
			holder.mStatusImageView = (ImageView) convertView
					.findViewById(R.id.orderstatusimage);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		OrderList item = getItem(position);
		holder.mOrderTimeView.setText(item.getConsumerName()+" : "+item.getRequestedDelivery());
		holder.mOrderNameView.setText(item.getOfferItemName());
		String id = item.getStatusId();
		int statusid = Integer.parseInt(id);

		switch (statusid) {
		case 1:
			holder.mStatusImageView.setImageResource(R.drawable.icon_message);
			break;
		case 2:
			holder.mStatusImageView.setImageResource(R.drawable.icon_confirmed);
			break;
		case 3:
			holder.mStatusImageView
					.setImageResource(R.drawable.icon_dispatched);
			break;
		case 4:
			holder.mStatusImageView.setImageResource(R.drawable.icon_delivered);
			break;
		case 5:
			holder.mStatusImageView.setImageResource(R.drawable.icon_canceled);
			break;

		default:
			break;
		}
		if (position % 2 == 0) {
			holder.mOrderTimeView
					.setBackgroundResource(R.drawable.right_solid_grey_black_border_view);
			holder.mOrderNameView
					.setBackgroundResource(R.drawable.right_solid_grey_grey_border_view);
			// holder.mTotalTimeView
			// .setBackgroundResource(R.drawable.right_solid_grey_grey_border_view);
			convertView.setBackgroundColor(Color.parseColor("#e7e7e7"));
		} else {
			holder.mOrderTimeView
					.setBackgroundResource(R.drawable.right_border_view);
			holder.mOrderNameView
					.setBackgroundResource(R.drawable.right_solid_white_grey_border_view);
			// holder.mTotalTimeView
			// .setBackgroundResource(R.drawable.right_solid_white_grey_border_view);
			convertView.setBackgroundColor(Color.WHITE);
		}
		return convertView;
	}

	static class ViewHolder {
		private TextView mOrderTimeView;
		private TextView mOrderNameView;
		private ImageView mStatusImageView;
	}

}
