package me.liuyun.bjutlgn.tile;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.liuyun.bjutlgn.R;
import me.liuyun.bjutlgn.api.BjutRetrofit;
import me.liuyun.bjutlgn.api.BjutService;
import me.liuyun.bjutlgn.entity.Stats;
import me.liuyun.bjutlgn.ui.StatusDialog;
import me.liuyun.bjutlgn.ui.StatusLockedActivity;
import me.liuyun.bjutlgn.util.StatsUtils;

import static me.liuyun.bjutlgn.util.NetworkUtils.STATE_BJUT_WIFI;
import static me.liuyun.bjutlgn.util.NetworkUtils.getNetworkState;

@TargetApi(Build.VERSION_CODES.N)
public class BjutTileService extends TileService {
    private final String TAG = BjutTileService.class.getSimpleName();
    private Icon iconOff;
    private Icon iconOn;
    private Resources res;
    private BjutService service;

    @Override
    public void onCreate() {
        super.onCreate();
        iconOff = Icon.createWithResource(this, R.drawable.ic_cloud_off);
        iconOn = Icon.createWithResource(this, R.drawable.ic_cloud_done);
        res = getResources();
        service = BjutRetrofit.getBjutService();
    }

    @Override
    public void onTileAdded() {
        Log.d(TAG, "onTileAdded");
    }

    @Override
    public void onTileRemoved() {
        Log.d(TAG, "onTileRemoved");
    }

    @Override
    public void onClick() {
        Log.d(TAG, "onClick");
        if (isLocked()) {
            if (isSecure()) {
                Intent intent = new Intent(this, StatusLockedActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityAndCollapse(intent);
            } else {
                unlockAndRun(() -> showDialog(StatusDialog.statusDialog(getApplicationContext(), null)));
            }
        } else {
            showDialog(StatusDialog.statusDialog(getApplicationContext(), null));
        }
    }

    @Override
    public void onStartListening() {
        Log.d(TAG, "onStartListening");
        Tile tile = getQsTile();
        if (getNetworkState(this) != STATE_BJUT_WIFI) {
            setUnavailableState(tile);
        } else {
            setAvailableState(tile);
        }
        tile.updateTile();
    }

    @Override
    public void onStopListening() {
        Log.d(TAG, "onStopListening");
    }

    void setAvailableState(Tile tile) {
        service.stats()
                .map(StatsUtils::parseStats)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stats -> {
                            if (stats.isOnline()) {
                                setOnlineState(tile, stats);
                            } else {
                                setOfflineState(tile);
                            }
                        },
                        throwable -> setOfflineState(tile));
    }

    void setOnlineState(Tile tile, Stats stats) {
        tile.setIcon(iconOn);
        tile.setLabel(res.getString(R.string.status_logged_in, stats.getFlow(), StatsUtils.getPercent(stats, this)));
        tile.setState(Tile.STATE_ACTIVE);
    }

    void setOfflineState(Tile tile) {
        tile.setIcon(iconOff);
        tile.setLabel(res.getString(R.string.status_not_logged_in));
        tile.setState(Tile.STATE_INACTIVE);
    }

    void setUnavailableState(Tile tile) {
        tile.setIcon(iconOff);
        tile.setLabel(res.getString(R.string.status_unavailable));
        tile.setState(Tile.STATE_INACTIVE);
        //tile.setState(Tile.STATE_UNAVAILABLE);
    }
}
