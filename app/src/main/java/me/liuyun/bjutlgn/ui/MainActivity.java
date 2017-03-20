package me.liuyun.bjutlgn.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.liuyun.bjutlgn.R;
import me.liuyun.bjutlgn.WiFiApplication;
import me.liuyun.bjutlgn.widget.GraphCard;
import me.liuyun.bjutlgn.widget.StatusCard;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.swipe_refresh) SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.status_card) CardView statusCardView;
    @BindView(R.id.graph_card) CardView graphCardView;
    public StatusCard statusCard;
    public GraphCard graphCard;
    private BroadcastReceiver receiver;
    private Spring spring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        graphCard = new GraphCard(graphCardView, (WiFiApplication) getApplication());
        statusCard = new StatusCard(statusCardView, graphCard, (WiFiApplication) getApplication(), null);

        spring = SpringSystem.create().createSpring();
        spring.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                float value = (float) spring.getCurrentValue();
                graphCardView.setScaleX(value);
                graphCardView.setScaleY(value);
                statusCardView.setScaleX(value);
                statusCardView.setScaleY(value);
            }
        });


        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                statusCard.onRefresh();
            }
        };

        fab.setOnClickListener(v -> statusCard.onRefresh());

        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorAccent, getTheme()));
        swipeRefresh.setOnRefreshListener(() -> {
            statusCard.onRefresh();
            swipeRefresh.setRefreshing(false);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        statusCard.onRefresh();
        graphCard.show();
        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        spring.setCurrentValue(0);
        spring.setEndValue(1);
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
}
