package com.gm375.vidshare;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class VideoPreview extends SurfaceView {
    private float mAspectRatio;
    private int mHorizontalTileSize = 1;
    private int mVerticalTileSize = 1;
    
    public static float DONT_CARE = 0.0f;
    
    public VideoPreview(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
    
    public VideoPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }
    
    public VideoPreview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }
    
    public void setAspectRatio(int width, int height) {
        setAspectRatio(((float) width) / ((float) height));
    }

    public void setAspectRatio(float aspectRatio) {
        if (mAspectRatio != aspectRatio) {
            mAspectRatio = aspectRatio;
            requestLayout();
            invalidate();
        }
    }
    
}
