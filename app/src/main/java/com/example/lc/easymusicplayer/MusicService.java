package com.example.lc.easymusicplayer;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

/**
 * Created by lc on 2016/5/31.
 */
public class MusicService extends Service{

    public static final String TAG = "MusicService";
    private MediaPlayer mMediaPlayer;
    private int [] mMusicList= new int[3];
    private int mIndex=1;
    public static final String PREV = "2";
    private IBinder mIBinder= new LocalBinder();
    private boolean mPlayState = true;
    private boolean mState=true;
    public static String ACTION = "to_service";
    public static String KEY_USR_ACTION = "key_usr_action";
    public static final int ACTION_PRE = 0, ACTION_PLAY_PAUSE = 1, ACTION_NEXT = 2;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action= intent.getAction();
            if(ACTION.equals(action)){
                int widget_action= intent.getIntExtra(KEY_USR_ACTION,-1);
                switch (widget_action){
                    case ACTION_PLAY_PAUSE:
                        if(mPlayState){
                            mMediaPlayer.start();
                            mPlayState= false;
                            postState(context,MusicWidget.VAL_UPDATE_UI_PLAY,mIndex);
                        }else {
                            mMediaPlayer.pause();
                            mPlayState = true;

                        }

                        Log.d("MusicService","action_play");
                        break;
                    case ACTION_NEXT:
                        playNext();
                        break;
                    case ACTION_PRE:
                        playPrev();
                        break;
                }
            }


        }
    };

    public int getIndex() {
        return mIndex;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);
        registerReceiver(receiver,intentFilter);
        Log.i("MusicService","onCreate");
        initList();
        mMediaPlayer = MediaPlayer.create(this,mMusicList[mIndex]);
    }

    private void initList() {

        mMusicList[0]=R.raw.hen_ai_hen_ai_ni;
        mMusicList[1]=R.raw.libai;
        mMusicList[2]=R.raw.new_day;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if(intent.hasExtra("play")&&mState){
            Log.i("MusicService","play");
            mMediaPlayer.start();
            mPlayState = true;
            postState(getApplicationContext(),MusicWidget.VAL_UPDATE_UI_PLAY,mIndex);
            mState = false;

        }else {
            Log.i("MusicService","pause");
            mMediaPlayer.pause();
            mPlayState = false;
            postState(getApplicationContext(),MusicWidget.VAL_UPDATE_UI_PAUSE,mIndex);
            mState = true;
        }
        Log.i("MusicService","");
        if(intent.hasExtra("prev")){
            Log.i("MusicService","playPrev");
            playPrev();
        }
        if(intent.hasExtra("next")){
            playNext();
        }

        Log.d(TAG,"hen_ai_hen_ai_ni"+intent.hasExtra("hen_ai_hen_ai_ni"+""));
        if (intent.hasExtra("hen_ai_hen_ai_ni")){

            mIndex = intent.getIntExtra("hen_ai_hen_ai_ni",1);
            AssetFileDescriptor afd = getApplicationContext().getResources().openRawResourceFd(mMusicList[mIndex]);
            Log.i(TAG,"hen_ai_hen_ai_ni clicked");
            mMediaPlayer.reset();
            try {
                mMediaPlayer.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getDeclaredLength());
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                afd.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            getSongName();
            mPlayState = true;
            mState = false;
            postState(getApplicationContext(),MusicWidget.VAL_UPDATE_UI_PLAY,mIndex);

        }
        if (intent.hasExtra("libai")){

            mIndex = intent.getIntExtra("libai",1);
            AssetFileDescriptor afd = getApplicationContext().getResources().openRawResourceFd(mMusicList[mIndex]);
            Log.i(TAG,"libai clicked");
            mMediaPlayer.reset();
            try {
                mMediaPlayer.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getDeclaredLength());
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                afd.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            getSongName();
            mPlayState = true;
            mState = false;
            postState(getApplicationContext(),MusicWidget.VAL_UPDATE_UI_PLAY,mIndex);

        }

        if (intent.hasExtra("new_day")){

            mIndex = intent.getIntExtra("new_day",1);
            AssetFileDescriptor afd = getApplicationContext().getResources().openRawResourceFd(mMusicList[mIndex]);
            Log.i(TAG,"new_day clicked");
            mMediaPlayer.reset();
            try {
                mMediaPlayer.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getDeclaredLength());
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                afd.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            getSongName();
            mPlayState = true;
            mState = false;
            postState(getApplicationContext(),MusicWidget.VAL_UPDATE_UI_PLAY,mIndex);

        }


        return START_NOT_STICKY;

    }

    private void postState(Context context,int state ,int songid) {
        Intent actionIntent= new Intent(MusicWidget.MAIN_UPDATE_UI);
        actionIntent.putExtra(MusicWidget.KEY_MAIN_ACTIVITY_UI_BTN,state);
        actionIntent.putExtra(MusicWidget.KEY_MAIN_ACTIVITY_UI_TEXT,songid);
        context.sendBroadcast(actionIntent);

    }

    private void playNext() {
        if(++mIndex>2){
            mIndex =0;
        }
        AssetFileDescriptor afd = getApplicationContext().getResources().openRawResourceFd(mMusicList[mIndex]);
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getDeclaredLength());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            afd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPlayState = true;
        mState = false;
        postState(getApplicationContext(),MusicWidget.VAL_UPDATE_UI_PLAY,mIndex);

    }

    private void playPrev() {

        if(--mIndex<0){
            mIndex= 2;
        }
        AssetFileDescriptor afd = getApplicationContext().getResources().openRawResourceFd(mMusicList[mIndex]);
        Log.i("MusicService","playPrev");
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getDeclaredLength());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            afd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        getSongName();
        mPlayState = true;
        mState = false;
        postState(getApplicationContext(),MusicWidget.VAL_UPDATE_UI_PLAY,mIndex);

    }

    @Override
    public void onDestroy() {


        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }
    public class LocalBinder extends Binder{
        MusicService getService(){
            return  MusicService.this;
        }
    }
    public String getSongName(){
        String songName="默认";
        Log.i("MusicService",String.valueOf(mIndex));
        switch (mIndex){
            case 0:
                songName = "hen_ai_hen_ai_ni";
                break;
            case 1:
                songName = "li_bai";
                break;
            case 2:
                songName = "new_day";
                break;


        }
        return songName;
    }
}
