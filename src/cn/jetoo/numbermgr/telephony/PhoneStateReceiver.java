package cn.jetoo.numbermgr.telephony;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import cn.jetoo.numbermgr.common.Constants;
import cn.jetoo.numbermgr.common.SettingsMgr;
import cn.jetoo.numbermgr.intercept.InterceptSettings;
import cn.jetoo.numbermgr.intercept.bean.InterceptCall;
import cn.jetoo.numbermgr.intercept.data.BlackListDao;
import cn.jetoo.numbermgr.intercept.data.IntercepCallDao;
import cn.jetoo.numbermgr.query.LocationService;
import cn.jetoo.numbermgr.query.floatingWindow.FloatingManager;
import cn.jetoo.numbermgr.settings.SettingsActivity;
import cn.jetoo.numbermgr.utils.FeatureConfig;
import cn.jetoo.numbermgr.utils.LogHelper;

public class PhoneStateReceiver extends BroadcastReceiver {

    private static final String TAG = "PhoneStateReceiver";
    private static final boolean DEBUG = FeatureConfig.DEBUG_LOG;
    
    private static final String INTENT_DATA_CALL = "incoming_number";
    private static boolean sIsIncoming = false;

    //

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            if (SettingsActivity.IsIpDialerEnabled(context)) {
                String phone = getResultData();
                setResultData(SettingsActivity.getDialerPrefix(context) + phone);
            }
            if (SettingsMgr.getOutgoingFloatingWindowEnabled(context)) {
                String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                Intent it = new Intent(context, LocationService.class);
                it.setAction(Constants.QL_ACTION_OUTGONIG_CALL);
                it.putExtra(Constants.QL_PHONE_NUMBER_FIELD, phoneNumber);
                context.startService(it);
            }
        } else {
            // 来电
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            switch (tm.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING: {
                    String phoneNumber = TelephonyUtils.stripPrefix(intent.getStringExtra(INTENT_DATA_CALL));
                    if (phoneNumber != null && phoneNumber.length() > 0 &&
                            InterceptSettings.IsCallInterceptNeed(context, phoneNumber)) {
                        interceptCall(context, phoneNumber);
                    } else if (SettingsMgr.getIncomingFloatingWindowEnabled(context)) {
                        sIsIncoming = true;
                        Intent it = new Intent(context, LocationService.class);
                        it.setAction(Constants.QL_ACTION_INCOMING_CALL);
                        it.putExtra(Constants.QL_PHONE_NUMBER_FIELD, intent.getStringExtra(INTENT_DATA_CALL));
                        context.startService(it);
                    }
                    break;
                }
                case TelephonyManager.CALL_STATE_OFFHOOK: {
                    if (sIsIncoming) {
                        FloatingManager.getInstance(context).sendDelayedDetachIncMessage();
                        sIsIncoming = false;
                    }
                    break;
                }
                case TelephonyManager.CALL_STATE_IDLE: {
                    FloatingManager.getInstance(context).detachFromWindow();
                    sIsIncoming = false;
                    break;
                }
            }
        }
    }

    private void interceptCall(Context context, String number) {
        TelephonyMgr.hangup(context);
        InterceptCall call = new InterceptCall();
        call.phoneName = "";
        call.phoneNumber = number;
        call.callTime = System.currentTimeMillis();
        call.location = "";
        IntercepCallDao.insertIntercepCallItem(context, call);
    }
}
