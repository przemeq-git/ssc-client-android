package edu.agh.mobile.sc;

import android.content.Context;
import android.os.Build;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Przemyslaw Dadel
 */
public class DeviceDataProvider implements DataProvider {
    @Override
    public Map<String, Object> getData(Context context) {
        final Map<String, Object> result = new HashMap<String, Object>();
        result.put("osVersion", Build.VERSION.RELEASE);
        result.put("kernel", System.getProperty("os.version"));
        result.put("codename", Build.VERSION.CODENAME);
        result.put("incremental", Build.VERSION.INCREMENTAL);
        result.put("sdk", Build.VERSION.SDK_INT);
        result.put("device", Build.DEVICE);
        result.put("model", Build.MODEL);
        result.put("product", Build.PRODUCT);
        return result;
    }
}
