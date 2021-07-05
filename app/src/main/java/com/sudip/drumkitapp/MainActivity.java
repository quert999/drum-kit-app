package com.sudip.drumkitapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private long recordStartTime;
    private final List<SingleRecordBean> recordAudioList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        checkPermission();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ((AudioManager) getSystemService(AUDIO_SERVICE)).setAllowedCapturePolicy(AudioAttributes.ALLOW_CAPTURE_BY_ALL);
        }
        ViewGroup group = findViewById(R.id.layout);
        SoundPoolUtil.initResource(group);


        for (int i = 0; i < group.getChildCount(); i++) {
            group.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        int id = view.getId();
                        String ourId = view.getResources().getResourceEntryName(id);

                        int resourceId = getResources().getIdentifier(ourId, "raw", "com.sudip.drumkitapp");
//                        SoundPoolPlayer mPlayer = SoundPoolPlayer.create(MainActivity.this, resourceId);
//                        mPlayer.play();
                        SoundPoolUtil.playByResourceId(resourceId);

                        if (recordStartTime > 0) {
                            recordAudioList.add(new SingleRecordBean(System.currentTimeMillis() - recordStartTime, SoundPoolUtil.getSoundPoolId(resourceId)));
                        }
                    }
                    return true;
                }
            });
        }


        findViewById(R.id.record).setOnClickListener(v -> {
            if (((TextView) v).getText().equals("record")) {
                startRecorder();
                recordAudioList.clear();
                ((TextView) v).setText("click to stop");
            } else if (((TextView) v).getText().equals("click to stop")) {
                stopRecorder();
                ((TextView) v).setText("record");
            }
        });

        findViewById(R.id.play).setOnClickListener(v -> {
            if (recordStartTime > 0) {
                Toast.makeText(getApplicationContext(), "please stop record", Toast.LENGTH_LONG).show();
                return;
            }
            Intent intent = new Intent(MainActivity.this, TestActivity.class);
            intent.putExtra("recordTimeLength", getIntent().getLongExtra("recordTimeLength", 0L));
            intent.putExtra("recordListData",new Gson().toJson(recordAudioList));
            startActivity(intent);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                Intent service = new Intent(this, ForegroundService.class);
//                service.putExtra("type", "play");
//                startForegroundService(service);
//            }
        });
    }

    private void startRecorder() {
        recordStartTime = System.currentTimeMillis();
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
//            MediaProjectionManager manager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
//            Intent intent = manager.createScreenCaptureIntent();
//            startActivityForResult(intent, 1);
//        }
    }

    private void stopRecorder() {
        getIntent().putExtra("recordTimeLength", System.currentTimeMillis() - recordStartTime);
        recordStartTime = 0;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Intent service = new Intent(this, ForegroundService.class);
//            service.putExtra("type", "stop");
//            startForegroundService(service);
//        }
    }


    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, permissions, 200);
                    return;
                }
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && requestCode == 200) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, 200);
                    return;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 200) {
            checkPermission();
        }
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
                return;
            }
            Intent service = new Intent(this, ForegroundService.class);
            service.putExtra("code", resultCode);
            service.putExtra("data", data);
            service.putExtra("type", "start");
            startForegroundService(service);
        }
    }

}