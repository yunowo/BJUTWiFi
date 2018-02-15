package me.liuyun.bjutlgn.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import me.liuyun.bjutlgn.util.NetworkUtils.NetworkState.*

object NetworkUtils {
    enum class NetworkState {
        STATE_NO_NETWORK, STATE_MOBILE, STATE_BJUT_WIFI, STATE_OTHER_WIFI
    }

    fun getNetworkState(context: Context): NetworkState {
        var isMobile = false
        var isBjut = false
        var isOther = false
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = manager.activeNetworkInfo ?: return STATE_NO_NETWORK
        Log.d("activeNetworkInfo", activeNetworkInfo.toString())
        for (network in manager.allNetworks) {
            val info = manager.getNetworkInfo(network)
            Log.d("networkInfo", info.toString())
            when (info.type) {
                ConnectivityManager.TYPE_MOBILE -> {
                    isMobile = true
                }
                ConnectivityManager.TYPE_WIFI -> {
                    if (info.extraInfo != null && info.extraInfo.replace("\"", "") == "bjut_wifi") {
                        if (info.extraInfo != activeNetworkInfo.extraInfo) {
                            manager.bindProcessToNetwork(network)
                            Log.d("bind", info.extraInfo)
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
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = manager.activeNetworkInfo ?: return "<unknown ssid>"
        if (info.type != ConnectivityManager.TYPE_WIFI || info.state != NetworkInfo.State.CONNECTED) return "<unknown ssid>"
        return info.extraInfo
    }
}
