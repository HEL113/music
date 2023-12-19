package com.mk.music.helongjie;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static SeekBar seekBar; // 进度条
    private ImageView iv_back, iv_music;
    ;// 音乐图像
    private Button btn_play, btn_pause, btn_previous, btn_exit, btn_next; // 按钮
    private static TextView tv_progress, tv_total, tv_title, musicname; // 文本视图
    MyserviceConn conn; // 服务连接
    Intent intent; // 意图
    private ObjectAnimator animator; // 动画
    private MusicService.MusicBinder binder; // 音乐服务绑定器
    List<Song> musicList; // 音乐列表
    String path; // 音乐路径

    // 处理音乐播放进度的Handler
    @SuppressLint("HandlerLeak")
    public static Handler handler = new Handler() {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            int duration = bundle.getInt("duration"); // 音乐总时长
            int currentPosition = bundle.getInt("currentPosition"); // 当前播放位置
            seekBar.setMax(duration);
            seekBar.setProgress(currentPosition);
            int minute = duration / 1000 / 60;
            int second = duration / 1000 % 60;
            String strMinute = null;
            String strSecond = null;
            if (minute < 10) {
                strMinute = "0" + minute;
            } else {
                strMinute = minute + "";
            }
            if (second < 10) {
                strSecond = "0" + second;
            } else {
                strSecond = second + "";
            }
            tv_total.setText(strMinute + ":" + strSecond);
            minute = currentPosition / 1000 / 60;
            second = currentPosition / 1000 % 60;
            if (minute < 10) {
                strMinute = "0" + minute;
            } else {
                strMinute = minute + "";
            }
            if (second < 10) {
                strSecond = "0" + second;
            } else {
                strSecond = second + "";
            }
            tv_progress.setText(strMinute + ":" + strSecond);
        }
    };

    @SuppressLint({"SetTextI18n", "CutPasteId", "MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化控件
        tv_progress = findViewById(R.id.tv_progress);
        tv_total = findViewById(R.id.tv_total);
        tv_title = findViewById(R.id.tv_title);
        btn_exit = findViewById(R.id.btn_exit);
        seekBar = findViewById(R.id.sb);
        btn_play = findViewById(R.id.btn_play);
        btn_pause = findViewById(R.id.btn_pause);
        btn_next = findViewById(R.id.btn_next);
        btn_previous = findViewById(R.id.btn_previous);
        iv_back = findViewById(R.id.iv_back);
        musicname = findViewById(R.id.name);
        iv_music = findViewById(R.id.iv_music);

        // 设置点击事件监听器
        iv_music.setOnClickListener(this);
        btn_play.setOnClickListener(this);
        btn_pause.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        btn_exit.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        btn_previous.setOnClickListener(this);

        btn_play.setVisibility(View.GONE);
        btn_pause.setVisibility(View.VISIBLE);
        // 获取传递过来的音乐信息
        path = getIntent().getStringExtra("music");
        String name = getIntent().getStringExtra("title");
        int duration = getIntent().getIntExtra("duration", 0);
        int musicId = getIntent().getIntExtra("musicId", 0);
        Log.e("MainActivity", "path" + path + ", " + name + ", " + duration + ", " + musicId);


        // 设置音乐标题和总时长
        if (name != null) {
            tv_title.setText(getString(R.string.zz) + name.substring(0, name.length()));
            tv_title.setSelected(true);  // 设置TextView获取焦点
            tv_title.setEllipsize(TextUtils.TruncateAt.MARQUEE);  // 设置滚动效果
            tv_title.setSingleLine(true);  // 设置为单行显示
            tv_title.setMarqueeRepeatLimit(-1);  // 设置滚动次数为无限次
        }
        tv_total.setText(timeFormat(duration));

        // 获取用户信息并显示
        Map<String, String> userInfo = SPSave.getUserInfo(this);
        musicname.setText(getString(R.string.una)+ userInfo.get("account"));
        Log.d("MainActivity", "userInfo" + userInfo);

        // 获取音乐列表
        musicList = SongHelper.getMusic(MainActivity.this);

        // 设置进度条的监听器
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == seekBar.getMax()) {
                    animator.pause();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                binder.seekTo(progress);
            }
        });

        // 设置音乐图像的动画效果
        ImageView imageView = findViewById(R.id.iv_music);
        animator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360.0f);
        animator.setDuration(4000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(-1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 绑定音乐服务
        intent = new Intent(this, MusicService.class);
        conn = new MyserviceConn();
        bindService(intent, conn, BIND_AUTO_CREATE);
    }

    // 连接服务器
    class MyserviceConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            Log.e("MusicListActivity", "onServiceConnected: " + name + ", " + iBinder);
            binder = (MusicService.MusicBinder) iBinder;

            if (binder != null) {
                animator.start();
                binder.play(path);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("MusicListActivity", "onServiceDisconnected: " + name);
        }
    }

    // 按钮点击事件处理
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_play) {
            btn_pause.setVisibility(View.VISIBLE);
            btn_play.setVisibility(View.GONE);
            // 执行播放操作
            binder.continuePlay();
            animator.start();
        }
        if (v.getId() == R.id.btn_pause) {
            btn_play.setVisibility(View.VISIBLE);
            btn_pause.setVisibility(View.GONE);
            // 执行暂停操作
            binder.pausePlay();
            animator.pause();
        }
        if (v.getId() == R.id.btn_exit) {
            binder.pausePlay();
            animator.pause();
            moveTaskToBack(true);
        }
        if (v.getId() == R.id.iv_back) {
            intent = new Intent(this, MusicListActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.iv_music) {
            intent = new Intent(this, MusicListActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.btn_next) {
            int currentIndex = getCurrentMusicIndex();
            int nextIndex = currentIndex + 1;
            if (nextIndex >= musicList.size()) {
                nextIndex = 0;
            }
            play(nextIndex);
        }
        if (v.getId() == R.id.btn_previous) {
            int currentIndex = getCurrentMusicIndex();
            int lastIndex = currentIndex - 1;
            if (lastIndex < 0) {
                lastIndex = musicList.size() - 1;
            }
            play(lastIndex);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    // 格式化时间
    @SuppressLint("DefaultLocale")
    String timeFormat(int miltime) {
        int time = miltime / 1000;
        int rtime = Math.round(time);
        int min = rtime / 60;
        int sec = rtime % 60;
        return min + ":" + String.format("%2d", sec);
    }

    private int getCurrentMusicIndex() {
        for (int i = 0; i < musicList.size(); i++) {
            if (musicList.get(i).path.equals(path)) {
                return i;
            }
        }
        return -1;
    }

    private void play(int index) {
        if (index >= 0 && index < musicList.size()) {
            Song song = musicList.get(index);
            path = song.path;
            String name = song.name;
            int duration = song.duration;
            tv_title.setText(getString(R.string.zz) + name.substring(0, name.length()));
            tv_title.setSelected(true);  // 设置TextView获取焦点
            tv_title.setEllipsize(TextUtils.TruncateAt.MARQUEE);  // 设置滚动效果
            tv_title.setSingleLine(true);  // 设置为单行显示
            tv_title.setMarqueeRepeatLimit(-1);  // 设置滚动次数为无限次
            tv_total.setText(timeFormat(duration));
            binder.play(path);
            animator.start();
        }
    }

}

