package com.mk.music.helongjie;

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
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static SeekBar seekBar;
    private Button btn_play, btn_pause, btn_continue_play, btn_exit;
    private static TextView tv_progress, tv_total, tv_music_title, musicname;
    MyserviceConn conn;
    Intent intent;
    ImageView iv_music;
    private ObjectAnimator animator;
    private MusicService.MusicBinder binder;
    List<Song> musicList;
    String path;
    @SuppressLint("HandlerLeak")
    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            int duration = bundle.getInt("duration");
            int currentPosition = bundle.getInt("currentPosition");
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
    @SuppressLint({"SetTextI18n", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("onclick","onclik");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_progress = findViewById(R.id.tv_progress);
        tv_total = findViewById(R.id.tv_total);
        tv_music_title = findViewById(R.id.tv_music_title);
        seekBar = findViewById(R.id.sb);
        btn_play = findViewById(R.id.btn_play);
        btn_pause = findViewById(R.id.btn_pause);
        btn_continue_play = findViewById(R.id.btn_continue_play);
        btn_exit = findViewById(R.id.btn_exit);
        musicname = findViewById(R.id.name);
        iv_music = findViewById(R.id.iv_music);

        iv_music.setOnClickListener(this);
        btn_play.setOnClickListener(this);
        btn_pause.setOnClickListener(this);
        btn_continue_play.setOnClickListener(this);
        btn_exit.setOnClickListener(this);

        path = getIntent().getStringExtra("music");
        String name = getIntent().getStringExtra("title");
        int duration = getIntent().getIntExtra("duration", 0);
        int musicId = getIntent().getIntExtra("musciId", 0);
        Log.e("MainActivity", "path" + path + ", " + name + ", " + duration + ", " + musicId);

        if (name != null) {
            tv_music_title.setText(name.substring(0, name.length() - 4));
        }
        tv_total.setText(timeFormat(duration));

        Map<String, String> userInfo = SPSave.getUserInfo(this);
        musicname.setText("用户名：" + userInfo.get("account"));
        Log.d("MainActivity","userInfo"+userInfo);

        musicList = SongHelper.getMusic(MainActivity.this);

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

        ImageView imageView = findViewById(R.id.iv_music);
        animator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360.0f);
        animator.setDuration(2000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(-1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        intent = new Intent(this, MusicService.class);
        conn = new MyserviceConn();
        bindService(intent, conn, BIND_AUTO_CREATE);
    }

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
        if (v.getId() == R.id.btn_continue_play) {
            binder.continnuePlay();
            animator.start();
        }
        if (v.getId() == R.id.btn_exit) {
            intent = new Intent(MainActivity.this, MusicListActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.iv_music) {
            intent = new Intent(MainActivity.this, MusicListActivity.class);
            startActivity(intent);
        }
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    String timeFormat(int miltime){
        int time = miltime/1000;
        int rtime =Math.round(time);
        int min = rtime/60;
        int sec = rtime%60;
        return min+":"+String.format("%2d",sec);
    }
}