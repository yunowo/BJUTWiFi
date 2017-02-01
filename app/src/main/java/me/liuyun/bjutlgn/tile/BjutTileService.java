package me.liuyun.bjutlgn.tile;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.preference.PreferenceManager;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;

import me.liuyun.bjutlgn.entity.Stat;
import me.liuyun.bjutlgn.util.LoginUtils;
import me.liuyun.bjutlgn.R;
import me.liuyun.bjutlgn.util.StatUtils;

import static me.liuyun.bjutlgn.util.NetworkUtils.*;

@TargetApi(Build.VERSION_CODES.N)
public class BjutTileService extends TileService {
    private final String TAG = BjutTileService.class.getSimpleName();
    private Icon iconOff;
    private Icon iconOn;
    private SharedPreferences prefs;
    private Resources resources;
    private LoginUtils helper = new LoginUtils();

    @Override
    public void onCreate() {
        super.onCreate();
        iconOff = Icon.createWithResource(this, R.drawable.ic_cloud_off);
        iconOn = Icon.createWithResource(this, R.drawable.ic_cloud_done);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        resources = this.getResources();
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
        Tile tile = getQsTile();
        if (tile.getState() == Tile.STATE_ACTIVE) {
            helper.logout();
            setOfflineState(tile);
        } else if (tile.getState() == Tile.STATE_INACTIVE) {
            String account = prefs.getString("account", null);
            String password = prefs.getString("password", null);
            helper.login(account, password, true);
            setAvailableState(tile);
        }
        tile.updateTile();
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
        Stat stat = helper.stat();
        if (stat.isOnline()) {
            setOnlineState(tile, stat);
        } else {
            setOfflineState(tile);
        }
    }

    void setOnlineState(Tile tile, Stat stat) {
        tile.setIcon(iconOn);
        tile.setLabel(resources.getString(R.string.status_logged_in, stat.getFlow(), StatUtils.getPercent(stat, StatUtils.getPack(resources, prefs))));
        tile.setState(Tile.STATE_ACTIVE);
    }

    void setOfflineState(Tile tile) {
        tile.setIcon(iconOff);
        tile.setLabel(resources.getString(R.string.status_not_logged_in));
        tile.setState(Tile.STATE_INACTIVE);
    }

    void setUnavailableState(Tile tile) {
        tile.setIcon(iconOff);
        tile.setLabel(resources.getString(R.string.status_unavailable));
        tile.setState(Tile.STATE_UNAVAILABLE);
    }
}
