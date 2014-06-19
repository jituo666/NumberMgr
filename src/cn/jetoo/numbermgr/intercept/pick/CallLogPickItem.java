package cn.jetoo.numbermgr.intercept.pick;

public class CallLogPickItem {
    public String phoneNumber = "";
    public String phoneName = "";
    public String loc = "";
    public long lastCallTime = 0;
    public int type = 1; // incoming ,outgoing, missed
    public boolean isSelected = false;
}
