package me.liuyun.bjutlgn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.stats)
    TextView mStatsView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorlayout;
    private LoginHelper helper = new LoginHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        //fab.show();
        //fab.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //                .setAction("Action", null).show();
        //);
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

    @OnClick(R.id.sign_in_button)
    public void attemptCallLogin() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String account = prefs.getString("account", null);
        String password = prefs.getString("password", null);

        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)) {
            helper.Login(account, password, true, coordinatorlayout);
            helper.GetStats(coordinatorlayout, mStatsView);
        }
    }

    @OnClick(R.id.refresh_button)
    public void attemptCallRefresh() {
        helper.GetStats(coordinatorlayout, mStatsView);
    }

    @OnClick(R.id.sign_out_button)
    public void attemptCallLogout() {
        helper.Logout(coordinatorlayout);
    }
}
