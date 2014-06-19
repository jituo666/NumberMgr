package cn.jetoo.numbermgr.intercept.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import cn.jetoo.numbermgr.R;
import cn.jetoo.numbermgr.common.SharedPreferencesCompat;
import cn.jetoo.numbermgr.intercept.bean.BlackListItem;
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

public class BlackListDao implements BaseColumns {

    private static final String TAG = "BlackListDao";
    private static final boolean DEBUG = FeatureConfig.DEBUG_LOG;

    public static final String NAME = "name";
    public static final String NUMBER = "number";
    public static final String LOCATION = "location";

    private static final int FIELD_NUMBER = 1;
    private static final int FIELD_NAME = 2;
    private static final int FIELD_LOC = 3;

    public static List<BlackListItem> queryBlackList(Context context) {
        List<BlackListItem> list = new ArrayList<BlackListItem>();
        try {
            DBHelper dbHelper = new DBHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            StringBuffer excSql = new StringBuffer();
            excSql.append("select * from ");
            excSql.append(DBHelper.SQLITE_TABLE_BLACK_LIST_TABLE);
            if (DEBUG) {
                LogHelper.i(TAG, "excSql:" + excSql);
            }
            Cursor cursor = db.rawQuery(excSql.toString(), null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    BlackListItem item = new BlackListItem();
                    item.phoneName = cursor.getString(FIELD_NAME);
                    item.phoneNumber = cursor.getString(FIELD_NUMBER);
                    item.locaton = cursor.getString(FIELD_LOC);
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

    public static boolean queryBlackListItem(Context context, String phoneNumber) {
        boolean exist = false;
        try {
            DBHelper dbHelper = new DBHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            StringBuffer excSql = new StringBuffer();
            excSql.append("select * from ");
            excSql.append(DBHelper.SQLITE_TABLE_BLACK_LIST_TABLE);
            excSql.append(" where ").append(NUMBER).append(" = ");
            excSql.append("'").append(phoneNumber).append("'");
            excSql.append(";");
            if (DEBUG) {
                LogHelper.i(TAG, "excSql:" + excSql);
            }
            Cursor cursor = db.rawQuery(excSql.toString(), null);
            if (cursor != null && cursor.moveToNext()) {
                exist = true;
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            exist = false;
        }
        return exist;
    }

    public static boolean insertBlackListItem(Context context, final BlackListItem item) {
        boolean ret = true;
        try {
            DBHelper dbHelper = new DBHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            StringBuffer excSql = new StringBuffer();
            excSql.append("INSERT INTO ").append(DBHelper.SQLITE_TABLE_BLACK_LIST_TABLE);
            excSql.append(" (").append(NUMBER).append(",").append(NAME).append(",")
                    .append(LOCATION).append(")");
            excSql.append(" VALUES(");
            excSql.append("'").append(item.phoneNumber).append("'");
            excSql.append(",'").append(item.phoneName).append("'");
            excSql.append(",'").append(item.locaton).append("'");
            excSql.append(");");
            db.execSQL(excSql.toString());
            db.close();
        } catch (Exception e) {
            ret = false;
        }
        return ret;
    }

    public static boolean insertBlackList(Context context, final List<BlackListItem> batch) {
        boolean ret = true;
        try {
            ContentValues values = new ContentValues();
            if (batch.size() > 0) {
                DBHelper dbHelper = new DBHelper(context);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.beginTransaction();
                try {
                    for (BlackListItem item : batch) {
                        values.put(NUMBER, item.phoneNumber);
                        values.put(NAME, item.phoneName);
                        values.put(LOCATION, item.locaton);
                        db.replace(DBHelper.SQLITE_TABLE_BLACK_LIST_TABLE, null, values);
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

    public static boolean deleteBlackListItem(Context context, String phoneNumber) {
        boolean ret = true;
        try {
            DBHelper dbHelper = new DBHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            StringBuffer excSql = new StringBuffer();
            excSql.append("DELETE FROM ").append(DBHelper.SQLITE_TABLE_BLACK_LIST_TABLE);
            excSql.append(" where ").append(NUMBER).append(" = ");
            excSql.append("'").append(phoneNumber).append("'");
            excSql.append(";");
            db.execSQL(excSql.toString());
            db.close();
        } catch (Exception e) {
            ret = false;
        }
        return ret;
    }
}
