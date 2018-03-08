package com.buyopic.android.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.buyopic.android.beacon.R;

public class CustomMapListAdapter extends ArrayAdapter<String> {
	private Context mContext;
	ViewHolder holder = null;
	private List<String> mPlacessList;

	public CustomMapListAdapter(Context context, List<String> objects) {
		super(context, -1, objects);
		this.mContext = context;
		this.mPlacessList = objects;
	}

	@Override
	public int getCount() {
		return mPlacessList.size();
	}

	@Override
	public String getItem(int position) {
		return super.getItem(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.map_list_item, null);
			holder = new ViewHolder();
			holder.mMapListItemText = (TextView) convertView
					.findViewById(R.id.maplistviewtext);
			holder.mMapPinImgView = (ImageView) convertView
					.findViewById(R.id.maplistviewimg);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final String placesList = getItem(position);
		holder.mMapListItemText.setText(placesList);
		holder.mMapPinImgView.setVisibility(View.INVISIBLE);
		if(position==0)
		holder.mMapPinImgView.setVisibility(View.VISIBLE);

		return convertView;
	}

	static class ViewHolder {

		public ImageView mMapPinImgView;
		public TextView mMapListItemText;
	}

}
