package com.buyopic.android.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.buyopic.android.models.Beacon;
import com.buyopic.android.utils.Utils;

public class BuyopicConsumerDatabase {

	private static int DB_VERSION = 5;
	private static String DB_NAME = "buyopic.db";

	private Context mContext;
	private SQLiteDatabase sqLiteDatabase;
	private DBHelper helper;
	public static BuyopicConsumerDatabase wayappdb;

	public static BuyopicConsumerDatabase shareInstance(Context context) {
		if (wayappdb == null) {
			wayappdb = new BuyopicConsumerDatabase(context);
		}
		return wayappdb;
	}

	public BuyopicConsumerDatabase(Context context) {
		mContext = context;
		helper = new DBHelper(mContext);
	}

	public static final class BEACON_TABLE_COLUMNS implements BaseColumns {

		public static final String TABLE_NAME = "tblBeaconDetails";
		public static final String COL_BEACON_ID = "_id";
		public static final String COL_BEACON_UUID = "uuid";
		public static final String COL_BEACON_MAJOR = "major";
		public static final String COL_BEACON_MINOR = "minor";
		public static final String COL_BEACON_RSSI = "rssi";
		public static final String COL_BEACON_ENTRY_TIME = "entry_time";
		public static final String COL_IS_NOTIFIED = "isnotified";
		public static final String COL_BEACON_MAJOR_MINOR = "major_minor";

		public static final String CREATE_TABLE = new StringBuilder()
				.append("CREATE TABLE IF NOT EXISTS ")
				.append(BEACON_TABLE_COLUMNS.TABLE_NAME).append(" (")
				.append(BaseColumns._ID)
				.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
				.append(BEACON_TABLE_COLUMNS.COL_BEACON_UUID).append(" TEXT, ")
				.append(BEACON_TABLE_COLUMNS.COL_BEACON_MAJOR)
				.append(" TEXT, ")
				.append(BEACON_TABLE_COLUMNS.COL_BEACON_MINOR)
				.append(" TEXT, ").append(BEACON_TABLE_COLUMNS.COL_BEACON_RSSI)
				.append(" TEXT, ")
				.append(BEACON_TABLE_COLUMNS.COL_BEACON_ENTRY_TIME)
				.append(" TEXT, ").append(BEACON_TABLE_COLUMNS.COL_IS_NOTIFIED)
				.append(" TEXT, ")
				.append(BEACON_TABLE_COLUMNS.COL_BEACON_MAJOR_MINOR)
				.append(" TEXT ").append(");").toString();
	}

