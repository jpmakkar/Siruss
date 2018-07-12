package com.example.hp2.sirus;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.io.IOException;
import java.util.ArrayList;


public class MyService extends Service {
    public MyService() {
    }

    boolean play;
    int idx=1;
    MediaPlayer mediaPlayer;
    ArrayList<String> arrayList;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer=new MediaPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        arrayList=intent.getStringArrayListExtra("Array");
        if(!play){
            idx=intent.getIntExtra("Path",1);
            try {
                mediaPlayer.setDataSource(arrayList.get(idx));
                mediaPlayer.prepare();
                play=true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(arrayList.get(++idx));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.pause();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
