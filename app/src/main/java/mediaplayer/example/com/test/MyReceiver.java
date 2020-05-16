package mediaplayer.example.com.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by shashank on 3/22/2015.
 */
public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent myintent = new Intent(context, MyService.class);
        context.startService(myintent);
    }
}

