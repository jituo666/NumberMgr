package cn.jetoo.numbermgr;

import android.app.Application;

import cn.jetoo.numbermgr.query.codec.LocationCodec;
import cn.jetoo.numbermgr.query.codec.NaiveLocationCodec;

public class NumberMgrApp extends Application {

    @Override
    public void onCreate() {
        new Thread() {
            public void run() {
                try {
                    LocationCodec instance = NaiveLocationCodec.getInstance();
                    if (instance != null)
                        instance.init(NumberMgrApp.this);
                } catch (Exception e) {
                }
            }
        }.start();
        super.onCreate();
    }

}
