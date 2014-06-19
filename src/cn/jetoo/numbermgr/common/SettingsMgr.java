
package cn.jetoo.numbermgr.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class SettingsMgr {

    private static final String KEY_INCOMING_FLOATING_VIEW_ENABLED = "incoming_enabled";
    private static final String KEY_OUTGOING_FLOATING_VIEW_COORDINATE_X = "outgoing_enabled";

    private static SharedPreferences sPrefs = null;

    private static SharedPreferences initSharedPreferences(Context ctx) {
        if (sPrefs == null) {
            sPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        }
        return sPrefs;
    }

    public static void setIncomingFloatingWindowEnabled(Context cxt, boolean enabled) {
        SharedPreferences prefs = initSharedPreferences(cxt);
        SharedPreferencesCompat.apply(prefs.edit().putBoolean(KEY_INCOMING_FLOATING_VIEW_ENABLED, enabled));
    }

    public static boolean getIncomingFloatingWindowEnabled(Context cxt) {
        SharedPreferences prefs = initSharedPreferences(cxt);
        return prefs.getBoolean(KEY_INCOMING_FLOATING_VIEW_ENABLED, true);
    }

    public static void setOutgoingFloatingWindowEnabled(Context cxt, boolean enabled) {
        SharedPreferences prefs = initSharedPreferences(cxt);
        SharedPreferencesCompat.apply(prefs.edit().putBoolean(KEY_OUTGOING_FLOATING_VIEW_COORDINATE_X, enabled));
    }

    public static boolean getOutgoingFloatingWindowEnabled(Context cxt) {
        SharedPreferences prefs = initSharedPreferences(cxt);
        return prefs.getBoolean(KEY_OUTGOING_FLOATING_VIEW_COORDINATE_X, true);
    }

}
