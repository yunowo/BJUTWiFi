package me.liuyun.bjutlgn.widget;

import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.netopen.hotbitmapgg.library.view.RingProgressBar;
import me.liuyun.bjutlgn.BjutApi;
import me.liuyun.bjutlgn.BjutRetrofit;
import me.liuyun.bjutlgn.R;
import me.liuyun.bjutlgn.db.FlowDao;
import me.liuyun.bjutlgn.entity.Stat;
import me.liuyun.bjutlgn.util.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatusCard {
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
    private CardView cardView;
    private BjutApi api;
    private Resources resources;

    public StatusCard(CardView cardView) {
        this.cardView = cardView;
        ButterKnife.bind(this, cardView);
        api = new BjutRetrofit().getBjutService();
        resources = cardView.getResources();
    }

    @OnClick(R.id.refresh_button)
    public void onRefresh() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(cardView.getContext());
        String account = prefs.getString("account", null);
        int pack = prefs.getInt("current_package", 0);
        userView.setText(account);
        Call<ResponseBody> call = api.stats();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Stat currentStat = Utils.parseStat(response.body().string());
                    flowView.setText(String.format(resources.getString(R.string.stats_flow), currentStat.getFlow()));
                    feeView.setText(String.format(resources.getString(R.string.stats_fee), currentStat.getFee()));
                    timeView.setText(String.format(resources.getString(R.string.stats_time), currentStat.getTime()));

                    int percent = Math.round(currentStat.getFlow() / 1024 / resources.getIntArray(R.array.packages_values)[pack] * 100);
                    ObjectAnimator animator = ObjectAnimator.ofInt(progressRing, "progress", percent);
                    animator.setInterpolator(new AccelerateDecelerateInterpolator());
                    animator.setDuration(500);
                    animator.start();

                    Snackbar.make(cardView, "Refresh OK.", Snackbar.LENGTH_LONG).show();

                    FlowDao flowDao = new FlowDao(cardView.getContext());
                    flowDao.insertFlow(System.currentTimeMillis() / 1000L, currentStat.getFlow());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Snackbar.make(cardView, "Refresh failed.", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @OnClick(R.id.sign_in_button)
    public void onLogin() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(cardView.getContext());
        String account = prefs.getString("account", null);
        String password = prefs.getString("password", null);

        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)) {
            Call<ResponseBody> call = api.login(account, password, "1", "123");
            call.enqueue(BjutRetrofit.okFailCallback(cardView));
            onRefresh();
        }
    }

    @OnClick(R.id.sign_out_button)
    public void onLogout() {
        Call<ResponseBody> call = api.logout();
        call.enqueue(BjutRetrofit.okFailCallback(cardView));
    }

}
