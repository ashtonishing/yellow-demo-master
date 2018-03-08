package com.buyopic.android.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.buyopic.android.beacon.R;
import com.buyopic.android.radius.BaseActivity;
import com.buyopic.android.radius.BuyOpic;
import com.buyopic.android.radius.ConsumerLoginActivity;
import com.buyopic.android.radius.ConsumerRegisterActivity;
import com.buyopic.android.radius.CreateNewListingActivity;
import com.buyopic.android.radius.HomePageSetupActivity;
import com.buyopic.android.radius.MyListingsActivity;
import com.buyopic.android.radius.MyOrdersActivity;
import com.buyopic.android.radius.SearchActivity;
import com.buyopic.android.utils.Constants;
import com.buyopic.android.utils.Utils;

public class SlideMenuOptionsFragment extends Fragment implements
		 OnClickListener {

	public static final String KEY_IS_FAVORITE = "is_favorite";
	public static final String KEY_IS_SHARED = "is_shared";
	private Context mContext;
	private BuyOpic buyOpic;
	private TextView loginView;

	@Override
	public void onResume() {
		super.onResume();
		buyOpic = (BuyOpic) mContext.getApplicationContext();
		if (buyOpic.getmConsumerRegistrationStatus()) {
			loginView.setText("Logout");
		} else {
			loginView.setText("Login");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_slidemenu_options, null);
		Utils.overrideFonts(mContext, view);

		view.findViewById(R.id.slidemenu_home).setOnClickListener(this);
		view.findViewById(R.id.slidemenu_search).setOnClickListener(this);
		view.findViewById(R.id.slidemenu_create_listing).setOnClickListener(
				this);
		view.findViewById(R.id.slidemenu_favorites).setOnClickListener(this);
		view.findViewById(R.id.slidemenu_shared).setOnClickListener(this);
		view.findViewById(R.id.slidemenu_mylistings).setOnClickListener(this);
		view.findViewById(R.id.slidemenu_myorders).setOnClickListener(this);
		view.findViewById(R.id.slidemenu_edit_profile).setOnClickListener(this);
		view.findViewById(R.id.slidemenu_send_us_feedback).setOnClickListener(this);
		loginView = (TextView) view.findViewById(R.id.slidemenu_logout);
		loginView.setOnClickListener(this);
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mContext = activity;
	}

	@Override
	public void onClick(View v) {

		if (getActivity() instanceof BaseActivity) {
			BaseActivity baseActivity = (BaseActivity) getActivity();
			baseActivity.closeMenu();
		}
		switch (v.getId()) {
		case R.id.slidemenu_home:
			startActivity(new Intent(mContext, HomePageSetupActivity.class));
			break;
		case R.id.slidemenu_create_listing:
			if (buyOpic.getmConsumerRegistrationStatus()) {
				startActivity(new Intent(mContext,
						CreateNewListingActivity.class));
			} else {
				startActivity(new Intent(mContext, ConsumerLoginActivity.class));
			}

			break;
		case R.id.slidemenu_favorites:
			Intent intent = new Intent(mContext, HomePageSetupActivity.class);
			intent.putExtra(KEY_IS_FAVORITE, true);
			startActivity(intent);
			break;
		case R.id.slidemenu_mylistings:
			if (buyOpic.getmConsumerRegistrationStatus()) {
				startActivity(new Intent(mContext, MyListingsActivity.class));
			} else {
				startActivity(new Intent(mContext, ConsumerLoginActivity.class));
			}
			break;
		case R.id.slidemenu_myorders:
			if (buyOpic.getmConsumerRegistrationStatus()) {
				startActivity(new Intent(mContext, MyOrdersActivity.class));
					}
					else{
						Intent intent1 =new Intent(mContext, ConsumerLoginActivity.class);
					intent1.putExtra("callingactivity", Constants.REQUEST_ORDERS_LOGIN);
					startActivity(intent1);
					}
			break;
		case R.id.slidemenu_search:
			startActivity(new Intent(mContext, SearchActivity.class));
			break;
		case R.id.slidemenu_logout:
			if (buyOpic.getmConsumerRegistrationStatus()) {
				buyOpic.setmConsumerRegistrationStatus(false);
				mContext.sendBroadcast(new Intent(Constants.CUSTOM_ACTION_INTENT));
				startActivity(new Intent(mContext, HomePageSetupActivity.class));
			} else {
				startActivity(new Intent(mContext, ConsumerLoginActivity.class));
			}
			break;
		case R.id.slidemenu_shared:

			intent = new Intent(mContext, HomePageSetupActivity.class);
			intent.putExtra(KEY_IS_SHARED, true);
			startActivity(intent);
			break;
		case R.id.slidemenu_send_us_feedback:
			displaydialog();
			break;
		case R.id.slidemenu_edit_profile:
			if(buyOpic.getmConsumerRegistrationStatus())
			{
				intent=new Intent(mContext,ConsumerRegisterActivity.class);
				intent.putExtra(ConsumerRegisterActivity.KEY_IS_FROM_UPDATE, true);
				startActivity(intent);
			}
			else
			{
				startActivity(new Intent(mContext, ConsumerLoginActivity.class));
			}
			break;
		default:
			break;
		}
	}

	
	private void displaydialog() {
		final Dialog dialog = new Dialog(getActivity(), R.style.Theme_Dialog);
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.layout_forgotpassword_dialog_view, null);
		Utils.overrideFonts(getActivity(), view);
		dialog.setContentView(view);
		TextView dialogHeader = (TextView) view
				.findViewById(R.id.dialog_header);
		dialogHeader.setText("Send us Feedback");
		TextView forgotPasswordText = (TextView) view
				.findViewById(R.id.forgot_password_text);
		String forgotPasswordResponseText = "We are obsessed with providing you with an absolutely great Yellow experience.\n"
				+ "Therefore, we would love to hear your feedback regarding your Yellow experience. Click on the link below to send us email:\n feedback@buyopic.com\n\n Thank you. The Buyopic Team";
		forgotPasswordText.setText(forgotPasswordResponseText);
		ImageButton cancelDialog = (ImageButton) view
				.findViewById(R.id.dialog_cancel);
		cancelDialog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();

	}
}
