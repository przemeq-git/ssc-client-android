package edu.agh.mobile.sc.dynamic;

import android.content.BroadcastReceiver;
import android.util.Log;
import edu.agh.mobile.sc.Constants;
import edu.agh.mobile.sc.communication.MessageBuilder;
import edu.agh.mobile.sc.communication.SocialComputingServer;

/**
 * @author Przemyslaw Dadel
 */
public abstract class AbstractStatusBroadcastReceiver extends BroadcastReceiver {


    protected void sendMessage(String serverURL, MessageBuilder messageBuilder) {
        final SocialComputingServer server = new SocialComputingServer();
        try {
            server.sendStatus(serverURL, messageBuilder.build());
        } catch (Exception e) {
            Log.e(Constants.SC_LOG_TAG, "Exception", e);
            messageBuilder.with("error", e.getMessage());
            messageBuilder.with("cause", e.getCause().toString());
            server.sendStatus(serverURL, messageBuilder.build());
        }
    }
}
