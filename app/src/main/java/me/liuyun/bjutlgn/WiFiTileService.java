package me.liuyun.bjutlgn;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.preference.PreferenceManager;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.N)
public class WiFiTileService extends TileService {
    private final String TAG = WiFiTileService.class.getSimpleName();
    private Icon iconOff = Icon.createWithResource(getApplicationContext(), R.drawable.ic_cloud_off);
    private Icon iconOn = Icon.createWithResource(getApplicationContext(), R.drawable.ic_cloud_done);

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
        LoginHelper helper = new LoginHelper();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String account = prefs.getString("account", null);
        String password = prefs.getString("password", null);

        Tile tile = getQsTile();
        if (tile.getState() == Tile.STATE_ACTIVE) {
            helper.Logout(null);
            tile.setIcon(iconOff);
            tile.setState(Tile.STATE_INACTIVE);
        } else {
            helper.Login(account, password, true, null);
            tile.setIcon(iconOn);
            tile.setState(Tile.STATE_ACTIVE);
        }
        tile.updateTile();
    }

    @Override
    public void onStartListening() {
        Log.d(TAG, "onStartListening");
        Tile tile = getQsTile();
        //TODO check network state
        tile.setState(Tile.STATE_INACTIVE);
    }

    @Override
    public void onStopListening() {
        Log.d(TAG, "onStopListening");
    }


}
