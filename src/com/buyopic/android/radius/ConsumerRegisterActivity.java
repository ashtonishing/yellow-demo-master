package com.buyopic.android.radius;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buyopic.android.beacon.R;
import com.buyopic.android.models.Consumer;
import com.buyopic.android.models.MapData;
import com.buyopic.android.network.BuyopicNetworkCallBack;
import com.buyopic.android.network.BuyopicNetworkServiceManager;
import com.buyopic.android.network.JsonResponseParser;
import com.buyopic.android.utils.Constants;
import com.buyopic.android.utils.FinishActivityReceiver;
import com.buyopic.android.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class ConsumerRegisterActivity extends BaseActivity implements
		OnClickListener, BuyopicNetworkCallBack {

	private EditText mMerchantName;
	//private EditText mStreet;
	//private EditText mCity;
	//private EditText mStateZipCode;
	private EditText mEmail;
	private EditText mPassword;
	private Button mRegisterButton;
	private ImageView mProductImage;
	private TextView loginButton;
	protected String mCurrentFilePath;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private ProgressDialog mProgressDialog;
	public static final String KEY_IS_FROM_UPDATE = "is_from_update";
	private BuyOpic buyOpic;
	private MapData mapData;
	private EditText mPhoneNumberView;
	private boolean isFromUpdate = false;
	private LinearLayout saveCancelLayout;
	private LinearLayout loginLabelLayout;
	private Button saveButton;
	private ImageView mShowMap;
	private Button cancelButton;
	private FinishActivityReceiver activityReceiver;
	private View view;
	private String addressImagePath;

	protected static final int GALLERY_PICTURE = 1000;
	protected static final int CAMERA_PICTURE = 1001;
	public static final int SCALE_WIDTH = 500;
	public static final int SCALE_HEIGHT = 500;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerReceiver();
		view = LayoutInflater.from(this).inflate(
				R.layout.layout_register_merchant, null);
		setContentView(view);
		Utils.overrideFonts(this, view);
		buyOpic = (BuyOpic) getApplication();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null && bundle.containsKey(KEY_IS_FROM_UPDATE)) {
			isFromUpdate = bundle.getBoolean(KEY_IS_FROM_UPDATE);
		}
		if (bundle != null) {
			mapData = (MapData) bundle.get("mapdata");
		}
		prepareViews();
		if (isFromUpdate) {
			bindDataToViews();
		}

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if(addressImagePath!=null){
		File externalFile = new File(addressImagePath);
		Uri uri = Uri.fromFile(externalFile);
		imageLoader.displayImage(uri.toString(),
				mShowMap);
		}

		super.onResume();
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
	
	private void bindDataToViews() {

		mMerchantName.setText(buyOpic.getmConsumerName());
	//	mStreet.setText(buyOpic.getmConsumerStreet());
	//	mCity.setText(buyOpic.getmConsumerCity());
	//	mStateZipCode.setText(buyOpic.getmConsumerState());
		mPhoneNumberView.setText(buyOpic.getmConsumerPhoneNumber());
		mEmail.setText(buyOpic.getmConsumerEmail());
		mPassword.setText(buyOpic.getmConsumerPassword());
		if (buyOpic.getmConsumerProfilePic() != null
				&& !TextUtils.isEmpty(buyOpic.getmConsumerProfilePic())) {
			imageLoader.displayImage(buyOpic.getmConsumerProfilePic(),
					mProductImage);
			mCurrentFilePath=buyOpic.getmConsumerProfilePic();
			Log.i("SRT","productimage url"+mCurrentFilePath);
		}
		//adressmap image
		if (buyOpic.getmConsumerUserAddressImageUrl()!= null
				&& !TextUtils.isEmpty(buyOpic.getmConsumerUserAddressImageUrl())) {
			Log.i("SRT","addressimage url"+buyOpic.getmConsumerUserAddressImageUrl());
			 imageLoader.displayImage(buyOpic.getmConsumerUserAddressImageUrl(),
					mShowMap);
		//	addressImagePath=buyOpic.getmConsumerUserAddressImageUrl();
		}
		saveCancelLayout.setVisibility(View.VISIBLE);
		loginLabelLayout.setVisibility(View.GONE);
		mRegisterButton.setVisibility(View.GONE);
	}

	private void prepareViews() {
		String postText = "<font color='#EE0000'>*</font>";

		mMerchantName = (EditText) findViewById(R.id.layout_register_merchant_name);
	//	mStreet = (EditText) findViewById(R.id.layout_register_street);
	//	mStreet.setHint(Html.fromHtml("Street" + postText));

		mPhoneNumberView = (EditText) findViewById(R.id.layout_register_phone_number);

		TextView mAddressLabel = (TextView) findViewById(R.id.address_label);
		mAddressLabel.setText(Html.fromHtml("Address:" + postText));
//		mCity = (EditText) findViewById(R.id.layout_register_city);
//		mCity.setHint(Html.fromHtml("City" + postText));

//		mStateZipCode = (EditText) findViewById(R.id.layout_register_city_zipcode);
//		mStateZipCode.setHint(Html.fromHtml("State, Zip" + postText));
		mEmail = (EditText) findViewById(R.id.layout_register_email);
		mPassword = (EditText) findViewById(R.id.layout_register_password);
		mRegisterButton = (Button) findViewById(R.id.layout_register_button);
		mRegisterButton.setOnClickListener(this);

		mProductImage = (ImageView) findViewById(R.id.layout_register_image);
		mProductImage.setOnClickListener(this);
		loginButton = (TextView) findViewById(R.id.layout_login_button);
		String str = "<html><body><u>Login Here</u></body></html>";
		loginButton.setText(Html.fromHtml(str));
		loginButton.setOnClickListener(this);
		BaseActivity baseActivity = this;
		if (!isFromUpdate) {
			baseActivity.setBeaconActionBar("Registration", 1);
		} else {
			baseActivity.setBeaconActionBar("Edit Profile", 2);
		}
		saveCancelLayout=(LinearLayout)findViewById(R.id.layout_save_cancel);
		loginLabelLayout=(LinearLayout)findViewById(R.id.layout_login_page);
		saveButton=(Button)findViewById(R.id.layout_register_save_button);
		saveButton.setOnClickListener(this);
		cancelButton=(Button)findViewById(R.id.layout_register_cancel_button);
		cancelButton.setOnClickListener(this);
		mShowMap = (ImageView) findViewById(R.id.layout_register_addressmap_image);
		mShowMap.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_register_button:
		
			submitDataToServer();
			break;
		case R.id.layout_register_image:
			startDialog();
			break;
		case R.id.layout_login_button:
			startActivity(new Intent(this, ConsumerLoginActivity.class));
			break;
			
		case R.id.layout_register_save_button:
			submitDataToServer();
			break;
		case R.id.layout_register_cancel_button:
			finish();
			break;
		case R.id.layout_register_addressmap_image:

			Intent intent = new Intent(this, ShowMapActivity.class);
		//	Bundle bundle = new Bundle();
		//	bundle.putString("username", mMerchantName.getText().toString());
		//	intent.putExtras(bundle);

			startActivityForResult(intent, Constants.RESULT_MAP);
			break;
		default:
			break;
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

	private boolean isEmpty(EditText editText) {

		return TextUtils.isEmpty(editText.getText().toString());
	}

	private void submitDataToServer() {
		if (!isEmpty(mEmail) && !isEmpty(mPassword) && !isEmpty(mMerchantName)
				&& !isEmpty(mPhoneNumberView)) {

			String imageName = "";
			String imageFile = "";
			if (mCurrentFilePath != null) {
				imageName = new File(mCurrentFilePath).getName();
				imageFile = getEncodedImageString(mCurrentFilePath);
			}

			if (mCurrentFilePath != null
					&& mCurrentFilePath.startsWith("https://")) {
				imageName = new File(mCurrentFilePath).getName();
				imageFile = mCurrentFilePath;
			}

			
			String addressImageName = "";
			String addressImageFile = "";
			if (addressImagePath != null) {
				addressImageName = new File(addressImagePath).getName();
				addressImageFile = getEncodedImageString(addressImagePath);
			}

			if (addressImagePath != null
					&& addressImagePath.startsWith("https://")) {
				addressImageName = new File(addressImagePath).getName();
				addressImageFile = addressImagePath;
			}

			if(imageName==null||imageFile==null||imageName.equalsIgnoreCase("")||imageFile.equalsIgnoreCase("")){
				if(buyOpic.getmConsumerProfilePic()!=null){

					imageName = new File(buyOpic.getmConsumerProfilePic()).getName();
					imageFile = buyOpic.getmConsumerProfilePic();
				
					
				}
				
			}
			if(addressImageFile==null||addressImageName==null||addressImageFile.equalsIgnoreCase("")||addressImageName.equalsIgnoreCase("")){
				if(buyOpic.getmConsumerUserAddressImageUrl()!=null){

					addressImageName = new File(buyOpic.getmConsumerUserAddressImageUrl()).getName();
					addressImageFile = buyOpic.getmConsumerUserAddressImageUrl();
				
					
				}
				
			}

			int requestCode = 0;
			if (isFromUpdate) {
				requestCode = Constants.REQUEST_CONSUMER_UPDATE;
			} else {
				requestCode = Constants.REQUEST_CONSUMER_REGISTRATION;
			}
			if (Utils.isValidEmail(mEmail)) {
				mRegisterButton.setEnabled(false);
				saveButton.setEnabled(false);
				showProgressDialog();
				Locale locale = Locale.getDefault();
				locale.getCountry();
				BuyopicNetworkServiceManager buyopicNetworkServiceManager = BuyopicNetworkServiceManager
						.getInstance(this);
				if (mapData == null) {
					
					

					buyopicNetworkServiceManager
							.sendConsumerRegistrationRequest(
									requestCode,
									isFromUpdate,
									mEmail.getText().toString(),
									mPassword.getText().toString(),
									mMerchantName.getText().toString(),
									String.valueOf(buyOpic.getmSourceLatitude()),
									String.valueOf(buyOpic.getmSourceLatitude()),
									buyOpic.getmConsumerAddress1(), 
									buyOpic.getmConsumerCity(), 
									buyOpic.getmConsumerState(), 
									buyOpic.getmConsumerCountry(), 
									buyOpic.getmConsumerId(), 
									imageName,
									imageFile, 
									mPhoneNumberView.getText().toString(), 
									buyOpic.getmConsumerGooglePlaceID(), 
									buyOpic.getmConsumerGoogleIconImage(),
									addressImageName,
									addressImageFile,
									buyOpic.getmConsumerAddress2(),
									buyOpic.getmConsumerPostalCode(),
									this);
				} 
				
				
				else {

					
					buyopicNetworkServiceManager
							.sendConsumerRegistrationRequest(requestCode,
									isFromUpdate,
									mEmail.getText().toString(),
									mPassword.getText().toString(),
									mMerchantName.getText().toString(), 
									mapData.getLatitude(), 
									mapData.getLongitude(),
									mapData.getmAddress1(), 
									mapData.getmCity(),
									mapData.getmState(), 
									mapData.getmCountry(), 
									buyOpic.getmConsumerId(), 
									imageName,
									imageFile,
									mPhoneNumberView.getText().toString(), 
									mapData.getPlaceid(),
									mapData.getIconurl(), 
									addressImageName,
									addressImageFile,
									mapData.getmAddress2(),
							        mapData.getmPostalCode(),
									this);
				
				}
			} else {
				Utils.showToast(this, "Please give a valid email address");
			}
		}

		else {
			Utils.showToast(this, "Some Fields are missing");
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
				if (selectedImageUri != null) {
					mCurrentFilePath = getPath(selectedImageUri);
				}

			case CAMERA_PICTURE:
				if (mCurrentFilePath != null) {
					imageLoader.displayImage("file:///" + mCurrentFilePath,
							mProductImage, configureOptions());
				}

				break;
			}
		} else if (resultCode == Constants.RESULT_MAP) {

			mapData = (MapData) data.getExtras().getSerializable("mapdata");
			addressImagePath= (String) data.getExtras().getSerializable("imagepath");
		//	mMerchantName.setActivated(false);
		//	mMerchantName.setFocusable(false);
			Log.i("map data is ", "map  " + mapData);
		/*	if (mapData != null) {
				if (mapData.getmStreet() == null) {
					mapData.setmStreet("");
				}
				if (mapData.getmPostalCode() == null) {
					mapData.setmPostalCode("");
				}
				if (mapData.getmCity() == null) {
					mapData.setmCity("");
				}
				if (mapData.getmCountry() == null) {
					mapData.setmCountry("");
				}
		*/		/*mAddress.setText(mapData.getmStreet() + "\n"
						+ mapData.getmPostalCode() + "\n" + mapData.getmCity()
						+ " " + mapData.getmCountry());*/

			//}
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

	private String getEncodedImageString(String path) {
		if (path != null) {
			byte[] byteArray = decodeSampledBitmapFromFile(path);
			return byteArray != null ? Base64.encodeToString(byteArray,
					Base64.URL_SAFE) : "Unable to Encode Image With Base64";
		}
		return "Unable to Encode Image With Base64";

	}

	private int getRotation(String path) {
		ExifInterface exif;
		try {
			exif = new ExifInterface(path);

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

	private byte[] convertFileIntoBase64(String path) {

		if (path != null) {
			File file = new File(path);
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
				.showImageForEmptyUri(R.drawable.ic_placeholder_image)
				.imageScaleType(ImageScaleType.EXACTLY).cacheInMemory(true)
				.considerExifParams(true)
				.showImageOnFail(R.drawable.ic_placeholder_image)
				.cacheOnDisc(true).build();
	}

	public synchronized byte[] decodeSampledBitmapFromFile(String path) {

		byte[] bytes = convertFileIntoBase64(path);
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
			matrix.postRotate(getRotation(path));
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
		case Constants.REQUEST_CONSUMER_REGISTRATION:
			mRegisterButton.setEnabled(true);
			
			String string = JsonResponseParser
					.parseConsumerRegistrationResponse((String) object);
			if (string
					.equalsIgnoreCase("Please check your email and confirm your registration.")) {
				setFocusable(false,view);
				forgotPassword();
			}
			break;
		case Constants.REQUEST_CONSUMER_UPDATE:
			saveButton.setEnabled(true);
			Consumer consumer = JsonResponseParser
					.parseFinalConsumerRegistrationResponse((String) object);
			if (consumer != null) {
				BuyOpic buyOpic = (BuyOpic) getApplication();
				Log.i("CREDENTIALS", "register act" +consumer.getmConsumerEmail());
				buyOpic.setmConsumerEmail(consumer.getmConsumerEmail());
				buyOpic.setmBaseUrl(BuyopicNetworkServiceManager.BASE_URL);
				buyOpic.setmConsumerId(consumer.getmConsumerId());
				buyOpic.setmConsumerRegistrationStatus(consumer.isConsumerFinalRegistered());
				buyOpic.setmConsumerName(consumer.getmConsumerUserName());
				buyOpic.setmConsumerAddress(consumer.getmConsumerAddress());
				buyOpic.setmConsumerAddress1(consumer.getmConsumerAddress1());
				buyOpic.setmConsumerProfilePic(consumer.getmConsumerProfilePic());
				buyOpic.setmConsumerPhoneNumber(consumer.getmPhoneNumber());
				buyOpic.setmConsumerPassword(consumer.getmConsumerPassword());
				buyOpic.setmConsumerCity(consumer.getmConsumerCity());
				buyOpic.setmConsumerCountry(consumer.getmConsumerCountry());
				buyOpic.setmConsumerState(consumer.getmConsumerState());
				buyOpic.setmConsumerStreet(consumer.getmConsumerStreet());
				buyOpic.setmConsumerAddress2(consumer.getmConsumerAddress2());
				buyOpic.setmConsumerGoogleIconImage(consumer.getmGoogleIconImage());
				buyOpic.setmConsumerGooglePlaceID(consumer.getmGooglePlaceId());
				buyOpic.setmConsumerPostalCode(consumer.getmPostalCode());
				buyOpic.setmConsumerLatitude(consumer.getmConsumerLatitude());
				buyOpic.setmConsumerLongitude(consumer.getmConsumerLongitude());
				if(consumer.getmUserAddressImageUrl()!=null)
					buyOpic.setmConsumerUserAddressImageUrl(consumer.getmUserAddressImageUrl());
					else
						buyOpic.setmConsumerUserAddressImageUrl(consumer.getmConsumerAddressImageUrl());
				
				
				Intent intent = new Intent(this, HomePageSetupActivity.class);
				startActivity(intent);
				finish();
			} else {
				Utils.showToast(this, "Login Failed.Please try again");
			}

		default:
			break;
		}

	}

	private void setFocusable(boolean b,View view) {
		if (view instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup) view;
			for (int i = 0; i < vg.getChildCount(); i++) {
				View child = vg.getChildAt(i);
				setFocusable(b, child);
			}
		} else if (view instanceof EditText) {
			EditText editText=(EditText) view;
			editText.setFocusable(b);
		}
	}

	private void forgotPassword() {
		try
		{
		final Dialog dialog = new Dialog(this, R.style.Theme_Dialog);
		View view = LayoutInflater.from(this).inflate(
				R.layout.layout_forgotpassword_dialog_view, null);
		Utils.overrideFonts(this, view);
		dialog.setContentView(view);
		TextView forgotPasswordText = (TextView) view
				.findViewById(R.id.forgot_password_text);
		String forgotPasswordResponseText = "We have just sent you an email with a confirmation link (to your email address above). Please open that email and click on the confirmation link to complete this one-time registration. Welcome to Yellow!";
		forgotPasswordText.setText(forgotPasswordResponseText);
		ImageButton cancelDialog = (ImageButton) view
				.findViewById(R.id.dialog_cancel);
		cancelDialog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				finish();
			}
		});
		dialog.setCancelable(false);
		dialog.show();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	@Override
	public void onFailure(int requestCode, String message) {

	}

}
