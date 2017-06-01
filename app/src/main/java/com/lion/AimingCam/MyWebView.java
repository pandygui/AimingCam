package com.lion.AimingCam;

import android.content.Context;
import android.support.annotation.Px;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

/**
 * Created by Lion on 2017/5/9.
 */

public class MyWebView extends WebView {

    private OnWebViewInteractionListener listener;

    private void bindListener(Context context) {
        if (context instanceof OnWebViewInteractionListener) {
            listener = (OnWebViewInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnWebViewInteractionListener");
        }
    }

    public MyWebView(Context context) {
        super(context);
        bindListener(context);
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        bindListener(context);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        bindListener(context);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        bindListener(context);
    }

    int getStreamWidth() {
        return super.computeHorizontalScrollRange();
    }

    public int getStreamHeight() {
        return (int)(super.computeHorizontalScrollRange() * 0.75);
    }

    public void zoomTo(int factor) {
        zoomBy((float).02);
        float streamHeight = getStreamHeight();
        float expectedHeight = getHeight();
        float defaultZoomFactor = expectedHeight / streamHeight;
        zoomBy(defaultZoomFactor * factor);
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldX, int oldY) {
        super.onScrollChanged(x,y,oldX,oldY);
        if (scrollMode) listener.onScrollChanged(x, y);
        //Deprecated
        /*
        if (aimModeEnabled) {
            int deltaX = getStreamWidth() / 2 - getWidth() / 2;
            int deltaY = getStreamHeight() / 2 - getHeight() / 2;
            scrollTo(deltaX, deltaY);
        }
        else
            super.onScrollChanged(x,y,oldX,oldY);*/
    }

    @Override
    public void scrollTo(@Px int x, @Px int y) {
        super.scrollTo(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return !scrollMode || super.onTouchEvent(event);
    }

    private boolean scrollMode = false;

    public void enableScroll(boolean enabled) {
        scrollMode = enabled;
    }

    interface OnWebViewInteractionListener {
        void onScrollChanged(int x, int y);
    }


}