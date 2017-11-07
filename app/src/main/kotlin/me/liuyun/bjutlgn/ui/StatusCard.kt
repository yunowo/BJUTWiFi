package me.liuyun.bjutlgn.ui

import android.animation.ObjectAnimator
import android.databinding.DataBindingUtil
import android.net.CaptivePortal
import android.support.design.widget.Snackbar
import android.text.TextUtils
import android.text.format.Formatter
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import me.liuyun.bjutlgn.App
import me.liuyun.bjutlgn.R
import me.liuyun.bjutlgn.api.BjutRetrofit
import me.liuyun.bjutlgn.api.BjutService
import me.liuyun.bjutlgn.databinding.StatusViewBinding
import me.liuyun.bjutlgn.entity.Flow
import me.liuyun.bjutlgn.util.NetworkUtils
import me.liuyun.bjutlgn.util.NetworkUtils.NetworkState.*
import me.liuyun.bjutlgn.util.StatsUtils

class StatusCard(private val cardView: FrameLayout, private val graphCard: GraphCard?, private val app: App, private val captivePortal: CaptivePortal?) {
    val b: StatusViewBinding = DataBindingUtil.findBinding(cardView)
    private val service: BjutService = BjutRetrofit.bjutService

    init {
        b.refreshButton.setOnClickListener { onRefresh() }
        b.signInButton.setOnClickListener { onLogin() }
        b.signOutButton.setOnClickListener { onLogout() }
    }

    fun onRefresh() {
        val state = NetworkUtils.getNetworkState(app)
        if (state == STATE_BJUT_WIFI) {
            arrayOf(b.user, b.progressRing, b.infoLayout, b.buttonsLayout).forEach { it.visibility = View.VISIBLE }
            b.nonBjut.visibility = View.GONE
        } else {
            arrayOf(b.user, b.progressRing, b.infoLayout, b.buttonsLayout).forEach { it.visibility = View.GONE }
            b.nonBjut.visibility = View.VISIBLE
        }
        when (state) {
            STATE_NO_NETWORK -> b.nonBjut.text = app.res.getString(R.string.status_no_network)
            STATE_MOBILE -> b.nonBjut.text = app.res.getString(R.string.status_mobile_network)
            STATE_BJUT_WIFI -> refreshStatus()
            STATE_OTHER_WIFI -> b.nonBjut.text = app.res.getString(R.string.status_other_wifi).format(NetworkUtils.getWifiSSID(app))
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
                .map(StatsUtils::parseStats)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ stats ->
                    Snackbar.make(cardView, R.string.stats_refresh_ok, Snackbar.LENGTH_SHORT).show()
                    b.flow.text = app.res.getString(R.string.stats_flow).format(Formatter.formatFileSize(app, stats.flow * 1024L))
                    b.fee.text = app.res.getString(R.string.stats_fee).format(stats.fee / 10000f)
                    b.time.text = app.res.getString(R.string.stats_time).format(stats.time)

                    val percent = StatsUtils.getPercent(stats, app)
                    b.progressRing.centerTitle = "$percent %"
                    val w = app.res.getColor(android.R.color.white, app.theme)
                    val a = app.res.getColor(R.color.colorAccent, app.theme)
                    if (percent > 50) {
                        b.progressRing.centerTitleColor = w
                        b.progressRing.setCenterTitleStrokeColor(a)
                    } else {
                        b.progressRing.centerTitleColor = a
                        b.progressRing.setCenterTitleStrokeColor(w)
                    }
                    val animator = ObjectAnimator.ofInt(b.progressRing, "progressValue", percent)
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

    private fun onLogin() {
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

    private fun onLogout() {
        service.logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ Snackbar.make(cardView, R.string.stats_logout_ok, Snackbar.LENGTH_SHORT).show() },
                        { Snackbar.make(cardView, R.string.stats_logout_failed, Snackbar.LENGTH_SHORT).show() })
    }
}
