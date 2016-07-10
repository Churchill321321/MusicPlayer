package com.example.lc.easymusicplayer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MusicButtonActivity extends Activity implements View.OnClickListener {

    public static final String PREV = "2";
    private TextView mPrevSongTextView;
    private ImageView mPlayPauseImageView;
    private TextView mNextSongTextView;
    private boolean mPlayPause = true;
    private MusicService mMusicService;
    private boolean mFlag = false;
    private String[] musicData = {"1    hen_ai_hen_ai_ni","2    libai","3   new_day"};
    Handler mHandler = new Handler();
    private Runnable thread = new Runnable() {
        @Override
        public void run() {
            if (mMusicService != null) {
                Log.d("MusicButtonActivity",String.valueOf(mMusicService.getIndex()));
                mSongNameTextView.setText(mMusicService.getSongName());
            }
            mHandler.postDelayed(thread,1000);
        }
    };

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.LocalBinder localBinder = (MusicService.LocalBinder) service;
            mMusicService = localBinder.getService();
            Log.d("MusicButtonActivity", "mMusicService1");

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private TextView mSongNameTextView;
    private ListView mMusicListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_button);

        mPrevSongTextView = (TextView) findViewById(R.id.prev_song);
        mPlayPauseImageView = (ImageView) findViewById(R.id.play_pause);
        mNextSongTextView = (TextView) findViewById(R.id.next_song);
        mSongNameTextView = (TextView) findViewById(R.id.song_name);

        //修改的地方
        //获取listView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MusicButtonActivity.this,android.R.layout.simple_list_item_1,musicData);
        mMusicListView = (ListView) findViewById(R.id.music_list_view);
        mMusicListView.setAdapter(adapter);

        mPlayPauseImageView.setOnClickListener(this);
        mPrevSongTextView.setOnClickListener(this);
        mNextSongTextView.setOnClickListener(this);
        //listView set clickListener
        mMusicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        mPlayPauseImageView.setImageResource(R.drawable.car_musiccard_pause);
                        Intent intent = new Intent(MusicButtonActivity.this,MusicService.class);
                        intent.putExtra("hen_ai_hen_ai_ni",0);
                        mPlayPause = false;
                        mFlag =true;
                        startService(intent);
                        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);



                        break;
                    case 1:
                        mPlayPauseImageView.setImageResource(R.drawable.car_musiccard_pause);
                        intent = new Intent(MusicButtonActivity.this,MusicService.class);
                        intent.putExtra("libai",1);
                        mPlayPause = false;
                        mFlag =true;
                        startService(intent);
                        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
                        break;
                    case 2:
                        mPlayPauseImageView.setImageResource(R.drawable.car_musiccard_pause);
                        intent = new Intent(MusicButtonActivity.this,MusicService.class);
                        intent.putExtra("new_day",2);
                        mPlayPause = false;
                        mFlag =true;
                        startService(intent);
                        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
                        break;
                }


            }
        });
        new Thread(thread).start();


    }

    @Override
    public void onClick(View v) {
        final Intent intent = new Intent(MusicButtonActivity.this, MusicService.class);
        switch (v.getId()) {
            case R.id.play_pause:
                if (mPlayPause) {

                    mPlayPauseImageView.setImageResource(R.drawable.car_musiccard_pause);
                    mPlayPause = false;
                    intent.putExtra("play", true);
                } else {
                    mPlayPauseImageView.setImageResource(R.drawable.car_musiccard_play);
                    mPlayPause = true;
                }
                break;
            case R.id.prev_song:
                intent.putExtra("prev", true);
                mPlayPauseImageView.setImageResource(R.drawable.car_musiccard_pause);
                mPlayPause = false;
                Log.d("MusicButtonActivity", "prev");
                mFlag = true;


                break;
            case R.id.next_song:
                mPlayPauseImageView.setImageResource(R.drawable.car_musiccard_pause);
                mPlayPause = false;
                intent.putExtra("next", true);
                mFlag = true;

                break;
            default:
                break;
        }

        startService(intent);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }
}
