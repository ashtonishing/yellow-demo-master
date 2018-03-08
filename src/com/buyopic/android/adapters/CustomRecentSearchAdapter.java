package com.buyopic.android.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.buyopic.android.beacon.R;
import com.buyopic.android.utils.Utils;

public class CustomRecentSearchAdapter extends ArrayAdapter<String> {

	private List<String> recentHistoryList;
	private Context mContext;

	public CustomRecentSearchAdapter(Context context, List<String> objects) {
		super(context, -1, objects);
		this.recentHistoryList = objects;
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return recentHistoryList.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (holder == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.layout_custom_recent_adapter_view, null);
			Utils.overrideFonts(mContext, convertView);
			holder.mRecentHistoryTextView = (TextView) convertView
					.findViewById(R.id.search_recent_history_text);
		}

		String string = getItem(position);
		holder.mRecentHistoryTextView.setText(string);
		return convertView;
	}

	static class ViewHolder {
		TextView mRecentHistoryTextView;

	}

}
