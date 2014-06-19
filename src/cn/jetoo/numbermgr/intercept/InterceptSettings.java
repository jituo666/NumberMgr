package cn.jetoo.numbermgr.intercept;

import android.content.Context;

import cn.jetoo.numbermgr.intercept.data.BlackListDao;
import cn.jetoo.numbermgr.settings.SettingsActivity;

public class InterceptSettings {
    public static boolean IsCallInterceptNeed(Context context, String phoneNumber) {
        if (SettingsActivity.IsInterceptEnabled(context)) {
            if (BlackListDao.queryBlackListItem(context, phoneNumber)) {
                return true;
            }
        }
        return false;
    }

    public static boolean IsSmsInterceptNeed(Context context, String phoneNumber) {
        if (SettingsActivity.IsInterceptEnabled(context)) {
            if (BlackListDao.queryBlackListItem(context, phoneNumber)) {
                return true;
            }
        }
        return false;
    }
}
