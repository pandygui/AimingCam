package com.lion.AimingCam;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPurpleMain));

        if (!privilegeGranted()) {
            ActivityCompat.requestPermissions(this, permissions, 0);
        }

        /*
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onClick(findViewById(R.id.buttonEnter));
            }
        }, 5000);*/

    }

    private void showPrivilege() {
        if (!privilegeGranted()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("需要系统权限")
                    .setMessage("此应用需要以下权限以正常运行:"
                            + "\n· 网络权限"
                            + "\n· 外部储存写入权限")
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean denied = false;
        if (grantResults.length == 0) {
            denied = true;
        } else {
            for (int i : grantResults) {
                if (i == PackageManager.PERMISSION_DENIED) denied = true;
            }
        }
        if (denied) {
            showPrivilege();
        }
    }

    private boolean privilegeGranted() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED);
    }

    private static final String[] permissions = new String[] {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };

    public void onClick(View view) {
        if (privilegeGranted()) {
            Intent intent = new Intent(WelcomeActivity.this, WebActivity.class);
            startActivity(intent);
            this.finish();
        } else {
            ActivityCompat.requestPermissions(this, permissions, 0);
        }
    }

    public void testClick(View view) {
        try {
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            //String provider = locationManager.getBestProvider(new Criteria(), true);
            List<String> providers = locationManager.getAllProviders();
            Iterator<String> iterator = providers.iterator();
            Location location = null;
            while (iterator.hasNext()) {
                location = locationManager.getLastKnownLocation(iterator.next());
                if (location != null) break;
            }
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses == null || addresses.size() == 0) {
                Log.e("Location", "fail to get addresses");
            } else {
                Address address = addresses.get(0);
                String addressName = "";
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressName += address.getAddressLine(i);
                }
                Log.e("Location", addressName);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
