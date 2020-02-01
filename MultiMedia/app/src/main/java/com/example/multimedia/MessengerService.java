package com.example.multimedia;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Messenger;
import android.provider.MediaStore;
import android.widget.Toast;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import java.util.ArrayList;
import java.util.Locale;

import static android.media.session.PlaybackState.ACTION_PLAY;


public class MessengerService extends Service {
    public static final Intent ACTION_PLAY = new Intent("com.example.multimedia.ACTION_PLAY");
    static final int MSG_SAY_HELLO=1;

    public static MediaPlayer mp;
    private static ArrayList<String> musicPaths;
    private static Context context;
    private static int index;

    final String musicOnly = MediaStore.Audio.Media.IS_MUSIC + " != 0 ";

    class IncomingHandler extends Handler{
        @Override
        public void handleMessage(Message msg){
            switch (msg.what)
            {
                case MSG_SAY_HELLO:
                    Toast.makeText(getApplicationContext(),"hello",Toast.LENGTH_SHORT).show();
                    break;
                    default:
                        super.handleMessage(msg);
            }
        }
    }

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    public IBinder onBind(Intent intent){
        return mMessenger.getBinder();
    }

    @Override
    public void onCreate(){
     mp  = new MediaPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        //musicPaths = null;
        context = this;
        int index = 1;
        //String path = "";
        Bundle extras = intent.getExtras();
        if(extras != null){
            musicPaths = (ArrayList<String>) extras.get("allPaths");
        }
        return START_STICKY;
    }

    private static void processPlayRequest(Uri path){
        mp.reset();
        mp = MediaPlayer.create(context, path);
        mp.start();
    }

    public static void changeIndex(int newIndex){
        index = newIndex;
        processPlayRequest(Uri.parse(musicPaths.get(newIndex)));
    }

    public static void start(){
        if(!mp.isPlaying()){
            changeIndex(0);
        }
    }
}
