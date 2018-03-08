package com.buyopic.android.adapters;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

import com.buyopic.android.beacon.R;
import com.buyopic.android.models.Alert;
import com.buyopic.android.network.BuyopicNetworkCallBack;
import com.buyopic.android.network.BuyopicNetworkServiceManager;
import com.buyopic.android.network.JsonResponseParser;
import com.buyopic.android.radius.BuyOpic;
import com.buyopic.android.utils.Constants;
import com.buyopic.android.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class CustomAlertMessageAdapter extends ArrayAdapter<Alert> {
	private Context mContext;
	private List<Alert> mAlerts;
	ViewHolder holder = null;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private BuyOpic buyOpic;
	private String mStoreName;
	

	public CustomAlertMessageAdapter(Context context, List<Alert> objects,String storeName) {
		super(context, -1, objects);
		this.mContext = context;
		this.mAlerts = objects;
		this.mStoreName=storeName;
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
					R.layout.layout_custom_price_adapter_view, null);
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
			holder.mShareOptionsMenuView = (ImageView) convertView
					.findViewById(R.id.custom_layout_offers_ic_overflow_menu);
			holder.mFavoriteCheckBoxView = (CheckBox) convertView
					.findViewById(R.id.custom_layout_offers_toggle_favorites);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final Alert alert = getItem(position);

		holder.mFavoriteCheckBoxView.setChecked(alert.isFavorite());
		buyOpic = (BuyOpic) mContext.getApplicationContext();
		holder.mFavoriteCheckBoxView
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(
							final CompoundButton buttonView,
							final boolean isChecked) {
						BuyopicNetworkServiceManager buyopicNetworkServiceManager = BuyopicNetworkServiceManager
								.getInstance(mContext);
						buyopicNetworkServiceManager.sendFavoriteRequest(
								Constants.REQUEST_FAVORITE,
								buyOpic.getmConsumerId(), alert.getmOfferId(),
								isChecked, new BuyopicNetworkCallBack() {

									@Override
									public void onSuccess(int requestCode,
											Object object) {
										switch (requestCode) {
										case Constants.REQUEST_FAVORITE:
											boolean status = JsonResponseParser
													.parseFavoriteResponse((String) object);
											if (status) {
												buttonView
														.setChecked(isChecked);
											}
											break;

										default:
											break;
										}
									}

									@Override
									public void onFailure(int requestCode,
											String message) {

									}
								});
					}
				});
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
		holder.mShareOptionsMenuView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				displayPopUpMenu(v, alert);

			}
		});
		if (alert.ismIsAlertDiscontinued()) {
			convertView.setBackgroundColor(Color.parseColor("#D1D1D1"));
		} else {
			convertView.setBackgroundColor(Color.TRANSPARENT);
		}
		return convertView;
	}

	static class ViewHolder {
		private TextView mMessageView;
		private TextView mPriceView;
		private TextView mAlertTitleView;
		private ImageView mThumbnailImageView;
		private ImageView mShareOptionsMenuView;
		private CheckBox mFavoriteCheckBoxView;

	}

	private void displayPopUpMenu(View v, final Alert alert) {
		PopupMenu popupMenu = new PopupMenu(mContext, v);
		try {
			Field[] fields = popupMenu.getClass().getDeclaredFields();
			for (Field field : fields) {
				if ("mPopup".equals(field.getName())) {
					field.setAccessible(true);
					Object menuPopupHelper = field.get(popupMenu);
					Class<?> classPopupHelper = Class.forName(menuPopupHelper
							.getClass().getName());
					Method setForceIcons = classPopupHelper.getMethod(
							"setForceShowIcon", boolean.class);
					setForceIcons.invoke(menuPopupHelper, true);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		popupMenu.getMenuInflater().inflate(R.menu.menu_context,
				popupMenu.getMenu());
		popupMenu.getMenu().add(0, 2, 0, "Call "+mStoreName).setIcon(R.drawable.ic_phone_icon);
		popupMenu.show();
		

		popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if (item.getItemId() == R.id.share) {
					sendShareStoreAlertRequestToServer(alert.getmOfferId(),
							buyOpic.getmConsumerId());
					Utils.shareOffer(mContext, alert.getmOfferId(), buyOpic.getmConsumerId(), alert.getmPostedConsumerId(), alert.getmStoreId(), alert.getmRetailerId());
				}
				else if(item.getItemId()==2)
				{
					
					Utils.callPhone(mContext, alert.getmPhoneNumber());
				}
				return true;
			}
		});
	}
	
	protected void sendShareStoreAlertRequestToServer(String mOfferId,
			String consumerId) {
		BuyopicNetworkServiceManager buyopicNetworkServiceManager=BuyopicNetworkServiceManager.getInstance(mContext);
		buyopicNetworkServiceManager.sendShareStoreAlertRequest(Constants.REQUEST_SHARE_STORE_ALERT, consumerId, mOfferId, new BuyopicNetworkCallBack() {
			
			@Override
			public void onSuccess(int requestCode, Object object) {
				
			}
			
			@Override
			public void onFailure(int requestCode, String message) {
				
			}
		});
	}

	private DisplayImageOptions configureOptions() {
		return new DisplayImageOptions.Builder()
				.showImageOnLoading(android.R.color.transparent)
				.showImageForEmptyUri(R.drawable.ic_placeholder_image)
				.imageScaleType(ImageScaleType.EXACTLY).cacheInMemory(false)
				.considerExifParams(true)
				.showImageOnFail(R.drawable.ic_placeholder_image)
				.cacheOnDisc(true).build();
	}

}
