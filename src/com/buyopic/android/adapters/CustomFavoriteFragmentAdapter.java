package com.buyopic.android.adapters;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import com.buyopic.android.models.StoreInfo;
import com.buyopic.android.network.BuyopicNetworkCallBack;
import com.buyopic.android.network.BuyopicNetworkServiceManager;
import com.buyopic.android.network.JsonResponseParser;
import com.buyopic.android.radius.BuyOpic;
import com.buyopic.android.utils.Constants;
import com.buyopic.android.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class CustomFavoriteFragmentAdapter extends ArrayAdapter<StoreInfo> {

	private Context mContext;
	private List<StoreInfo> mFavorites;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private BuyOpic buyOpic;

	public CustomFavoriteFragmentAdapter(Context context, List<StoreInfo> objects,String mStoreName,String mPhoneNumber) {
		super(context, -1, objects);
		this.mContext = context;
		this.mFavorites = objects;
	}

	@Override
	public int getCount() {
		return mFavorites.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder=null;
		if(convertView==null)
		{
			viewHolder=new ViewHolder();
			convertView=LayoutInflater.from(mContext).inflate(R.layout.custom_layout_favorites, null);
			Utils.overrideFonts(mContext, convertView);
			viewHolder.mOfferLogoView=(ImageView) convertView.findViewById(R.id.custom_layout_favorites_offer_image);
			viewHolder.mOfferTitleView=(TextView)convertView.findViewById(R.id.custom_layout_favorites_offer_title);
			viewHolder.mPriceView=(TextView)convertView.findViewById(R.id.custom_layout_favorites_offer_price);
			viewHolder.storeNameView=(TextView)convertView.findViewById(R.id.custom_layout_favorites_offer_store_name);
			viewHolder.mStoreAddressView=(TextView)convertView.findViewById(R.id.custom_layout_favorites_offer_store_address);
			viewHolder.mDistanceView=(TextView)convertView.findViewById(R.id.custom_layout_favorites_offer_store_distance);
			viewHolder.mOverFlowView=(ImageView)convertView.findViewById(R.id.custom_layout_offers_ic_overflow_menu);
			viewHolder.mFavoriteCheckBox=(CheckBox)convertView.findViewById(R.id.custom_layout_offers_toggle_favorites);
			viewHolder.mConvertLayoutView=(LinearLayout)convertView.findViewById(R.id.convert_layout);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		
		final StoreInfo storeInfo=getItem(position);
		
		viewHolder.mOfferTitleView.setText(storeInfo.getmTitle());
		

		// BEGIN suppress zero price list items
		//viewHolder.mPriceView.setText("$"+storeInfo.getmPrice());
		if (!storeInfo.getmPrice().equalsIgnoreCase("0.00")){
			viewHolder.mPriceView.setText(Constants.CURRENCYSYMBOL+storeInfo.getmPrice());
		} else{
			viewHolder.mPriceView.setText("");
		}
		// END suppress zero price list items
		
		
		viewHolder.mDistanceView.setText(storeInfo.getDistanceValue());
		viewHolder.storeNameView.setText(storeInfo.getmStoreName());
		viewHolder.mStoreAddressView.setText(storeInfo.getmDescription());
		buyOpic=(BuyOpic) mContext.getApplicationContext();
		
		imageLoader.displayImage(storeInfo.getmProductLogo(), viewHolder.mOfferLogoView,configureOptions());
		viewHolder.mFavoriteCheckBox.setChecked(storeInfo.isFavorite());
		/*if (storeInfo.isInBeaconRange()) {
			
		} else {
			convertView.setBackgroundColor(Color.TRANSPARENT);
			viewHolder.mConvertLayoutView.setBackgroundColor(Color.TRANSPARENT);
		}*/
		viewHolder.mFavoriteCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(!isChecked)
					{
						BuyopicNetworkServiceManager buyopicNetworkServiceManager=BuyopicNetworkServiceManager.getInstance(mContext);
						buyopicNetworkServiceManager.sendFavoriteRequest(Constants.REQUEST_FAVORITE, buyOpic.getmConsumerId(), storeInfo.getmOfferId(), isChecked, new BuyopicNetworkCallBack() {
							
							@Override
							public void onSuccess(int requestCode, Object object) {
								boolean status=JsonResponseParser.parseFavoriteResponse((String)object);
								if(status)
								{
									mFavorites.remove(storeInfo);
									CustomFavoriteFragmentAdapter.this.notifyDataSetChanged();
								}
							}
							
							@Override
							public void onFailure(int requestCode, String message) {
								
							}
						});
					}
			}
		});
		viewHolder.mOverFlowView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				PopupMenu popupMenu=new PopupMenu(mContext, v);
				
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
				popupMenu.getMenuInflater().inflate(R.menu.menu_context,popupMenu.getMenu());
				popupMenu.getMenu().add(0, 1, 0, "Call "+storeInfo.getmStoreName()).setIcon(R.drawable.ic_phone_icon);
				popupMenu.show();
				popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						if(item.getItemId()==R.id.share)
						{
							shareThisItem(storeInfo);
						}
						else if(item.getItemId()==1)
						{
							Utils.callPhone(mContext, storeInfo.getmPhoneNumber());
						}
						return true;
					}

				});
			}
		});
		
		if(compareDates(storeInfo.getmEndDate()))
		{
			viewHolder.mConvertLayoutView.setBackgroundColor(Color.parseColor("#F2F2F2"));
		}
		else if(storeInfo.isInBeaconRange())
		{
			viewHolder.mConvertLayoutView.setBackgroundColor(Color
					.parseColor("#fff98f"));
			
		}
		else 
		{
			viewHolder.mConvertLayoutView.setBackgroundColor(Color.WHITE);
		}
		return convertView;
	}
	

	protected void sendRequestToServer(StoreInfo storeInfo) {
		
	}

	private void shareThisItem(StoreInfo storeInfo) {
		sendShareStoreAlertRequestToServer(storeInfo.getmOfferId(),
				buyOpic.getmConsumerId());
		Utils.shareOffer(mContext, storeInfo.getmOfferId(), buyOpic.getmConsumerId(), storeInfo.getmPostedAlertConsumerId(), storeInfo.getmStoreId(), storeInfo.getmRetailerId());
		
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

	
	static class ViewHolder
	{
		public LinearLayout mConvertLayoutView;
		private TextView storeNameView;
		private TextView mPriceView;
		private TextView mOfferTitleView;
		private TextView mStoreAddressView;
		private TextView mDistanceView;
		private ImageView mOverFlowView;
		private CheckBox mFavoriteCheckBox;
		private ImageView mOfferLogoView;
	}

	private static boolean compareDates(String endDate)
	{
		int compareTo=-1;
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US);
		try {
			Date d1=dateFormat.parse(endDate);
			String fo=dateFormat.format(new Date());
			Date d2=dateFormat.parse(fo);
			compareTo=d1.compareTo(d2);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(compareTo==-1)
		{
			return true;
		}
		return false;
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
