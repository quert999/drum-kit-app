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
                            //定义输入流，将音频写入到AudioTrack类中，实现播放
                            File file = new File(getFilesDir() + "/recorderdemo/record.m4a");
                            DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
                            //实例AudioTrack
                            AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, dataSize, AudioTrack.MODE_STREAM);
                            //开始播放
                            track.play();
                            isPlaying = true;
                            //由于AudioTrack播放的是流，所以，我们需要一边播放一边读取
                            while ((dataSize = dis.read(bytes)) != -1) {
                                if (dataSize > 0) {
                                    track.write(bytes, 0, dataSize);
                                }
                            }
                            //播放结束
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
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext()); //获取一个Notification构造器
        Intent nfIntent = new Intent(this, MainActivity.class); //点击后跳转的界面，可以设置跳转数据

        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher)) // 设置下拉列表中的图标(大图标)
                //.setContentTitle("SMI InstantView") // 设置下拉列表里的标题
                .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                .setContentText("is running......") // 设置上下文内容
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("notification_id");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("notification_id", "notification_name", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = builder.build(); // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        startForeground(110, notification);

    }
}