package com.buyopic.android.adapters;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
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
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

import com.buyopic.android.beacon.R;
import com.buyopic.android.network.BuyopicNetworkCallBack;
import com.buyopic.android.network.BuyopicNetworkServiceManager;
import com.buyopic.android.network.JsonResponseParser;
import com.buyopic.android.radius.BuyOpic;
import com.buyopic.android.utils.Constants;
import com.buyopic.android.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class CustomSharedWithMeFragmentAdapter extends
		ArrayAdapter<HashMap<String, String>> implements BuyopicNetworkCallBack {

	private Context mContext;
	private List<HashMap<String, String>> mSharesWithMe;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private BuyOpic buyOpic;
	private boolean isFromSharedWithMe = false;

	public CustomSharedWithMeFragmentAdapter(Context context,
			List<HashMap<String, String>> objects, boolean isFromSharedWithMe) {
		super(context, -1, objects);
		this.mContext = context;
		this.mSharesWithMe = objects;
		this.isFromSharedWithMe = isFromSharedWithMe;
		buyOpic = (BuyOpic) mContext.getApplicationContext();
	}

	@Override
	public int getCount() {
		return mSharesWithMe.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.custom_layout_shared_with_me, null);
			viewHolder.mOfferLogoView = (ImageView) convertView
					.findViewById(R.id.custom_layout_favorites_offer_image);
			viewHolder.mOfferTitleView = (TextView) convertView
					.findViewById(R.id.custom_layout_favorites_offer_title);
			viewHolder.mPriceView = (TextView) convertView
					.findViewById(R.id.custom_layout_favorites_offer_price);
			viewHolder.storeNameView = (TextView) convertView
					.findViewById(R.id.custom_layout_favorites_offer_store_name);
			viewHolder.mStoreAddressView = (TextView) convertView
					.findViewById(R.id.custom_layout_favorites_offer_store_address);
			viewHolder.mDistanceView = (TextView) convertView
					.findViewById(R.id.custom_layout_favorites_offer_store_distance);
			viewHolder.mSharedUserNameView = (TextView) convertView
					.findViewById(R.id.custom_layout_shared_with_me_user_name);
			viewHolder.mOverFlowView = (ImageView) convertView
					.findViewById(R.id.custom_layout_offers_ic_overflow_menu);
			viewHolder.mFavoriteCheckBox = (CheckBox) convertView
					.findViewById(R.id.custom_layout_offers_toggle_favorites);
			viewHolder.viewConvertLayout = (LinearLayout) convertView
					.findViewById(R.id.convert_layout);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final HashMap<String, String> hashMap = getItem(position);
		viewHolder.mOfferTitleView.setText(hashMap.get("alert_title"));
		
		// BEGIN suppress zero price list items
		//viewHolder.mPriceView.setText("$" + hashMap.get("alert_price"));
		String alertPrice = hashMap.get("alert_price"); 
		if (!alertPrice.equalsIgnoreCase("0")){
			viewHolder.mPriceView.setText(Constants.CURRENCYSYMBOL + hashMap.get("alert_price"));
		} else{
			viewHolder.mPriceView.setText("");
		}
		// END suppress zero price list items

		
		viewHolder.mDistanceView.setText(hashMap.get("distance"));
		viewHolder.storeNameView.setText(hashMap.get("store_name"));
		viewHolder.mStoreAddressView.setText(hashMap.get("alert_message"));
		if (isFromSharedWithMe) {
			viewHolder.mSharedUserNameView.setText("From: "+hashMap
					.get("shared_by_consumer_name")+":");
		} else {
			viewHolder.mSharedUserNameView.setVisibility(View.GONE);
		}
		imageLoader.displayImage(hashMap.get("alert_image_url"),
				viewHolder.mOfferLogoView, configureOptions());
		viewHolder.mFavoriteCheckBox.setChecked(Boolean.valueOf(hashMap
				.get("consumer_favorite")));

		viewHolder.mFavoriteCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(
							final CompoundButton buttonView,
							final boolean isChecked) {
						BuyopicNetworkServiceManager buyopicNetworkServiceManager = BuyopicNetworkServiceManager
								.getInstance(mContext);
						buyopicNetworkServiceManager.sendFavoriteRequest(
								Constants.REQUEST_FAVORITE,
								buyOpic.getmConsumerId(),
								hashMap.get("alert_id"), isChecked,
								new BuyopicNetworkCallBack() {

									@Override
									public void onSuccess(int requestCode,
											Object object) {
										boolean status = JsonResponseParser
												.parseFavoriteResponse((String) object);
										if (status) {
											buttonView.setChecked(isChecked);
											hashMap.put("consumer_favorite",
													String.valueOf(isChecked));
										}
									}

									@Override
									public void onFailure(int requestCode,
											String message) {

									}
								});
					};
				});
		viewHolder.mOverFlowView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PopupMenu popupMenu = new PopupMenu(mContext, v);
				
				try {
					Field[] fields = popupMenu.getClass().getDeclaredFields();
					for (Field field : fields) {
						if ("mPopup".equals(field.getName())) {
							field.setAccessible(true);
							Object menuPopupHelper = field.get(popupMenu);
							Class<?> classPopupHelper = Class
									.forName(menuPopupHelper.getClass()
											.getName());
							Method setForceIcons = classPopupHelper.getMethod(
									"setForceShowIcon", boolean.class);
							setForceIcons.invoke(menuPopupHelper, true);
							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				popupMenu.getMenuInflater().inflate(R.menu.menu_shared,
						popupMenu.getMenu());
				popupMenu.getMenu().add(0, 1, 0, "Call "+hashMap.get("store_name")).setIcon(R.drawable.ic_phone_icon);
				popupMenu.show();
				popupMenu
						.setOnMenuItemClickListener(new OnMenuItemClickListener() {

							@Override
							public boolean onMenuItemClick(MenuItem item) {
								if (item.getItemId() == R.id.share) {
									sendShareStoreAlertRequestToServer(
											hashMap.get("alert_id"),
											buyOpic.getmConsumerId());
									Utils.shareOffer(mContext, hashMap.get("alert_id"), buyOpic.getmConsumerId(), hashMap.get("posted_consumer_id"), hashMap.get("store_id"), hashMap.get("retailer_id"));

								} else if (item.getItemId() == R.id.delete) {
									BuyopicNetworkServiceManager buyopicNetworkServiceManager = BuyopicNetworkServiceManager
											.getInstance(mContext);
									buyopicNetworkServiceManager
											.sendDeleteItemFromSharedWithMeRequest(
													Constants.REQUEST_DELETE_SHARE_ITEM,
													isFromSharedWithMe,
													buyOpic.getmConsumerId(),
													hashMap.get("alert_id"),
													new BuyopicNetworkCallBack() {

														@Override
														public void onSuccess(
																int requestCode,
																Object object) {
															String string = JsonResponseParser
																	.parseCreateAlertResponse((String) object);
															if (string
																	.equalsIgnoreCase("Alert deleted from shared offers list.")) {
																mSharesWithMe
																		.remove(hashMap);
																CustomSharedWithMeFragmentAdapter.this
																		.notifyDataSetChanged();
															}
														}

														@Override
														public void onFailure(
																int requestCode,
																String message) {

														}
													});
								}
								else
								{
									if(hashMap.get("posted_consumer_id")!=null && !hashMap.get("posted_consumer_id").equalsIgnoreCase("null"))
									{
									Utils.callPhone(mContext, hashMap.get("posted_by_consumer_phone_number"));
									}
									else
									{
										Utils.callPhone(mContext, hashMap.get("store_associate_phone_no"));
									}
								}
								return true;
							}

						});
			}
		});

		if (hashMap.containsKey("isinbeaconrange")) {
			
			if (hashMap
					.get("isinbeaconrange").equalsIgnoreCase("true")) {
				viewHolder.viewConvertLayout.setBackgroundColor(Color
						.parseColor("#fff98f"));
			} else {
				viewHolder.viewConvertLayout
						.setBackgroundColor(Color.TRANSPARENT);
			}
		}
		return convertView;
	}

	protected void sendShareStoreAlertRequestToServer(String mOfferId,
			String consumerId) {
		BuyopicNetworkServiceManager buyopicNetworkServiceManager = BuyopicNetworkServiceManager
				.getInstance(mContext);
		buyopicNetworkServiceManager
				.sendShareStoreAlertRequest(
						Constants.REQUEST_SHARE_STORE_ALERT, consumerId,
						mOfferId, this);
	}

	static class ViewHolder {
		private TextView storeNameView;
		private TextView mPriceView;
		private TextView mOfferTitleView;
		private TextView mStoreAddressView;
		private TextView mDistanceView;
		private ImageView mOverFlowView;
		private CheckBox mFavoriteCheckBox;
		private ImageView mOfferLogoView;
		private TextView mSharedUserNameView;
		private LinearLayout viewConvertLayout;

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

	@Override
	public void onSuccess(int requestCode, Object object) {

	}

	@Override
	public void onFailure(int requestCode, String message) {

	}

}
