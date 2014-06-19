
package cn.jetoo.numbermgr.common;

public class Constants {
    public static final String QL_PHONE_NUMBER_FIELD = "phone_number";
    public static final String QL_ACTION_INCOMING_CALL = "ql_incoming_call";
    public static final String QL_ACTION_OUTGONIG_CALL = "ql_outgoing_call";
    public static final String QL_ACTION_END_CALL = "end_call";


    public static final long MINUTE_MS = 1000l * 60;
    public static final long HOUR_MS = MINUTE_MS * 60;
    public static final long DAY_MS = HOUR_MS * 24;
    public static final long HALF_DAY_MS = HOUR_MS * 12;

    //statistic items
    public static final boolean QUERYLOCATION_STATS = true;
    public static final String ST_KEY_QUERYLOCATION_CATAGORY = "querylocaiotn";
    public static final String ST_KEY_QUERYLOCATION_INCOMING = "inc_ifo"; // 来电归属地状态信息
    public static final String ST_KEY_QUERYLOCATION_OUTGOING= "out_ifo"; // 去电归属地状态信息
}
