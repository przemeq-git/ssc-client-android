package edu.agh.mobile.sc.dynamic;

import android.content.BroadcastReceiver;
import android.content.Context;
import edu.agh.mobile.sc.communication.MessageBuilder;
import edu.agh.mobile.sc.communication.StatusAsyncTask;

/**
 * @author Przemyslaw Dadel
 */
public abstract class AbstractStatusBroadcastReceiver extends BroadcastReceiver {

    protected void sendMessage(Context context, String serverURL, MessageBuilder messageBuilder) {
        new StatusAsyncTask(context, serverURL).execute(messageBuilder.build());
    }
}
