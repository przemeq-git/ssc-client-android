package edu.agh.mobile.sc.providers;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import java.util.UUID;

import static edu.agh.mobile.sc.HashUtil.hash;

/**
 * @author Przemyslaw Dadel
 */
public class  IdentifierProvider {

    private static final String DEVICE_ID = "device.id";
    private final Context context;
    private final SharedPreferences preferences;

    public IdentifierProvider(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(edu.agh.mobile.sc.Settings.SC_PREFERENCE_TAG, Context.MODE_PRIVATE);
    }

    public String getDeviceId() {
        final String deviceId = preferences.getString(DEVICE_ID, null);
        if (deviceId != null) {
            return deviceId;
        }

        final StringBuilder idBuilder = new StringBuilder();
        final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (androidId != null && !"9774d56d682e549c".equals(androidId)) {
            final String uuid = hash(androidId);
            idBuilder.append("AID-").append(uuid);
        }

        if (idBuilder.length() != 0) {
            idBuilder.append("RID-").append(UUID.randomUUID().toString());
        }
        final String generatedDeviceId = idBuilder.toString();
        preferences.edit().putString(DEVICE_ID, generatedDeviceId).commit();
        return generatedDeviceId;
    }
}
