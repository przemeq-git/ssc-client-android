package edu.agh.mobile.sc;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import edu.agh.mobile.sc.dynamic.PowerUpdateReceiver;

/**
 * @author Przemyslaw Dadel
 */
public class Settings {
    public static final String SC_PREFERENCE_TAG = "SC_PREFERENCE_TAG";

    public final SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SC_PREFERENCE_TAG, Context.MODE_PRIVATE);
    }

    public boolean isMobileComputingActive(Context context) {
        return getSharedPreferences(context).getBoolean("social-computing-active", true);
    }

    /**
     * @param context
     * @param value   true - active
     * @return
     */
    public boolean setMobileComputingActive(Context context, boolean value) {
        return getSharedPreferences(context).
                edit().
                putBoolean("social-computing-active", value).
                commit();
    }

    /**
     * @param context
     * @return registration id or empty string if no id is present
     */
    public String getRegistrationId(Context context) {
        return getSharedPreferences(context).getString(Constants.REGISTRATION_ID, "");
    }

    public String getServerAddress(Context context) {
        return getSharedPreferences(context).getString(Constants.SC_SERVER, "http://elab.lab.uvalight.net:8080");
    }

    public boolean isRegistrationSend(Context context) {
        final SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getBoolean(Constants.SC_REGISTERED, false);
    }

    public void setRegistrationSend(Context context, boolean value) {
        setBooleanValue(context, Constants.SC_REGISTERED, value);
    }

    public boolean isUnregistrationSend(Context context) {
        final SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getBoolean(Constants.SC_UNREGISTERED, false);
    }

    public void setUnregistrationSend(Context context, boolean value) {
        setBooleanValue(context, Constants.SC_UNREGISTERED, value);
    }

    private void setBooleanValue(Context context, String key, boolean value) {
        getSharedPreferences(context).
                edit().
                putBoolean(key, value).
                commit();
    }

    public void removeRegistrationFlag(Context context) {
        getSharedPreferences(context).edit().putBoolean(Constants.SC_REGISTERED, false).commit();
    }

    public boolean getDynamicUpdatesStatus(Context context) {
        final ComponentName receiver = new ComponentName(context, PowerUpdateReceiver.class);
        final PackageManager pm = context.getPackageManager();
        final int componentEnabledSetting = pm.getComponentEnabledSetting(receiver);
        return componentEnabledSetting == PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
    }
}
