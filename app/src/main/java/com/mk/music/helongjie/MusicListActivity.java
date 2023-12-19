package com.mk.music.helongjie;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class MusicListActivity extends AppCompatActivity {

    TextView musiclist_title;
    ListView listView;
    List<Song> musicList;
    private int lastVisiblePosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music);

        musiclist_title = findViewById(R.id.tv_title);
        musiclist_title.setText("本地音乐列表");
        listView =findViewById(R.id.lv);
        musicList = SongHelper.getMusic(MusicListActivity.this);
        Log.e("MusicList--hlj","getList" + musicList.size());
        MusicAdapter adapter = new MusicAdapter();
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("MusicListActivity--hlj", "点击了" + musicList.get(i).path);
                // 创建 Intent
                Intent ins = new Intent(MusicListActivity.this, MainActivity.class);

                // 将音乐信息传递给 MainActivity
                ins.putExtra("music", musicList.get(i).path);
                ins.putExtra("title", musicList.get(i).name);
                ins.putExtra("duration", musicList.get(i).duration);
                ins.putExtra("musicId",i);
                // 启动 MainActivity
                startActivity(ins);
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                // 当滚动状态改变时，记录当前可见的第一个列表项的位置
                if (i == SCROLL_STATE_IDLE) {
                    lastVisiblePosition = listView.getFirstVisiblePosition();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                // 不需要实现此方法
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        listView.setSelection(lastVisiblePosition);
    }

    @Override
    protected void onPause() {
        super.onPause();
        lastVisiblePosition = listView.getFirstVisiblePosition();
    }


    class MusicAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return musicList.size();
        }

        @Override
        public Object getItem(int i) {
            return musicList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if(view== null){
                view = View.inflate(MusicListActivity.this,R.layout.musiclist,null);
                holder = new ViewHolder();
                holder.id = view.findViewById(R.id.tv_id);
                holder.name = view.findViewById(R.id.tv_title);
                holder.duration = view.findViewById(R.id.tv_duration);

                view.setTag(holder);
            }else {
                holder = (ViewHolder) view.getTag();
            }
            holder.id.setText((i+1)+"");
            holder.name.setText(musicList.get(i).name);
            holder.duration.setText(musicList.get(i).duration+"");
            holder.duration.setText(timeFormat(musicList.get(i).duration));
            return view;
        }
    }
    class ViewHolder{
        TextView id;
        TextView name;
        TextView duration;
    }
    @SuppressLint("DefaultLocale")
    String timeFormat(int miltime){
        int time = miltime/1000;
        int rtime =Math.round(time);
        int min = rtime/60;
        int sec = rtime%60;
        return min+":"+String.format("%2d",sec);
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("确定要退出应用吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 退出应用
                finishAffinity();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }
}