package com.buyopic.android.adapters;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.buyopic.android.beacon.R;
import com.buyopic.android.models.StoreInfo;
import com.buyopic.android.network.BuyopicNetworkCallBack;
import com.buyopic.android.network.BuyopicNetworkServiceManager;
import com.buyopic.android.network.GoogleStoreInfo;
import com.buyopic.android.network.JsonResponseParser;
import com.buyopic.android.radius.BuyOpic;
import com.buyopic.android.utils.Constants;
import com.buyopic.android.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class CustomSearchAdapter extends ArrayAdapter<StoreInfo> {

	private List<StoreInfo> storeAlertsList;
	private Context mContext;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private BuyOpic buyOpic;

	public CustomSearchAdapter(Context context, List<StoreInfo> objects) {
		super(context, -1, objects);
		this.storeAlertsList = objects;
		this.mContext = context;
		buyOpic = (BuyOpic) mContext.getApplicationContext();
	}

	@Override
	public int getCount() {
		return storeAlertsList.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (holder == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.custom_layout_favorites, null);
			Utils.overrideFonts(mContext, convertView);
			holder.mDistanceView = (TextView) convertView
					.findViewById(R.id.custom_layout_favorites_offer_store_distance);
			holder.mProductTitleView = (TextView) convertView
					.findViewById(R.id.custom_layout_favorites_offer_title);
			holder.mProductStoreNameView = (TextView) convertView
					.findViewById(R.id.custom_layout_favorites_offer_store_name);
			holder.mProductStoreAddressView = (TextView) convertView
					.findViewById(R.id.custom_layout_favorites_offer_store_address);
			holder.mPriceView = (TextView) convertView
					.findViewById(R.id.custom_layout_favorites_offer_price);
			holder.mProductLogo = (ImageView) convertView
					.findViewById(R.id.custom_layout_favorites_offer_image);
			holder.mOverFlowImageView = (ImageView) convertView
					.findViewById(R.id.custom_layout_offers_ic_overflow_menu);
			holder.mFavoriteCheckBoxView = (CheckBox) convertView
					.findViewById(R.id.custom_layout_offers_toggle_favorites);
			holder.mProgressBarBeneathImage = (ProgressBar) convertView
					.findViewById(R.id.searchProdImageProgBar);
			holder.mBottomLinearLayout = (LinearLayout) convertView
					.findViewById(R.id.bottomlinear);

		}

		final StoreInfo searchResult = getItem(position);
		if (searchResult.getmGoogleStoreInfo() == null
				|| searchResult.getmGoogleStoreInfo().size() == 0) {

			holder.mProductStoreNameView.setVisibility(View.VISIBLE);
			holder.mFavoriteCheckBoxView.setVisibility(View.VISIBLE);
			holder.mOverFlowImageView.setVisibility(View.VISIBLE);
			holder.mFavoriteCheckBoxView.setVisibility(View.VISIBLE);
			holder.mPriceView.setVisibility(View.VISIBLE);
			holder.mProgressBarBeneathImage.setVisibility(View.VISIBLE);
			holder.mBottomLinearLayout.setGravity(Gravity.CENTER);
			holder.mProductTitleView.setSelected(true);

			holder.mFavoriteCheckBoxView.setChecked(searchResult.isFavorite());
			holder.mDistanceView.setText(searchResult.getDistanceValue());
			holder.mProductStoreNameView.setText(searchResult.getmStoreName());
			holder.mProductStoreAddressView.setText(searchResult
					.getmDescription());
			holder.mProductTitleView.setText(searchResult.getmTitle() + ":");

			// BEGIN suppress zero price list items
			// holder.mPriceView.setText("$" + searchResult.getmPrice());
			if (!searchResult.getmPrice().equalsIgnoreCase("0")) {
				holder.mPriceView.setText(Constants.CURRENCYSYMBOL
						+ searchResult.getmPrice());
			} else {
				holder.mPriceView.setText("");
			}
			// END suppress zero price list items

			imageLoader.displayImage(searchResult.getmProductLogo(),
					holder.mProductLogo, configureOptions());

			if (searchResult.isInBeaconRange()) {
				convertView.setBackgroundColor(Color.parseColor("#fff98f"));
			} else {
				convertView.setBackgroundColor(Color.TRANSPARENT);
			}

			if (!searchResult.isInBeaconRange()) {
				convertView
						.setBackgroundResource(R.drawable.no_padding_edittext_grey_bg);
			} else {
				convertView
						.setBackgroundResource(R.drawable.four_border_bg_in_beacon_range);
			}

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
									buyOpic.getmConsumerId(),
									searchResult.getmOfferId(), isChecked,
									new BuyopicNetworkCallBack() {

										@Override
										public void onSuccess(int requestCode,
												Object object) {
											boolean status = JsonResponseParser
													.parseFavoriteResponse((String) object);
											if (status) {
												buttonView
														.setChecked(isChecked);
												searchResult
														.setFavorite(isChecked);
											}
										}

										@Override
										public void onFailure(int requestCode,
												String message) {

										}
									});
						}
					});
			holder.mOverFlowImageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					PopupMenu popupMenu = new PopupMenu(mContext, v);

					try {
						Field[] fields = popupMenu.getClass()
								.getDeclaredFields();
						for (Field field : fields) {
							if ("mPopup".equals(field.getName())) {
								field.setAccessible(true);
								Object menuPopupHelper = field.get(popupMenu);
								Class<?> classPopupHelper = Class
										.forName(menuPopupHelper.getClass()
												.getName());
								Method setForceIcons = classPopupHelper
										.getMethod("setForceShowIcon",
												boolean.class);
								setForceIcons.invoke(menuPopupHelper, true);
								break;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					popupMenu.getMenuInflater().inflate(R.menu.menu_context,
							popupMenu.getMenu());
					popupMenu
							.getMenu()
							.add(0, 1, 0,
									"Call " + searchResult.getmStoreName())
							.setIcon(R.drawable.ic_phone_icon);
					popupMenu.show();
					popupMenu
							.setOnMenuItemClickListener(new OnMenuItemClickListener() {

								@Override
								public boolean onMenuItemClick(MenuItem item) {
									if (item.getItemId() == R.id.share) {
										shareThisItem(searchResult);
									} else {
										Utils.callPhone(mContext,
												searchResult.getmPhoneNumber());
									}
									return true;
								}

							});
				}
			});
		} else if (searchResult.getmGoogleStoreInfo() != null
				&& searchResult.getmGoogleStoreInfo().size() > 0) {

			float width = mContext.getResources().getDimension(
					R.dimen.googleimage_width);
			float height = mContext.getResources().getDimension(
					R.dimen.googlimagechanged_height);
			float height1 = mContext.getResources().getDimension(
					R.dimen.googlimagesearch_height);
			convertView.setLayoutParams(new AbsListView.LayoutParams(
					AbsListView.LayoutParams.MATCH_PARENT, (int) height));
			FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
					(int) width, (int) height1);
			holder.mBottomLinearLayout.setGravity(Gravity.BOTTOM);
			holder.mProductStoreAddressView.setTextSize(mContext.getResources()
					.getDimension(R.dimen.tenspsize));
			holder.mProductTitleView.setTextSize(mContext.getResources()
					.getDimension(R.dimen.twelvespsize));
			holder.mDistanceView.setGravity(Gravity.BOTTOM);
			holder.mDistanceView.setTextSize(mContext.getResources()
					.getDimension(R.dimen.tenspsize));
			holder.mProductStoreNameView.setTextSize(mContext.getResources()
					.getDimension(R.dimen.tenspsize));

			holder.mProductLogo.setLayoutParams(layoutParams);
			holder.mProductTitleView.setSelected(true);
			holder.mProductStoreNameView.setVisibility(View.INVISIBLE);
			holder.mFavoriteCheckBoxView.setVisibility(View.GONE);
			holder.mOverFlowImageView.setVisibility(View.GONE);
			holder.mFavoriteCheckBoxView.setVisibility(View.GONE);
			holder.mPriceView.setVisibility(View.GONE);
			holder.mProgressBarBeneathImage.setVisibility(View.GONE);
			List<GoogleStoreInfo> mgooglestoreinfo = searchResult
					.getmGoogleStoreInfo();

			if (mgooglestoreinfo != null) {

				holder.mDistanceView.setText(Utils.convertMetrsAndFeets(
						mgooglestoreinfo.get(0).getmDistance(), mContext));

				holder.mProductStoreAddressView.setText(searchResult
						.getmGoogleStoreInfo().get(0).getmVicinity());

				if (searchResult.getmGoogleStoreInfo().get(0).getmName() != null
						&& !searchResult.getmGoogleStoreInfo().get(0)
								.getmName().isEmpty())
					holder.mProductTitleView.setText(searchResult
							.getmGoogleStoreInfo().get(0).getmName());

				imageLoader.displayImage(searchResult.getmGoogleStoreInfo()
						.get(0).getmIconURL(), holder.mProductLogo,
						configureOptions());

				convertView
						.setBackgroundResource(R.drawable.no_padding_googlelistitem_grey_bg);
			}
		}
		return convertView;
	}

	private void shareThisItem(StoreInfo storeInfo) {
		sendShareStoreAlertRequestToServer(storeInfo.getmOfferId(),
				buyOpic.getmConsumerId());
		Utils.shareOffer(mContext, storeInfo.getmOfferId(),
				buyOpic.getmConsumerId(),
				storeInfo.getmPostedAlertConsumerId(), storeInfo.getmStoreId(),
				storeInfo.getmRetailerId());

	}

	protected void sendShareStoreAlertRequestToServer(String mOfferId,
			String consumerId) {
		BuyopicNetworkServiceManager buyopicNetworkServiceManager = BuyopicNetworkServiceManager
				.getInstance(mContext);
		buyopicNetworkServiceManager.sendShareStoreAlertRequest(
				Constants.REQUEST_SHARE_STORE_ALERT, consumerId, mOfferId,
				new BuyopicNetworkCallBack() {

					@Override
					public void onSuccess(int requestCode, Object object) {

					}

					@Override
					public void onFailure(int requestCode, String message) {
						// TODO Auto-generated method stub
					}
				});
	}

	static class ViewHolder {
		LinearLayout mBottomLinearLayout;
		TextView mProductTitleView;
		TextView mProductStoreAddressView;
		TextView mProductStoreNameView;
		TextView mDistanceView;
		TextView mPriceView;
		ImageView mProductLogo;
		ImageView mOverFlowImageView;
		CheckBox mFavoriteCheckBoxView;
		ProgressBar mProgressBarBeneathImage;
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
