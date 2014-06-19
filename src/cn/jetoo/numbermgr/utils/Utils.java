
package cn.jetoo.numbermgr.utils;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.Environment;
import android.text.ClipboardManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import cn.jetoo.numbermgr.query.codec.LocationCodec;
import cn.jetoo.numbermgr.query.codec.NaiveLocationCodec;

public class Utils {

    public static final int MAX_CALL_LOGS = 500;

    private static PackageInfo sPackageInfo;
    private static final LocationCodec LOCATION_CODEC = NaiveLocationCodec.getInstance();

    public static String getCityFromNumber(String number) {
        return getPhoneLocation(number, false, false);
    }

    public static String getPhoneLocation(String number) {
        return getPhoneLocation(number, true, true);
    }

    public static String getPhoneLocation(String number, boolean showProvince, boolean showOperator) {
        return LOCATION_CODEC.getLocation(number, showProvince, showOperator);
    }

    public static SpannableString getHighLightString(String originalText,
            String highLightMatchText, int highLightColor) {
        if (TextUtils.isEmpty(originalText) || TextUtils.isEmpty(highLightMatchText)) {
            return new SpannableString(TextUtils.isEmpty(originalText) ? "" : originalText);
        }
        SpannableString resultString = new SpannableString(originalText);

        for (int i = 0; i < highLightMatchText.length(); i++) {
            if (highLightMatchText.charAt(i) == '1') {
                resultString.setSpan(new ForegroundColorSpan(highLightColor), i, i + 1,
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }

        return resultString;
    }

    public static void copyToClipboard(Context context, String message) {
        ClipboardManager mgr = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        mgr.setText(message);
    }

    public static CharSequence getFromClipboard(Context context) {
        ClipboardManager mgr = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        return mgr.getText();
    }

    public static boolean isPhoneNumber(String number) {
        // TODO: more precise matches?
        return !TextUtils.isEmpty(number) && number.matches("[0-9\\-\\+\\(\\)\\ ]+");
    }

    public static boolean isIntentAvailable(Context context, final Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public static int getCurrentVersionCode(Context context) {
        PackageInfo info = getCurrentVersionInfo(context);
        if (info == null) {
            return -1;
        }
        return info.versionCode;
    }

    public static String getCurrentVersionName(Context context) {
        PackageInfo info = getCurrentVersionInfo(context);
        if (info == null) {
            return "";
        }
        return info.versionName;
    }

    public static PackageInfo getCurrentVersionInfo(Context context) {
        if (sPackageInfo == null) {
            try {
                sPackageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),
                        0);
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return sPackageInfo;
    }

    public static String[] getCopyOfArray(String[] array) {
        String[] res = new String[array.length];
        System.arraycopy(array, 0, res, 0, array.length);
        return res;
    }

    public static boolean isSdCardMounted() {
        return TextUtils.equals(Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED);
    }

    private static final String PREFERENCES_SPLITTER = ",";

    public static String[] splitValue(String value) {
        return value.split(PREFERENCES_SPLITTER);
    }

    public static String joinValues(Object[] values) {
        StringBuilder sb = new StringBuilder();
        if (values.length > 0) {
            sb.append(values[0].toString());
            for (int i = 1; i < values.length; i++) {
                sb.append(PREFERENCES_SPLITTER).append(values[i].toString());
            }
        }
        return sb.toString();
    }

    public static String getEffectivePartOfNumber(String number) {
        if (number.length() <= 11) {
            return number;
        }
        return number.substring(number.length() - 11);
    }

    static boolean isSpecialNumber(String number) {
        return TextUtils.equals(number, "-1")
                || TextUtils.equals(number, "-2")
                || TextUtils.equals(number, "-3");
    }
}
