package edu.agh.mobile.sc;

import android.content.Context;
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

    private final Settings settings = new Settings();
    private final DeviceDataProvider deviceProvider = new DeviceDataProvider();
    private final CPUDataProvider cpuProvider = new CPUDataProvider();

    public void registerId(Context context, String id) {
        settings.getSharedPreferences(context).
                edit().
                putString(Constants.REGISTRATION_ID, id).
                commit();

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
        }
    }

    public void unregisterId(Context context, String id) {
        settings.removeRegistrationFlag(context);
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
        }
    }

}
