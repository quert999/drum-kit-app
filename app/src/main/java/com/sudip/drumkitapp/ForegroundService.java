package com.sudip.drumkitapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioPlaybackCaptureConfiguration;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: Xu
 * @create: 2021-07-02 15:53
 **/
public class ForegroundService extends Service {
    public boolean isRecording = false;
    public boolean isPlaying = false;

    ExecutorService singleExecutor = Executors.newSingleThreadExecutor();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        MediaProjectionManager manager;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            int code = intent.getIntExtra("code", -1);
            Intent intentData = intent.getParcelableExtra("data");
            if ((isPlaying || isRecording) & !"stop".equals(intent.getStringExtra("type")))
                return super.onStartCommand(intent, flags, startId);
            if ("start".equals(intent.getStringExtra("type"))) {
                manager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                MediaProjectionManager finalManager = manager;
                singleExecutor.submit(() -> {
                    MediaProjection mediaProjection = finalManager.getMediaProjection(code, intentData);
                    AudioPlaybackCaptureConfiguration config = new AudioPlaybackCaptureConfiguration.Builder(mediaProjection)
                            .addMatchingUsage(AudioAttributes.USAGE_MEDIA)
                            .build();

                    AudioRecord record = new AudioRecord.Builder()
                            .setAudioFormat(new AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                    .setSampleRate(8000)
                                    .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO).build())
                            .setAudioPlaybackCaptureConfig(config)
                            .build();
                    record.startRecording();

                    try {
                        File file = new File(getFilesDir() + "/recorderdemo/record.m4a");
                        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
                        file.createNewFile();
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        int bufferSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
                        byte[] buffer = new byte[bufferSize];
                        isRecording = true;
                        while (isRecording) {
                            record.read(buffer, 0, buffer.length);
                            fileOutputStream.write(buffer);
                        }
                        fileOutputStream.close();
                        record.stop();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    isRecording = false;
                });
            } else if ("stop".equals(intent.getStringExtra("type"))) {
                isRecording = false;
            } else if ("play".equals(intent.getStringExtra("type"))) {
                singleExecutor.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int dataSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
                            byte[] bytes = new byte[dataSize];
                            //????????????????????????????????????AudioTrack?????????????????????
                            File file = new File(getFilesDir() + "/recorderdemo/record.m4a");
                            DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
                            //??????AudioTrack
                            AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, dataSize, AudioTrack.MODE_STREAM);
                            //????????????
                            track.play();
                            isPlaying = true;
                            //??????AudioTrack???????????????????????????????????????????????????????????????
                            while ((dataSize = dis.read(bytes)) != -1) {
                                if (dataSize > 0) {
                                    track.write(bytes, 0, dataSize);
                                }
                            }
                            //????????????
                            track.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        isPlaying = false;
                    }
                });
            }


            //for information
            //https://blog.csdn.net/jiangliloveyou/article/details/11218555
            //https://github.com/AFinalStone/AudioRecord.git
            //http://www.audiobar.cn/  find tone
            //https://www.bilibili.com/read/cv3250059/
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void createNotificationChannel() {
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext()); //????????????Notification?????????
        Intent nfIntent = new Intent(this, MainActivity.class); //???????????????????????????????????????????????????

        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0)) // ??????PendingIntent
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher)) // ??????????????????????????????(?????????)
                //.setContentTitle("SMI InstantView") // ??????????????????????????????
                .setSmallIcon(R.mipmap.ic_launcher) // ??????????????????????????????
                .setContentText("is running......") // ?????????????????????
                .setWhen(System.currentTimeMillis()); // ??????????????????????????????

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("notification_id");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("notification_id", "notification_name", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = builder.build(); // ??????????????????Notification
        notification.defaults = Notification.DEFAULT_SOUND; //????????????????????????
        startForeground(110, notification);

    }
}