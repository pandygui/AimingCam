package com.lion.AimingCam;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class LogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPurpleDark));

        File file  = new File(Environment.getExternalStorageDirectory().toString()
                + "/" + getString(R.string.app_name_eng));
        String[] names = file.list();
        for (int i = 0; i < names.length; i++) {
           names[i] = names[i].replace(".jpg","");
        }

        ListView listView = (ListView) findViewById(R.id.listViewLog);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.simple_list_item_1, names);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(onItemClickListener);
    }

    private AdapterView.OnItemClickListener onItemClickListener= new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String name = ((TextView)view).getText().toString();
            File file = new File(Environment.getExternalStorageDirectory().toString()
                    + "/" + getString(R.string.app_name_eng) + '/' + name + ".jpg");
            Bitmap img = BitmapFactory.decodeFile(file.getPath());
            ImageView imageView = new ImageView(LogActivity.this);
            imageView.setImageBitmap(img);
            AlertDialog.Builder builder = new AlertDialog.Builder(LogActivity.this);
            builder.setView(imageView).create().show();
        }
    };
}
