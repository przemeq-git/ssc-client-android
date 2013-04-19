package edu.agh.mobile.sc.commands;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import edu.agh.mobile.sc.Constants;
import edu.agh.mobile.sc.IdRegistration;
import edu.agh.mobile.sc.Settings;

/**
 * @author Przemyslaw Dadel
 */
public class RegisterCommand extends AbstractExtrasCommand {

    @Override
    public boolean accepts(Bundle extras) {
        if (hasExtrasValue(extras, "register")) {
            return true;
        }
        return false;
    }

    @Override
    public void execute(Context context, Bundle extras) {
        final Settings settings = new Settings();
        final IdRegistration idRegistration = new IdRegistration();
        final String server = getExtrasValue(extras, "register", String.class);
        settings.getSharedPreferences(context).edit().putString(Constants.SC_SERVER, server).commit();
        Log.d(Constants.SC_LOG_TAG, "Server location has been updated to: " + server);
        idRegistration.registerId(context, settings.getRegistrationId(context));
    }

}
