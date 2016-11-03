package me.liuyun.bjutlgn.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.netopen.hotbitmapgg.library.view.RingProgressBar;
import me.liuyun.bjutlgn.BjutApi;
import me.liuyun.bjutlgn.BjutRetrofit;
import me.liuyun.bjutlgn.R;
import me.liuyun.bjutlgn.entity.Stat;
import me.liuyun.bjutlgn.ui.MainActivity;
import me.liuyun.bjutlgn.util.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatusCard {
    private final static int STATE_NO_NETWORK = 0;
    private final static int STATE_MOBILE = 1;
    private final static int STATE_BJUT_WIFI = 2;
    private final static int STATE_OTHER_WIFI = 3;

    @BindView(R.id.progress_ring) RingProgressBar progressRing;
    @BindView(R.id.user) TextView userView;
    @BindView(R.id.fee) TextView feeView;
    @BindView(R.id.time) TextView timeView;
    @BindView(R.id.flow) TextView flowView;
    @BindView(R.id.status_layout) LinearLayout statusLayout;
    @BindView(R.id.buttons_layout) LinearLayout buttonsLayout;
    private CardView cardView;
    private MainActivity activity;
    private BjutApi api;

    public StatusCard(CardView cardView, MainActivity activity) {
        this.cardView = cardView;
        this.activity = activity;
        ButterKnife.bind(this, cardView);
        api = new BjutRetrofit().getBjutService();
    }

    @OnClick(R.id.refresh_button)
    public void onRefresh() {
        int state = getNetworkState();
        if (state == STATE_BJUT_WIFI) {
            progressRing.setVisibility(View.VISIBLE);
            statusLayout.setVisibility(View.VISIBLE);
            buttonsLayout.setVisibility(View.VISIBLE);
        } else {
            progressRing.setVisibility(View.INVISIBLE);
            statusLayout.setVisibility(View.INVISIBLE);
            buttonsLayout.setVisibility(View.INVISIBLE);
        }
        switch (state) {
            case STATE_NO_NETWORK:
                userView.setText(activity.resources.getString(R.string.status_no_network));
                break;
            case STATE_MOBILE:
                userView.setText(activity.resources.getString(R.string.status_mobile_network));
                break;
            case STATE_BJUT_WIFI:
                refreshStatus();
                break;
            case STATE_OTHER_WIFI:
                userView.setText(String.format(activity.resources.getString(R.string.status_other_wifi), getWifiSSID()));
                break;
        }
    }

    public void refreshStatus() {
        String account = activity.prefs.getString("account", null);
        userView.setText(account);
        Call<ResponseBody> call = api.stats();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Stat currentStat = Utils.parseStat(response.body().string());
                    flowView.setText(String.format(activity.resources.getString(R.string.stats_flow), currentStat.getFlow() / 1024f));
                    feeView.setText(String.format(activity.resources.getString(R.string.stats_fee), currentStat.getFee() / 1000f));
                    timeView.setText(String.format(activity.resources.getString(R.string.stats_time), currentStat.getTime()));

                    int percent = Math.round(currentStat.getFlow() / 1024 / 1024 / activity.getPack() * 100);
                    ObjectAnimator animator = ObjectAnimator.ofInt(progressRing, "progress", percent);
                    animator.setInterpolator(new AccelerateDecelerateInterpolator());
                    animator.setDuration(500);
                    animator.start();

                    Snackbar.make(cardView, "Refresh OK.", Snackbar.LENGTH_LONG).show();

                    if (currentStat.getFlow() != 0) {
                        activity.dao.insertFlow(System.currentTimeMillis() / 1000L, currentStat.getFlow());
                        activity.graphCard.show();
                    }
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
        String account = activity.prefs.getString("account", null);
        String password = activity.prefs.getString("password", null);

        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)) {
            Call<ResponseBody> call = api.login(account, password, "1", "123");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    refreshStatus();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                }
            });
        }
    }

    @OnClick(R.id.sign_out_button)
    public void onLogout() {
        Call<ResponseBody> call = api.logout();
        call.enqueue(BjutRetrofit.okFailCallback(cardView));
    }

    private int getNetworkState() {
        ConnectivityManager connManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        if (info != null && info.isConnectedOrConnecting()) {
            switch (info.getType()) {
                case ConnectivityManager.TYPE_MOBILE:
                    return STATE_MOBILE;
                case ConnectivityManager.TYPE_WIFI: {
                    if (getWifiSSID().replace("\"", "").equals("bjut_wifi"))
                        return STATE_BJUT_WIFI;
                    else return STATE_OTHER_WIFI;
                }
                default:
                    return STATE_NO_NETWORK;
            }
        } else return STATE_NO_NETWORK;
    }

    private String getWifiSSID() {
        WifiManager wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.getConnectionInfo().getSSID();
    }
}
