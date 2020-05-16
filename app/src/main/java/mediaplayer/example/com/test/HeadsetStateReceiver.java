package mediaplayer.example.com.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by shashank on 3/22/2015.
 */
public class HeadsetStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
       // Intent myIntent = new Intent(context, MainActivity.class);
        Intent i = new Intent();
        i.setClassName("mediaplayer.example.com.test", "mediaplayer.example.com.test.MainActivity");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        MyService myService = new MyService();
//        myService.listenForHeadset();
//        try{
//            context.startActivity(myIntent);
//        }catch (Exception e){
//            Log.d("Error in apllication start","mydebug");
//        }

    }

}
