package com.lion.AimingCam;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WebActivity extends AppCompatActivity {

    private void disableScroll() {
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);
        webView.setScrollbarFadingEnabled(true);
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setBuiltInZoomControls(false);
    }

    private void initializeWebView() {
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
    }

    private void bindLayout() {
        webView = (MyWebView) findViewById(R.id.webView);
        aimButtons = findViewById(R.id.layoutAimButtons);
        calibrationButtons = findViewById(R.id.layoutCalibrateButtons);
        imageViewAim = (ImageView) findViewById(R.id.imageViewAim);
        layoutWebView = findViewById(R.id.layoutWebView);
        textViewWelcome = (TextView)findViewById(R.id.textViewWelcome);
    }

    TextView textViewWelcome;

    private void initializeWelcomeView() {
        imageViewAim.setVisibility(View.INVISIBLE);
        layoutWebView.setVisibility(View.GONE);
        aimButtons.setVisibility(View.GONE);
        calibrationButtons.setVisibility(View.GONE);
        textViewWelcome.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.setGroupEnabled(R.id.group_2, false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_login:
                login();
                return true;
            case R.id.action_aim:
                toAim();
                return true;
            case R.id.action_test:
                logcat();
                return true;
            case R.id.action_calibrate:
                calibrate();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setCurrentDistance(String currentDistance) {
        this.currentDistance = currentDistance;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)imageViewAim.getLayoutParams();
        params.bottomMargin = preferences.getInt(currentDistance, webView.getHeight() / 2);
        imageViewAim.setLayoutParams(params);

        switch (currentDistance) {
            case DISTANCE_10:
                webView.zoomTo(1);
                break;
            case DISTANCE_20:
                webView.zoomTo(2);
                break;
            case DISTANCE_30:
                webView.zoomTo(3);
                break;
            default:
                break;
        }
    }

    private String currentDistance = DISTANCE_10;

    private void calibrate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WebActivity.this);
        builder.setTitle("select calibration distance")
                .setItems(R.array.distance, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                setCurrentDistance(DISTANCE_10);
                                break;
                            case 1:
                                setCurrentDistance(DISTANCE_20);
                                break;
                            case 2:
                                setCurrentDistance(DISTANCE_30);
                                break;
                            default:
                                break;
                        }
                        calibrationButtons.setVisibility(View.VISIBLE);
                        aimButtons.setVisibility(View.INVISIBLE);
                        toolbar.getMenu().setGroupEnabled(R.id.group_1, false);
                        toolbar.getMenu().setGroupEnabled(R.id.group_2, false);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();
    }

    public void onCalibrationButtonClick(View view) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)imageViewAim.getLayoutParams();
        switch (view.getId()) {
            case R.id.buttonAimUp:
                params.bottomMargin = params.bottomMargin + 5;
                if (params.bottomMargin <= webView.getHeight()) imageViewAim.setLayoutParams(params);
                break;
            case R.id.buttonAimDown:
                params.bottomMargin = params.bottomMargin - 5;
                if (params.bottomMargin  >= 0) imageViewAim.setLayoutParams(params);
                break;
            case R.id.buttonAimSet:
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(currentDistance, params.bottomMargin);
                editor.apply();
                calibrationButtons.setVisibility(View.GONE);
                aimButtons.setVisibility(View.VISIBLE);
                toolbar.getMenu().setGroupEnabled(R.id.group_1, true);
                toolbar.getMenu().setGroupEnabled(R.id.group_2, true);
                break;
            default:
                break;
        }
    }

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        toolbar = (Toolbar)findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        bindLayout();
        initializeWebView();
        disableScroll();
        initializeWelcomeView();

        preferences = getSharedPreferences(PREFS_NAME, 0);

    }

    private static final String PREFS_NAME = "LaserAimingCamPrefsFile";
    SharedPreferences preferences;
    private static final String DISTANCE_10 = "distance10";
    private static final String DISTANCE_20 = "distance20";
    private static final String DISTANCE_30 = "distance30";


    private void logcat() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)imageViewAim.getLayoutParams();
        Log.i("ฅ(Logcat)ฅ", "aimParams.bottomMargin " + params.bottomMargin);
        Log.i("ฅ(Logcat)ฅ", "webView.getWidth() " + webView.getWidth());
        Log.i("ฅ(Logcat)ฅ", "webView.getStreamWidth() " + webView.getStreamWidth());
        Log.i("ฅ(Logcat)ฅ", "webView.getHeight() " + webView.getHeight());
        Log.i("ฅ(Logcat)ฅ", "webView.getStreamHeight() " + webView.getStreamHeight());
    }

    View aimButtons, calibrationButtons, layoutWebView;

    public void onMoveAim(View view) {
        switch (view.getId()) {
            case R.id.buttonAim10:
                setCurrentDistance(DISTANCE_10);
                break;
            case R.id.buttonAim20:
                setCurrentDistance(DISTANCE_20);
                break;
            case R.id.buttonAim30:
                setCurrentDistance(DISTANCE_30);
                break;
            default:
                break;
        }
    }

    private static final String loginUrl = "http://192.168.8.1/html/mobile/index";
    private static final String streamUrl = "http://192.168.8.1/stream";
    MyWebView webView;
    ImageView imageViewAim;

    public void login() {
        webView.loadUrl(loginUrl);

        webView.aimModeEnable(false);

        aimButtons.setVisibility(View.INVISIBLE);
        imageViewAim.setVisibility(View.INVISIBLE);
        layoutWebView.setVisibility(View.VISIBLE);
        textViewWelcome.setVisibility(View.GONE);
    }

    public void toAim() {
        webView.loadUrl(streamUrl);

        webView.aimModeEnable(true);

        aimButtons.setVisibility(View.VISIBLE);
        imageViewAim.setVisibility(View.VISIBLE);
        layoutWebView.setVisibility(View.VISIBLE);
        textViewWelcome.setVisibility(View.GONE);
        toolbar.getMenu().setGroupEnabled(R.id.group_2, true);
    }

}
