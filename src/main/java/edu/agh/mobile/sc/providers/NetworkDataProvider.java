package edu.agh.mobile.sc.providers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.format.Formatter;
import android.util.Log;
import edu.agh.mobile.sc.Constants;
import edu.agh.mobile.sc.Settings;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

import static edu.agh.mobile.sc.HashUtil.hash;

/**
 * @author Przemyslaw Dadel
 */
public class NetworkDataProvider implements DataProvider {

    private final Settings settings = new Settings();

    @Override
    public Map<String, Object> getData(Context context) {
        final Map<String, Object> result = new HashMap<String, Object>();


        final Map<String, Object> telephony = getTelephonySettings(context);
        result.put("telephony", new JSONObject(telephony));

        final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        /** Check the connection **/
        final NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        // Make sure the network is available
        result.put("wifi", false);
        if (wifiNetwork != null && wifiNetwork.isAvailable()) {
            Log.d(Constants.SC_LOG_TAG, "Wifi connection is available");
            Log.d(Constants.SC_LOG_TAG, "Wifi network subtype: " + wifiNetwork.getSubtypeName());
            if (wifiNetwork.isConnected()) {
                Log.d(Constants.SC_LOG_TAG, "Wifi connection is connected");
                result.put("wifi", true);
                final Map<String, Object> wifiSettings = getWifiSettgings(context);
                result.put("wifiConnection", new JSONObject(wifiSettings));
            }
        }


        /** Check the connection **/
        result.put("wimax", false);
        final NetworkInfo wimaxNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);
        if (wimaxNetwork != null && wimaxNetwork.isAvailable()) {
            Log.d(Constants.SC_LOG_TAG, "Wimax connection is available");
            Log.d(Constants.SC_LOG_TAG, "Wimax network subtype: " + wimaxNetwork.getSubtypeName());
            if (wimaxNetwork.isConnected()) {
                Log.d(Constants.SC_LOG_TAG, "Wimax connection is connected");
                result.put("wimax", true);
            }
        }

        /** Check the connection **/
        final NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        result.put("mobile", false);

        if (mobileNetwork != null && mobileNetwork.isAvailable()) {
            Log.d(Constants.SC_LOG_TAG, "Mobile connection is available");
            Log.d(Constants.SC_LOG_TAG, "Mobile network subtype: " + mobileNetwork.getSubtypeName());
            if (mobileNetwork.isConnected()) {
                Log.d(Constants.SC_LOG_TAG, "Mobile connection is connected");
                final Map<String, Object> mobileSettings = new HashMap<String, Object>();
                result.put("mobile", true);
                mobileSettings.put("mobileSubtype", mobileNetwork.getSubtypeName());
                mobileSettings.put("ip", getMobileIp(context, wifiNetwork));
                result.put("mobileConnection", new JSONObject(mobileSettings));
            }
        }

