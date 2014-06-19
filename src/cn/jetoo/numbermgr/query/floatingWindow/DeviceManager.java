
package cn.jetoo.numbermgr.query.floatingWindow;

import android.os.Build;

import java.util.HashMap;

public class DeviceManager {

    private static final String GALAXY_NEXUS = "Galaxy Nexus";
    private static final String GT_I9100 = "GT-I9100";
    private static final String HTC_SENSE = "HTC Sensation Z710e";
    private static final String HUA_WEI_C8826_D = "huawei-c8826D";
    private static final String GT_I9108 = "GT-I9108";
    private static final String MOTO_MB525 = "MB525";
    private static final String SONY_ERICSSON_LT15I = "LT15i";
    private static final String KP_7260 = "7260+";
    private static final String LENOVO_A60 = "Lenovo A60";
    private static final String LENOVO_S720 = "Lenovo S720";
    private static final String GT_I9000 = "GT-I9000";
    private static final String MI_2 = "MI 2";
    private static final String BLADE = "blade";
    private static final String ZTE_C_N700 = "ZTE-C N700";

    private static final HashMap<String, Integer> sMarginTop = new HashMap<String, Integer>();

    static {
        sMarginTop.put(GALAXY_NEXUS, 39);
        sMarginTop.put(GT_I9100, 30);
        sMarginTop.put(HTC_SENSE, 112);
        sMarginTop.put(HUA_WEI_C8826_D, 36);
        sMarginTop.put(GT_I9108, 100);
        sMarginTop.put(MOTO_MB525, 60);
        sMarginTop.put(SONY_ERICSSON_LT15I, 24);
        sMarginTop.put(GT_I9000, 4);
        sMarginTop.put(MI_2, 224);
        sMarginTop.put(LENOVO_A60, 36);
        sMarginTop.put(LENOVO_S720, 60);
        sMarginTop.put(KP_7260, 21);
        sMarginTop.put(BLADE, 100);
        sMarginTop.put(ZTE_C_N700, 34);
    }

    public static int getCoordinateY() {
        if (sMarginTop.containsKey(Build.MODEL)) {
            return sMarginTop.get(Build.MODEL);
        }
        return 144;
    }
}
