package cn.jetoo.numbermgr.content;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.CallLog;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import cn.jetoo.numbermgr.intercept.pick.CallLogPickItem;
import cn.jetoo.numbermgr.intercept.pick.ContactPickItem;
import cn.jetoo.numbermgr.intercept.pick.SmsPickItem;
import cn.jetoo.numbermgr.telephony.TelephonyUtils;
import cn.jetoo.numbermgr.utils.FeatureConfig;
import cn.jetoo.numbermgr.utils.LogHelper;

public class PickContent {

    private static final String TAG = "PickContent";
    private static final boolean DEBUG = FeatureConfig.DEBUG_LOG;

    //
    private static final String[] CALL_LOG_PROJECTION = {
            CallLog.Calls._ID, // 0
            CallLog.Calls.CACHED_NAME, // 1
            CallLog.Calls.NUMBER, // 2
            CallLog.Calls.DATE, // 3
            CallLog.Calls.DURATION, // 4
            CallLog.Calls.TYPE, // 5
    };
    private static final int COLUMN_CALL_NAME = 1;
    private static final int COLUMN_CALL_NUMBER = 2;
    private static final int COLUMN_CALL_DATE = 3;
    private static final int COLUMN_CALL_TYPE = 5;

    //
    private static final String[] CONTACT_PROJECTION = {
            Phone.CONTACT_ID,
            Phone.DISPLAY_NAME,
            Phone.NUMBER
    };
    private static final int DISPLAY_NAME = 1;
    private static final int NUMBER = 2;

    //
    private static final String[] SMSMESSAGE_PROJECTION = {
            BaseColumns._ID,// 0
            "body",
            "address",
            "date",
            "read"
    };
    private static final int BODY = 1;
    private static final int ADDRESS = 2;
    private static final int DATE = 3;

    public static List<CallLogPickItem> queryCallLogList(Context context) {
        List<CallLogPickItem> pickList = new ArrayList<CallLogPickItem>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI,
                CALL_LOG_PROJECTION, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                CallLogPickItem item = new CallLogPickItem();
                item.phoneName = cursor.getString(COLUMN_CALL_NAME);
                item.phoneNumber = cursor.getString(COLUMN_CALL_NUMBER);
                item.lastCallTime = cursor.getLong(COLUMN_CALL_DATE);
                item.type = cursor.getInt(COLUMN_CALL_TYPE);
                item.type = item.type > 3 ? 3 : item.type;
                pickList.add(item);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return pickList;
    }

    public static List<ContactPickItem> queryContactList(Context context) {
        List<ContactPickItem> pickList = new ArrayList<ContactPickItem>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(Phone.CONTENT_URI,
                CONTACT_PROJECTION, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                ContactPickItem item = new ContactPickItem();
                item.phoneName = cursor.getString(DISPLAY_NAME);
                item.phoneNumber = cursor.getString(NUMBER);
                pickList.add(item);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return pickList;
    }

    public static List<SmsPickItem> querySmsList(Context context) {
        List<SmsPickItem> pickList = new ArrayList<SmsPickItem>();
        ContentResolver cr = context.getContentResolver();
        String selection = "type" + " in (" + 1 + "," + 2 + ")";
        String[] selectionArgs = null;

        Cursor cursor = cr.query(Uri.parse("content://sms"),
                SMSMESSAGE_PROJECTION,
                selection,
                selectionArgs, "date DESC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                SmsPickItem sms = new SmsPickItem();
                sms.lastSms = cursor.getString(BODY);
                sms.phoneNumber = TelephonyUtils.stripPrefix(cursor.getString(ADDRESS));
                sms.lastSmsTime = cursor.getLong(DATE);
                pickList.add(sms);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return pickList;
    }
}
