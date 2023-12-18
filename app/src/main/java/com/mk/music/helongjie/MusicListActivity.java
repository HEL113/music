package com.mk.music.helongjie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class MusicListActivity extends AppCompatActivity {

    TextView musiclist_title;
    ListView listView;
    List<Song> musicList;

    MediaPlayer player = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music);
        musiclist_title = findViewById(R.id.tv_title);
        musiclist_title.setText("本地音乐列表");
        listView =findViewById(R.id.lv);

        musicList = SongHelper.getMusic(MusicListActivity.this);
        Log.e("MusicList--keyar","getList" + musicList.size());

        MusicAdapter adapter = new MusicAdapter();
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("MusicListActivity---keyar", "点击了" + musicList.get(i).path);
                // 创建 Intent
                Intent ins = new Intent(MusicListActivity.this, MainActivity.class);

                // 将音乐信息传递给 MainActivity
                ins.putExtra("music", musicList.get(i).path);
                ins.putExtra("title", musicList.get(i).name);
                ins.putExtra("duration", musicList.get(i).duration);
                ins.putExtra("musciId",i);
                // 启动 MainActivity
                startActivity(ins);
            }
        });

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


            if(view == null){
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
    String timeFormat(int miltime){
        int time = miltime/1000;
        int rtime =Math.round(time);
        int min = rtime/60;
        int sec = rtime%60;
        return min+":"+String.format("%2d",sec);
    }
}