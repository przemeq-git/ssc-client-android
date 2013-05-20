package edu.agh.mobile.sc.communication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import edu.agh.mobile.sc.Constants;
import edu.agh.mobile.sc.Settings;

/**
 * @author Przemyslaw Dadel
 */
public class RegistrationBackOff extends BroadcastReceiver {

    public static final String REGISTRATION_BACK_OFF = "edu.agh.mobile.sc.REGISTRATION_BACK_OFF";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(REGISTRATION_BACK_OFF)){
            final boolean register = intent.getBooleanExtra("register", false);
            final long token = intent.getLongExtra("token", System.currentTimeMillis());
            final Settings settings = new Settings();
            final long timestamp = settings.getRegistrationTimestamp(context);
            if (token == timestamp){
                final String id = settings.getRegistrationId(context);
                new RegistrationTask(context).execute(id, Boolean.toString(register));
            } else {
                Log.d(Constants.SC_LOG_TAG, "Will not retry, registration invalidated");
            }
        }
    }
}
