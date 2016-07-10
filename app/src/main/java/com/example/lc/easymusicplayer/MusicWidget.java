package com.example.lc.easymusicplayer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Created by lc on 2016/6/2.
 */
public class MusicWidget extends AppWidgetProvider {

    public static final String WIDGET_BUTTON_ACTON = "widget_button_acton";
    public static final String TAG = "MusicWidget";
    public static final String MUSIC_WIDGET = "MusicWidget";
    private int mPlayFlag = 0;
    public static String MAIN_UPDATE_UI = "main_activity_update_ui";//Action
    public static String KEY_MAIN_ACTIVITY_UI_BTN = "main_activity_ui_btn_key";  //putExtra中传送当前播放状态的key
    public static String KEY_MAIN_ACTIVITY_UI_TEXT = "main_activity_ui_text_key"; //putextra中传送TextView的key
    public static final int  VAL_UPDATE_UI_PLAY = 1,VAL_UPDATE_UI_PAUSE =2;//当前歌曲的播放状态
    private boolean mStop = true;


    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        Log.d("MusicWidget", "action:"+action);
        if(intent.hasCategory(Intent.CATEGORY_ALTERNATIVE)){
            Log.d(MUSIC_WIDGET, "action:"+action);
            Uri data = intent.getData();
            int buttonId = Integer.parseInt(data.getSchemeSpecificPart());
            Log.d(MUSIC_WIDGET,String.valueOf(buttonId));
            switch (buttonId){
                case R.id.play_pause:
                    Log.d(MUSIC_WIDGET,"play");
                    pushAction(context,MusicService.ACTION_PLAY_PAUSE);
                    if(mStop){
                        Intent startIntent = new Intent(context,MusicService.class);
                        startIntent.putExtra("play",true);
                        context.startService(startIntent);
                        mStop = false;
                        Log.d(MUSIC_WIDGET,"play");

                    }

                    break;
                case R.id.next_song:
                    pushAction(context,MusicService.ACTION_NEXT);
                    break;
                case R.id.prev_song:
                    pushAction(context,MusicService.ACTION_PRE);
                    break;
            }
        }else if(MAIN_UPDATE_UI.equals(action)){
            Log.d(MUSIC_WIDGET,"UI");
            int play_pause = intent.getIntExtra(KEY_MAIN_ACTIVITY_UI_BTN,-1);
            int songid= intent.getIntExtra(KEY_MAIN_ACTIVITY_UI_TEXT,-1);
            switch (play_pause){
                case VAL_UPDATE_UI_PLAY:
                    pushUpdate(context,AppWidgetManager.getInstance(context),"" +songId2songName(songid),true);
                    break;
                case VAL_UPDATE_UI_PAUSE:
                    pushUpdate(context,AppWidgetManager.getInstance(context),""+songId2songName(songid),false);
                    break;
            }
        }
        super.onReceive(context, intent);
    }
    private String songId2songName(int songId){
        String songName="libai";
        switch (songId){
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
    private void pushAction(Context context,int ACTION){
        Intent actionIntent= new Intent(MusicService.ACTION);
        actionIntent.putExtra(MusicService.KEY_USR_ACTION,ACTION);
        context.sendBroadcast(actionIntent);
    }
    private PendingIntent getPendingIntent(Context context , int buttonId){
        Intent intent = new Intent();
        intent.setClass(context,MusicWidget.class);
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        intent.setData(Uri.parse("lc:"+buttonId));
        PendingIntent pi=PendingIntent.getBroadcast(context,0,intent,0);
        return  pi;
    }
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        super.onUpdate(context, appWidgetManager, appWidgetIds);

        pushUpdate(context, appWidgetManager,"",false);

    }

    private void pushUpdate(Context context, AppWidgetManager appWidgetManager,String songName,Boolean play_pause) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_widget);


        remoteViews.setOnClickPendingIntent(R.id.play_pause,getPendingIntent(context,R.id.play_pause));
        remoteViews.setOnClickPendingIntent(R.id.next_song,getPendingIntent(context,R.id.next_song));
        remoteViews.setOnClickPendingIntent(R.id.prev_song,getPendingIntent(context,R.id.prev_song));
        //设置歌曲标题
        if(!songName.equals("")){
            remoteViews.setTextViewText(R.id.song_name,songName);
        }
        // 设定按钮图片
        if(play_pause){
            remoteViews.setImageViewResource(R.id.play_pause,R.drawable.car_musiccard_pause);
        }else {
            remoteViews.setImageViewResource(R.id.play_pause,R.drawable.car_musiccard_play);
        }

        ComponentName componentName = new ComponentName(context,MusicWidget.class);
        appWidgetManager.updateAppWidget(componentName,remoteViews);
    }
}
