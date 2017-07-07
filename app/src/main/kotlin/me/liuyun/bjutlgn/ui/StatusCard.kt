package me.liuyun.bjutlgn.ui

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
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import me.liuyun.bjutlgn.R
import me.liuyun.bjutlgn.WiFiApplication
import me.liuyun.bjutlgn.api.BjutRetrofit
import me.liuyun.bjutlgn.api.BjutService
import me.liuyun.bjutlgn.databinding.StatusViewBinding
import me.liuyun.bjutlgn.entity.Flow
import me.liuyun.bjutlgn.util.NetworkUtils
import me.liuyun.bjutlgn.util.StatsUtils

class StatusCard(private val cardView: FrameLayout, private val graphCard: GraphCard?, private val app: WiFiApplication, private val captivePortal: CaptivePortal?) {
    val b: StatusViewBinding = DataBindingUtil.findBinding(cardView)
    private val service: BjutService = BjutRetrofit.bjutService

    init {
        b.refreshButton.setOnClickListener { onRefresh() }
        b.signInButton.setOnClickListener { onLogin() }
        b.signOutButton.setOnClickListener { onLogout() }
    }

    fun onRefresh() {
        val state = NetworkUtils.getNetworkState(app)
        if (state == NetworkUtils.STATE_BJUT_WIFI) {
            arrayOf(b.progressRing, b.infoLayout, b.buttonsLayout).forEach { it.visibility = View.VISIBLE }
        } else {
            arrayOf(b.progressRing, b.infoLayout, b.buttonsLayout).forEach { it.visibility = View.GONE }
        }
        when (state) {
            NetworkUtils.STATE_NO_NETWORK -> b.user.text = app.res.getString(R.string.status_no_network)
            NetworkUtils.STATE_MOBILE -> b.user.text = app.res.getString(R.string.status_mobile_network)
            NetworkUtils.STATE_BJUT_WIFI -> refreshStatus()
            NetworkUtils.STATE_OTHER_WIFI -> b.user.text = String.format(app.res.getString(R.string.status_other_wifi), NetworkUtils.getWifiSSID(app))
        }
    }

    private fun refreshStatus() = async(UI) {
        val job = async(CommonPool) {
            BjutRetrofit.evictAll()
        }
        job.await()

        val account = app.prefs.getString("account", "")
        b.user.text = account
        service.stats()
                .map({ StatsUtils.parseStats(it) })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ stats ->
                    Snackbar.make(cardView, R.string.stats_refresh_ok, Snackbar.LENGTH_SHORT).show()
                    b.flow.text = String.format(app.res.getString(R.string.stats_flow), stats.flow / 1024f)
                    b.fee.text = String.format(app.res.getString(R.string.stats_fee), stats.fee / 10000f)
                    b.time.text = String.format(app.res.getString(R.string.stats_time), stats.time)

                    val animator = ObjectAnimator.ofInt(b.progressRing, "progress", StatsUtils.getPercent(stats, app))
                    animator.interpolator = AccelerateDecelerateInterpolator()
                    animator.duration = 500
                    animator.start()

                    if (stats.isOnline) {
                        app.appDatabase.flowDao().insert(Flow(0, System.currentTimeMillis() / 1000L, stats.flow))
                        graphCard?.show()

                        captivePortal?.reportCaptivePortalDismissed()
                    }
                },
                        { Snackbar.make(cardView, R.string.stats_refresh_failed, Snackbar.LENGTH_SHORT).show() })
    }

    internal fun onLogin() {
        val account = app.prefs.getString("account", "")
        val password = app.prefs.getString("password", "")

        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)) {
            service.login(account, password, "1", "123")
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
