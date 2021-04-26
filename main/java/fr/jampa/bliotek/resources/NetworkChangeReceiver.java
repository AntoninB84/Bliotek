package fr.jampa.bliotek.resources;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        /** Not actually used ... **/

        if(activeNetwork != null){
            // Internet connection
            Log.i("NetworkChange", "Network state has changed");
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){
                // connected to wifi
                Log.i("Bliotest", "Internet Connected");
            } else if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE){
                // Connected to mobile data
                Log.i("Bliotest", "Mobile Data Connected");
            }
        }else{
            // No connection
            Log.i("Bliotest", "No Internet");
        }
    }
}
