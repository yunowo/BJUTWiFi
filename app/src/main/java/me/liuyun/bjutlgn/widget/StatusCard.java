package me.liuyun.bjutlgn.widget;

import android.animation.ObjectAnimator;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.netopen.hotbitmapgg.library.view.RingProgressBar;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.liuyun.bjutlgn.R;
import me.liuyun.bjutlgn.WiFiApplication;
import me.liuyun.bjutlgn.api.BjutRetrofit;
import me.liuyun.bjutlgn.api.BjutService;
import me.liuyun.bjutlgn.util.StatUtils;

import static me.liuyun.bjutlgn.util.NetworkUtils.STATE_BJUT_WIFI;
import static me.liuyun.bjutlgn.util.NetworkUtils.STATE_MOBILE;
import static me.liuyun.bjutlgn.util.NetworkUtils.STATE_NO_NETWORK;
import static me.liuyun.bjutlgn.util.NetworkUtils.STATE_OTHER_WIFI;
import static me.liuyun.bjutlgn.util.NetworkUtils.getNetworkState;
import static me.liuyun.bjutlgn.util.NetworkUtils.getWifiSSID;

public class StatusCard {
    @BindView(R.id.progress_ring) RingProgressBar progressRing;
    @BindView(R.id.user) TextView userView;
    @BindView(R.id.fee) TextView feeView;
    @BindView(R.id.time) TextView timeView;
    @BindView(R.id.flow) TextView flowView;
    @BindView(R.id.info_layout) LinearLayout infoLayout;
    @BindView(R.id.buttons_layout) LinearLayout buttonsLayout;
    private FrameLayout cardView;
    @Nullable private GraphCard graphCard;
    private WiFiApplication context;
    private BjutService service;

    public StatusCard(FrameLayout cardView, @Nullable GraphCard graphCard, WiFiApplication context) {
        this.cardView = cardView;
        this.graphCard = graphCard;
        this.context = context;
        ButterKnife.bind(this, cardView);
        service = BjutRetrofit.getBjutService();
    }

    @OnClick(R.id.refresh_button)
    public void onRefresh() {
        int state = getNetworkState(context);
        if (state == STATE_BJUT_WIFI) {
            progressRing.setVisibility(View.VISIBLE);
            infoLayout.setVisibility(View.VISIBLE);
            buttonsLayout.setVisibility(View.VISIBLE);
        } else {
            progressRing.setVisibility(View.GONE);
            infoLayout.setVisibility(View.INVISIBLE);
            buttonsLayout.setVisibility(View.INVISIBLE);
        }
        switch (state) {
            case STATE_NO_NETWORK:
                userView.setText(context.getRes().getString(R.string.status_no_network));
                break;
            case STATE_MOBILE:
                userView.setText(context.getRes().getString(R.string.status_mobile_network));
                break;
            case STATE_BJUT_WIFI:
                refreshStatus();
                break;
            case STATE_OTHER_WIFI:
                userView.setText(String.format(context.getRes().getString(R.string.status_other_wifi), getWifiSSID(context)));
                break;
        }
    }

    private void refreshStatus() {
        String account = context.getPrefs().getString("account", "");
        userView.setText(account);
        service.stats()
                .map(StatUtils::parseStat)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stat -> {
                            Snackbar.make(cardView, R.string.stats_refresh_ok, Snackbar.LENGTH_LONG).show();
                            flowView.setText(String.format(context.getRes().getString(R.string.stats_flow), stat.getFlow() / 1024f));
                            feeView.setText(String.format(context.getRes().getString(R.string.stats_fee), stat.getFee() / 1000f));
                            timeView.setText(String.format(context.getRes().getString(R.string.stats_time), stat.getTime()));

                            ObjectAnimator animator = ObjectAnimator.ofInt(progressRing, "progress", StatUtils.getPercent(stat, context));
                            animator.setInterpolator(new AccelerateDecelerateInterpolator());
                            animator.setDuration(500);
                            animator.start();

                            if (stat.getFlow() != 0) {
                                context.getFlowManager().insertFlow(System.currentTimeMillis() / 1000L, stat.getFlow());
                                if (graphCard != null)
                                    graphCard.show();
                            }
                        },
                        throwable -> Snackbar.make(cardView, R.string.stats_refresh_failed, Snackbar.LENGTH_LONG).show());
    }

    @OnClick(R.id.sign_in_button)
    void onLogin() {
        String account = context.getPrefs().getString("account", "");
        String password = context.getPrefs().getString("password", "");

        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)) {
            service.login(account, password, "1", "123")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(string -> refreshStatus(),
                            throwable -> Snackbar.make(cardView, R.string.stats_login_failed, Snackbar.LENGTH_LONG).show());
        }
    }

    @OnClick(R.id.sign_out_button)
    void onLogout() {
        service.logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(string -> Snackbar.make(cardView, R.string.stats_logout_ok, Snackbar.LENGTH_LONG).show(),
                        throwable -> Snackbar.make(cardView, R.string.stats_logout_failed, Snackbar.LENGTH_LONG).show());
    }
}
