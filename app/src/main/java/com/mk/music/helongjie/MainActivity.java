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
    private ImageView  iv_back ,iv_music; ;// 音乐图像
    private Button btn_play, btn_pause, btn_continue,btn_exit; // 按钮
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
        seekBar = findViewById(R.id.sb);
        btn_play = findViewById(R.id.btn_play);
        btn_pause = findViewById(R.id.btn_pause);
        btn_continue = findViewById(R.id.btn_continue);
        iv_back = findViewById(R.id.iv_back);
        musicname = findViewById(R.id.name);
        iv_music = findViewById(R.id.iv_music);

        // 设置点击事件监听器
        iv_music.setOnClickListener(this);
        btn_play.setOnClickListener(this);
        btn_pause.setOnClickListener(this);
        btn_continue.setOnClickListener(this);
        iv_back.setOnClickListener(this);

        // 获取传递过来的音乐信息
        path = getIntent().getStringExtra("music");
        String name = getIntent().getStringExtra("title");
        int duration = getIntent().getIntExtra("duration", 0);
        int musicId = getIntent().getIntExtra("musicId", 0);
        Log.e("MainActivity", "path" + path + ", " + name + ", " + duration + ", " + musicId);



    // 设置音乐标题和总时长
        if (name != null) {
            tv_title.setText(name.substring(0, name.length() - 4));
        }
        tv_total.setText(timeFormat(duration));

        // 获取用户信息并显示
        Map<String, String> userInfo = SPSave.getUserInfo(this);
        musicname.setText("用户名：" + userInfo.get("account"));
        Log.d("MainActivity","userInfo"+userInfo);

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
            binder.play(path);
            animator.start();
        }
        if (v.getId() == R.id.btn_pause) {
            binder.pausePlay();
            animator.pause();
        }
        if (v.getId() == R.id.btn_continue) {
            binder.continuePlay();
            animator.start();
        }
        if (v.getId() == R.id.iv_back) {
            intent = new Intent(this, MusicListActivity.class);
            startActivity(intent);

        }
        if (v.getId() == R.id.iv_music) {
            intent = new Intent(this, MusicListActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    // 格式化时间
    @SuppressLint("DefaultLocale")
    String timeFormat(int miltime){
        int time = miltime/1000;
        int rtime =Math.round(time);
        int min = rtime/60;
        int sec = rtime%60;
        return min+":"+String.format("%2d",sec);
    }
}