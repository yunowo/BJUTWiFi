package me.liuyun.bjutlgn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {
    @Bind(R.id.stats)
    TextView mStatsView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout coordinatorlayout;
    private LoginTask mLoginTask = null;
    private LogoutTask mLogoutTask = null;
    private GetStatsTask mStatsTask = null;


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
            mLoginTask = new LoginTask(account, password);
            mLoginTask.execute((Void) null);
        }
    }

    @OnClick(R.id.refresh_button)
    public void attemptCallRefresh() {
        mStatsTask = new GetStatsTask();
        mStatsTask.execute((Void) null);
    }

    @OnClick(R.id.sign_out_button)
    public void attemptCallLogout() {
        mLogoutTask = new LogoutTask();
        mLogoutTask.execute((Void) null);
    }

    public class LoginTask extends AsyncTask<Void, Void, Boolean> {
        private final String mAccount;
        private final String mPassword;

        LoginTask(String account, String password) {
            mAccount = account;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return LoginHelper.DoLogin(mAccount, mPassword, true);
        }

        @Override
        protected void onPostExecute(final Boolean result) {
            mLoginTask = null;
            final Snackbar snackbar = Snackbar.make(coordinatorlayout, result ? "Login OK" : "Login failed", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        @Override
        protected void onCancelled() {
            mLoginTask = null;
        }
    }


    public class GetStatsTask extends AsyncTask<Void, Void, Stats> {
        @Override
        protected Stats doInBackground(Void... params) {
            return LoginHelper.GetStats();
        }

        @Override
        protected void onPostExecute(final Stats result) {
            mStatsTask = null;
            if (result != null) {
                String text = (float) result.getFlow() / 1024 + " MB\n" + result.getTime() + " min\n" + (float) result.getFee() / 100 + " RMB";
                mStatsView.setText(text);
            }
            else{
                final Snackbar snackbar = Snackbar.make(coordinatorlayout, "Refresh failed", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }

        @Override
        protected void onCancelled() {
            mLoginTask = null;
        }
    }


    public class LogoutTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            return LoginHelper.DoLogout();
        }

        @Override
        protected void onPostExecute(final Boolean result) {
            mLogoutTask = null;
            final Snackbar snackbar = Snackbar.make(coordinatorlayout,  result ? "Logout OK" : "Logout failed", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        @Override
        protected void onCancelled() {
            mLoginTask = null;
        }
    }
}
