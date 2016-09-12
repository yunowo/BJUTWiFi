package me.liuyun.bjutlgn;

import android.annotation.TargetApi;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.N)
public class WiFiTileService extends TileService {
    private final int STATE_OFF = 0;
    private final int STATE_ON = 1;
    public static final String ACTION_QS_TILE_PREFERENCE = "me.liuyun.bjutlgn.SettingsActivity";
    private final String LOG_TAG = "BJUTWiFiTileService";
    private int toggleState = STATE_ON;

    public WiFiTileService() {
    }

    @Override
    public void onTileAdded() {
        Log.d(LOG_TAG, "onTileAdded");
    }

    @Override
    public void onTileRemoved() {
        Log.d(LOG_TAG, "onTileRemoved");
    }

    @Override
    public void onClick() {
        Tile tile = getQsTile();
        Log.d(LOG_TAG, "onClick state = " + Integer.toString(getQsTile().getState()));
        Icon icon;
        if (toggleState == STATE_ON) {
            toggleState = STATE_OFF;
            //TODO do logout
            icon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_cloud_off);
            tile.setState(Tile.STATE_INACTIVE);
        } else {
            toggleState = STATE_ON;
            //TODO do login
            icon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_cloud_done);
            tile.setState(Tile.STATE_ACTIVE);
        }

        tile.setIcon(icon);
        tile.updateTile();
    }

    @Override
    public void onStartListening() {
        Log.d(LOG_TAG, "onStartListening");
    }

    @Override
    public void onStopListening() {
        Log.d(LOG_TAG, "onStopListening");
    }


}
