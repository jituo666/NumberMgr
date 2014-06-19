
package cn.jetoo.numbermgr.query;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;


import cn.jetoo.numbermgr.R;
import cn.jetoo.numbermgr.common.Constants;
import cn.jetoo.numbermgr.query.codec.LocationCodec;
import cn.jetoo.numbermgr.query.codec.NaiveLocationCodec;
import cn.jetoo.numbermgr.query.floatingWindow.FloatingManager;
import cn.jetoo.numbermgr.utils.FeatureConfig;
import cn.jetoo.numbermgr.utils.LogHelper;

import java.io.IOException;

public class LocationService extends Service {

    private static final String TAG = "LocationService";
    private static final boolean DEBUG = FeatureConfig.DEBUG_LOG;

    private LocationCodec mLocationCodec;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        mLocationCodec = NaiveLocationCodec.getInstance();
        try {
            long start = System.currentTimeMillis();
            mLocationCodec.init(getApplicationContext());
            if (DEBUG) {
                LogHelper.d(TAG, "NaiveLocationCodec ready, time used(ms):" + (System.currentTimeMillis() - start));
            }
        } catch (IOException e) {
            LogHelper.e(TAG, e.toString());
        }
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        if (Constants.QL_ACTION_INCOMING_CALL.equals(intent.getAction()) || Constants.QL_ACTION_OUTGONIG_CALL.equals(intent.getAction())) {
            String phoneNumber = intent.getStringExtra(Constants.QL_PHONE_NUMBER_FIELD);
            new QueryTask().execute(phoneNumber, intent.getAction());
        } else if (Constants.QL_ACTION_END_CALL.equals(intent.getAction())) {
            FloatingManager.getInstance(getApplicationContext()).detachFromWindow();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private class QueryTask extends AsyncTask<String, Void, Void> {

        private String mPhoneNumber = "";
        private String mAction = "";
        private String mPhoneContactName = "";
        private Bitmap mPhoto = null;

        @Override
        protected void onPreExecute() {
            mPhoneContactName = "";
            mPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.people_img_default); // default
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            mPhoneNumber = params[0];
            mAction = params[1];
            getContactIdFromPhoneNumber();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            FloatingManager.getInstance(getApplicationContext()).attachToWindow(mPhoneContactName, mPhoneNumber, mLocationCodec.getLocation(mPhoneNumber), mPhoto,
                    Constants.QL_ACTION_INCOMING_CALL.equals(mAction));
            super.onPostExecute(result);
        }

        private void getContactIdFromPhoneNumber() {
            Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(mPhoneNumber));
            Cursor resultCursor = null;
            try {
                resultCursor = getContentResolver().query(uri, new String[] { PhoneLookup._ID, PhoneLookup.DISPLAY_NAME}, null, null, null);
                if (resultCursor != null && resultCursor.getCount() > 0) {
                    resultCursor.moveToFirst();
                    mPhoneContactName = resultCursor.getString(resultCursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));
                    Long contactID = resultCursor.getLong(resultCursor.getColumnIndex(PhoneLookup._ID));
                    mPhoto = BitmapFactory.decodeStream(ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
                            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactID)));
                    if (mPhoto == null) {
                        mPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.people_img_default);
                    }
                } else {
                    LogHelper.i(TAG, "no phonelookup info for number:" + mPhoneNumber);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (resultCursor != null) {
                        resultCursor.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
