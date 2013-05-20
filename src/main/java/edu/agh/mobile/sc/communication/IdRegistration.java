package edu.agh.mobile.sc.communication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import edu.agh.mobile.sc.Constants;
import edu.agh.mobile.sc.Settings;
import edu.agh.mobile.sc.communication.RegistrationBackOff;
import edu.agh.mobile.sc.communication.SocialComputingServer;
import edu.agh.mobile.sc.providers.CPUDataProvider;
import edu.agh.mobile.sc.providers.DeviceDataProvider;
import edu.agh.mobile.sc.providers.IdentifierProvider;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Przemyslaw Dadel
 */
public class IdRegistration {

    private final static long MAX_BACK_OFF = 14 * 24 * 3600 * 1000; //2 weeks
    private static final int INITIAL_BACK_OFF = 30000; // 5min
    private final Settings settings = new Settings();
    private final DeviceDataProvider deviceProvider = new DeviceDataProvider();
    private final CPUDataProvider cpuProvider = new CPUDataProvider();

    public void registerId(Context context, String id) {
        settings.getSharedPreferences(context).
                edit().
                putString(Constants.REGISTRATION_ID, id).
                commit();
        settings.removeRegistrationFlag(context);
        final long timestamp = settings.setRegistrationTimestamp(context);

        final SocialComputingServer server = new SocialComputingServer();
        final String serverURL = settings.getServerAddress(context);

        final Map<String, Object> registrationData = new HashMap<String, Object>();
        registrationData.put("registrationId", id);
        registrationData.put("deviceId", new IdentifierProvider(context).getDeviceId());
        registrationData.put("device", new JSONObject(deviceProvider.getData(context)));
        registrationData.put("cpu", new JSONObject(cpuProvider.getData(context)));
        registrationData.put("register", true);
        registrationData.put("version", Constants.VERSION);

        boolean success = server.registerId(serverURL, new JSONObject(registrationData));
        if (success) {
            settings.setRegistrationSend(context, true);
            settings.setUnregistrationSend(context, false);
            setBackOff(context, INITIAL_BACK_OFF);
        } else {
            backOffServerRegistration(context, true, timestamp);
        }
    }

    public void unregisterId(Context context, String id) {
        settings.removeRegistrationFlag(context);
        final long timestamp = settings.setRegistrationTimestamp(context);
        final SocialComputingServer server = new SocialComputingServer();
        final String serverURL = settings.getServerAddress(context);
        final Map<String, Object> registrationData = new HashMap<String, Object>();
        registrationData.put("registrationId", id);
        registrationData.put("deviceId", new IdentifierProvider(context).getDeviceId());
        registrationData.put("register", false);
        registrationData.put("version", Constants.VERSION);
        boolean success = server.unregisterId(serverURL, new JSONObject(registrationData));
        if (success) {
            settings.setRegistrationSend(context, false);
            settings.setUnregistrationSend(context, true);
            setBackOff(context, INITIAL_BACK_OFF);
        } else {
            backOffServerRegistration(context, false, timestamp);
        }
    }

    private void backOffServerRegistration(Context context, boolean register, long timestamp) {
        final long backOffTimeMs = settings.getSharedPreferences(context).getLong(Constants.REGISTER_BACK_OFF_TIME, INITIAL_BACK_OFF);
        final long nextAttempt = SystemClock.elapsedRealtime() + backOffTimeMs;
        final Intent retryIntent = new Intent(RegistrationBackOff.REGISTRATION_BACK_OFF);
        retryIntent.putExtra("token", timestamp);
        retryIntent.putExtra("register", register);
        final PendingIntent retryPendingIntent = PendingIntent.getBroadcast(context, 0, retryIntent, 0);
        final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(retryPendingIntent);
        am.set(AlarmManager.ELAPSED_REALTIME, nextAttempt, retryPendingIntent);
        Log.d(Constants.SC_LOG_TAG, String.format("Will retry registration (%b) in %d s.", register, backOffTimeMs / 1000));
        final long newBackOff = (backOffTimeMs <= MAX_BACK_OFF / 2) ? backOffTimeMs * 2 : MAX_BACK_OFF;
        setBackOff(context, newBackOff);
    }

    private void setBackOff(Context context, long backOff) {
        settings.getSharedPreferences(context).edit().putLong(Constants.REGISTER_BACK_OFF_TIME, backOff).commit();
    }

}
