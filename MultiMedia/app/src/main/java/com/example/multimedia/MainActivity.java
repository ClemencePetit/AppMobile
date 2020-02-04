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
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public ListOfSongs listOfSongs;
    private Messenger mService = null;
    boolean bound;
    boolean sent=false;
    boolean isPlaying=false;
    IntentFilter filter1;
    IntentFilter filter2;
    IntentFilter filter3;
    Button playButton;
    SeekBar seekBar;


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
        filter1 = new IntentFilter();
        filter1.addAction("isPlaying");
        filter2 = new IntentFilter();
        filter2.addAction("startMusic");
        filter3 = new IntentFilter();
        filter3.addAction("updateSeekbar");

        registerReceiver(mMBroadcastReceiver, filter1);
        registerReceiver(mMBroadcastReceiver, filter2);
        registerReceiver(mMBroadcastReceiver, filter3);

        playButton=(Button)findViewById(R.id.playButton);
        seekBar=(SeekBar)findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    Message message = Message.obtain(null, MessengerService.MSG_SEEKBAR, progress * 1000, 0);
                    try {
                        mService.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


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
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mMBroadcastReceiver);

    }

    @Override
    protected void onStop(){
        super.onStop();
        if(bound)
        {
            unbindService(mConnection);
            bound=false;
        }
        //unregisterReceiver(mMBroadcastReceiver);
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

    public void next(View v) {

        if(!sent) {
            sendMessagePlay(1);
        }
        else {
            Message message;
            message= Message.obtain(null, MessengerService.MSG_NEXT,0,0);
            try{
                mService.send(message);
            } catch (RemoteException e){
                e.printStackTrace();
            }

        }

    }

    public void prev(View v) {

        if(!sent) {
            sendMessagePlay(listOfSongs.getPaths().size()-1);
        }
        else {
            Message message;
            message= Message.obtain(null, MessengerService.MSG_PREV,0,0);
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
            //Toast.makeText(context,"azertyuio "+intent.getBooleanExtra("Status",false),Toast.LENGTH_SHORT).show();
            //+intent.getBooleanExtra("Status",false)
            //isPlaying = intent.getBooleanExtra("Status",false);
            if(intent.getAction().compareTo("isPlaying")==0){
                if(intent.getBooleanExtra("Status",false))
                {
                    playButton.setText("PAUSE");
                }
                else
                {
                    playButton.setText("PLAY");
                }
            }else if(intent.getAction().compareTo("startMusic")==0)
            {
                //Toast.makeText(context, "start musique "+intent.getIntExtra("MaxDuration",0), Toast.LENGTH_SHORT).show();
                seekBar.setMax(intent.getIntExtra("MaxDuration",0));
                seekBar.setProgress(0);
            }else if(intent.getAction().compareTo("updateSeekbar")==0)
            {
                //Toast.makeText(context, "start musique", Toast.LENGTH_SHORT).show();
                seekBar.setProgress(intent.getIntExtra("Seek",0));
               // playButton.setText(""+intent.getIntExtra("Seek",0));
            }
            //Toast.makeText(getApplicationContext(),"playing : "+message,Toast.LENGTH_SHORT).show();
            // Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    };




}


