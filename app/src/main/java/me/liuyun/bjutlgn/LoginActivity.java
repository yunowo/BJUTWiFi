package me.liuyun.bjutlgn;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.netopen.hotbitmapgg.library.view.RingProgressBar;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.progress_ring)
    RingProgressBar progressRing;
    @BindView(R.id.user)
    TextView userView;
    @BindView(R.id.fee)
    TextView feeView;
    @BindView(R.id.time)
    TextView timeView;
    @BindView(R.id.flow)
    TextView flowView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorlayout;

    private BjutApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        api = new BjutRetrofit().getBjutService();
        fab.setOnClickListener(view -> Snackbar.make(view, "This is an FAB.", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
        onRefresh();
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

    @OnClick(R.id.refresh_button)
    public void onRefresh() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String account = prefs.getString("account", null);
        int pack = prefs.getInt("package", 8);
        userView.setText(account);
        Call<ResponseBody> call = api.stats();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Resources resources = getResources();
                    Stat currentStat = Utils.parseStat(response.body().string());
                    flowView.setText(String.format(resources.getString(R.string.stats_flow), currentStat.getFlow()));
                    feeView.setText(String.format(resources.getString(R.string.stats_fee), currentStat.getFee()));
                    timeView.setText(String.format(resources.getString(R.string.stats_time), currentStat.getTime()));
                    int percent = Math.round(currentStat.getFlow() / 1024 / pack * 100);
                    ObjectAnimator animator = ObjectAnimator.ofInt(progressRing, "progress", percent);
                    animator.setInterpolator(new AccelerateDecelerateInterpolator());
                    animator.setDuration(500);
                    animator.start();
                    Snackbar.make(coordinatorlayout, "Refresh OK.", Snackbar.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Snackbar.make(coordinatorlayout, "Refresh failed.", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @OnClick(R.id.sign_in_button)
    public void onLogin() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String account = prefs.getString("account", null);
        String password = prefs.getString("password", null);

        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)) {
            Call<ResponseBody> call = api.login(account, password, "1", "123");
            call.enqueue(BjutRetrofit.okFailCallback(coordinatorlayout));
            onRefresh();
        }
    }

    @OnClick(R.id.sign_out_button)
    public void onLogout() {
        Call<ResponseBody> call = api.logout();
        call.enqueue(BjutRetrofit.okFailCallback(coordinatorlayout));
    }

}
