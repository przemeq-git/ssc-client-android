package edu.agh.mobile.sc.dynamic;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import edu.agh.mobile.sc.Settings;
import edu.agh.mobile.sc.communication.MessageBuilder;

/**
 * @author Przemyslaw Dadel
 */
public class PowerUpdateReceiver extends AbstractStatusBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        final boolean isNetworkConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (isNetworkConnected) {
            final boolean connected;
            if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
                connected = true;
            } else {
                connected = false;
            }

            final String serverURL = new Settings().getServerAddress(context) + "/battery";

            final MessageBuilder builder = MessageBuilder.create(context, "btoken");
            builder.with("connected", connected);
            sendMessage(serverURL, builder);
        }
    }

}
