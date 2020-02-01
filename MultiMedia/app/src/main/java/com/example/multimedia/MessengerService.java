package com.example.multimedia;

import android.app.Service;
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

import java.util.Locale;

import static android.media.session.PlaybackState.ACTION_PLAY;


public class MessengerService extends Service {
    public static final Intent ACTION_PLAY = new Intent("com.example.multimedia.ACTION_PLAY");
    static final int MSG_SAY_HELLO=1;

    public static MediaPlayer mp;

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
        processPlayRequest();
        return START_STICKY;
    }

    private void processPlayRequest(){
        ListOfSongs los = new ListOfSongs(this,musicOnly);
        mp = MediaPlayer.create(this, Uri.parse(los.getPaths().get(0)));
        mp.start();
    }
}
