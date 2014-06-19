package cn.jetoo.numbermgr.intercept.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import cn.jetoo.numbermgr.R;
import cn.jetoo.numbermgr.common.SharedPreferencesCompat;
import cn.jetoo.numbermgr.intercept.bean.InterceptCall;
import cn.jetoo.numbermgr.numbers.bean.NumberDetail;
import cn.jetoo.numbermgr.utils.FeatureConfig;
import cn.jetoo.numbermgr.utils.LogHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class IntercepCallDao implements BaseColumns {

    private static final String TAG = "IntercepCallDao";
    private static final boolean DEBUG = FeatureConfig.DEBUG_LOG;

    public static final String NAME = "name";
    public static final String NUMBER = "number";
    public static final String DATE = "date";
    public static final String LOCATION = "location";

    private static final int FIELD_NUMBER = 1;
    private static final int FIELD_NAME = 2;
    private static final int FIELD_LAST_TIME = 3;
    private static final int FIELD_LOC = 4;

    public static List<InterceptCall> queryIntercepCallList(Context context) {
        List<InterceptCall> list = new ArrayList<InterceptCall>();
        try {
            DBHelper dbHelper = new DBHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            StringBuffer excSql = new StringBuffer();
            excSql.append("select * from ");
            excSql.append(DBHelper.SQLITE_TABLE_INTERCEPT_CALL_TABLE);
            if (DEBUG) {
                LogHelper.i(TAG, "excSql:" + excSql);
            }
            Cursor cursor = db.rawQuery(excSql.toString(), null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    InterceptCall item = new InterceptCall();
                    item.phoneNumber = cursor.getString(FIELD_NUMBER);
                    item.phoneName = cursor.getString(FIELD_NAME);
                    item.callTime = cursor.getLong(FIELD_LAST_TIME);
                    item.location = cursor.getString(FIELD_LOC);
                    list.add(item);
                }
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<InterceptCall> queryIntercepCallItem(Context context, String keyword) {
        List<InterceptCall> list = new ArrayList<InterceptCall>();
        try {
            DBHelper dbHelper = new DBHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            StringBuffer excSql = new StringBuffer();
            excSql.append("select * from ");
            excSql.append(DBHelper.SQLITE_TABLE_INTERCEPT_CALL_TABLE);
            excSql.append(" where ").append(NUMBER).append(" = ");
            excSql.append("'").append(keyword).append("'");
            excSql.append(" or ").append(NAME).append(" = ");
            excSql.append("'").append(keyword).append("'");
            excSql.append(";");
            if (DEBUG) {
                LogHelper.i(TAG, "excSql:" + excSql);
            }
            Cursor cursor = db.rawQuery(excSql.toString(), null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    InterceptCall item = new InterceptCall();
                    item.phoneNumber = cursor.getString(FIELD_NUMBER);
                    item.phoneName = cursor.getString(FIELD_NAME);
                    item.callTime = cursor.getLong(FIELD_LAST_TIME);
                    item.location = cursor.getString(FIELD_LOC);
                    list.add(item);
                }
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean insertIntercepCallItem(Context context, final InterceptCall item) {
        boolean ret = true;
        try {
            DBHelper dbHelper = new DBHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            StringBuffer excSql = new StringBuffer();
            excSql.append("INSERT INTO ").append(DBHelper.SQLITE_TABLE_INTERCEPT_CALL_TABLE);
            excSql.append(" (").append(NUMBER).append(",").append(NAME).append(",").append(DATE).append(",")
                    .append(LOCATION).append(") ");
            excSql.append(" VALUES(");
            excSql.append("'").append(item.phoneNumber).append("'");
            excSql.append(",'").append(item.phoneName).append("'");
            excSql.append(",'").append(item.callTime).append("'");
            excSql.append(",'").append(item.location).append("'");
            excSql.append(");");
            if (DEBUG) {
                LogHelper.d(TAG, "insertIntercepCallItem:" + excSql);
            }
            db.execSQL(excSql.toString());
            db.close();
        } catch (Exception e) {
            ret = false;
        }
        return ret;
    }

    public static boolean insertIntercepCall(Context context, final List<InterceptCall> batch) {
        boolean ret = true;
        try {
            ContentValues values = new ContentValues();
            if (batch.size() > 0) {
                DBHelper dbHelper = new DBHelper(context);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.beginTransaction();
                try {
                    for (InterceptCall item : batch) {
                        values.put(NUMBER, item.phoneNumber);
                        values.put(NAME, item.phoneName);
                        values.put(DATE, item.callTime);
                        values.put(LOCATION, item.location);
                        db.replace(DBHelper.SQLITE_TABLE_INTERCEPT_CALL_TABLE, null, values);
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                    db.close();
                }
            }
        } catch (Exception e) {
            ret = false;
        }
        return ret;
    }

    public static boolean deleteAllIntercepCalls(Context context) {
        boolean ret = true;
        try {
            DBHelper dbHelper = new DBHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            StringBuffer excSql = new StringBuffer();
            excSql.append("DELETE FROM ").append(DBHelper.SQLITE_TABLE_INTERCEPT_CALL_TABLE);
            db.execSQL(excSql.toString());
            db.close();
        } catch (Exception e) {
            ret = false;
        }
        return ret;
    }
}
