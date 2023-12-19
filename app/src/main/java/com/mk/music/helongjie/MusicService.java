package com.mk.music.helongjie;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {
    static final String TAG = "MusicService-hlj";  // 定义日志标签
    private MediaPlayer mediaPlayer;  // 媒体播放器对象
    private Timer timer;  // 定时器对象

    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBinder();  // 返回一个MusicBinder实例，用于客户端与服务交互
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();  // 创建媒体播放器对象
    }

    public void addTimer() {
        if (timer == null) {
            timer = new Timer();  // 创建定时器
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if (mediaPlayer == null) return;  // 如果媒体播放器为空，则返回
                    int duration = mediaPlayer.getDuration();  // 获取音频总时长
                    int currenPosition = mediaPlayer.getCurrentPosition();  // 获取当前播放位置
                    Message message = MainActivity.handler.obtainMessage();  // 获取消息对象
                    Bundle bundle = new Bundle();  // 创建Bundle对象
                    bundle.putInt("duration", duration);  // 将音频总时长放入Bundle
                    bundle.putInt("currentPosition", currenPosition);  // 将当前播放位置放入Bundle
                    message.setData(bundle);  // 将Bundle放入消息对象
                    MainActivity.handler.sendMessage(message);  // 发送消息到主线程
                }
            };
            timer.schedule(task, 5, 500);  // 定时器任务，5毫秒后开始执行，每隔500毫秒执行一次
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer == null) return;  // 如果媒体播放器为空，则返回
        if (mediaPlayer.isPlaying()) mediaPlayer.stop();  // 如果媒体正在播放，则停止播放
        mediaPlayer.release();  // 释放媒体播放器资源
        mediaPlayer = null;  // 将媒体播放器对象置空
    }

    class MusicBinder extends Binder {

        List<Song> musicList;  // 歌曲列表
        private String currentPath;  // 当前播放的音乐路径
        private int path;

        public void play(String path) {
            if (currentPath != null && currentPath.equals(path) && mediaPlayer.isPlaying()) {
                // 如果当前正在播放同一首歌曲，则不重新播放
                return;
            }
            mediaPlayer.reset();  // 重置媒体播放器
            try {
                mediaPlayer.setDataSource(path);  // 设置音频数据源
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);  // 设置音频流类型
                mediaPlayer.prepareAsync();  // 异步准备音频播放
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mediaPlayer.start();  // 开始播放音频
                        addTimer();  // 启动定时器
                    }
                });
                currentPath = path;  // 更新当前播放的音乐路径
            } catch (IOException e) {
                throw new RuntimeException(e);  // 捕获异常并抛出运行时异常
            }
        }


        private String findSongPathByMusicId(int musicId) {
            return musicList.get(musicId).toString();
        }
        public void pausePlay() {
            mediaPlayer.pause();  // 暂停音频播放
        }

        public void continuePlay() {
            mediaPlayer.start();  // 继续音频播放
        }

        public void seekTo(int progress) {
            mediaPlayer.seekTo(progress);  // 设置音频播放位置
        }
    }
}