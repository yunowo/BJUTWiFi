package me.liuyun.bjutlgn;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import cyanogenmod.app.CMStatusBarManager;
import cyanogenmod.app.CustomTile;


public class CMTileReceiver extends BroadcastReceiver {
    private final String TAG = CMTileReceiver.class.getSimpleName();
    public final static int STATE_OFF = 0;
    public final static int STATE_ON = 1;
    public static final int CUSTOM_TILE_ID = 1;
    public static final String ACTION_TOGGLE_STATE = "me.liuyun.bjutlgn.tiles.ACTION_TOGGLE_STATE";
    public static final String STATE = "state";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (ACTION_TOGGLE_STATE.equals(intent.getAction())) {
            LoginHelper helper = new LoginHelper();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String account = prefs.getString("account", null);
            String password = prefs.getString("password", null);

            Intent newIntent = new Intent();
            newIntent.setAction(ACTION_TOGGLE_STATE);
            String label = "BJUT WiFi";

            int state = getCurrentState(intent);
            switch (state) {
                case STATE_OFF:
                    newIntent.putExtra(STATE, STATE_ON);
                    label = "BJUT WiFi Off";
                    helper.Login(account, password, true, null);
                    Log.d(TAG, "Trying to login.");
                    break;
                case STATE_ON:
                    newIntent.putExtra(STATE, STATE_OFF);
                    helper.Logout(null);
                    label = "BJUT WiFi On";
                    Log.d(TAG, "Trying to logout.");
                    break;
            }

            PendingIntent pendingIntent =
                    PendingIntent.getBroadcast(context, 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            CustomTile customTile = new CustomTile.Builder(context)
                    .setOnClickIntent(pendingIntent)
                    .setContentDescription("BJUT WiFi")
                    .shouldCollapsePanel(false)
                    .setLabel(label)
                    .setIcon(state == STATE_ON ? R.drawable.ic_cloud_done : R.drawable.ic_cloud_off)
                    .build();

            CMStatusBarManager.getInstance(context)
                    .publishTile(CUSTOM_TILE_ID, customTile);
        }
    }

    private int getCurrentState(Intent intent) {
        return intent.getIntExtra(STATE, 0);
    }
}
