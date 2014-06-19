
package cn.jetoo.numbermgr.query.floatingWindow;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import cn.jetoo.numbermgr.R;
import cn.jetoo.numbermgr.common.ImageUtils;


public class FloatingView extends FrameLayout {

    private static final String TAG = "PhoneFloatingView";
    private static final boolean DEBUG = cn.jetoo.numbermgr.BuildConfig.DEBUG;

    private ImageButton mClose;
    private TextView mName;
    private TextView mNumber;
    private TextView mLocation;
    private ImageView mImage;

    private OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FloatingManager.getInstance(getContext()).detachFromWindow();
        }
    };

    public FloatingView(Context context) {
        super(context);
    }

    public FloatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.floating_anim);
        ((ImageView) findViewById(R.id.caller_image_progress)).startAnimation(anim);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mClose = (ImageButton) findViewById(R.id.bt_close);
        mName = (TextView) findViewById(R.id.call_name);
        mNumber = (TextView) findViewById(R.id.phone_number);
        mLocation = (TextView) findViewById(R.id.phone_location);
        mImage = (ImageView) findViewById(R.id.caller_image);
        mClose.setOnClickListener(mClickListener);
    }

    public void setPhoneLocation(String name, String number, String loc, Bitmap image, boolean incomming) {

        if (TextUtils.isEmpty(name)) {
            mName.setText("");
        } else {
            mName.setText(name);
        }
        if (TextUtils.isEmpty(number)) {
            mNumber.setText("");
        } else {
            mNumber.setText(number);
        }
        if (TextUtils.isEmpty(loc)) {
            mLocation.setText("未知");
        } else {
            mLocation.setText(loc);
        }

        if (image != null) {
            int dis =getResources().getDimensionPixelSize(R.dimen.float_window_image_size);
            image = ImageUtils.scaleTo(image, dis, dis, true);
            mImage.setImageBitmap(ImageUtils.getRoundBitmap(image, dis, true));
        }
    }

}
