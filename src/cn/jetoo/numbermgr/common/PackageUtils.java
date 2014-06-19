package cn.jetoo.numbermgr.common;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import cn.jetoo.numbermgr.utils.FeatureConfig;

public class PackageUtils {

    private static final String TAG = "PackageUtils";
    private static final boolean DEBUG = FeatureConfig.DEBUG_LOG;

    public static boolean isPkgInstalled(Context context, String pkgName) {
        return isPkgInstalled(context, pkgName, 0);
    }

    public static boolean isPkgInstalled(Context context, String pkgName, int flags) {
        PackageManager pm = context.getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(pkgName, flags);
            installed = true;
        } catch (NameNotFoundException e) {
            // ignore the exception
        }
        return installed;
    }
}
