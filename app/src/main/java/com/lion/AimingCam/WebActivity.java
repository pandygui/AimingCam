package com.lion.AimingCam;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class WebActivity extends AppCompatActivity implements MyWebView.OnWebViewInteractionListener {

    private int lastestX = 0, lastestY = 0;
    @Override
    public void onScrollChanged(int x, int y) {
        lastestX = x;
        lastestY = y;
    }

    private void disableScroll() {
        webView.setHorizontalScrollBarEnabled(true);
        webView.setVerticalScrollBarEnabled(true);
        webView.setScrollbarFadingEnabled(false);
        /*webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });*/
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
    }

    private void initializeWelcomeView() {
        imageViewAim.setVisibility(View.GONE);
        layoutWebView.setVisibility(View.GONE);
        aimButtons.setVisibility(View.GONE);
        calibrationButtons.setVisibility(View.GONE);
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
                return true;
            case R.id.action_screenshot:
                showScreenshot();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showScreenshot() {
        Intent intent = new Intent(this, LogActivity.class);
        startActivity(intent);
        /*
        try {
            String bucketId = "";

            final String[] projection = new String[] {"DISTINCT " + MediaStore.Images.Media.BUCKET_DISPLAY_NAME + ", " + MediaStore.Images.Media.BUCKET_ID};
            final Cursor cur = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);

            while (cur != null && cur.moveToNext()) {
                final String bucketName = cur.getString((cur.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)));
                if (bucketName.equals("AimingCam")) {
                    bucketId = cur.getString((cur.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID)));
                    break;
                }
            }
            Uri mediaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            if (bucketId.length() > 0) {
                mediaUri = mediaUri.buildUpon()
                        .authority("media")
                        .appendQueryParameter("bucketId", bucketId)
                        .build();
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, mediaUri);
            intent.setType("image/*");
            startActivity(intent);
        } catch (Exception e) {
            Log.e("screenshot", e.getMessage());
        }*/
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

        lastestX = preferences.getInt(currentDistance + "X", 0);
        lastestY = preferences.getInt(currentDistance + "Y", 0);
        webView.enableScroll(true);
        webView.scrollTo(lastestX, lastestY);
        webView.enableScroll(false);
    }

    private String currentDistance = DISTANCE_10;

    private void calibrate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WebActivity.this);
        builder.setTitle("选择校准距离")
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
                        aimButtons.setVisibility(View.GONE);
                        toolbar.getMenu().setGroupEnabled(R.id.group_1, false);
                        toolbar.getMenu().setGroupEnabled(R.id.group_2, false);
                        webView.enableScroll(true);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
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
                editor.putInt(currentDistance + "X", lastestX);
                editor.putInt(currentDistance + "Y", lastestY);
                editor.apply();
                calibrationButtons.setVisibility(View.GONE);
                aimButtons.setVisibility(View.VISIBLE);
                toolbar.getMenu().setGroupEnabled(R.id.group_1, true);
                toolbar.getMenu().setGroupEnabled(R.id.group_2, true);
                webView.enableScroll(false);
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
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPurpleDark));

        toolbar = (Toolbar)findViewById(R.id.toolbarMain);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        //toolbar.setNavigationIcon(R.drawable.icon_launch_mini);

        bindLayout();
        initializeWebView();
        //disableScroll();
        initializeWelcomeView();

        preferences = getSharedPreferences(PREFS_NAME, 0);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                login();
            }
        }, 100);
    }

    private void showPrivilege() {
        if (!privilegeGranted()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("需要系统权限")
                    .setMessage("此应用需要以下权限以正常运行:\n·网络权限\n·外部储存写入权限")
                    .setPositiveButton("设置页面", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    })
                    .setCancelable(false);
            builder.create().show();
        }
    }

    private boolean privilegeGranted() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED);
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
        if (privilegeGranted()) {
            webView.loadUrl(loginUrl);
            webView.enableScroll(true);

            aimButtons.setVisibility(View.GONE);
            imageViewAim.setVisibility(View.GONE);
            layoutWebView.setVisibility(View.VISIBLE);

            toolbar.getMenu().setGroupEnabled(R.id.group_2, false);
        } else {
            showPrivilege();
        }
    }

    public void toAim() {
        if (privilegeGranted()) {
            webView.loadUrl(streamUrl);
            webView.enableScroll(false);

            aimButtons.setVisibility(View.VISIBLE);
            imageViewAim.setVisibility(View.VISIBLE);
            layoutWebView.setVisibility(View.VISIBLE);
            toolbar.getMenu().setGroupEnabled(R.id.group_2, true);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    takeScreenshot();
                }
            }, 2000);
        } else {
            showPrivilege();
        }
    }

    private void takeScreenshot() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd_kk:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault());
        String time = sdf.format(new Date());
        try {
            File dir = new File(Environment.getExternalStorageDirectory().toString()
                    + "/" + getString(R.string.app_name_eng));
            if (!dir.exists()) dir.mkdir();
            String mPath = dir.getPath() + "/" + time + ".jpg";

            View view = getWindow().getDecorView().getRootView();
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
