package edu.agh.mobile.sc.assumtions;

import android.content.Context;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Przemyslaw Dadel
 */
public class PingDataProvider {

    private static final String REACHABLE = "REACHABLE";
    private static final String UNREACHABLE = "UNREACHABLE";
    private static final String UNKNOWN_HOST = "UNKNOWN_HOST";
    private static final String ERROR = "ERROR";

    public Map<String, Object> getData(Context context, String address, int timeout) {
        final Map<String, Object> result = new HashMap<String, Object>();
        result.put("success", false);
        result.put("address", address);
        result.put("timeout", timeout);
        try {
            final InetAddress inetAddress = InetAddress.getByName(address);
            if (inetAddress.isReachable(timeout)) {
                result.put("status", REACHABLE);
            } else {
                result.put("unreachable", UNREACHABLE);
            }
            result.put("success", true);
        } catch (UnknownHostException e) {
            result.put("status", UNKNOWN_HOST);
        } catch (IOException e) {
            result.put("status", ERROR + "|" + e.getMessage());
        }
        return result;
    }
}