	private static class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(BEACON_TABLE_COLUMNS.CREATE_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		}
	}

	public boolean checkBeaconExistsOrNot(String major, String minor,
			String uuid) {
		
		boolean status = false;
		try
		{
		sqLiteDatabase = helper.getReadableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery("select count(*) from "
				+ BEACON_TABLE_COLUMNS.TABLE_NAME + " where "
				+ BEACON_TABLE_COLUMNS.COL_BEACON_MINOR + "=? and "
				+ BEACON_TABLE_COLUMNS.COL_BEACON_MAJOR + "=?", new String[] {
				minor, major });
		if (cursor != null && cursor.moveToNext()) {
			int count = cursor.getInt(0);
			if (count > 0) {
				status = true;
			} else {
				status = false;
			}
			cursor.close();
			cursor = null;
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return status;
	}

	public Beacon getBeaconDetails(String major, String minor) {
		Beacon beacon = null;
		sqLiteDatabase = helper.getReadableDatabase();
		Cursor cursor = sqLiteDatabase.query(BEACON_TABLE_COLUMNS.TABLE_NAME,
				null, BEACON_TABLE_COLUMNS.COL_BEACON_MAJOR + "=? and "
						+ BEACON_TABLE_COLUMNS.COL_BEACON_MINOR + "=?",
				new String[] { major, minor }, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			beacon = new Beacon();
			cursor.moveToFirst();
			beacon.setmBeaconUUID(cursor.getString(cursor
					.getColumnIndex(BEACON_TABLE_COLUMNS.COL_BEACON_UUID)));
			beacon.setmMinor(cursor.getString(cursor
					.getColumnIndex(BEACON_TABLE_COLUMNS.COL_BEACON_MINOR)));
			beacon.setmMajor(cursor.getString(cursor
					.getColumnIndex(BEACON_TABLE_COLUMNS.COL_BEACON_MAJOR)));
			beacon.setmRssi(cursor.getString(cursor
					.getColumnIndex(BEACON_TABLE_COLUMNS.COL_BEACON_RSSI)));
			beacon.setmEnteredTime(cursor.getString(cursor
					.getColumnIndex(BEACON_TABLE_COLUMNS.COL_BEACON_ENTRY_TIME)));
			beacon.setIsNotified(cursor.getString(cursor
					.getColumnIndex(BEACON_TABLE_COLUMNS.COL_IS_NOTIFIED)));
		}
		if (cursor != null) {
			cursor.close();
		}
		return beacon;
	}

	public int truncateDatabase() {
		sqLiteDatabase = helper.getReadableDatabase();
		int rowsAffected = sqLiteDatabase.delete(
				BEACON_TABLE_COLUMNS.TABLE_NAME, null, null);
		return rowsAffected;
	}

	public int deleteExitedBeacons(String major_minors) {
		sqLiteDatabase = helper.getReadableDatabase();
		int rowsAffected = -1;
		rowsAffected = sqLiteDatabase.delete(BEACON_TABLE_COLUMNS.TABLE_NAME,
				BEACON_TABLE_COLUMNS.COL_BEACON_MAJOR_MINOR + " NOT IN("
						+ major_minors + ")", null);
		Utils.showLog(major_minors);
		return rowsAffected;
	}
	
	public int deleteExitedBeaconsRow(String major_minors) {
		sqLiteDatabase = helper.getReadableDatabase();
		int rowsAffected = -1;
		rowsAffected = sqLiteDatabase.delete(BEACON_TABLE_COLUMNS.TABLE_NAME,
				BEACON_TABLE_COLUMNS.COL_BEACON_MAJOR_MINOR + "  IN("
						+ major_minors + ")", null);
		Utils.showLog(major_minors);
		return rowsAffected;
	}
	

	public int updateNotFoundCount(Beacon beacon) {
		sqLiteDatabase = helper.getReadableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(BEACON_TABLE_COLUMNS.COL_BEACON_ENTRY_TIME,
				beacon.getmEnteredTime());
		contentValues.put(BEACON_TABLE_COLUMNS.COL_BEACON_MAJOR,
				beacon.getmMajor());
		contentValues.put(BEACON_TABLE_COLUMNS.COL_BEACON_MINOR,
				beacon.getmMinor());
		contentValues.put(BEACON_TABLE_COLUMNS.COL_BEACON_RSSI,
				beacon.getmRssi());
		contentValues.put(BEACON_TABLE_COLUMNS.COL_BEACON_UUID,
				beacon.getmBeaconUUID());
		contentValues.put(BEACON_TABLE_COLUMNS.COL_IS_NOTIFIED,
				beacon.getIsNotified());
		contentValues.put(BEACON_TABLE_COLUMNS.COL_BEACON_MAJOR_MINOR,
				beacon.getmBeaconId_Minor_Major());
		
		return sqLiteDatabase.update(BEACON_TABLE_COLUMNS.TABLE_NAME,
				contentValues, BEACON_TABLE_COLUMNS.COL_BEACON_MAJOR
						+ "=? and " + BEACON_TABLE_COLUMNS.COL_BEACON_MINOR
						+ "=?",
				new String[] { beacon.getmMajor(), beacon.getmMinor() });

	}

	public List<Beacon> getNotInBeaconDetails(String majors_minors) {

		List<Beacon> beaconList = new ArrayList<Beacon>();
		Beacon beacon = null;
		sqLiteDatabase = helper.getReadableDatabase();
		Cursor cursor = sqLiteDatabase.query(BEACON_TABLE_COLUMNS.TABLE_NAME,
				null, BEACON_TABLE_COLUMNS.COL_BEACON_MAJOR_MINOR + " not in("
						+ majors_minors + ")", null, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				beacon = new Beacon();
				beacon.setmBeaconUUID(cursor.getString(cursor
						.getColumnIndex(BEACON_TABLE_COLUMNS.COL_BEACON_UUID)));
				beacon.setmMinor(cursor.getString(cursor
						.getColumnIndex(BEACON_TABLE_COLUMNS.COL_BEACON_MINOR)));
				beacon.setmMajor(cursor.getString(cursor
						.getColumnIndex(BEACON_TABLE_COLUMNS.COL_BEACON_MAJOR)));
				beacon.setmRssi(cursor.getString(cursor
						.getColumnIndex(BEACON_TABLE_COLUMNS.COL_BEACON_RSSI)));
				beacon.setmEnteredTime(cursor.getString(cursor
						.getColumnIndex(BEACON_TABLE_COLUMNS.COL_BEACON_ENTRY_TIME)));
				beacon.setIsNotified(cursor.getString(cursor
						.getColumnIndex(BEACON_TABLE_COLUMNS.COL_IS_NOTIFIED)));
				beacon.setmBeaconId_Minor_Major(cursor.getString(cursor
						.getColumnIndex(BEACON_TABLE_COLUMNS.COL_BEACON_MAJOR_MINOR)));
				beaconList.add(beacon);
			}
		}
		if (cursor != null) {
			cursor.close();
		}
		return beaconList;
	}


	public List<Beacon> getAllBeaconDetails() {

		List<Beacon> beaconList = new ArrayList<Beacon>();
		Beacon beacon = null;
		sqLiteDatabase = helper.getReadableDatabase();
		Cursor cursor = sqLiteDatabase.query(BEACON_TABLE_COLUMNS.TABLE_NAME,
				null, null, null, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				beacon = new Beacon();
				beacon.setmBeaconUUID(cursor.getString(cursor
						.getColumnIndex(BEACON_TABLE_COLUMNS.COL_BEACON_UUID)));
				beacon.setmMinor(cursor.getString(cursor
						.getColumnIndex(BEACON_TABLE_COLUMNS.COL_BEACON_MINOR)));
				beacon.setmMajor(cursor.getString(cursor
						.getColumnIndex(BEACON_TABLE_COLUMNS.COL_BEACON_MAJOR)));
				beacon.setmRssi(cursor.getString(cursor
						.getColumnIndex(BEACON_TABLE_COLUMNS.COL_BEACON_RSSI)));
				beacon.setmEnteredTime(cursor.getString(cursor
						.getColumnIndex(BEACON_TABLE_COLUMNS.COL_BEACON_ENTRY_TIME)));
				beacon.setIsNotified(cursor.getString(cursor
						.getColumnIndex(BEACON_TABLE_COLUMNS.COL_IS_NOTIFIED)));
				beacon.setmBeaconId_Minor_Major(cursor.getString(cursor
						.getColumnIndex(BEACON_TABLE_COLUMNS.COL_BEACON_MAJOR_MINOR)));
				beaconList.add(beacon);
			}
		}
		if (cursor != null) {
			cursor.close();
		}
		return beaconList;
	}

	public long insertItem(Beacon beacon) {
		sqLiteDatabase = helper.getWritableDatabase();
		boolean isUpdate = false;

		Cursor cursor = sqLiteDatabase.rawQuery("select count(*) from "
				+ BEACON_TABLE_COLUMNS.TABLE_NAME + " where "
				+ BEACON_TABLE_COLUMNS.COL_BEACON_MINOR + "=? and "
				+ BEACON_TABLE_COLUMNS.COL_BEACON_MAJOR + "=?", new String[] {
				beacon.getmMinor(), beacon.getmMajor() });
		if (cursor != null && cursor.moveToNext()) {
			int count = cursor.getInt(0);
			if (count >= 1) {
				isUpdate = true;
			}
			cursor.close();
			cursor = null;
		}

		ContentValues contentValues = new ContentValues();
		contentValues.put(BEACON_TABLE_COLUMNS.COL_BEACON_ENTRY_TIME,
				beacon.getmEnteredTime());
		contentValues.put(BEACON_TABLE_COLUMNS.COL_BEACON_MAJOR,
				beacon.getmMajor());
		contentValues.put(BEACON_TABLE_COLUMNS.COL_BEACON_MINOR,
				beacon.getmMinor());
		contentValues.put(BEACON_TABLE_COLUMNS.COL_BEACON_RSSI,
				beacon.getmRssi());
		contentValues.put(BEACON_TABLE_COLUMNS.COL_BEACON_UUID,
				beacon.getmBeaconUUID());
		contentValues.put(BEACON_TABLE_COLUMNS.COL_IS_NOTIFIED,
				beacon.getIsNotified());
		contentValues.put(BEACON_TABLE_COLUMNS.COL_BEACON_MAJOR_MINOR,
				beacon.getmBeaconId_Minor_Major());
		if (isUpdate) {
			return sqLiteDatabase.update(BEACON_TABLE_COLUMNS.TABLE_NAME,
					contentValues, BEACON_TABLE_COLUMNS.COL_BEACON_MAJOR
							+ "=? and " + BEACON_TABLE_COLUMNS.COL_BEACON_MINOR
							+ "=?",
					new String[] { beacon.getmMajor(), beacon.getmMinor() });
		} else {
			return sqLiteDatabase.insert(BEACON_TABLE_COLUMNS.TABLE_NAME, null,
					contentValues);
		}

	}

}