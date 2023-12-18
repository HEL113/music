package com.mk.music.helongjie;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {
    static final String TAG="MusicService";
    private MediaPlayer mediaPlayer;
    private Timer timer;

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
    }

    public void addTimer() {
        if (timer == null) {
            timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if (mediaPlayer == null) return;
                    int duration = mediaPlayer.getDuration();
                    int currenPosition = mediaPlayer.getCurrentPosition();
                    Message message = MainActivity.handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putInt("duration", duration);
                    bundle.putInt("currentPosition", currenPosition);
                    message.setData(bundle);
                    MainActivity.handler.sendMessage(message);
                }
            };
            timer.schedule(task,5,500);
        }
    }

    class MusicBinder extends Binder {
        public void play(String path) {
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(path);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mediaPlayer.start();
                        addTimer();
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void pausePlay() {
            mediaPlayer.pause();
        }

        public void continnuePlay() {
            mediaPlayer.start();
        }

        public void seekTo(int progress) {
            mediaPlayer.seekTo(progress);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }
}