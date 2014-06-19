package cn.jetoo.numbermgr.telephony;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import cn.jetoo.numbermgr.intercept.bean.InterceptSms;
import cn.jetoo.numbermgr.intercept.data.BlackListDao;
import cn.jetoo.numbermgr.intercept.data.InterceptSmsDao;
import cn.jetoo.numbermgr.settings.SettingsActivity;
import cn.jetoo.numbermgr.utils.FeatureConfig;
import cn.jetoo.numbermgr.utils.LogHelper;

public class SmsReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsReceiver";
    private static final boolean DEBUG = FeatureConfig.DEBUG_LOG;

    private static final String SMS_EXTRA_NAME = "pdus";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || (intent.getAction()) == null)
            return;
        boolean isInterceptEnabled = SettingsActivity.IsInterceptEnabled(context);
        if (!isInterceptEnabled) {
            return;
        }
        if (DEBUG) {
            LogHelper.d(TAG, " isInterceptEnabled:" + isInterceptEnabled);
        }
        SmsMessage[] messages = getMessagesFromIntent(intent);
        SmsInfoBase[] smsInfoBases = spliceSmsContent(messages);
        for (SmsInfoBase smsMessage : smsInfoBases) {
            analyzeSms(context, smsMessage);
        }
    }

    public void analyzeSms(Context context, SmsInfoBase smsMessage) {
        String uniqueNumber = getUniqueNumber(smsMessage.originatingAddress);
        uniqueNumber = TelephonyUtils.stripPrefix(uniqueNumber);
        boolean isNumberInBlackList = BlackListDao.queryBlackListItem(context, uniqueNumber);
        if (DEBUG) {
            LogHelper.d(TAG, " sms intercept phone number:" + uniqueNumber + " isNumberInBlackList:"
                    + isNumberInBlackList);
        }
        if (isNumberInBlackList) {
            abortBroadcast();
            InterceptSms sms = new InterceptSms();
            sms.phoneName = "";
            sms.phoneNumber = uniqueNumber;
            sms.smsContent = smsMessage.messageBody;
            sms.smsDate = smsMessage.timestampMillis;
            InterceptSmsDao.insertIntercepSmsItem(context, sms);
        }
    }

    private SmsMessage[] getMessagesFromIntent(Intent intent) {
        Object[] messages = (Object[]) intent.getSerializableExtra(SMS_EXTRA_NAME);
        if (messages == null) {
            return null;
        }
        if (messages.length == 0) {
            return null;
        }

        byte[][] pduObjs = new byte[messages.length][];

        for (int i = 0; i < messages.length; i++) {
            pduObjs[i] = (byte[]) messages[i];
        }
        byte[][] pdus = new byte[pduObjs.length][];
        int pduCount = pdus.length;
        SmsMessage[] msgs = new SmsMessage[pduCount];
        for (int i = 0; i < pduCount; i++) {
            pdus[i] = pduObjs[i];
            msgs[i] = SmsMessage.createFromPdu(pdus[i]);
        }
        return msgs;
    }

    private SmsInfoBase[] spliceSmsContent(SmsMessage[] msgs) {
        HashMap<String, SmsInfoBase> smsMap = new HashMap<String, SmsInfoBase>();
        for (SmsMessage sms : msgs) {
            String originatinAddress = sms.getOriginatingAddress();
            String content = sms.getDisplayMessageBody();
            long timeStamp = sms.getTimestampMillis();
            if (!smsMap.containsKey(originatinAddress)) {
                SmsInfoBase smsInfo = new SmsInfoBase(originatinAddress, content, timeStamp);
                smsMap.put(originatinAddress, smsInfo);
            } else {
                String previousparts = smsMap.get(originatinAddress).messageBody;
                String msgString = previousparts + content;
                SmsInfoBase smsInfo = new SmsInfoBase(originatinAddress, msgString, timeStamp);
                smsMap.put(originatinAddress, smsInfo);
            }
        }

        Iterator<String> iterator = smsMap.keySet().iterator();
        ArrayList<SmsInfoBase> smsInfoList = new ArrayList<SmsReceiver.SmsInfoBase>();
        while (iterator.hasNext()) {
            smsInfoList.add(smsMap.get(iterator.next()));
        }
        return (SmsInfoBase[]) smsInfoList.toArray(new SmsInfoBase[0]);
    }

    private final class SmsInfoBase {
        private String originatingAddress;
        private String messageBody;
        private long timestampMillis;
        private int spamType;

        private SmsInfoBase() {
        };

        private SmsInfoBase(String address, String content) {
            this.originatingAddress = address;
            this.messageBody = content;
        }

        private SmsInfoBase(String address, String content, long timeStamp) {
            this.originatingAddress = address;
            this.messageBody = content;
            this.timestampMillis = timeStamp;
        }
    }

    public static String getUniqueNumber(String number) {
        // input +86 123--4-5-6 78
        // output +8612345678
        String validateNumber = android.telephony.PhoneNumberUtils.stripSeparators(number);
        if (TextUtils.isEmpty(validateNumber)) {
            return null;
        }
        if (validateNumber.length() <= 11) {
            return validateNumber;
        } else {
            return validateNumber.substring(validateNumber.length() - 11);
        }
    }

}
