package me.liuyun.bjutlgn.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class NetworkUtils {
    public final static int STATE_NO_NETWORK = 0;
    public final static int STATE_MOBILE = 1;
    public final static int STATE_BJUT_WIFI = 2;
    public final static int STATE_OTHER_WIFI = 3;

    public static int getNetworkStateLegacy(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        if (info != null && info.isConnectedOrConnecting()) {
            switch (info.getType()) {
                case ConnectivityManager.TYPE_MOBILE:
                    return STATE_MOBILE;
                case ConnectivityManager.TYPE_WIFI: {
                    if (getWifiSSID(context).replace("\"", "").equals("bjut_wifi"))
                        return STATE_BJUT_WIFI;
                    else return STATE_OTHER_WIFI;
                }
                default:
                    return STATE_NO_NETWORK;
            }
        } else return STATE_NO_NETWORK;
    }

    public static int getNetworkState(Context context) {
        boolean isMobile = false;
        boolean isBjut = false;
        boolean isOther = false;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        Log.d("activeNetworkInfo", activeNetworkInfo.toString());
        for (Network network : manager.getAllNetworks()) {
            NetworkInfo info = manager.getNetworkInfo(network);
            Log.d("networkInfo", info.toString());
            switch (info.getType()) {
                case ConnectivityManager.TYPE_MOBILE: {
                    isMobile = true;
                    break;
                }
                case ConnectivityManager.TYPE_WIFI: {
                    if (info.getExtraInfo().replace("\"", "").equals("bjut_wifi")) {
                        if (!info.getExtraInfo().equals(activeNetworkInfo.getExtraInfo())) {
                            manager.bindProcessToNetwork(network);
                            Log.d("bind", network.toString());
                        }
                        isBjut = true;
                    } else isOther = true;
                }
            }
        }
        if (isBjut) return STATE_BJUT_WIFI;
        if (isMobile) return STATE_MOBILE;
        if (isOther) return STATE_OTHER_WIFI;
        return STATE_NO_NETWORK;
    }

    public static String getWifiSSID(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wifiManager.getConnectionInfo().getSSID();
    }
}
