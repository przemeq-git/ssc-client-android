package edu.agh.mobile.sc;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Przemyslaw Dadel
 */
public class BatteryDataProvider implements DataProvider {

    private final IntentFilter batteryIntentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

    public Map<String, Object> getData(Context context) {
        final Map<String, Object> result = new HashMap<String, Object>();

        final Intent batteryStatus = context.registerReceiver(null, batteryIntentFilter);

        final int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        final boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING;
        final boolean isCharged = status == BatteryManager.BATTERY_STATUS_FULL;
        Log.d(Constants.SC_LOG_TAG, String.format("Is charging/charged = %b/%b", isCharging, isCharged));
        result.put("status", getBatteryStateName(status));

        final int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        final boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        final boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
        Log.d(Constants.SC_LOG_TAG, String.format("USB/AC = %b/%b", usbCharge, acCharge));
        result.put("plugged", getPowerSourceName(usbCharge, acCharge));

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryLevel = level / (float) scale;
        Log.i(Constants.SC_LOG_TAG, String.format("Battery level %f", batteryLevel));
        result.put("level", batteryLevel);

        int voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        Log.i(Constants.SC_LOG_TAG, String.format("Battery voltage %d", voltage));
        result.put("voltage", voltage);

        return result;
    }

    private Object getBatteryStateName(int status) {
        switch (status) {
            case BatteryManager.BATTERY_STATUS_FULL:
                return "FULL";
            case BatteryManager.BATTERY_STATUS_CHARGING:
                return "CHARGING";
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                return "DISCHARGING";
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                return "NOT_CHARGING";
            case BatteryManager.BATTERY_STATUS_UNKNOWN:
                return "UNKNOWN";
            default:
                return "UNDEFINED";
        }

    }

    private Object getPowerSourceName(boolean usbCharge, boolean acCharge) {
        if (acCharge) return "AC";
        if (usbCharge) return "USB";
        return "OTHER";
    }


}
