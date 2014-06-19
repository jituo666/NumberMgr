
package cn.jetoo.numbermgr.query.floatingWindow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import cn.jetoo.numbermgr.R;
import cn.jetoo.numbermgr.utils.FeatureConfig;

public class FloatingManager {

    private static final String TAG = "FloatingManager";
    private static final boolean DEBUG = FeatureConfig.DEBUG_LOG;

    private static FloatingManager mInstance;
    private Context mContext;
    private WindowManager mWindowManager;
    private TelephonyManager mTelephonyManager;
    private FloatingView mFloatingView;
    private LayoutParams mFloatingViewParams;
    private boolean mAttachedToWindow = false;

    private static final int DETACH_FROM_WINDOW = 1;
    private static final int DETACH_FROM_WINDOW_INCOMMING_DELAY = 3000;
    private static final int DETACH_FROM_WINDOW_OUTGOING_DELAY = 5000;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DETACH_FROM_WINDOW: {
                    detachFromWindow();
                    break;
                }
            }
        }
    };

    public static FloatingManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new FloatingManager(context);
        }
        return mInstance;
    }

    private FloatingManager(Context context) {
        mContext = context;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        initFloatingView();
    }

    private void initFloatingView() {
        mFloatingView = (FloatingView) LayoutInflater.from(mContext).inflate(R.layout.floating_window, null);
        mFloatingView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    detachFromWindow();
                }
                return true;
            }
        });
        mFloatingViewParams = new WindowManager.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_SYSTEM_ERROR,
                LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | LayoutParams.FLAG_LAYOUT_INSET_DECOR |
                LayoutParams.FLAG_FORCE_NOT_FULLSCREEN | LayoutParams.FLAG_NOT_TOUCH_MODAL |
                LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
        mFloatingViewParams.gravity = Gravity.LEFT | Gravity.TOP;

    }

    public void attachToWindow(String name, String number, String loc, Bitmap image, boolean incomming) {
        if (TelephonyManager.SIM_STATE_READY != mTelephonyManager.getSimState()
                || mAttachedToWindow || TextUtils.isEmpty(loc)) {
            return;
        }

        mWindowManager.addView(mFloatingView, mFloatingViewParams);
        mFloatingView.setPhoneLocation(name, number, loc, image, incomming);
        if (!incomming) { // 去电
            sendDelayedDetachOutMessage();
        }
        mAttachedToWindow = true;
    }

    public void detachFromWindow() {
        if (!mAttachedToWindow) {
            return;
        }
        mWindowManager.removeView(mFloatingView);
        mAttachedToWindow = false;
    }

    public void sendDelayedDetachIncMessage() {
        mHandler.sendEmptyMessageDelayed(DETACH_FROM_WINDOW, DETACH_FROM_WINDOW_INCOMMING_DELAY);
    }

    public void sendDelayedDetachOutMessage() {
        mHandler.sendEmptyMessageDelayed(DETACH_FROM_WINDOW, DETACH_FROM_WINDOW_OUTGOING_DELAY);
    }
}
