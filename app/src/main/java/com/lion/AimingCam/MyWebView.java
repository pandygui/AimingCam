package com.lion.AimingCam;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * Created by Lion on 2017/5/9.
 */

public class MyWebView extends WebView {
    public MyWebView(Context context) {
        super(context);
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    int getStreamWidth() {
        return super.computeHorizontalScrollRange();
    }

    public int getStreamHeight() {
        return (int)(super.computeHorizontalScrollRange() * 0.75);
    }

    private boolean aimModeEnabled = false;

    public void aimModeEnable(boolean aimModeEnabled) {
        this.aimModeEnabled = aimModeEnabled;
        //TODO: 无法自动初始化焦距
        //if(aimModeEnabled) onScrollChanged(0, 0, 0, 0);
    }

    public void zoomTo(int times) {
        zoomBy((float).02);
        float streamHeight = getStreamHeight();
        float expectedHeight = getHeight();
        float defaultZoomFactor = expectedHeight / streamHeight;
        zoomBy(defaultZoomFactor * times);
    }

    @Override
    protected void onScrollChanged(int x, int y, int xx, int yy) {
        //super.onScrollChanged(x,y,xx,yy);
        if (aimModeEnabled) {
            int deltaX = getStreamWidth() / 2 - getWidth() / 2;
            int deltaY = getStreamHeight() / 2 - getHeight() / 2;
            scrollTo(deltaX, deltaY);
        }
        else
            super.onScrollChanged(x,y,xx,yy);
    }
}