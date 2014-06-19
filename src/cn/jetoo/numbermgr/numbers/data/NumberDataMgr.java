package cn.jetoo.numbermgr.numbers.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import cn.jetoo.numbermgr.R;
import cn.jetoo.numbermgr.common.SharedPreferencesCompat;
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

public class NumberDataMgr {

    private static final String TAG = "";
    private static final boolean DEBUG = FeatureConfig.DEBUG_LOG;

    private static final int FIELD_NUMBER = 1;
    private static final int FIELD_SORT = 2;
    private static final int FIELD_NAME = 3;

    private static NumberDataMgr mInstance;

    private static final String DEFAUT_SORT_LIST = "银行-保险-证券-网上购物-互联网-知名企业-快递服务-报警急救-运营商-外卖订餐-鲜花预定-航空公司-咨询服务-投诉举报-旅游预订";
    private static final String[] SORT_NAMES = new String[] {
            "银行",
            "保险",
            "证券",
            "网上购物",
            "互联网",
            "知名企业",
            "快递服务",
            "报警急救",
            "运营商",
            "外卖订餐",
            "鲜花预定",
            "航空公司",
            "咨询服务",
            "投诉举报",
            "旅游预订",
            "市政便民",
            "健康咨询",
            "租车热线",
            "连锁酒店",
            "公益团体",
            "大学热线",
            "演出预订",
            "高考查分",
            "机场热线",
            "租车热线",
            "教育培训",
            "铁路订票",
            "足疗保健",
            "婚恋网",
            "连锁火锅",
            "宜家家居"
    };

    private static final int[] SOR_ICON_RES_IDS = new int[] {
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
    };

    private NumberDataMgr(Context context) {
        copyNumbersDB(context);
    }

    public static NumberDataMgr getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NumberDataMgr(context);
        }
        return mInstance;
    }

    public void saveSortList(Context context, List<String> toBeSavedStrings) {
        StringBuffer result = new StringBuffer();
        for (String str : toBeSavedStrings) {
            result.append(str);
            result.append("-");
        }
        result.substring(0, result.length() - 1);
        setSortList(context, result.toString());
    }

    public List<String> loadList(Context context) {
        String saveString = getSortList(context);
        List<String> list = new ArrayList<String>();
        for (String str : saveString.split("-")) {
            list.add(str);
        }
        return list;
    }

    private void setSortList(Context context, String version) {
        String PREFS_NAME = "numbers_sort_list";
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("sort_list", version);
        SharedPreferencesCompat.apply(editor);
    }

    private String getSortList(Context context) {
        String sortList = "";
        String PREFS_NAME = "numbers_sort_list";
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        sortList = settings.getString("sort_list", DEFAUT_SORT_LIST);
        return sortList;
    }

    public List<String> querySortListFromDB(Context context) {
        List<String> list = new ArrayList<String>();
        try {
            DBHelper dbHelper = new DBHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            StringBuffer excSql = new StringBuffer();
            excSql.append("select sort ");
            excSql.append(" from ");
            excSql.append(DBHelper.SQLITE_TABLE_NUMBERS);
            excSql.append(" group by sort");
            if (DEBUG) {
                LogHelper.i(TAG, "excSql:" + excSql);
            }
            Cursor cursor = db.rawQuery(excSql.toString(), null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    list.add(cursor.getString(0));
                }
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<NumberDetail> querySet(Context context, String sort) {
        List<NumberDetail> list = new ArrayList<NumberDetail>();
        try {
            DBHelper dbHelper = new DBHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            StringBuffer excSql = new StringBuffer();
            excSql.append("select * from ");
            excSql.append(DBHelper.SQLITE_TABLE_NUMBERS);
            excSql.append(" where sort=\'" + sort + "\'");

            if (DEBUG) {
                LogHelper.i(TAG, "excSql:" + excSql);
            }

            Cursor cursor = db.rawQuery(excSql.toString(), null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    NumberDetail info = new NumberDetail();
                    info.phoneNumber = cursor.getString(FIELD_NUMBER);
                    info.phoneName = cursor.getString(FIELD_NAME);
                    list.add(info);
                }
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void copyNumbersDB(Context context) {

        File file = context.getDatabasePath(DBHelper.SQLITE_NAME);

        if (!file.exists()) {
            try {
                File fileDir = new File(file.getParent());
                fileDir.mkdirs();
                CopyFile(context, file);
                writeNumbersDBVersion(context, DBHelper.NUMBRT_DB_VERSION);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                int savedVersion = 0;
                savedVersion = readNumbersDBVersion(context);
                if (savedVersion < DBHelper.NUMBRT_DB_VERSION) {
                    CopyFile(context, file);
                    writeNumbersDBVersion(context, DBHelper.NUMBRT_DB_VERSION);
                }
            } catch (Exception e) {

            }
        }
    }

    private void CopyFile(Context context, File file) throws IOException {
        InputStream myInput = context.getAssets().open(DBHelper.SQLITE_NAME);
        OutputStream myOutput = new FileOutputStream(file);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    private void writeNumbersDBVersion(Context context, int version) {
        String PREFS_NAME = "numbers_db_version";
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("db_version", version);
        SharedPreferencesCompat.apply(editor);
    }

    private int readNumbersDBVersion(Context context) {
        int version = 0;
        String PREFS_NAME = "numbers_db_version";
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        version = settings.getInt("db_version", 0);
        return version;
    }
}
