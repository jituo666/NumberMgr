package cn.jetoo.numbermgr.telephony;

import android.content.Context;

public class TelephonyMgr {

    public static void hangup(Context context) {
        TelephonyUtils.endCall(context);
    }

}
