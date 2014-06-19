package cn.jetoo.numbermgr.intercept.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import cn.jetoo.numbermgr.intercept.bean.InterceptSms;

import java.util.ArrayList;
import java.util.List;

public class InterceptSmsDao implements BaseColumns {

    private static final int FIELD_NUMBER = 1;
    private static final int FIELD_NAME = 2;
    private static final int FIELD_BODY = 3;
    private static final int FIELD_DATE = 4;

    // field
    public static final String NUMBER = "number";
    public static final String NAME = "name";
    public static final String BODY = "body";
    public static final String DATE = "date";

    /**
     * The TP-Status value for the message, or -1 if no status has been
     * received
     */
    public static final String STATUS = "status";

    public static List<InterceptSms> queryAllInterceptSms(Context context) {
        List<InterceptSms> list = new ArrayList<InterceptSms>();
        try {
            DBHelper dbHelper = new DBHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            StringBuffer excSql = new StringBuffer();
            excSql.append("select * from ");
            excSql.append(DBHelper.SQLITE_TABLE_INTERCEPT_SMS_TABLE);

            Cursor cursor = db.rawQuery(excSql.toString(), null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    InterceptSms item = new InterceptSms();
                    item.phoneNumber = cursor.getString(FIELD_NUMBER);
                    item.phoneName = cursor.getString(FIELD_NAME);
                    item.smsContent = cursor.getString(FIELD_BODY);
                    item.smsDate = cursor.getLong(FIELD_DATE);
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

    public static List<InterceptSms> queryIntercepSmsItem(Context context, String keyWord) {
        List<InterceptSms> list = new ArrayList<InterceptSms>();
        try {
            DBHelper dbHelper = new DBHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            StringBuffer excSql = new StringBuffer();
            excSql.append("select * from ");
            excSql.append(DBHelper.SQLITE_TABLE_INTERCEPT_SMS_TABLE);
            excSql.append(" where ").append(NUMBER).append(" = ");
            excSql.append("'").append(keyWord).append("'");
            excSql.append(" or ").append(NAME).append(" = ");
            excSql.append("'").append(keyWord).append("'");
            excSql.append(";");
            Cursor cursor = db.rawQuery(excSql.toString(), null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    InterceptSms item = new InterceptSms();
                    item.phoneNumber = cursor.getString(FIELD_NUMBER);
                    item.phoneName = cursor.getString(FIELD_NAME);
                    item.smsContent = cursor.getString(FIELD_BODY);
                    item.smsDate = cursor.getLong(FIELD_DATE);
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

    public static boolean insertIntercepSmsItem(Context context, final InterceptSms item) {
        boolean ret = true;
        try {
            DBHelper dbHelper = new DBHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            StringBuffer excSql = new StringBuffer();
            excSql.append("INSERT INTO ").append(DBHelper.SQLITE_TABLE_INTERCEPT_SMS_TABLE);
            excSql.append(" (").append(NUMBER).append(",").append(NAME).append(",")
                    .append(BODY).append(",").append(DATE).append(")");
            excSql.append(" VALUES(");
            excSql.append("'").append(item.phoneNumber).append("'");
            excSql.append(",'").append(item.phoneName).append("'");
            excSql.append(",'").append(item.smsContent).append("'");
            excSql.append(",'").append(item.smsDate).append("'");
            excSql.append(");");
            db.execSQL(excSql.toString());
            db.close();
        } catch (Exception e) {
            ret = false;
        }
        return ret;
    }

    public static boolean insertIntercepSms(Context context, final List<InterceptSms> batch) {
        boolean ret = true;
        try {
            ContentValues values = new ContentValues();
            if (batch.size() > 0) {
                DBHelper dbHelper = new DBHelper(context);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.beginTransaction();
                try {
                    for (InterceptSms item : batch) {
                        values.put(NUMBER, item.phoneNumber);
                        values.put(NAME, item.phoneName);
                        values.put(BODY, item.smsContent);
                        values.put(DATE, item.smsDate);
                        db.replace(DBHelper.SQLITE_TABLE_INTERCEPT_SMS_TABLE, null, values);
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

    public static boolean deleteAllIntercepSms(Context context) {
        boolean ret = true;
        try {
            DBHelper dbHelper = new DBHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            StringBuffer excSql = new StringBuffer();
            excSql.append("DELETE FROM ").append(DBHelper.SQLITE_TABLE_INTERCEPT_SMS_TABLE);
            db.execSQL(excSql.toString());
            db.close();
        } catch (Exception e) {
            ret = false;
        }
        return ret;
    }
}
