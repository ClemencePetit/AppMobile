package com.example.multimedia;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Messenger;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;


public class MessengerService extends Service {
    static final int MSG_SEND_PATHS=2;
    static final int MSG_PLAY=3;
    static final int MSG_PAUSE=4;
    //static final int MSG_UNPAUSE=5;
    private static ArrayList<String> musicPaths;

    private static Context context;

    public static MediaPlayer mp=new MediaPlayer();
    private static int currentIndex;

    @Override
    public void onCreate()
    {
        super.onCreate();
        //mp=new MediaPlayer();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
            public void onCompletion(MediaPlayer mediaPlayer){
                currentIndex = (currentIndex+1) % musicPaths.size() ;
                setMusic();
            }
        });
        context=this;
    }

    private void setMusic(){
        mp.reset();
        try { mp.setDataSource(this,Uri.parse((musicPaths.get(currentIndex)))); } catch (Exception e) {}
        try { mp.prepare(); } catch (Exception e) {}
        mp.start();
    }

   private void setPause(){
        if(mp.isPlaying()) {
            mp.pause();
        }
        else{
            mp.start();
        }
       //sendMessageToActivity();
    }

    /*private void setUnpause(){
        mp.start();
    }*/

    class IncomingHandler extends Handler{
        @Override
        public void handleMessage(Message msg){
            switch (msg.what)
            {
                case MSG_SEND_PATHS:
                    musicPaths=msg.getData().getStringArrayList("paths");
                    currentIndex=msg.arg1;
                    setMusic();
                    break;
                case MSG_PLAY:
                    currentIndex=msg.arg1;
                    setMusic();
                    break;
                case MSG_PAUSE:
                    setPause();
                    //sendMessageToActivity();
                    break;
                /*case MSG_UNPAUSE:
                    setUnpause();
                    //sendMessageToActivity();
                    break;*/
                    default:
                        super.handleMessage(msg);
            }

            //sendMessageToActivity();
        }
    }

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    public IBinder onBind(Intent intent){
        return mMessenger.getBinder();
    }

    @Override
    public void onDestroy() {

    }

    /*public boolean isPlaying()
    {
        return mp.isPlaying();
    }*/

    private void sendMessageToActivity() {
        Intent intent = new Intent();//context,MainActivity.class);
        // You can also include some extra data.
        intent.putExtra("Status", mp.isPlaying());
        intent.setAction("isPlaying");
        //LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        sendBroadcast(intent);

        Toast.makeText(context,"pifpafpouf",Toast.LENGTH_SHORT).show();

    }

}
