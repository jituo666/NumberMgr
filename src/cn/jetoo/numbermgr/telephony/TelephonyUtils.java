package cn.jetoo.numbermgr.telephony;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.TelephonyManager;

public class TelephonyUtils {

    private static final Set<String> IP_DIAL_PREFIX = new HashSet<String>() {
        /**
         *
         */
        private static final long serialVersionUID = 8258948768278578200L;

        {
            add("17951");
            add("12593");
            add("17911");
            add("17901");
            add("11801");
            add("10193");
        }
    };
    private static final Pattern ILLEGAL_NUMBER_PATTERN = Pattern.compile("[^+\\d]");

    public static String stripPrefix(String number)
    {
        if (number == null) {
            return null;
        }

        if (number.length() <= 11)
            return number;
        if ((number.startsWith("+86")) || (number.startsWith("86")) || (number.startsWith("086"))) {
            number = number.substring(number.indexOf('6') + 1);
        }
        return stripIpPrefix(number, null);
    }

    public static String stripIpPrefix(String number, String customeIpPrefix)
    {
        if ((customeIpPrefix != null) && (number.startsWith(customeIpPrefix))) {
            return number.substring(customeIpPrefix.length());
        }
        return stripIpPrefix(number);
    }

    public static String stripIpPrefix(String number) {
        number = ILLEGAL_NUMBER_PATTERN.matcher(number).replaceAll("");

        if (number.length() > 5) {
            String prefix = number.substring(0, 5);
            if (IP_DIAL_PREFIX.contains(prefix)) {
                number = number.substring(5);
            }
        }
        return number;
    }

    public enum FirewallRingtone {

        BUSY(0),
        POWER_OFF(1),
        OUT_OF_SERVICE(2),
        NUMBER_NOT_EXIST(3);

        private static final int DEFAULT = 4;
        private int mValueId;

        private FirewallRingtone(int valueId) {
            mValueId = valueId;
        }

        public int getValue() {
            return mValueId;
        }

        public static FirewallRingtone fromValue(int value) {
            for (FirewallRingtone c : FirewallRingtone.values()) {
                if (value == c.getValue()) {
                    return c;
                }
            }
            return getDefault();
        }

        public static FirewallRingtone getDefault() {
            for (FirewallRingtone c : FirewallRingtone.values()) {
                if (DEFAULT == c.getValue()) {
                    return c;
                }
            }
            return null;
        }

    }

    public static final int OPER_CHINAMOBILE = 0;
    public static final int OPER_CHINAUNICOM = 1;
    public static final int OPER_CHINATELECOM = 2;
    public static final int OPER_OTHER = 3;
    public static final int OPER_NONE = -1;

    public static int getOperator(Context ctx) {
        TelephonyManager telManager = (TelephonyManager) ctx
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (telManager.getSimState() != TelephonyManager.SIM_STATE_READY) {
            return OPER_NONE;
        }
        String operator = telManager.getSimOperator();
        if (operator != null) {
            if (operator.equals("46000") || operator.equals("46002") || operator.equals("46007")) {
                // China Mobile
                return OPER_CHINAMOBILE;
            } else if (operator.equals("46001") || operator.equals("46006")) {
                // China Unicom
                return OPER_CHINAUNICOM;
            } else if (operator.equals("46003") || operator.equals("46005")) {
                // China Telecom
                return OPER_CHINATELECOM;
            } else {
                return OPER_OTHER;
            }
        }
        return OPER_NONE;
    }

    public static boolean setBusyCallForwarding(Context context, FirewallRingtone ringtone) {
        String number;
        boolean result = true;
        if (TelephonyUtils.getOperator(context) == TelephonyUtils.OPER_CHINATELECOM) {
            switch (ringtone) {
                case NUMBER_NOT_EXIST:
                    number = "*9013800000000";
                    break;
                case OUT_OF_SERVICE:
                    number = "*9013910827493";
                    break;
                case POWER_OFF:
                    number = "*9013716919709";
                    break;
                default:
                    number = "*900";
            }
        } else {
            switch (ringtone) {
                case NUMBER_NOT_EXIST:
                    number = "**67*13800000000%23";
                    break;
                case OUT_OF_SERVICE:
                    number = "**67*13910827493%23";
                    break;
                case POWER_OFF:
                    number = "**67*13716919709%23";
                    break;
                default:
                    number = "%23%2367%23";
            }
        }
        Intent intent = new Intent("android.intent.action.CALL");
        Uri uri = Uri.parse("tel:" + number);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    public static boolean endCall(Context context) {
        return ITelephonyCompat.endCall(context);
    }
}
