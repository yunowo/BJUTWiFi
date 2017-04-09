package me.liuyun.bjutlgn.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.util.Log

object NetworkUtils {
    val STATE_NO_NETWORK = 0
    val STATE_MOBILE = 1
    val STATE_BJUT_WIFI = 2
    val STATE_OTHER_WIFI = 3

    fun getNetworkState(context: Context): Int {
        var isMobile = false
        var isBjut = false
        var isOther = false
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = manager.activeNetworkInfo
        Log.d("activeNetworkInfo", activeNetworkInfo.toString())
        for (network in manager.allNetworks) {
            val info = manager.getNetworkInfo(network)
            Log.d("networkInfo", info.toString())
            when (info.type) {
                ConnectivityManager.TYPE_MOBILE -> {
                    isMobile = true
                }
                ConnectivityManager.TYPE_WIFI -> {
                    if (info.extraInfo.replace("\"", "") == "bjut_wifi") {
                        if (info.extraInfo != activeNetworkInfo.extraInfo) {
                            manager.bindProcessToNetwork(network)
                            Log.d("bind", network.toString())
                        }
                        isBjut = true
                    } else
                        isOther = true
                }
            }
        }
        if (isBjut) return STATE_BJUT_WIFI
        if (isMobile) return STATE_MOBILE
        if (isOther) return STATE_OTHER_WIFI
        return STATE_NO_NETWORK
    }

    fun getWifiSSID(context: Context): String {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifiManager.connectionInfo.ssid
    }
}
