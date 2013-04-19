package edu.agh.mobile.sc.commands;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import edu.agh.mobile.sc.Constants;
import edu.agh.mobile.sc.dynamic.NetworkChangeReceiver;
import edu.agh.mobile.sc.dynamic.PowerUpdateReceiver;

/**
 * @author Przemyslaw Dadel
 */
public class DynamicCommand extends AbstractExtrasCommand {

    public static final String DYNAMIC_POWER_KEY = "dynamic.power";
    public static final String DYNAMIC_NETWORK_KEY = "dynamic.network";

    @Override
    public boolean accepts(Bundle extras) {
        if (hasExtrasValue(extras, DYNAMIC_POWER_KEY)) {
            return true;
        }
        if (hasExtrasValue(extras, DYNAMIC_NETWORK_KEY)) {
            return true;
        }
        return false;
    }

    @Override
    public final void execute(Context context, Bundle extras) {
        if (hasExtrasValue(extras, DYNAMIC_POWER_KEY)) {
            final boolean state = Boolean.valueOf(getExtrasValue(extras, DYNAMIC_POWER_KEY, String.class));
            updateComponentState(context, PowerUpdateReceiver.class, state);
        }
        if (hasExtrasValue(extras, DYNAMIC_NETWORK_KEY)) {
            final boolean state = Boolean.valueOf(getExtrasValue(extras, DYNAMIC_NETWORK_KEY, String.class));
            updateComponentState(context, NetworkChangeReceiver.class, state);
        }
    }

    private void updateComponentState(Context context, Class<?> component, boolean state) {
        final ComponentName receiver = new ComponentName(context, component);
        final PackageManager pm = context.getPackageManager();
        if (state) {
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
            Log.d(Constants.SC_LOG_TAG, "Disabling power connection updates");
        } else {
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);

        }
    }

    public final void execute(Context context, boolean status) {
        final Bundle data = new Bundle();
        data.putString(DynamicCommand.DYNAMIC_POWER_KEY, Boolean.toString(status));
        execute(context, data);
    }


}
