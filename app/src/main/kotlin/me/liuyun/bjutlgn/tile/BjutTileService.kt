package me.liuyun.bjutlgn.tile

import android.annotation.TargetApi
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.liuyun.bjutlgn.R
import me.liuyun.bjutlgn.api.BjutRetrofit
import me.liuyun.bjutlgn.api.BjutService
import me.liuyun.bjutlgn.entity.Stats
import me.liuyun.bjutlgn.ui.StatusDialog
import me.liuyun.bjutlgn.ui.StatusLockedActivity
import me.liuyun.bjutlgn.util.NetworkUtils
import me.liuyun.bjutlgn.util.StatsUtils

@TargetApi(Build.VERSION_CODES.N)
class BjutTileService : TileService() {
    private val TAG = BjutTileService::class.java.simpleName
    lateinit var iconOff: Icon
    lateinit var iconOn: Icon
    lateinit var res: Resources
    lateinit var service: BjutService

    override fun onCreate() {
        super.onCreate()
        iconOff = Icon.createWithResource(this, R.drawable.ic_cloud_off)
        iconOn = Icon.createWithResource(this, R.drawable.ic_cloud_done)
        res = resources
        service = BjutRetrofit.bjutService
    }

    override fun onTileAdded() {
        Log.d(TAG, "onTileAdded")
    }

    override fun onTileRemoved() {
        Log.d(TAG, "onTileRemoved")
    }

    override fun onClick() {
        Log.d(TAG, "onClick")
        if (isLocked) {
            if (isSecure) {
                val intent = Intent(this, StatusLockedActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivityAndCollapse(intent)
            } else {
                unlockAndRun { showDialog(StatusDialog.statusDialog(applicationContext, null)) }
            }
        } else {
            showDialog(StatusDialog.statusDialog(applicationContext, null))
        }
    }

    override fun onStartListening() {
        Log.d(TAG, "onStartListening")
        val tile = qsTile
        if (NetworkUtils.getNetworkState(this) != NetworkUtils.STATE_BJUT_WIFI) {
            setUnavailableState(tile)
        } else {
            setAvailableState(tile)
        }
        tile.updateTile()
    }

    override fun onStopListening() {
        Log.d(TAG, "onStopListening")
    }

    internal fun setAvailableState(tile: Tile) {
        service.stats()
                .map<Stats>({ StatsUtils.parseStats(it) })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ stats ->
                    if (stats.isOnline) {
                        setOnlineState(tile, stats)
                    } else {
                        setOfflineState(tile)
                    }
                }
                ) { setOfflineState(tile) }
    }

    internal fun setOnlineState(tile: Tile, stats: Stats) {
        tile.icon = iconOn
        tile.label = res.getString(R.string.status_logged_in, stats.flow, StatsUtils.getPercent(stats, this))
        tile.state = Tile.STATE_ACTIVE
    }

    internal fun setOfflineState(tile: Tile) {
        tile.icon = iconOff
        tile.label = res.getString(R.string.status_not_logged_in)
        tile.state = Tile.STATE_INACTIVE
    }

    internal fun setUnavailableState(tile: Tile) {
        tile.icon = iconOff
        tile.label = res.getString(R.string.status_unavailable)
        tile.state = Tile.STATE_INACTIVE
        //tile.setState(Tile.STATE_UNAVAILABLE);
    }
}
