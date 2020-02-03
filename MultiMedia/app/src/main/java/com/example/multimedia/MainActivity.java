package com.example.multimedia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public ListOfSongs listOfSongs;
    private Messenger mService = null;
    boolean bound;
    boolean sent=false;
    //boolean isPlaying=false;
    IntentFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        final String musicOnly = MediaStore.Audio.Media.IS_MUSIC + " != 0 ";

        //AJOUTER PROTECTION

        listOfSongs = new ListOfSongs(this,musicOnly);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.simplerow, listOfSongs.getNames());
        ListView listView =(ListView) findViewById(R.id.listMusics);
        listView.setAdapter(adapter);

        filter = new IntentFilter();
        filter.addAction("isPlaying");

        registerReceiver(mMBroadcastReceiver, filter);


    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService=new Messenger(service);
            bound=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService=null;
            bound=false;
        }
    };

    @Override
    protected void onStart(){
        super.onStart();
        bindService(new Intent(this, MessengerService.class),mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onStop(){
        super.onStop();
        if(bound)
        {
            unbindService(mConnection);
            bound=false;
        }
        unregisterReceiver(mMBroadcastReceiver);
    }



    public void playThis(View v){
        TextView Tv = (TextView) v;
        int index = listOfSongs.getNames().indexOf(Tv.getText());
        sendMessagePlay(index);
    }

    public void play(View v) {

       if(!sent) {
            sendMessagePlay(0);
        }
        else {
            Message message;
            /*if (isPlaying) {
                message= Message.obtain(null, MessengerService.MSG_PAUSE,0,0);
                //isPlaying = false;
            } else {
                message= Message.obtain(null, MessengerService.MSG_UNPAUSE,0,0);
                //isPlaying = true;
            }*/
            message= Message.obtain(null, MessengerService.MSG_PAUSE,0,0);
            try{
                mService.send(message);
            } catch (RemoteException e){
                e.printStackTrace();
            }

        }

    }

    public void sendMessagePlay(int index)
    {
        Message message;
        if(!bound) return;
        if(!sent)
        {
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("paths",listOfSongs.getPaths());
            message = Message.obtain(null, MessengerService.MSG_SEND_PATHS,index,0);
            message.setData(bundle);
            sent=true;
        }
        else
        {
            message= Message.obtain(null, MessengerService.MSG_PLAY,index,0);
        }
        try{
            mService.send(message);
        } catch (RemoteException e){
            e.printStackTrace();
        }
        //isPlaying=true;
    }

   private BroadcastReceiver mMBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Toast.makeText(context,""+intent.getBooleanExtra("Status",false),Toast.LENGTH_SHORT).show();
            //isPlaying = intent.getBooleanExtra("Status",false);
            //Toast.makeText(getApplicationContext(),"playing : "+message,Toast.LENGTH_SHORT).show();
            // Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    };


}


