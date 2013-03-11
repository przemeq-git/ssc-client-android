package edu.agh.mobile.sc;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Przemyslaw Dadel
 */
public class GCMIntentService extends GCMBaseIntentService {

    private final Settings settings = new Settings();
    private final IdRegistration idRegistration = new IdRegistration();
    private final SocialComputingServer server = new SocialComputingServer();
    private final BatteryDataProvider batteryProvider = new BatteryDataProvider();
    private final NetworkDataProvider networkProvider = new NetworkDataProvider();
    private final MemDataProvider memoryDataProvider = new MemDataProvider();

    public GCMIntentService() {
        super(Constants.SENDER_ID);
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        if (!settings.isMobileComputingActive(context)) {
            Log.d(Constants.SC_LOG_TAG, "Computing platform was deactivated");
            return;
        }

        Log.d(Constants.SC_LOG_TAG, "Received message: " + intent.toString());

        final String token;
        if (hasExtrasValue(intent, "token")) {
            token = getExtrasValue(intent, "token", String.class);
        } else {
            token = "token";
        }

        if (hasExtrasValue(intent, "status")) {
            String value = getExtrasValue(intent, "status", String.class);
            Log.d(Constants.SC_LOG_TAG, "Reporting device status");
            reportDeviceStatus(value, token, context);
        }

        if (hasExtrasValue(intent, "message")) {
            final String message = getExtrasValue(intent, "message", String.class);
            final String title = getExtrasValue(intent, "title", String.class);
            handleNotification(context, message, title);
        }

        if (hasExtrasValue(intent, "register")) {
            final String server = getExtrasValue(intent, "register", String.class);
            settings.getSharedPreferences(context).edit().putString(Constants.SC_SERVER, server).commit();
            Log.d(Constants.SC_LOG_TAG, "Server location has been updated to: " + server);
            idRegistration.registerId(context, settings.getRegistrationId(context));

        }
    }

    private void handleNotification(Context context, String message, String title) {
        final int NOTIFICATION_ID = 1314151912;
        final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), 0);
        final NotificationManager notifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        final Notification note = new Notification(R.drawable.sc, title, System.currentTimeMillis());
        note.flags |= Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_ONLY_ALERT_ONCE;
        note.setLatestEventInfo(context, title, message, pendingIntent);
        notifManager.notify(NOTIFICATION_ID, note);
    }

    private boolean hasExtrasValue(Intent intent, String command) {
        final Object commandObject = intent.getExtras().get(command);
        return commandObject != null;
    }

    private <T> T getExtrasValue(Intent intent, String key, Class<T> clazz) {
        final Object value = intent.getExtras().get(key);
        return clazz.cast(value);
    }

    private void reportDeviceStatus(String server, String token, Context context) {
        try {
            final JSONObject result = new JSONObject();
            final IdentifierProvider statusHelper = new IdentifierProvider(context);
            result.put("deviceId", statusHelper.getDeviceId());

            result.put("battery", new JSONObject(batteryProvider.getData(context)));
            result.put("network", new JSONObject(networkProvider.getData(context)));

            result.put("memory", new JSONObject(memoryDataProvider.getData(context)));
            result.put("timestamp", System.currentTimeMillis());
            result.put("token", token);
            result.put("version", Constants.VERSION);

            this.server.sendStatus(server, result);
        } catch (Exception e) {
            Log.e(Constants.SC_LOG_TAG, "Exception", e);
            final Map<String, Object> error = new HashMap<String, Object>();
            error.put("error", e.getMessage());
            error.put("cause", e.getCause().toString());
            this.server.sendStatus(server, new JSONObject(error));
        }
    }

    @Override
    protected void onError(Context context, String errorId) {
        Log.i(Constants.SC_LOG_TAG, "Error occurred : " + errorId);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(Constants.SC_LOG_TAG, "Received registration id = " + registrationId);
        idRegistration.registerId(context, registrationId);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(Constants.SC_LOG_TAG, "Received un-registration id = " + registrationId);
        idRegistration.unregisterId(context, registrationId);
    }

}
