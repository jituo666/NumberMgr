/*
 * Copyright (C) 2012 Tapas Mobile Ltd.  All Rights Reserved.
 */

package cn.jetoo.numbermgr.telephony;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

public class ITelephonyCompat {
    private static final boolean DEBUG = false;
    private static final String TAG = "ITelephonyCompat";

    private static final int APN_TYPE_NOT_AVAILABLE = 2;
    private static final int APN_REQUEST_FAILED     = 3;

    private static final String APN_DEFAULT = "default";
    private static final String CLASSNAME_ITELEPONY = "com.android.internal.telephony.ITelephony";

    private static Method sEnableApnTypeMethod = null;
    private static Method sDisableApnTypeMethod = null;
    private static Method sEnableDataConnectivityMethod = null;
    private static Method sDisableDataConnectivityMethod = null;
    private static Method sEndCallMethod = null;

    private static Method sGetITelephonyMethod = null;

    static {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(CLASSNAME_ITELEPONY, false, Thread.currentThread()
                    .getContextClassLoader());
        } catch (ClassNotFoundException e) {
            if (DEBUG) e.printStackTrace();
        }
        if (clazz != null) {
            try {
                Class<?>[] args1 = new Class[] { String.class };
                Class<?>[] args2 = new Class[0];
                sEnableApnTypeMethod = clazz.getDeclaredMethod("enableApnType", args1);
                sDisableApnTypeMethod = clazz.getDeclaredMethod("disableApnType", args1);
                sEnableDataConnectivityMethod = clazz.getDeclaredMethod("enableDataConnectivity",
                        args2);
                sDisableDataConnectivityMethod = clazz.getDeclaredMethod("disableDataConnectivity",
                        args2);
                sEndCallMethod = clazz.getDeclaredMethod("endCall", args2);
            } catch (Exception e) {
            }
        }
        try {
            sGetITelephonyMethod = TelephonyManager.class.getDeclaredMethod("getITelephony",
                    new Class[0]);
            sGetITelephonyMethod.setAccessible(true);
        } catch (Exception e) {
        }
    }

    private static Object getITelephony(Context context) {
        if (sGetITelephonyMethod != null) {
            try {
                TelephonyManager obj = (TelephonyManager) context.getSystemService("phone");
                Method localMethod = sGetITelephonyMethod;
                Object[] arrayOfObject = new Object[0];
                return localMethod.invoke(obj, arrayOfObject);
            } catch (IllegalAccessException localIllegalAccessException) {
                // ignore this, will to the final
            } catch (InvocationTargetException localInvocationTargetException) {
                // ignore this, will to the final
            }
        }
        // if anything wrong, will be here
        if (DEBUG) Log.e(TAG, "getITelephony failure");
        return null;
    }

    public static boolean setApnEnabled(Context context) {
        if (sEnableDataConnectivityMethod != null && sGetITelephonyMethod != null) {
            try {
                Object obj = getITelephony(context);
                Method localMethod = sEnableApnTypeMethod;
                Object[] arrayOfObject = new Object[] { APN_DEFAULT };
                Object ret = localMethod.invoke(obj, arrayOfObject);
                int retv = (Integer) ret;
                if (retv == APN_TYPE_NOT_AVAILABLE || retv == APN_REQUEST_FAILED) {
                    return false;
                }
                localMethod = sEnableDataConnectivityMethod;
                ret = localMethod.invoke(obj, new Object[0]);
                return (Boolean) ret;
            } catch (IllegalAccessException localIllegalAccessException) {
                // ignore this, will to the final
            } catch (InvocationTargetException localInvocationTargetException) {
                // ignore this, will to the final
            }
        }
        return false;
    }

    public static boolean setApnDisabled(Context context) {
        if (sDisableDataConnectivityMethod != null && sGetITelephonyMethod != null) {
            try {
                Object obj = getITelephony(context);
                Method localMethod = sDisableApnTypeMethod;
                Object[] arrayOfObject = new Object[] { APN_DEFAULT };
                Object ret = localMethod.invoke(obj, arrayOfObject);
                int retv = (Integer) ret;
                if (retv == APN_TYPE_NOT_AVAILABLE || retv == APN_REQUEST_FAILED) {
                    return false;
                }
                localMethod = sDisableDataConnectivityMethod;
                ret = localMethod.invoke(obj, new Object[0]);
                return (Boolean) ret;
            } catch (IllegalAccessException localIllegalAccessException) {
                // ignore this, will to the final
            } catch (InvocationTargetException localInvocationTargetException) {
                // ignore this, will to the final
            }
        }
        return false;
    }

    public static boolean endCall(Context context) {
        if (sEndCallMethod != null && sGetITelephonyMethod != null) {
            try {
                Object obj = getITelephony(context);
                Method localMethod = sEndCallMethod;
                Object ret = localMethod.invoke(obj, new Object[0]);
                return (Boolean) ret;
            } catch (IllegalAccessException localIllegalAccessException) {
                // ignore this, will to the final
            } catch (InvocationTargetException localInvocationTargetException) {
                // ignore this, will to the final
            }
        }
        return false;
    }
}
