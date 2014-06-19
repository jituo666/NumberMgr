package cn.jetoo.numbermgr.intercept.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public final static String SQLITE_NAME = "blacklist.db";

    public final static String SQLITE_TABLE_BLACK_LIST_TABLE = "black_list";
    public final static String SQLITE_TABLE_INTERCEPT_CALL_TABLE = "inter_call";
    public final static String SQLITE_TABLE_INTERCEPT_SMS_TABLE = "inter_sms";

    public final static int NUMBRT_DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, SQLITE_NAME, null, NUMBRT_DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // black list table
        String excSql_create_black_list = "CREATE TABLE IF NOT EXISTS "
                + SQLITE_TABLE_BLACK_LIST_TABLE + " (" +
                BlackListDao._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                BlackListDao.NUMBER + " TEXT," +
                BlackListDao.NAME + " TEXT," +
                BlackListDao.LOCATION + " TEXT" +
                ");";
        db.execSQL(excSql_create_black_list);
        // intercept calls
        String excSql_create_intercept_calls = "CREATE TABLE IF NOT EXISTS "
                + SQLITE_TABLE_INTERCEPT_CALL_TABLE + " (" +
                IntercepCallDao._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                IntercepCallDao.NUMBER + " TEXT," +
                IntercepCallDao.NAME + " TEXT," +
                IntercepCallDao.DATE + " LONG," +
                IntercepCallDao.LOCATION + " TEXT" +
                ");";
        db.execSQL(excSql_create_intercept_calls);
        // intercept sms
        String excSql_create_intercept_sms = "CREATE TABLE IF NOT EXISTS " + SQLITE_TABLE_INTERCEPT_SMS_TABLE
                + " (" +
                InterceptSmsDao._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                InterceptSmsDao.NUMBER + " TEXT," +
                InterceptSmsDao.NAME + " TEXT," +
                InterceptSmsDao.BODY + " TEXT," +
                InterceptSmsDao.DATE + " LONG" +
                ");";
        db.execSQL(excSql_create_intercept_sms);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        //
        String excSql_drop_black_list = "drop table if exists ";
        excSql_drop_black_list += SQLITE_TABLE_BLACK_LIST_TABLE;
        db.execSQL(excSql_drop_black_list);
        onCreate(db);
        //
        String excSql_drop_intercept_call_list = "drop table if exists ";
        excSql_drop_intercept_call_list += SQLITE_TABLE_INTERCEPT_CALL_TABLE;
        db.execSQL(excSql_drop_intercept_call_list);
        onCreate(db);
        //
        String excSql_drop_intercep_sms_list = "drop table if exists ";
        excSql_drop_intercep_sms_list += SQLITE_TABLE_INTERCEPT_SMS_TABLE;
        db.execSQL(excSql_drop_intercep_sms_list);
        onCreate(db);
    }
}
