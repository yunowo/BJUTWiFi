package me.liuyun.bjutlgn.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        graphCard = new GraphCard(graphCardView, (WiFiApplication) getApplication());
        statusCard = new StatusCard(statusCardView, graphCard, (WiFiApplication) getApplication(), null);

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

        startSpringAnimation(statusCardView);
        startSpringAnimation(graphCardView);
        startSpringAnimation(fab);
        startSpringAnimation(toolbar);
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

    private void startSpringAnimation(View view) {
        view.setScaleX(0.1f);
        view.setScaleY(0.1f);
        SpringForce spring = new SpringForce(1)
                .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY)
                .setStiffness(SpringForce.STIFFNESS_LOW);
        new SpringAnimation(view, SpringAnimation.SCALE_X)
                .setSpring(spring)
                .setStartVelocity(0.7f)
                .start();
        new SpringAnimation(view, SpringAnimation.SCALE_Y)
                .setSpring(spring)
                .setStartVelocity(0.7f)
                .start();
    }
}
