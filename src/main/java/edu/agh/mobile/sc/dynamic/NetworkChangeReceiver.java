package edu.agh.mobile.sc.dynamic;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import edu.agh.mobile.sc.Constants;
import edu.agh.mobile.sc.Settings;
import edu.agh.mobile.sc.communication.MessageBuilder;

/**
 * @author Przemyslaw Dadel
 */
public class NetworkChangeReceiver extends AbstractStatusBroadcastReceiver {

    private static final String LAST_NETWORK_STATUS = "last.network.status";
    private final Settings settings = new Settings();

    @Override
    public void onReceive(Context context, Intent intent) {

        final ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        Log.d(Constants.SC_LOG_TAG, "Active network =" + activeNetwork);
        if (activeNetwork != null) {
            boolean isConnected = activeNetwork.isConnectedOrConnecting();

            boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
            boolean isMobile = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
            boolean isWiMax = activeNetwork.getType() == ConnectivityManager.TYPE_WIMAX;

            final String status = String.format("%b/%b/%b/%b", isConnected, isWiFi, isMobile, isWiMax);
            if (!status.equals(getLastStatus(context))) {
                sendNetworkUpdate(context, isWiFi, isMobile, isWiMax);
                rememberLastStatus(context, status);
            }

        } else {
            rememberLastStatus(context, "empty");
        }
    }

    private void sendNetworkUpdate(Context context, boolean wiFi, boolean mobile, boolean wiMax) {
        final String serverURL = new Settings().getServerAddress(context) + "/network";
        final MessageBuilder builder = MessageBuilder.create(context, "ntoken");

        builder.with("wifi", wiFi);
        builder.with("mobile", mobile);
        builder.with("wimax", wiMax);

        sendMessage(context, serverURL, builder);
    }

    private String getLastStatus(Context context) {
        return settings.getSharedPreferences(context).getString(LAST_NETWORK_STATUS, "none");
    }

    private void rememberLastStatus(Context context, String status) {
        settings.getSharedPreferences(context).edit().putString(LAST_NETWORK_STATUS, status).commit();
    }
}
