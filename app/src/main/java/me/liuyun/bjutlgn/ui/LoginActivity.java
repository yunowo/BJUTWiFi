package me.liuyun.bjutlgn.ui;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import cyanogenmod.app.CMStatusBarManager;
import cyanogenmod.app.CustomTile;
import me.liuyun.bjutlgn.R;
import me.liuyun.bjutlgn.tile.CMTileReceiver;
import me.liuyun.bjutlgn.widget.GraphCard;
import me.liuyun.bjutlgn.widget.StatusCard;


public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.status_card)
    CardView statusCardView;
    StatusCard statusCard;
    @BindView(R.id.graph_card)
    CardView graphCardView;
    GraphCard graphCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        statusCard = new StatusCard(statusCardView);
        graphCard = new GraphCard(graphCardView);

        fab.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(CMTileReceiver.ACTION_TOGGLE_STATE);
            intent.putExtra(CMTileReceiver.STATE, CMTileReceiver.STATE_OFF);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(LoginActivity.this, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            CustomTile customTile = new CustomTile.Builder(LoginActivity.this)
                    .setOnClickIntent(pendingIntent)
                    .setContentDescription("BJUT WiFi")
                    .setLabel("BJUT WiFi Off")
                    .shouldCollapsePanel(false)
                    .setIcon(R.drawable.ic_cloud_off)
                    .build();
            CMStatusBarManager.getInstance(LoginActivity.this)
                    .publishTile(CMTileReceiver.CUSTOM_TILE_ID, customTile);
        });
        statusCard.onRefresh();
        graphCard.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            this.startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