        return result;
    }

    private Map<String, Object> getTelephonySettings(Context context) {
        final Map<String, Object> mobile = new HashMap<String, Object>();

        final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (Log.isLoggable(Constants.SC_LOG_TAG, Log.DEBUG)) {
            Log.d(Constants.SC_LOG_TAG, "Mobile " + telephonyManager.getNetworkCountryIso());
            Log.d(Constants.SC_LOG_TAG, "Mobile " + telephonyManager.getNetworkOperatorName());
            Log.d(Constants.SC_LOG_TAG, "Mobile " + getNetworkTypeString(telephonyManager.getNetworkType()));
            Log.d(Constants.SC_LOG_TAG, "Mobile " + telephonyManager.getSimOperatorName());
        }
        mobile.put("operator", hash(telephonyManager.getNetworkOperatorName()));
        mobile.put("simOperator", hash(telephonyManager.getSimOperatorName()));
        mobile.put("networkCountry", telephonyManager.getNetworkCountryIso());
        mobile.put("networkType", getNetworkTypeString(telephonyManager.getNetworkType()));

        final Map<String, Object> cell = new HashMap<String, Object>();
        final CellLocation location = telephonyManager.getCellLocation();
        if (location instanceof CdmaCellLocation) {
            final CdmaCellLocation cellLocation = (CdmaCellLocation) location;
            if (Log.isLoggable(Constants.SC_LOG_TAG, Log.DEBUG)) {
                Log.d(Constants.SC_LOG_TAG, "CMDA = " + cellLocation.getBaseStationId());
                Log.d(Constants.SC_LOG_TAG, "CMDA = " + cellLocation.getNetworkId());
                Log.d(Constants.SC_LOG_TAG, "CMDA = " + cellLocation.getSystemId());
            }
            cell.put("cellType", "CDMA");
            cell.put("bsId", hash(cellLocation.getBaseStationId()));
            cell.put("networkId", hash(cellLocation.getNetworkId()));
            cell.put("systemId", hash(cellLocation.getSystemId()));
        } else if (location instanceof GsmCellLocation) {
            final GsmCellLocation cellLocation = (GsmCellLocation) location;
            if (Log.isLoggable(Constants.SC_LOG_TAG, Log.DEBUG)) {
                Log.d(Constants.SC_LOG_TAG, "GSM = " + cellLocation.getCid());
                Log.d(Constants.SC_LOG_TAG, "GSM = " + cellLocation.getLac());
            }
            cell.put("cellType", "GSM");
            cell.put("gsmCid", hash(cellLocation.getCid()));
            cell.put("gsmLac", hash(cellLocation.getLac()));
        } else if (location != null) {
            cell.put("cellType", location.getClass().getSimpleName());
        } else {
            cell.put("cellType", "NONE");
        }
        mobile.put("cell", new JSONObject(cell));
        return mobile;
    }

    private Map<String, Object> getWifiSettgings(Context context) {


        final Map<String, Object> wifiSettings = new HashMap<String, Object>();
        final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        final WifiInfo connection = wifiManager.getConnectionInfo();

        if (Log.isLoggable(Constants.SC_LOG_TAG, Log.DEBUG)) {
            Log.d(Constants.SC_LOG_TAG, connection.getSSID());
            Log.d(Constants.SC_LOG_TAG, connection.getBSSID());
            Log.d(Constants.SC_LOG_TAG, "" + connection.getIpAddress());
            Log.d(Constants.SC_LOG_TAG, "" + connection.getNetworkId());
            Log.d(Constants.SC_LOG_TAG, "" + connection.getHiddenSSID());
            Log.d(Constants.SC_LOG_TAG, "" + connection.getLinkSpeed());
            Log.d(Constants.SC_LOG_TAG, "" + connection.getSupplicantState().toString());
        }
        wifiSettings.put("ssid", hash(connection.getSSID()));
        wifiSettings.put("bssid", hash(connection.getBSSID()));
        wifiSettings.put("linkSpeed", connection.getLinkSpeed());
        wifiSettings.put("ip", Formatter.formatIpAddress(connection.getIpAddress()));
        wifiSettings.put("source", "static");


        final DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        if (dhcpInfo != null) {
            if (Log.isLoggable(Constants.SC_LOG_TAG, Log.DEBUG)) {
                Log.d(Constants.SC_LOG_TAG, "" + Formatter.formatIpAddress(dhcpInfo.ipAddress));
                Log.d(Constants.SC_LOG_TAG, "" + Formatter.formatIpAddress(dhcpInfo.netmask));
                Log.d(Constants.SC_LOG_TAG, "" + Formatter.formatIpAddress(dhcpInfo.gateway));
            }
            wifiSettings.put("ip", Formatter.formatIpAddress(dhcpInfo.ipAddress));
            wifiSettings.put("netmask", Formatter.formatIpAddress(dhcpInfo.netmask));
            wifiSettings.put("gateway", Formatter.formatIpAddress(dhcpInfo.gateway));
            wifiSettings.put("source", "dhcp");
        }

        return wifiSettings;
    }

    //gets no localhost ip that is not a wifi ip
    private String getMobileIp(Context context, NetworkInfo wifiNetwork) {
        final Set<String> excludedIp = new HashSet<String>();
        if (wifiNetwork.isConnected()) {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connection = wifiManager.getConnectionInfo();
            excludedIp.add(Formatter.formatIpAddress(connection.getIpAddress()));
        }
        final Set<String> availableIps = getAvailableIps();
        availableIps.removeAll(excludedIp);
        if (availableIps.size() > 0) {
            return availableIps.iterator().next();
        }
        return null;
    }

    private Set<String> getAvailableIps() {
        final Set<String> availableDeviceIp = new HashSet<String>();
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                final NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        Log.d(Constants.SC_LOG_TAG, "Available Ip: " + inetAddress.getHostAddress().toString());
                        availableDeviceIp.add(inetAddress.getHostAddress().toString());
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(Constants.SC_LOG_TAG, ex.toString());
        }
        return availableDeviceIp;
    }

    private String getNetworkTypeString(int networkType) {

        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return "UNKNOWN";
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "GPRS";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "EDGE";
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "UMTS";
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return "HSUPA";
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "HSPA";
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return "CDMA";
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return "EVDO_0";
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return "EVDO_A";
            case 12: //API 9 - TelephonyManager.NETWORK_TYPE_EVDO_B:
                return "EVDO_B";
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return "1xRTT";
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "IDEN";
            case 13: //API 11 - TelephonyManager.NETWORK_TYPE_LTE:
                return "LTE";
            case 14: //API 11 - TelephonyManager.NETWORK_TYPE_EHRPD:
                return "EHRPD";
            case 15: //API 13 - TelephonyManager.NETWORK_TYPE_HSPAP:
                return "HSPA+";
            default:
                return "UNDEFINED";
        }
    }
}
