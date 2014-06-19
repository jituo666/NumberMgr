package cn.jetoo.numbermgr.settings;

import android.content.Context;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import cn.jetoo.numbermgr.R;
import cn.jetoo.numbermgr.telephony.TelephonyUtils;
import cn.jetoo.numbermgr.telephony.TelephonyUtils.FirewallRingtone;

public class SettingsActivity extends PreferenceActivity {

    private static final String SETTINGS_INCOMMING_CALL_LOCATION_SHOW = "inc_show";
    private static final String SETTINGS_OUTGOING_CALL_LOCATION_SHOW = "out_show";
    private static final String SETTINGS_IP_DIALER_SIWTCH = "ip_dialer_enbled";
    private static final String SETTINGS_IP_DIALER_PREFIX = "ip_prefix";
    private static final String SETTINGS_IP_LOCAL_ID = "ip_loc";
    private static final String SETTINGS_INTERCEPT_SWITCH = "intercept_enabled";
    private static final String SETTINGS_INTERCEPT_RINGTON = "intercept_ringtone";

    private ListPreference mIPPrefixPref;
    private EditTextPreference mIPLocalAreaID;
    private ListPreference mIntercpetRingtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        mIPPrefixPref = (ListPreference) findPreference(SETTINGS_IP_DIALER_PREFIX);
        String ip_prefix = mIPPrefixPref.getValue();
        if (ip_prefix != null && ip_prefix.length() > 0) {
            mIPPrefixPref.setSummary(mIPPrefixPref.getValue().toString());
        }
        mIPPrefixPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary(newValue.toString());
                return true;
            }
        });

        mIPLocalAreaID = (EditTextPreference) findPreference(SETTINGS_IP_LOCAL_ID);
        String local_area_id = mIPLocalAreaID.getText();
        if (local_area_id != null && local_area_id.length() > 0) {
            mIPLocalAreaID.setSummary(mIPLocalAreaID.getText().toString());
        }
        mIPLocalAreaID.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary(newValue.toString());
                return true;
            }
        });

        mIntercpetRingtone = (ListPreference) findPreference(SETTINGS_INTERCEPT_RINGTON);
        String intercpet_ringtone = mIntercpetRingtone.getValue();
        if (intercpet_ringtone != null && intercpet_ringtone.length() > 0) {
            mIntercpetRingtone.setSummary(mIntercpetRingtone.getValue().toString());
        }
        mIntercpetRingtone.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary(newValue.toString());
                TelephonyUtils.setBusyCallForwarding(SettingsActivity.this,FirewallRingtone.fromValue(getIntValueFromRington(newValue.toString())));
                return true;
            }
        });
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private int getIntValueFromRington(String value) {
        String []array = this.getResources().getStringArray(R.array.intercept_ringtone_list_preference);
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) {
                return i;
            }
        }
        return 0;
    }
    public static boolean IsIncommingCallShowLocation(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                SETTINGS_INCOMMING_CALL_LOCATION_SHOW, false);
    }

    public static boolean IsOutgoingCallShowLocation(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                SETTINGS_OUTGOING_CALL_LOCATION_SHOW, false);
    }

    public static boolean IsIpDialerEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SETTINGS_IP_DIALER_SIWTCH,
                false);
    }

    public static String getDialerPrefix(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(SETTINGS_IP_DIALER_PREFIX,
                "17951");
    }

    public static String getIpLocationID(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(SETTINGS_IP_LOCAL_ID, "010");
    }

    public static boolean IsInterceptEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SETTINGS_INTERCEPT_SWITCH,
                false);
    }

    public static String IsInterceptRingTone(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(SETTINGS_INTERCEPT_RINGTON,
                "");
    }
}
