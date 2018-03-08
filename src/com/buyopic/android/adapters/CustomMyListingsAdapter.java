package com.buyopic.android.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.buyopic.android.beacon.R;
import com.buyopic.android.models.Alert;
import com.buyopic.android.utils.Constants;
import com.buyopic.android.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class CustomMyListingsAdapter extends ArrayAdapter<Alert> {
	private Context mContext;
	private List<Alert> mAlerts;
	ViewHolder holder = null;
	private ImageLoader imageLoader = ImageLoader.getInstance();

	public CustomMyListingsAdapter(Context context, List<Alert> objects) {
		super(context, -1, objects);
		this.mContext = context;
		this.mAlerts = objects;
	}

	@Override
	public int getCount() {
		return mAlerts.size();
	}

	@Override
	public Alert getItem(int position) {
		return super.getItem(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.layout_custom_my_listings_adapter_view, null);
			Utils.overrideFonts(mContext, convertView);
			holder = new ViewHolder();
			holder.mMessageView = (TextView) convertView
					.findViewById(R.id.custom_layout_offers_message_description_view);
			holder.mPriceView = (TextView) convertView
					.findViewById(R.id.custom_layout_offers_price_view);
			holder.mThumbnailImageView = (ImageView) convertView
					.findViewById(R.id.custom_layout_offers_image_view);
			holder.mAlertTitleView = (TextView) convertView
					.findViewById(R.id.custom_layout_offers_title_view);

			holder.mActivateImgeView = (CheckBox) convertView
					.findViewById(R.id.custom_layout_offers_activated_icon_view);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Alert alert = getItem(position);
		holder.mMessageView.setText(alert.getmOfferMessage());
		holder.mAlertTitleView.setText(alert.getmOfferTitle() + ":");
		
		
		// BEGIN suppress zero price list items
		//holder.mPriceView.setText("$" + alert.getmPrice());
		if (!alert.getmPrice().equalsIgnoreCase("0.00")){
			holder.mPriceView.setText(Constants.CURRENCYSYMBOL + alert.getmPrice());
		} else{
			holder.mPriceView.setText("");
		}
		// END suppress zero price list items
		
		imageLoader.displayImage(alert.getmThumbnailUrl(),
				holder.mThumbnailImageView, configureOptions());
		holder.mActivateImgeView.setChecked(alert.ismIsActivated());
		return convertView;
	}

	static class ViewHolder {
		private TextView mMessageView;
		private TextView mPriceView;
		private TextView mAlertTitleView;
		private ImageView mThumbnailImageView;
		private CheckBox mActivateImgeView;
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
