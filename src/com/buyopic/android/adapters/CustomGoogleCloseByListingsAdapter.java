package com.buyopic.android.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buyopic.android.beacon.R;
import com.buyopic.android.network.GoogleStoreInfo;
import com.buyopic.android.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class CustomGoogleCloseByListingsAdapter extends ArrayAdapter<GoogleStoreInfo> {
	ViewHolder1 holder1 = null;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private Context mContext;
    ArrayList<GoogleStoreInfo> list;
	public CustomGoogleCloseByListingsAdapter(Context context, List<GoogleStoreInfo> mGooglestoreInfo) {
		super(context, -1, mGooglestoreInfo);
		this.mContext = context;
		this.list=(ArrayList<GoogleStoreInfo>) mGooglestoreInfo;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public GoogleStoreInfo getItem(int position) {
		return super.getItem(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 if(list!=null&&list.size()>0){
			 if(convertView==null){

			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.layout_custom_google_list_home_new, null);
			Utils.overrideFonts(mContext, convertView);
			holder1 = new ViewHolder1();
			holder1.mStoreImageView1 = (ImageView) convertView
					.findViewById(R.id.custom_layout_home_list_view_google_store_logo);
			holder1.mConvertViewLayout1 = (LinearLayout) convertView
					.findViewById(R.id.convert_view_google);
			holder1.mStoreNameView1 = (TextView) convertView
					.findViewById(R.id.custom_layout_home_google_list_view_store_name);
			holder1.mDistanceView1 = (TextView) convertView
					.findViewById(R.id.custom_layout_home_google_list_view_distance);
		
			holder1.mOfferTitle1 = (TextView) convertView
					.findViewById(R.id.custom_layout_home_google_list_view_product_name);
			holder1.mOfferPrice1 = (TextView) convertView
					.findViewById(R.id.custom_layout_home_google_list_view_product_price);
			ArrayList<GoogleStoreInfo>  mgooglestoreinfo=list;
			imageLoader.displayImage(mgooglestoreinfo.get(position).getmIconURL(),
					holder1.mStoreImageView1, configureOptions());
			holder1.mConvertViewLayout1.setBackgroundColor(Color.TRANSPARENT);
			holder1.mStoreNameView1.setText(mgooglestoreinfo.get(position).getmName());
			holder1.mDistanceView1.setText(String.valueOf(mgooglestoreinfo.get(position).getmDistance()));
			holder1.mOfferTitle1.setText(mgooglestoreinfo.get(position).getmName());
			holder1.mOfferPrice1.setText(mgooglestoreinfo.get(position).getmName());
			
			convertView.setTag(holder1);
			 }
			 else {
				 if( convertView.getTag().equals(holder1))
					holder1 = (ViewHolder1) convertView.getTag();
				}
		
			
		}
		return convertView;
	}

	public class ViewHolder1 {
		
		private TextView mStoreNameView1;
		private ImageView mStoreImageView1;
		private TextView mDistanceView1;
		private LinearLayout mConvertViewLayout1;
		
		private TextView mOfferTitle1;
		private TextView mOfferPrice1;
	}
	private DisplayImageOptions configureOptions() {
		return new DisplayImageOptions.Builder()
				.showImageOnLoading(android.R.color.transparent)
				.showImageForEmptyUri(android.R.color.transparent)
				.imageScaleType(ImageScaleType.EXACTLY).cacheInMemory(true)
				.considerExifParams(true)
				.showImageOnFail(android.R.color.transparent).cacheOnDisc(true)
				.build();
	}

}
