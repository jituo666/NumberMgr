package cn.jetoo.numbermgr.numbers.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public final static String SQLITE_NAME = "numbers.db";
    public final static String SQLITE_TABLE_NUMBERS = "numbers";
    public final static int NUMBRT_DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, SQLITE_NAME, null, NUMBRT_DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String excSql = "create table if not exists ";
        excSql += SQLITE_TABLE_NUMBERS;
        excSql += " (_id INTEGER  primary key autoincrement, number TEXT, sort TEXT, name TEXT)";
        db.execSQL(excSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        String excSql = "drop table if exists ";
        excSql += SQLITE_TABLE_NUMBERS;
        db.execSQL(excSql);
        onCreate(db);
    }
}
