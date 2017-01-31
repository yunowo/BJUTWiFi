package me.liuyun.bjutlgn.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.liuyun.bjutlgn.R;
import me.liuyun.bjutlgn.db.FlowManager;
import me.liuyun.bjutlgn.widget.GraphCard;
import me.liuyun.bjutlgn.widget.StatusCard;


public class MainActivity extends AppCompatActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.status_card) CardView statusCardView;
    @BindView(R.id.graph_card) CardView graphCardView;
    public StatusCard statusCard;
    public GraphCard graphCard;
    public FlowManager flowManager;
    public Resources resources;
    public SharedPreferences prefs;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        flowManager=new FlowManager(this);
        resources = getResources();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        statusCard = new StatusCard(statusCardView, this);
        graphCard = new GraphCard(graphCardView, this);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                statusCard.onRefresh();
            }
        };

        fab.setOnClickListener(v -> {
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        statusCard.onRefresh();
        graphCard.show();
        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
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
        } else if (id == R.id.action_users) {
            Intent intent = new Intent(this, UserActivity.class);
            this.startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public int getPack() {
        return resources.getIntArray(R.array.packages_values)[prefs.getInt("current_package", 0)];
    }
}
