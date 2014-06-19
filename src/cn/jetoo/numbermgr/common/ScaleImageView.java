
package cn.jetoo.numbermgr.common;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ScaleImageView extends ImageView {

    public ScaleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (heightMeasureSpec <= 0) {
            Drawable drawable = getDrawable();
            if (drawable != null) {
                final int rwidth = getMeasuredWidth();
                int width = drawable.getIntrinsicWidth();
                if (width > 0 && rwidth != width) {
                    int height = drawable.getIntrinsicHeight();
                    float aspect = 1f * height / width;
                    final int rheight = (int) (rwidth * aspect);
                    if (rheight != getMeasuredHeight()) {
                        setMeasuredDimension(rwidth, rheight);
                    }
                }
            }
        }
    }

}
