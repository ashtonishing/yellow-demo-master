package com.buyopic.android.radius;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buyopic.android.beacon.R;
import com.buyopic.android.models.Alert;
import com.buyopic.android.models.AlertDetail;
import com.buyopic.android.network.BuyopicNetworkCallBack;
import com.buyopic.android.network.BuyopicNetworkServiceManager;
import com.buyopic.android.network.JsonResponseParser;
import com.buyopic.android.utils.Constants;
import com.buyopic.android.utils.DateTimePicker;
import com.buyopic.android.utils.FinishActivityReceiver;
import com.buyopic.android.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class CreateNewListingActivity extends BaseActivity implements
		OnClickListener, BuyopicNetworkCallBack {

	private EditText mOfferTitle;
	private TextView mOfferPriceText;
	private TextView mCurrencySymbol;
	private EditText mOfferDescription;
	private EditText mOfferPrice;
	private TextView mValidFromText;
	private TextView mUntilText;
	private EditText mOfferValid;
	private EditText mOfferValidTo;
	private CheckBox mActivateView;
	private ImageView mProductImage;
	protected String mCurrentFilePath;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private ProgressDialog mProgressDialog;
	private BuyOpic buyOpic;

	protected static final int GALLERY_PICTURE = 1000;
	protected static final int CAMERA_PICTURE = 1001;
	public static final int SCALE_WIDTH = 500;
	public static final int SCALE_HEIGHT = 500;
	
	private static final int START_DATE = 1;
	private static final int END_DATE = 2;
	
	private long offerStartTime = -1;
	private TextView mConsumerName;
	private TextView mConsumerAddress;
	private BuyopicNetworkServiceManager buyopicNetworkServiceManager;
	private boolean isInEditMode=false;
	private String offerId;
	private String mThumbnailPath;
	private ImageView mConsumerProfilePic;
	private FinishActivityReceiver activityReceiver;
	private Button mSubmitButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerReceiver();
		View view=LayoutInflater.from(this).inflate(R.layout.layout_create_offer, null);
		setContentView(view);
		Utils.overrideFonts(this,view);
		buyOpic = (BuyOpic) getApplication();
		prepareViews();
		buyopicNetworkServiceManager=BuyopicNetworkServiceManager.getInstance(this);
		Bundle bundle=getIntent().getExtras();
		if(bundle!=null && bundle.containsKey("offer_id"))
		{
			offerId=bundle.getString("offer_id");
			buyopicNetworkServiceManager.sendConsumerAlertDetailsPostedByMeRequest(Constants.REQUEST_CONSUMER_ALERT_DETAILS, buyOpic.getmConsumerId(),
					offerId, this);
		}
	}
	
	public void registerReceiver() {
		IntentFilter filter = new IntentFilter(Constants.CUSTOM_ACTION_INTENT);
		activityReceiver = new FinishActivityReceiver(this);
		registerReceiver(activityReceiver, filter);
	}

	public void unregisterReceiver() {
		if (activityReceiver != null) {
			unregisterReceiver(activityReceiver);
			activityReceiver = null;
		}
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver();
	}
	
	
	private void bindAlertDetailsToViews(Alert alert) {
		if(alert!=null)
		{
			mOfferTitle.setText(alert.getmOfferTitle());
			mOfferPrice.setText(alert.getmPrice());
			mOfferDescription.setText(alert.getmOfferMessage());
			mOfferValid.setText(alert.getmStartDate());
			mOfferValidTo.setText(alert.getmEndDate());
			mActivateView.setChecked(alert.ismIsActivated());
			imageLoader.displayImage(alert.getmThumbnailUrl(), mProductImage, configureOptions());
			mCurrentFilePath=alert.getmThumbnailUrl();
			isInEditMode=true;
			mThumbnailPath=alert.getmThumbnailUrl();
			BaseActivity baseActivity = this;
			if (baseActivity != null) {
				baseActivity.setBeaconActionBar("Edit Listing", 3);
			}
		}
		
	}
	private void prepareViews() {
		String postText = "<font color='#EE0000'>*</font>";
		mOfferTitle = (EditText) findViewById(R.id.layout_create_offer_title);
		String preText = "Offer Name";
		String countryname=Utils.getCountryName(getBaseContext()); 
		mOfferTitle.setHint(Html.fromHtml(preText + postText));

		mOfferPriceText = (TextView) findViewById(R.id.offerpricetext);
		preText = "Price:";
		mOfferPriceText.setText(Html.fromHtml(preText + postText));
		
		mCurrencySymbol=(TextView) findViewById(R.id.currencysymbol);
		if(countryname!=null&&countryname.equalsIgnoreCase("IN")){
			Constants.CURRENCYSYMBOL=this.getResources().getString(R.string.rs);
		}
		else if(countryname!=null&&countryname.equalsIgnoreCase("US")){
			Constants.CURRENCYSYMBOL=this.getResources().getString(R.string.dollar);
		}
		mCurrencySymbol.setText(Constants.CURRENCYSYMBOL);

		mOfferDescription = (EditText) findViewById(R.id.layout_create_offer_description);
		mOfferPrice = (EditText) findViewById(R.id.layout_create_offer_price);
		

		mValidFromText = (TextView) findViewById(R.id.validfromtext);
		mValidFromText.setText(Html.fromHtml("Valid From:" + postText));

		mUntilText = (TextView) findViewById(R.id.untiltext);
		mUntilText.setText(Html.fromHtml("Until:" + postText));

		mOfferValid = (EditText) findViewById(R.id.layout_create_offer_valid_from);
		mOfferValidTo = (EditText) findViewById(R.id.layout_create_offer_valid_to);
		mActivateView = (CheckBox) findViewById(R.id.layout_create_offer_checkbox_activate);
		mProductImage = (ImageView) findViewById(R.id.layout_create_offer_product_image);
		mProductImage.setOnClickListener(this);

		mSubmitButton=(Button) findViewById(R.id.layout_create_offer_submit_button);
		mSubmitButton.setOnClickListener(this);
		findViewById(R.id.layout_create_offer_cancel_button)
				.setOnClickListener(this);
		mOfferValid.setOnClickListener(this);
		mOfferValidTo.setOnClickListener(this);
		BaseActivity baseActivity = this;
		if (baseActivity != null) {
			baseActivity.setBeaconActionBar("Create New Listing",4);
		}
		
		mConsumerName=(TextView)findViewById(R.id.layout_offer_desc_consumer_name);
		mConsumerAddress=(TextView)findViewById(R.id.layout_offer_desc_consumer_address);
		mConsumerProfilePic=(ImageView)findViewById(R.id.custom_layout_home_list_view_store_logo);
		mConsumerAddress.setText(buyOpic.getmConsumerAddress());
		mConsumerName.setText(buyOpic.getmConsumerName());
		imageLoader.displayImage(buyOpic.getmConsumerProfilePic(), mConsumerProfilePic, configureOptions());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_create_offer_submit_button:
			submitDataToServer();
			break;
		case R.id.layout_create_offer_cancel_button:
			finish();
			break;
		case R.id.layout_create_offer_product_image:
			startDialog();
			break;
		case R.id.layout_create_offer_valid_from:
			showDateTimeDialog((EditText) v, START_DATE);
			break;
		case R.id.layout_create_offer_valid_to:
			showDateTimeDialog((EditText) v, END_DATE);
			break;
		default:
			break;
		}
	}

	private void showDateTimeDialog(final EditText edittext, final int which) {
		// Create the dialog
		final Dialog mDateTimeDialog = new Dialog(this);
		// Inflate the root layout
		final RelativeLayout mDateTimeDialogView = (RelativeLayout) getLayoutInflater()
				.inflate(R.layout.date_time_dialog, null);
		// Grab widget instance
		final DateTimePicker mDateTimePicker = (DateTimePicker) mDateTimeDialogView
				.findViewById(R.id.DateTimePicker);
		// Check is system is set to use 24h time (this doesn't seem to work as
		// expected though)
		final String timeS = android.provider.Settings.System.getString(
				getContentResolver(),
				android.provider.Settings.System.TIME_12_24);
		final boolean is24h = !(timeS == null || timeS.equals("12"));

		// Update demo TextViews when the "OK" button is clicked
		((Button) mDateTimeDialogView.findViewById(R.id.SetDateTime))
				.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						mDateTimePicker.clearFocus();

						String dateTime = ((mDateTimePicker.get(Calendar.MONTH)) + 1)
								+ "/"
								+ mDateTimePicker.get(Calendar.DAY_OF_MONTH)
								+ "/"
								+ mDateTimePicker.get(Calendar.YEAR)
								+ " "
								+ mDateTimePicker.get(Calendar.HOUR)
								+ ":"
								+ mDateTimePicker.get(Calendar.MINUTE)
								+ " "
								+ (mDateTimePicker.get(Calendar.AM_PM) == Calendar.AM ? "AM"
										: "PM");

						Calendar calendar = Calendar.getInstance();
						String currentDateTime = (calendar.get(Calendar.MONTH) + 1)
								+ "/"
								+ calendar.get(Calendar.DAY_OF_MONTH)
								+ "/"
								+ calendar.get(Calendar.YEAR)
								+ " "
								+ calendar.get(Calendar.HOUR)
								+ ":"
								+ calendar.get(Calendar.MINUTE)
								+ " "
								+ (calendar.get(Calendar.AM_PM) == 0 ? "AM"
										: "PM");
						if (which == START_DATE) {
							offerStartTime = convertDateToMilliSeconds(dateTime);
							boolean isValidDate = compareDates(offerStartTime,
									convertDateToMilliSeconds(currentDateTime));
							if (isValidDate) {
								edittext.setText(dateTime);
								mDateTimeDialog.dismiss();
							} else {
								Utils.showToast(
										CreateNewListingActivity.this,
										"Invalid Time");
							}
						} else if (which == END_DATE) {
							boolean isValidDate = compareDates(
									convertDateToMilliSeconds(dateTime),
									offerStartTime);
							if (isValidDate) {
								edittext.setText(dateTime);
								mDateTimeDialog.dismiss();
							} else {
								Utils.showToast(
										CreateNewListingActivity.this,
										"Invalid Time");
							}
						}
					}
				});

		// Cancel the dialog when the "Cancel" button is clicked
		((Button) mDateTimeDialogView.findViewById(R.id.CancelDialog))
				.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						// TODO Auto-generated method stub
						mDateTimeDialog.cancel();
					}
				});

		// Reset Date and Time pickers when the "Reset" button is clicked
		((Button) mDateTimeDialogView.findViewById(R.id.ResetDateTime))
				.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						// TODO Auto-generated method stub
						mDateTimePicker.reset();
					}
				});

		// Setup TimePicker
		mDateTimePicker.setIs24HourView(is24h);
		// No title on the dialog window
		mDateTimeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Set the dialog content view
		mDateTimeDialog.setContentView(mDateTimeDialogView);
		// Display the dialog
		mDateTimeDialog.show();
	}

	private long convertDateToMilliSeconds(String date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"MM/dd/yyyy hh:mm a", Locale.US);
		try {
			Date convertedDate = dateFormat.parse(date);
			return convertedDate.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return -1;
	}

	private boolean compareDates(long start, long end) {
		if (end != -1) {
			return end <= start;
		} else {
			Utils.showToast(this, "Plese give offer start Time");
			return false;
		}
	}
	private void showProgressDialog() {

		if (mProgressDialog == null) {
			mProgressDialog = ProgressDialog.show(this, "", "Please wait",
					false, false);
		}
		mProgressDialog.show();
	}

	private void dismissProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	private void submitDataToServer() {
		if (!isEmpty(mOfferTitle) && !isEmpty(mOfferDescription)
				&& !isEmpty(mOfferPrice) && !isEmpty(mOfferValid)
				&& !isEmpty(mOfferValidTo)) {
			if (mCurrentFilePath != null) {
				
				DecimalFormat decimalFormat = new DecimalFormat("#0.00");
				String mPrice = decimalFormat.format(Double.valueOf(mOfferPrice
						.getText().toString()));
				BuyopicNetworkServiceManager buyopicNetworkServiceManager = BuyopicNetworkServiceManager
						.getInstance(this);
				String imageString = null;
				if (mCurrentFilePath.trim().startsWith("https:")) {
					imageString = mCurrentFilePath;
				} else {
					imageString = getEncodedImageString();
				}

				String imageName = new File(mCurrentFilePath).getName();
				
				long startMillis=convertDateToMilliSeconds(mOfferValid.getText().toString());
				long endTimeMillis=convertDateToMilliSeconds(mOfferValidTo.getText().toString());
	
				if(startMillis<endTimeMillis)
				{
					mSubmitButton.setEnabled(false);
					showProgressDialog();
				buyopicNetworkServiceManager
						.sendConsumerCreateOffersDataRequest(
								Constants.REQUEST_CREATE_CONSUMER_ALERT,
								offerId, isInEditMode,
								buyOpic.getmConsumerId(), mOfferTitle.getText()
										.toString(), mOfferDescription
										.getText().toString(), mOfferValid
										.getText().toString(), mOfferValidTo
										.getText().toString(), mPrice,
								mActivateView.isChecked() ? "active"
										: "inactive", imageName, imageString,
								mThumbnailPath, this);
				Utils.showLog( "Sent consumer Create offer to Server");
				}
				else
				{
					Utils.showToast(this, "Start Time must be less than End Time");
				}
			} else {
				Utils.showToast(this, "Please select the product Picture");
			}
		} else {
			Utils.showToast(this, "Please fill all the fields");
		}
	}

	private void startDialog() {

		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
		myAlertDialog.setTitle("Upload");
		myAlertDialog.setMessage("How do you want to add the Picture?");

		myAlertDialog.setPositiveButton("Gallery",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface arg0, int arg1) {
						Intent pictureActionIntent = new Intent(
								Intent.ACTION_GET_CONTENT, null);
						pictureActionIntent.setType("image/*");
						pictureActionIntent.putExtra("return-data", true);
						startActivityForResult(Intent.createChooser(
								pictureActionIntent, "Select Picture"),
								GALLERY_PICTURE);
					}
				});

		myAlertDialog.setNegativeButton("Camera",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {

						Intent pictureActionIntent = new Intent(
								android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
						File f = createImageFile();
						mCurrentFilePath = f.getAbsolutePath();
						pictureActionIntent.putExtra(MediaStore.EXTRA_OUTPUT,
								Uri.fromFile(f));
						startActivityForResult(pictureActionIntent,
								CAMERA_PICTURE);
					}
				});
		myAlertDialog.show();
	}

	private boolean isEmpty(EditText editText) {
		return TextUtils.isEmpty(editText.getText().toString().trim());
	}

	private File createImageFile() {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
				.format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

		File image = null;
		try {
			image = File.createTempFile(imageFileName, /* prefix */
					".jpg", /* suffix */
					storageDir /* directory */
			);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return image;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case GALLERY_PICTURE:
				Uri selectedImageUri = data.getData();
				mCurrentFilePath = getPath(selectedImageUri);

			case CAMERA_PICTURE:
				if (mCurrentFilePath != null) {
					imageLoader.displayImage("file:///" + mCurrentFilePath,
							mProductImage, configureOptions());
				}

				break;
			}
		}
	}

	private String getPath(Uri selectedImageUri) {
		String[] projection = new String[] { MediaStore.Images.Media.DATA };
		String path = null;
		CursorLoader cursorLoader = new CursorLoader(this, selectedImageUri,
				projection, null, null, null);
		Cursor cursor = cursorLoader.loadInBackground();
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			path = cursor.getString(cursor.getColumnIndex(projection[0]));
			cursor.close();
		}
		return path;
	}

	private String getEncodedImageString() {
		if (mCurrentFilePath != null) {
			byte[] byteArray = decodeSampledBitmapFromFile();
			return byteArray != null ? Base64.encodeToString(byteArray,
					Base64.URL_SAFE) : "Unable to Encode Image With Base64";
		}
		return "Unable to Encode Image With Base64";

	}

	private int getRotation() {
		ExifInterface exif;
		try {
			exif = new ExifInterface(mCurrentFilePath);

			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			switch (orientation) {

			case ExifInterface.ORIENTATION_ROTATE_270:
				return 270;
			case ExifInterface.ORIENTATION_ROTATE_180:
				return 180;
			case ExifInterface.ORIENTATION_ROTATE_90:
				return 90;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private byte[] convertFileIntoBase64() {

		if (mCurrentFilePath != null) {
			File file = new File(mCurrentFilePath);
			byte[] data = new byte[(int) file.length()];
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			FileInputStream fileInputStream = null;

			try {

				int nRead;
				fileInputStream = new FileInputStream(file);
				while ((nRead = fileInputStream.read(data, 0, data.length)) != -1) {
					buffer.write(data, 0, nRead);
				}
				buffer.flush();
				return buffer.toByteArray();

			} catch (FileNotFoundException e) {
				System.out.println("File Not Found.");
				e.printStackTrace();
			} catch (IOException e1) {
				System.out.println("Error Reading The File.");
				e1.printStackTrace();
			} finally {
				try {
					if (fileInputStream != null) {
						fileInputStream.close();
					}
				} catch (IOException ioe) {

				}
			}
		}
		return null;

	}

	private DisplayImageOptions configureOptions() {
		return new DisplayImageOptions.Builder()
				.showImageOnLoading(android.R.color.transparent)
				.showImageForEmptyUri(android.R.color.transparent)
				.imageScaleType(ImageScaleType.EXACTLY).cacheInMemory(true)
				.considerExifParams(true)
				.showImageOnFail(R.drawable.ic_placeholder_image)
				.cacheOnDisc(true).build();
	}

	public synchronized byte[] decodeSampledBitmapFromFile() {

		byte[] bytes = convertFileIntoBase64();
		if (bytes != null && bytes.length > 0) {
			// First decode with inJustDecodeBounds=true to check dimensions
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, SCALE_WIDTH,
					SCALE_HEIGHT);
			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0,
					bytes.length, options);
			Matrix matrix = new Matrix();
			matrix.postRotate(getRotation());
			Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0,
					bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			rotated.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] byteArray = stream.toByteArray();

			return byteArray;
		}
		return null;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
			final float totalPixels = width * height;

			// Anything more than 2x the requested pixels we'll sample down
			// further.
			final float totalReqPixelsCap = reqWidth * reqHeight * 2;

			while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
				inSampleSize++;
			}
		}
		return inSampleSize;
	}

	@Override
	public void onSuccess(int requestCode, Object object) {
		dismissProgressDialog();
		switch (requestCode) {
		case Constants.REQUEST_CREATE_CONSUMER_ALERT:
			mSubmitButton.setEnabled(true);
			String message=JsonResponseParser.parseCreateAlertResponse((String)object);
			if (message.equalsIgnoreCase("Listing posted successfully.")
					
					|| message
							.equalsIgnoreCase("Listing updated successfully.")) {
				Intent intent = new Intent(this,
						MyListingsActivity.class);
				startActivity(intent);
				finish();
			}
			Utils.showToast(this, message);
			break;
		case Constants.REQUEST_CONSUMER_ALERT_DETAILS:
			AlertDetail alert=JsonResponseParser.parseConsumerAlertDetail((String)object);
			if(alert!=null)
			{
			bindAlertDetailsToViews(alert.getAlert());
			}
		default:
			break;
		}

	}

	

	@Override
	public void onFailure(int requestCode, String message) {
		dismissProgressDialog();
		Utils.showToast(this, message);
	}

}
