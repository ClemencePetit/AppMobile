package com.example.multimedia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
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

        //on récupère la liste des musiques
        setContentView(R.layout.activity_main);

        final String musicOnly = MediaStore.Audio.Media.IS_MUSIC + " != 0 ";

        //AJOUTER PROTECTION

        listOfSongs = new ListOfSongs(this,musicOnly);

        //On affiche la liste des musiques
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

    public void playThis(View v){
        TextView Tv = (TextView) v;
        Toast.makeText(getApplicationContext(),"Playing : " + Tv.getText(),Toast.LENGTH_SHORT).show();
        int index = listOfSongs.getNames().indexOf(Tv.getText());
        //startMusic(index);

        Intent intent = new Intent(this,MessengerService.class);
        MessengerService.changeIndex(index);
    }

    public void play(View v){
        Toast.makeText(getApplicationContext(),"Playing : " + listOfSongs.getNames().get(0),Toast.LENGTH_SHORT).show();
        startMusic(0);
    }
    public void startMusic(int index){
        Intent intent = new Intent(this, MessengerService.class);
        ArrayList<String> paths = listOfSongs.getPaths();
        intent.putExtra("allPaths", paths);
        intent.putExtra("index",index);
        startService(intent);
    }
    /*
    @Override
    protected void OnStart(){
        super.onStart();
        bindService(new Intent(this, MessengerService.class),mConnection,)
    }
*/
}
