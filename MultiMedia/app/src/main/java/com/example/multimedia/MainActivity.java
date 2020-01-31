package com.example.multimedia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
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

    private ListOfSongs listOfSongs;
    private Messenger mService = null;
    boolean bound;

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
    }

    private ServiceConnection mConnectier = new ServiceConnection() {
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

    public void sayHello(View v){
        if(!bound) return;
        Message msg = Message.obtain(null, MessengerService.MSG_SAY_HELLO,0,0);
        try{
            mService.send(msg);
        } catch (RemoteException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void OnStart(){
        super.onStart();
        bindService(new Intent(this, MessengerService.class),mConnection,)
    }

}
