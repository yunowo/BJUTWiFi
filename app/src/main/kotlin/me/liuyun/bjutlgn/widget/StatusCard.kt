package me.liuyun.bjutlgn.widget

import android.animation.ObjectAnimator
import android.databinding.DataBindingUtil
import android.net.CaptivePortal
import android.support.design.widget.Snackbar
import android.text.TextUtils
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.liuyun.bjutlgn.R
import me.liuyun.bjutlgn.WiFiApplication
import me.liuyun.bjutlgn.api.BjutRetrofit
import me.liuyun.bjutlgn.api.BjutService
import me.liuyun.bjutlgn.databinding.StatusViewBinding
import me.liuyun.bjutlgn.entity.Stats
import me.liuyun.bjutlgn.util.NetworkUtils
import me.liuyun.bjutlgn.util.StatsUtils

class StatusCard(private val cardView: FrameLayout, private val graphCard: GraphCard?, private val context: WiFiApplication, private val captivePortal: CaptivePortal?) {
    val binding: StatusViewBinding = DataBindingUtil.findBinding(cardView)
    internal var progressRing = binding.progressRing
    internal var userView = binding.user
    internal var feeView = binding.fee
    internal var timeView = binding.time
    internal var flowView = binding.flow
    internal var infoLayout = binding.infoLayout
    internal var buttonsLayout = binding.buttonsLayout
    private val service: BjutService = BjutRetrofit.bjutService

    init {
        binding.refreshButton.setOnClickListener { onRefresh() }
        binding.signInButton.setOnClickListener { onLogin() }
        binding.signOutButton.setOnClickListener { onLogout() }
    }

    fun onRefresh() {
        val state = NetworkUtils.getNetworkState(context)
        if (state == NetworkUtils.STATE_BJUT_WIFI) {
            progressRing.visibility = View.VISIBLE
            infoLayout.visibility = View.VISIBLE
            buttonsLayout.visibility = View.VISIBLE
        } else {
            progressRing.visibility = View.GONE
            infoLayout.visibility = View.INVISIBLE
            buttonsLayout.visibility = View.INVISIBLE
        }
        when (state) {
            NetworkUtils.STATE_NO_NETWORK -> userView.text = context.res.getString(R.string.status_no_network)
            NetworkUtils.STATE_MOBILE -> userView.text = context.res.getString(R.string.status_mobile_network)
            NetworkUtils.STATE_BJUT_WIFI -> refreshStatus()
            NetworkUtils.STATE_OTHER_WIFI -> userView.text = String.format(context.res.getString(R.string.status_other_wifi), NetworkUtils.getWifiSSID(context))
        }
    }

    private fun refreshStatus() {
        val account = context.prefs.getString("account", "")
        userView.text = account
        service.stats()
                .map<Stats>({ StatsUtils.parseStats(it) })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ stat ->
                    Snackbar.make(cardView, R.string.stats_refresh_ok, Snackbar.LENGTH_SHORT).show()
                    flowView.text = String.format(context.res.getString(R.string.stats_flow), stat.flow / 1024f)
                    feeView.text = String.format(context.res.getString(R.string.stats_fee), stat.fee / 10000f)
                    timeView.text = String.format(context.res.getString(R.string.stats_time), stat.time)

                    val animator = ObjectAnimator.ofInt(progressRing, "progress", StatsUtils.getPercent(stat, context))
                    animator.interpolator = AccelerateDecelerateInterpolator()
                    animator.duration = 500
                    animator.start()

                    if (stat.flow != 0) {
                        context.flowManager.insertFlow(System.currentTimeMillis() / 1000L, stat.flow)
                        graphCard?.show()

                        captivePortal?.reportCaptivePortalDismissed()
                    }
                },
                        { Snackbar.make(cardView, R.string.stats_refresh_failed, Snackbar.LENGTH_SHORT).show() })
    }

    internal fun onLogin() {
        val account = context.prefs.getString("account", "")
        val password = context.prefs.getString("password", "")

        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)) {
            service.login(account!!, password!!, "1", "123")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ refreshStatus() },
                            { Snackbar.make(cardView, R.string.stats_login_failed, Snackbar.LENGTH_SHORT).show() })
        }
    }

    internal fun onLogout() {
        service.logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ Snackbar.make(cardView, R.string.stats_logout_ok, Snackbar.LENGTH_SHORT).show() },
                        { Snackbar.make(cardView, R.string.stats_logout_failed, Snackbar.LENGTH_SHORT).show() })
    }
}
