package mediaplayer.example.com.test;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

/**
 * Created by shashank on 3/22/2015.
 */

  public  class  MyService extends Service{

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter receiverFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        HeadsetStateReceiver receiver = new HeadsetStateReceiver();
        registerReceiver( receiver, receiverFilter );
        return Service.START_NOT_STICKY;
    }

    public void listenForHeadset(){
        IntentFilter receiverFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        HeadsetStateReceiver receiver = new HeadsetStateReceiver();
        registerReceiver( receiver, receiverFilter );
    }

}
